package com.kaidoh.mayuukhvarshney.smartprix;

/**
 * Created by mayuukhvarshney on 01/07/16.
 */
public class ProductVariables {

    private  String ID;
    private Integer Price;
    private String Brand;
    private String Category;
    private String ImageUrl;
    private String Name;

    public void setID(String id){
        this.ID=id;
    }
    public void setPrice(Integer prc){
        this.Price=prc;
    }
    public void setBrand(String brand){
        this.Brand=brand;
    }
    public void setCategory(String category){
        this.Category=category;
    }
    public void setItemName(String n){
        this.Name=n;
    }
    public void setImageUrl(String url){
        this.ImageUrl=url;
    }
    public String getID(){
        return this.ID;
    }
    public String getBrand(){
        return this.Brand;
    }
    public String getCategory(){
        return this.Category;
    }
    public String getImageUrl(){
        return this.ImageUrl;
    }
    public Integer getPrice(){
        return this.Price;
    }
    public String getItemName(){
        return this.Name;
    }

}
