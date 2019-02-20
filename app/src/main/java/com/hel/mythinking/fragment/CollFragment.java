package com.hel.mythinking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.hel.mythinking.R;
import com.hel.mythinking.activity.JdShowActivity;
import com.hel.mythinking.activity.JokeActivity;
import com.hel.mythinking.activity.ThinkActivity;
import com.hel.mythinking.adapter.ShowFragmentItemAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * @author hel
 * @date 2018/2/27
 * 文件 MyThinking
 * 描述
 */

public class CollFragment extends BaseFragment {
    private GridView grid_coll;
    private List<String> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_common, null);
        initAdData();
        grid_coll = inflate.findViewById(R.id.grid_view);
        if (list.size() <= 0) {
            list.add(getResources().getString(R.string.coll_item_think));
            list.add(getResources().getString(R.string.coll_item_joke));
//            list.add(getResources().getString(R.string.coll_item_pic));
            list.add(getResources().getString(R.string.coll_item_jd));
        }

        grid_coll.setAdapter(new ShowFragmentItemAdapter(getActivity(), list));
        grid_coll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(getActivity(), ThinkActivity.class);
                        intent.putExtra("id", "");
                        intent.putExtra("type", 1);
                        intent.putExtra("title", getResources().getString(R.string.title_think));
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent2 = new Intent(getActivity(), JokeActivity.class);
                        intent2.putExtra("id", "");
                        intent2.putExtra("type", 1);
                        intent2.putExtra("title", getResources().getString(R.string.coll_item_joke));
                        startActivity(intent2);
                        break;
//                    case 2:
//                        Intent intent3 = new Intent(getActivity(), CollActivity.class);
//                        startActivity(intent3);
//                        break;
                    case 3:
                        Intent intent4 = new Intent(getActivity(), JdShowActivity.class);
                        intent4.putExtra("type", 1);
                        startActivity(intent4);
                        break;
                }
            }
        });
        return inflate;
    }

}
