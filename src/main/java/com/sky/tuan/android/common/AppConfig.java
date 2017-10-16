package com.sky.tuan.android.common;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppConfig {
    public final static String CONFIG="TUAN";
    public final static int pageSize=20;
    public final static String HOST = "192.168.1.101";//192.168.1.213  
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	
	public final static String URL_SPLITTER = "/";
	
	public final static String URL_UNDERLINE = "_";
	
	public final static String URL_REQUEST_DEAL=HTTP+HOST+URL_SPLITTER+"api/deals.xml";
	
	public final static String URL_REQUEST_CITY=HTTP+HOST+URL_SPLITTER+"api/cities.xml";
	
	public final static String URL_REQUEST_TAG=HTTP+HOST+URL_SPLITTER+"api/tags.xml";
	
	public final static String URL_REQUEST_HOTTAG=HTTP+HOST+URL_SPLITTER+"api/hotTags.xml";
	
	public final static String URL_REQUEST_HOTCITY=HTTP+HOST+URL_SPLITTER+"api/hotCities.xml";
	
	public final static String KEY_TAG="KEYTAG";
	
	public final static String KEY_CITY="KEYCITY";
	
	public final static String KEY_SEARCH="KEYSEARCH";
	
    public static void setString(String key,String str,Context context)
    {
    	SharedPreferences pref=context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
    	Editor editor=pref.edit();
    	editor.putString(key, str);
    	
    	editor.commit();
    }
    
    public static void removeString(String key,Context context)
    {
    	SharedPreferences pref=context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
    	Editor editor=pref.edit();
    	editor.remove(key);
    	
    	editor.commit();
    }
    
    public static String getString(String key,Context context)
    {
    	SharedPreferences pref=context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
    	return pref.getString(key, "");
    }
    
   
}
