package com.hel.mythinking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hel.mythinking.R;

import java.util.List;

/**
 * @author hel
 * @date 2018/8/23
 * 文件 MyThinking
 * 描述
 */

public class ShowFragmentItemAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;


    public ShowFragmentItemAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.list_item, null);
            holder = new ViewHolder();
            holder.content = convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.content.setText(list.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView content;

    }
}


