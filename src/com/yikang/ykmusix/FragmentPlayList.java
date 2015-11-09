package com.yikang.ykmusix;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.yikang.ykmusix.been.MusicInfo;
import com.yikang.ykmusix.been.MusicListingItem;
import com.yikang.ykmusix.been.MusicStaticPool;
import com.yikang.ykmusix.been.ViewHolder;
import com.yikang.ykmusix.model.MusicDBManager;



/**
 * 当前播放音乐文件列表 在主界面中显示 MainMusixActivity中的Fragemt
 * @author Administrator
 *
 */
public class FragmentPlayList extends Fragment{
	


	ListView listView;
	static PlayingListAdapter MusicAdapter;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		System.out.println("PlayListFragment onCreateView");
		View view = inflater.inflate(R.layout.f_music_play_list, null);
		listView = (ListView) view.findViewById(R.id.lv_music_play_list);
		MusicAdapter = new PlayingListAdapter(MainMusixActivity.mContext);
		listView.setAdapter(MusicAdapter);

		// 默认显示系统列表
		if (MusicStaticPool.getCurPlayListPS() == -1) {
			MusicStaticPool.setCurListing(reSetLoadMusicListings());
			MusicStaticPool.setCurListingPS(0);
			MusicStaticPool.setCurPlayList(loadSelectPlayList(MusicStaticPool.getCurListing().get(0).getId()));
		}

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MusicStaticPool.setCurPlayListPS(position);
				MainMusixActivity.musicPlay(MusicStaticPool.getCurPlayList().get(position).getUrl());
				Toast.makeText(MainMusixActivity.mContext, "播放" + MusicStaticPool.getCurPlayList().get(position).getTitle(), 1000).show();
				MainMusixActivity.mBT_Music_Play.setBackgroundResource(R.drawable.btn_pause_normal);
				MusicAdapter.notifyDataSetChanged();

			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		System.out.println("onResume");
		if (MainMusixActivity.mMusicPlayerService != null) {
			dataHavedChanged();

		}

	}

	public static void dataHavedChanged() {
		if (MusicAdapter != null) {
			MusicAdapter.notifyDataSetChanged();
		} else {
			System.out.println("MusicAdapter = null ");
		}

	}

	class PlayingListAdapter extends BaseAdapter {

		Context context;
		LayoutInflater inflater;

		public PlayingListAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
			// initData();
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			// initData();
		}

		@Override
		public int getCount() {
			return MusicStaticPool.getCurPlayList().size();
		}

		@Override
		public Object getItem(int position) {
			return MusicStaticPool.getCurPlayList().get(position);
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
				convertView = inflater.inflate(R.layout.item_main_music_playing_list, null);
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_show_music_title);
				holder.tv_artist = (TextView) convertView.findViewById(R.id.tv_show_music_artist);
				holder.tv_duration = (TextView) convertView.findViewById(R.id.tv_show_music_duration);

				// 为view设置标签
				convertView.setTag(holder);
			} else {
				// 取出holder
				holder = (ViewHolder) convertView.getTag();
			}
			// 设置list中TextView的显示
			holder.tv_title.setText(MusicStaticPool.getCurPlayList().get(position).getTitle());
			if (MusicStaticPool.getCurPlayListPS() == position) {
				holder.tv_title.setTextColor(Color.RED);
			} else {
				holder.tv_title.setTextColor(Color.BLACK);
			}
			holder.tv_artist.setText(MusicStaticPool.getCurPlayList().get(position).getArtist());

			holder.tv_duration.setText(MusicInfo.getDurStr(MusicStaticPool.getCurPlayList().get(position).getDuration()) + "");
			// 根据isSelected来设置checkbox的选中状况

			return convertView;
		}

	}

	static private List<MusicListingItem> reSetLoadMusicListings() {
		MusicDBManager dbMusicManager = new MusicDBManager(MainMusixActivity.mContext);

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
		MusicDBManager dbManager = new MusicDBManager(MainMusixActivity.mContext);
		List<MusicInfo> infos = dbManager.queryMusicInfos(musicListID);
		dbManager.closeDB();
		return infos;

	}


}
