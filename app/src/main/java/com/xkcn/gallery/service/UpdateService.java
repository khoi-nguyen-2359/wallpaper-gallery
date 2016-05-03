package com.xkcn.gallery.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.Html;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.khoinguyen.util.log.L;
import com.xkcn.gallery.BaseApp;
import com.xkcn.gallery.data.model.ModelConstants;
import com.xkcn.gallery.data.model.PhotoDetails;
import com.xkcn.gallery.data.model.PhotoTag;
import com.xkcn.gallery.data.repo.PhotoDetailsRepository;
import com.xkcn.gallery.data.repo.PhotoTagRepository;
import com.xkcn.gallery.data.repo.PreferenceRepository;
import com.xkcn.gallery.event.CrawlNextPageEvent;
import com.xkcn.gallery.event.PhotoCrawlingFinishedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class UpdateService extends Service {
    public static final String XPATH_POST_ID = "//*[@id=\"post-%d\"]";

    public static final String ACTION_UPDATE = "com.xkcn.crawler.action.UPDATE";

    private boolean isWaitingHtml;
    private Pattern patternPermalinkMeta;
    private Pattern patternGridBottom;
    private WebView webview;
    private int crawlingPage;
    private long lastUpdatedPhotoId;

    private L logger = L.get(UpdateService.class.getSimpleName());

    @Inject
    PreferenceRepository prefDataStore;
    @Inject
    PhotoDetailsRepository photoDetailsRepository;
    @Inject
    PhotoTagRepository photoTagRepository;

    public static void startActionUpdate(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(ACTION_UPDATE);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ((BaseApp)getApplication()).getApplicationComponent().inject(this);

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
            lastUpdatedPhotoId = prefDataStore.getLastCrawledPhotoId();
            if (!prefDataStore.hasPhotoCrawled()) {
                lastUpdatedPhotoId = photoDetailsRepository.getLargestPhotoId();
                prefDataStore.setLastCrawledPhotoId(lastUpdatedPhotoId);
            }
            logger.d("update started lastUpdatedPhotoId=%d", lastUpdatedPhotoId);

            startPageCrawling(1);
        }

        return START_NOT_STICKY;
    }

    private void createWebBrowser() {
        patternPermalinkMeta = Pattern.compile("permalinkMeta.*>\\n.*<p>(.*)</p>");
        patternGridBottom = Pattern.compile("<!--<li class=\"gridTags\"><span>(.*)</span></li>-->(.|\\n)*<li class=\"gridNotes\"><span>(.*)</span></li>");

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

    private void startPageCrawling(int page) {
        String urlToCrawl = "http://xkcn.info/page/" + page;
        logger.d("startPageCrawling %s", urlToCrawl);
        crawlingPage = page;
        isWaitingHtml = true;
        webview.loadUrl(urlToCrawl);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CrawlNextPageEvent e) {
        startPageCrawling(e.getPage());
    }

    public PhotoDetails parsePhotoNode(HtmlCleaner htmlCleaner, TagNode post, HashSet<String> photoTags) {
        PhotoDetails photo = null;

        String dataType = post.getAttributeByName("data-type");
        if ("photo".equals(dataType)) {
            photo = new PhotoDetails();

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
                Matcher matcher = patternPermalinkMeta.matcher(templateText);
                if (matcher.find()) {
                    String permalinkMetaValue = matcher.group(1);
                    photo.setPermalinkMeta(permalinkMetaValue);
                }
            }

            TagNode gridBottom = post.findElementByAttValue("class", "gridBottom", true, true);
            if (gridBottom != null) {
                String gridBottomHtml = htmlCleaner.getInnerHtml(gridBottom);
                Matcher matcher = patternGridBottom.matcher(gridBottomHtml);
                if (matcher.find()) {
                    String tags = matcher.group(1);
                    String notes = matcher.group(3);

                    photo.setTags(tags);
                    photo.setNotes(Integer.valueOf(notes));

                    String[] tagArr = tags.split("#");
                    if (tagArr != null && tags.length() != 0) {
                        for (String tag : tagArr) {
                            tag = Html.fromHtml(tag.trim()).toString();
                            if (TextUtils.isEmpty(tag)) {
                                continue;
                            }
                            logger.d("tag=%s", tag);
                            photoTags.add(tag);
                        }
                    }
                }
            }
        }

        return photo;
    }

    public class CrawlerJsInterface {
        @JavascriptInterface
        public void processHTML(String html) {
            List<PhotoDetails> photoList = new ArrayList<>();
            HashSet<String> photoTagValues = new HashSet<>();
            if (html != null) {
                HtmlCleaner htmlCleaner = new HtmlCleaner();
                TagNode root = htmlCleaner.clean(html);
                for (int i = 1; ; ++i) {
                    try {
                        Object[] objs = root.evaluateXPath(String.format(XPATH_POST_ID, i));
                        if (objs.length > 0) {
                            TagNode post = (TagNode) objs[0];
                            PhotoDetails photo = parsePhotoNode(htmlCleaner, post, photoTagValues);
                            if (photo != null) {
                                photo.setStatus(ModelConstants.PHOTO_STATUS_CRAWLING);
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

            photoDetailsRepository.addPhotos(photoList);
            List<PhotoTag> photoTags = createCrawlingPhotoTags(photoTagValues);
            photoTagRepository.addTags(photoTags);

            int listSize = photoList.size();
            long lastPhotoId = listSize > 0 ? photoList.get(listSize - 1).getIdentifier() : 0;
            logger.d("lastPhotoId=%d", lastPhotoId);
            if (listSize == 0 || lastPhotoId <= lastUpdatedPhotoId) {
                logger.d("processHTML done");
                if (lastPhotoId > 0 && lastPhotoId <= lastUpdatedPhotoId) {
                    prefDataStore.setLastCrawledPhotoId(lastUpdatedPhotoId = photoDetailsRepository.getLargestPhotoId());
                    prefDataStore.setLastPhotoCrawlTime(System.currentTimeMillis());
                    logger.d("update finished lastUpdatedPhotoId=%d", lastUpdatedPhotoId);

                    photoDetailsRepository.updatePhotosStatus(ModelConstants.PHOTO_STATUS_CRAWLED);
                    photoTagRepository.updatePhotosStatus(ModelConstants.PHOTO_STATUS_CRAWLED);
                }
                EventBus.getDefault().post(new PhotoCrawlingFinishedEvent());
                stopSelf();
            } else {
                EventBus.getDefault().post(new CrawlNextPageEvent(crawlingPage + 1));
            }
        }
    }

    private List<PhotoTag> createCrawlingPhotoTags(HashSet<String> photoTagValues) {
        List<PhotoTag> photoTags = new ArrayList<>();

        if (photoTagValues == null || photoTags.isEmpty()) {
            return photoTags;
        }

        for (String val : photoTagValues) {
            PhotoTag tag = new PhotoTag();
            tag.setTag(val);
            tag.setStatus(ModelConstants.PHOTO_STATUS_CRAWLING);

            photoTags.add(tag);
        }

        return photoTags;
    }
}
