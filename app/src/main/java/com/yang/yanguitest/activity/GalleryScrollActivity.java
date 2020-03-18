package com.yang.yanguitest.activity;

import android.graphics.drawable.Drawable;

import com.yang.yanguitest.BaseActivity;
import com.yang.yanguitest.R;
import com.yang.yanguitest.view.gallery.GalleryScrollView;
import com.yang.yanguitest.view.gallery.RevealDrawable;

/**
 * 横向滑动改变中间选中图片
 */
public class GalleryScrollActivity extends BaseActivity {
    private GalleryScrollView gsvGalleryScrollActivity;
    private int[][] drawableIds;
    private Drawable[] drawables;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gallery_scroll;
    }

    @Override
    protected void initView() {
        gsvGalleryScrollActivity = findViewById(R.id.gsvGalleryScrollActivity);
    }

    @Override
    protected void initData() {
        drawableIds = new int[][]{{R.drawable.avft, R.drawable.avft_active},
                {R.drawable.box_stack, R.drawable.box_stack_active},
                {R.drawable.bubble_frame, R.drawable.bubble_frame_active},
                {R.drawable.bubbles, R.drawable.bubbles_active},
                {R.drawable.bullseye, R.drawable.bullseye_active},
                {R.drawable.circle_filled, R.drawable.circle_filled_active},
                {R.drawable.circle_outline, R.drawable.circle_outline_active}};
        drawables = new Drawable[drawableIds.length];
        for (int i = 0; i < drawableIds.length; i++) {
            drawables[i] = new RevealDrawable(getResources().getDrawable(drawableIds[i][0]),
                    getResources().getDrawable(drawableIds[i][1]));
        }
        gsvGalleryScrollActivity.addImageViews(drawables);
    }
}
