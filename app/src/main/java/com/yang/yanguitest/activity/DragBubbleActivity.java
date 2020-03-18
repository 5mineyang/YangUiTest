package com.yang.yanguitest.activity;

import android.view.View;
import android.widget.TextView;

import com.yang.yanguitest.BaseActivity;
import com.yang.yanguitest.R;
import com.yang.yanguitest.view.bezier.DragBubbleView;

import java.util.Random;

/**
 * QQ气泡效果
 */
public class DragBubbleActivity extends BaseActivity {
    private DragBubbleView dbvDragBubbleActivity;
    private TextView tvDragBubbleActivityReset;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_drag_bubble;
    }

    @Override
    protected void initView() {
        super.initView();
        dbvDragBubbleActivity = findViewById(R.id.dbvDragBubbleActivity);
        tvDragBubbleActivityReset = findViewById(R.id.tvDragBubbleActivityReset);
    }

    @Override
    protected void initListener() {
        super.initListener();

        //重置
        tvDragBubbleActivityReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                dbvDragBubbleActivity.setShowLocation(random.nextInt(1080), random.nextInt(1920));
            }
        });
    }
}
