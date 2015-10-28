package com.yikang.ykmusix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yikang.ykmusix.been.MusicStaticPool;
import com.yikang.ykmusix.been.MusicDBManager;
import com.yikang.ykmusix.been.MusicInfo;
import com.yikang.ykmusix.been.MusicListingItem;
import com.yikang.ykmusix.been.MusicStatus;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 列表操作，不一定会影响当前播放信息，故部分变量需重新定义
 * 
 * @author Administrator
 * 
 */
public class MusicListingActivity extends Activity {
	private static MusicPlayerService mMusicPlayerService;
	private static final int requestCode = 20;
	private static final String FileSelectResult = "Result";

	private static Context mContext;
	private static FragmentManager mFM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.a_music_listing);
		// mMusicListings = reSetLoadMusicListings();

		// mMusicPlayerService = (MusicPlayerService)
		// getIntent().getExtras().getSerializable("player");
		// mMusicListingFragment = new MusicListingFragment();
		// mPlayListingFragment = new PlayListingFragment();
		mFM = getFragmentManager();
		setToMusicListingFragment();

	}

	void setToPlayListingFragment() {
		isListingFragment = false;

		mFM.beginTransaction().replace(R.id.fl_music_listing_container, new PlayListingFragment()).commit();
	}

	static void setToMusicListingFragment() {
		isListingFragment = true;
		mFM.beginTransaction().replace(R.id.fl_music_listing_container, new MusicListingFragment()).commit();
	}

	static List<MusicListingItem> reSetLoadMusicListings() {
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

	static void saveAnListing(String title) {
		MusicDBManager dbManager = new MusicDBManager(mContext);
		MusicListingItem item = new MusicListingItem();
		item.setTitle(title);
		dbManager.addMusicListingItem(item);

		dbManager.closeDB();
	}

	static void deleteAnListing(int listingId) {
		MusicDBManager dbManager = new MusicDBManager(mContext);
		MusicListingItem item = new MusicListingItem();
		item.setId(listingId);
		dbManager.deleteMusicListingItem(item);
		dbManager.closeDB();
	}

	static void deleteMusicInfos(int listingId) {
		MusicDBManager dbManager = new MusicDBManager(mContext);
		dbManager.deleteMusicInfos(listingId);
		dbManager.closeDB();

	}

	static void deletAnMusicInfo(long musicInfoID) {
		MusicDBManager dbManager = new MusicDBManager(mContext);
		dbManager.deleteMusicInfo(musicInfoID);
		dbManager.closeDB();
	}

	static void deletMusicInfos(List<MusicInfo> infos) {
		MusicDBManager dbManager = new MusicDBManager(mContext);
		for (MusicInfo info : infos) {
			dbManager.deleteMusicInfo(info.getId());
		}
		dbManager.closeDB();
	}

	static List<MusicInfo> loadSelectPlayList(int musicListID) {

		MusicDBManager dbManager = new MusicDBManager(mContext);
		List<MusicInfo> infos = dbManager.queryMusicInfos(musicListID);
		dbManager.closeDB();
		return infos;
	}

	static void openPlayListing(int musicListID) {
		PlayListingFragment.SelectPlayLists = loadSelectPlayList(musicListID);
		isListingFragment = false;
		mFM.beginTransaction().replace(R.id.fl_music_listing_container, new PlayListingFragment()).commit();
	}

	static boolean isListingFragment = false;

	/**
	 * 
	 * @author Administrator
	 * 
	 */

	
	//---------------------------onKeyDown------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		
//		if(isListingFragment){
//			
//			if (keyCode == KeyEvent.KEYCODE_BACK) {
//				if (PlayListingFragment.isDelMode) {
//					PlayListingFragment.playListingFragmentCancelKey();
//					setToPlayListingFragment();
//					return true;
//				} else {
//					setToMusicListingFragment();
//					return true;
//					// return super.onKeyDown(keyCode, event);
//				}
//			}
//		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (isListingFragment) {
				return super.onKeyDown(keyCode, event);
			} else {
				if (PlayListingFragment.isDelMode) {
					PlayListingFragment.playListingFragmentCancelKey();
					setToPlayListingFragment();
					return true;
				} else {
					setToMusicListingFragment();
					return true;
					// return super.onKeyDown(keyCode, event);
				}
			}

		} else {
			return super.onKeyDown(keyCode, event);
		}
