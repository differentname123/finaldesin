package com.fb.standard.base;

import android.view.View;

import com.fb.standard.listener.OnItemSelectedListener;

import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseItem<T,V extends ViewDataBinding> {
    protected List<T> dates;
    protected V binding;
    protected OnItemSelectedListener listener;

    public BaseItem(List<T> dates) {
        this.dates = dates;
    }

    public OnItemSelectedListener getListener() {
        return listener;
    }

    public void setListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public int getCount(){
        return dates.size();
    }

    protected abstract int getResId();
    public abstract void initData(View view, int position);
}
