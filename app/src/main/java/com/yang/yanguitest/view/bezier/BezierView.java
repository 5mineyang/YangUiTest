package com.yang.yanguitest.view.bezier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 贝塞尔曲线view
 */
public class BezierView extends View {
    private Paint mPointLinePaint;      //点和线的画笔
    private List<PointF> mPointList;    //几介贝塞尔曲线的连接点
    private Paint mPaint;               //曲线画笔
    private Path mPath;                 //曲线path
    private boolean isRecursion = true; //默认递归计算

    public BezierView(Context context) {
        this(context, null);
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPointLinePaint = new Paint();
        //抗锯齿
        mPointLinePaint.setAntiAlias(true);
        //画笔宽度
        mPointLinePaint.setStrokeWidth(4);
        //颜色
        mPointLinePaint.setColor(Color.GRAY);
        //空心的
        mPointLinePaint.setStyle(Paint.Style.STROKE);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Paint.Style.STROKE);
        mPointList = new ArrayList<>();
        mPath = new Path();
        init();
    }

    private void init() {
        mPointList.clear();
        Random random = new Random();
        int bezierInt = random.nextInt(isRecursion ? 10 : 100) + 2;
        //随机生成几介贝塞尔
        for (int i = 0; i < bezierInt; i++) {
            //随机生成坐标
            int x = random.nextInt(800) + 200;
            int y = random.nextInt(800) + 200;
            PointF pointF = new PointF(x, y);
            mPointList.add(pointF);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //先绘制点和线
        for (int i = 0; i < mPointList.size(); i++) {
            if (i > 0) {
                mPointLinePaint.setColor(Color.GRAY);
                //绘制线
                canvas.drawLine(mPointList.get(i - 1).x, mPointList.get(i - 1).y, mPointList.get(i).x, mPointList.get(i).y, mPointLinePaint);
            }
            //起点和终点颜色绘制不同的
            if (i == 0) {
                mPointLinePaint.setColor(Color.RED);
            } else if (i == mPointList.size() - 1) {
                mPointLinePaint.setColor(Color.GREEN);
            }
            //绘制点
            canvas.drawCircle(mPointList.get(i).x, mPointList.get(i).y, 10, mPointLinePaint);
        }
        //曲线连接
        if (isRecursion) {
            //递归计算
            buildBezierPoints();
        } else {
            //杨辉三角计算
            calculate();
        }
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击刷新
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            init();
            invalidate();
        }
        return super.onTouchEvent(event);
    }

    public boolean isRecursion() {
        return isRecursion;
    }

    public void setRecursion(boolean recursion) {
        isRecursion = recursion;
        init();
        invalidate();
    }

    /**
     * 递归填充贝塞尔曲线
     */
    private ArrayList<PointF> buildBezierPoints() {
        mPath.reset();
        ArrayList<PointF> pointFList = new ArrayList<>();
        //分成1000等分 分别计算各个点的位置
        float equalDivision = 1.0f / 1000;
        for (float t = 0; t <= 1; t += equalDivision) {
            //分别计算x点和y点 mPointList.size()-1是几介贝塞尔，所有的点-1
            PointF pointF = new PointF(deCastelJau(mPointList.size() - 1, 0, t, true), deCastelJau(mPointList.size() - 1, 0, t, false));
            pointFList.add(pointF);
            //第一个点不用画 直接移到该点
            if (t == 0) {
                mPath.moveTo(pointFList.get(0).x, pointFList.get(0).y);
            } else {
                mPath.lineTo(pointF.x, pointF.y);
            }
        }
        return pointFList;
    }

    //计算贝塞尔曲线的点 i几介贝塞尔 j控制点每次加1 t把2个点连线分成的等份 isX是否是算x点
    private float deCastelJau(int i, int j, float t, boolean isX) {
        //当i不等于0时开始递归调用自己 最后都会走到这个公式里
        //p(i,j) =  (1-t) * p(i-1,j)  +  t * p(i-1,j+1)
        //(1-t)*j.*+t*(j+1).*
        if (i == 1) {
            return isX ? (1 - t) * mPointList.get(j).x + t * mPointList.get(j + 1).x :
                    (1 - t) * mPointList.get(j).y + t * mPointList.get(j + 1).y;
        } else {
            return (1 - t) * deCastelJau(i - 1, j, t, isX) + t * deCastelJau(i - 1, j + 1, t, isX);
        }
    }

    /**
     * 杨辉三角计算曲线点 填充mPath
     */
    private ArrayList<PointF> calculate() {
        mPath.reset();
        //控制点个数(number-1阶)
        int number = mPointList.size();
        //小于2阶省略
        if (number < 2) {
            return null;
        }
        ArrayList<PointF> points = new ArrayList<>();

        //计算杨辉三角
        int[] mi = new int[number];
        mi[0] = mi[1] = 1;//第二层（一阶常数项）
        for (int i = 3; i <= number; i++) {
            //得到上一层的数据
            int[] t = new int[i - 1];
            for (int j = 0; j < t.length; j++) {
                t[j] = mi[j];
            }
            //计算当前行的数据
            mi[0] = mi[i - 1] = 1;
            for (int j = 0; j < i - 2; j++) {
                mi[j + 1] = t[j] + t[j + 1];
            }
        }
        //计算坐标点
        for (int i = 0; i < 1000; i++) {
            float t = (float) i / 1000;
            //分别计算x,y坐标
            //计算各项和(𝑛¦𝑖) 𝑃_𝑖 〖(1−𝑡)〗^(𝑛−i) 𝑡^𝑖
            PointF pointF = new PointF();
            for (int j = 0; j < number; j++) {
                pointF.x += mi[j] * mPointList.get(j).x * Math.pow(1 - t, number - 1 - j) * Math.pow(t, j);
                pointF.y += mi[j] * mPointList.get(j).y * Math.pow(1 - t, number - 1 - j) * Math.pow(t, j);
            }
            points.add(pointF);
            //0 moveTo
            if (i == 0) {
                mPath.moveTo(pointF.x, pointF.y);
            } else {
                mPath.lineTo(pointF.x, pointF.y);
            }
        }
        return points;
    }
}