//		return super.onKeyDown(keyCode, event);
	

	}

	static public class PlayListingFragment extends Fragment implements OnClickListener {

		private static List<MusicInfo> SelectPlayLists; // 当前选中的音乐列表
		private static int SelectPlayListsPS; // 当前选中的音乐列表选中位置
		static List<MusicInfo> hasChoose;
		private ListView listView;
		private static Button bt_add_del;
		private Button bt_all_inv;
		// private Button bt_del;
		private TextView tv_hasChoosed;
		private TextView tv_play_listing_title;
		private static boolean isDelMode = false;

		private static PlayListingAdapter adapter;
		private static HashMap<Integer, Boolean> HashMap;
		private static boolean isSelectedAll = false;
		private static boolean isShowInv = false;
		private static int hasSelectedCount = 0; // 用来统计当前选中的数目

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);

			View view = inflater.inflate(R.layout.f_play_listing, null);

			SelectPlayLists = loadSelectPlayList(MusicListingFragment.SelectListings.get(MusicListingFragment.SelectListingPS).getId());

			tv_play_listing_title = (TextView) view.findViewById(R.id.tv_play_listing_title);
			tv_hasChoosed = (TextView) view.findViewById(R.id.tv_play_listing);
			bt_add_del = (Button) view.findViewById(R.id.bt_play_listing_add_del);
			// bt_del = (Button) view.findViewById(R.id.bt_play_listing_del);
			bt_all_inv = (Button) view.findViewById(R.id.bt_play_listing_all_inv);
			bt_add_del.setOnClickListener(this);
			// bt_del.setOnClickListener(this);
			bt_all_inv.setOnClickListener(this);
			tv_play_listing_title.setText(MusicListingFragment.SelectListings.get(MusicListingFragment.SelectListingPS).getTitle() + ":");
			// SelectPlayLists;
			tv_hasChoosed.setVisibility(View.INVISIBLE);
			// bt_add.setVisibility(View.INVISIBLE);
			// bt_del.setVisibility(View.INVISIBLE);
			bt_all_inv.setVisibility(View.INVISIBLE);
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
						if (holder.mCB_Choose.isChecked()) {
							hasSelectedCount++;
							tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
							Toast.makeText(mContext, "Has select " + hasSelectedCount, Toast.LENGTH_SHORT).show();
							if (hasSelectedCount == HashMap.size()) {
								isShowInv = true;
								bt_all_inv.setText("取消全选");
							}
						} else {

							hasSelectedCount--;
							tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
							Toast.makeText(mContext, "Has select " + hasSelectedCount, Toast.LENGTH_SHORT).show();
						}

					} else {
						if (SelectPlayLists.size() > 0) {
							MusicStaticPool.setCurPlayList(SelectPlayLists);
							MusicStaticPool.setCurPlayListPS(position);
							MusicStaticPool.setCurListingPS(MusicListingFragment.SelectListingPS);// 更新到播放器后台的信息状态池

							playMusic(MusicStaticPool.getCurPlayList().get(position).getUrl());

						}
					}

				}
			});
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					isDelMode = true;
					bt_add_del.setText("从列表中删除音乐");
					tv_hasChoosed.setVisibility(View.VISIBLE);
					// bt_add.setVisibility(View.VISIBLE);
					// bt_del.setVisibility(View.VISIBLE);
					bt_all_inv.setVisibility(View.VISIBLE);
					int count = SelectPlayLists.size();
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

			return view;
		}

		@Override
		public void onResume() {
			super.onResume();
			isDelMode = false;
			isSelectedAll = false;
			isShowInv = false;
			hasSelectedCount = 0;
			tv_hasChoosed.setText("已经选中0项");
			bt_all_inv.setText("全选");
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
				int count = SelectPlayLists.size();
				HashMap = new HashMap<Integer, Boolean>();
				for (int i = 0; i < count; i++) {
					HashMap.put(i, false);
				}
			}

			@Override
			public int getCount() {

				return SelectPlayLists.size();
			}

			@Override
			public Object getItem(int position) {
				return SelectPlayLists.get(position);
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
				holder.mTV_title.setText(SelectPlayLists.get(position).getTitle() + "");
				holder.mTV_artist.setText(SelectPlayLists.get(position).getArtist() + "");
				holder.mTV_duration.setText(MusicInfo.getDurStr(SelectPlayLists.get(position).getDuration()) + "");

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
				if (((CompoundButton) v).isChecked()) {
					HashMap.put(p, true);
					hasSelectedCount++;
					tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
					Toast.makeText(mContext, "Has select " + hasSelectedCount, Toast.LENGTH_SHORT).show();
					if (hasSelectedCount == HashMap.size()) {
						isShowInv = true;
						bt_all_inv.setText("取消全选");
					}
				} else {
					HashMap.put(p, false);
					hasSelectedCount--;
					tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
					Toast.makeText(mContext, "Has select " + hasSelectedCount, Toast.LENGTH_SHORT).show();
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

		public static void playListingFragmentCancelKey() {
			Toast.makeText(mContext, "playListingFragmentCancelKey", Toast.LENGTH_SHORT).show();
			if (adapter != null) {
				bt_add_del.setText("添加音乐到当前列表");
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
					hasChoose = new ArrayList<MusicInfo>();
					for (int i = 0; i < HashMap.size(); i++) {
						if (HashMap.get(i)) {
							hasChoose.add(SelectPlayLists.get(i));
						}
					}
					deletMusicInfos(hasChoose);
					// 删除后重新加载
					SelectPlayLists = loadSelectPlayList(MusicListingFragment.SelectListings.get(MusicListingFragment.SelectListingPS)
							.getId());

					MusicStaticPool.setCurListing(reSetLoadMusicListings());
					MusicStaticPool.setCurPlayList(SelectPlayLists);// 同步到播放静态池中去
					tv_hasChoosed.setVisibility(View.INVISIBLE);
					// bt_add.setVisibility(View.INVISIBLE);
					// bt_del.setVisibility(View.INVISIBLE);
					bt_all_inv.setVisibility(View.INVISIBLE);
					adapter.notifyDataSetChanged();

				}
			}
			if (v == bt_all_inv) {
				if (!isDelMode) {
					return;
				}
				if (hasSelectedCount == HashMap.size()) {
					isShowInv = true;
					hasSelectedCount = 0;
					bt_all_inv.setText("全选");
					tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
					for (int i = 0; i < HashMap.size(); i++) {
						HashMap.put(i, false);
					}
					Toast.makeText(mContext, "has select " + hasSelectedCount, Toast.LENGTH_SHORT).show();
					adapter.notifyDataSetChanged();
				} else {
					bt_all_inv.setText("取消全选");
					isSelectedAll = true;
					hasSelectedCount = HashMap.size();
					tv_hasChoosed.setText("已经选中" + hasSelectedCount + "项");
					for (int i = 0; i < HashMap.size(); i++) {
						HashMap.put(i, true);
					}
					Toast.makeText(mContext, "has select " + hasSelectedCount, Toast.LENGTH_SHORT).show();
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
			SelectPlayLists = (List<MusicInfo>) data.getSerializableExtra(FileSelectResult);
			MusicDBManager dbManager = new MusicDBManager(mContext);
			dbManager.addMusicInfos(SelectPlayLists, MusicListingFragment.SelectListings.get(MusicListingFragment.SelectListingPS).getId());
			MusicListingItem item = MusicListingFragment.SelectListings.get(MusicListingFragment.SelectListingPS);
			item.setCount(MusicListingFragment.SelectListings.get(MusicListingFragment.SelectListingPS).getCount() + SelectPlayLists.size());

			dbManager.updateMusicListingItem(item);
			SelectPlayLists = dbManager.queryMusicInfos(MusicListingFragment.SelectListings.get(MusicListingFragment.SelectListingPS)
					.getId());// 保存之后再重新加载
			dbManager.closeDB();
			MusicListingFragment.SelectListings = reSetLoadMusicListings();
			MusicStaticPool.setCurListing(MusicListingFragment.SelectListings);// 将数更新到播放的信息状态池中
			adapter.notifyDataSetChanged();// 更新视图

		}
	} // END of Fragment

	/**
	 * 列表标题
	 * 
	 * @author Administrator
	 * 
	 */

	static public class MusicListingFragment extends Fragment implements OnClickListener {

		private ListView listView;
		// private Button bt_ok;
		// private int CurListingItemPS;
		private Button bt_add;
		private MusicListingAdapter mListingAdapter;
		private static boolean isShowDelBT = false;
		private static int SelectListingPS = -1; // 记录当前选中的状态
		private static List<MusicListingItem> SelectListings;

		// private static int SelectListingsPS ;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			View view = inflater.inflate(R.layout.f_music_listing, null);
			// bt_ok = (Button) view.findViewById(R.id.bt_music_listing_ok);
			bt_add = (Button) view.findViewById(R.id.bt_music_listing_add);
			bt_add.setOnClickListener(this);
			// bt_ok.setOnClickListener(this);
			listView = (ListView) view.findViewById(R.id.lv_music_listing);

			SelectListings = reSetLoadMusicListings();// 重新加载
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (isShowDelBT) {
						Toast.makeText(mContext, "别闹，" + position + "~", Toast.LENGTH_SHORT).show();
					} else {
						SelectListingPS = position;
						// CurSelectMusicListingID =
						// MusicStaticPool.getCurListing().get(position).getId();
						openPlayListing(SelectListings.get(position).getId());
					}
				}
			});
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					isShowDelBT = true;
					mListingAdapter.notifyDataSetChanged();
					// bt_ok.setVisibility(View.VISIBLE);

					// bt_add.setVisibility(View.VISIBLE);
					return false;
				}
			});

			// mMusicListings = reSetLoadMusicListings();
			mListingAdapter = new MusicListingAdapter(mContext);
			listView.setAdapter(mListingAdapter);
			return view;

		}

		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			isShowDelBT = false;
			SelectListings = reSetLoadMusicListings();
			mListingAdapter.notifyDataSetChanged();

		}

		@Override
		public void onClick(View v) {

			// if (v == bt_ok) {
			// isShowDelBT = false;
			// mListingAdapter.notifyDataSetChanged();
			// bt_ok.setVisibility(View.INVISIBLE);
			//
			// // bt_add.setVisibility(View.INVISIBLE);
			// }

			if (v == bt_add) {
				// isShowDelBT = true;
				// mListingAdapter.notifyDataSetChanged();
				showInputTitleDialog();
			}
		}

		void showDelListingDialog(String title, int listingId) {

			final int id = listingId;
			final String t = title;
			new AlertDialog.Builder(mContext)
			// 对话框的标题
					.setTitle("删除列表:" + title + "？")
					// 设定显示的View
					.setPositiveButton("删除", new DialogInterface.OnClickListener() {
						@SuppressLint("NewApi")
						public void onClick(DialogInterface dialog, int whichButton) {
							deleteAnListing(id);
							deleteMusicInfos(id);
							Toast.makeText(mContext, "已删除 " + t + "列表", Toast.LENGTH_SHORT).show();

							isShowDelBT = false;
							SelectListings = reSetLoadMusicListings();
							MusicStaticPool.setCurListing(reSetLoadMusicListings());
							mListingAdapter.notifyDataSetChanged();
						}
					})
					// 对话框的“退出”单击事件
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							isShowDelBT = false;
							mListingAdapter.notifyDataSetChanged();
						}
					})

					.create().show();

		}

		// 显示对话框
		public void showInputTitleDialog() {

			LayoutInflater factory = LayoutInflater.from(mContext);
			// 把activity_login中的控件定义在View中
			final View textEntryView = factory.inflate(R.layout.dialog_input_listing_title, null);

			// 控件显示在对话框中
			new AlertDialog.Builder(mContext)
			// 对话框的标题
					.setTitle("新建列表")
					// 设定显示的View
					.setView(textEntryView).setPositiveButton("完成", new DialogInterface.OnClickListener() {
						@SuppressLint("NewApi")
						public void onClick(DialogInterface dialog, int whichButton) {
							final EditText etInput = (EditText) textEntryView.findViewById(R.id.et_dialog_input_title);
							String title = etInput.getText().toString().trim();

							if (title == null | title.equals("")) {
								Toast.makeText(mContext, "新建标题不能为空", Toast.LENGTH_SHORT).show();
								showInputTitleDialog();
							} else {
								saveAnListing(title);
								MusicStaticPool.setCurListing(SelectListings);
								SelectListings = reSetLoadMusicListings();
								mListingAdapter.notifyDataSetChanged();
							}

						}
					})
					// 对话框的“退出”单击事件
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					})

					.create().show();
		}

		class MusicListingAdapter extends BaseAdapter {
			Context context;
			LayoutInflater inflater;

			public MusicListingAdapter() {

			}

			public MusicListingAdapter(Context context) {
				this.context = context;

				inflater = LayoutInflater.from(context);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return SelectListings.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return SelectListings.get(position);
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				ViewHolder holder = null;
				if (convertView == null) {
					holder = new ViewHolder();
					convertView = inflater.inflate(R.layout.item_music_listing, null);
					holder.mET_title = (TextView) convertView.findViewById(R.id.et_listing_title);
					holder.mBT_del = (Button) convertView.findViewById(R.id.bt_listing_del);
					holder.mTV_count = (TextView) convertView.findViewById(R.id.tv_listing_count);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				if (isShowDelBT) {
					holder.mBT_del.setVisibility(View.VISIBLE);
					holder.mET_title.setFocusable(true);
					// holder.mET_title.requestFocus() ;
					// holder.mET_title.setOnClickListener(new
					// OnDelItemClick(position));

					holder.mET_title.setFocusableInTouchMode(true);

					holder.mBT_del.setOnClickListener(new OnDelItemClick(position));
				} else {
					holder.mBT_del.setVisibility(View.INVISIBLE);
					holder.mET_title.setFocusable(false);
					holder.mET_title.setFocusableInTouchMode(false);
					// holder.mET_title.clearFocus();

				}

				holder.mET_title.setText(SelectListings.get(position).getTitle());

				if (MusicStaticPool.getCurListingPS() == position) {
					holder.mET_title.setTextColor(Color.RED);
				} else {
					holder.mET_title.setTextColor(Color.BLACK);
				}
				holder.mTV_count.setText(SelectListings.get(position).getCount() + "");

				for (MusicListingItem item : SelectListings) {
					System.out.println("MusicListingItem getCount" + item.getCount() + " getId" + item.getId());
				}
				return convertView;
			}

		}

		class OnDelItemClick implements OnClickListener {
			int ps;

			public OnDelItemClick(int p) {
				ps = p;
			}

			@Override
			public void onClick(View v) {
				showDelListingDialog(SelectListings.get(ps).getTitle(), SelectListings.get(ps).getId());
			}

		}

		class ViewHolder {
			TextView mET_title;
			TextView mTV_count;
			Button mBT_del;
		}
	}// END of Fragemnt

	private static void playMusic(String path) {

		MusicPlayerService.playNext(path);
	}

}
