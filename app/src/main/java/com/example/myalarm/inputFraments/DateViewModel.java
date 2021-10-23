package com.example.myalarm.inputFraments;

import android.content.ClipData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DateViewModel extends ViewModel {
    private MutableLiveData<String>selectedItem = new MutableLiveData<>();
    public void selectItem(String item){
        selectedItem.setValue(item);
    }
    public LiveData<String> getSelectedItem(){
        return selectedItem;
    }
}
