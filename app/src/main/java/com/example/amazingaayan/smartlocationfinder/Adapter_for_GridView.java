package com.example.amazingaayan.smartlocationfinder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Amazing Aayan on 23-May-16.
 */
public class Adapter_for_GridView extends BaseAdapter { // for Managing GridView data;
    Context context;
    int[] category_icons = {R.drawable.atm_icon, R.drawable.bank_icon, R.drawable.hospital, R.drawable.cafe_icon,
            R.drawable.police_icon, R.drawable.airport_icon, R.drawable.filling_station_icon, R.drawable.fire_service_icon};

    Adapter_for_GridView(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return category_icons.length;
    }

    @Override
    public Object getItem(int position) {
        return category_icons[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View row = view;
        ViewHolder holder = null;
        if(row == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.single_layout_icon_holder, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.categoryIcon);
            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) row.getTag();
        }

        holder.image.setImageResource(category_icons[position]);
        return row;
    }

    class ViewHolder{
        ImageView image;
    }


}


