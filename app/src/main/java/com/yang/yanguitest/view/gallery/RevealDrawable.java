package com.yang.yanguitest.view.gallery;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 两张图片 裁剪绘制拼接
 */
public class RevealDrawable extends Drawable {
    private Drawable mUnSelectDrawable;
    private Drawable mSelectDrawable;
    private Rect mTempRect = new Rect();

    public RevealDrawable(Drawable unSelectDrawable, Drawable selectDrawable) {
        this.mUnSelectDrawable = unSelectDrawable;
        this.mSelectDrawable = selectDrawable;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        //灰色图片
        if (getLevel() == 0) {
            mUnSelectDrawable.draw(canvas);
        } else if (getLevel() == 5000) {   //正好移到中间了，设置有色彩图片
            mSelectDrawable.draw(canvas);
        } else {  //其他都是拼接起来的
            //获取当前Drawable矩形区域
            Rect bounds = getBounds();
            //获取比例
            float ratio = (getLevel() / 5000f) - 1f;
            /**
             * 绘制左边未选中drawable
             */
            //根据level获取截取的宽度
            int unSelectW = (int) (bounds.width() * Math.abs(ratio));
            //如果比例小于0 则截取中心往左区域 否则截取右边区域
            int unSelectGravity = ratio < 0 ? Gravity.START : Gravity.END;
            //从bounds获取相应大小的区域装载到temp中
            Gravity.apply(unSelectGravity, unSelectW, bounds.height(), bounds, mTempRect);
            //创建一张新的画布
            canvas.save();
            canvas.clipRect(mTempRect);
            mUnSelectDrawable.draw(canvas);
            //合并画布
            canvas.restore();
            /**
             * 绘制右边选中drawable
             */
            //根据level获取截取的宽度
            int selectW = bounds.width() - (int) (bounds.width() * Math.abs(ratio));
            //有色彩截取的区域与上面相反 如果比例大于0 则截取中心往左区域 否则截取右边区域
            int selectGravity = ratio > 0 ? Gravity.START : Gravity.END;
            //从bounds获取相应大小的区域装载到temp中
            Gravity.apply(selectGravity, selectW, bounds.height(), bounds, mTempRect);
            canvas.clipRect(mTempRect);
            mSelectDrawable.draw(canvas);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mUnSelectDrawable.setBounds(bounds);
        mSelectDrawable.setBounds(bounds);
    }

    @Override
    public int getIntrinsicWidth() {
        return Math.max(mUnSelectDrawable.getIntrinsicWidth(), mSelectDrawable.getIntrinsicWidth());
    }

    @Override
    public int getIntrinsicHeight() {
        return Math.max(mUnSelectDrawable.getIntrinsicHeight(), mSelectDrawable.getIntrinsicHeight());
    }

    @Override
    protected boolean onLevelChange(int level) {
        //重绘自己
        invalidateSelf();
        return true;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @SuppressLint("WrongConstant")
    @Override
    public int getOpacity() {
        return 0;
    }
}
