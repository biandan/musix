package com.yikang.ykmusix;

import java.io.Serializable;
import com.yikang.ykmusix.MainMusixActivity.MainMusicHandle;
import com.yikang.ykmusix.been.MusicStaticPool;
import com.yikang.ykmusix.model.LRCParser;
import com.yikang.ykmusix.model.LRCSearch;
import com.yikang.ykmusix.model.MyPhoneStateListener;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * 播放器的后台服务，播放器的核心
 * 
 * @author Administrator
 * 
 */
public class MusicPlayerService extends Service implements Serializable {

	private static final long serialVersionUID = 1L;
	static MediaPlayer mPlayer;
	static MainMusicHandle handler;

	private static boolean isUpdateUI = false;
	private boolean isDestroy = false;
	private static boolean isReadyToPlay = false;

	// private static int CurrenModel = 0;
	private static final int Loop = 0;

	private static final int Random = 1;
	private static final int Single = 2;
	/**
	 * 是否得到歌词
	 */
	static boolean isGetLRC = false;

	private static MusicPlayerService mPlayerService;
	private static Context sContext;
	private UpUIThread thread;

	static boolean isGoingPlayNewSong = false;
	/**
	 * 更新主界面歌词
	 */
	private final static int MSG_UPDATA_LRC_TEXT = 3000;
	private static final String TAG = "ButtonBroadcastReceiver";

	@Override
	public IBinder onBind(Intent intent) {
		if (!isDestroy) {
			handler = (MainMusicHandle) intent.getSerializableExtra("MainMusicHandle");
			thread = new UpUIThread();
			thread.start();
		}

		return new MusicPlayerBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return super.onStartCommand(intent, flags, startId);

	}

	/**
	 * 这个线程做了很多事情，界面更新以及下载歌词 这里有个bug，下载歌词应带在另一线程下载，否则将出现界面更新阻塞
	 * 
	 * @author Administrator
	 * 
	 */
	class UpUIThread extends Thread {

		@SuppressWarnings("static-access")
		@Override
		public void run() {
			super.run();
			if (mPlayer == null) {
				return;
			}

			System.out.println("UpUIThread run");
			while (!isDestroy) {
				// System.out.println("UpUIThread run while");
				try {

					Thread.currentThread().sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (isUpdateUI() && isReadyToPlay) {
					// System.out.println("UpUIThread run while isUpdateUI "+isUpdateUI());
					String mMusicCurTimeLen;
					String mMusicTimeLen;
					long mProgress = 0;

					long musicTimeLen = mPlayer.getDuration();
					long currentTime = mPlayer.getCurrentPosition();
					long MUSICtime = musicTimeLen;
					musicTimeLen /= 1000;
					// 求分
					long minute = musicTimeLen / 60;
					// 求秒
					long second = musicTimeLen % 60;
					minute %= 60;
					mMusicTimeLen = String.format("%02d:%02d", minute, second);
					if (MUSICtime != 0)
						mProgress = (int) ((currentTime) * ((1000.0) / MUSICtime));
					currentTime /= 1000;
					// 求分
					long currentMinute = currentTime / 60;
					// 求秒
					long currentsecond = currentTime % 60;

					mMusicCurTimeLen = String.format("%02d:%02d", currentMinute, currentsecond);

					Message msg = Message.obtain();
					msg.what = 2000;
					Bundle data = new Bundle();
					data.putString("mMusicTimeLen", mMusicTimeLen);
					data.putString("mMusicCurTimeLen", mMusicCurTimeLen);
					data.putLong("mProgress", mProgress);
					msg.setData(data);
					handler.sendMessage(msg);

					if (getURL) {
						isGetLRC = getLRC();
						getURL = false;
					} else {
						handler.sendEmptyMessage(MSG_UPDATA_LRC_TEXT);
					}

					// 跟新歌词
					try {
						if (isGoingPlayNewSong) {// 清屏
							handler.sendEmptyMessage(MSG_UPDATA_LRC_TEXT);
						}
						if (updataLRC()) {
							System.out.println("MSG_UPDATA_LRC_TEXT=>>"
									+ MusicStaticPool.getLrInfo().getInfos()
											.get(MusicStaticPool.getLrInfo().getKey()[MusicStaticPool.getLrcIndexShow()]));
							handler.sendEmptyMessage(MSG_UPDATA_LRC_TEXT);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}//

				}

			}

		}// END run（）

		/**
		 * 更新歌词显示的数据
		 * 
		 * @return
		 * @throws Exception
		 */
		boolean updataLRC() throws Exception {
			if (!isGetLRC) {
				return false;
			}

			if (((mPlayer.getCurrentPosition()) + 500 - (Long) MusicStaticPool.getLrInfo().getKey()[MusicStaticPool.getLrcIndexShow()]) > 0) {
				System.out.println("Time: Player" + mPlayer.getCurrentPosition() + " LRC "
						+ MusicStaticPool.getLrInfo().getKey()[MusicStaticPool.getLrcIndexShow()]);

				if (MusicStaticPool.getLrcIndexShow() < MusicStaticPool.getLrInfo().getKey().length - 1) {
					MusicStaticPool.setLrcIndexShow(MusicStaticPool.getLrcIndexShow() + 1);
					// System.out.println(" info.getKey() " +
					// MusicStaticPool.getLrcIndexShow());
					return true;

				}
			}

			return false;

		}

	};

	/**
	 * 获取歌词 获取之后会将歌词信息保存到MusicStaticPool中以便使用
	 * 
	 * @return
	 */
	static boolean getLRC() {
		if (MusicStaticPool.getCurMusicPlaying() != null && MusicStaticPool.getCurMusicPlaying().getTitle() != null) {
			String title = MusicStaticPool.getCurMusicPlaying().getTitle();
			String singer = MusicStaticPool.getCurMusicPlaying().getArtist();
			System.out.println("正在自动下载 LRC " + title);
			LRCParser.LrcInfo lrcInfo = new LRCSearch().loadLRC(title, singer);
			MusicStaticPool.setLrcIndexShow(0); // 从新计数
			if (lrcInfo != null) {
				MusicStaticPool.setLrInfo(lrcInfo);
				return true;
			} else {
				MusicStaticPool.setLrInfo(null);
				return false;

			}
		}
		return false;
	}

	public class MusicPlayerBinder extends Binder {
		public MusicPlayerService getService() {
			return MusicPlayerService.this;
		}
	}

	/** 通知栏按钮广播 */
	public NotifycationReceiver bReceiver;

	public static NotificationManager mNotificationManager;

	@Override
	public void onCreate() {
		super.onCreate();
		mPlayerService = this;
		isDestroy = false;
		sContext = this;
		new MyPhoneStateListener(sContext).startLisentToPhoneState(); // 启动电话监听
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		initButtonReceiver();

		mPlayer = new MediaPlayer();
		mPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Toast.makeText(mPlayerService, "" + extra, Toast.LENGTH_SHORT).show();
				return true;
			}
		});

