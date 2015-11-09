package com.yikang.ykmusix.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LRCSearch {

	
	
	class LoadLRCThread implements Runnable{

		String title;
		String singer;
		LoadLRCThread(String title, String singer){
			
		}
		@Override
		public void run() {
			loadLRC( title,  singer);
		}
		
	}
	

	
	
	
	
	
	/**
	 * 下载歌词 对外公开
	 * 
	 * @param title
	 * @param singer
	 * @return
	 */
	public LRCParser.LrcInfo loadLRC(String title, String singer) {

		LRCParser parser = new LRCParser();
		List<String> lrcURLs = new ArrayList<String>();
		InputStream is = null;
		try {
			lrcURLs = getOnlineLrcURL(title, singer);
			if (lrcURLs == null || lrcURLs.size() == 0) {
				return null;
			}

		} catch (MalformedURLException e) {
			Log.i("MusicPlayerService loadLRC", "歌词搜索失败！");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Log.i("MusicPlayerService loadLRC", "歌词搜索异常！");
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		try {
			is = getOnlineLrcInputStream(lrcURLs.get(0));
		} catch (MalformedURLException e) {
			Log.i("MusicPlayerService loadLRC", "下载歌词失败！");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Log.i("MusicPlayerService loadLRC", "下载歌词异常！");
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		try {
			return parser.parser(is);
		} catch (IOException e) {
			Log.i("MusicPlayerService loadLRC", "歌词解析失败！");
			e.printStackTrace();
			return null;
		}

	}

	

	/**
	 * 网上得到歌词
	 * 
	 * @param title
	 *            歌曲名
	 * @param singer
	 *            歌手
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private List<String> getOnlineLrcURL(String title, String singer) throws MalformedURLException, IOException ,Exception{
		String musicAddress = "http://geci.me/api/lyric/" + title + "/" + singer;
		System.out.println("getOnlineLrcURL==>>musicAddress:" + musicAddress);
		// String musicAddress = "http://geci.me/api/lyric/" + title + "/" +
		// singer;
		HttpGet httpGet = new HttpGet(musicAddress);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResp = httpClient.execute(httpGet);
		OnlineLrc lrc = null;
		List<String> address = new ArrayList<String>();
		if (httpResp.getStatusLine().getStatusCode() == 200) {
			String result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
			Log.i("HttpResponse", "HttpGet方式请求成功，返回数据如下：");
			Log.i("HttpResponse", result);
			new OnlineLrc();
			Gson gson = new Gson();
			lrc = gson.fromJson(result, new TypeToken<OnlineLrc>() {
			}.getType());

			List<LrcUrl> lrcUrl = lrc.getResult();

			if (lrcUrl == null || lrcUrl.size() == 0) {
				return getOnlineLrcURL(title);

			}
			for (int i = 0; i < lrcUrl.size(); i++) {

				address.add(lrcUrl.get(i).getLrc());
				System.out.println("getMusicLrcURL====>>>>" + lrcUrl.get(i).getLrc());
			}
			return address;

		} else {
			Log.i("LRCSearch", " getOnlineLrcURL：(title, singer) 方式请求失败 ！启动 (title) 方式···");

			return getOnlineLrcURL(title);

		}

	}

	private List<String> getOnlineLrcURL(String title) throws MalformedURLException, IOException {
		String musicAddress = "http://geci.me/api/lyric/" + title;
		System.out.println("getOnlineLrcURL==>>musicAddress:" + musicAddress);
		HttpGet httpGet = new HttpGet(musicAddress);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResp = httpClient.execute(httpGet);
		OnlineLrc lrc = null;
		if (httpResp.getStatusLine().getStatusCode() == 200) {
			String result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
			Log.i("HttpResponse", "HttpGet方式请求成功，返回数据如下：");
			Log.i("HttpResponse", result);
			new OnlineLrc();
			Gson gson = new Gson();
			lrc = gson.fromJson(result, new TypeToken<OnlineLrc>() {
			}.getType());

		} else {
			Log.i("LRCSearch", " getOnlineLrcURL：(title) 方式请求失败！搜索歌词结束！ ");
			return null;
		}

		List<LrcUrl> lrcUrl = lrc.getResult();
		if (lrcUrl == null || lrcUrl.size() == 0) {
			return null;
		}
		List<String> address = new ArrayList<String>();
		for (int i = 0; i < lrcUrl.size(); i++) {

			address.add(lrcUrl.get(i).getLrc());
			System.out.println("getMusicLrcURL====>>>>" + lrcUrl.get(i).getLrc());
		}
		return address;

	}

	/**
	 * 根据搜索得到的歌曲LRC的URL地址下载歌词
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public InputStream getOnlineLrcInputStream(String url) throws MalformedURLException, IOException {
		String musicAddress = url;
		URLConnection connection = new URL(musicAddress).openConnection();
		return connection.getInputStream();
	}

	/**
	 * 一首音乐对应的单个歌词信息
	 * 
	 * @author 李跃东
	 * @mail androiddevelop@qq.com
	 * @date 2013-11-8
	 */
	public class LrcUrl {

		String lrc;
		String song;
		String artist;
		long sid;
		long aid;

		public String getLrc() {
			return lrc;
		}

		public void setLrc(String lrc) {
			this.lrc = lrc;
		}

		public String getSong() {
			return song;
		}

		public void setSong(String song) {
			this.song = song;
		}

		public String getArtist() {
			return artist;
		}

		public void setArtist(String artist) {
			this.artist = artist;
		}

		public long getSid() {
			return sid;
		}

		public void setSid(long sid) {
			this.sid = sid;
		}

		public long getAid() {
			return aid;
		}

		public void setAid(long aid) {
			this.aid = aid;
		}

	}

	/**
	 * 歌词
	 * 
	 * @author 李跃东
	 * @mail androiddevelop@qq.com
	 * @date 2013-11-8
	 */

	public class OnlineLrc {

		public int count;
		public int code;
		public List<LrcUrl> result;

		public void setCount(int count) {
			this.count = count;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public void setResult(List<LrcUrl> result) {
			this.result = result;
		}

		public int getCount() {
			return count;
		}

		public int getCode() {
			return code;
		}

		public List<LrcUrl> getResult() {
			return result;
		}
	}

	/**
	 * 歌曲信息(名字，绝对路径，播放时长,总帧数)
	 * 
	 * @author 李跃东
	 * @mail androiddevelop@qq.com
	 * @date 2013-11-8
	 */
	public class Music {
		private String musicName; // 音乐名字
		private String musicAuthor; // 歌手
		private String musicAbsolutePath; // 音乐绝对路径
		private long musicTime; // 音乐总时长 (ms)
		private int musicFrameNumber; // 音乐总帧数

		public String getMusicName() {
			return musicName;
		}

		public void setMusicName(String musicName) {
			this.musicName = musicName;
		}

		public String getMusicAuthor() {
			return musicAuthor;
		}

		public void setMusicAuthor(String musicAuthor) {
			this.musicAuthor = musicAuthor;
		}

		public String getMusicAbsolutePath() {
			return musicAbsolutePath;
		}

		public void setMusicAbsolutePath(String musicAbsolutePath) {
			this.musicAbsolutePath = musicAbsolutePath;
		}

		public long getMusicTime() {
			return musicTime;
		}

		public void setMusicTime(long musicTime) {
			this.musicTime = musicTime;
		}

		public int getMusicFrameNumber() {
			return musicFrameNumber;
		}

		public void setMusicFrameNumber(int musicFrameNumber) {
			this.musicFrameNumber = musicFrameNumber;
		}

	}

}
