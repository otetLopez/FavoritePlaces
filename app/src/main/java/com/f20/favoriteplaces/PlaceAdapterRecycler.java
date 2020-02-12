package com.f20.favoriteplaces;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PlaceAdapterRecycler extends RecyclerView.Adapter<PlaceAdapterRecycler.MyViewHolder> {
    private List<Place> mDataset;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAddress;
        public TextView tvDate;
        public LinearLayout cell;
        public MyViewHolder(View v) {
            super(v);

            tvAddress = v.findViewById(R.id.tv_addr);
            tvDate = v.findViewById(R.id.tv_date);
            cell = v.findViewById(R.id.cell_place);
        }
    }

    public PlaceAdapterRecycler(Context context, List<Place> myDataset) {
        this.context = context;
        this.mDataset = myDataset;
    }

    @Override
    public PlaceAdapterRecycler.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_places, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Place place = mDataset.get(position);

        holder.tvAddress.setText(place.getAddr());
        holder.tvDate.setText("Date created: " + place.getDate());

        if(place.isVisited())
            holder.cell.setBackgroundColor(Color.LTGRAY);
        else
            holder.cell.setBackgroundColor(Color.parseColor("#FAFAFA"));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    //@Override
    //public int getSwipeLayoutResourceId(int position) {
//        return R.id.swipe;
//    }
}
