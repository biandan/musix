package com.yikang.ykmusix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yikang.ykmusix.been.MusicStaticPool;
import com.yikang.ykmusix.been.MusicInfo;
import com.yikang.ykmusix.been.MusicListingItem;
import com.yikang.ykmusix.model.MusicDBManager;
import com.yikang.ykmusix.model.MusicListing;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 列表操作，不一定会影响当前播放信息，故部分变量需重新定义
 * 
 * @author Administrator
 * 
 */
public class MusicListingActivity extends Activity implements FragmentMusicListing.MusicSelcetCallBack,
		FragmentPlayListing.PlayingMusicSelectCallBack {

	private static Context mContext;
	private static FragmentManager mFM;
	/**
	 * 用来标志当前的Fragment，以便在onkey（）方法中去正确识别处理事件
	 */
	static boolean isListingFragment = false;

	/**
	 * MusicListingActivity 的model层，用于给MusicListingActivity提供服务
	 */
	private static MusicListing mListingMode;

	@Override
	protected void onResume() {
		super.onResume();
		if (MusicStaticPool.isExitApp()) {
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.a_music_listing);
		mFM = getFragmentManager();
		setToMusicListingFragment();
		mListingMode = new MusicListing(mContext);

	}

	void setToPlayListingFragment(MusicListingItem mItem ,int MusicItemPS) {
		isListingFragment = false;

		mFM.beginTransaction().replace(R.id.fl_music_listing_container, FragmentPlayListing.newInstance(mContext, mListingMode, mItem,MusicItemPS),TagPlayFragemt)
				.commit();
	}

	static void setToMusicListingFragment() {
		isListingFragment = true;
		mFM.beginTransaction().replace(R.id.fl_music_listing_container, new FragmentMusicListing()).commit();
	}

	String TagPlayFragemt = "PlayListingFragment";

//	void openPlayListing(int musicListID) {
//		// PlayListingFragment.SelectPlayLists =
//		// mListingMode.loadSelectPlayList(musicListID);
//		// isListingFragment = false;
//		// mFM.beginTransaction().replace(R.id.fl_music_listing_container,FragmentPlayListing.newInstance(mContext,mListingMode,),"222").commit();
//	}

	// ---------------------------onKeyDown------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (isListingFragment) {
				return super.onKeyDown(keyCode, event);
			} else {
				if (FragmentPlayListing.isDelMode) {
					((FragmentPlayListing)mFM.findFragmentByTag(TagPlayFragemt)).playListingFragmentCancelKey();
//					 .;
//					 setToPlayListingFragment();
					return true;
				} else {
					setToMusicListingFragment();
					return true;
				}
			}

		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 列表标题
	 * 
	 * @author Administrator
	 * 
	 */

	public  void playMusic(String path) {

		MusicPlayerService.playNext(path);
	}

	@Override
	public void openPlayListing(MusicListingItem mItem,int MusicItemPS ) {
		setToPlayListingFragment(mItem,MusicItemPS);
	}

	@Override
	public void itemHasSelectToPlay(String URL) {
		playMusic(URL) ;
	}

}
