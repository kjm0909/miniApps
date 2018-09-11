package com.mini.apps.series.Adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mini.apps.series.NewsTTS.NewsActivity;
import com.mini.apps.series.R;
import com.mini.apps.series.UtilLog;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<NewsActivity.NewsItem> mList = new ArrayList<NewsActivity.NewsItem>();
    private int mLayout;
    private String[] mTitle;
    private String[] mSub;

    public ListAdapter(Context context, int layout, ArrayList<NewsActivity.NewsItem> mArrayList)
    {
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mLayout = layout;
        this.mList = mArrayList;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        if(mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mInflater.inflate(mLayout, null);
        }
        UtilLog.d("position: "+position);
        TextView text_title = (TextView)convertView.findViewById(R.id.name);
        text_title.setText(mList.get(position).getNewsTitle());
        UtilLog.d("title : "+ mList.get(position).getNewsTitle());
        TextView text_sub = (TextView)convertView.findViewById(R.id.attribute);
        text_sub.setText(mList.get(position).getNewsSub());
        UtilLog.d("sub : "+ mList.get(position).getNewsSub());

        return convertView;

    }

}
