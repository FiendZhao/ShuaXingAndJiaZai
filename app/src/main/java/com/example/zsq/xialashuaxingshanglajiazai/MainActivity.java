package com.example.zsq.xialashuaxingshanglajiazai;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.example.zsq.xialashuaxingshanglajiazai.PullDownView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements PullDownView.OnPullDownListener,AdapterView.OnItemClickListener {
    /**
     * Called when the activity is first created.
     */
    private static final int WHAT_DID_LOAD_DATA = 0;    // Handler what 数据加载完毕
    private static final int WHAT_ON_REFRESH = 1;       // Handler what 刷新中
    private static final int WHAT_DID_MORE = 2;
    private ListView mListView;
    private ArrayList<String> mStrings = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private PullDownView mPullDownView;

    // Handler what 已经获取完更多
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 1、使用PullDownView
         * 2、设置OnPullDownListener
         * 3、从mPullDownView里获取ListView
         */
        mPullDownView = (PullDownView) findViewById(R.id.pull_down_view);
        mPullDownView.setOnPullDownListener(this);
        mListView = mPullDownView.getListView();

        mListView.setOnItemClickListener(this);
        mAdapter = new ArrayAdapter<String>(this, R.layout.pulldown_item, mStrings);
        mListView.setAdapter(mAdapter);

        mPullDownView.enableAutoFetchMore(true, 1);

        loadData();
    }

    private void loadData()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<String> strings = new ArrayList<>();
                for (String body:
                        mStringArray) {
                    strings.add(body);
                }
                Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
                msg.obj = strings;
                msg.sendToTarget();
            }
        }).start();
    }
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = mUIHandler.obtainMessage(WHAT_ON_REFRESH);
                msg.obj = "刷新之后的数据"+System.currentTimeMillis();
                msg.sendToTarget();
            }
        }).start();
    }
    private Handler mUIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case WHAT_DID_LOAD_DATA:{
                    if (msg.obj!=null)
                    {
                        List<String> strings = (List<String>) msg.obj;
                        if (!strings.isEmpty())
                        {
                            //将新的数据加入到Strings中
                            mStrings.addAll(strings);
                            //通知ListView更新
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    //停止刷新，告诉他数据加载完毕
                    mPullDownView.notifyDidLoad();
                    break;
                }
                case WHAT_DID_MORE:{
                    String body = (String) msg.obj;
                    mStrings.add(body);
                    mAdapter.notifyDataSetChanged();
                    //告诉他获取更多完毕
                    mPullDownView.notifyDidMore();
                    break;
                }
                case WHAT_ON_REFRESH:{
                    String body = (String) msg.obj;
                    mStrings.add(0,body);
                    mAdapter.notifyDataSetChanged();
                    //告诉他更新完毕
                    mPullDownView.notifyDidRefresh();
                    break;
                }

            }
        }
    };
    @Override
    public void onMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);
                msg.obj = "加载更多"+System.currentTimeMillis();
                msg.sendToTarget();
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this,"啊，我被点啦！",Toast.LENGTH_SHORT).show();
    }
    // 模拟数据
    private String[] mStringArray = {
            "你好", "呵呵", "吃了吗", "嘿嘿", "滚去写代码！",
            "啦啦啦", "走！去开黑！", "好可怕！你好可怕啊！", "糟了。", "好困呀！", "哎，又饿了",
            "开饭啦！", "天好黑", "我好怕", "哎哟", "睡什么睡，起来嗨！"
    };
}
