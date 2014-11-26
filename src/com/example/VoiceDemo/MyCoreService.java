package com.example.VoiceDemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import com.yzx.api.CallType;
import com.yzx.api.UCSCall;
import com.yzx.api.UCSService;
import com.yzx.listenerInterface.CallStateListener;
import com.yzx.listenerInterface.ConnectionListener;
import com.yzx.listenerInterface.UcsReason;

import java.util.List;

/**
 * Created by ChenHao on 2014-11-25 上午9:58.
 *
 * @modify:
 */
public class MyCoreService extends Service implements CallStateListener {
    private MyBinder myBinder = new MyBinder();
    private MyActivity.MyHandler mHandler;
    public static final int WHAT_CALL_STATU_ONLINE = 1000;
    public static final int WHAT_CALL_STATU_OFFLINE = 2000;
    public static final int WHAT_CALL_INCOMIING = 3000;
    public static final int WHAT_DIAL_FAILED = 4000;
    public static final  int WHAT_ON_ALERTING=5000;
    public static final int WHAT_HANG_UP=6000;
    public static final int WHAT_ON_ANSWER=7000;
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UCSService.init(getApplicationContext(), true);
        UCSService.addConnectionListener(new ConnectionListener() {
            @Override
            public void onConnectionSuccessful() {
                Trace.i("onConnectionSuccessful");
                mHandler.sendEmptyMessage(WHAT_CALL_STATU_ONLINE);
            }

            @Override
            public void onConnectionFailed(UcsReason ucsReason) {
                Trace.i("onConnectionFailed");
                mHandler.sendEmptyMessage(WHAT_CALL_STATU_OFFLINE);
            }
        });
        UCSCall.addCallStateListener(this);
        /**
         * 连接云平台
         * @param accountSid  开发者账户
         * @param accountToken  开发者账户密码
         * @param ClientNumber  Client账号
         * @param ClientPwd  Client账号密码
         * @return void
         */
        UCSService.connect(Contants.ACCOUNT_SID, Contants.ACCOUNT_TOKEN, Contants.CLIENT_NUMBER2, Contants.CLIENT_PWD2);
    }


    /**
     * 回拨状态成功
     *
     * @param
     * @return void
     */

    @Override
    public void onCallBackSuccess() {
        Trace.i("onCallBackSuccess");

    }

    /**
     * 呼叫失败回调
     *
         * @param callid 当前通话id（目前版本可以为空）
     * @param reason 呼叫失败的原因
     * @return void
     */

    @Override
    public void onDialFailed(String callid, UcsReason reason) {
        Trace.i("onDialFailed");
        Message msg=mHandler.obtainMessage();
        msg.obj=reason;
        msg.what=WHAT_DIAL_FAILED;
        mHandler.sendMessage(msg);
    }

    /**
     * 接到呼叫代理
     *
     * @param callid       当前通话id（目前版本可以为空）
     * @param callType     0:表示VOIP语音来电  1：VOIP视频来电
     * @param callerNumber 主叫来电号码，来电号码为手机号码或Client账号
     * @return void
     */

    @Override
    public void onIncomingCall(String callid, String callType, String callerNumber) {
        Trace.i("onIncomingCall");
        UCSCall.startRinging(true);//播放来电铃声
        Message msg = mHandler.obtainMessage();
        msg.what = WHAT_CALL_INCOMIING;
        //StringBuilder sb = new StringBuilder();
        //sb.append("通话id:").append(callid).append("，通话类型：")
         //       .append(callType.equals("0") ? "VoIP语音来电" : "").append(",来电号码：").append(callerNumber);
        msg.obj =callid;
        mHandler.sendMessage(msg);
    }


    /**
     * 呼叫被释放回调
     *
     * @param callid 当前通话id（目前版本可以为空）
     * @param reason 呼叫被释放的原因
     * @return void
     */
    @Override
    public void onHangUp(String callid, UcsReason reason) {
        Trace.i("onHangUp");
        mHandler.sendEmptyMessage(WHAT_HANG_UP);
    }


    /**
     * 呼叫振铃中回调
     *
     * @param callid 当前通话id （目前版本可以为空）
     * @return void
     */


    @Override
    public void onAlerting(String callid) {

        mHandler.sendEmptyMessage(WHAT_ON_ALERTING);

        Trace.i("onAlerting");
    }

    /**
     * 被叫接听回调
     *
     * @param callid 当前通话id （目前版本可以为空）
     * @return void
     */

    @Override
    public void onAnswer(String callid)

    {

        mHandler.sendEmptyMessage(WHAT_ON_ANSWER);
        Trace.i("onAnswer");
    }

    @Override
    public void onConferenceState(String s, List list) {
        Trace.i("onConferenceState");
    }

    @Override
    public void onConferenceModeConvert(String s) {
        Trace.i("onConferenceModeConvert");
    }

    class MyBinder extends Binder {
        public void setHandler(MyActivity.MyHandler handler) {
            mHandler = handler;
        }

        public void call(String calledNumner) {
            Trace.i("calledNumber=="+calledNumner);
            UCSCall.dial(MyCoreService.this, CallType.VOIP, calledNumner);
        }

        public void answer(String callid) {
            UCSCall.answer(callid);
            UCSCall.stopRinging();
        }


        public void hangUp(String callid) {
            UCSCall.hangUp(callid);
            UCSCall.stopRinging();
        }
    }
}
