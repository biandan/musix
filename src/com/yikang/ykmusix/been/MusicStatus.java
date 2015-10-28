package com.yikang.ykmusix.been;

import java.io.Serializable;
import java.util.List;

import com.yikang.ykmusix.MusicPlayerService;

public class MusicStatus implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private  MusicPlayerService Player;
	private  MusicInfo CurMusicPlaying;
	private  List<MusicInfo> CurPlayList;
	private  List<MusicListingItem> CurMusicListing;
	private  int CurModel;
	private int courentListingID ;
	private int currentPlayingInfoListId ;
	private boolean isPlaying ;
	
	
	public int getCurrentPlayingInfoListId() {
		return currentPlayingInfoListId;
	}
	public void setCurrentPlayingInfoListId(int currentPlayingInfoListId) {
		this.currentPlayingInfoListId = currentPlayingInfoListId;
	}
	public boolean isPlaying() {
		return isPlaying;
	}
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
	public  MusicPlayerService getPlayer() {
		return Player;
	}
	public  void setPlayer(MusicPlayerService player) {
		Player = player;
	}
	public  MusicInfo getCurMusicPlaying() {
		return CurMusicPlaying;
	}
	public  void setCurMusicPlayingInfo(MusicInfo curMusicPlaying) {
		CurMusicPlaying = curMusicPlaying;
	}
	public  List<MusicInfo> getCurPlayList() {
		return CurPlayList;
	}
	public  void setCurPlayList(List<MusicInfo> curPlayList) {
		CurPlayList = curPlayList;
	}
	public  List<MusicListingItem> getCurMusicListing() {
		return CurMusicListing;
	}
	public  void setCurMusicListing(List<MusicListingItem> curMusicListing) {
		CurMusicListing = curMusicListing;
	}
	public  int getCurModel() {
		return CurModel;
	}
	public  void setCurModel(int curModel) {
		CurModel = curModel;
	}
	public int getCourentListingID() {
		return courentListingID;
	}
	public void setCourentListingID(int courentListingID) {
		this.courentListingID = courentListingID;
	}
	
}
