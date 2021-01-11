package com.example.jsontest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ColorAdapter extends BaseAdapter {
    private Context mContext;
    private int lastPosition;
    private String[] str = null;

    public void setData(String[] str, int lastPos) {
        this.str = str;
        this.lastPosition = lastPos;
    }

    public void setSelection(int position) {
        lastPosition = position;
    }

    public ColorAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return str.length;
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        ColorAdapter.ViewHolder holder = null;
        if (view == null) {
            holder = new ColorAdapter.ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
            holder.text = (TextView) view.findViewById(R.id.idGridviewTextview);
            holder.check = (ImageView) view.findViewById(R.id.idGridviewCheck);
            view.setTag(holder);
        } else {
            holder = (ColorAdapter.ViewHolder) view.getTag();
        }
        holder.text.setText(str[position] + "");
        if (lastPosition == position) {    //最後選擇的位置
            holder.check.setBackgroundResource(R.mipmap.button_checked);
        } else {
            holder.check.setBackgroundResource(R.mipmap.button_unchecked);
        }
        return view;
    }

    class ViewHolder {
        private TextView text;
        private ImageView check;
    }
}
