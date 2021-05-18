package com.fb.standard.ui.mood;

import com.fb.standard.base.BaseViewModel;

import androidx.lifecycle.MutableLiveData;

public class MoodViewModel extends BaseViewModel {

    private MutableLiveData<Integer> mNumber;

    public MoodViewModel() {
        mNumber = new MutableLiveData<>();
    }


}