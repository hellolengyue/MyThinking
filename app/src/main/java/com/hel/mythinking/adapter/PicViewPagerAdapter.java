package com.hel.mythinking.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hel.mythinking.R;
import com.hel.mythinking.bean.PicBean;
import com.hel.mythinking.utils.PinchImageView;

import java.util.List;

/**
 * @author hel
 * @date 2018/8/6
 * 文件 MyThinking
 * 描述
 */

public class PicViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<PicBean.ResultsBean> datas;
    private setOnItemListener listener;

    public PicViewPagerAdapter(Context context, List<PicBean.ResultsBean> datas) {
        this.context = context;
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
        View view = View.inflate(context, R.layout.activity_view_item,null);
        PinchImageView pinchImageView = view.findViewById(R.id.pinch_image);
        Glide.with(context).load(datas.get(position).getUrl()).placeholder(R.drawable.replace).error(R.drawable.replace).into(pinchImageView);
        container.addView(view);
        pinchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               listener.onClickListener(position);
            }
        });
        pinchImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClickListener(position);
                return true;
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
    public void setOnItemListener(setOnItemListener listener){
        this.listener = listener;
    }
    public interface setOnItemListener{
        void onClickListener(int position);
        void onLongClickListener(int position);
    }
}
