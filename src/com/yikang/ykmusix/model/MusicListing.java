package com.yikang.ykmusix.model;

import java.util.List;

import android.content.Context;

import com.yikang.ykmusix.been.MusicInfo;
import com.yikang.ykmusix.been.MusicListingItem;


/**
 * MusicListing的业务逻辑层，实现对MusicListing的各种支持
 * @author Administrator
 *
 */
public class MusicListing {
	Context mContext;

	public MusicListing(Context mContext) {
		this.mContext = mContext;
	}

	public List<MusicListingItem> reSetLoadMusicListings() {
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

	public void saveAnListing(String title) {
		MusicDBManager dbManager = new MusicDBManager(mContext);
		MusicListingItem item = new MusicListingItem();
		item.setTitle(title);
		dbManager.addMusicListingItem(item);

		dbManager.closeDB();
	}

	public void deleteAnListing(int listingId) {
		MusicDBManager dbManager = new MusicDBManager(mContext);
		MusicListingItem item = new MusicListingItem();
		item.setId(listingId);
		dbManager.deleteMusicListingItem(item);
		dbManager.closeDB();
	}

	public void deleteMusicInfos(int listingId) {
		MusicDBManager dbManager = new MusicDBManager(mContext);
		dbManager.deleteMusicInfos(listingId);
		dbManager.closeDB();

	}

	void deletAnMusicInfo(long musicInfoID) {
		MusicDBManager dbManager = new MusicDBManager(mContext);
		dbManager.deleteMusicInfo(musicInfoID);
		dbManager.closeDB();
	}

	public void deletMusicInfos(List<MusicInfo> infos) {
		MusicDBManager dbManager = new MusicDBManager(mContext);
		for (MusicInfo info : infos) {
			dbManager.deleteMusicInfo(info.getId());
		}
		dbManager.closeDB();
	}

	public List<MusicInfo> loadSelectPlayList(int musicListID) {

		MusicDBManager dbManager = new MusicDBManager(mContext);
		List<MusicInfo> infos = dbManager.queryMusicInfos(musicListID);
		dbManager.closeDB();
		return infos;
	}

}
