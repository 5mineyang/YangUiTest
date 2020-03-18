package com.yang.yanguitest.view.bezier;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

/**
 * 气泡辅助 在列表里使用
 */
public class DragBubbleViewListener implements OnTouchListener, DragBubbleView.OnDragBubbleListener {
    private WindowManager mWm;
    private WindowManager.LayoutParams mParams;
    private DragBubbleView mDragBubbleView;
    private View pointLayout;
    private int number;

    public DragBubbleViewListener(Context context, View pointLayout) {
        this.pointLayout = pointLayout;
        this.number = (int) pointLayout.getTag();
        mDragBubbleView = new DragBubbleView(context);
        mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.format = PixelFormat.TRANSLUCENT;   //使窗口支持透明度

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 当按下时，将自定义View添加到WindowManager中
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //请求其父级View不拦截Touch事件
            v.getParent().requestDisallowInterceptTouchEvent(true);
            int[] points = new int[2];
            //获取pointLayout在屏幕中的位置（layout的左上角坐标）
            pointLayout.getLocationInWindow(points);
            //获取初始小红点中心坐标
            int x = points[0] + pointLayout.getWidth() / 2;
            int y = points[1] + pointLayout.getHeight() / 2;
            mDragBubbleView.setBubbleText(String.valueOf(number));
            mDragBubbleView.setShowLocation(x, y);
            mDragBubbleView.setOnDragBubbleListener(this);
            //添加当前DragBubbleView到WindowManager
            mWm.addView(mDragBubbleView, mParams);
            pointLayout.setVisibility(View.INVISIBLE);
        }
        //将所有touch事件转交给DragBubbleView处理
        mDragBubbleView.onTouchEvent(event);
        return true;
    }

    @Override
    public void onBubbleRest() {
        //弹回时，去除该View，等下次ACTION_DOWN的时候再添加
        if (mWm != null && mDragBubbleView.getParent() != null) {
            mWm.removeView(mDragBubbleView);
            pointLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBubbleBurst() {
        if (mWm != null && mDragBubbleView.getParent() != null) {
            mWm.removeView(mDragBubbleView);
        }
    }
}
