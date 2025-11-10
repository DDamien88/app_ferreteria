package com.example.appferreteria.ui.stock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AjusteStockViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AjusteStockViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}