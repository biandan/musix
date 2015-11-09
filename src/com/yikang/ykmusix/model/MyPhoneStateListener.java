package com.yikang.ykmusix.model;


import com.yikang.ykmusix.MusicPlayerService;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * 电话状态监听，当有电话打进来时，以便暂停播放
 * @author Administrator
 *
 */
public class MyPhoneStateListener extends PhoneStateListener {
	String TAG = "MyPhoneStateListener";
	Context context;
	MyPhoneStateListener listener ;
	
	
	public MyPhoneStateListener(Context context) {
		this.context = context;
		listener=this ;
	}

	
	public void startLisentToPhoneState() {
		TelephonyManager tManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
				tManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		System.out
				.println(">>>>CenterProcessUnitService———>ProcessBroadcastReceiver———>onReceive————>onCallStateChanged");
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:// 空闲

			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:// 摘机

			if(MusicPlayerService.getCurPlayingStata()){
				MusicPlayerService.Pause();
			}
			
			Log.v(TAG, "TelephonyManager.CALL_STATE_OFFHOOK");
			break;
		case TelephonyManager.CALL_STATE_RINGING:// 来电
		
			if(MusicPlayerService.getCurPlayingStata()){
				MusicPlayerService.Pause();
			}
			
			 
			break;
		default:
			System.out.println("模拟电话进行测试");
			break;
		}

		super.onCallStateChanged(state, incomingNumber);
	}

}
