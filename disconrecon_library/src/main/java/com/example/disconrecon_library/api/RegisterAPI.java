package com.example.disconrecon_library.api;

import com.example.disconrecon_library.model.DTC;
import com.example.disconrecon_library.model.DisconData;
import com.example.disconrecon_library.model.Feeder;
import com.example.disconrecon_library.model.LoginDetails;
import com.example.disconrecon_library.model.MRcode;
import com.example.disconrecon_library.model.ReconMemo;
import com.example.disconrecon_library.model.Result;
import com.example.disconrecon_library.model.UpdateResult;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterAPI {
    @POST("mr_login")//1
    @FormUrlEncoded
    Call<List<LoginDetails>> getLoginDetails2(@Field("MRCode") String MRCode, @Field("DeviceId") String DeviceId, @Field("PASSWORD") String PASSWORD);

    @POST("DisConList")//2
    @FormUrlEncoded
    Call<List<DisconData>> getDisconList(@Field("MRCode") String MRCode, @Field("Date") String Datel, @Field("PASSWORD") String PASSWORD, @Field("DEVICE_ID") String DEVICE_ID, @Field("AndKey") String AndKey);

    @POST("DisConUpdate")//3
    @FormUrlEncoded
    Call<List<Result>> disconUpdate(@Field("Acc_id") String Acc_id, @Field("Dis_Date") String Dis_Date, @Field("CURREAD") String CURREAD,
                                    @Field("Remarks") String Remarks, @Field("Comment") String Comment, @Field("MRCODE") String MRCODE, @Field("PASSWORD") String PASSWORD, @Field("DEVICE_ID") String DEVICE_ID, @Field("AndKey") String AndKey);

    @POST("ReConList")//4
    @FormUrlEncoded
    Call<List<DisconData>> getReconList(@Field("MRCode") String MRCode, @Field("Date") String Date, @Field("PASSWORD") String PASSWORD, @Field("DEVICE_ID") String DEVICE_ID, @Field("AndKey") String AndKey);

    @POST("ReConUpdate")//5
    @FormUrlEncoded
    Call<List<Result>> reconUpdate(@Field("Acc_id") String Acc_id, @Field("Dis_Date") String Dis_Date, @Field("CURREAD") String CURREAD,
                                   @Field("Remarks") String Remarks, @Field("Comment") String Comment, @Field("MRCODE") String MRCODE, @Field("PASSWORD") String PASSWORD, @Field("DEVICE_ID") String DEVICE_ID, @Field("AndKey") String AndKey);

    @POST("Reconnection")//5
    @FormUrlEncoded
    Call<List<Result>> getReconMessage(@Field("Acc_id") String Acc_id,@Field("MRCODE") String MRCODE, @Field("PASSWORD") String PASSWORD, @Field("DEVICE_ID") String DEVICE_ID, @Field("AndKey") String AndKey);

    @POST("FDR_DETAILS")//6
    @FormUrlEncoded
    Call<List<Feeder>> getFeederDetails(@Field("SUB_DIVCODE") String SUB_DIVCODE, @Field("DATE") String DATE, @Field("AndKey") String AndKey);

    @POST("FDRFR_Update")//7
    @FormUrlEncoded
    Call<List<Result>> feederUpdate(@Field("FDRCODE") String FDRCODE, @Field("DATE") String DATE, @Field("FDRFR") String FDRFR, @Field("Latitude") String Latitude,
                                    @Field("Longitude") String Longitude, @Field("SRTPV") String SRTPV, @Field("Boundary_Export") String Boundary_Export, @Field("AndKey") String AndKey);

    @POST("FDR_FETCH")//8
    @FormUrlEncoded
    Call<List<Feeder>> getFDRNames(@Field("SUBDIV_CODE") String SUBDIV_CODE, @Field("DATE") String DATE, @Field("AndKey") String AndKey);


    @POST("TC_DETAILS")//9
    @FormUrlEncoded
    Call<List<DTC>> getDTCDeatils(@Field("SUBDIVCODE") String SUBDIVCODE, @Field("DATE") String DATE, @Field("FDRCODE") String FDRCODE,@Field("AndKey") String AndKey);

    @POST("TCFR_Update")//10
    @FormUrlEncoded
    Call<List<Result>> dtcUpdate(@Field("TCCODE") String TCCODE, @Field("DATE") String DATE, @Field("TCFR") String TCFR,@Field("AndKey") String AndKey);

    @POST("TC_DETAILS_MR")//11
    @FormUrlEncoded
    Call<List<DTC>> getDTCDeatilsMR(@Field("MRCODE") String MRCODE, @Field("DATE") String DATE,@Field("AndKey") String AndKey);

    @POST("TCFR_Update_MR")//12
    @FormUrlEncoded
    Call<List<Result>> dtcUpdateMR(@Field("MRCODE") String MRCODE, @Field("TCCODE") String TCCODE, @Field("DATE") String DATE,
                                 @Field("TCFR") String TCFR, @Field("Latitude") String Latitude, @Field("Langitude") String Langitude,
                                 @Field("imagename") String imagename, @Field("encodefile") String encodefile,@Field("AndKey") String AndKey);

    @POST("Update_DTC_MR")//13
    @FormUrlEncoded
    Call<List<Result>> dtcMapping(@Field("MRCODE") String MRCODE, @Field("TCCODE") String TCCODE, @Field("READ_DATE") String READ_DATE,@Field("AndKey") String AndKey);

    @POST("MR_FETCH")//14
    @FormUrlEncoded
    Call<List<MRcode>> getMRCode(@Field("SUBDIVCODE") String SUBDIVCODE, @Field("AndKey") String AndKey);

    @POST("ReConMemo")//15
    @FormUrlEncoded
    Call<List<ReconMemo>> getReconaMemoData(@Field("AccountId") String AccountId, @Field("SUBDIVCODE") String SUBDIVCODE, @Field("MRCODE") String MRCODE, @Field("PASSWORD") String PASSWORD, @Field("DEVICE_ID") String DEVICE_ID, @Field("AndKey") String AndKey);

    @POST("ReconMemo_Update")//16
    @FormUrlEncoded
    Call<List<Result>> reconMemoUpdate(@Field("ACCT_ID") String ACCT_ID, @Field("paid_amount") String paid_amount, @Field("rcpt_num") String rcpt_num, @Field("MRCODE") String MRCODE, @Field("PASSWORD") String PASSWORD, @Field("DEVICE_ID") String DEVICE_ID, @Field("AndKey") String AndKey);

    @POST("DisconUpdateResult")//17
    @FormUrlEncoded
    Single<UpdateResult> getDisconResult(@Field("ACCOUNT_ID") String ACCOUNT_ID, @Field("DATE") String DATE, @Field("AndKey") String AndKey);
}
