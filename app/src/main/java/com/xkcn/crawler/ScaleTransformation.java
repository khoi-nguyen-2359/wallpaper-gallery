package com.xkcn.crawler;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by khoinguyen on 12/25/14.
 */
public class ScaleTransformation implements Transformation {
    private float scaleRatio = 1;
    private String key;

    public ScaleTransformation(float ratio) {
        scaleRatio = ratio;
        key = "scale("+scaleRatio+")";
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int width = (int) (source.getWidth() * scaleRatio);
        int height = (int) (source.getHeight() * scaleRatio);
        Bitmap result = Bitmap.createScaledBitmap(source, width, height, true);
        if (result != source) {
            source.recycle();
        }

        return result;
    }

    @Override
    public String key() {
        return key;
    }
}
