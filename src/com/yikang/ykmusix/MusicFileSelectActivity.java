package com.yikang.ykmusix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yikang.ykmusix.been.MusicInfo;
import com.yikang.ykmusix.been.MusicStaticPool;
import com.yikang.ykmusix.been.ViewHolder;
import com.yikang.ykmusix.model.MusicFilter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 
 * @author Administrator
 *
 */
public class MusicFileSelectActivity extends Activity {

	private static final String FileSelectResult = "Result";

	ListView mLV_MusicShow;

	RelativeLayout mLayout;
	LinearLayout mLayoutBTPerent;
	Button mBT_all;
	Button mBT_invert;
	Button mBT_cancel;
	Button mBT_ok;
	MusicAdapter mAdapter;
	static HashMap<Integer, MusicInfo> mHashMap = new HashMap<Integer, MusicInfo>();
	List<MusicInfo> mMusicList = new ArrayList<MusicInfo>();
	List<MusicInfo> mMusicSelectList = new ArrayList<MusicInfo>();

	public MusicAdapter getAdapter() {
		return mAdapter;
	}

	private void dataChanged() {
		// 通知listView刷新
		mAdapter.notifyDataSetChanged();

	};

	@Override
	protected void onResume() {
		super.onResume();
		if (MusicStaticPool.isExitApp()) {
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_music_select);
		mLayout = (RelativeLayout) findViewById(R.id.rl_select_root);
		mLayoutBTPerent = (LinearLayout) findViewById(R.id.ll_select_bt_perent);
		mBT_all = (Button) findViewById(R.id.bt_select_all);
		mBT_invert = (Button) findViewById(R.id.bt_select_invert);
		mBT_cancel = (Button) findViewById(R.id.bt_select_cancel);
		mBT_ok = (Button) findViewById(R.id.bt_select_ok);

		mBT_all.setOnClickListener(new ButtonOnClick());
		mBT_invert.setOnClickListener(new ButtonOnClick());
		mBT_cancel.setOnClickListener(new ButtonOnClick());
		mBT_ok.setOnClickListener(new ButtonOnClick());

		List<MusicInfo> ls = getMusicInfos(this);
		if (ls == null) {
			TextView child = new TextView(this);
			child.setText("找不到任何音乐文件！");
			mLayout.addView(child);
			mLayoutBTPerent.setVisibility(View.INVISIBLE);
			return;
		}

		mMusicList = ls;
		mAdapter = new MusicAdapter(this, mMusicList);
		mLV_MusicShow = (ListView) findViewById(R.id.lv_show_music);
		mLV_MusicShow.setOnItemClickListener(new ListItemOnClick());
		mLV_MusicShow.setAdapter(mAdapter);

	}

	public static List<MusicInfo> getMusicInfos(Context context) {
		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		List<MusicInfo> mp3Infos = new ArrayList<MusicInfo>();

		if (cursor == null) {

			return null;
		}
		for (int i = 0; i < cursor.getCount(); i++) {
			MusicInfo mp3Info = new MusicInfo();
			cursor.moveToNext();
			long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)); // 音乐id
			String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));// 音乐标题
			String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));// 艺术家
			long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));// 时长
			long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
			String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径
			int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));// 是否为音乐
			String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)); // 专辑
			long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));// 专辑 ID
			mp3Info.setId(id);
			mp3Info.setTitle(title);
			mp3Info.setArtist(artist);
			mp3Info.setDuration(duration);
			mp3Info.setSize(size);
			mp3Info.setUrl(url);
			mp3Info.setAlbum(album);
			mp3Info.setAlbumID(albumId);
			mp3Info.setLrcUrl("NO");
			if (isMusic != 0 && MusicFilter.filterMusicFile(mp3Info)) { // 只把音乐添加到集合当中
				mp3Infos.add(mp3Info);
			}
		}

		return mp3Infos;
	}

	class ButtonOnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == mBT_all) {
				HashMap<Integer, Boolean> list = getAdapter().getSelectedList();
				for (int i = 0; i < list.size(); i++) {
					getAdapter().getSelectedList().put(i, true);
				}
				dataChanged();
			}
			if (v == mBT_cancel) {
				HashMap<Integer, Boolean> list = getAdapter().getSelectedList();
				for (int i = 0; i < list.size(); i++) {
					getAdapter().getSelectedList().put(i, false);
				}
				dataChanged();
			}
			if (v == mBT_invert) {
				HashMap<Integer, Boolean> list = getAdapter().getSelectedList();
				for (int i = 0; i < list.size(); i++) {
					if (getAdapter().getSelectedList().get(i)) {
						getAdapter().getSelectedList().put(i, false);
					} else {
						getAdapter().getSelectedList().put(i, true);
					}
				}
				dataChanged();
			}
			if (v == mBT_ok) {
				mHashMap.clear();
				Intent intent = new Intent();
				// 把返回数据存入Intent

				int count = mAdapter.getSelectedList().size();
				int hasChooseCount = 0;
				for (int i = 0; i < count; i++) {
					if (mAdapter.getSelectedList().get(i)) {
						// mHashMap.put(hasChooseCount++, mMusicList.get(i)) ;
						mMusicSelectList.add(hasChooseCount++, mMusicList.get(i));
					}
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable(FileSelectResult, (Serializable) mMusicSelectList);
				// intent.putExtra("result", mHashMap);
				intent.putExtras(bundle);
				// 设置返回数据
				MusicFileSelectActivity.this.setResult(RESULT_OK, intent);
				// 关闭Activity
				MusicFileSelectActivity.this.finish();
			}

		}

	}

	class ListItemOnClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.cb_choose.toggle();
			getAdapter().getSelectedList().put(position, holder.cb_choose.isChecked());
		}

	}

	class MusicAdapter extends BaseAdapter {

		List<MusicInfo> list = new ArrayList<MusicInfo>();
		Context context;
		LayoutInflater inflater;
		private HashMap<Integer, Boolean> selectedList = new HashMap<Integer, Boolean>();

		public HashMap<Integer, Boolean> getSelectedList() {
			return selectedList;
		}

		public MusicAdapter(Context context, List<MusicInfo> list) {

			this.context = context;
			this.list = list;
			inflater = LayoutInflater.from(context);
			initData();
		}

		public MusicAdapter() {

		}

		void initData() {
			selectedList = new HashMap<Integer, Boolean>();
			for (int i = 0; i < list.size(); i++) {
				selectedList.put(i, false);
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				// 获得ViewHolder对象
				holder = new ViewHolder();
				// 导入布局并赋值给convertview
				convertView = inflater.inflate(R.layout.item_select_music_list, null);
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_show_music_title);
				holder.tv_artist = (TextView) convertView.findViewById(R.id.tv_show_music_artist);
				holder.tv_duration = (TextView) convertView.findViewById(R.id.tv_show_music_duration);
				holder.cb_choose = (CheckBox) convertView.findViewById(R.id.cb_show_music_choose);
				// 为view设置标签
				convertView.setTag(holder);
			} else {
				// 取出holder
				holder = (ViewHolder) convertView.getTag();
			}
			// 设置list中TextView的显示
			holder.tv_title.setText(list.get(position).getTitle());
			holder.tv_artist.setText(list.get(position).getArtist());
			holder.tv_duration.setText(MusicInfo.getDurStr(list.get(position).getDuration()) + "");
			// 根据isSelected来设置checkbox的选中状况
			holder.cb_choose.setChecked(selectedList.get(position));
			return convertView;
		}

	}

}
