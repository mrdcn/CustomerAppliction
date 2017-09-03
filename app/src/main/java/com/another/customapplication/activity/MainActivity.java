package com.another.customapplication.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.another.customapplication.R;
import com.another.customapplication.view.ColorBorderTextView;

/**
 * Created by another on 17-2-18.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private ColorBorderTextView tv;
    private ColorBorderTextView black;
    private ColorBorderTextView white;
    private ColorBorderTextView blue;
    private LinearLayout tvContainer;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (ColorBorderTextView)findViewById(R.id.tv);
        black = (ColorBorderTextView)findViewById(R.id.tv_black);
        white = (ColorBorderTextView) findViewById(R.id.tv_white);
        blue = (ColorBorderTextView) findViewById(R.id.tv_blue);
        tvContainer = (LinearLayout) findViewById(R.id.text_container);
        black.setOnClickListener(this);
        blue.setOnClickListener(this);
        white.setOnClickListener(this);
        java_8test();
    }

    void java_8test(){
    }
    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.tv_black:
//                break;
//            case R.id.tv_white:
//                break;
//            case R.id.tv_blue:
//                break;
//        }
        if( v instanceof  ColorBorderTextView){
            ColorBorderTextView view = (ColorBorderTextView)v;

        }
    }

    private void addColorTextView(ColorBorderTextView view){

    }
}
