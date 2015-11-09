package com.yikang.ykmusix.model;

import java.util.ArrayList;
import java.util.List;

import com.yikang.ykmusix.been.MusicInfo;
import com.yikang.ykmusix.been.MusicListingItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDBManager {

	String MusicListingDBName = "ykmusix_music_listing.db";
	String MusicPlayListingDBName = "ykmusix_music_play_listing.db";
	int DBVersion = 1;
	/**
	 * 列表
	 */
	String musicListingTable = "music_listing";

	/**
	 * 歌曲列表
	 */
	String musicPlayingListingTable = "music_play_listing";

	/**
	 * 操作数据 列表
	 */
	private MusicListingDBHelp mMusicListingDBHelp;
	/**
	 * 数据操作 歌曲列表
	 */
	private PlayListingDBHelp mPlayListingDBHelp;
	/**
	 * 数据库 列表
	 */
	private SQLiteDatabase mMusicDB;
	/**
	 * 数据库 音乐列表
	 */
	private SQLiteDatabase mListingDB;

	public MusicDBManager(Context context) {

		mMusicListingDBHelp = new MusicListingDBHelp(context);
		mMusicDB = mMusicListingDBHelp.getWritableDatabase();

		mPlayListingDBHelp = new PlayListingDBHelp(context);
		mListingDB = mPlayListingDBHelp.getWritableDatabase();

	}

	/**
	 * 保存音乐列表 操作的数据库类
	 * 
	 * @author Administrator
	 * 
	 */
	class MusicListingDBHelp extends SQLiteOpenHelper {
		public MusicListingDBHelp(Context context) {

			super(context, MusicPlayListingDBName, null, DBVersion);
			System.out.println(" MusicListingDBHelp(Context context)");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + musicListingTable
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, count INTEGER, playlist_id INTEGER)");
			System.out.println("CREATE TABLE " + musicListingTable);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("ALTER TABLE " + musicListingTable + " ADD COLUMN other STRING");
			System.out.println("ALTER TABLE " + musicListingTable);
		}

	}

	/**
	 * 保存列表歌曲
	 * 
	 * @author Administrator
	 * 
	 */
	class PlayListingDBHelp extends SQLiteOpenHelper {

		public PlayListingDBHelp(Context context) {
			super(context, MusicListingDBName, null, DBVersion);

			Cursor c = mMusicDB.rawQuery("select * from " + musicListingTable + " where title=?", new String[] { "系统列表" });

			if (c.getCount() <= 0) {
				mMusicDB.beginTransaction(); // 开始事务
				try {
					mMusicDB.execSQL("INSERT INTO " + musicListingTable + " VALUES(null, ?, ?, ?)", new Object[] { "系统列表", 0, null });
					mMusicDB.setTransactionSuccessful(); // 设置事务成功完成
				} finally {
					mMusicDB.endTransaction(); // 结束事务
				}
				System.out.println("已经创建  系统列表");
			}

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ musicPlayingListingTable
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,music_listing_id INTEGER ,title VARCHAR,music_url VARCHAR," +
					" artist VARCHAR, duration INTEGER, size INTEGER , albunm_id INTEGER,album VARCHAR,lrc_url VARCHAR)");

			System.out.println("已经创建：" + musicPlayingListingTable);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("ALTER 创建： " + musicPlayingListingTable + " ADD COLUMN other STRING");
			System.out.println("ALTER TABLE " + musicPlayingListingTable);
		}

	}

	public void addMusicListingItem(MusicListingItem item) {
		mMusicDB.beginTransaction(); // 开始事务
		try {
			mMusicDB.execSQL("INSERT INTO " + musicListingTable + " VALUES(null, ?, ?, ?)", new Object[] { item.getTitle(), item.getCount(),
					item.getMusicListID() });
			mMusicDB.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			mMusicDB.endTransaction(); // 结束事务
		}
	}

	/**
	 * 保存单首歌曲信息
	 * 
	 * @param info
	 */
	public void addMusicInfo(MusicInfo info) {
		mListingDB.beginTransaction(); // 开始事务
		try {
			mListingDB.execSQL(
					"INSERT INTO " + musicPlayingListingTable + " VALUES(null, ?, ?, ?, ?, ?, ? , ?,?,?)",
					new Object[] { info.getMusicListingID(), info.getTitle(), info.getUrl(), info.getArtist(), info.getDuration(),
							info.getSize(), info.getAlbumID(), info.getAlbum(), info.getLrcUrl() });
			mListingDB.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			mListingDB.endTransaction(); // 结束事务
		}
	}

	/**
	 * 批量保存音乐文件信息
	 * 
	 * @param infos
	 * @param musicListingID
	 */
	public void addMusicInfos(List<MusicInfo> infos, int musicListingID) {
		mListingDB.beginTransaction(); // 开始事务
		try {
			for (MusicInfo info : infos) {
				mListingDB.execSQL("INSERT INTO " + musicPlayingListingTable + " VALUES(null, ?, ?, ?, ?, ?, ?, ? , ?,? )",
						new Object[] { musicListingID, info.getTitle(), info.getUrl(), info.getArtist(), info.getDuration(),
								info.getSize(), info.getAlbumID(), info.getAlbum(), info.getLrcUrl() });
			}

			mListingDB.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			mListingDB.endTransaction(); // 结束事务
		}
	}

	/**
	 * 更新音乐信息中的 LRC 中的URL信息
	 * 
	 * @param info
	 */
	public void updateMusicInfoLrcUrl(MusicInfo info) {
		ContentValues cv = new ContentValues();
		cv.put("lrc_url", info.getLrcUrl());
		int conut = mMusicDB.update(musicPlayingListingTable, cv, "_id = ?", new String[] { info.getId() + "" });
		System.out.println("已经更新 " + musicPlayingListingTable + ":" + conut);
	}

	public void updateMusicListingItem(MusicListingItem item) {
		ContentValues cv = new ContentValues();
		cv.put("title", item.getTitle());
		cv.put("count", item.getCount());
		int conut = mMusicDB.update(musicListingTable, cv, "_id = ?", new String[] { item.getId() + "" });
		System.out.println("已经更新 MusicListing：" + conut);
	}

	public void deleteMusicListingItem(MusicListingItem item) {
		int i = mMusicDB.delete(musicListingTable, "_id= ?", new String[] { String.valueOf(item.getId()) });
		System.out.println("已经删除：" + i + " MusicListingItem");
	}

	/**
	 * 删除单个音乐信息
	 * 
	 * @param infoID
	 */
	public void deleteMusicInfo(long infoID) {
		int i = mListingDB.delete(musicPlayingListingTable, "_id= ?", new String[] { String.valueOf(infoID) });
		System.out.println("已经删除：" + i + " MusicInfo");
	}

	public void deleteMusicInfos(int listingID) {
		int i = mListingDB.delete(musicPlayingListingTable, "music_listing_id= ?", new String[] { String.valueOf(listingID) });
		System.out.println("已经删除：" + i + " MusicInfo");
	}

	public List<MusicListingItem> queryAllMusicListingItem() {
		ArrayList<MusicListingItem> items = new ArrayList<MusicListingItem>();
		Cursor c = mMusicDB.query(musicListingTable, null, null, null, null, null, null);
		while (c.moveToNext()) {
			MusicListingItem item = new MusicListingItem();
			item.setId(c.getInt(c.getColumnIndex("_id")));
			item.setTitle(c.getString(c.getColumnIndex("title")));
			item.setCount(c.getInt(c.getColumnIndex("count")));
			items.add(item);
		}
		c.close();
		return items;
	}

	/**
	 * 查询所有的音乐曲目的信息
	 * 
	 * @return
	 */
	public List<MusicInfo> queryAllMusicInfos() {
		ArrayList<MusicInfo> infos = new ArrayList<MusicInfo>();
		Cursor c = mListingDB.query(musicPlayingListingTable, null, null, null, null, null, null);
		while (c.moveToNext()) {
			MusicInfo info = new MusicInfo();
			info.setId(c.getInt(c.getColumnIndex("_id")));
			info.setMusicListingID(c.getInt(c.getColumnIndex("music_listing_id")));
			info.setTitle(c.getString(c.getColumnIndex("title")));
			info.setUrl(c.getString(c.getColumnIndex("music_url")));
			info.setArtist(c.getString(c.getColumnIndex("artist")));
			info.setDuration(c.getInt(c.getColumnIndex("duration")));
			info.setSize(c.getInt(c.getColumnIndex("size")));

			info.setAlbumID(c.getInt(c.getColumnIndex("albunm_id")));
			info.setAlbum(c.getString(c.getColumnIndex("album")));
			info.setLrcUrl(c.getString(c.getColumnIndex("lrc_url")));
			infos.add(info);
		}
		c.close();
		return infos;
	}

	/**
	 * 查询特定音乐列表里面的所有音乐
	 * 
	 * @param music_listing_id
	 * @return
	 */
	public List<MusicInfo> queryMusicInfos(int music_listing_id) {
		ArrayList<MusicInfo> infos = new ArrayList<MusicInfo>();
		Cursor c = mListingDB.rawQuery("select * from " + musicPlayingListingTable + " where music_listing_id=?",
				new String[] { music_listing_id + "" });
		System.out.println("queryMusicInfos count " + c.getCount());
		while (c.moveToNext()) {
			MusicInfo info = new MusicInfo();
			info.setId(c.getInt(c.getColumnIndex("_id")));
			info.setMusicListingID(c.getInt(c.getColumnIndex("music_listing_id")));
			info.setTitle(c.getString(c.getColumnIndex("title")));
			info.setUrl(c.getString(c.getColumnIndex("music_url")));
			info.setArtist(c.getString(c.getColumnIndex("artist")));
			info.setDuration(c.getInt(c.getColumnIndex("duration")));
			info.setSize(c.getInt(c.getColumnIndex("size")));
			info.setAlbumID(c.getInt(c.getColumnIndex("albunm_id")));
			info.setAlbum(c.getString(c.getColumnIndex("album")));
			info.setLrcUrl(c.getString(c.getColumnIndex("lrc_url")));
			infos.add(info);
		}
		c.close();
		return infos;
	}

	public long queryMusicInfoCount(int music_listing_id) {
		Cursor c = mListingDB.rawQuery("select * from " + musicPlayingListingTable + " where music_listing_id=?",
				new String[] { music_listing_id + "" });
		long count = c.getColumnCount();
		System.out.println("queryMusicInfoCount music_listing_id " + music_listing_id + " count " + count);
		c.close();
		return count;
	}

	public void closeDB() {
		if (mListingDB.isOpen()) {
			mListingDB.close();
		}
		if (mMusicDB.isOpen()) {
			mMusicDB.close();
		}

	}

}
