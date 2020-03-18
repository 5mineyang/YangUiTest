package com.yang.yanguitest.activity;

import android.view.View;
import android.widget.TextView;

import com.yang.yanguitest.BaseActivity;
import com.yang.yanguitest.R;
import com.yang.yanguitest.view.bezier.BezierView;

/**
 * 贝塞尔曲线
 */
public class BezierActivity extends BaseActivity {
    private TextView tvBezierActivityRecursion;     //递归
    private TextView tvBezierActivityErgodic;       //杨辉三角
    private BezierView bzvBezierActivity;           //贝塞尔曲线view

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bezier;
    }

    @Override
    protected void initView() {
        super.initView();
        tvBezierActivityRecursion = findViewById(R.id.tvBezierActivityRecursion);
        tvBezierActivityErgodic = findViewById(R.id.tvBezierActivityErgodic);
        bzvBezierActivity = findViewById(R.id.bzvBezierActivity);

        tvBezierActivityRecursion.setSelected(bzvBezierActivity.isRecursion());
        tvBezierActivityErgodic.setSelected(!bzvBezierActivity.isRecursion());
    }

    @Override
    protected void initListener() {
        super.initListener();

        //递归
        tvBezierActivityRecursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bzvBezierActivity.setRecursion(true);
                tvBezierActivityRecursion.setSelected(true);
                tvBezierActivityErgodic.setSelected(false);
            }
        });

        //杨辉三角
        tvBezierActivityErgodic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bzvBezierActivity.setRecursion(false);
                tvBezierActivityRecursion.setSelected(false);
                tvBezierActivityErgodic.setSelected(true);
            }
        });
    }
}
