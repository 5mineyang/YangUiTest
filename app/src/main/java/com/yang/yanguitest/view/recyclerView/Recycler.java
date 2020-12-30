package com.yang.yanguitest.view.recyclerView;

import android.view.View;

import java.util.Stack;

//存放RecyclerView的栈容器
public class Recycler {
    private Stack<View>[] views;

    public Recycler(int viewTypeCount) {
        //根据viewTypeCount生成几个二维数组
        views = new Stack[viewTypeCount];
        //里面二维Stack都实例化
        for (int i = 0; i < viewTypeCount; i++) {
            views[i] = new Stack<>();
        }
    }

    //添加item 根据viewType
    public void put(View item, int viewType) {
        views[viewType].push(item);
    }

    //获取item
    public View get(int viewType) {
        try {
            return views[viewType].pop();
        } catch (Exception e) {
            return null;
        }
    }
}
