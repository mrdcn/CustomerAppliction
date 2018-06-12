package com.another.customapplication.view;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.another.customapplication.R;

public class DragHelper {

    private View mDragView;

    public static DragHelper newInstance(View dragView) {
        DragHelper dragHelper = new DragHelper();
        dragHelper.mDragView = dragView;
        return dragHelper;
    }

    private DragHelper() {

    }


    public boolean isPointOnView(MotionEvent event) {
        boolean isPointonView;
        Rect rect = new Rect();
        View headerView = mDragView.findViewById(R.id.drag_header);
        if (headerView == null)
            return false;
        rect.set((int) headerView.getX(), (int) headerView.getY(),
                (int) (headerView.getX() + headerView.getMeasuredWidth()),
                (int) (headerView.getY() + headerView.getMeasuredHeight()));
        return rect.contains((int) event.getX(), (int) event.getY());
    }


    public abstract class Callback{
        abstract  void onViewPositionChanged(View changedView , int left , int top , int right , int bottom);

        abstract  void onViewReleased();
    }

}
