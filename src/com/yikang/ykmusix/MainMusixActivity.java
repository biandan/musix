package com.yikang.ykmusix;

import java.io.Serializable;
import java.util.List;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.yikang.ykmusix.been.LRCTextView;
import com.yikang.ykmusix.been.MusicInfo;
import com.yikang.ykmusix.been.MusicListingItem;
import com.yikang.ykmusix.been.MusicStaticPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.yikang.ykmusix.been.MyViewPager;
import com.yikang.ykmusix.model.MusicDBManager;
import com.yikang.ykmusix.model.QZoneShare;

public class MainMusixActivity extends FragmentActivity {

	private FragmentSlidingMenu mSlidingFragment;
	static Context mContext;
	private static boolean isUpState = false;
	static MusicPlayerService mMusicPlayerService;

	private static MyViewPager mViewPager;
	static Button mBT_Music_Play;
	private Button mBT_Music_next;
	private Button mBT_Music_before;
	private Button mBT_Music_list;
	private Button mBT_Music_Mode;
	private static TextView mTVShowCurTime;
	private static TextView mTVShowTime;

	private static SeekBar mSeekBar;

	private static final int Loop = 0;
	// private static final int Order = 1;
	private static final int Random = 1;
	private static final int Single = 2;

	static ViewPagerAdapter mAdapter;
	static Intent service;

	/**
	 * 获取当前服务的 Intent
	 * 
	 * @return
	 */

	MainMusicHandle mHandle = new MainMusicHandle();

	/****************************************************************************************/
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putSerializable("save", new MusicStaticPool()) ;
		// MusicStaticPool staticPool =new MusicStaticPool() ;
		//
		//
		//
		//
		// staticPool.setCurMusicPlaying(MusicStaticPool.getCurPlayList()) ;
		//
		// staticPool.setCurPlayList(MusicStaticPool.getCurPlayList()) ;
		//
		//
		// staticPool.setCurListing(List<MusicListingItem> curListing) ;
		//
		// staticPool. setCurModel(int curModel) ;
		//
		// staticPool.setCourentListingID(int courentListingID) ;
		//
		// staticPool.setCurPlayListPS(int curPlayListPS) ;
		//
		//
		// staticPool.setCurListingPS(int curListingPS);
		//
		// staticPool.setPlaying(boolean isPlaying) ;
		//
		//
		//
		//
		//
		//

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initButtonReceiver();
		mContext = this;
		mViewPager = (MyViewPager) findViewById(R.id.vp_musix_index);

		mBT_Music_Mode = (Button) findViewById(R.id.bt_music_mode);
		mBT_Music_Play = (Button) findViewById(R.id.bt_music_play);
		mBT_Music_next = (Button) findViewById(R.id.bt_music_next);
		mBT_Music_before = (Button) findViewById(R.id.bt_music_before);
		mBT_Music_list = (Button) findViewById(R.id.bt_music_list);
		mSeekBar = (SeekBar) findViewById(R.id.sb_player_progress);
		mTVShowCurTime = (TextView) findViewById(R.id.tv_player_cur_time);
		mTVShowTime = (TextView) findViewById(R.id.tv_player_time);

		mSeekBar.setEnabled(false);

		mBT_Music_Play.setOnClickListener(new PlayerOnClick());
		mBT_Music_Mode.setOnClickListener(new PlayerOnClick());
		mBT_Music_next.setOnClickListener(new PlayerOnClick());
		mBT_Music_list.setOnClickListener(new PlayerOnClick());
		mBT_Music_before.setOnClickListener(new PlayerOnClick());

		mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
		initSlidingMenu(savedInstanceState);
		service = new Intent(this, MusicPlayerService.class);
		startService(service);
		// bindService(intent, connService, Context.BIND_AUTO_CREATE);

