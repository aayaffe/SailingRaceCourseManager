package com.aayaffe.sailingracecoursemanager.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.R;

import java.util.ArrayList;
import java.util.List;

import in.avimarine.orccertificatesimporter.ORCCertObj;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 14/01/2017.
 */

public class OrcCertListAdapter extends BaseAdapter {
    private static final String TAG = "OrcCertListAdapter";
    Context mContext;
    List<ORCCertObj> items;
    public OrcCertListAdapter(Context c) {
        super();
        mContext = c;
        items = new ArrayList();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setItems(ArrayList<ORCCertObj> items) {
        this.items = items;
    }

    public void addItem(ORCCertObj o) {
        if (!items.contains(o)) {
            this.items.add(o);
            this.notifyDataSetChanged();
        } else {
            Toast.makeText(mContext, "Certificate already in list", Toast.LENGTH_SHORT).show();
        }
    }
    public void removeItem(int i) {
        items.remove(i);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_cert, viewGroup, false);
        }
        ((TextView) view.findViewById(android.R.id.text1))
                .setText(((ORCCertObj)getItem(i)).getYachtsName());
        ((TextView) view.findViewById(android.R.id.text2))
                .setText(((ORCCertObj)getItem(i)).getSailNo());
        final ImageButton delete =(ImageButton)view.findViewById(R.id.remove_assignment_button);
        delete.setOnClickListener(view1 -> {
            Log.d(TAG, "getView: Remove item " + i);
            removeItem(i);
        });
        return view;
    }

}
