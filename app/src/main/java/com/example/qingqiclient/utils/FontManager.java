package com.example.qingqiclient.utils;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/10/30 0030.
 * 这个类对android的某个activity范围内的字体做设置
 */

public class FontManager {

    public static void changeFonts(ViewGroup root, Activity act){
        if (root == null || act == null)   //防止抛出异常以致程序停止
            return;

        Typeface tf = Typeface.createFromAsset(act.getAssets(), "fonts/STKAITI.TTF");
        for (int i=0; i<root.getChildCount(); i++){
            View v = root.getChildAt(i);
            if (v instanceof TextView){
                ((TextView) v).setTypeface(tf);
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(tf);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(tf);
            } else if (v instanceof ViewGroup) {
                changeFonts((ViewGroup) v,act);
            }
        }
    }

}
