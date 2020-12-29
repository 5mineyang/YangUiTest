package com.yang.yanguitest.view.gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 横向滑动改变中间选中图片view
 */
public class GalleryScrollView extends HorizontalScrollView {
    private LinearLayout mLinearLayout;
    private int mChildWidth;

    public GalleryScrollView(@NonNull Context context) {
        this(context, null);
    }

    public GalleryScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalleryScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLinearLayout = new LinearLayout(getContext());
        mLinearLayout.setGravity(Gravity.CENTER);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //获取图片view的宽度
        mChildWidth = mLinearLayout.getChildAt(0).getWidth();
        //获取view中心点再减去图片view宽度一半，得到padding的距离
        int paddingLeftOrRight = (getWidth() / 2) - (mChildWidth / 2);
        //让第一张和最后一张图片居中显示，设置左右padding
        mLinearLayout.setPadding(paddingLeftOrRight, 0, paddingLeftOrRight, 0);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //渐变图片
        reveal();
    }

    //移动的核心代码
    private void reveal() {
        //滑动的x坐标
        int scrollX = getScrollX();
        //通过滑动x坐标 可以得到当前滑动到了第几个view
        int leftIndex = scrollX / mChildWidth;
        //遍历所有子view 设置level 然后在Drawable的onLevelChange方法里处理
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            //已经滑到的图片 左边的和右边的都要处理
            if (i == leftIndex || i == leftIndex + 1) {
                //5000除图片宽度 获得比例
                float ratio = 5000f / mChildWidth;
                //左边图片
                ImageView leftSelectImg = (ImageView) mLinearLayout.getChildAt(leftIndex);
                //scrollX % mChildWidth代表滑出去的距离
                leftSelectImg.setImageLevel((int) (5000 - scrollX % mChildWidth * ratio));
                //右边图片
                if (leftIndex + 1 < mLinearLayout.getChildCount()) {
                    ImageView rightSelectImg = (ImageView) mLinearLayout.getChildAt(leftIndex + 1);
                    rightSelectImg.setImageLevel((int) (10000 - scrollX % mChildWidth * ratio));
                }
            } else {  //没有滑动到的图片
                //设置为0 回头在Drawable的onLevelChange方法里处理
                ImageView unSelectImg = (ImageView) mLinearLayout.getChildAt(i);
                unSelectImg.setImageLevel(0);
            }
        }
    }

    public void addImageViews(Drawable[] drawables) {
        for (int i = 0; i < drawables.length; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(drawables[i]);
            if (i == 0) {
                imageView.setImageLevel(5000);
            }
            mLinearLayout.addView(imageView);
        }
        addView(mLinearLayout);
    }
}
