package com.fb.standard.ui.future;

import android.view.View;

import com.fb.standard.R;
import com.fb.standard.base.BaseFragment;
import com.fb.standard.databinding.FragmentFutureBinding;


public class FutureFragment extends BaseFragment<FragmentFutureBinding, FutureViewModel> {

    @Override
    protected void initData() {
//        binding.imageWithText.setTitle("Title");
//        binding.imageWithText.setContent("Content");
        binding.imageWithText.setImageResource(R.drawable.image);
        binding.imageWithText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast("点击");
            }
        });
    }

    @Override
    protected void initView(View root) {

    }

    @Override
    protected int getResId() {
        return R.layout.fragment_future;
    }

    @Override
    protected String setTitle() {
        return getResources().getString(R.string.title_future);
    }

}