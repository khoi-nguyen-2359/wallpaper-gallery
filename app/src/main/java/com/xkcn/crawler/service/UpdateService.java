package com.xkcn.crawler.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xkcn.crawler.util.U;
import com.xkcn.crawler.db.Photo;
import com.xkcn.crawler.db.PhotoDao;
import com.xkcn.crawler.event.CrawlNextPageEvent;
import com.xkcn.crawler.event.UpdateFinishedEvent;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

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
                U.d("khoi", "page %d finished", crawlingPage);
                if (isWaitingHtml == false)
                    return;

                isWaitingHtml = false;
                view.loadUrl("javascript:window.jsi.processHTML(document.getElementsByTagName('html')[0].innerHTML);");
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
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_UPDATE.equals(action)) {
            startPageCrawling(1);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startPageCrawling(int page) {
        crawlingPage = page;
        isWaitingHtml = true;
        webview.loadUrl("http://xkcn.info/page/" + crawlingPage);
    }

    public void onEventMainThread(CrawlNextPageEvent e) {
        U.d("khoi", "onEventMainThread");
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
                    U.d("khoi", "url=%s", photo.getPermalink());
                    photo.setPermalinkMeta(permalinkMetaValue);
                }
            }

//            TagNode gridOverlay = post.findElementByAttValue("class", "gridOverlay", false, true);
//            if (gridOverlay != null) {
//            }
        }

        return photo;
    }

    public class CrawlerJsInterface {
        @JavascriptInterface
        public void processHTML(String html) {
            U.d("khoi", "processHTML");

            HtmlCleaner htmlCleaner = new HtmlCleaner();
            TagNode root = htmlCleaner.clean(html);
            List<Photo> photoList = new ArrayList<>();
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
                } catch (XPatherException e) {
                    e.printStackTrace();
                }
            }

            int count = PhotoDao.bulkInsertPhoto(photoList);
            if (count != 0 && count == photoList.size()) {
                EventBus.getDefault().post(new CrawlNextPageEvent(crawlingPage + 1));
            } else {
                U.d("khoi", "processHTML done");
                EventBus.getDefault().post(new UpdateFinishedEvent());
                U.saveLastUpdate(System.currentTimeMillis());
                stopSelf();
            }
        }
    }
}