		/*----------------------------------- 监听----------------------------------*/

		mPlayer.setOnPreparedListener(new OnPreparedListener() {

			public void onPrepared(MediaPlayer mp) {
				isReadyToPlay = true;
			}
		});
		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				setUpdateUI(false);
				isReadyToPlay = false;
				isGoingPlayNewSong = true;
				Message msg = Message.obtain();
				msg.what = 2001;// 当音乐播放完毕后，通知主界面更新
				msg.arg1 = MusicStaticPool.getCurPlayListPS();
				handler.sendMessage(msg);

				if (MusicStaticPool.getCurPlayList() == null || MusicStaticPool.getCurPlayList().size() == 0) {
					return;
				}
				switch (MusicStaticPool.getCurModel()) {
				case Loop:
					if (MusicStaticPool.getCurPlayListPS() + 1 < MusicStaticPool.getCurPlayList().size()) {
						MusicStaticPool.setCurPlayListPS(MusicStaticPool.getCurPlayListPS() + 1);
					} else {
						MusicStaticPool.setCurPlayListPS(0);
					}
					playNext(MusicStaticPool.getCurPlayList().get(MusicStaticPool.getCurPlayListPS()).getUrl());
					break;
				case Random:
					int pstion = (int) ((Math.random()) * (MusicStaticPool.getCurPlayList().size()));

					MusicStaticPool.setCurPlayListPS(pstion);
					playNext(MusicStaticPool.getCurPlayList().get(MusicStaticPool.getCurPlayListPS()).getUrl());
					break;
				case Single:

					playNext(MusicStaticPool.getCurPlayList().get(MusicStaticPool.getCurPlayListPS()).getUrl());

					break;

				default:
					break;
				}

			}
		});

	}

	@Override
	public void onDestroy() {
		System.out.println("MusicService is onDestroy");
		isDestroy = true;
		isReadyToPlay = false;
		while (thread.isAlive()) {

		}
		if (bReceiver != null) {
			unregisterReceiver(bReceiver);
		}
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.release();

		}
		mNotificationManager.cancelAll();

		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0); // 常规java、c#的标准退出法，返回值为0代表正常退出
	}

	// public static void Play() {
	// isReadyToPlay = false;
	// AssetFileDescriptor fileDescriptor = null;
	// mPlayer.reset();
	// try {
	// fileDescriptor = sContext.getAssets().openFd("ten.mp3");
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// }
	// try {
	// mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
	// fileDescriptor.getStartOffset(), fileDescriptor.getLength());
	// } catch (IllegalArgumentException e2) {
	// e2.printStackTrace();
	// } catch (IllegalStateException e2) {
	// e2.printStackTrace();
	// } catch (IOException e2) {
	// e2.printStackTrace();
	// }
	//
	// try {
	// mPlayer.prepare();
	// } catch (IllegalStateException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// mPlayer.start();
	// setUpdateUI(true);
	// showButtonNotify();
	// }

	public static void Pause() {

		if (mPlayer.isPlaying()) {
			mPlayer.pause();
			System.out.println("mPlayer.pause()");
		}
		setUpdateUI(false);
		showButtonNotify();
	}

	public static void Resume() {

		if (!mPlayer.isPlaying()) {
			mPlayer.start();
			System.out.println("mPlayer Resume()");
		} else {
			// Play();
		}
		setUpdateUI(true);
		showButtonNotify();
	}

	public void Stop() {
		if (mPlayer.isPlaying()) {
			mPlayer.stop();
		}
		isReadyToPlay = false;
		setUpdateUI(false);
		showButtonNotify();
	}

	public static void playNext(String path) {
		handler.sendEmptyMessage(MSG_UPDATA_LRC_TEXT);

		isReadyToPlay = false;
		MusicStaticPool.setLrInfo(null);
		if (mPlayer.isPlaying()) {
			mPlayer.stop();
		}
		try {
			mPlayer.reset();
			mPlayer.setDataSource(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			mPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mPlayer.start();

		// while (!mPlayer.isPlaying()) {// 需要等待播放器准备，否则，会直接发出通知让播放器又停止了
		showButtonNotify();
		// }

		setUpdateUI(true);

		getURL = true;
		isGoingPlayNewSong = false;

	}

	static boolean getURL = false;

	public long getCurPlayingTime() {
		if (mPlayer == null) {
			return 0;
		}
		if (mPlayer.isPlaying()) {
			return mPlayer.getCurrentPosition();
		}
		return 0;

	}

	public long getCurMusicTime() {

		if (mPlayer != null && mPlayer.isPlaying()) {
			try {
				return mPlayer.getDuration();
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}

		}
		return 0;
	}

	public static boolean getCurPlayingStata() {
		if (mPlayer == null) {
			return false;
		}
		try {
			return mPlayer.isPlaying();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}


	}

	@Override
	public boolean onUnbind(Intent intent) {

		// mPlayer.release();
		return super.onUnbind(intent);
	}

	public final static String ACTION_BUTTON = "com.yikang.ykmusic.ButtonClick";
	public final static String INTENT_BUTTONID_TAG = "action_tag";

	/*******************************************************************************************/

	/**
	 * 带按钮的通知栏
	 */
	@SuppressLint("InlinedApi")
	public static void showButtonNotify() {
		if (MusicStaticPool.getCurMusicPlaying() == null) {
			return;
		}
		System.out.println("===========showButtonNotify==============");
		NotificationCompat.Builder mBuilder = new Builder(sContext);
		RemoteViews mRemoteViews = new RemoteViews(sContext.getPackageName(), R.layout.notify_re_view);
		// mRemoteViews.setImageViewResource(R.id.notif_song_icon,
		// R.drawable.ic_launcher);
		// API3.0 以上的时候显示按钮，否则消失
		mRemoteViews.setTextViewText(R.id.tv_notif_song_singer, MusicStaticPool.getCurMusicPlaying().getArtist() + "");
		mRemoteViews.setTextViewText(R.id.tv_notif_song_name, MusicStaticPool.getCurMusicPlaying().getTitle() + "");
		// 如果版本号低于（3。0），那么不显示按钮
		// mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
		// mRemoteViews.setImageViewResource(R.id.btn_notif_play,
		// R.drawable.play_bt);
		//
		// if(BaseTools.getSystemVersion() <= 9){
		// mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.GONE);
		// }else{
		// mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
		// //
		if (getCurPlayingStata()) {

			mRemoteViews.setImageViewResource(R.id.btn_notif_play, R.drawable.pause_bt);
		} else {

			mRemoteViews.setImageViewResource(R.id.btn_notif_play, R.drawable.play_bt);
		}
		// }

		// 点击的事件处理
		Intent buttonIntent = new Intent(ACTION_BUTTON);
		Intent buttonIntents = new Intent(sContext.getApplicationContext(), com.yikang.ykmusix.MainMusixActivity.class);
		/* 上一首按钮 */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, 1);
		// 这里加了广播，所及INTENT的必须用getBroadcast方法
		PendingIntent intent_main = PendingIntent.getActivity(sContext, 1, buttonIntents, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.notif_song_icon, intent_main);
		/* 播放/暂停 按钮 */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, 2);
		PendingIntent intent_paly = PendingIntent.getBroadcast(sContext, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_notif_play, intent_paly);
		/* 下一首 按钮 */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, 3);
		PendingIntent intent_next = PendingIntent.getBroadcast(sContext, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_notif_next, intent_next);
		/* 退出 按钮 */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, 4);
		PendingIntent intent_exit = PendingIntent.getBroadcast(sContext, 4, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_notif_exit, intent_exit);

		mBuilder.setContent(mRemoteViews)
				.setContentIntent(PendingIntent.getBroadcast(sContext, 1, new Intent(), Notification.FLAG_ONGOING_EVENT))
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setTicker("正在播放").setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setOngoing(true).setSmallIcon(R.drawable.ic_launcher);
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		// 会报错，还在找解决思路
		// notify.contentView = mRemoteViews;
		// notify.contentIntent = PendingIntent.getActivity(this, 0,
		// buttonIntent, 0);
		mNotificationManager.notify(258, notify);
	}

	/** 带按钮的通知栏点击广播接收 */
	public void initButtonReceiver() {
		bReceiver = new NotifycationReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		registerReceiver(bReceiver, intentFilter);
	}

	public class NotifycationReceiver extends BroadcastReceiver implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void onReceive(Context context, Intent intent) {
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
					Log.d(TAG, "onReceive case 2");
					if (getCurPlayingStata()) {
						Pause();

					} else {
						Resume();
					}
					setUpdateUI(false);
					Message msg = Message.obtain();
					msg.what = 2002;
					handler.sendMessage(msg);
					showButtonNotify();
					Log.d(TAG, "play_status");
					Toast.makeText(getApplicationContext(), "play_status " + getCurPlayingStata(), Toast.LENGTH_SHORT).show();
					break;
				case 3:
					Log.d(TAG, "下一首");
					playNext();
					showButtonNotify();
					Toast.makeText(getApplicationContext(), "下一首", Toast.LENGTH_SHORT).show();
					break;
				case 4:

					Log.d(TAG, "程序已经退出！");
					mNotificationManager.cancel(258);
					Intent i = new Intent(sContext, MusicPlayerService.class);
					context.stopService(i);
					// 强制退出程序
					// android.os.Process.killProcess(android.os.Process.myPid());
					// System.exit(0); // 常规java、c#的标准退出法，返回值为0代表正常退出
					break;
				default:
					break;
				}
			}
		}
	}

	public static void playPre() {
		if (MusicStaticPool.getCurPlayList() == null || MusicStaticPool.getCurPlayList().size() == 0) {
			Toast.makeText(sContext, "没有更多音乐了！", Toast.LENGTH_SHORT).show();
			System.out.println("mCurrentPlayingList 出问题了！");
			return;
		}
		if (MusicStaticPool.getCurPlayListPS() - 1 >= 0) {
			MusicStaticPool.setCurPlayListPS(MusicStaticPool.getCurPlayListPS() - 1);
		} else {
			MusicStaticPool.setCurPlayListPS(MusicStaticPool.getCurPlayList().size() - 1);

		}

		playNext(MusicStaticPool.getCurPlayList().get(MusicStaticPool.getCurPlayListPS()).getUrl());
		FragmentPlayList.dataHavedChanged();

	}

	public static void playNext() {
		if (MusicStaticPool.getCurPlayList() == null || MusicStaticPool.getCurPlayList().size() == 0) {
			Toast.makeText(sContext, "没有更多音乐了！", Toast.LENGTH_SHORT).show();
			System.out.println("mCurrentPlayingList 出问题了！");
			return;
		}
		if (MusicStaticPool.getCurPlayListPS() + 1 < MusicStaticPool.getCurPlayList().size()) {
			MusicStaticPool.setCurPlayListPS(MusicStaticPool.getCurPlayListPS() + 1);
		} else {
			MusicStaticPool.setCurPlayListPS(0);

		}

		playNext(MusicStaticPool.getCurPlayList().get(MusicStaticPool.getCurPlayListPS()).getUrl());
		FragmentPlayList.dataHavedChanged();

	}

	public static boolean isUpdateUI() {
		return isUpdateUI;
	}

	public static void setUpdateUI(boolean isUpdateUI) {
		MusicPlayerService.isUpdateUI = isUpdateUI;
	}

}
