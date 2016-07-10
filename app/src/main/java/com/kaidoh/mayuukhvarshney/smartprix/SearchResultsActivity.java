package com.kaidoh.mayuukhvarshney.smartprix;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mayuukhvarshney on 08/07/16.
 */
public class SearchResultsActivity extends AppCompatActivity  {

    ArrayList<String> SearchIds;
    ArrayList<ProductVariables>ReturnedResults;
    MyAdapter adapter;
    ProgressWheel prog;
    RecyclerView theContentList;
    boolean flag =false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_list);
        SearchIds = new ArrayList<>();
       ReturnedResults= new ArrayList<>();
        prog = (ProgressWheel) findViewById(R.id.progress_wheel);
        theContentList = (RecyclerView) findViewById(R.id.products_list);
        handleIntent(getIntent());
    }
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY); // the query to search for...

            prog.spin();
            theContentList.setVisibility(View.INVISIBLE);
            new SearchForQuery(query).execute();
        }

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
                Picasso.with(SearchResultsActivity.this).load(theNames.get(position).getImageUrl()).into(holder.ProductImage);
                String DisplayPrice = "Price: "+"Rs. "+String.valueOf(theNames.get(position).getPrice());
                holder.thePrice.setText(DisplayPrice);
            }
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchResultsActivity.this,ItemDetailActivity.class);
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

    private void setupRecyclerView(String result,@NonNull RecyclerView recyclerView) {
        adapter= new MyAdapter(result,ReturnedResults);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }
    class SearchForQuery extends AsyncTask<Void,Void,Void>{

        String SearchVariable;

        public SearchForQuery(String txt){
            this.SearchVariable= txt;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //gett all the ids first=>
            SmartService connection = SmartConnection.getService();
            HashMap<String,String> queries;


            for(int i=0;i<200;i+=10)
            {
                queries= new HashMap<>();
                queries.put("q",SearchVariable);
                queries.put("start",String.valueOf((i)));
                connection.getSearchResults(queries, new Callback<ProductDetail>() {
                    @Override
                    public void success(ProductDetail productDetail, Response response) {
                        if(productDetail.getResponse().equals("SUCCESS")) {
                            int listsize = productDetail.getResult().getProductResults().size();
                            for (int i = 0; i < listsize; i++) {
                                SearchIds.add(productDetail.getResult().getProductResults().get(i).getID());
                                Log.d("SearchResultsActivity","the retured ids are "+SearchIds.get(i));
                            }
                            if (listsize != 0) {
                                new FinalSearchResults().execute();
                            }
                            else{
                                flag=true;
                                prog.stopSpinning();
                                prog.setVisibility(View.INVISIBLE);
                                theContentList .setVisibility(View.VISIBLE);
                                Toast.makeText(SearchResultsActivity.this,"No Results found!",Toast.LENGTH_SHORT).show();


                            }
                        }
                        else
                        {
                            Log.d("SearchResults","Response is fail");
                            prog.stopSpinning();
                            prog.setVisibility(View.INVISIBLE);
                            theContentList .setVisibility(View.VISIBLE);
                            Toast.makeText(SearchResultsActivity.this,"No Results found!",Toast.LENGTH_SHORT).show();
                            flag= true;
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                    Log.d("SearchResults","there was a problem "+error);
                        prog.stopSpinning();
                        prog.setVisibility(View.INVISIBLE);
                        theContentList .setVisibility(View.VISIBLE);
                    }
                });

                if(flag)
                {
                    break;
                }

            }

            return null;
        }
    }
    class FinalSearchResults extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            SmartService connection = SmartConnection.getService();
            HashMap<String,String> SearchPrices;
            for(int i=0;i<SearchIds.size();i++)
            {
                SearchPrices= new HashMap<>();
                SearchPrices.put("id",SearchIds.get(i));
                connection.getProductPrices(SearchPrices, new Callback<SingleItemDetail>() {
                    @Override
                    public void success(SingleItemDetail singleItemDetail, Response response) {
                        if(singleItemDetail.getStatus().equals("SUCCESS") &&  singleItemDetail.getResult().getPrices().size()!=0){
                            ProductVariables var = new ProductVariables();
                            var.setCategory(singleItemDetail.getResult().getTheCategory());
                            var.setPrice(Integer.parseInt(singleItemDetail.getResult().getThePrice()));
                            var.setBrand(singleItemDetail.getResult().getTheBrand);
                            var.setItemName(singleItemDetail.getResult().getTheName());
                            var.setImageUrl(singleItemDetail.getResult().getImageUrl());
                            var.setID(singleItemDetail.getResult().getTheID());
                            ReturnedResults.add(var);

                            prog.stopSpinning();
                            prog.setVisibility(View.INVISIBLE);
                            theContentList .setVisibility(View.VISIBLE);
                            View recyclerView = findViewById(R.id.products_list);
                            assert recyclerView != null;
                            setupRecyclerView(singleItemDetail.getStatus(), (RecyclerView)recyclerView);

                        }

                        else
                        {
                            Log.d("SearchResults","Response is fail in Returning Prices");
                            prog.stopSpinning();
                            prog.setVisibility(View.INVISIBLE);
                            theContentList .setVisibility(View.VISIBLE);

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("SearchResults","There was an error in second async");
                        prog.stopSpinning();
                        prog.setVisibility(View.INVISIBLE);
                        theContentList .setVisibility(View.VISIBLE);

                    }
                });
            }
            return null;
        }
    }
}
