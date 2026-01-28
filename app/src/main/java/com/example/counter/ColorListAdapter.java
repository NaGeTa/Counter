package com.example.counter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ColorListAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> names;
    private final List<Integer> values;

    public ColorListAdapter(Context context, List<String> names, List<Integer> values) {
        this.context = context;
        this.names = names;
        this.values = values;
    }

    @Override public int getCount() { return names.size(); }
    @Override public Object getItem(int position) { return names.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false);
        }

        View swatch = v.findViewById(R.id.viewSwatch);
        TextView tvName = v.findViewById(R.id.tvName);

        tvName.setText(names.get(position));

        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(values.get(position));
        swatch.setBackground(d);

        return v;
    }
}
