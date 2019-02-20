package com.hel.mythinking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hel.mythinking.R;
import com.hel.mythinking.bean.PicBean;

import java.util.List;

/**
 * @author hel
 * @date 2018/6/9
 * 文件 MyThinking
 * 描述
 */

public class PicRecyclerViewAdapter extends RecyclerView.Adapter<PicRecyclerViewAdapter.MyViewHolder> {
    private List<PicBean.ResultsBean> resultsBeen;
    private Context context;
    private PicRecyclerViewAdapter.OnItemClickListener onItemClickListener;
    public PicRecyclerViewAdapter(Context context, List<PicBean.ResultsBean> resultsBeen) {
        this.resultsBeen = resultsBeen;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Glide.with(context).load(resultsBeen.get(position).getUrl()).error(R.drawable.replace).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, pos);
                }
                //表示此事件已经消费，不会触发单击事件
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultsBeen.size();
    }
    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(PicRecyclerViewAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
