package com.yang.yanguitest.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yang.yanguitest.BaseActivity;
import com.yang.yanguitest.R;
import com.yang.yanguitest.view.recyclerView.MyRecyclerView;

/**
 * 手写RecyclerView
 */
public class RecyclerViewActivity extends BaseActivity {
    MyRecyclerView rvRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recycler_view;
    }

    @Override
    protected void initView() {
        super.initView();
        rvRecyclerView = findViewById(R.id.rvRecyclerView);

        initAdapter();
    }

    private void initAdapter() {
        rvRecyclerView.setAdapter(new MyRecyclerView.Adapter() {
            @Override
            public View onCreateViewHolder(int position, View convertView, ViewGroup parent) {
                convertView = getLayoutInflater().inflate(R.layout.activity_recycler_view_item, parent, false);
                TextView textView = convertView.findViewById(R.id.tvRecyclerViewRv);
                textView.setText("yang" + position);
                return convertView;
            }

            @Override
            public View onBinderViewHolder(int position, View convertView, ViewGroup parent) {
                TextView textView = convertView.findViewById(R.id.tvRecyclerViewRv);
                textView.setText("yang" + position);
                return convertView;
            }

            @Override
            public int getItemViewType(int row) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getCount() {
                return 40;
            }

            @Override
            public int getItemHeight(int position) {
                return 100;
            }
        });
    }
}
