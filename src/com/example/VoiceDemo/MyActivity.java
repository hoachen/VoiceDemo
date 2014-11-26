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
import com.yzx.listenerInterface.UcsReason;

import java.lang.ref.WeakReference;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private TextView textView;
    private MyHandler mHandler;
    private MyCoreService.MyBinder binder;
    private MyConnection conn = new MyConnection();
    private String callId;
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
                    if(msg.obj instanceof  String)
                    theActivity.callId= (String) msg.obj;
                    break;
                case MyCoreService.WHAT_CALL_STATU_ONLINE:
                    theActivity.textView.setText("您当前在线");
                    break;
                case MyCoreService.WHAT_CALL_STATU_OFFLINE:
                    theActivity.textView.setText("您当前离线");
                    break;
                case MyCoreService.WHAT_DIAL_FAILED:
                    if(msg.obj instanceof  UcsReason) {
                        UcsReason reason = (UcsReason) msg.obj;
                        theActivity.textView.setText("msg:"+reason.getMsg()+"reason:"+reason.getReason());
                    }
                    break;
                case MyCoreService.WHAT_ON_ALERTING:
                    theActivity.textView.setText("对方正在响铃...");
                    break;
                case MyCoreService.WHAT_HANG_UP:
                    if(theActivity.callId!=null)
                    {
                        theActivity.textView.setText("已挂断");}
                    else {
                        theActivity.textView.setText("对方拒接..Mother egg..");
                    }
                    break;
                case MyCoreService.WHAT_ON_ANSWER:
                    theActivity.textView.setText("和某傻逼正在通话中...");
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
        bindService(new Intent(this, MyCoreService.class), conn, BIND_AUTO_CREATE);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:   //播放電話
                if(binder==null)
                    return;
                binder.call(Contants.CLIENT_NUMBER1);
                break;
            case R.id.button2:    //接聽
                if(binder!=null&&callId!=null) {
                    binder.answer(callId);
                }
                break;
            case R.id.button3:   //挂断
                if(binder!=null&&callId!=null) {
                    binder.hangUp(callId);
                }
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        unbindService(conn);
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
