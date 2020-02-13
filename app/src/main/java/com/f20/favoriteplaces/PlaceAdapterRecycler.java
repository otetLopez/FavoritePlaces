package com.f20.favoriteplaces;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
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
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // int itemPosition = this.getChildLayoutPosition(view);
                //Place place = mDataset.get(itemPosition);
                Intent intent = new Intent(context, MapsActivity.class);
                //context.startActivity(intent);
                String addr = view.findViewById(R.id.tv_addr).toString();
                //Toast.makeText(context, addr, Toast.LENGTH_SHORT).show();
            }
        }); */

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Place place = mDataset.get(position);

        holder.tvAddress.setText(place.getAddr());
        holder.tvDate.setText("Date created: " + place.getDate());

        if(place.isVisited())
            holder.cell.setBackgroundColor(Color.LTGRAY);
        else
            holder.cell.setBackgroundColor(Color.parseColor("#FAFAFA"));

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, String.format("Address: %s", place.getAddr()), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context, MapsActivity.class);
//                intent.putExtra("modPlace", place);
//                intent.putExtra("position", position);
//                context.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Place place, int position) {
        mDataset.add(position, place);
        notifyItemInserted(position);
    }

    public List<Place> getData() {
        return mDataset;
    }
}


