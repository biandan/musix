package com.yikang.ykmusix.been;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.Serializable;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;


/**
 *一首歌曲包含的信息
 * @author Administrator
 *
 */
public class MusicInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	long musicListingID;
	String title;// 音乐标题
	String artist;// 艺术家
	long duration;// 时长
	String durationStr;// 时长
	String album;
	long albumID;
	long size; // 文件大小
	String url; // 文件路径
	int isMusic;// 是否为音乐
	long id; // 音乐id
	/**
	 * 歌曲对应的歌词地址
	 */
	String lrcUrl ;

	public String getLrcUrl() {
		return lrcUrl;
	}

	public void setLrcUrl(String lrcUrl) {
		this.lrcUrl = lrcUrl;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMusicListingID() {
		return musicListingID;
	}

	public void setMusicListingID(long musicListingID) {
		this.musicListingID = musicListingID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getDurationStr() {
		return durationStr;
	}

	public void setDurationStr(String durationStr) {
		this.durationStr = durationStr;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public long getAlbumID() {
		return albumID;
	}

	public void setAlbumID(long albumID) {
		this.albumID = albumID;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getIsMusic() {
		return isMusic;
	}

	public void setIsMusic(int isMusic) {
		this.isMusic = isMusic;
	}

	// --------------------------------------------------
	public static String getDurStr(long duration) {
		long time = duration;
		time /= 1000;
		// 求分
		long minute = time / 60;
		// 求秒
		long second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

	/**
	 * 从文件当中获取专辑封面位图,并且重新匹配大小
	 * 
	 * @param context
	 * @param songid
	 * @param albumid
	 * @return
	 */

	public static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
		int fitSize=600;
		Bitmap bm = null;
		Bitmap bm1 = null;
		if (albumid < 0 && songid < 0) {
			throw new IllegalArgumentException("Must specify an album or a song id");
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			FileDescriptor fd = null;
			if (albumid < 0) {
				Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			} else {
				Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			}
			options.inSampleSize = 1;
			// 只进行大小判断
			options.inJustDecodeBounds = true;
			// 调用此方法得到options得到图片大小
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// 我们的目标是在800pixel的画面上显示
			// 所以需要调用computeSampleSize得到图片缩放的比例
			options.inSampleSize = 1;

			// 我们得到了缩放的比例，现在开始正式读入Bitmap数据
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			// 根据options参数，减少所需要的内存

			int bmpWidth = options.outWidth;
			int bmpHeight = options.outHeight;
			Log.d("MusicInfo ", "before autoZooImg bmpWidth " + bmpWidth + " bmpHeight " + bmpHeight);

			options.inSampleSize = 1;
			bm = BitmapFactory.decodeFileDescriptor(fd, null, options);

			System.out.println("view bitmap h:" + bm.getHeight() + " w:" + bm.getWidth());

			Log.d("MusicInfo ", "view  bm h " + bm.getHeight() + " w " + bm.getWidth());
			return figSize(bm, fitSize);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Log.d("MusicInfo ", " getArtworkFromFile bm==null " + (bm == null));

		return bm;
	}

	static void figSmall(BitmapFactory.Options options, int fitSize) {
		int bmpWidth = options.outWidth;
		int bmpHeight = options.outHeight;
		float minSize = Math.min(bmpWidth, bmpHeight);
		double scale = 0;

		scale = Math.sqrt( (double)minSize/fitSize);
		BitmapFactory.Options options2=options ;
		
	}

	static Bitmap figSize(Bitmap bmp, int fitSize) {
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		float scaleWidth = 0;
		float scaleHeight = 0;
		/* 放大变量 */
		float scale = 0;

		float bigSize = Math.max(bmpWidth, bmpHeight);
		
		scale = fitSize / bigSize;

		/* 放大以后的宽高，一定要强制转换为float型的 */
		scaleWidth = (float) (bmpWidth * scale);
		scaleHeight = (float) (bmpHeight * scale);

		/* 产生resize后的Bitmap对象 */
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);

		Log.d("MusicInfo ", "after makeBig  scaleWidth " + scaleWidth + " scaleHeight " + scaleHeight);
		Log.d("MusicInfo ", "after makeBig bmpHeight " + resizeBmp.getHeight() + " w " + resizeBmp.getWidth());

		return resizeBmp;
	}

}
