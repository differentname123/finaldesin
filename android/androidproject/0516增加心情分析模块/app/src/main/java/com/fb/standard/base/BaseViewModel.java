package com.fb.standard.base;

import com.fb.standard.R;

import androidx.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel {
    protected String url = App.getContext().getResources().getString(R.string.url);
    public BaseViewModel() {

    }

}
