package com.hel.mythinking.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hel.mythinking.R;

import java.util.List;

/**
 * @author hel
 * @date 2018/6/9
 * 文件 MyThinking
 * 描述
 */

public class JdAdapter extends RecyclerView.Adapter<JdAdapter.MyViewHolder> {
    private List<String> resultsBeen;
    private JdAdapter.OnItemClickListener onItemClickListener;
    public JdAdapter( List<String> resultsBeen) {
        this.resultsBeen = resultsBeen;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jd_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.textView.setText(resultsBeen.get(position));
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
    public void setOnItemClickListener(JdAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.content);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
