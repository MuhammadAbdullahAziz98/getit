package com.example.lenovo.getit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class TextListAdapter extends ArrayAdapter<String> implements Filterable
{
    private ArrayList<String> str;
    Context myContext;
    public TextListAdapter(Context context, ArrayList<String> str) {
        super(context, 0,str);
        this.str = str;
        myContext = context;

    }
    public String getItem(int position){
    return str.get(position);
}

    public int getCount() {
    return str.size();
}

    public View getView(int position, View convertView, ViewGroup parent) {

        final String s = getItem(position);
        final String string = s;
        ViewHolder holder;
        if (convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.description = (TextView) convertView.findViewById(R.id.email);


            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.description.setText(s);
        holder.description.setTag(s);
        return convertView;
    }


}
