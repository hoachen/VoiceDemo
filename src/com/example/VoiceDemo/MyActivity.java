package com.example.VoiceDemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private TextView textView;
    private MyHandler mHandler;
    private MyCoreService.MyBinder binder;
    private MyConnection conn = new MyConnection();
    private Intent intent;

    static class MyHandler extends Handler {
        WeakReference<MyActivity> theRef = null;

        public MyHandler(MyActivity activity) {
            this.theRef = new WeakReference<MyActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MyActivity theActivity = theRef.get();
            if (theActivity == null) {
                return;
            }
            int what = msg.what;
            switch (what) {
                case MyCoreService.WHAT_CALL_INCOMIING:
                    theActivity.textView.setText("有电话来了，接还是不接？");
                    break;
                case MyCoreService.WHAT_CALL_STATU_ONLINE:
                    theActivity.textView.setText("您当前在线");
                    break;
                case MyCoreService.WHAT_CALL_STATU_OFFLINE:
                    theActivity.textView.setText("您当前离线");
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.tv_status);
        mHandler = new MyHandler(this);
        intent = new Intent(this, MyCoreService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                break;
            case R.id.button2:
                break;
            case R.id.button3:
                break;
            case R.id.button4:
                break;

        }

    }

    @Override
    public void finish() {
        super.finish();
    }

    class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MyCoreService.MyBinder) service;
            binder.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    }
}
