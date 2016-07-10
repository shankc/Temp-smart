package com.kaidoh.mayuukhvarshney.smartprix;

/**
 * Created by mayuukhvarshney on 30/06/16.
 */

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductListActivity extends AppCompatActivity {

    String ProductCategory="";
    SmartService connection;
    HashMap<String,String> theProducts;
    ArrayList<ProductVariables>ItemList;
    ArrayList<String> theIds;

    MyAdapter mAdapter;
    ProgressWheel prog;
    RecyclerView theContentList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_list);
        if(savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras==null)
            {
                ProductCategory="";
            }
            else
            {
                ProductCategory=extras.getString("Product");
            }
        }
        else
        {
            ProductCategory=(String)savedInstanceState.getSerializable("Product");
        }
 prog = (ProgressWheel) findViewById(R.id.progress_wheel);
        theContentList = (RecyclerView) findViewById(R.id.products_list);
        connection = SmartConnection.getService();
        theProducts= new HashMap<>();
        ItemList= new ArrayList<>();
        theIds = new ArrayList<>();
        //theProducts.put("category",ProductCategory);
        Log.d("productactivity","the recived msg "+ProductCategory);
        prog.spin();
        theContentList.setVisibility(View.INVISIBLE);
 new GetProductList(){

 }.execute();
    }
    private void setupRecyclerView(String result,@NonNull RecyclerView recyclerView) {
        mAdapter= new MyAdapter(result,ItemList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<ProductVariables> theNames;

     String Result;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mName,thePrice;
            public ImageView ProductImage;

            public View mView;
            public MyViewHolder(View view) {
                super(view);
                mName=(TextView)view.findViewById(R.id.product_title);
                ProductImage= (ImageView) view.findViewById(R.id.product_image);
                thePrice = (TextView) view.findViewById(R.id.product_price);
                mView = view;


            }
        }


        public MyAdapter(String result,ArrayList<ProductVariables> names) {
            this.theNames = names;
            this.Result = result;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.products_list_item, parent, false);

            return new MyViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            //holder.mName.setText(theNames.get(position).ger);
            if(Result.equals("SUCCESS")){
                holder.mName.setText(theNames.get(position).getItemName());
                Picasso.with(ProductListActivity.this).load(theNames.get(position).getImageUrl()).into(holder.ProductImage);
                String DisplayPrice = "Price: "+"Rs. "+String.valueOf(theNames.get(position).getPrice());
                holder.thePrice.setText(DisplayPrice);
            }
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 Intent intent = new Intent(ProductListActivity.this,ItemDetailActivity.class);
                    intent.putExtra("ID",theNames.get(position).getID());
                    intent.putExtra("Image",theNames.get(position).getImageUrl());
                    intent.putExtra("Name",theNames.get(position).getItemName());
                    startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return theNames.size();
        }
    }

    class GetProductList extends AsyncTask<Void,Void,Void> {

        SmartService greenlit;
        HashMap<String, String> AvailPrices = new HashMap<>();

        @Override
        protected Void doInBackground(Void... params) {

            greenlit = SmartConnection.getService();
            for(int i=0;i<100;i+=10)
            {
                theProducts = new HashMap<>();
                theProducts.put("category",ProductCategory);
                theProducts.put("start",String.valueOf(i));


                greenlit.getSearchResults(theProducts, new Callback<ProductDetail>() {
                    @Override
                    public void success(ProductDetail productDetail, Response response) {
                        if(productDetail.getResponse().equals("SUCCESS"))
                        {
                            int ListSize = productDetail.getResult().getProductResults().size();
                           for(int i=0;i<ListSize;i++)
                           {
                               ProductVariables var = new ProductVariables();
                               var.setID(productDetail.getResult().getProductResults().get(i).getID());
                               var.setImageUrl(productDetail.getResult().getProductResults().get(i).getImageURL());
                               var.setPrice(productDetail.getResult().getProductResults().get(i).getProductPrice());
                               var.setItemName(productDetail.getResult().getProductResults().get(i).getProductName());
                               var.setBrand(productDetail.getResult().getProductResults().get(i).getBrand());
                               var.setCategory(productDetail.getResult().getProductResults().get(i).getCategory());
                               theIds.add(var.getID());
                               Log.d("ProductListActivity","the list ids are "+var.getID());

                           }
                            new CheckPriceAvail(){
                            }.execute();

                        }
                        else{
                            Log.d("ProductListActivity","there was a problem ");

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("ProductListActivity","there was a problem "+error);
                    }
                });
            }


        return null;
        }
    }
    class CheckPriceAvail extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String,String >tempMap;
            SmartService connection = SmartConnection.getService();
            for(int i=0;i<theIds.size();i++)
            {
                tempMap = new HashMap<>();
                tempMap.put("id",theIds.get(i));
                connection.getProductPrices(tempMap, new Callback<SingleItemDetail>() {
                    @Override
                    public void success(SingleItemDetail singleItemDetail, Response response) {
                        if(singleItemDetail.getStatus().equals("SUCCESS") && singleItemDetail.getResult().getPrices().size()!=0){
                            ProductVariables var = new ProductVariables();
                            var.setCategory(singleItemDetail.getResult().getTheCategory());
                            var.setPrice(Integer.parseInt(singleItemDetail.getResult().getThePrice()));
                            var.setBrand(singleItemDetail.getResult().getTheBrand);
                            var.setItemName(singleItemDetail.getResult().getTheName());
                            var.setImageUrl(singleItemDetail.getResult().getImageUrl());
                            var.setID(singleItemDetail.getResult().getTheID());
                            ItemList.add(var);

                            prog.stopSpinning();
                            prog.setVisibility(View.INVISIBLE);
                            theContentList.setVisibility(View.VISIBLE);
                            View recyclerView = findViewById(R.id.products_list);
                            assert recyclerView != null;
                            setupRecyclerView(singleItemDetail.getStatus(),(RecyclerView) recyclerView);

                        }
                        else
                        {
                            Log.d("ProductListActivity","there is a problem with the if condition");


                        }
                    }


                    @Override
                    public void failure(RetrofitError error) {
Log.d("ProductListActivity","There is a problem in this async task"+error);

                    }
                });

            }

            return null;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        if(searchView!=null){
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
        else
        {
            Log.d("MessageListActivity","Searchview is null ");
        }
        return true;
    }
}
