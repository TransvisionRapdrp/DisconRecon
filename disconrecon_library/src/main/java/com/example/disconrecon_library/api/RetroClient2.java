package com.example.disconrecon_library.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.disconrecon_library.SplashActivity.PACKAGE_NAME;
import static com.example.disconrecon_library.values.constant.NONRAPDRP_TEST_APP;
import static com.example.disconrecon_library.values.constant.PROD_APP;
import static com.example.disconrecon_library.values.constant.PROD_URL2;
import static com.example.disconrecon_library.values.constant.RAPDRP_TEST_URL1;
import static com.example.disconrecon_library.values.constant.RAPDRP_TEST_URL2;
import static com.example.disconrecon_library.values.constant.TEST_APP;
import static com.example.disconrecon_library.values.constant.TEST_URL2;


public class RetroClient2 {
    private static String BASE_URL = PROD_URL2;

    public RetroClient2() {
        if (PACKAGE_NAME.equals(PROD_APP)) {
            BASE_URL = PROD_URL2;
        } else if (PACKAGE_NAME.equals(TEST_APP)) {
            BASE_URL = TEST_URL2;
        }else if (PACKAGE_NAME.equals(NONRAPDRP_TEST_APP)){
            BASE_URL = RAPDRP_TEST_URL2;
        }
    }

  /*  private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public RegisterAPI getApiService() {
        return getRetrofitInstance().create(RegisterAPI.class);
    }*/

    private static Retrofit retrofit = null;
    private static int REQUEST_TIMEOUT = 4000;
    private static OkHttpClient okHttpClient;


    public Retrofit getClient() {
        if (okHttpClient == null)
            initOkHttp();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static void initOkHttp() {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(interceptor);

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Request-Type", "Android")
                    .addHeader("Content-Type", "application/json");

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        okHttpClient = httpClient.build();
    }

    public static void resetApiClient() {
        retrofit = null;
        okHttpClient = null;
    }
}
