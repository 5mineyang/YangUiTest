package com.yang.yanguitest.view.bezier;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.yang.yanguitest.R;

/**
 * QQ气泡效果
 */
public class DragBubbleView extends View {
    private final float MOVE_OFFSET;                    //手指触摸偏移量
    private final int BUBBLE_STATE_DEFAULT = 0;         //气泡默认状态--静止
    private final int BUBBLE_STATE_CONNECT = 1;         //气泡相连
    private final int BUBBLE_STATE_APART = 2;           //气泡分离
    private final int BUBBLE_STATE_DISMISS = 3;         //气泡消失
    private int mBubbleState = BUBBLE_STATE_DEFAULT;    //气泡状态 默认静止

    private float mBubbleStillRadius;                   //不动气泡的半径 随着可动气泡拖动，半径随之变小 所以单独分开
    private PointF mBubbleStillCenter;                  //不动气泡的圆心
    private PointF mBubbleMoveCenter;                   //可动气泡的圆心
    private float mDist;                                //两气泡圆心距离
    private float mMaxDist;                             //两气泡间可拖动最大圆心距离

    private Rect mTextRect;                             //文本绘制区域
    private Rect mBurstRect;                            //爆炸绘制区域
    private Path mBezierPath;                           //贝塞尔曲线path
    private Paint mBubblePaint;                         //气泡的画笔
    private Paint mTextPaint;                           //气泡文字画笔
    private Paint mBurstPaint;                          //爆炸bitmap画笔

    private Bitmap[] mBurstBitmaps;                     //气泡爆炸的bitmap数组
    private boolean mIsBurstAnimStart = false;          //是否在执行气泡爆炸动画
    private int mBurstDrawableIndex;                    //当前气泡爆炸图片下标
    private int[] mBurstDrawables = {R.drawable.burst_1, R.drawable.burst_2,
            R.drawable.burst_3, R.drawable.burst_4, R.drawable.burst_5};        //气泡爆炸的图片数组
    /**
     * attrs一些属性
     */
    private float mBubbleRadius;                    //气泡半径
    private int mBubbleColor;                       //气泡颜色
    private String mBubbleText;                     //气泡文字
    private int mBubbleTextColor;                   //气泡文字颜色
    private float mBubbleTextSize;                  //气泡文字大小

    private OnDragBubbleListener mOnDragBubbleListener;

    public DragBubbleView(Context context) {
        this(context, null);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取 XML layout中的属性值
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DragBubbleView, defStyleAttr, 0);
        mBubbleRadius = array.getDimension(R.styleable.DragBubbleView_bubble_radius, 30);
        mBubbleColor = array.getColor(R.styleable.DragBubbleView_bubble_color, Color.RED);
        mBubbleText = array.getString(R.styleable.DragBubbleView_bubble_text);
        mBubbleTextSize = array.getDimension(R.styleable.DragBubbleView_bubble_text_size, 36);
        mBubbleTextColor = array.getColor(R.styleable.DragBubbleView_bubble_text_color, Color.WHITE);
        //回收TypedArray
        array.recycle();

