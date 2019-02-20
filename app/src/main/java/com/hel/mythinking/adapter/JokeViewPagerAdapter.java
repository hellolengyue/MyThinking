package com.hel.mythinking.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hel.mythinking.R;
import com.hel.mythinking.bean.NJ;

import java.util.List;

/**
 * @author hel
 * @date 2018/8/6
 * 文件 MyThinking
 * 描述
 */

public class JokeViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<NJ> datas;

    public JokeViewPagerAdapter(Context context, List<NJ> datas) {
        this.context = context;
        this.datas = datas;
    }

    public void setDatas(List<NJ> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = View.inflate(context, R.layout.joke_show_item, null);
        TextView jokeTitle = view.findViewById(R.id.tv_joke_title);
        TextView jokeContent = view.findViewById(R.id.tv_joke_content);
        jokeContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        String title = datas.get(position).getAnswer();
        String content = datas.get(position).getProblem();
        jokeTitle.setText(title);

        if (content.contains("、")){
            String[] split = content.split("、");
            jokeContent.setText(split[1]);
        }else{
            jokeContent.setText(content);
        }

        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
}
