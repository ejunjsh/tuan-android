package com.sky.tuan.android.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.sky.tuan.android.R;
import com.sky.tuan.android.adapter.QuickLocationListAdapter;
import com.sky.tuan.android.tag.QuickLocationRightTool;
import com.sky.tuan.android.tag.QuickLocationRightTool.OnTouchingLetterChangedListener;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zuowen
 * @time 2013.05.08
 * @email 782287169@qq.com
 * <p/>
 * 混合字符串处理  中英文排序   右侧悬浮栏   顶部名称提示栏   滚动悬浮提示
 */
public class QuickLocationMainActivity extends Activity implements ListView.OnScrollListener,
        OnItemClickListener, android.view.View.OnClickListener
{
    private QuickLocationRightTool letterListView;
    private Handler handler;
    private DisapearThread disapearThread;
    private int scrollState;
    private QuickLocationListAdapter quickLocationListAdapter;
    private ListView listMain;
    private TextView txtOverlay, title;
    private WindowManager windowManager;

    private String[] stringArr ;

    private String[] stringArr3 = new String[0];
    private ArrayList arrayList = new ArrayList();
    private ArrayList arrayList2 = new ArrayList();
    private ArrayList arrayList3 = new ArrayList();
    private Map<String, String> map = new HashMap<String, String>();


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_location_list);
        
        stringArr=getIntent().getStringArrayExtra("LIST");
        String[] pinArr=getIntent().getStringArrayExtra("PINYIN");

        for (int i = 0; i < stringArr.length; i++) {
            String pinyin = pinArr[i];
            arrayList.add(pinyin);                                 //此列表增加拼音
           
            if (!arrayList2.contains(pinyin.substring(0, 1).toUpperCase()) && isWord(pinyin.substring(0, 1).toUpperCase())) {
                arrayList2.add(pinyin.substring(0, 1).toUpperCase());        //此列表添加拼音首字母
               
            }
            map.put(pinyin, stringArr[i]);
        }
        Collections.sort(arrayList, new MixComparator());
        Collections.sort(arrayList2, new MixComparator());
        stringArr = (String[]) arrayList.toArray(stringArr);

        arrayList3.add("#");                                     //此列表添加不规则字符
        for (int i = 0; i < arrayList2.size(); i++) {
            String string = (String) arrayList2.get(i);
            arrayList3.add(string.toUpperCase());       //toUpperCase大写字母
        }
        arrayList3.add("*");

        stringArr3 = (String[]) arrayList3.toArray(stringArr3); // 得到右侧英文字母列表
        letterListView = (QuickLocationRightTool) findViewById(R.id.rightCharacterListView);
        letterListView.setB(stringArr3);
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());

        textOverlayout();

        // 初始化ListAdapter
        quickLocationListAdapter = new QuickLocationListAdapter(this, stringArr, this, map);
        listMain = (ListView) findViewById(R.id.listInfo);
        listMain.setOnItemClickListener(this);
        listMain.setOnScrollListener(this);
        listMain.setAdapter(quickLocationListAdapter);
        disapearThread = new DisapearThread();
    }

    /**
     * 滚到悬浮字母
     */
    public void textOverlayout()
    {
        handler = new Handler();
        //顶部悬浮
        title = (TextView) findViewById(R.id.list_title);
        // 初始化首字母悬浮提示框
        txtOverlay = (TextView) LayoutInflater.from(this).inflate(
                R.layout.popup_char, null);
        txtOverlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(txtOverlay, lp);
    }

    /**
     * 右侧导航条点击列表滚动指定位置
     */
    public class LetterListViewListener implements
            OnTouchingLetterChangedListener
    {
        public void onTouchingLetterChanged(final String s)
        {
            int num = 0;
            for (int i = 0; i < stringArr.length; i++) {
                if ("A".equals(s) || "#".equals(s)) {      //顶部
                    num = 0;
                    break;
                }
                else if ("*".equals(s)) {                      //底部
                    num = stringArr.length;
                    break;
                }
                else if (isWord(stringArr[i].substring(0, 1)) && (character2ASCII(stringArr[i].substring(0, 1).toUpperCase()) < (character2ASCII(s.toUpperCase())))) {
                    num += 1;                                     //首先判断是字母，字母的ascll值小于s是，滚动位置+1；如果有10个数据小于s，就滚到10处
                }

            }
            if (num < 2) {
                listMain.setSelectionFromTop(num, 0);
            }
            else {
                listMain.setSelectionFromTop(num, 5);    //留点间隔
            }
        }
    }

    /**
     * 滚动处理
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount)
    {
        title.setVisibility(View.VISIBLE);
        if (firstVisibleItem != 0) {
            title.setText(map.get(stringArr[firstVisibleItem]));
        }
        else {
            title.setText("a");
        }
        title.setText(map.get(stringArr[firstVisibleItem]));
        txtOverlay.setText(String.valueOf(stringArr[firstVisibleItem].toUpperCase().charAt(0)));// 泡泡文字以第一个可见列表为准

    }

    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        this.scrollState = scrollState;
        if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
            handler.removeCallbacks(disapearThread);
            // 提示延迟1.0s再消失
            boolean bool = handler.postDelayed(disapearThread, 1000);
        }
        else {
            txtOverlay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 列表点击
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id)
    {
        String name = map.get(stringArr[position]);
        Intent intent=QuickLocationMainActivity.this.getIntent();
   	    intent.putExtra("NAME", name);
   	    QuickLocationMainActivity.this.setResult(1,intent);
   	    QuickLocationMainActivity.this.finish();
    }

    public void onClick(View view)
    {

    }

    private class DisapearThread implements Runnable
    {
        public void run()
        {
            // 避免在1.5s内，用户再次拖动时提示框又执行隐藏命令。
            if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
                txtOverlay.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        txtOverlay.setVisibility(View.INVISIBLE);
        title.setVisibility(View.INVISIBLE);
        windowManager.removeView(txtOverlay);
    }


  
    /**
     * 把单个英文字母或者字符串转换成数字ASCII码
     *
     * @param input
     * @return
     */
    public static int character2ASCII(String input)
    {
        char[] temp = input.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char each : temp) {
            builder.append((int) each);
        }
        String result = builder.toString();
        return Integer.parseInt(result);
    }

    /**
     * 混合排序工具
     */
    public class MixComparator implements Comparator<String>
    {
        public int compare(String o1, String o2)
        {
            // 判断是否为空""
            if (isEmpty(o1) && isEmpty(o2))
                return 0;
            if (isEmpty(o1))
                return -1;
            if (isEmpty(o2))
                return 1;
            String str1 = "";
            String str2 = "";
            try {
                str1 = (o1.toUpperCase()).substring(0, 1);
                str2 = (o2.toUpperCase()).substring(0, 1);
            }
            catch (Exception e) {
                System.out.println("某个str为\" \" 空");
            }
            if (isWord(str1) && isWord(str2)) {               //字母
                return str1.compareTo(str2);
            }
            else if (isNumeric(str1) && isWord(str2)) {     //数字字母
                return 1;
            }
            else if (isNumeric(str2) && isWord(str1)) {
                return -1;
            }
            else if (isNumeric(str1) && isNumeric(str2)) {       //数字数字
                if (Integer.parseInt(str1) > Integer.parseInt(str2)) {
                    return 1;
                }
                else {
                    return -1;
                }
            }
            else if (isAllWord(str1) && (!isAllWord(str2))) {      //数字字母  其他字符
                return -1;
            }
            else if ((!isAllWord(str1)) && isWord(str2)) {
                return 1;
            }
            else {
                return 1;
            }
        }
    }

    /**
     * 判断空
     *
     * @param str
     * @return
     */
    private boolean isEmpty(String str)
    {
        return "".equals(str.trim());
    }

    /**
     * 判断数字
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str)
    {
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 判读字母
     *
     * @param str
     * @return
     */
    public boolean isWord(String str)
    {
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 判断字母数字混合
     *
     * @param str
     * @return
     */
    public boolean isAllWord(String str)
    {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        else {
            return true;
        }
    }

}