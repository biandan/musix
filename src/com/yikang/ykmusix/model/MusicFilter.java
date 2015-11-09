package com.yikang.ykmusix.model;

import com.yikang.ykmusix.been.MusicInfo;

/**
 * 用来过滤掉一些非音乐文件，及一些短的音频文件
 * 
 * @author ZSP
 * 
 */
public class MusicFilter {
	final static int LEN = 60*1000;

	/**
	 * 只返回时长超过60秒的MP3文件
	 * 
	 * @param info
	 * @return
	 */
	public static boolean filterMusicFile(MusicInfo info) {
		String str;
		System.out.println("(int)info.getDuration():"+(int)info.getDuration() );
		if ((int)info.getDuration() > LEN) {
			str = getURLfileName(info.getUrl());
			if (str.contains(".mp3")) {
				System.out.println("filterMusicFile return true");
				return true;
			} else {
				System.out.println("filterMusicFile return  no mp3");
				return false;
			}
		} else {
			System.out.println("filterMusicFile return info.getDuration() < LEN");
			return false;
		}

	}

	/**
	 * 
	 * @param url
	 * @return 返回文件名
	 */
	private static String getURLfileName(String url) {
		System.out.println("getURLfileName url " + url);
		String[] fileName = null;

		if (url != null) {
			fileName = url.split("/");
			// 得到最终需要的文件名
			System.out.println(fileName[fileName.length - 1]);
			return fileName[fileName.length - 1];
		}else{
			return null;
		}

		

	}

}
