package com.hel.mythinking.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.hel.mythinking.Constant;
import com.hel.mythinking.R;
import com.hel.mythinking.activity.JokeActivity;
import com.hel.mythinking.adapter.ShowFragmentItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hel
 * @date 2018/2/27
 * 文件 MyThinking
 * 描述
 */

public class JokeFragment extends BaseFragment {
    private GridView grid_joke;
    private List<String> indexNj = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_common, null);
        initAdData();
        grid_joke = inflate.findViewById(R.id.grid_view);
        initData();
        grid_joke.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = indexNj.get(position);
                Intent intent = new Intent(getActivity(), JokeActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("title", name);
                startActivity(intent);
            }
        });
        return inflate;
    }
    private void initData() {
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(Constant.dataPath + "collection", null);
        Cursor cursor = database.rawQuery("select distinct type from joke where type != ''", null);
        if (indexNj.size() > 0) {
            indexNj.clear();
        }
        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndex("type"));
            indexNj.add(type);
        }
        cursor.close();
        grid_joke.setAdapter(new ShowFragmentItemAdapter(getActivity(),indexNj));
    }

}