        mBubbleStillRadius = mBubbleRadius;
        //最大拖移量 4个气泡直径
        mMaxDist = 8 * mBubbleRadius;
        //拖动的偏移量 一个气泡直径
        MOVE_OFFSET = mBubbleRadius * 2;
        init();
    }

    //初始化参数
    private void init() {
        mTextRect = new Rect();
        mBurstRect = new Rect();
        mBezierPath = new Path();
        //气泡画笔
        mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBubblePaint.setColor(mBubbleColor);
        mBubblePaint.setStyle(Paint.Style.FILL);
        //文本画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mBubbleTextColor);
        mTextPaint.setTextSize(mBubbleTextSize);
        //爆炸画笔
        mBurstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBurstPaint.setFilterBitmap(true);
        //填充爆炸bitmap
        mBurstBitmaps = new Bitmap[mBurstDrawables.length];
        for (int i = 0; i < mBurstDrawables.length; i++) {
            //将气泡爆炸的drawable转为bitmap
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mBurstDrawables[i]);
            mBurstBitmaps[i] = bitmap;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initView(w / 2, h / 2);
    }

    //初始化气泡位置
    private void initView(int w, int h) {
        //设置两气泡圆心初始坐标
        if (mBubbleStillCenter == null) {
            mBubbleStillCenter = new PointF(w, h);
        } else {
            mBubbleStillCenter.set(w, h);
        }

        if (mBubbleMoveCenter == null) {
            mBubbleMoveCenter = new PointF(w, h);
        } else {
            mBubbleMoveCenter.set(w, h);
        }
        mBubbleState = BUBBLE_STATE_DEFAULT;
    }

    /**
     * 设置内容
     */
    public void setBubbleText(String text) {
        mBubbleText = text;
    }

    /**
     * 设置红点出现位置
     */
    public void setShowLocation(int w, int h) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initView(w, h);
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null == mBubbleStillCenter || null == mBubbleMoveCenter) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //气泡没有消失
                if (mBubbleState != BUBBLE_STATE_DISMISS) {
                    //按照点击的坐标 算出与静态气泡中心点的距离
                    mDist = (float) Math.hypot(event.getX() - mBubbleStillCenter.x, event.getY() - mBubbleStillCenter.y);
                    //点击在静态气泡附近，才可以往下走
                    //+ MOVE_OFFSET相当于扩大触发范围
                    if (mDist < mBubbleRadius + MOVE_OFFSET) {
                        mBubbleState = BUBBLE_STATE_CONNECT;
                        return true;
                    } else {
                        mBubbleState = BUBBLE_STATE_DEFAULT;
                        return false;
                    }
                } else {
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                //不是静止和消失状态 处理移动事件
                if (mBubbleState != BUBBLE_STATE_DEFAULT && mBubbleState != BUBBLE_STATE_DISMISS) {
                    mBubbleMoveCenter.x = event.getX();
                    mBubbleMoveCenter.y = event.getY();
                    //按照坐标算出与静态气泡中心点的距离
                    mDist = (float) Math.hypot(event.getX() - mBubbleStillCenter.x, event.getY() - mBubbleStillCenter.y);
                    //拖动时 如果拖动距离不超过指定值 就让静态气泡变小
                    //- MOVE_OFFSET是为了让不动气泡半径到一个较小值时就直接消失
                    if (mDist < mMaxDist - MOVE_OFFSET) {
                        mBubbleStillRadius = mBubbleRadius - mDist / 8;
                    } else {  //超过了就把状态设置成分离状态 在onDraw()方法里就不会绘制中间相连部分了
                        mBubbleState = BUBBLE_STATE_APART;
                    }
                    invalidate();
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //松开后移动气泡位置还处于连接状态 让气泡回到原来位置
                if (mBubbleState == BUBBLE_STATE_CONNECT) {
                    startBubbleRestAnim();
                } else if (mBubbleState == BUBBLE_STATE_APART) {    //处于分离状态
                    //气泡被拉至分离状态了，但是最后移动气泡的坐标还在静态气泡的周围 也让气泡回到原来位置
                    if (mDist < mBubbleRadius * 2) {
                        startBubbleRestAnim();
                    } else {  //执行爆炸动画
                        startBubbleBurstAnim();
                    }
                }
                return false;
            default:
                return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mBubbleStillCenter || null == mBubbleMoveCenter) {
            return;
        }
        /**
         * 先画静止气泡和移动气泡中间相连的部分 后画移动气泡和文字 否则文字会被中间部分覆盖
         */
        if (mBubbleState == BUBBLE_STATE_CONNECT) {
            //画静止气泡
            canvas.drawCircle(mBubbleStillCenter.x, mBubbleStillCenter.y, mBubbleStillRadius, mBubblePaint);
            //画相连曲线
            //计算控制点坐标，两个圆心的中点
            int iAnchorX = (int) ((mBubbleStillCenter.x + mBubbleMoveCenter.x) / 2);
            int iAnchorY = (int) ((mBubbleStillCenter.y + mBubbleMoveCenter.y) / 2);

            float cosTheta = (mBubbleMoveCenter.x - mBubbleStillCenter.x) / mDist;
            float sinTheta = (mBubbleMoveCenter.y - mBubbleStillCenter.y) / mDist;

            /**
             *                                               _.(B)  _
             *                                            -            - _
             *                                          -                  -
             *                                        -                      -
             *                                       -                        -
             *                                     --                          -
             *                                   - -              .             -
             *                                 -   -                            -
             *                               -      -                          -
             *                            _-         -                        -
             *                         _-             -                      -
             *                      _-                   -                 -
             *                  _-        .            _  _-  _  _    .(C)
             *             _-                    _-
             *      _.(A) _                  _-
             *    -         -            _-
             *   -            -       _-
             *  -      .       -    -
             *  -              - _-
             *   -            -
             *     - _ _  .(D)
             */
            //A
            float iBubStillStartX = mBubbleStillCenter.x + mBubbleStillRadius * sinTheta;
            float iBubStillStartY = mBubbleStillCenter.y - mBubbleStillRadius * cosTheta;
            //D
            float iBubStillEndX = mBubbleStillCenter.x - mBubbleStillRadius * sinTheta;
            float iBubStillEndY = mBubbleStillCenter.y + mBubbleStillRadius * cosTheta;
            //B
            float iBubMoveAbleStartX = mBubbleMoveCenter.x + mBubbleRadius * sinTheta;
            float iBubMoveAbleStartY = mBubbleMoveCenter.y - mBubbleRadius * cosTheta;
            //C
            float iBubMoveAbleEndX = mBubbleMoveCenter.x - mBubbleRadius * sinTheta;
            float iBubMoveAbleEndY = mBubbleMoveCenter.y + mBubbleRadius * cosTheta;

            mBezierPath.reset();//清除Path中的内容, reset不保留内部数据结构(重置路径)
            //画上半弧
            //将路径的绘制位置定在（x,y）的位置
            mBezierPath.moveTo(iBubStillEndX, iBubStillEndY);
            //二阶贝塞尔曲线
            mBezierPath.quadTo(iAnchorX, iAnchorY, iBubMoveAbleEndX, iBubMoveAbleEndY);
            //画下半弧
            //结束点或者下一次绘制直线路径的开始点
            mBezierPath.lineTo(iBubMoveAbleStartX, iBubMoveAbleStartY);
            //二阶贝塞尔曲线
            mBezierPath.quadTo(iAnchorX, iAnchorY, iBubStillStartX, iBubStillStartY);
            //连接第一个点连接到最后一个点，形成一个闭合区域
            mBezierPath.close();
            canvas.drawPath(mBezierPath, mBubblePaint);
        }
        /**
         * 只要气泡状态不是消失状态 就都要绘制拖动的气泡和文字
         */
        if (mBubbleState != BUBBLE_STATE_DISMISS) {
            //绘制气泡
            canvas.drawCircle(mBubbleMoveCenter.x, mBubbleMoveCenter.y, mBubbleRadius, mBubblePaint);
            //绘制文字
            mTextPaint.getTextBounds(mBubbleText, 0, mBubbleText.length(), mTextRect);
            canvas.drawText(mBubbleText, mBubbleMoveCenter.x - mTextRect.width() / 2,
                    mBubbleMoveCenter.y + mTextRect.height() / 2, mTextPaint);
        }
        /**
         * 绘制松开后的爆炸bitmap
         */
        if (mIsBurstAnimStart) {
            mBurstRect.set((int) (mBubbleMoveCenter.x - mBubbleRadius), (int) (mBubbleMoveCenter.y - mBubbleRadius),
                    (int) (mBubbleMoveCenter.x + mBubbleRadius), (int) (mBubbleMoveCenter.y + mBubbleRadius));
            canvas.drawBitmap(mBurstBitmaps[mBurstDrawableIndex], null, mBurstRect, mBurstPaint);
        }
    }

    //气泡恢复动画
    private void startBubbleRestAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //执行属性动画
            ValueAnimator valueAnimator = ValueAnimator.ofObject(new PointFEvaluator(),
                    new PointF(mBubbleMoveCenter.x, mBubbleMoveCenter.y),
                    new PointF(mBubbleStillCenter.x, mBubbleStillCenter.y))
                    .setDuration(200);
            valueAnimator.setInterpolator(new OvershootInterpolator(5f));
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //获取属性动画里的值 设置移动气泡的中心 在刷新view
                    mBubbleMoveCenter = (PointF) animation.getAnimatedValue();
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBubbleState = BUBBLE_STATE_DEFAULT;
                    if (null != mOnDragBubbleListener) {
                        mOnDragBubbleListener.onBubbleRest();
                    }
                }
            });
            valueAnimator.start();
        }
    }

    //气泡爆炸动画
    private void startBubbleBurstAnim() {
        //气泡改为消失状态
        mBubbleState = BUBBLE_STATE_DISMISS;
        //开始执行动画
        mIsBurstAnimStart = true;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mBurstBitmaps.length)
                .setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取属性动画里的值 设置当前绘制的爆炸图片下标 刷新view
                mBurstDrawableIndex = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsBurstAnimStart = false;
                if (null != mOnDragBubbleListener) {
                    mOnDragBubbleListener.onBubbleBurst();
                }
            }
        });
        valueAnimator.start();
    }

    public void setOnDragBubbleListener(OnDragBubbleListener onDragBubbleListener) {
        this.mOnDragBubbleListener = onDragBubbleListener;
    }

    public interface OnDragBubbleListener {
        //恢复
        void onBubbleRest();

        //爆炸
        void onBubbleBurst();
    }
}
