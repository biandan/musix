package com.yikang.ykmusix.been;

import java.io.Serializable;

import com.yikang.ykmusix.MusicPlayerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class NotifycationReceiver extends BroadcastReceiver implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onReceive(Context context, Intent intent) {/*
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action.equals(ACTION_BUTTON)) {
			// 通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
			int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
			switch (buttonId) {
			case 1:
				Log.d(TAG, "上一首");
				Toast.makeText(getApplicationContext(), "上一首", Toast.LENGTH_SHORT).show();
				break;
			case 2:

				if (MusicPlayerService.getCurPlayingStata()) {
					MusicPlayerService.Pause();
					
				} else {
					MusicPlayerService.Resume();
				}
				MusicPlayerService.setUpdateUI(false);
				Message msg = Message.obtain();
				msg.what = 2002;
			
				handler.sendMessage(msg);
				MusicPlayerService.showButtonNotify();
				Log.d(TAG, "play_status");
				Toast.makeText(getApplicationContext(), "play_status " + getCurPlayingStata(), Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Log.d(TAG, "下一首");
				MusicPlayerService.playNext();
				MusicPlayerService.showButtonNotify();
				Toast.makeText(getApplicationContext(), "下一首", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	*/}
}
