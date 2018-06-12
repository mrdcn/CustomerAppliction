package com.another.customapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.another.customapplication.R;

public class HorizontalDragView extends RelativeLayout {


    ScrollView dragContentView;
    DragHelper mDragHelper;

    private int mDragState = -1;

    private final int DEFUALT_VIEW_HEIGHT = 100;
    private final int DEFUALT_VIEW_WIDTH = LayoutParams.MATCH_PARENT;

    public HorizontalDragView(Context context) {
        super(context);
        init();
    }

    public HorizontalDragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalDragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void init(){
        mDragHelper = DragHelper.newInstance(this);
    }




    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dragContentView = (ScrollView) findViewById(R.id.drag_content);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        float touchX = event.getX();
        float touchY = event.getY();
        float currentX;
        float currentY;


        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                if(mDragHelper.isPointOnView(event)){
                    mDragState = DragState.STATE_DRAGING;
                }else{
                    mDragState = DragState.STATE_OUTSIDE;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(mDragState == DragState.STATE_DRAGING){
                    // TODO: 2018/6/12  处理滑动事件

                }

                break;
            case MotionEvent.ACTION_UP:
                if((mDragState & (DragState.STATE_DRAGING|DragState.STATE_MOVING)) != 0 ){
                    // TODO: 2018/6/12 如果当前位置在临界点之下， 缩回去， 否则移动到顶部， 并且可以滑动
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    public void showContnet(){

    }

    public void hideContent(){

    }



}
