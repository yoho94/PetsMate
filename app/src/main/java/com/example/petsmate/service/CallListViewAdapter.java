package com.example.petsmate.service;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.petsmate.R;
import com.example.petsmate.table.CallListViewItem;

import java.util.ArrayList;

public class CallListViewAdapter extends BaseAdapter {
    private ArrayList<CallListViewItem> listViewItems = new ArrayList<>();

    @Override
    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.call_list_item, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.callListImageView1);
        TextView title = (TextView) convertView.findViewById(R.id.callListtextView1);
        TextView textView1 = (TextView) convertView.findViewById(R.id.callListtextView2);
        TextView textView2 = (TextView) convertView.findViewById(R.id.callListtextView3);
        TextView textView3 = (TextView) convertView.findViewById(R.id.callListtextView4);
        TextView textView4 = (TextView) convertView.findViewById(R.id.callListtextView5);
        TextView textView5 = (TextView) convertView.findViewById(R.id.callListtextView6);

        CallListViewItem callListViewItem = listViewItems.get(position);

        imageView.setImageDrawable(callListViewItem.getIconDrawable());
        title.setText(callListViewItem.getTitleStr());
        textView1.setText(callListViewItem.getStr1());
        textView2.setText(callListViewItem.getStr2());
        textView3.setText(callListViewItem.getStr3());
        textView4.setText(callListViewItem.getStr4());
        textView5.setText(callListViewItem.getStr5());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItems.get(position);
    }

    public void addItem(Drawable icon, String title, String str1, String str2, String str3, String str4, String str5, String serialNumber) {
        CallListViewItem item = new CallListViewItem();

        item.setIconDrawable(icon);
        item.setTitleStr(title);
        item.setStr1(str1);
        item.setStr2(str2);
        item.setStr3(str3);
        item.setStr4(str4);
        item.setStr5(str5);
        item.setSerialNumber(serialNumber);
        item.setSERIAL_NUMBER(Integer.parseInt(serialNumber));

        listViewItems.add(item);
    }

    public void addItem(Drawable icon, String title, String str1, String str2, String str3, String str4, String str5, int serialNumber) {
        CallListViewItem item = new CallListViewItem();

        item.setIconDrawable(icon);
        item.setTitleStr(title);
        item.setStr1(str1);
        item.setStr2(str2);
        item.setStr3(str3);
        item.setStr4(str4);
        item.setStr5(str5);
        item.setSERIAL_NUMBER(serialNumber);
        item.setSerialNumber(serialNumber+"");

        listViewItems.add(item);
    }

    public void clearItem() {
        listViewItems.clear();
    }
}
