package com.example.appdoctruyen.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Chapter;

import java.util.List;

public class ChapterAdapter extends BaseAdapter {

    private Context context;
    private List<Chapter> chapterList;

    public ChapterAdapter(Context context, List<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
    }

    @Override
    public int getCount() {
        return chapterList.size();
    }

    @Override
    public Object getItem(int position) {
        return chapterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView tvChapterName, tvChapterDate, tvFree;
        ImageView imgLock;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Nạp layout item_chapter.xml
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
            holder = new ViewHolder();

            // Ánh xạ các thành phần
            holder.tvChapterName = convertView.findViewById(R.id.tvChapterName);
            holder.tvChapterDate = convertView.findViewById(R.id.tvChapterDate);
            holder.tvFree = convertView.findViewById(R.id.tvFree);
            holder.imgLock = convertView.findViewById(R.id.imgLock);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Lấy dữ liệu của chương hiện tại
        Chapter chapter = chapterList.get(position);

        // Đổ dữ liệu lên UI
        holder.tvChapterName.setText(chapter.getName());
        holder.tvChapterDate.setText(chapter.getDate());

        // Xử lý logic 5 chương đầu miễn phí, các chương sau khóa
        if (chapter.isFree()) {
            holder.tvFree.setVisibility(View.VISIBLE);
            holder.imgLock.setVisibility(View.GONE);
        } else {
            holder.tvFree.setVisibility(View.GONE);
            holder.imgLock.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