		MusicStaticPool.setCurListing(reSetLoadMusicListings());
		// mMusicListings = reSetLoadMusicListings();
		if (MusicStaticPool.getCurPlayListPS() == -1) {
			if (MusicStaticPool.getCurListing().size() > 0) {
				MusicStaticPool.setCurPlayList(loadSelectPlayList(MusicStaticPool.getCurListing().get(0).getId()));
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (MusicStaticPool.isExitApp()) {
			finish();
		}
		Intent intent = new Intent(this, MusicPlayerService.class);
		intent.putExtra("MainMusicHandle", mHandle);
		bindService(intent, connService, Context.BIND_AUTO_CREATE);
		if (!MusicPlayerService.getCurPlayingStata()) {
			mBT_Music_Play.setBackgroundResource(R.drawable.play_bt);
		} else {
			mBT_Music_Play.setBackgroundResource(R.drawable.pause_bt);

		}

		switch (MusicStaticPool.getCurModel()) {
		case Loop:
			mBT_Music_Mode.setBackgroundResource(R.drawable.loop_bt);
			break;
		case Random:
			mBT_Music_Mode.setBackgroundResource(R.drawable.shuffle_bt);
			break;
		case Single:
			mBT_Music_Mode.setBackgroundResource(R.drawable.single_bt);
			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// thread.stop() ;
		if (bReceiver != null) {
			unregisterReceiver(bReceiver);
		}
		unbindService(connService);
		System.out.println("MainMusixActivity onDestroy!");
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		
		if(QZoneShare.mTencent!=null){
			QZoneShare.mTencent.onActivityResult(arg0, arg1, arg2);
		}
	}

	static private List<MusicListingItem> reSetLoadMusicListings() {
		MusicDBManager dbMusicManager = new MusicDBManager(mContext);

		List<MusicListingItem> list = dbMusicManager.queryAllMusicListingItem();
		for (MusicListingItem item : list) {// 每次加载的时候都重新对数据进行更新以保证正确的数据
			long count1 = dbMusicManager.queryMusicInfos(item.getId()).size();
			item.setCount((int) count1);
			dbMusicManager.updateMusicListingItem(item);
		}
		dbMusicManager.closeDB();
		return list;

	}

	static private List<MusicInfo> loadSelectPlayList(int musicListID) {
		MusicDBManager dbManager = new MusicDBManager(mContext);
		List<MusicInfo> infos = dbManager.queryMusicInfos(musicListID);
		dbManager.closeDB();
		return infos;

	}

	static void musicPlay(String url) {
		MusicPlayerService.playNext(url);
		mBT_Music_Play.setBackgroundResource(R.drawable.pause_bt);
	}

	// ----------------------------------------------------------------------------------

	class PlayerOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == mBT_Music_Mode) {
				if (MusicStaticPool.getCurModel() + 1 < 3) {
					MusicStaticPool.setCurModel(MusicStaticPool.getCurModel() + 1);
				} else {
					MusicStaticPool.setCurModel(0);
				}
				// updateBGPlayingStatus();
				switch (MusicStaticPool.getCurModel()) {
				case Loop:
					// mBT_Music_Mode.setText("循环");
					mBT_Music_Mode.setBackgroundResource(R.drawable.loop_bt);
					break;
				case Random:
					// mBT_Music_Mode.setText("随机");
					mBT_Music_Mode.setBackgroundResource(R.drawable.shuffle_bt);
					break;
				case Single:
					// mBT_Music_Mode.setText("单曲");
					mBT_Music_Mode.setBackgroundResource(R.drawable.single_bt);
					break;

				default:
					break;
				}

			}
			if (v == mBT_Music_before) {

				if (MusicStaticPool.getCurPlayList() == null || MusicStaticPool.getCurPlayList().size() == 0) {
					Toast.makeText(MainMusixActivity.this, "没有更多音乐了！", Toast.LENGTH_SHORT).show();
					System.out.println("mCurrentPlayingList 出问题了！");
					return;
				}
				if (MusicStaticPool.getCurPlayListPS() - 1 >= 0) {
					MusicStaticPool.setCurPlayListPS(MusicStaticPool.getCurPlayListPS() - 1);
				} else {
					MusicStaticPool.setCurPlayListPS(MusicStaticPool.getCurPlayList().size() - 1);
				}
				FragmentPlayList.dataHavedChanged();
				musicPlay(MusicStaticPool.getCurPlayList().get(MusicStaticPool.getCurPlayListPS()).getUrl());
				ShowFragment.setAlbumImg();
			}
			if (v == mBT_Music_Play && mMusicPlayerService != null) {

				if (MusicPlayerService.getCurPlayingStata()) {

					mBT_Music_Play.setBackgroundResource(R.drawable.play_bt);
					MusicPlayerService.Pause();
				} else {
					mBT_Music_Play.setBackgroundResource(R.drawable.pause_bt);
					MusicPlayerService.Resume();
				}

			}
			if (v == mBT_Music_next) {

				if (MusicStaticPool.getCurPlayList() == null || MusicStaticPool.getCurPlayList().size() == 0) {
					Toast.makeText(MainMusixActivity.this, "没有更多音乐了！", Toast.LENGTH_SHORT).show();
					System.out.println("mCurrentPlayingList 出问题了！");
					return;
				}
				if (MusicStaticPool.getCurPlayListPS() + 1 < MusicStaticPool.getCurPlayList().size()) {
					MusicStaticPool.setCurPlayListPS(MusicStaticPool.getCurPlayListPS() + 1);
				} else {
					MusicStaticPool.setCurPlayListPS(0);
				}
				musicPlay(MusicStaticPool.getCurPlayList().get(MusicStaticPool.getCurPlayListPS()).getUrl());
				FragmentPlayList.dataHavedChanged();
				ShowFragment.setAlbumImg();
			}

			if (v == mBT_Music_list) {
				Intent intent = new Intent(MainMusixActivity.this, MusicListingActivity.class);
				startActivity(intent);
			}

		}

	}

	SlidingMenu mSlidMenu;
	boolean isMenuOpen = false;

	/**
	 * 初始化菜单
	 * @param savedInstanceState
	 */
	private void initSlidingMenu(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mSlidingFragment = (FragmentSlidingMenu) getSupportFragmentManager().getFragment(savedInstanceState, "mFragment");
		}

		if (mSlidingFragment == null) {
			mSlidingFragment = new FragmentSlidingMenu();
		}

		// 实例化滑动菜单对象
		mSlidMenu = new SlidingMenu(this);
		// 设置可以左右滑动的菜单
		mSlidMenu.setMode(SlidingMenu.LEFT);

		// 设置滑动阴影的宽度

		mSlidMenu.setShadowWidthRes(R.dimen.shadow_width);
		// 设置滑动菜单阴影的图像资源
		mSlidMenu.setShadowDrawable(null);
		// 设置滑动菜单视图的宽度
		mSlidMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		mSlidMenu.setFadeDegree(0.35f);
		// 设置触摸屏幕的模式,这里设置为全屏
		mSlidMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// 设置下方视图的在滚动时的缩放比例
		mSlidMenu.setBehindScrollScale(0.0f);

		mSlidMenu.setBackgroundImage(R.drawable.img_frame_background);
		mSlidMenu.setMenu(R.layout.s_menu);
		mSlidMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, -canvas.getWidth() / 2, canvas.getHeight() / 2);
			}
		});

		mSlidMenu.setOnOpenedListener(new OnOpenedListener() {

			@Override
			public void onOpened() {
				isMenuOpen = true;

			}
		});
		mSlidMenu.setOnClosedListener(new OnClosedListener() {

			@Override
			public void onClosed() {
				isMenuOpen = false;
			}
		});
		mSlidMenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.15);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_menu, mSlidingFragment, "mFragment").commit();

		mSlidMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
	}

	ServiceConnection connService = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mMusicPlayerService = ((MusicPlayerService.MusicPlayerBinder) service).getService();
		}
	};

	// =====================================ViewPagerAdapter=================================================

	public class ViewPagerAdapter extends FragmentPagerAdapter {
		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int arg0) {
			if (arg0 == 0) {
				return new FragmentPlayList();
			} else {
				return new ShowFragment();
			}

		}

		@Override
		public int getCount() {
			return 2;
		}

	}

	// ---------------------------------ShowFragment-----------------------------------

	/**
	 * 暂时用来显示歌词以及歌曲封面
	 * @author Administrator
	 *
	 */
	public static class ShowFragment extends Fragment {
		RelativeLayout mLL_behind;
		RelativeLayout mRL_frond;
//		Button mBT_music_setting, mBT_behind;
		static ImageView img_album;
		static com.yikang.ykmusix.been.LRCTextView tv_show_fragmet_lrc;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			View view = inflater.inflate(R.layout.f_pull_and_push, null);
			mLL_behind = (RelativeLayout) view.findViewById(R.id.ll_behind);
			mRL_frond = (RelativeLayout) view.findViewById(R.id.ll_front);
			img_album = (ImageView) view.findViewById(R.id.img_album);
			tv_show_fragmet_lrc = (LRCTextView) view.findViewById(R.id.tv_show_fragmet_lrc);
//			mBT_music_setting = (Button) view.findViewById(R.id.bt_music_setting);
//			 mBT_music_setting.setBackgroundResource(R.drawable.btn_up);

			/**** ？ 先暂时屏蔽掉 */
//			mBT_music_setting.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					System.out.println("mBT_music_setting onClick ");
//
//					if (isUpState) {
//						mViewPager.setCanScroll(true);
//						moveDown(mRL_frond);
//						// mBT_music_setting
//						// .setBackgroundResource(R.drawable.btn_up);
//						isUpState = false;
//
//					} else {
//						mViewPager.setCanScroll(false);
//						moveUp(mRL_frond);
//						// mBT_music_setting
//						// .setBackgroundResource(R.drawable.btn_down);
//						isUpState = true;
//
//					}
//
//				}
//			});

			return view;
		}

		@Override
		public void onResume() {
			setAlbumImg();
			super.onResume();
		}

		public static void setAlbumImg() {
			if (MusicStaticPool.getCurMusicPlaying() != null) {
				Bitmap bitmap = MusicInfo.getArtworkFromFile(mContext, MusicStaticPool.getCurMusicPlaying().getId(), MusicStaticPool
						.getCurMusicPlaying().getAlbumID());

				if (bitmap == null) {
					img_album.setImageResource(R.drawable.ic_launcher);
				} else {
					img_album.setImageBitmap(bitmap);
				}
			} else {
				img_album.setImageResource(R.drawable.ic_launcher);

			}
		}

		void moveUp(View view) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0f, -600f);
			animator.setDuration(200);
			animator.start();
		}

		void moveDown(View view) {

			ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -600f, 0f);
			animator.setDuration(200);
			// animator.addListener(new AfterDisaListener( behind)) ;
			animator.start();
		}
	}

	/*--------------------MainMusicHandle----------------*/
	static class MainMusicHandle extends Handler implements Serializable {

		private static final long serialVersionUID = 1L;

		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 2000) {// 更新界面消息
				Bundle data = msg.getData();
				mMusicTimeLen = data.getString("mMusicTimeLen", "00:00");
				mMusicCurTimeLen = data.getString("mMusicCurTimeLen", "00:00");
				mProgress = (int) data.getLong("mProgress", 00);

				mTVShowCurTime.setText(mMusicCurTimeLen + "");
				mTVShowTime.setText(mMusicTimeLen + "");
				mSeekBar.setProgress(mProgress);
			}

			if (msg.what == 2001) {// 播放完消息
				Toast.makeText(mContext, "" + MusicStaticPool.getCurPlayList().get(MusicStaticPool.getCurPlayListPS()).getTitle(),
						Toast.LENGTH_SHORT).show();

				if (mMusicPlayerService != null) {
					//
					// if (MusicPlayerService.getCurPlayingStata()) {
					//
					// mBT_Music_Play.setBackgroundResource(R.drawable.play_bt);
					// MusicPlayerService.Pause();
					// } else {
					// mBT_Music_Play.setBackgroundResource(R.drawable.pause_bt);
					// MusicPlayerService.Resume();
					// }
					FragmentPlayList.dataHavedChanged();
				}
			}
			if (msg.what == 2002) {// 播放完消息
				Toast.makeText(mContext, "" + MusicStaticPool.getCurPlayList().get(MusicStaticPool.getCurPlayListPS()).getTitle(),
						Toast.LENGTH_SHORT).show();

				if (mMusicPlayerService != null) {
					if (!MusicPlayerService.getCurPlayingStata()) {
						mBT_Music_Play.setBackgroundResource(R.drawable.play_bt);
					} else {
						mBT_Music_Play.setBackgroundResource(R.drawable.pause_bt);
					}
					FragmentPlayList.dataHavedChanged();
				}

			}

			if (msg.what == MSG_UPDATA_LRC_TEXT) {
				ShowFragment.tv_show_fragmet_lrc.invalidate();
				try {
					ShowFragment.tv_show_fragmet_lrc.updata(MusicStaticPool.getLrcIndexShow(), MusicStaticPool.getLrInfo().getInfos(),
							MusicStaticPool.getLrInfo().getKey());
				} catch (Exception e) {
					Log.d("MainMusicHandle", "歌词为空所以会异常，程序继续执行~~");
				}

			}
		}
	}

	private final static int MSG_UPDATA_LRC_TEXT = 3000;
	private static String mMusicTimeLen = "";
	private static String mMusicCurTimeLen = "";
	private static int mProgress = 0;

	// 显示对话框
	public void showExitDialog() {

		// 控件显示在对话框中
		new AlertDialog.Builder(mContext)
		// 对话框的标题
				.setTitle("是否要退出应用程序？")
				// 设定显示的View
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					public void onClick(DialogInterface dialog, int whichButton) {
						stopApp();
					}
				})
				// 对话框的“退出”单击事件
				.setNegativeButton("否", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				})

				.create().show();
	}

	public void stopApp() {
		mContext.getApplicationContext().stopService(service);
		finish();
		// android.os.Process.killProcess(android.os.Process.myPid());
		// System.exit(0); //常规java、c#的标准退出法，返回值为0代表正常退出
	}

	MainNotifycationReceiver bReceiver;

	/** 带按钮的通知栏点击广播接收 */
	private void initButtonReceiver() {
		bReceiver = new MainNotifycationReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		registerReceiver(bReceiver, intentFilter);
	}

	private static final String TAG = "ButtonBroadcastReceiver";
	public final static String ACTION_BUTTON = "com.yikang.ykmusic.ButtonClick";
	public final static String INTENT_BUTTONID_TAG = "action_tag";

	public class MainNotifycationReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(ACTION_BUTTON)) {

				int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
				switch (buttonId) {

				case 4:
					Log.d(TAG, "已经退出程序");
					stopApp();
					Toast.makeText(getApplicationContext(), "已经退出程序", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		}
	}

	// ====================================onKeyDown===================

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isMenuOpen) {
				mSlidMenu.toggle();
				return true;
			}

		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			mSlidMenu.toggle();
		}
		return super.onKeyDown(keyCode, event);

	}

}
