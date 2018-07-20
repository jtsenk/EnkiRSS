package com.jtsenkbeil.enki.enkirss.feature.adapt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jtsenkbeil.enki.enkirss.feature.R;
import com.jtsenkbeil.enki.enkirss.feature.util.Episode;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.util.ArrayList;

public class EpsListAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<Episode> list;

    public EpsListAdapter(Context context, ArrayList<Episode> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        } else {
            Utils.logD("EpsListAdapter","list is null: returning 0");
            return 0;
        }
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
        convertView = inflater.inflate(R.layout.eps_item, parent, false);
        TextView tv = (TextView)convertView.findViewById(R.id.eps_item_tv);
        tv.setText(list.get(position).getTitle() );
        //Log.d("get", list.get(position).getTitle() );
        return tv;
    }
}
