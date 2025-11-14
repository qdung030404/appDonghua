package com.example.appdonghua.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.appdonghua.R;
import com.example.appdonghua.Model.Chapter;

import java.util.List;

public class ChapterAdapter extends BaseAdapter {
    private List<Chapter> chapters;
    private LayoutInflater inflater;

    public ChapterAdapter(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    @Override
    public int getCount() {
        return chapters.size();
    }

    @Override
    public Object getItem(int position) {
        return chapters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            if (inflater == null) {
                inflater = LayoutInflater.from(parent.getContext());
            }

            convertView = inflater.inflate(R.layout.item_chapter, parent, false);

            holder = new ViewHolder();
            holder.tvChapter = convertView.findViewById(R.id.tvChapter);
            holder.tvView = convertView.findViewById(R.id.tvView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Chapter chapter = chapters.get(position);
        holder.tvChapter.setText(chapter.getChapter());
        holder.tvView.setText(String.valueOf(chapter.getViews()));

        return convertView;
    }
    static class ViewHolder {
        TextView tvChapter;
        TextView tvView;
    }
}