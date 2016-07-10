package com.kaidoh.mayuukhvarshney.smartprix;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mayuukhvarshney on 01/07/16.
 */
public class ItemDetailActivity extends AppCompatActivity {

    SmartService connection;
    ImageView ProductImage;
    TextView ProductName,BestPrice;
    String RecievedImageUrl="",RecievedId="",RecievedName="";
    HashMap<String,String>queries;
    ArrayList<ArrayDetail> ItemPrices;
    MyAdapter mAdapter;
    ProgressWheel prog ;
    RecyclerView thelist;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail_page);
        connection = SmartConnection.getService();
       ProductImage = (ImageView) findViewById(R.id.Product_Image);
        ProductName = (TextView) findViewById(R.id.Product_Name);
        BestPrice = (TextView) findViewById(R.id.Best_price);
        prog = (ProgressWheel) findViewById(R.id.progress_wheel1);
        thelist = (RecyclerView) findViewById(R.id.store_price_list);
        if(savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras==null)
            {
                RecievedId="";
                RecievedImageUrl="";
                RecievedName="";
            }
            else
            {
                RecievedId=extras.getString("ID");
                RecievedImageUrl=extras.getString("Image");
                RecievedName=extras.getString("Name");
            }
        }
        else
        {
            RecievedImageUrl=(String)savedInstanceState.getSerializable("Image");
            RecievedId=(String)savedInstanceState.getSerializable("ID");
            RecievedName=(String)savedInstanceState.getSerializable("Name");
        }

        queries = new HashMap<>();
        ItemPrices = new ArrayList<>();
        prog.spin();
        thelist.setVisibility(View.INVISIBLE);
        new getProductPrices().execute();



    }
    private void setupRecyclerView(String result,@NonNull RecyclerView recyclerView) {
        mAdapter= new MyAdapter(result,ItemPrices);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<ArrayDetail> theNames;

        String Result;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView thePrice,BestPrice;
            public ImageView StoreImage;
            public Button BuyButton;

            public View mView;
            public MyViewHolder(View view) {
                super(view);

                StoreImage= (ImageView) view.findViewById(R.id.store_logo);
                thePrice = (TextView) view.findViewById(R.id.item_Price);
                BuyButton = (Button) view.findViewById(R.id.buy_button);
                BestPrice = (TextView) view.findViewById(R.id.Best_price);
                mView = view;



            }
        }


        public MyAdapter(String result,ArrayList<ArrayDetail> names) {
            this.theNames = names;
            this.Result = result;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_detail_list_item, parent, false);

            return new MyViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            //holder.mName.setText(theNames.get(position).ger);
            if(Result.equals("SUCCESS")){

                Picasso.with(ItemDetailActivity.this).load(theNames.get(position).getStoreUrl()).into(holder.StoreImage);
                String DisplayPrice = "Rs. "+String.valueOf(theNames.get(position).getStorePrice());
                holder.thePrice.setText(DisplayPrice);
            }
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

            holder.BuyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri= Uri.parse(theNames.get(position).getBuylink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return theNames.size();
        }
    }

    class getProductPrices extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            queries.put("id",RecievedId);

            queries.put("indent","1");
            Log.d("itemDetailActivity","the string is "+RecievedId);
            connection.getProductPrices(queries, new Callback<SingleItemDetail>() {
                @Override
                public void success(SingleItemDetail singleItemDetail, Response response) {
                    Log.d("ItemDetailActivity","the resonse is "+response);
                    if(singleItemDetail.getStatus().equals("SUCCESS")){
                        Log.d("ItemDetailActivity"," the array stuff is "+singleItemDetail.getResult().getTheID()+" "+singleItemDetail.getResult().getPrices());
                        int arraysize = singleItemDetail.getResult().getPrices().size();
                        Log.d("Arraysize","the array size is "+arraysize);
                        for(int i=0;i<arraysize;i++)
                        {
                         ArrayDetail arr = new ArrayDetail();
                            arr.setStoreName(singleItemDetail.getResult().getPrices().get(i).getStoreName());
                            arr.setStorePrice(singleItemDetail.getResult().getPrices().get(i).getStorePrice());
                            arr.setStoreUrl(singleItemDetail.getResult().getPrices().get(i).getStoreLogo());
                            arr.setBuyLink(singleItemDetail.getResult().getPrices().get(i).getBuyLink());
                            Log.d("ItemDetailActivity","the array items are "+arr.getStorePrice());
                            ItemPrices.add(arr);
                        }
                        prog.stopSpinning();
                        prog.setVisibility(View.INVISIBLE);
                        thelist.setVisibility(View.VISIBLE);
                        Picasso.with(ItemDetailActivity.this).load(RecievedImageUrl).into(ProductImage);
                        ProductName.setText(RecievedName);
                        Collections.sort(ItemPrices, new Comparator<ArrayDetail>() {
                            @Override
                            public int compare(ArrayDetail lhs, ArrayDetail rhs) {
                             return lhs.getStorePrice().compareTo(rhs.getStorePrice());
                            }
                        });
                        String bestprice="Best Price:"+ItemPrices.get(0).getStorePrice();
                       BestPrice.setText(bestprice);
                        View recyclerView = findViewById(R.id.store_price_list);
                        assert recyclerView != null;
                        setupRecyclerView(singleItemDetail.getStatus(),(RecyclerView) recyclerView);
                    }
                    else
                    {
                        Log.d("ItemDetailActivity","result is "+singleItemDetail.getStatus());
                        prog.stopSpinning();
                        prog.setVisibility(View.INVISIBLE);
                        thelist.setVisibility(View.VISIBLE);
                        Toast.makeText(ItemDetailActivity.this,"There was a Problem :(",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("ItemDetailActivity", "there was a failure "+error);
                    prog.stopSpinning();
                    prog.setVisibility(View.INVISIBLE);
                    thelist.setVisibility(View.VISIBLE);
                    Toast.makeText(ItemDetailActivity.this,"There was a Problem :(",Toast.LENGTH_SHORT).show();

                }
            });
            return null;

        }
    }
    class ArrayDetail{
        private String BuyLink;
        private String StoreName;
        private String StorePrice;
        private String StoreUrl;

        public void setBuyLink(String link){
            this.BuyLink = link;

        }
        public void setStoreName(String name ){
            this.StoreName = name;

        }
        public void setStorePrice(String price ){
            this.StorePrice = price;
        }
        public void setStoreUrl(String url){
            this.StoreUrl = url;
        }

        public String getBuylink(){
            return this.BuyLink;
        }
        public String getStoreName(){
            return this.StoreName;
        }

        public String getStoreUrl() {
            return StoreUrl;
        }

        public String getStorePrice() {
            return StorePrice;
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
