package com.yikang.ykmusix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yikang.ykmusix.FragmentMusicListing.MusicSelcetCallBack;
import com.yikang.ykmusix.been.MusicInfo;
import com.yikang.ykmusix.been.MusicListingItem;
import com.yikang.ykmusix.been.MusicStaticPool;
import com.yikang.ykmusix.model.MusicDBManager;
import com.yikang.ykmusix.model.MusicListing;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * 音乐文件列表
 * 
 * @author Administrator
 * 
 */
public class FragmentPlayListing extends Fragment implements OnClickListener {

	private List<MusicInfo> curPlayLists; // 当前选中的音乐列表

	private int MusicItemPS = 0;
	static List<MusicInfo> hasChoose;
	private ListView listView;
	private LinearLayout root_play_listing;
	private LinearLayout ll_play_listing_state;
	private Button bt_add_del;
	private Button bt_all_inv;
	private TextView tv_hasChoosed;
	static boolean isDelMode = false;

	private PlayListingAdapter adapter;
	private HashMap<Integer, Boolean> HashMap;
	private int hasSelectedCount = 0; // 用来统计当前选中的数目
	private final String FileSelectResult = "Result";
	private final int requestCode = 20;

	/**
	 * 当前选中的 item 也就是点击进入的歌曲列表
	 */
	MusicListingItem mItem;
	PlayingMusicSelectCallBack mCallBack;

