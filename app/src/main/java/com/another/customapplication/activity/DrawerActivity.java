package com.another.customapplication.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.another.customapplication.R;

public class DrawerActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
    }
}
