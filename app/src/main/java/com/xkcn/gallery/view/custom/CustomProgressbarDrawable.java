package com.xkcn.gallery.view.custom;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import com.fantageek.toolkit.util.L;

/**
 * Created by 06peng on 15/6/26.
 */
public class CustomProgressbarDrawable extends Drawable {

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    protected boolean onLevelChange(int level) {
        L.get(this).d("onLevelChange %d", level);
        return super.onLevelChange(level);
    }
}