package com.yang.yanguitest.activity;

import android.view.View;

import com.yang.yanguitest.BaseActivity;
import com.yang.yanguitest.R;
import com.yang.yanguitest.view.pathMeasure.FaceLoadingView;

/**
 * 用PathMeasure搞一些动画
 */
public class PathMeasureActivity extends BaseActivity {
    private FaceLoadingView flvPathMeasureActivity;
    private int progress = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_path_measure;
    }

    @Override
    protected void initView() {
        super.initView();

        flvPathMeasureActivity = findViewById(R.id.flvPathMeasureActivity);
    }

    @Override
    protected void initListener() {
        super.initListener();

        flvPathMeasureActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progress > 100){
                    progress = 0;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progress <= 100) {
                            progress += 1;
                            flvPathMeasureActivity.setProgress(progress);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }
}
