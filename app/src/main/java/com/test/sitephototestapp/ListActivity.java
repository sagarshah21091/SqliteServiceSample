package com.test.sitephototestapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.sitephototestapp.adapter.AdapterEmployee;
import com.test.sitephototestapp.adapter.AdapterLocation;
import com.test.sitephototestapp.helper.Utils;
import com.test.sitephototestapp.helper.db.DbManager;
import com.test.sitephototestapp.helper.model.BinEmpData;
import com.test.sitephototestapp.helper.model.BinLocationData;

import java.util.ArrayList;


public class ListActivity extends AppCompatActivity {
    private RecyclerView rcvData;
    private LinearLayout ivEmpty;
    private AdapterEmployee adapterEmployee;
    private AdapterLocation adapterLocation;
    private int fromTable;
    DbManager dbManager;
    ArrayList<BinEmpData> listDataEmp = new ArrayList<>();
    ArrayList<BinLocationData> listDataLoc = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);
        fromTable = getIntent().getExtras().getInt(Utils.constant.key_fromTable);
        dbManager = new DbManager(this);
        rcvData = (RecyclerView)findViewById(R.id.rcvData);
        ivEmpty = (LinearLayout) findViewById(R.id.ivEmpty);
        setData();
    }

    private void setData() {
        switch (fromTable)
        {
            case Utils.constant.table.Employee:
                initRcvEmpData();
                fetchUpdateEmpData();
                break;
            case Utils.constant.table.Location:
                initRcvLocData();
                fetchUpdateLocData();
                break;
        }
    }

    private void initRcvEmpData() {
        adapterEmployee = new AdapterEmployee(this, listDataEmp);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);
        rcvData.setAdapter(adapterEmployee);
    }

    private void initRcvLocData() {
        adapterLocation = new AdapterLocation(this, listDataLoc);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);
        rcvData.setAdapter(adapterLocation);
    }

    private void fetchUpdateEmpData() {
        listDataEmp.addAll(dbManager.fetchEmpData());
        adapterEmployee.notifyDataSetChanged();
        setEmptyUi(listDataEmp.size()==0);
    }

    private void fetchUpdateLocData() {
        listDataLoc.addAll(dbManager.fetchLocData());
        adapterLocation.notifyDataSetChanged();
        setEmptyUi(listDataLoc.size()==0);
    }

    private void setEmptyUi(boolean haveData)
    {
        switch (fromTable)
        {
            case Utils.constant.table.Employee:
                ivEmpty.setVisibility(haveData? View.VISIBLE:View.GONE);
                break;
            case Utils.constant.table.Location:
                ivEmpty.setVisibility(haveData? View.VISIBLE:View.GONE);
                break;
        }
    }
}
