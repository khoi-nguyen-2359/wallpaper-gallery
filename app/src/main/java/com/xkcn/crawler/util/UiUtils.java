package com.xkcn.crawler.util;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

/**
 * Created by khoinguyen on 1/27/15.
 */
public final class UiUtils {
    public static void dismissDlg(Dialog dlg) {
        if (dlg != null && dlg.isShowing()) {
            dlg.dismiss();
        }
    }

    public static Dialog showSimpleProgressDlg(Context context, DialogInterface.OnCancelListener cancelListener) {
        Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dlg.setContentView(new ProgressBar(context));
        dlg.setCancelable(true);
        dlg.setOnCancelListener(cancelListener);
        dlg.show();

        return dlg;
    }

    public static void makeStableLayout(View decorView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            return;

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    public static void hideStatusBar(Window window, View decorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        } else {
            if (window != null) {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        }
    }

    public static void showStatusBar(Window window, View decorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        } else {
            if (window != null) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                decorView.setSystemUiVisibility(0);
            }
        }
    }

    public static boolean isStatusBarVisible(int visibility) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && (visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                || (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
                && (visibility & View.SYSTEM_UI_FLAG_LOW_PROFILE) == 0);
        }

    public static boolean isStatusBarVisible(Window window, View decorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return (decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0;
        } else {
            return window != null && (decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LOW_PROFILE) == 0;
        }
    }
}
