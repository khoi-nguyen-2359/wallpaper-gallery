package com.xkcn.crawler.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xkcn.crawler.util.L;
import com.xkcn.crawler.util.P;
import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.db.PhotoDao;
import com.xkcn.crawler.event.CrawlNextPageEvent;
import com.xkcn.crawler.event.UpdateFinishedEvent;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

public class UpdateService extends Service {
    public static final String XPATH_POST_ID = "//*[@id=\"post-%d\"]";

    public static final String ACTION_UPDATE = "com.xkcn.crawler.action.UPDATE";

    private boolean isWaitingHtml;
    private Pattern pattern;
    private WebView webview;
    private int crawlingPage;
    private long lastUpdatedPhotoId;

    private L logger = L.get(UpdateService.class.getSimpleName());

    public static void startActionUpdate(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(ACTION_UPDATE);
        context.startService(intent);
    }

    private void createWebBrowser() {
        pattern = Pattern.compile("permalinkMeta.*>\\n.*<p>(.*)</p>");

        webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadsImagesAutomatically(false);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setUserAgentString("Android Chrome"); // this fakes tablet's screen
        webview.addJavascriptInterface(new CrawlerJsInterface(), "jsi");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                logger.d("page %d finished", crawlingPage);
                if (isWaitingHtml == false)
                    return;

                isWaitingHtml = false;
                view.loadUrl("javascript:var x = document.getElementsByTagName('html'); window.jsi.processHTML(x.length > 0 ? x[0].innerHTML : null);");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                logger.d("page %d error %s", crawlingPage, description);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createWebBrowser();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_UPDATE.equals(action)) {
            lastUpdatedPhotoId = P.getLastUpdatedPhotoId();
            if (lastUpdatedPhotoId == 0) {
                lastUpdatedPhotoId = PhotoDao.getLargestPhotoId();
                P.saveLastUpdatedPhotoId(lastUpdatedPhotoId);
            }
            logger.d("update started lastUpdatedPhotoId=%d", lastUpdatedPhotoId);

            startPageCrawling(1);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startPageCrawling(int page) {
        String urlToCrawl = "http://xkcn.info/page/" + page;
        logger.d("startPageCrawling %s", urlToCrawl);
        crawlingPage = page;
        isWaitingHtml = true;
        webview.loadUrl(urlToCrawl);
    }

    public void onEventMainThread(CrawlNextPageEvent e) {
        startPageCrawling(e.getPage());
    }

    public Photo parsePhotoNode(TagNode post) {
        Photo photo = null;

        String dataType = post.getAttributeByName("data-type");
        if ("photo".equals(dataType)) {
            photo = new Photo();

            String photoHigh = post.getAttributeByName("data-photo-high");
            String photo500 = photoHigh.replace("_1280.", "_500.");
            String photo100 = photoHigh.replace("_1280.", "_100.");
            String photo250 = photoHigh.replace("_1280.", "_250.");

            photo.setPhotoHigh(photoHigh);
            photo.setPhoto500(photo500);
            photo.setPhoto250(photo250);
            photo.setPhoto100(photo100);
            photo.setIdentifier(Long.valueOf(post.getAttributeByName("data-identifier")));
            photo.setPermalink(post.getAttributeByName("data-permalink"));
            photo.setNotesUrl(post.getAttributeByName("data-notes-url"));
            photo.setHeightHighRes(Integer.valueOf(post.getAttributeByName("data-height-high-res")));
            photo.setWidthHighRes(Integer.valueOf(post.getAttributeByName("data-width-high-res")));
            photo.setTitle(post.getAttributeByName("data-title"));
            photo.setTags(post.getAttributeByName("data-tags"));

            TagNode template = post.findElementByAttValue("class", "template", false, true);
            if (template != null) {
                String templateText = template.getText().toString();
                Matcher matcher = pattern.matcher(templateText);
                if (matcher.find()) {
                    String permalinkMetaValue = matcher.group(1);
                    logger.d("url=%s", photo.getPermalink());
                    photo.setPermalinkMeta(permalinkMetaValue);
                }
            }

            TagNode gridNotes = post.findElementByAttValue("class", "gridNotes", true, true);
            if (gridNotes != null) {
                int notes = 0;
                try {
                    notes = Integer.valueOf(gridNotes.getText().toString());
                } catch (Exception e) {

                }

                photo.setNotes(notes);
            }
        }

        return photo;
    }

    public class CrawlerJsInterface {
        @JavascriptInterface
        public void processHTML(String html) {
            logger.d("processHTML");

            List<Photo> photoList = new ArrayList<>();
            if (html != null) {
                HtmlCleaner htmlCleaner = new HtmlCleaner();
                TagNode root = htmlCleaner.clean(html);
                for (int i = 1; ; ++i) {
                    try {
                        Object[] objs = root.evaluateXPath(String.format(XPATH_POST_ID, i));
                        if (objs.length > 0) {
                            TagNode post = (TagNode) objs[0];
                            Photo photo = parsePhotoNode(post);
                            if (photo != null) {
                                photoList.add(photo);
                            }
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                logger.d("html null");
            }
            logger.d("crawled list size=%d", photoList.size());

            PhotoDao.bulkInsertPhoto(photoList);
            int listSize = photoList.size();
            long lastPhotoId = listSize > 0 ? photoList.get(listSize - 1).getIdentifier() : 0;
            logger.d("lastPhotoId=%d", lastPhotoId);
            if (listSize == 0 || lastPhotoId <= lastUpdatedPhotoId) {
                logger.d("processHTML done");
                if (lastPhotoId > 0 && lastPhotoId <= lastUpdatedPhotoId) {
                    P.saveLastUpdatedPhotoId(lastUpdatedPhotoId = PhotoDao.getLargestPhotoId());
                    P.saveLastUpdateTime(System.currentTimeMillis());
                    logger.d("update finished lastUpdatedPhotoId=%d", lastUpdatedPhotoId);
                }
                EventBus.getDefault().post(new UpdateFinishedEvent());
                stopSelf();
            } else {
                EventBus.getDefault().post(new CrawlNextPageEvent(crawlingPage + 1));
            }
        }
    }
}
