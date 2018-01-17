package com.another.customapplication.activity;

import android.os.Bundle;

import com.another.customapplication.R;

public class BActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        discriptor = "-----B";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
    }



}
