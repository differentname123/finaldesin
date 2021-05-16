package com.fb.standard.ui.cloud;

import com.fb.standard.base.BaseViewModel;

import androidx.lifecycle.MutableLiveData;

public class CloudViewModel extends BaseViewModel {

    private MutableLiveData<Integer> mNumber;

    public CloudViewModel() {
        mNumber = new MutableLiveData<>();
    }


}