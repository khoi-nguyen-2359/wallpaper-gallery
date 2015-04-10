package com.xkcn.crawler.imageloader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.ImageInfo;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * Created by khoinguyen on 4/6/15.
 */
public class DraweeImageViewTouch extends ImageViewTouch implements ControllerListener<ImageInfo> {
    DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;

    public DraweeImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraweeImageViewTouch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);

        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .build();
        mDraweeHolder = DraweeHolder.create(hierarchy, context);
    }

    @Override
    public void setImageURI(Uri uri) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(this)
                .setOldController(mDraweeHolder.getController())
                .build();
        mDraweeHolder.setController(controller);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDraweeHolder.onAttach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        mDraweeHolder.onAttach();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDraweeHolder.getHierarchy().getTopLevelDrawable();
    }

    @Override
    public void onSubmit(String s, Object o) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mDraweeHolder.onTouchEvent(event);
        boolean result2 = super.onTouchEvent(event);
        return result || result2;
    }

    @Override
    public void onFinalImageSet(String s, ImageInfo imageInfo, Animatable animatable) {
        Drawable drawable = mDraweeHolder.getHierarchy().getTopLevelDrawable();
        drawable.setBounds(0, 0, imageInfo.getWidth(), imageInfo.getHeight());
        setImageDrawable(drawable);
    }

    @Override
    public void onIntermediateImageSet(String s, ImageInfo imageInfo) {

    }

    @Override
    public void onIntermediateImageFailed(String s, Throwable throwable) {

    }

    @Override
    public void onFailure(String s, Throwable throwable) {

    }

    @Override
    public void onRelease(String s) {

    }
}
