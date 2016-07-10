package com.kaidoh.mayuukhvarshney.smartprix;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mayuukhvarshney on 01/07/16.
 */

import java.util.ArrayList;

public class SingleItemDetail {

    @SerializedName("request_status")
    private String Status;
    public String getStatus(){
        return this.Status;
    }
    @SerializedName("request_result")
    private ProductInfo Result;
    public ProductInfo getResult(){
        return this.Result;
    }

    public class ProductInfo{
        @SerializedName("id")
        private String ID;
        public String getTheID(){
            return this.ID;
        }
        @SerializedName("category")
        private String Category;
        public String getTheCategory(){
            return this.Category;
        }
        @SerializedName("name")
        private String TheName;

        public String getTheName() {
            return this.TheName;
        }
        @SerializedName("price")

        private String ThePrice;

        public String getThePrice() {
            return ThePrice;
        }
        @SerializedName("brand")

        public String getTheBrand;

        public String getGetTheBrand() {
            return getTheBrand;
        }
        @SerializedName("product_url")
        private String ProductUrl;

        public String getProductUrl() {
            return ProductUrl;
        }
        @SerializedName("img_url")
        private String ImageUrl;

        public String getImageUrl() {
            return ImageUrl;
        }

        @SerializedName("prices")
        private ArrayList<PriceDetail> Prices;
        public ArrayList<PriceDetail> getPrices(){
            return this.Prices;
        }



    }
    public class PriceDetail{
        @SerializedName("store_name")
        private String StoreName;
        public String getStoreName(){
            return this.StoreName;
        }
        @SerializedName("store_url")
        private String StoreUrl;
        public String getStoreUrl(){
            return this.StoreUrl;
        }
        @SerializedName("link")
        private String BuyLink;

        public String getBuyLink() {
            return this.BuyLink;
        }
        @SerializedName("price")
        private String StorePrice;

        public String getStorePrice() {
            return this.StorePrice;
        }
        @SerializedName("logo")
        private String StoreLogo;

        public String getStoreLogo() {
            return this.StoreLogo;
        }
    }
}
