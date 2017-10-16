package com.sky.tuan.android.adapter;

import java.util.List;

import com.sky.tuan.android.R;
import com.sky.tuan.android.bean.Deal;
import com.sky.tuan.android.bean.Tag;
import com.sky.tuan.android.common.BitmapManager;

import android.R.raw;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TagAdapter extends BaseAdapter {

	public int getCount() {
		if(tags!=null)
		return tags.size();
		else
		{
			return 0;
		}
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private List<Tag> tags;

	public View getView(int position, View view, ViewGroup viewGroup) {
		if(view==null)
		{
			view=listContainer.inflate(itemViewResource, null);
		}
		Tag tag=tags.get(position);
		TextView textView=(TextView)view.findViewById(R.id.tag_name);
		textView.setText(tag.getName());
	    return view;
	}
	
	private Context context;//运行上下文
	private LayoutInflater 	listContainer;//视图容器
	private int itemViewResource;
	
	public TagAdapter(Context context,int resource,List<Tag> tags) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.tags=tags;
	}

}
