package com.example.vivu;

import com.example.vivu.Remote.IGoogleApi;
import com.example.vivu.Remote.RetrofitClient;

public class Common {
    public static final String baseURL= "https://googleapis.com";
    public static IGoogleApi getGoogleApi(){
        return RetrofitClient.getClient(baseURL).create(IGoogleApi.class);
    }
}
