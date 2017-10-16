package com.sky.tuan.android.adapter;

import java.util.List;

import com.sky.tuan.android.R;
import com.sky.tuan.android.bean.City;
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

public class CityAdapter extends BaseAdapter {

	public int getCount() {
		if(cities!=null)
		return cities.size();
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
	
	private List<City> cities;

	public View getView(int position, View view, ViewGroup viewGroup) {
		if(view==null)
		{
			view=listContainer.inflate(itemViewResource, null);
		}
		City city=cities.get(position);
		TextView textView=(TextView)view.findViewById(R.id.city_name);
		textView.setText(city.getName());
	    return view;
	}
	
	private Context context;//运行上下文
	private LayoutInflater 	listContainer;//视图容器
	private int itemViewResource;
	
	public CityAdapter(Context context,int resource,List<City> cities) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.cities=cities;
	}

}
