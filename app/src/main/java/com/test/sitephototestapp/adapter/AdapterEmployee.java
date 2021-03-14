package com.test.sitephototestapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.test.sitephototestapp.R;
import com.test.sitephototestapp.helper.model.BinEmpData;

import java.util.ArrayList;
import java.util.Locale;

public class AdapterEmployee extends Adapter<AdapterEmployee.MyViewHolder> {

    Activity activity;
    ArrayList<BinEmpData> listData;
    public AdapterEmployee(Activity activity, ArrayList<BinEmpData> listData)
    {
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
        holder.txtName.setText(String.format(Locale.US,"Id: %d\n%s\nEmail: %s",listData.get(position).getId(),listData.get(position).getName(),listData.get(position).getEmail()));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = (TextView)itemView.findViewById(R.id.txtName);
        }
    }

}
