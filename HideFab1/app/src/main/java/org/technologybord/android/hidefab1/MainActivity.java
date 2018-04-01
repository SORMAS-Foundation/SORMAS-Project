package org.technologybord.android.hidefab1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> listArray = new ArrayList<>();
        listArray.add("One");
        listArray.add("Two");
        listArray.add("Three");
        listArray.add("Four");
        listArray.add("Five");
        listArray.add("Six");
        listArray.add("Two");
        listArray.add("Three");
        listArray.add("Four");
        listArray.add("Five");
        listArray.add("One");
        listArray.add("Two");
        listArray.add("Three");
        listArray.add("Four");
        listArray.add("Five");
        listArray.add("Six");
        listArray.add("Two");
        listArray.add("Three");
        listArray.add("Four");
        listArray.add("Five");

        recyclerView = (RecyclerView)findViewById(R.id.lvToDoList);
        layoutManager = new LinearLayoutManager(this);
        adapter = new MyAdapter(listArray);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mDataSet;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_text_view, parent, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mDataSet.get(position));
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(TextView textView) {
                super(textView);
                this.mTextView = textView;
            }
        }

        public MyAdapter(List<String> dataSet) {
            this.mDataSet = dataSet;
        }
    }
}
