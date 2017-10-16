package com.sky.tuan.android.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sky.tuan.android.R;
import com.sky.tuan.android.adapter.CityAdapter;
import com.sky.tuan.android.adapter.DealAdapter;
import com.sky.tuan.android.adapter.TagAdapter;
import com.sky.tuan.android.bean.City;
import com.sky.tuan.android.bean.Deal;
import com.sky.tuan.android.bean.Tag;
import com.sky.tuan.android.common.AppConfig;
import com.sky.tuan.android.common.AppContext;
import com.sky.tuan.android.common.AppException;
import com.sky.tuan.android.common.DialogTool;
import com.sky.tuan.android.common.IntentUtils;
import com.sky.tuan.android.tag.PullToRefreshListView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;

public class MainActivity extends Activity {

	private PullToRefreshListView listView;
	private View listView_footer;
	private TextView listView_foot_more;
	private ProgressBar listView_foot_progress;
	private DealAdapter adapter;
	private ProgressBar loadingBar;
	private List<Deal> deals;
	private List<City> hotCities;
	private List<Tag> hotTags;
	private Handler handler;
	private int curLoadState = 0;

	private final int NOTHING = 0;
	private final int LOADMORE = 1;
	private final int REFRESH = 2;

	private boolean isRefresh = false;
	
	private List<Tag> tags;
	private List<City> cities;
	
	private PopupWindow tagPopupWindow;
	
	private  PopupWindow cityPopupWindow;
	
    private Button hotTagsBtn;
    
    private Button hotCitiesBtn;
    
    private TextView searchTextView;
    
    private Button searchButton;
    
    
    private boolean isNoData=false;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		listView_footer = getLayoutInflater().inflate(R.layout.listview_footer,
				null);
		listView_foot_more = (TextView) listView_footer
				.findViewById(R.id.listview_foot_more);
		listView_foot_progress = (ProgressBar) listView_footer
				.findViewById(R.id.listview_foot_progress);
		listView = (PullToRefreshListView) findViewById(R.id.listview_deal);

