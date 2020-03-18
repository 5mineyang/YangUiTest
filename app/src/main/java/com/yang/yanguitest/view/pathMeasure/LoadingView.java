package com.yang.yanguitest.view.pathMeasure;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 加载圈
 */
public class LoadingView extends View {
    private Path mPath;             //圆的path
    private Path mDstPath;          //截取圆的path
    private Paint mPaint;
    private float mAnimalValue;     //用属性动画控制每段时间截取的长度 0-1
    private PathMeasure mPathMeasure;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPath = new Path();
        mDstPath = new Path();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置圆角
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(8);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(1500);
        //无限重复
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimalValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mPath.addCircle(w / 2, h / 2, 50, Path.Direction.CW);
        mPathMeasure = new PathMeasure(mPath, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (null == mPathMeasure) {
            return;
        }
        mDstPath.reset();
        /**
         * 由于硬件加速的问题，PathMeasure中的getSegment添加到dst数组中时会被导致一些错误，
         * 需要通过mDst.lineTo(0,0)来避免这样一个Bug。
         */
        mDstPath.lineTo(0, 0);
        //截取的终点
        float stop = mAnimalValue * mPathMeasure.getLength();
        //截取的起点
        float start = (float) (stop - (0.5 - Math.abs(mAnimalValue - 0.5)) * mPathMeasure.getLength());
        //每次要绘制的部分 startWithMoveTo起点是否是原起点
        mPathMeasure.getSegment(start, stop, mDstPath, true);
        //绘制截取部分
        canvas.drawPath(mDstPath, mPaint);
    }
}
