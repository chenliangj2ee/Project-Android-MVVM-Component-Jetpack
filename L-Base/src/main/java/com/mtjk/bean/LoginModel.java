package com.mtjk.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;


public class LoginModel extends BaseObservable {
    public ObservableField<String> phoneNumber = new ObservableField<>();
    public ObservableField<String> code = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<Boolean> isPass = new ObservableField<>(true);


}
