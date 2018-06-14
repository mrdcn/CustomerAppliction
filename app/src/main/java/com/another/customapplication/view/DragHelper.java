package com.another.customapplication.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

import java.util.Arrays;

public class DragHelper {


    public static final int INVALID_POINTER = -1;


    public static final int STATE_IDLE = 0;


    public static final int STATE_DRAGGING = 1;

    public static final int STATE_SETTLING = 2;


    //    private View mDragView;
    private Callback mCallback;
    private OverScroller mScroller;
    private int mDragState;

    private VelocityTracker mVelocityTracker;
    private float mMinVelocity;
    private float mMaxVelocity;


    private int mActivePointerId = INVALID_POINTER;

    private static final Interpolator sInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    ViewGroup mParentView;
    View mCapturedView;
    int mTouchSlop;

    public DragHelper(Context context, ViewGroup viewGroup, Callback callback) {
        mParentView = viewGroup;
        mCallback = callback;
        final ViewConfiguration vc = ViewConfiguration.get(context);
        mMaxVelocity = vc.getScaledMaximumFlingVelocity();
        mMinVelocity = vc.getScaledMinimumFlingVelocity();
        mTouchSlop = vc.getScaledTouchSlop();
        mScroller = new OverScroller(context, sInterpolator);
    }

    private boolean checkTouchSlop(View child, float dx, float dy) {
        if (child == null)
            return false;
        final boolean check = mCallback.getViewDragRange(child) > 0;

        if (check) {
            return Math.abs(dy) > mTouchSlop;
        }
        return false;
    }


    public boolean shouldInterceptTouchEvent(MotionEvent event) {
        Log.e("drag" , event.getAction()+"");
        final int action = event.getActionMasked();
        final int actionIndex = event.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN) {
            //down is always first action.
            cancel();
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();
                final int pointerId = event.getPointerId(0);
                final View toCapture = findTopChildUnder((int) x, (int) y);
                if (toCapture == mCapturedView && mDragState == STATE_SETTLING) {
                    tryCaptureViewForDrag(toCapture, pointerId);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointId = event.getPointerId(actionIndex);
                final float x = event.getX(actionIndex);
                final float y = event.getY(actionIndex);
                saveInitialMotion(x, y, pointId);
                if (mDragState == STATE_SETTLING) {
                    final View toCapture = findTopChildUnder((int) x, (int) y);
                    if (toCapture == mCapturedView) {
                        tryCaptureViewForDrag(toCapture, pointId);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mInitialMotionX == null || mInitialMotionY == null) break;
                final int pointerCount = event.getPointerCount();
                for (int i = 0; i < pointerCount; i++) {
                    final int pointerId = event.getPointerId(i);

                    // If pointer is invalid then skip the ACTION_MOVE.
                    if (!isValidPointerForActionMove(pointerId)) continue;

                    final float x = event.getX(i);
                    final float y = event.getY(i);
                    final float dx = x - mInitialMotionX[pointerId];
                    final float dy = y - mInitialMotionY[pointerId];

                    final View toCapture = findTopChildUnder((int) x, (int) y);
                    final boolean pastSlop = toCapture != null && checkTouchSlop(toCapture, dx, dy);
                    if (pastSlop) {
                        // check the callback's
                        // getView[Horizontal|Vertical]DragRange methods to know
                        // if you can move at all along an axis, then see if it
                        // would clamp to the same value. If you can't move at
                        // all in every dimension with a nonzero range, bail.
                        final int oldTop = toCapture.getTop();
                        final int targetTop = oldTop + (int) dy;
                        final int newTop = mCallback.clampViewPositionVertical(toCapture, targetTop,
                                (int) dy);
                        final int vDragRange = mCallback.getViewDragRange(toCapture);
                        if ((vDragRange == 0 || (vDragRange > 0 && newTop == oldTop))) {
                            break;
                        }
                    }

                    if (mDragState == STATE_DRAGGING) {
                        // Callback might have started an edge drag
                        break;
                    }

                    if (pastSlop && tryCaptureViewForDrag(toCapture, pointerId)) {
                        break;
                    }
                }
                saveLastMotion(event);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerId = event.getPointerId(actionIndex);
                clearMotionHistory(pointerId);
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                cancel();
            }
        }

        return mDragState == STATE_DRAGGING;
    }

    private float[] mLastMotionX;
    private float[] mLastMotionY;
    private float[] mInitialMotionX;
    private float[] mInitialMotionY;
    private int mPointersDown;

