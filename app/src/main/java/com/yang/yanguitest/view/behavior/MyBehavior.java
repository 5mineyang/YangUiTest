package com.yang.yanguitest.view.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class MyBehavior {

    public MyBehavior(Context context, AttributeSet attributeSet) {

    }

    /**
     * 触摸事件
     */
    public boolean onInterceptTouchEvent(@NonNull View parent, @NonNull View child, @NonNull MotionEvent ev) {
        return false;
    }

    /**
     * 筛选观察者
     */
    public boolean layoutDependsOn(@NonNull View parent, @NonNull View child, @NonNull View target) {
        return false;
    }

    /**
     * 观察者的大小或者位置发生变化的时候
     */
    public boolean onDependentViewChanged(@NonNull View parent, @NonNull View child, @NonNull View target) {
        return false;
    }


    /**
     * 开始嵌套滑动的方法
     */
    public boolean onStartNestedScroll(@NonNull View parent, @NonNull View child, @NonNull View directTargetChild,
                                       @NonNull View target, int axes) {
        return false;
    }

    /**
     * 嵌套滑动中的方法
     */
    public void onNestedScroll(@NonNull View parent, @NonNull View child, @NonNull View target, int dxConsumed,
                               int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
    }

    public void onNestedScrollAccepted(@NonNull View parent, @NonNull View child, @NonNull View directTargetChild,
                                       @NonNull View target, int axes) {
    }

    public void onStopNestedScroll(@NonNull View parent, @NonNull View child, @NonNull View target) {
    }

    /**
     * 在target每次滑动之前会调用这个方法，。
     *
     * @param target   发出NestedScroll事件的子view
     * @param dx       这次滑动事件在x方向上滑动的距离
     * @param dy       这次滑动事件在y方向上滑动的距离
     * @param consumed 一个长度为2的数组。第0位时我们在x方向消耗的滑动距离，第1位是我们在y方向上消耗的滑动距离。子view会根据这个和dx/dy来计算余下的滑动量，
     *                 来决定自己是否还要进行剩下的滑动。比如我们使consumed[1] = dy，那么子view在y方向上就不会滑动。
     */
    public void onNestedPreScroll(@NonNull View target, View child, int dx, int dy, @NonNull int[] consumed, int type) {
    }
}
