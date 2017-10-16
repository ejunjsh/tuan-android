package com.sky.tuan.android.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sky.tuan.android.bean.City;
import com.sky.tuan.android.bean.Deal;
import com.sky.tuan.android.bean.Tag;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.CacheManager;

public class AppContext extends Application{
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	
	public static final int PAGE_SIZE = 20;//默认分页大小
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!StringUtils.isEmpty(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	
	/**
	 * 获取App安装包信息
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}
	
	/**
	 * 获取App唯一标识
	 * @return
	 */
	public String getAppId() {
		String uniqueID = AppConfig.getString("UniqueID",this);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			AppConfig.setString("UniqueID", uniqueID, this);
		}
		return uniqueID;
	}
	
	@SuppressWarnings("unchecked")
	public  List<Deal> getDeals(int pageIndex,int pageSize,String key,String tag,String city,boolean isRefresh)throws AppException
	{
		List<Deal> list = null;
		String cacheKey = "deals_"+city+"_"+tag+"_"+key+"_"+pageIndex+"_"+pageSize; 
		if(isNetworkConnected() && (!isReadDataCache(cacheKey) || isRefresh)) {
			try{
				list = ApiClient.getDeals(this, pageIndex, pageSize, key, tag,city);
				if(list != null&&list.size()>0){
					saveObject((Serializable) list, cacheKey);
				}
			}catch(AppException e){
				list = (List<Deal>)readObject(cacheKey);
				if(list == null)
					throw e;
			}
		} else {
			list = (List<Deal>)readObject(cacheKey);
			if(list == null)
				list = new ArrayList<Deal>();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public  List<Tag> getTags()throws AppException
	{
		List<Tag> list = null;
		String cacheKey = "tags"; 
		if(isNetworkConnected()) {
			try{
				list = ApiClient.getTags(this);
				if(list != null&&list.size()>0){
					saveObject((Serializable) list, cacheKey);
				}
			}catch(AppException e){
				list = (List<Tag>)readObject(cacheKey);
				if(list == null)
					throw e;
			}
		} else {
			list = (List<Tag>)readObject(cacheKey);
			if(list == null)
				list = new ArrayList<Tag>();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public  List<Tag> getHotTags()throws AppException
	{
		List<Tag> list = null;
		String cacheKey = "hotTags"; 
		if(isNetworkConnected()) {
			try{
				list = ApiClient.getHotTags(this);
				if(list != null&&list.size()>0){
					saveObject((Serializable) list, cacheKey);
				}
			}catch(AppException e){
				list = (List<Tag>)readObject(cacheKey);
				if(list == null)
					throw e;
			}
		} else {
			list = (List<Tag>)readObject(cacheKey);
			if(list == null)
				list = new ArrayList<Tag>();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public  List<City> getCities()throws AppException
	{
		List<City> list = null;
		String cacheKey = "cities"; 
		if(isNetworkConnected()) {
			try{
				list = ApiClient.getCities(this);
				if(list != null&&list.size()>0){
					saveObject((Serializable) list, cacheKey);
				}
			}catch(AppException e){
				list = (List<City>)readObject(cacheKey);
				if(list == null)
					throw e;
			}
		} else {
			list = (List<City>)readObject(cacheKey);
			if(list == null)
				list = new ArrayList<City>();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public  List<City> getHotCities()throws AppException
	{
		List<City> list = null;
		String cacheKey = "hotCities"; 
		if(isNetworkConnected()) {
			try{
				list = ApiClient.getHotCities(this);
				if(list != null&&list.size()>0){
					saveObject((Serializable) list, cacheKey);
				}
			}catch(AppException e){
				list = (List<City>)readObject(cacheKey);
				if(list == null)
					throw e;
			}
		} else {
			list = (List<City>)readObject(cacheKey);
			if(list == null)
				list = new ArrayList<City>();
		}
		return list;
	}
	
	/**
	 * 保存对象
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try {
				oos.close();
			} catch (Exception e) {}
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 读取对象
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file){
		if(!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable)ois.readObject();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
			//反序列化失败 - 删除缓存文件
			if(e instanceof InvalidClassException){
				File data = getFileStreamPath(file);
				data.delete();
			}
		}finally{
			try {
				ois.close();
			} catch (Exception e) {}
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return null;
	}
	
	/**
	 * 判断缓存数据是否可读
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile)
	{
		return readObject(cachefile) != null;
	}
	
	/**
	 * 判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile)
	{
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * 清除app缓存
	 */
	public void clearAppCache()
	{
		//清除webview缓存
		File file = CacheManager.getCacheFileBaseDir();  
		if (file != null && file.exists() && file.isDirectory()) {  
		    for (File item : file.listFiles()) {  
		    	item.delete();  
		    }  
		    file.delete();  
		}  		  
		deleteDatabase("webview.db");  
		deleteDatabase("webview.db-shm");  
		deleteDatabase("webview.db-wal");  
		deleteDatabase("webviewCache.db");  
		deleteDatabase("webviewCache.db-shm");  
		deleteDatabase("webviewCache.db-wal");  
		//清除数据缓存
		clearCacheFolder(getFilesDir(),System.currentTimeMillis());
		clearCacheFolder(getCacheDir(),System.currentTimeMillis());
		//2.2版本才有将应用缓存转移到sd卡的功能
		if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),System.currentTimeMillis());
		}
	}	
	
	/**
	 * 清除缓存目录
	 * @param dir 目录
	 * @param numDays 当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {          
	    int deletedFiles = 0;         
	    if (dir!= null && dir.isDirectory()) {             
	        try {                
	            for (File child:dir.listFiles()) {    
	                if (child.isDirectory()) {              
	                    deletedFiles += clearCacheFolder(child, curTime);          
	                }  
	                if (child.lastModified() < curTime) {     
	                    if (child.delete()) {                   
	                        deletedFiles++;           
	                    }    
	                }    
	            }             
	        } catch(Exception e) {       
	            e.printStackTrace();    
	        }     
	    }       
	    return deletedFiles;     
	}
	
	 public String getCacheSize(Context context)
	    {
	    	// 计算缓存大小
	    			long fileSize = 0;
	    			String cacheSize = "0KB";
	    			File filesDir = context.getFilesDir();
	    			File cacheDir = context.getCacheDir();

	    			fileSize += FileUtils.getDirSize(filesDir);
	    			fileSize += FileUtils.getDirSize(cacheDir);
	    			// 2.2版本才有将应用缓存转移到sd卡的功能
	    			if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
	    				File externalCacheDir = MethodsCompat.getExternalCacheDir(context);
	    				fileSize += FileUtils.getDirSize(externalCacheDir);
	    			}
	    			if (fileSize > 0)
	    				cacheSize = FileUtils.formatFileSize(fileSize);
	    			return cacheSize;

	    }
}
