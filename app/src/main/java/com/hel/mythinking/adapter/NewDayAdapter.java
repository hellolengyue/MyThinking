package com.hel.mythinking.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hel.mythinking.Constant;
import com.hel.mythinking.R;
import com.hel.mythinking.bean.JokeData;

import java.util.List;

/**
 * @author hel
 * @date 2018/8/24
 * 文件 MyThinking
 * 描述
 */

public class NewDayAdapter extends RecyclerView.Adapter<NewDayAdapter.ViewHolder> {
    private List<JokeData.ResultBean> datas;
    private Context context;
    private SQLiteDatabase database;


    public NewDayAdapter(List<JokeData.ResultBean> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setText(datas.get(position).getContent());
        holder.dayShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "每日更新笑话：\n" + datas.get(position).getContent() + "\n——更多笑话尽在笑话急转弯");
                context.startActivity(Intent.createChooser(textIntent, "分享"));
            }
        });
        holder.dayColl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = SQLiteDatabase.openOrCreateDatabase(Constant.dataPath + "collection", null);

                Cursor cursor = database.query("joke", null, "content = ?", new String[]{datas.get(position).getContent()}, null, null, null);
                if (cursor.moveToFirst()) {
                    Toast.makeText(context, "你已经收藏了", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put("content", datas.get(position).getContent());
                    values.put("coll", 1);
                    long joke = database.insert("joke", null, values);
                    if (joke > 0) {
                        Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        holder.dayCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                ClipData clipData = ClipData.newPlainText(null, datas.get(position).getContent());
                // 把数据集设置（复制）到剪贴板
                cm.setPrimaryClip(clipData);
                Toast.makeText(context, "复制成功，可以发给朋友们了。", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private LinearLayout dayShare;
        private LinearLayout dayColl;
        private LinearLayout dayCopy;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.content);
            dayShare = itemView.findViewById(R.id.day_share);
            dayColl = itemView.findViewById(R.id.day_coll);
            dayCopy = itemView.findViewById(R.id.day_copy);
        }
    }
}
