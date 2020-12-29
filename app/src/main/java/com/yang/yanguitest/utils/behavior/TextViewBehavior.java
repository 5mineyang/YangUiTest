package com.yang.yanguitest.utils.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.yang.yanguitest.R;
import com.yang.yanguitest.view.behavior.MyBehavior;

public class TextViewBehavior extends MyBehavior {

    public TextViewBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    //筛选当前被观察者是否是你要的被观察者
    @Override
    public boolean layoutDependsOn(@NonNull View parent, @NonNull View child, @NonNull View target) {
        return target instanceof NestedScrollView && target.getId() == R.id.nsvBehavior;
    }

    @Override
    public void onNestedScroll(@NonNull View parent, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type) {
        if (target.getY() > 0 || (child.getY() <= 0 && target.getY() <= child.getHeight())) {
            int translationY = -(target.getScrollY() > child.getHeight() ? child.getHeight() : target.getScrollY());
            child.setTranslationY(translationY);
            target.setTranslationY(translationY);
            ViewGroup.LayoutParams layoutParams = target.getLayoutParams();
            layoutParams.height = parent.getHeight() - child.getHeight() - translationY;
            target.setLayoutParams(layoutParams);
        }
    }
}
