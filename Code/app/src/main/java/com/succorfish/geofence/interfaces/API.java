package com.succorfish.geofence.interfaces;

import com.succorfish.geofence.customObjects.LoginData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface API {
    @GET("device/get-token/{id}")    //asset/getForAccountWithoutDevice/{id}    // device/get-token/{id}
    Call<String> getDeviceToken(@Path("id") String id);
    @GET("user/getOwn")
    Call<LoginData> userLoginAPI();
}
