package com.sky.tuan.android.adapter;

import java.util.List;

import com.sky.tuan.android.R;
import com.sky.tuan.android.bean.Deal;
import com.sky.tuan.android.common.BitmapManager;

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

public class DealAdapter extends BaseAdapter {

	public int getCount() {
		if(deals!=null)
		return deals.size();
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
	
	private List<Deal> deals;

	public View getView(int position, View view, ViewGroup viewGroup) {
		if(view==null)
		{
			view=listContainer.inflate(itemViewResource, null);
		}
		Deal deal=deals.get(position);
		ImageView image = (ImageView)view.findViewById(R.id.deal_image);
		TextView price= (TextView)view.findViewById(R.id.deal_price);
		Button link = (Button)view.findViewById(R.id.deal_link);
		TextView title=(TextView)view.findViewById(R.id.deal_title);
	    link.setText("购买");
	    bmpManager.loadBitmap(deal.getImageUrl(), image);
	    price.setText("$"+deal.getCurrentPrice());
	    title.setText(deal.getTitle());
	    view.setTag(deal);
	    return view;
	}
	
	private Context 	context;//运行上下文
	private LayoutInflater 	listContainer;//视图容器
	private int itemViewResource;
	private BitmapManager bmpManager;
	
	public DealAdapter(Context context,int resource,List<Deal> deals) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.deals=deals;
		this.bmpManager = new BitmapManager(null);
	}

}
