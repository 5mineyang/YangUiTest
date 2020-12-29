package com.yang.yanguitest;

import com.yang.yanguitest.activity.BehaviorActivity;
import com.yang.yanguitest.activity.BezierActivity;
import com.yang.yanguitest.activity.DragBubbleActivity;
import com.yang.yanguitest.activity.GalleryScrollActivity;
import com.yang.yanguitest.activity.PathMeasureActivity;
import com.yang.yanguitest.activity.RecyclerViewActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        setBackHidden();
        setToolTitle("YangUiTest");
    }

    @Override
    protected void initListener() {
        //横向滑动 中间view图片替换
        intentIntoActivity(R.id.tvMainActivityGallery,GalleryScrollActivity.class);
        //点击几介贝塞尔
        intentIntoActivity(R.id.tvMainActivityBezier,BezierActivity.class);
        //QQ气泡效果
        intentIntoActivity(R.id.tvMainActivityDrag, DragBubbleActivity.class);
        //用PathMeasure搞一些动画
        intentIntoActivity(R.id.tvMainActivityPath, PathMeasureActivity.class);
        //自定义LinerLayout实现Behavior实现嵌套滑动
        intentIntoActivity(R.id.tvMainActivityBehavior, BehaviorActivity.class);
        //手写RecyclerView
        intentIntoActivity(R.id.tvMainActivityRecyclerView, RecyclerViewActivity.class);
    }
}
