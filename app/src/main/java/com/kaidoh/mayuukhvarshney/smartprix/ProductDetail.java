package com.kaidoh.mayuukhvarshney.smartprix;

/**
 * Created by mayuukhvarshney on 30/06/16.
 */

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
public class ProductDetail {

    @SerializedName("request_status")
    private String Response;

    public String getResponse(){
        return this.Response;
    }

    @SerializedName("request_result")
    private TheResult mResult;

    public TheResult getResult(){
        return this.mResult;
    }



public class TheResult{

    @SerializedName("results")
    private ArrayList<Properties> ProductResults;

    public ArrayList<Properties> getProductResults(){
        return this.ProductResults;
    }

    }
    public class Properties{
        @SerializedName("id")
        private String ID;
        public String getID(){
            return this.ID;
        }
        @SerializedName("category")
        private String Category;
        public String getCategory(){
            return this.Category;
        }

        @SerializedName("name")
        private String ProductName;
        public String getProductName(){
            return this.ProductName;
        }
        @SerializedName("price")
        private Integer ProductPrice;
        public Integer getProductPrice(){
            return this.ProductPrice;
        }
        @SerializedName("brand")
        private String Brand;
        public String getBrand(){
            return this.Brand;
        }
        @SerializedName("img_url")
        private String ImageURL;
        public String getImageURL(){
            return this.ImageURL;
        }

    }
}