	public interface PlayingMusicSelectCallBack {
		void itemHasSelectToPlay(String URL);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallBack = (PlayingMusicSelectCallBack) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " 还没实现MusicSelcetCallBack接口");
		}
	}

	/*
	 * ===================================== 动画
	 * ==================================================
	 */

	/**
	 * 拉下动画
	 * 
	 * @param root
	 *            根
	 * @param view
	 *            子视图
	 * @param durationTime
	 */
	private void animatorAppear(LinearLayout root, View view, int durationTime) {
		view.clearAnimation();
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) view.getLayoutParams(); // 取控件mGrid当前的布局参数
		linearParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;// 当控件的高强制设成75象素
		view.setLayoutParams(linearParams);
		int height = view.getHeight();
		ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "translationY", -height, 0);
		ObjectAnimator objectAnimatorA = ObjectAnimator.ofFloat(view, "alpha", 0.1f, 1f);
		AnimatorSet animSet = new AnimatorSet();
		animSet.play(objectAnimatorY).with(objectAnimatorA);
		animSet.setDuration(durationTime);
		animSet.start();
	}

	private static void animatorDisappear(LinearLayout root, View view, int durationTime) {
		view.clearAnimation();

		int height = view.getHeight();
		ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(view, "translationY", 0, -height);
		ObjectAnimator objectAnimatorA = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.3f);
		AnimatorSet animSet = new AnimatorSet();
		animSet.play(objectAnimatorY).with(objectAnimatorA);
		animSet.setDuration(durationTime);
		animSet.start();
		animSet.addListener(new AfterAnimator(root, view));
	}

	public static class AfterAnimator implements AnimatorListener {
		LinearLayout root;
		View view;

		public AfterAnimator(LinearLayout root, View view) {
			this.root = root;
			this.view = view;
		}

		@Override
		public void onAnimationStart(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) view.getLayoutParams(); //

			linearParams.height = 0;// 当控件的高强制设成75象素
			view.setLayoutParams(linearParams);

		}

		@Override
		public void onAnimationCancel(Animator animation) {
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

	}

	/*
	 * =====================================
	 * ==================================================
	 */
	Context mContext;
	/**
	 * MusicListing业务
	 */
	MusicListing mListingMode;

	public static final FragmentPlayListing newInstance(Context mContext, MusicListing mListingMode, MusicListingItem mItem, int MusicItemPS) {
		FragmentPlayListing f = new FragmentPlayListing();
		f.mContext = mContext;
		f.mListingMode = mListingMode;
		f.mItem = mItem;
		f.MusicItemPS = MusicItemPS;
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.f_play_listing, null);

		curPlayLists = mListingMode.loadSelectPlayList(mItem.getId());

		root_play_listing = (LinearLayout) view.findViewById(R.id.root_play_listing);
		ll_play_listing_state = (LinearLayout) view.findViewById(R.id.ll_play_listing_state);
		tv_hasChoosed = (TextView) view.findViewById(R.id.tv_play_listing);
		bt_add_del = (Button) view.findViewById(R.id.bt_play_listing_add_del);
		bt_all_inv = (Button) view.findViewById(R.id.bt_play_listing_all_inv);
		bt_add_del.setOnClickListener(this);
		bt_all_inv.setOnClickListener(this);
		getActivity().setTitle(mItem.getTitle() + ":");
		bt_add_del.setText("添加音乐到列表");
		listView = (ListView) view.findViewById(R.id.lv_play_listing);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (isDelMode) {
					ViewHolderListingItem holder = (ViewHolderListingItem) view.getTag();
					holder.mCB_Choose.toggle();

					HashMap.put(position, holder.mCB_Choose.isChecked());

					bt_all_inv.setText("全选");
					bt_all_inv.setBackgroundResource(R.drawable.defau_p_bt);
					if (holder.mCB_Choose.isChecked()) {
						hasSelectedCount++;
						tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
						if (hasSelectedCount == HashMap.size()) {
							bt_all_inv.setText("取消全选");
							bt_all_inv.setBackgroundResource(R.drawable.defau_n_bt);

						}
					} else {

						hasSelectedCount--;
						tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
					}

				} else {
					if (curPlayLists.size() > 0) {
						// 更新到播放器后台的信息状态池
						MusicStaticPool.setCurPlayList(curPlayLists);
						MusicStaticPool.setCurPlayListPS(position);
						MusicStaticPool.setCurListingPS(MusicItemPS);
						mCallBack.itemHasSelectToPlay(MusicStaticPool.getCurPlayList().get(position).getUrl());
						adapter.notifyDataSetChanged();

					}
				}

			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				if (isDelMode) {// 什么也不处理，直接返回
					return false;
				}
				isDelMode = true;

				animatorAppear(root_play_listing, ll_play_listing_state, 550);

				bt_add_del.setText("从列表中删除音乐");
				bt_add_del.setBackgroundResource(R.drawable.music_del_style_bt);
				bt_all_inv.setBackgroundResource(R.drawable.defau_p_bt);

				int count = curPlayLists.size();
				HashMap = new HashMap<Integer, Boolean>();
				for (int i = 0; i < count; i++) {
					HashMap.put(i, false);
				}
				adapter.notifyDataSetChanged();
				return false;
			}
		});

		adapter = new PlayListingAdapter(mContext);
		listView.setAdapter(adapter);

		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ll_play_listing_state.getLayoutParams(); // 取控件mGrid当前的布局参数
		linearParams.height = 0;// 当控件的高强制设成75象素
		ll_play_listing_state.setLayoutParams(linearParams);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		isDelMode = false;
		hasSelectedCount = 0;
		tv_hasChoosed.setText("已经选中0项");
		bt_all_inv.setText("全选");
		bt_all_inv.setBackgroundResource(R.drawable.defau_p_bt);

	}

	@Override
	public void onDestroy() {
		isDelMode = false;
		super.onDestroy();
	}

	class PlayListingAdapter extends BaseAdapter {
		Context context;

		LayoutInflater inflater;

		public PlayListingAdapter() {

		}

		public PlayListingAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
			int count = curPlayLists.size();
			HashMap = new HashMap<Integer, Boolean>();
			for (int i = 0; i < count; i++) {
				HashMap.put(i, false);
			}
		}

		@Override
		public int getCount() {

			return curPlayLists.size();
		}

		@Override
		public Object getItem(int position) {
			return curPlayLists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolderListingItem holder = null;
			if (convertView == null) {
				holder = new ViewHolderListingItem();
				convertView = inflater.inflate(R.layout.item_play_listing, null);
				holder.mTV_title = (TextView) convertView.findViewById(R.id.tv_item_play_listing_title);
				holder.mTV_artist = (TextView) convertView.findViewById(R.id.tv_item_play_listing_artist);
				holder.mTV_duration = (TextView) convertView.findViewById(R.id.tv_item_play_listing_duration);
				holder.mCB_Choose = (CheckBox) convertView.findViewById(R.id.cb_item_play_listing_choose);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderListingItem) convertView.getTag();
			}
			holder.mTV_title.setText(curPlayLists.get(position).getTitle() + "");
			if(position==MusicStaticPool.getCurPlayListPS()){
				if(mItem.getId()==MusicStaticPool.getCurListing().get(MusicStaticPool.getCurListingPS()).getId()){
					holder.mTV_title.setTextColor(Color.RED);
				}
			}else{
				holder.mTV_title.setTextColor(Color.BLACK);
			}
			
			holder.mTV_artist.setText(curPlayLists.get(position).getArtist() + "");
			holder.mTV_duration.setText(MusicInfo.getDurStr(curPlayLists.get(position).getDuration()) + "");

			if (isDelMode) {
				holder.mCB_Choose.setVisibility(View.VISIBLE);

				holder.mCB_Choose.setChecked(HashMap.get(position));
			} else {
				holder.mCB_Choose.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

	}

	class ChooseOnClick implements OnClickListener {
		int p;

		public ChooseOnClick(int positon) {
			p = positon;
		}

		@Override
		public void onClick(View v) {
			bt_all_inv.setText("全选");
			bt_all_inv.setBackgroundResource(R.drawable.defau_p_bt);

			if (((CompoundButton) v).isChecked()) {
				HashMap.put(p, true);
				hasSelectedCount++;
				tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
				// Toast.makeText(mContext, "Has select " +
				// hasSelectedCount, Toast.LENGTH_SHORT).show();
				if (hasSelectedCount == HashMap.size()) {
					bt_all_inv.setText("取消全选");
					bt_all_inv.setBackgroundResource(R.drawable.defau_n_bt);

				}
			} else {
				HashMap.put(p, false);
				hasSelectedCount--;
				tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
				// Toast.makeText(mContext, "Has select " +
				// hasSelectedCount, Toast.LENGTH_SHORT).show();
			}

		}
	}

	class ViewHolderListingItem {
		TextView mTV_title;
		TextView mTV_artist;
		TextView mTV_duration;

		CheckBox mCB_Choose;
	}

	/**
	 * 按下取消键时，fragment的动作
	 */

	public  void playListingFragmentCancelKey() {
		if (adapter != null) {
			animatorDisappear(root_play_listing, ll_play_listing_state, 550);
			bt_add_del.setText("添加音乐到当前列表");
			bt_add_del.setBackgroundResource(R.drawable.music_add_style_bt);
			hasSelectedCount=0;
			tv_hasChoosed.setText("已经选中0项");
			isDelMode = false;
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == bt_add_del) {
			if (!isDelMode) {
				Intent intent = new Intent(mContext, MusicFileSelectActivity.class);
				startActivityForResult(intent, requestCode);
				isDelMode = false;

			} else {
				isDelMode = false;
				bt_add_del.setText("添加音乐到当前列表");
				bt_add_del.setBackgroundResource(R.drawable.music_add_style_bt);

				hasChoose = new ArrayList<MusicInfo>();
				for (int i = 0; i < HashMap.size(); i++) {
					if (HashMap.get(i)) {
						hasChoose.add(curPlayLists.get(i));
					}
				}
				animatorDisappear(root_play_listing, ll_play_listing_state, 550);
				mListingMode.deletMusicInfos(hasChoose);
				// 删除后重新加载
				curPlayLists = mListingMode.loadSelectPlayList(mItem.getId());

				MusicStaticPool.setCurListing(mListingMode.reSetLoadMusicListings());
				MusicStaticPool.setCurPlayList(curPlayLists);// 同步到播放静态池中去
				adapter.notifyDataSetChanged();

			}
		}
		if (v == bt_all_inv) {
			if (!isDelMode) {
				return;
			}
			if (hasSelectedCount == HashMap.size()) {
				hasSelectedCount = 0;
				bt_all_inv.setText("全选");
				bt_all_inv.setBackgroundResource(R.drawable.defau_p_bt);

				tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
				for (int i = 0; i < HashMap.size(); i++) {
					HashMap.put(i, false);
				}
				adapter.notifyDataSetChanged();
			} else {
				bt_all_inv.setText("取消全选");
				bt_all_inv.setBackgroundResource(R.drawable.defau_n_bt);

				hasSelectedCount = HashMap.size();
				tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
				for (int i = 0; i < HashMap.size(); i++) {
					HashMap.put(i, true);
				}
				adapter.notifyDataSetChanged();
			}
			return;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data == null) {
			return;
		}

		MusicDBManager dbManager = new MusicDBManager(mContext);
		dbManager.addMusicInfos((List<MusicInfo>) data.getSerializableExtra(FileSelectResult), mItem.getId());

		// 重新赋值，设置他的计数
		mItem.setCount(mItem.getCount() + curPlayLists.size());

		dbManager.updateMusicListingItem(mItem);
		curPlayLists = dbManager.queryMusicInfos(mItem.getId());// 保存之后再重新加载
		dbManager.closeDB();
		MusicStaticPool.setCurListing(mListingMode.reSetLoadMusicListings());// 将数更新到播放的信息状态池中
		adapter.notifyDataSetChanged();// 更新视图

	}
}
