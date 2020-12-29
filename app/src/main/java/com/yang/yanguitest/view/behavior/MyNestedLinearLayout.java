package com.yang.yanguitest.view.behavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent2;

import com.yang.yanguitest.R;

import java.lang.reflect.Constructor;

/**
 * 支持嵌套滑动的布局
 */
public class MyNestedLinearLayout extends LinearLayout implements NestedScrollingParent2 {

    public MyNestedLinearLayout(Context context) {
        super(context);
    }

    public MyNestedLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNestedLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 返回true接收事件
     */
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {

    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {

    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            MyLayoutParams layoutParams = (MyLayoutParams) childAt.getLayoutParams();
            MyBehavior behavior = layoutParams.behavior;
            //behavior接收这个事件再往下走
            if (behavior != null && behavior.layoutDependsOn(this, childAt, target)) {
                behavior.onNestedScroll(this, childAt, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
            }
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MyLayoutParams(getContext(), attrs);
    }

    class MyLayoutParams extends LayoutParams {
        private MyBehavior behavior;

        public MyLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.MyNestedLinearLayout);
            String className = a.getString(R.styleable.MyNestedLinearLayout_layout_behavior);
            behavior = parseBehavior(c, attrs, className);
            a.recycle();
        }

        private MyBehavior parseBehavior(Context context, AttributeSet attrs, String className) {
            if (TextUtils.isEmpty(className)) {
                return null;
            }
            try {
                //反射获取类
                Class clazz = Class.forName(className, true, context.getClassLoader());
                if (!MyBehavior.class.isAssignableFrom(clazz)) {
                    return null;
                }
                //获取构造方法
                Constructor<? extends MyBehavior> constructor = clazz.getConstructor(new Class[]{Context.class, AttributeSet.class});
                constructor.setAccessible(true);
                return constructor.newInstance(context, attrs);
            } catch (Exception e) {
                throw new RuntimeException("Could not inflate Behavior subclass " + className, e);
            }
        }
    }
}
