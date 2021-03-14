package com.test.sitephototestapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.test.sitephototestapp.R;
import com.test.sitephototestapp.helper.model.BinLocationData;

import java.util.ArrayList;
import java.util.Locale;

public class AdapterLocation extends Adapter<AdapterLocation.MyViewHolder> {

    Activity activity;
    ArrayList<BinLocationData> listData;

    public AdapterLocation(Activity activity, ArrayList<BinLocationData> listData) {
        this.activity = activity;
        this.listData = listData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .layout_simple_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtName.setText(String.format(Locale.US, "Id: %d\nLat: %s\nLong: %s", listData.get(position).getId(), listData.get(position).getLatitude(), listData.get(position).getLongitude()));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
        }
    }

}
