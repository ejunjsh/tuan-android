package com.sky.tuan.android.activity;

import java.util.List;

import com.sky.tuan.android.R;
import com.sky.tuan.android.adapter.TagAdapter;
import com.sky.tuan.android.bean.Tag;
import com.sky.tuan.android.common.AppContext;
import com.sky.tuan.android.common.AppException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TagActivity extends Activity {
	
	private Handler handler;
	private ProgressBar loadingBar;
	private List<Tag> tags;
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag);
		loadingBar=(ProgressBar)findViewById(R.id.headbar_progressBar_tag);
		listView=(ListView)findViewById(R.id.tag_listview);
		handler=new Handler(){
			public void handleMessage(Message msg)
			{
				if(msg.what==1)
				{
					loadingBar.setVisibility(View.GONE);
					TagAdapter adapter=new TagAdapter(TagActivity.this,R.layout.listitem_tag, tags);
					listView.setAdapter(adapter);
				}
			}
		};
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
                 TextView textView=(TextView)arg1.findViewById(R.id.tag_name);
                 if(textView!=null)
                 {
                	 Intent intent=TagActivity.this.getIntent();
                	 intent.putExtra("TAGNAME", textView.getText());
                	 TagActivity.this.setResult(1,intent);
                	 TagActivity.this.finish();
                 }
			}
		});
		
		loadingBar.setVisibility(View.VISIBLE);
		loadTags();
	}
	
	private void loadTags()
	{
		new Thread(new Runnable() {
			public void run() {
				try {
					tags=((AppContext)getApplication()).getTags();
				} catch (AppException e) {
					e.printStackTrace();
					e.makeToast(TagActivity.this);
				}
				Message msg=new Message();
				msg.what=1;
				handler.sendMessage(msg);
			}
	   }).start();
	}
}
