package com.yxw.quickindex;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Person> persons;

    public MyAdapter(Context mContext, ArrayList<Person> persons) {
        this.mContext = mContext;
        this.persons = persons;
    }

    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null){
            view = View.inflate(mContext, R.layout.item_list, null);
        }
        ViewHolder mViewHolder = ViewHolder.getHolder(view);
        Person p = persons.get(position);

        String str = null;
        String currentLetter = p.getPinyin().charAt(0) + "";
        // 根据上一个首字母,决定当前是否显示字母
        if(position == 0){
            str = currentLetter;//位置0的地方，字母是要显示的
        }else {
            // 上一个人的拼音的首字母
            String preLetter = persons.get(position - 1).getPinyin().charAt(0) + "";
            if(!TextUtils.equals(preLetter, currentLetter)){//两个字符串不相等，字母也要显示
                str = currentLetter;
            }
        }

        // 根据str是否为空,决定是否显示索引栏
        mViewHolder.mIndex.setVisibility(str == null ? View.GONE : View.VISIBLE);
        mViewHolder.mIndex.setText(currentLetter);
        mViewHolder.mName.setText(p.getName());
        return view;
    }

    private static class ViewHolder {
        TextView mIndex;
        TextView mName;

        private static ViewHolder getHolder(View view) {
            Object tag = view.getTag();
            if (tag != null) {
                return (ViewHolder) tag;
            } else {
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.mIndex = view.findViewById(R.id.tv_index);
                viewHolder.mName = view.findViewById(R.id.tv_name);
                view.setTag(viewHolder);
                return viewHolder;
            }
        }

    }
}