    public void processTouchEvent(MotionEvent event) {

        final int action = event.getAction();
        final int actionIndex = event.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN) {
            //reset
            cancel();
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {

                float x = event.getX();
                float y = event.getY();
                final int pointerId = event.getPointerId(0);
                final View toCapture = findTopChildUnder((int) x, (int) y);
                saveInitialMotion(x, y, pointerId);
                tryCaptureViewForDrag(toCapture, pointerId);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {

                final int pointerId = event.getPointerId(actionIndex);
                final float x = event.getX(actionIndex);
                final float y = event.getY(actionIndex);

                saveInitialMotion(x, y, pointerId);

                if (mDragState == STATE_IDLE) {
                    final View toCapture = findTopChildUnder((int) x, (int) y);
                    tryCaptureViewForDrag(toCapture, pointerId);
                } else {
                    tryCaptureViewForDrag(mCapturedView, pointerId);
                }

                break;
            }
            case MotionEvent.ACTION_MOVE: {

                if (mDragState == STATE_DRAGGING) {
                    //if pointer is invalid then skip the action_move
                    if (!isValidPointerForActionMove(mActivePointerId)) break;
                    final int index = event.findPointerIndex(0);
                    final float x = event.getX(index);
                    final float y = event.getY(index);
                    final int idx = (int) (x - mLastMotionX[mActivePointerId]);
                    final int idy = (int) (y - mLastMotionY[mActivePointerId]);

                    dragTo(mCapturedView.getLeft() + idx, mCapturedView.getTop() + idy, idx, idy);
                    saveLastMotion(event);
                } else {
                    final int pointerCount = event.getPointerCount();
                    for (int i = 0; i < pointerCount; i++) {
                        final int pointerId = event.getPointerId(i);
                        if (isValidPointerForActionMove(pointerId))
                            continue;
                        final float x = event.getX(i);
                        final float y = event.getY(i);
                        final float dx = x - mInitialMotionX[pointerId];
                        final float dy = y - mInitialMotionY[pointerId];
                    }
                }
                break;

            }
            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerId = event.getPointerId(actionIndex);
                if (mDragState == STATE_DRAGGING && pointerId == mActivePointerId) {
                    int newActivePointer = INVALID_POINTER;
                    final int pointerCount = event.getPointerCount();
                    for (int i = 0; i < pointerCount; i++) {
                        final int id = event.getPointerId(i);
                        if (id == mActivePointerId) {
                            continue;
                        }

                        final float x = event.getX();
                        final float y = event.getY();
                        //判断当前其他出点， 是否有在header上
                        if (findTopChildUnder((int) x, (int) y) == mCapturedView) {
                            newActivePointer = mActivePointerId;
                            break;
                        }
                    }

                    if (newActivePointer == INVALID_POINTER)
                        releaseViewForPointerUp();
                }
                clearMotionHistory(pointerId);
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mDragState == STATE_DRAGGING) {
                    releaseViewForPointerUp();
                }
                cancel();
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                cancel();
                break;
            }
        }
    }

    boolean tryCaptureViewForDrag(View toCapture, int pointerId) {
        if (toCapture == mCapturedView && mActivePointerId == pointerId) {
            // Already done!
            return true;
        }
        if (toCapture != null && mCallback.tryCaptureView(toCapture, pointerId)) {
            mActivePointerId = pointerId;
            captureChildView(toCapture, pointerId);
            return true;
        }
        return false;
    }

    public View findTopChildUnder(int x, int y) {
        final int childCount = mParentView.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = mParentView.getChildAt(mCallback.getOrderedChildIndex(i));
            if (x >= child.getLeft() && x < child.getRight()
                    && y >= child.getTop() && y < child.getBottom()) {
                return child;
            }
        }
        return null;
    }

    public void captureChildView(@NonNull View childView, int activePointerId) {
        if (childView.getParent() != mParentView) {
            throw new IllegalArgumentException("captureChildView: parameter must be a descendant "
                    + "of the ViewDragHelper's tracked parent view (" + mParentView + ")");
        }

        mCapturedView = childView;
        mActivePointerId = activePointerId;
//        mCallback.onViewCaptured(childView, activePointerId);
        setDragState(STATE_DRAGGING);
    }

    void setDragState(int state) {
        mParentView.removeCallbacks(mSetIdleRunnable);
        if (mDragState != state) {
            mDragState = state;
//            mCallback.onViewDragStateChanged(state);
            if (mDragState == STATE_IDLE) {
                mCapturedView = null;
            }
        }
    }

    int getDragState(){
        return mDragState;
    }

    public boolean isShowDragView(){
        return mDragState != STATE_IDLE;
    }

    private void releaseViewForPointerUp() {
        // TODO: 2018/6/13 release
    }

    public void cancel() {
        mActivePointerId = INVALID_POINTER;
        clearMotionHistory();

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    void clearMotionHistory() {
        if (mInitialMotionX == null) {
            return;
        }
        Arrays.fill(mInitialMotionX, 0);
        Arrays.fill(mInitialMotionY, 0);
        Arrays.fill(mLastMotionX, 0);
        Arrays.fill(mLastMotionY, 0);
//        Arrays.fill(mInitialEdgesTouched, 0);
//        Arrays.fill(mEdgeDragsInProgress, 0);
//        Arrays.fill(mEdgeDragsLocked, 0);
        mPointersDown = 0;
    }

    private void clearMotionHistory(int pointerId) {
        if (mInitialMotionX == null || !isPointerDown(pointerId)) {
            return;
        }
        mInitialMotionX[pointerId] = 0;
        mInitialMotionY[pointerId] = 0;
        mLastMotionX[pointerId] = 0;
        mLastMotionY[pointerId] = 0;
//        mInitialEdgesTouched[pointerId] = 0;
//        mEdgeDragsInProgress[pointerId] = 0;
//        mEdgeDragsLocked[pointerId] = 0;
        mPointersDown &= ~(1 << pointerId);
    }

    private void saveLastMotion(MotionEvent ev) {
        final int pointerCount = ev.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            final int pointerId = ev.getPointerId(i);
            // If pointer is invalid then skip saving on ACTION_MOVE.
            if (!isValidPointerForActionMove(pointerId)) {
                continue;
            }
            final float x = ev.getX(i);
            final float y = ev.getY(i);
            mLastMotionX[pointerId] = x;
            mLastMotionY[pointerId] = y;
        }
    }

    private void saveInitialMotion(float x, float y, int pointerId) {
        ensureMotionHistorySizeForId(pointerId);
        mInitialMotionX[pointerId] = mLastMotionX[pointerId] = x;
        mInitialMotionY[pointerId] = mLastMotionY[pointerId] = y;
//        mInitialEdgesTouched[pointerId] = getEdgesTouched((int) x, (int) y);
        mPointersDown |= 1 << pointerId;
    }

    private void ensureMotionHistorySizeForId(int pointerId) {
        if (mInitialMotionX == null || mInitialMotionX.length <= pointerId) {
            float[] imx = new float[pointerId + 1];
            float[] imy = new float[pointerId + 1];
            float[] lmx = new float[pointerId + 1];
            float[] lmy = new float[pointerId + 1];
            int[] iit = new int[pointerId + 1];
            int[] edip = new int[pointerId + 1];
            int[] edl = new int[pointerId + 1];

            if (mInitialMotionX != null) {
                System.arraycopy(mInitialMotionX, 0, imx, 0, mInitialMotionX.length);
                System.arraycopy(mInitialMotionY, 0, imy, 0, mInitialMotionY.length);
                System.arraycopy(mLastMotionX, 0, lmx, 0, mLastMotionX.length);
                System.arraycopy(mLastMotionY, 0, lmy, 0, mLastMotionY.length);
//                System.arraycopy(mInitialEdgesTouched, 0, iit, 0, mInitialEdgesTouched.length);
//                System.arraycopy(mEdgeDragsInProgress, 0, edip, 0, mEdgeDragsInProgress.length);
//                System.arraycopy(mEdgeDragsLocked, 0, edl, 0, mEdgeDragsLocked.length);
            }

            mInitialMotionX = imx;
            mInitialMotionY = imy;
            mLastMotionX = lmx;
            mLastMotionY = lmy;
//            mInitialEdgesTouched = iit;
//            mEdgeDragsInProgress = edip;
//            mEdgeDragsLocked = edl;
        }
    }


    //just deal vertical
    private void dragTo(int left, int top, int dx, int dy) {
        int clampedY = top;
        final int oldTop = mCapturedView.getTop();

        if (dy != 0) {
            clampedY = mCallback.clampViewPositionVertical(mCapturedView, top, dy);
            ViewCompat.offsetTopAndBottom(mCapturedView, clampedY - oldTop);
            final int clampedDy = clampedY - oldTop;
            mCallback.onViewPositionChanged(mCapturedView, left, clampedY, 0, clampedDy);
        }
    }

    public boolean isPointerDown(int pointerId) {
        return (mPointersDown & 1 << pointerId) != 0;
    }

    private boolean isValidPointerForActionMove(int pointerId) {

        return isPointerDown(pointerId);
    }


    public boolean isPointOnView(MotionEvent event) {
        Rect rect = new Rect();
        View headerView = mCapturedView;
        if (headerView == null)
            return false;
        rect.set((int) headerView.getX(), (int) headerView.getY(),
                (int) (headerView.getX() + headerView.getMeasuredWidth()),
                (int) (headerView.getY() + headerView.getMeasuredHeight()));
        return rect.contains((int) event.getX(), (int) event.getY());
    }

    public boolean isPointOnView(int x, int y) {
        Rect rect = new Rect();
        View headerView = mCapturedView;
        if (headerView == null)
            return false;
        rect.set((int) headerView.getX(), (int) headerView.getY(),
                (int) (headerView.getX() + headerView.getMeasuredWidth()),
                (int) (headerView.getY() + headerView.getMeasuredHeight()));
        return rect.contains(x, y);
    }


    private int computeSettleDuration(View child, int dx, int dy, int xvel, int yvel) {
//        xvel = clampMag(xvel, (int) mMinVelocity, (int) mMaxVelocity);
        yvel = clampMag(yvel, (int) mMinVelocity, (int) mMaxVelocity);
        final int absDx = Math.abs(dx);
        final int absDy = Math.abs(dy);
        final int absXVel = Math.abs(xvel);
        final int absYVel = Math.abs(yvel);
        final int addedVel = absXVel + absYVel;
        final int addedDistance = absDx + absDy;
        final float yweight = yvel != 0 ? (float) absYVel / addedVel :
                (float) absDy / addedDistance;
        // TODO: 2018/6/14 getViewDragRange
        int yduration = computeAxisDuration(dy, yvel, mCallback.getViewDragRange(child));

        return (int) (yduration * yweight);
    }

    private int clampMag(int value, int absMin, int absMax) {
        final int absValue = Math.abs(value);
        if (absValue < absMin) return 0;
        if (absValue > absMax) return value > 0 ? absMax : -absMax;
        return value;
    }

    private final int MAX_SETTLE_DURATION = 600;
    private final int BASE_SETTLE_DURATION = 256;

    private int computeAxisDuration(int delta, int velocity, int motionRange) {
        if (delta == 0) {
            return 0;
        }

        final int width = mParentView.getWidth();
        final int halfWidth = width / 2;
        final float distanceRatio = Math.min(1f, (float) Math.abs(delta) / width);
        final float distance = halfWidth + halfWidth
                * distanceInfluenceForSnapDuration(distanceRatio);

        int duration;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float range = (float) Math.abs(delta) / motionRange;
            duration = (int) ((range + 1) * BASE_SETTLE_DURATION);
        }
        return Math.min(duration, MAX_SETTLE_DURATION);
    }

    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * (float) Math.PI / 2.0f;
        return (float) Math.sin(f);
    }


    public boolean smoothSlideViewTo(View child, int finalLeft, int finalTop) {
        mCapturedView = child;
        boolean continueSliding = forceSettleCapturedViewAt(finalLeft, finalTop, 0, 0);
        return continueSliding;
    }

    private boolean forceSettleCapturedViewAt(int finalLeft, int finalTop, int xvel, int yvel) {
        final int startLeft = mCapturedView.getLeft();
        final int startTop = mCapturedView.getTop();

        int dx = finalLeft - startLeft;
        int dy = finalTop - startTop;

        if (dx == 0 && dy == 0) {
            mScroller.abortAnimation();
            return false;
        }

        final int duration = computeSettleDuration(mCapturedView, dx, dy, xvel, yvel);
        mScroller.startScroll(startLeft, startTop, dx, dy, duration);

        setDragState(STATE_SETTLING);
        return true;

    }


    public boolean continueSettling(boolean deferCallbacks) {
        if (mDragState == STATE_SETTLING) {

            boolean keepGoing = mScroller.computeScrollOffset();
            final int x = mScroller.getCurrX();
            final int y = mScroller.getCurrY();
            final int dx = x - mCapturedView.getLeft();
            final int dy = y - mCapturedView.getTop();

            if (dx != 0) {
                ViewCompat.offsetLeftAndRight(mCapturedView, dx);
            }
            if (dy != 0) {
                ViewCompat.offsetTopAndBottom(mCapturedView, dy);
            }

            if (dx != 0 || dy != 0) {
                mCallback.onViewPositionChanged(mCapturedView, x, y, dx, dy);
            }

            if (keepGoing && x == mScroller.getFinalX() && y == mScroller.getFinalY()) {
                // Close enough. The interpolator/scroller might think we're still moving
                // but the user sure doesn't.
                mScroller.abortAnimation();
                keepGoing = false;
            }

            if (!keepGoing) {
                if (deferCallbacks) {
                    mParentView.post(mSetIdleRunnable);
                } else {
                    setDragState(STATE_IDLE);
                }
            }
        }

        return mDragState == STATE_SETTLING;
    }

    private final Runnable mSetIdleRunnable = new Runnable() {
        @Override
        public void run() {
            setDragState(STATE_IDLE);
        }
    };


    public abstract static class Callback {
        abstract void onViewPositionChanged(View changedView, int left, int top, int right, int bottom);

        abstract void onViewReleased();

        abstract int clampViewPositionVertical(View view, int top, int dy);

        int getViewDragRange(View child) {
            return 1;
        }

        int getOrderedChildIndex(int index) {
            return index;
        }

        abstract boolean tryCaptureView(View child, int pointerId);


    }

}
