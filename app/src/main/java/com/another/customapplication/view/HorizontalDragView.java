package com.another.customapplication.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.another.customapplication.R;

public class HorizontalDragView extends ViewGroup {


    ScrollView dragContentView;
    DragHelper mDragHelper;

    float mInitialMotionX;
    float mInitialMotionY;

    private int mDragState = -1;

    boolean mInLayout = false;

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

    void init() {
        mDragHelper = DragHelper.newInstance(this);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dragContentView = (ScrollView) findViewById(R.id.drag_content);
    }

    /**
     * 如果header显示时， 点击透明区域， 拦截
     * 如果header不显示，点击透明区域，不拦截
     * 如果header显示，点击header所在区域，则透传给子view
     * @param ev
     * @return
     */

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean interceptForTap = false;

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                final float x = ev.getX();
                final float y = ev.getY();
                mInitialMotionX = x;
                mInitialMotionY = y;
                interceptForTap = mDragHelper.isPointOnView(ev);
                break;
        }

        return interceptForTap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        float touchX = event.getX();
        float touchY = event.getY();
        float currentX;
        float currentY;


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                if (mDragHelper.isPointOnView(event)) {
                    mDragState = DragState.STATE_DRAGING;
                } else {
                    mDragState = DragState.STATE_OUTSIDE;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mDragState == DragState.STATE_DRAGING) {
                    // TODO: 2018/6/12  处理滑动事件

                }

                break;
            case MotionEvent.ACTION_UP:
                if ((mDragState & (DragState.STATE_DRAGING | DragState.STATE_MOVING)) != 0) {
                    // TODO: 2018/6/12 如果当前位置在临界点之下， 缩回去， 否则移动到顶部， 并且可以滑动
                }
                break;
        }

        return super.onTouchEvent(event);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mInLayout = true;
        int  viewHeight = getMeasuredHeight();
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            if(child.getVisibility() == GONE)
                continue;
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (isHeaderView(child)) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                //just bottom
                int childTop = viewHeight - (int)(childHeight * lp.onScreen);
                final float newOffset = (viewHeight - childTop) / childHeight;
                boolean chageOffest = newOffset == lp.onScreen;

                child.layout(lp.leftMargin,childTop , lp.leftMargin + childWidth,childTop + childHeight);

                if(chageOffest){
                    setDrawerViewOffset(child,newOffset);
                }

                final int newVisibility = lp.onScreen > 0 ? VISIBLE : INVISIBLE;
                if (child.getVisibility() != newVisibility) {
                    child.setVisibility(newVisibility);
                }

            } else {
                child.layout(lp.leftMargin, lp.topMargin, lp.leftMargin + child.getMeasuredWidth(), lp.topMargin + child.getMeasuredHeight());
            }

        }


    }

    void setDrawerViewOffset(View drawerView , float slideOffset){
        final LayoutParams lp = (LayoutParams) drawerView.getLayoutParams();
        if (slideOffset == lp.onScreen) {
            return;
        }
        lp.onScreen = slideOffset;
        dispatchOnDrawerSlide(drawerView,slideOffset);
    }

    void dispatchOnDrawerSlide(View drawerView, float slideOffset) {
        //notify the listeners
    }

    private boolean isHeaderView(View view) {
        if (view == null)
            return false;
        return view.getId() == R.id.drag_header;
    }

    public void showContnet() {

    }

    public void hideContent() {

    }


    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int FLAG_IS_OPENED = 0x1;
        private static final int FLAG_IS_OPENING = 0x2;
        private static final int FLAG_IS_CLOSING = 0x4;

        public int gravity = Gravity.NO_GRAVITY;
        float onScreen;
        boolean isPeeking;
        int openState;

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);

//            final TypedArray a = c.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
//            this.gravity = a.getInt(0, Gravity.NO_GRAVITY);
            //目前先不处理
//            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            this(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(@NonNull DrawerLayout.LayoutParams source) {
            super(source);
            this.gravity = source.gravity;
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }


}
