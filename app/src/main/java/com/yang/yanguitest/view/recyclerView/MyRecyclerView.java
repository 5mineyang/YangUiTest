package com.yang.yanguitest.view.recyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.yang.yanguitest.R;

import java.util.ArrayList;
import java.util.List;

//手写RecyclerView
public class MyRecyclerView extends ViewGroup {
    private Adapter adapter;
    private Recycler recycler;      //item回收池
    private List<View> itemList;    //当前屏幕里的item
    private int[] heights;          //各个item的高
    private int width, height;      //当前宽高
    private int currentPosition;    //当前第一个item的位置
    private float currentY;         //手指按下的位置
    private int scrollY;            //y偏移量
    private boolean needRelayout;   //是否需要重新布局
    private int touchSlop;          //最小滑动距离
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int y = msg.arg2 - msg.arg1 > 8 ? 8 : msg.arg2 - msg.arg1;
            scrollBy(0, y);
            if (msg.arg2 - msg.arg1 > 8) {
                Message message = new Message();
                message.arg1 = msg.arg1 + 8;
                message.arg2 = msg.arg2;
                handler.sendMessageDelayed(message, 1);
            } else {
                handler.removeCallbacksAndMessages(null);
            }
        }
    };

    public MyRecyclerView(Context context) {
        this(context, null);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        init(context);
    }

    //初始化
    private void init(Context context) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
        itemList = new ArrayList<>();
        needRelayout = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //高度获取到了之后再计算
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (null != adapter) {
            heights = new int[adapter.getCount()];
            for (int i = 0; i < heights.length; i++) {
                heights[i] = adapter.getItemHeight(i);
            }
            //获取所有item的高度
            int allItemHeight = sumArray(0, heights.length);
            //设置高度
            setMeasuredDimension(widthSize, Math.min(heightSize, allItemHeight));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (needRelayout || changed) {
            needRelayout = false;
            //清掉当前所有view
            removeAllViews();
            itemList.clear();
            //记录宽高
            width = r - l;
            height = b - t;
            int left = 0, top = 0, right = width, bottom;
            if (null != adapter) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    //计算一下 只添加当前屏幕可以显示的item
                    if (sumArray(currentPosition, i) < height) {
                        bottom = top + heights[i];
                        //生成view
                        View view = makeAndStep(i, left, top, right, bottom);
                        top = bottom;
                        //添加到list里
                        itemList.add(view);
                    }
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录手指第一次按下位置
                currentY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = Math.abs(currentY - ev.getRawY());
                //滑动距离超过最小滑动距离了
                if (moveY > touchSlop) {
                    intercept = true;
                }
                break;
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float moveY = currentY - event.getRawY();
                //便于更快反应手指上下移动
                currentY = event.getRawY();
                //滑动
                scrollBy(0, (int) moveY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //松开手回弹动画
                if (currentPosition <= 0 && scrollY < 0) {
                    startAnim(scrollY);
                } else {
                    Log.i("yang5", "scrollY底部:" + scrollY);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void scrollBy(int x, int y) {
        changeView(y);
        super.scrollBy(x, y);
    }

    //改变view
    private void changeView(int y) {
        scrollY += y;
        //上滑
        if (scrollY > 0) {
            //移除最上面item
            if (heights.length - currentPosition > itemList.size() && scrollY > heights[currentPosition]) {
                removeView(itemList.remove(0));
                scrollY -= heights[currentPosition];
                currentPosition++;
            }
            //添加最下面item
            while (currentPosition + itemList.size() < heights.length && getFillHeight() < height) {
                //要添加item的位置
                int addPosition = currentPosition + itemList.size();
                //生成view
                View view = makeAndStep(addPosition, 0, sumArray(0, addPosition), width, sumArray(0, addPosition + 1));
                itemList.add(view);
            }
        } else if (scrollY < 0) {   //下滑
            if (getFillHeight() + scrollY > height) {
                //移除最下面item
                if (getFillHeight() + scrollY - heights[itemList.size() - 1] > height) {
                    removeView(itemList.remove(itemList.size() - 1));
                }
                //添加最上面item
                while (currentPosition > 0) {
                    int addPosition = currentPosition - 1;
                    View view = makeAndStep(addPosition, 0, sumArray(0, addPosition), width, sumArray(0, addPosition + 1), true);
                    itemList.add(0, view);
                    scrollY += heights[addPosition];
                    currentPosition--;
                }
            }
        }
    }

    //制造view
    private View makeAndStep(int position, int left, int top, int right, int bottom) {
        return makeAndStep(position, left, top, right, bottom, false);
    }

    //制造view
    private View makeAndStep(int position, int left, int top, int right, int bottom, boolean first) {
        //生成view
        View view = obtainView(position, right - left, bottom - top, first);
        view.layout(left, top, right, bottom);
        return view;
    }

    //生成view
    private View obtainView(int position, int width, int height, boolean first) {
        //获取viewType
        int viewType = adapter.getItemViewType(position);
        //根据viewType 从栈中拿view
        View view = recycler.get(viewType);
        View itemView;
        if (null == view) {
            itemView = adapter.onCreateViewHolder(position, null, this);
            if (itemView == null) {
                throw new RuntimeException("onCreateViewHolder没有填充布局");
            }
        } else {
            itemView = adapter.onBinderViewHolder(position, view, this);
        }
        //把viewType存进tag
        itemView.setTag(R.id.tag_type_view, viewType);
        //测量添加itemView
        itemView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        if (first) {
            addView(itemView, 0);
        } else {
            addView(itemView);
        }
        return itemView;
    }

    //获取到显示在控件中的item总高度
    private int getFillHeight() {
        return sumArray(currentPosition, itemList.size()) - scrollY;
    }

    //获取指定位置开始 多少个item的高度
    private int sumArray(int startPosition, int count) {
        int sum = 0;
        for (int i = startPosition; i < startPosition + count; i++) {
            sum += heights[i];
        }
        return sum;
    }

    //执行动画
    private void startAnim(int scrollY) {
        Message message = new Message();
        message.arg1 = 0;
        message.arg2 = Math.abs(scrollY);
        handler.sendMessage(message);
    }

    /**
     * 获取适配器
     */
    public Adapter getAdapter() {
        return adapter;
    }

    /**
     * 设置适配器
     */
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        if (null != adapter) {
            recycler = new Recycler(adapter.getViewTypeCount());
            currentPosition = 0;
            scrollY = 0;
            needRelayout = true;
            //重新测量布局
            requestLayout();
        }
    }

    /**
     * 适配器
     */
    public interface Adapter {
        //创建ViewHolder
        View onCreateViewHolder(int position, View convertView, ViewGroup parent);

        //绑定ViewHolder
        View onBinderViewHolder(int position, View convertView, ViewGroup parent);

        //获取某item的类型
        int getItemViewType(int position);

        //获取item的类型总数
        int getViewTypeCount();

        //获取item总数
        int getCount();

        //获取某item的高度
        int getItemHeight(int position);
    }
}
