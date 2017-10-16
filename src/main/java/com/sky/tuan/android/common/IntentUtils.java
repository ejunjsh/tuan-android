package com.sky.tuan.android.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentUtils {
	
   public static void startBrowser(Context context, String url)
   {
	   Intent intent = new Intent();
       intent.setAction("android.intent.action.VIEW");
       Uri content_url = Uri.parse(url);
       intent.setData(content_url);
       context.startActivity(intent);
   }
}
