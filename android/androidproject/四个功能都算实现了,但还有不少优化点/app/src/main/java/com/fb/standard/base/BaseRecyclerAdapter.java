package com.fb.standard.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

public class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.InnerHolder>{

    protected BaseItem baseItem;
    protected LayoutInflater mInflater;

    public BaseRecyclerAdapter(Context context,BaseItem baseItem) {
        this.baseItem = baseItem;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(baseItem.getResId(),parent,false);
        //View view = mInflater.inflate(baseItem.getResId(),null);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        baseItem.initData(holder.itemView,position);
    }

    @Override
    public int getItemCount() {
        return baseItem.getCount();
    }

    class InnerHolder extends RecyclerView.ViewHolder{

        public InnerHolder(View view) {
            super(view);
        }
    }
}
