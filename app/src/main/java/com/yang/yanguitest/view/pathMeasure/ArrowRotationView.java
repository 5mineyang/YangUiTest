package com.yang.yanguitest.view.pathMeasure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.yang.yanguitest.R;

/**
 * 箭头在圆上面旋转
 */
public class ArrowRotationView extends View {
    private Path mPath;                 //存放圆的path
    private Paint mPaint;
    private int mViewCenterX;           //view中心
    private int mViewCenterY;
    private PathMeasure mPathMeasure;
    private Bitmap mBitmap;             //箭头bitmap
    private Matrix mMatrix;
    private float mCurrentValue = 0;    //每次截取的系数
    private float[] pos;
    private float[] tan;

    public ArrowRotationView(Context context) {
        this(context, null);
    }

    public ArrowRotationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowRotationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPath = new Path();
        mPath.addCircle(0, 0, 150, Path.Direction.CW);
        mPathMeasure = new PathMeasure(mPath, false);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);

        BitmapFactory.Options options = new BitmapFactory.Options();
        //缩小尺寸
        options.inSampleSize = 10;
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow, options);
        mMatrix = new Matrix();
        pos = new float[2];
        tan = new float[2];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewCenterX = w / 2;
        mViewCenterY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //移动坐标系
        canvas.translate(mViewCenterX, mViewCenterY);
        mPaint.setColor(Color.GRAY);
        //绘制x轴和y轴的线
        canvas.drawLine(-getWidth() / 2, 0, getWidth() / 2, 0, mPaint);
        canvas.drawLine(0, -getHeight() / 2, 0, getHeight() / 2, mPaint);
        //绘制圆
        mPaint.setColor(Color.RED);
        canvas.drawPath(mPath, mPaint);
        //每次重绘改变比例
        mCurrentValue += 0.005;
        if (mCurrentValue >= 1) {
            mCurrentValue = 0;
        }
        /**
         * 方案一
         */
//        //获取相应位置的矩阵
//        pathMeasure.getMatrix(mCurrentValue * pathMeasure.getLength(), mMatrix, PathMeasure.TANGENT_MATRIX_FLAG | PathMeasure.POSITION_MATRIX_FLAG);
//        //位置稍微偏移下
//        mMatrix.preTranslate(-mBitmap.getWidth() / 2, -mBitmap.getHeight() / 2);
        /**
         * 方案二
         */
        //pos存放截取的点坐标 tan中心点
        mPathMeasure.getPosTan(mCurrentValue * mPathMeasure.getLength(), pos, tan);
        //计算图片旋转角度(或弧度)
        float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
        mMatrix.reset();
        mMatrix.postRotate(degrees, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        mMatrix.postTranslate(pos[0] - mBitmap.getWidth() / 2, pos[1] - mBitmap.getHeight() / 2);
        //绘制箭头
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        invalidate();
    }
}