		listView.addFooterView(listView_footer);
		deals = new ArrayList<Deal>();
		adapter = new DealAdapter(MainActivity.this, R.layout.listitem_deal,
				deals);
		listView.setAdapter(adapter);

		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				listView.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (deals.size() == 0)
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(listView_foot_more) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				if (scrollEnd && curLoadState == NOTHING) {
					curLoadState = LOADMORE;
					loadingBar.setVisibility(View.VISIBLE);
					getDeals(handler);
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				listView.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});

		listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {

				curLoadState = REFRESH;
				isRefresh = true;
				loadingBar.setVisibility(View.VISIBLE);
				getDeals(handler);

			}
		});
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					if (curLoadState == REFRESH) {
						listView.onRefreshComplete(getString(R.string.pull_to_refresh_update)
								+ new Date().toLocaleString());
						listView.setSelection(0);
						deals.clear();
					} else if (curLoadState == LOADMORE) {

					}
					
					List<Deal> newDeals=(List<Deal>) msg.obj;
                    if(newDeals.size()>0&&newDeals.size()==AppConfig.pageSize)
                    {
					curLoadState = NOTHING;
					deals.addAll(newDeals);
					adapter.notifyDataSetChanged();
					listView_foot_more.setText(R.string.load_ing);
					listView_foot_progress.setVisibility(View.VISIBLE);
					isNoData=false;
                    }
                    else {
						isNoData=true;
						listView_foot_more.setText(R.string.load_nodata);
						listView_foot_progress.setVisibility(View.GONE);
					}
					

				}
				else if(msg.what==2)
				{
					initPopupHotCity();
				}
				else if (msg.what==3) {
					initPopupHotTag();
				}
				
				loadingBar.setVisibility(View.GONE);
			}
		};
		loadingBar=(ProgressBar)findViewById(R.id.headbar_progressBar);
		loadingBar.setVisibility(View.VISIBLE);
		getDeals(handler);
		getHotCities(handler);
		getHotTags(handler);

		if (!((AppContext) getApplication()).isNetworkConnected()) {
			Toast.makeText(this, R.string.network_not_connected,
					Toast.LENGTH_SHORT).show();
		}
		
	   hotTagsBtn = (Button) findViewById(R.id.btn_hottags);
	   String string=AppConfig.getString(AppConfig.KEY_TAG, this);
	   if(!string.isEmpty())
	   {
		   hotTagsBtn.setText(string);
	   }
	   hotCitiesBtn = (Button) findViewById(R.id.btn_hotcities);
	   string=AppConfig.getString(AppConfig.KEY_CITY, this);
	   if(!string.isEmpty())
	   {
		   hotCitiesBtn.setText(string);
	   }
	   searchButton=(Button) findViewById(R.id.search_btn);
	   searchTextView=(TextView)findViewById(R.id.search_editer);
	   string=AppConfig.getString(AppConfig.KEY_SEARCH, this);
	   if(!string.isEmpty())
	   {
		   searchTextView.setText(string);
	   }
	   
	   searchButton.setOnClickListener(new OnClickListener() {
		
		public void onClick(View arg0) {
            if(!searchTextView.getText().toString().isEmpty())
            {
            	AppConfig.setString(AppConfig.KEY_SEARCH, searchTextView.getText().toString(), MainActivity.this);
            }
            else {
            	AppConfig.removeString(AppConfig.KEY_SEARCH, MainActivity.this);
			}
            listView.requestFocusFromTouch();
			    listView.setSelection(0);
			    curLoadState=REFRESH;
				loadingBar.setVisibility(View.VISIBLE);
			    getDeals(handler);
		}
	});
	   listView.setOnItemClickListener(new OnItemClickListener() {

		public void onItemClick(AdapterView<?> container, View view, int arg2,
				long arg3) {
            final Deal deal=(Deal)view.getTag();
            Dialog dialog= DialogTool.createConfirmDialog(MainActivity.this, "提示", "是否打开网站?", "确定", "取消",new android.content.DialogInterface.OnClickListener(){


				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					IntentUtils.startBrowser(MainActivity.this,deal.getSiteUrl());
				}
            	
            }, new android.content.DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}}, 0);
            dialog.show();
		}
	});
	}
    
	private void initPopupHotTag() {
		LayoutInflater inflater = LayoutInflater.from(this);

		View view = inflater.inflate(R.layout.hot_tag, null);
		ListView hotListView=(ListView)view.findViewById(R.id.hottag_listview);
		TagAdapter adapter=new TagAdapter(this,R.layout.listitem_tag,hotTags);
		TextView footTextView=new TextView(this);
		footTextView.setText("更多标签");
		footTextView.setGravity(Gravity.CENTER);
		hotListView.addFooterView(footTextView);
		footTextView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent=new Intent(MainActivity.this,QuickLocationMainActivity.class);
				tags.remove(0);
				String strings[]=new String[tags.size()];
				String strings1[]=new String[tags.size()];
				for(int i=0,j=tags.size();i<j;i++){
				  strings[i]=tags.get(i).getName();
				  strings1[i]=tags.get(i).getCnSpell();
				}
				intent.putExtra("LIST",strings);
				intent.putExtra("PINYIN", strings1);
				MainActivity.this.startActivityForResult(intent, 1);
				tagPopupWindow.dismiss();
			}
		});
		hotListView.setAdapter(adapter);
		tagPopupWindow = new PopupWindow(view,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);

		
		
		tagPopupWindow.setWidth(hotTagsBtn.getWidth());
		// 需要设置一下此参数，点击外边可消失

		tagPopupWindow.setBackgroundDrawable(new BitmapDrawable());

		// 设置点击窗口外边窗口消失

		tagPopupWindow.setOutsideTouchable(true);

		// 设置此参数获得焦点，否则无法点击

		tagPopupWindow.setFocusable(true);

		hotTagsBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (tagPopupWindow.isShowing()) {

					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏

					tagPopupWindow.dismiss();

				} else {

					// 显示窗口
					int[] location = new int[2];
					v.getLocationOnScreen(location);

					tagPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0],
							location[1] - tagPopupWindow.getHeight()-hotTagsBtn.getHeight());

				}

			}

		});
		
		hotListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			    TextView textView=(TextView)arg1.findViewById(R.id.tag_name);
			    AppConfig.setString(AppConfig.KEY_TAG, (String) textView.getText(),MainActivity.this);
			    tagPopupWindow.dismiss();
			    hotTagsBtn.setText(textView.getText());
			    listView.requestFocusFromTouch();
			    listView.setSelection(0);
			    curLoadState=REFRESH;
				loadingBar.setVisibility(View.VISIBLE);
			    getDeals(handler);
			}
			
		});

	}
	
	private void initPopupHotCity() {
		LayoutInflater inflater = LayoutInflater.from(this);

		View view = inflater.inflate(R.layout.hot_city, null);
		ListView hotListView=(ListView)view.findViewById(R.id.hotcity_listview);
		CityAdapter adapter=new CityAdapter(this,R.layout.listitem_city,hotCities);
		TextView footTextView=new TextView(this);
		footTextView.setText("更多城市");
		footTextView.setGravity(Gravity.CENTER);
		hotListView.addFooterView(footTextView);
		
		footTextView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent=new Intent(MainActivity.this,QuickLocationMainActivity.class);
				String strings[]=new String[cities.size()];
				String strings1[]=new String[cities.size()];
				for(int i=0,j=cities.size();i<j;i++){
				  strings[i]=cities.get(i).getName();
				  strings1[i]=cities.get(i).getCnSpell();
				}
				intent.putExtra("LIST",strings);
				intent.putExtra("PINYIN", strings1);
				MainActivity.this.startActivityForResult(intent, 2);
				cityPopupWindow.dismiss();
			}
		});
		
		hotListView.setAdapter(adapter);

		cityPopupWindow = new PopupWindow(view,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);

		
		cityPopupWindow.setWidth(hotCitiesBtn.getWidth());
		// 需要设置一下此参数，点击外边可消失
		cityPopupWindow.setBackgroundDrawable(new BitmapDrawable());

		// 设置点击窗口外边窗口消失

		cityPopupWindow.setOutsideTouchable(true);

		// 设置此参数获得焦点，否则无法点击

		cityPopupWindow.setFocusable(true);

		hotCitiesBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (cityPopupWindow.isShowing()) {

					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏

					cityPopupWindow.dismiss();

				} else {

					// 显示窗口
					int[] location = new int[2];
					v.getLocationOnScreen(location);


					cityPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0],
							location[1] - cityPopupWindow.getHeight()-hotCitiesBtn.getHeight());

				}

			}

		});
		hotListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			    TextView textView=(TextView)arg1.findViewById(R.id.city_name);
			    AppConfig.setString(AppConfig.KEY_CITY, (String) textView.getText(),MainActivity.this);
			    cityPopupWindow.dismiss();
			    hotCitiesBtn.setText(textView.getText());
			    listView.requestFocusFromTouch();
			    listView.setSelection(0);
			    curLoadState=REFRESH;
				loadingBar.setVisibility(View.VISIBLE);
			    getDeals(handler);
			}
			
		});
	}
	
	private void getHotCities(final Handler handler) {
		new Thread() {
			public void run() {

				try {
					Message msg = new Message();
					msg.what = 2;
					Looper.prepare();//
					hotCities= ((AppContext) getApplication()).getHotCities();
					cities=((AppContext)getApplication()).getCities();
					handler.sendMessage(msg);
				} catch (AppException e) {
					e.makeToast(MainActivity.this);
					e.printStackTrace();
				}
			}
		}.start();
	
	}
	
	private void getHotTags(final Handler handler) {
		new Thread() {
			public void run() {

				try {
					Message msg = new Message();
					msg.what = 3;
					Looper.prepare();//
					hotTags= ((AppContext) getApplication()).getHotTags();
					tags=((AppContext)getApplication()).getTags();
					handler.sendMessage(msg);
				} catch (AppException e) {
					e.makeToast(MainActivity.this);
					e.printStackTrace();
				}
			}
		}.start();
	
	}

	private void getDeals(final Handler handler) {
		if(!isNoData||curLoadState == REFRESH)
		{
			new Thread() {
				public void run() {
	                   
					try {
						
						Message msg = new Message();
						msg.what = 1;
						Looper.prepare();//
						int page = 1;
						if (curLoadState != REFRESH) {
							page = deals.size() / AppConfig.pageSize + 1;
						}
	                    String tag=AppConfig.getString(AppConfig.KEY_TAG,MainActivity.this);
	                    String search=AppConfig.getString(AppConfig.KEY_SEARCH,MainActivity.this);
	                    String city=AppConfig.getString(AppConfig.KEY_CITY,MainActivity.this);
						msg.obj = ((AppContext) getApplication()).getDeals(page,
								AppConfig.pageSize, search, tag, city,isRefresh);
						handler.sendMessage(msg);
					} catch (AppException e) {
						e.makeToast(MainActivity.this);
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		if(requestCode==1&& resultCode==1)
		{
			String tag =data.getStringExtra("NAME");
			 AppConfig.setString(AppConfig.KEY_TAG,tag,MainActivity.this);
			 final Button btn = (Button) findViewById(R.id.btn_hottags);
			    btn.setText(tag);
			    listView.requestFocusFromTouch();
			    listView.setSelection(0);
			    curLoadState=REFRESH;
				loadingBar.setVisibility(View.VISIBLE);
			    getDeals(handler);
		}
		else if(requestCode==2&& resultCode==1){
			String city =data.getStringExtra("NAME");
			 AppConfig.setString(AppConfig.KEY_CITY,city,MainActivity.this);
			 final Button btn = (Button) findViewById(R.id.btn_hotcities);
			    btn.setText(city);
			    listView.requestFocusFromTouch();
			    listView.setSelection(0);
			    curLoadState=REFRESH;
				loadingBar.setVisibility(View.VISIBLE);
			    getDeals(handler);
		}
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,0,0,"关于");
		menu.add(1,1,1,"清除缓存("+((AppContext)getApplication()).getCacheSize(this)+")");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
	    if(item.getGroupId()==0)
	    {
	    	startActivity(new Intent(this, AboutActivity.class));
	    }
	    else if(item.getGroupId()==1)
	    {
	    	final AppContext ac = (AppContext)getApplication();
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
					    item.setTitle("清除缓存(0KB)");
						Toast.makeText(MainActivity.this, "缓存清除成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(MainActivity.this, "缓存清除失败", Toast.LENGTH_SHORT).show();
					}
				}
			};
			new Thread() {
				public void run() {
					Message msg = new Message();
					try {
						ac.clearAppCache();
						msg.what = 1;
					} catch (Exception e) {
						e.printStackTrace();
						msg.what = -1;
					}
					handler.sendMessage(msg);
				}
			}.start();
	    }
	    return true;
	}
}
