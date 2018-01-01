package com.example.mikke_000.letsgo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

// Based on:
// https://developer.android.com/training/gestures/scale.html
// https://developer.android.com/training/gestures/viewgroup.html

/**
 * Zoom-able and drag-able layout.
 * Must contain exactly 1 child.
 */
public class ZoomLayout extends FrameLayout {
    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    private class Point extends PointF {
        public Point(float x, float y) { super(x, y); }
        public float distanceTo(float x, float y) {
            float dx = this.x - x;
            float dy = this.y - y;
            return (float) Math.sqrt(dx*dx + dy*dy);
        }
        public float distanceTo(PointF point) {
            return this.distanceTo(point.x, point.y);
        }
    }

    public float minZoom = 1.0f;
    public float maxZoom = 4.0f;

    private Mode mode = Mode.NONE;
    private float scale = 1.0f;
    private Point start;
    private ScaleGestureDetector scaleDetector;
    private ViewConfiguration viewConfiguration;

    public ZoomLayout(Context context) {
        super(context);
        initialize(context);
    }

    public ZoomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);

        // https://developer.android.com/training/custom-views/create-view.html
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ZoomLayout,
                0, 0
        );
        try {
            this.minZoom = a.getFloat(R.styleable.ZoomLayout_minZoom, this.minZoom);
            this.maxZoom = a.getFloat(R.styleable.ZoomLayout_maxZoom, this.maxZoom);
        } finally {
            a.recycle();
        }
    }

    private void initialize(Context context) {
        this.viewConfiguration = ViewConfiguration.get(context);
        this.scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Point focus = new Point(detector.getFocusX(), detector.getFocusY());
                doDrag(focus);
                doScale(detector.getScaleFactor(), focus);
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                mode = Mode.ZOOM;
                start = new Point(detector.getFocusX(), detector.getFocusY());
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                mode = Mode.NONE;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        /**
         * This decides if we want to handle the touch event.
         * Actual handling of touch event happens in onTouchEvent.
         */

        this.scaleDetector.onTouchEvent(event);

        // handle events if we are currently doing something
        if (this.mode != Mode.NONE) {
            return true;
        }

        // otherwise decide if we want to do something
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                // decide if we've moved enough to be dragging instead of tapping
                if (this.mode == Mode.NONE) {
                    Point point = new Point(event.getX(), event.getY());
                    int dragSlop = this.viewConfiguration.getScaledTouchSlop();
                    float dragDist = this.start.distanceTo(point);
                    if (dragDist >= dragSlop) {
                        this.mode = Mode.DRAG;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                // we don't know if we're tapping or dragging yet
                this.start = new Point(event.getX(), event.getY());
                return false;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /**
         * Handles the actual touch event.
         * At this point we know we are either dragging or scaling.
         * All future touch events from when we start intercepting are sent here.
         */

        this.scaleDetector.onTouchEvent(event);

        int action = event.getActionMasked();
        if (this.mode == Mode.NONE) {
            if (action == MotionEvent.ACTION_MOVE && event.getPointerCount() == 1) {
                this.mode = Mode.DRAG; // special case for lifting 1 finger when scaling
                MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
                event.getPointerCoords(0, coords);
                this.start = new Point(coords.x, coords.y);
            } else {
                return false;
            }
        }
        if (this.mode == Mode.DRAG) {
            // we have to handle dragging manually - zoom is handled by ScaleGestureDetector
            switch (action) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    this.mode = Mode.NONE;
                    return false;
                case MotionEvent.ACTION_MOVE:
                    Point focus = new Point(event.getX(), event.getY());
                    this.doDrag(focus);
                    return true;
            }
        }

        return false;
    }

    private void doDrag(Point target) {
        float dx = target.x - this.start.x;
        float dy = target.y - this.start.y;
        this.start = target;

        View child = this.getChild();
        float width = child.getWidth();
        float height = child.getHeight();
        float trueWidth = width * child.getScaleX();
        float trueHeight = height * child.getScaleY();
        float deltaWidth = trueWidth - width;
        float deltaHeight = trueHeight - height;
        float translX = child.getTranslationX() + dx;
        float translY = child.getTranslationY() + dy;
        // keep child in parent
        translX = Math.min(deltaWidth / 2, Math.max(-deltaWidth / 2, translX));
        translY = Math.min(deltaHeight / 2, Math.max(-deltaHeight / 2, translY));
        child.setTranslationX(translX);
        child.setTranslationY(translY);
    }
    private void doScale(float factor, Point focus) {
        this.scale *= factor;
        this.scale = Math.min(this.maxZoom, Math.max(this.minZoom, this.scale));

        View child = this.getChild();
        child.setScaleX(this.scale);
        child.setScaleY(this.scale);
    }

    private View getChild() { return this.getChildAt(0); }
}
