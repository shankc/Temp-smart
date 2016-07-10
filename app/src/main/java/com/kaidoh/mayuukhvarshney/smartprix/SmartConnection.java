package com.kaidoh.mayuukhvarshney.smartprix;

/**
 * Created by mayuukhvarshney on 30/06/16.
 */

import retrofit.RestAdapter;

public class SmartConnection {
    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder().setEndpoint(Config.API_URL).build();
    private static final SmartService SERVICE = REST_ADAPTER.create(SmartService.class);

    public static SmartService getService() {
        return SERVICE;
    }
}
