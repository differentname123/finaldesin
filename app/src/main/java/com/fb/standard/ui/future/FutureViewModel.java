package com.fb.standard.ui.future;

import com.fb.standard.base.BaseViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class FutureViewModel extends BaseViewModel {

    private MutableLiveData<String> mText;

    public FutureViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}