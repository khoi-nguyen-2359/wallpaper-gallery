package com.xkcn.gallery.view.custom;

import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.facebook.drawee.drawable.DrawableUtils;
import com.khoinguyen.logging.L;

/**
 * Created by khoinguyen on 16/03/22.
 */
public class DashLineProgressDrawable extends Drawable {

    private static final int MAX_LEVEL = 10000;
    private static final int BAR_WIDTH_DEFAULT = 30;
    private static final long DUR_ELAPSED_ANIM = 750;
    private static final long DUR_FILL_ELAPSED = 500;

    private ObjectAnimator dashAnimator;
    private float barWidth;
    private int backgroundColor;
    private int elapsedProgressColor;
    private int remainedProgressColor;
    private Paint paintElapsedProgress;
    private Paint paintRemainedProgress;
    private int progress;
    private float dashOffsetPercent;

    private ObjectAnimator animFillElapsedProgress;

    public DashLineProgressDrawable() {
        paintElapsedProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRemainedProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        barWidth = BAR_WIDTH_DEFAULT;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(backgroundColor);
        paintRemainedProgress.setStrokeWidth(barWidth);
        paintRemainedProgress.setColor(remainedProgressColor);

        Rect bounds = getBounds();
        float startX = bounds.width()/4f;
        float startY = bounds.height() / 2f;
        float stopX = bounds.width() * (3f / 4);
        canvas.drawLine(startX, startY, stopX, startY, paintRemainedProgress);
//        L.get(this).d("startX=%f startY=%f stopX=%f", startX, startY, stopX);
        paintElapsedProgress.setColor(elapsedProgressColor);
        paintElapsedProgress.setStrokeWidth(barWidth);
        float progressStopX = startX + (stopX - startX) * (progress *1f / MAX_LEVEL);
        float dashOffset = dashOffsetPercent * barWidth;
        Path progressPath = new Path();
        progressPath.moveTo(startX, startY);
        progressPath.lineTo(progressStopX, startY);
        L.get(this).d("progressStopX=%f", progressStopX);

        PathEffect effect = new PathDashPathEffect(makeProgressDashPath(barWidth, barWidth), barWidth, dashOffset, PathDashPathEffect.Style.ROTATE);
        paintElapsedProgress.setPathEffect(effect);

        canvas.drawPath(progressPath, paintElapsedProgress);

        canvas.drawLine(progressStopX, startY, stopX, startY, paintRemainedProgress);

        paintRemainedProgress.setColor(backgroundColor);
        canvas.drawLine(stopX, startY, stopX + barWidth, startY, paintRemainedProgress);
    }

    @Override
    public void setAlpha(int alpha) {
        paintElapsedProgress.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paintElapsedProgress.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return DrawableUtils.getOpacityFromColor(paintElapsedProgress.getColor());
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidateSelf();
    }

    @Override
    protected boolean onLevelChange(int level) {
        triggerDashAnimation();
        triggerFillElapsedProgressAnimation();
        return false;
    }


    private void triggerFillElapsedProgressAnimation() {
        if (animFillElapsedProgress != null) {
            animFillElapsedProgress.cancel();
        }

        animFillElapsedProgress = ObjectAnimator.ofInt(this, "progress", progress, getLevel())
                .setDuration(DUR_FILL_ELAPSED);
        animFillElapsedProgress.setInterpolator(new AccelerateDecelerateInterpolator());
        animFillElapsedProgress.start();
    }

    private void triggerDashAnimation() {
        if (dashAnimator == null && getLevel() < MAX_LEVEL) {
            dashAnimator = ObjectAnimator.ofFloat(this, "dashOffsetPercent", 1.0f, 0.0f).setDuration(DUR_ELAPSED_ANIM);
            dashAnimator.setRepeatMode(ObjectAnimator.RESTART);
            dashAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            dashAnimator.setInterpolator(new LinearInterpolator());
            dashAnimator.start();
        }

        if (dashAnimator != null && getLevel() == MAX_LEVEL) {
            dashAnimator.cancel();
        }
    }

    private Path makeProgressDashPath(float width, float height) {
        Path p = new Path();
        p.moveTo(0, height/2);
        p.lineTo(width / 2, height / 2);
        p.lineTo(width, -height / 2);
        p.lineTo(width / 2, -height / 2);
        p.lineTo(0, height / 2);
        p.close();

        return p;
    }

    public void setBarWidth(float barWidth) {
        this.barWidth = barWidth;
    }

    public void setDashOffsetPercent(float dashOffsetPercent) {
        this.dashOffsetPercent = dashOffsetPercent;
        invalidateSelf();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setElapsedProgressColor(int elapsedProgressColor) {
        this.elapsedProgressColor = elapsedProgressColor;
    }

    public void setRemainedProgressColor(int remainedProgressColor) {
        this.remainedProgressColor = remainedProgressColor;
    }
}