package com.jtsenkbeil.enki.enkirss.feature.adapt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jtsenkbeil.enki.enkirss.feature.R;

import java.util.ArrayList;

public class DownloadsListAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<String> list;

    public DownloadsListAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.downloads_item, parent, false);
        TextView tv = (TextView)convertView.findViewById(R.id.downloads_item_tv);
        tv.setText(list.get(position));
        Log.d("get", list.get(position));
        return tv;
    }

}
