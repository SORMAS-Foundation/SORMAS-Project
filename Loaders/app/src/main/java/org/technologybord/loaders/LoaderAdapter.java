package org.technologybord.loaders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Orson on 06/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class LoaderAdapter extends BaseAdapter {

    private List<String> data = new ArrayList<>();

    private LayoutInflater layoutInflater;

    public LoaderAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_loader, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText(data.get(position));
        return convertView;
    }

    public void swapData(Collection<String> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }
}
