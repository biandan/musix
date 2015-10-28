package com.yikang.ykmusix.been;

import java.io.Serializable;

public class MusicListingItem implements Serializable {


	private static final long serialVersionUID = 1L;
	int id ;
	String title ;
	int count ;
	int musicListID ;
	
	
	public int getMusicListID() {
		return musicListID;
	}
	public void setMusicListID(int musicListID) {
		this.musicListID = musicListID;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
