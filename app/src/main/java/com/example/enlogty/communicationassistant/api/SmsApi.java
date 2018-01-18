package com.example.enlogty.communicationassistant.api;

import com.example.enlogty.communicationassistant.domain.Sms;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by enlogty on 2017/12/11.
 */

public interface SmsApi {
    @GET(value = "cloud/sms/findAll")
    Call<List<Sms>> findAll(@Query(value = "localNumber") String localNumber);
    @POST(value = "cloud/sms/insertSms")
    @FormUrlEncoded
    Call<String> insertSmsToRemoteDB(@Field("localNumber") String localNumber,@Field("address") String address,@Field("date") long date,@Field("type") int type,@Field("body") String body);
}
