package com.yikang.ykmusix.been;

import java.io.Serializable;
import java.util.List;

import com.yikang.ykmusix.MusicPlayerService;
import com.yikang.ykmusix.model.LRCParser.LrcInfo;

/**
 * 这个类是用来保存当前播放时的信息状态
 * 
 * @author Administrator
 * 
 */
public class MusicStaticPool implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static MusicPlayerService Player; // 播放服务
	private static MusicInfo CurMusicPlaying;// 当前所播放的音乐信息
	private static List<MusicInfo> CurPlayList; // 当前播放列表
	private static List<MusicListingItem> CurListing; // 所有列表
	private static int CurModel = 0; // 当前播放模式 默认值为0 循环模式
	private static int courentListingID = -1; // 当前列表ID
	private static int CurPlayListPS = -1;// 当前列表所在的list中的位置
	private static int CurListingPS = -1;// 当前列表所在的list中的位置
	private static boolean isPlaying = false;

	/**
	 * 当前显示歌词的引索
	 */
	private static int lrcIndexShow = 0;
	/**
	 * 当前播放的歌词信息
	 */
	private static LrcInfo LrInfo = null;
	
	private static boolean isExitApp=false ;
	
	

	public static boolean isExitApp() {
		return isExitApp;
	}

	public static void setExitApp(boolean isExitApp) {
		MusicStaticPool.isExitApp = isExitApp;
	}

	public static int getLrcIndexShow() {
		return lrcIndexShow;
	}

	public static void setLrcIndexShow(int lrcIndexShow) {
		MusicStaticPool.lrcIndexShow = lrcIndexShow;
	}

	public static LrcInfo getLrInfo() {
		return LrInfo;
	}

	public static void setLrInfo(LrcInfo lrInfo) {
	
		LrInfo = lrInfo;
	}

	public static MusicPlayerService getPlayer() {
		return Player;
	}

	public static void setPlayer(MusicPlayerService player) {
		Player = player;
	}

	public static MusicInfo getCurMusicPlaying() {
		return CurMusicPlaying;
	}

	public static void setCurMusicPlaying(MusicInfo curMusicPlaying) {
		CurMusicPlaying = curMusicPlaying;
	}

	public static List<MusicInfo> getCurPlayList() {
		return CurPlayList;
	}

	public static void setCurPlayList(List<MusicInfo> curPlayList) {
		CurPlayList = curPlayList;
	}

	public static List<MusicListingItem> getCurListing() {
		return CurListing;
	}

	public static void setCurListing(List<MusicListingItem> curListing) {
		CurListing = curListing;
	}

	public static int getCurModel() {
		return CurModel;
	}

	public static void setCurModel(int curModel) {
		CurModel = curModel;
	}

	public static int getCourentListingID() {
		return courentListingID;
	}

	public static void setCourentListingID(int courentListingID) {
		MusicStaticPool.courentListingID = courentListingID;
	}

	public static int getCurPlayListPS() {
		return CurPlayListPS;
	}

	public static void setCurPlayListPS(int curPlayListPS) {

		CurMusicPlaying = CurPlayList.get(curPlayListPS);// 自动保存当前音乐播放信息
		CurPlayListPS = curPlayListPS;
	}

	public static int getCurListingPS() {
		return CurListingPS;
	}

	public static void setCurListingPS(int curListingPS) {
		CurListingPS = curListingPS;
	}

	public static boolean isPlaying() {
		return isPlaying;
	}

	public static void setPlaying(boolean isPlaying) {
		MusicStaticPool.isPlaying = isPlaying;
	}

}
