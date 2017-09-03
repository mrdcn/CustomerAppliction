package com.another.customapplication.activity;

import android.app.Activity;

/**
 * Created by another on 17-3-31.
 */

public class Java8TestActivity extends Activity {
    void java_8test(){
    }

    class Java8{
        Object field;
        public void out(){
            System.out.print(field);
        }
        public void in(Object field){
            this.field = field;
        }
    }
}
