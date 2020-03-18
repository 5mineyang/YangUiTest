package com.yang.yanguitest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected Activity mActivity;
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
        mActivity = this;
        mContext = getApplicationContext();
        setContentView(getLayoutId());
        setToolTitle(getClass().getSimpleName());
        initView();
        initData();
        initListener();
    }

    protected abstract int getLayoutId();

    protected void initView() {
    }

    protected void initData() {
    }

    protected void initListener() {
        //返回
        ImageView ivToolBack = findViewById(R.id.ivToolBack);
        if (null != ivToolBack) {
            ivToolBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    /**
     * -------------------- Base tool相关设置 start --------------------
     **/
    protected void setBackHidden() {
        ImageView ivToolBack = findViewById(R.id.ivToolBack);
        if (null != ivToolBack) {
            ivToolBack.setVisibility(View.GONE);
        }
    }

    protected void setToolTitle(String title) {
        setToolTitle(title, -1);
    }

    protected void setToolTitle(String title, int color) {
        TextView tvToolTitle = findViewById(R.id.tvToolTitle);
        if (null != tvToolTitle) {
            tvToolTitle.setVisibility(View.VISIBLE);
            tvToolTitle.setText(title);
            if (color != -1) {
                tvToolTitle.setTextColor(getResources().getColor(color));
            }
        }
    }

    protected void setToolRightImageOne(int resourcesId) {
        setToolRightImageOne(getResources().getDrawable(resourcesId));
    }

    protected void setToolRightImageOne(Drawable drawable) {
        ImageView ivToolRightOne = findViewById(R.id.ivToolRightOne);
        if (null != ivToolRightOne) {
            ivToolRightOne.setVisibility(View.VISIBLE);
            ivToolRightOne.setImageDrawable(drawable);
        }
    }

    protected void setToolRightImageTwo(int resourcesId) {
        setToolRightImageTwo(getResources().getDrawable(resourcesId));
    }

    protected void setToolRightImageTwo(Drawable drawable) {
        ImageView ivToolRightTwo = findViewById(R.id.ivToolRightTwo);
        if (null != ivToolRightTwo) {
            ivToolRightTwo.setVisibility(View.VISIBLE);
            ivToolRightTwo.setImageDrawable(drawable);
        }
    }

    protected void setToolRightText(String rightText) {
        setToolRightText(rightText, -1);
    }

    protected void setToolRightText(String rightText, int color) {
        TextView tvToolRight = findViewById(R.id.tvToolRight);
        if (null != tvToolRight) {
            tvToolRight.setVisibility(View.VISIBLE);
            tvToolRight.setText(rightText);
            if (color != -1) {
                tvToolRight.setTextColor(getResources().getColor(color));
            }
        }
    }

    /**
     * -------------------- end --------------------
     **/

    //跳转到相关aty
    protected void intentIntoActivity(int tvMainActivityGallery, final Class clz) {
        findViewById(tvMainActivityGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, clz));
            }
        });
    }

    @Override
    protected void onDestroy() {
        mActivity = null;
        mContext = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.push_right_out);
    }
}
