package com.kaidoh.mayuukhvarshney.smartprix;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;
/**
 * Created by mayuukhvarshney on 30/06/16.
 */
public interface SmartService {
    @GET("/simple/v1?type=search&key=" + Config.CLIENT_KEY)
    void getSearchResults(@QueryMap Map<String,String> x, Callback<ProductDetail> cb);

    @GET("/simple/v1?type=product_full&key="+Config.CLIENT_KEY)
    void getProductPrices(@QueryMap Map<String,String> y,Callback<SingleItemDetail> cb);

}
