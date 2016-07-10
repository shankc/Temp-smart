package com.kaidoh.mayuukhvarshney.smartprix;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

    MyAdapter mAdapter;
    ArrayList<String> Names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Names= new ArrayList<>();
        Names.add("Mobiles");
        Names.add("Tablets");
        Names.add("Laptops");
        Names.add("Cameras");
        Names.add("Memory Cards");

        View recyclerView = findViewById(R.id.categories_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter= new MyAdapter(Names);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<String> theNames;



        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mName;


            public View mView;
            public MyViewHolder(View view) {
                super(view);
                mName=(TextView)view.findViewById(R.id.category_names);
                mView = view;


            }
        }


        public MyAdapter(ArrayList<String> names) {
            this.theNames = names;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_list_content, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.mName.setText(theNames.get(position));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this,ProductListActivity.class);
                    intent.putExtra("Product",theNames.get(position));
                    startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return theNames.size();
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
