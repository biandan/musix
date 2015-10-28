package com.yikang.ykmusix.been;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDBManager {

	String MusicListingDBName = "ykmusix_music_listing.db";
	String MusicPlayListingDBName = "ykmusix_music_play_listing.db";
	int DBVersion = 1;
	String musicListingTable = "music_listing";
	String musicPlayingListingTable = "music_play_listing";

	private MusicListingDBHelp mMusicListingDBHelp;
	PlayListingDBHelp mPlayListingDBHelp;
	private SQLiteDatabase mMusicDB;
	private SQLiteDatabase mListingDB;

	public MusicDBManager(Context context) {

		mMusicListingDBHelp = new MusicListingDBHelp(context);
		mMusicDB = mMusicListingDBHelp.getWritableDatabase();

		mPlayListingDBHelp = new PlayListingDBHelp(context);
		mListingDB = mPlayListingDBHelp.getWritableDatabase();

	}

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
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,music_listing_id INTEGER ,title VARCHAR,music_url VARCHAR, artist VARCHAR, duration INTEGER, size INTEGER , albunm_id INTEGER,album VARCHAR)");

			System.out.println("CREATE TABLE " + musicPlayingListingTable);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("ALTER TABLE " + musicPlayingListingTable + " ADD COLUMN other STRING");
			System.out.println("ALTER TABLE " + musicPlayingListingTable);
		}

	}

	public void addMusicListingItem(MusicListingItem item) {
		mMusicDB.beginTransaction(); // 开始事务
		try {
			mMusicDB.execSQL("INSERT INTO " + musicListingTable + " VALUES(null, ?, ?, ?)", new Object[] { item.title, item.count,
					item.musicListID });
			mMusicDB.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			mMusicDB.endTransaction(); // 结束事务
		}
	}

	public void addMusicInfo(MusicInfo info) {
		mListingDB.beginTransaction(); // 开始事务
		try {
			mListingDB.execSQL(
					"INSERT INTO " + musicPlayingListingTable + " VALUES(null, ?, ?, ?, ?, ?, ? , ?,?)",
					new Object[] { info.getMusicListingID(), info.getTitle(), info.getUrl(), info.getArtist(), info.getDuration(),
							info.getSize(),info.getAlbumID(),info.getAlbum() });
			mListingDB.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			mListingDB.endTransaction(); // 结束事务
		}
	}

	public void addMusicInfos(List<MusicInfo> infos, int musicListingID) {
		mListingDB.beginTransaction(); // 开始事务
		try {
			for (MusicInfo info : infos) {
				mListingDB.execSQL("INSERT INTO " + musicPlayingListingTable + " VALUES(null, ?, ?, ?, ?, ?, ?,?,?)", new Object[] {
						musicListingID, info.getTitle(), info.getUrl(), info.getArtist(), info.getDuration(), info.getSize(),info.getAlbumID(),info.getAlbum() });
			}

			mListingDB.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			mListingDB.endTransaction(); // 结束事务
		}
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
			infos.add(info);
		}
		c.close();
		return infos;
	}

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
