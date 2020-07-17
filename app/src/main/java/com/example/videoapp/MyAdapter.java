package com.example.videoapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videoapp.data.VideoResponse;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<VideoResponse.Video> mDataSet;

    private static final String TAG = "MyAdapter";

    private int mNumberItems;

    private final ListItemClickListener mOnClickListener;

    private static int viewHolderCount;

    public MyAdapter(int numListItems, ListItemClickListener listener) {
//        mNumberItems = numListItems;
        mOnClickListener = listener;
        viewHolderCount = 0;
    }

    public void setDate(List<VideoResponse.Video> mDataSet) {
        this.mDataSet = mDataSet;
    }


    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.number_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: " + viewHolderCount);
        viewHolderCount++;

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //private final TextView id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //id= itemView.findViewById(R.id.item_number);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
           // id.setText("" + mDataSet.get(position).id);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            if (mOnClickListener != null) {
                mOnClickListener.onListItemClick(clickedPosition);
            }
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


}
