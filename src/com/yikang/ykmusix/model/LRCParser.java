package com.yikang.ykmusix.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 此类用来解析LRC文件 将解析完整的LRC文件放入�?��LrcInfo对象�?并且返回这个LrcInfo对象s author:java_mzd
 */
public class LRCParser {
	/**
	 * 解析后的LRC对象，其中包含标题以及歌词和时间 全局变量
	 */
	
	private long currentTime = 0;// 存放临时时间
	private String currentContent = null;// 存放临时歌词

	private InputStream readLrcFile(String path) throws FileNotFoundException {
		File f = new File(path);
		InputStream ins = new FileInputStream(f);
		return ins;
	}

	public LrcInfo parser(String path) throws Exception {
		InputStream in = readLrcFile(path);
		if(in==null){
			return null ;
		}
		 LrcInfo  lrcinfo = parser(in);
		lrcinfo.setKey(map2list(lrcinfo.getInfos()));
		return lrcinfo;

	}

	/**
	 * 将输入流中的信息解析，返回一个LrcInfo对象
	 * 
	 * @param inputStream
	 *            输入�?
	 * @return 解析好的LrcInfo对象
	 * @throws IOException
	 */
	public LrcInfo parser(InputStream inputStream) throws IOException {

		Map<Long, String> maps=new HashMap<Long, String>();
		 LrcInfo lrcinfo = new LrcInfo();
		 BufferedReader reader =null;
		// 三层包装
		 try{
				InputStreamReader inr = new InputStreamReader(inputStream);
				reader= new BufferedReader(inr);
		 }catch(Exception e){
			
			 e.printStackTrace() ;
			 return null ;
		 }
	
		String str = null;
		while ((str = reader.readLine()) != null) {

			// -------------------------------------------

			// 取得歌曲名信�?
			if (str.startsWith("[ti:")) {
				String title = str.substring(4, str.length() - 1);
				System.out.println("title--->" + title);
				lrcinfo.setTitle(title);

			}// 取得歌手信息
			else if (str.startsWith("[ar:")) {
				String singer = str.substring(4, str.length() - 1);
				System.out.println("singer--->" + singer);
				lrcinfo.setSinger(singer);

			}// 取得专辑信息
			else if (str.startsWith("[al:")) {
				String album = str.substring(4, str.length() - 1);
				System.out.println("album--->" + album);
				lrcinfo.setAlbum(album);

			}// 通过正则取得每句歌词信息
			else {

//				String reg = "\\[(\\d{2}:\\d{2}\\.\\d{2})\\]";
//				String reg = "\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]";
				 String reg = "\\[(\\d{2}:\\d{2}\\.\\d{2})\\]";  
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(str);

				while (matcher.find()) {

					String msg = matcher.group();
					int groupCount = matcher.groupCount();
					for (int i = 0; i <= groupCount; i++) {
						String timeStr = matcher.group(i);
						if (i == 1) {
							currentTime = strToLong(timeStr);
						}
					}

					String[] content = pattern.split(str);
					for (int i = 0; i < content.length; i++) {
						if (i == content.length - 1) {
							// 将内容设置为当前内容
							currentContent = content[i];
						}
					}
					maps.put(currentTime, currentContent);
					System.out.println("put---currentTime--->" + currentTime + "----currentContent---->" + currentContent);

				}
			}
			// -------------------------------------------

		}
		// 全部解析完后，设置info
		lrcinfo.setInfos((HashMap<Long, String>) maps);
		lrcinfo.setKey(map2list(lrcinfo.getInfos()));

		return lrcinfo;
	}

	/**
	 * 利用正则表达式解析每行具体语�?并在解析完该语句后，将解析出来的信息设置在LrcInfo对象�?
	 * 
	 * @param str
	 */
//	private Map<Long, String> parserLine(String str) {
//
//		// 取得歌曲名信�?
//		if (str.startsWith("[ti:")) {
//			String title = str.substring(4, str.length() - 1);
//			System.out.println("title--->" + title);
//			lrcinfo.setTitle(title);
//
//		}// 取得歌手信息
//		else if (str.startsWith("[ar:")) {
//			String singer = str.substring(4, str.length() - 1);
//			System.out.println("singer--->" + singer);
//			lrcinfo.setSinger(singer);
//
//		}// 取得专辑信息
//		else if (str.startsWith("[al:")) {
//			String album = str.substring(4, str.length() - 1);
//			System.out.println("album--->" + album);
//			lrcinfo.setAlbum(album);
//
//		}// 通过正则取得每句歌词信息
//		else {
//			// 设置正则规则
//
//			// Matcher m =
//			// Pattern.compile("\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]").matcher(line);
//			// 三种模式
//			// String reg = "\\[(\\d{1,2}:\\d{1,2}\\.\\d{1,2})\\]";
//			String reg = "\\[(\\d{2}:\\d{2}\\.\\d{2})\\]";
//			// String reg = "\\[(\\d{2}:\\d{2})\\]";
//			// 编译
//			Pattern pattern = Pattern.compile(reg);
//			Matcher matcher = pattern.matcher(str);
//
//			// 如果存在匹配项，则执行以下操�?
//			maps.clear();
//			while (matcher.find()) {
//				// 得到匹配的所有内�?
//				String msg = matcher.group();
//				// 得到这个匹配项开始的索引
//				int start = matcher.start();
//				// 得到这个匹配项结束的索引
//				int end = matcher.end();
//
//				// 得到这个匹配项中的组�?
//				int groupCount = matcher.groupCount();
//				// 得到每个组中内容
//				for (int i = 0; i <= groupCount; i++) {
//					String timeStr = matcher.group(i);
//					if (i == 1) {
//						// 这里还需改进
//						// 将第二组中的内容设置为当前的�?��时间�?
//						currentTime = strToLong(timeStr);
//					}
//				}
//
//				// 得到时间点后的内�?
//				String[] content = pattern.split(str);
//				// 输出数组内容
//				for (int i = 0; i < content.length; i++) {
//					if (i == content.length - 1) {
//						// 将内容设置为当前内容
//						currentContent = content[i];
//					}
//				}
//				// 设置时间点和内容的映�?
//				maps.put(currentTime, currentContent);
//				System.out.println("put---currentTime--->" + currentTime + "----currentContent---->" + currentContent);
//
//			}
//		}
//		return maps;
//	}

	/**
	 * 将解析得到的表示时间的字符转化为Long�?
	 * 
	 * @param group
	 *            字符形式的时间点
	 * @return Long形式的时�?
	 */
	private long strToLong(String timeStr) {
		// 因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单�?
		// 1:使用：分�?2：使�?分割
		String[] s = timeStr.split(":");
		int min = Integer.parseInt(s[0]);
		String[] ss = s[1].split("\\.");
		int sec = Integer.parseInt(ss[0]);
		int mill = Integer.parseInt(ss[1]);
		return min * 60 * 1000 + sec * 1000 + mill * 10;
	}

	private long strToLong2(String timeStr) {
		// 因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单�?
		// 1:使用：分�?2：使�?分割
		String[] s = timeStr.split(":");
		int min = Integer.parseInt(s[0]);
		// String[] ss = s[1].split("\\.");
		int sec = Integer.parseInt(s[1]);
		int mill = 0;
		return min * 60 * 1000 + sec * 1000 + mill * 10;
	}

	/**
	 * 用来封装歌词信息的类
	 * 
	 * @author Administrator
	 * 
	 */
	public class LrcInfo {
		private String title;// 歌曲�?
		private String singer;// 演唱�?
		private String album;// 专辑
		/**
		 * 歌词以及时间map集合
		 */
		private HashMap<Long, String> infos;// 保存歌词信息和时间点�?��对应的Map
		/**
		 * 重新排序后对应map的key值
		 */
		Object[] key;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getSinger() {
			return singer;
		}

		public void setSinger(String singer) {
			this.singer = singer;
		}

		public String getAlbum() {
			return album;
		}

		public void setAlbum(String album) {
			this.album = album;
		}

		public HashMap<Long, String> getInfos() {
			return infos;
		}

		public void setInfos(HashMap<Long, String> infos) {
			this.infos = infos;
		}

		public Object[] getKey() {
			return key;
		}

		public void setKey(Object[] key) {
			this.key = key;
		}
	}

	static Object[] map2list(Map<Long, String> maps2) {
		Object[] key = maps2.keySet().toArray();
		Arrays.sort(key);
		for (int i = 0; i < key.length; i++) {
			System.out.println(maps2.get(key[i]));
			System.out.println(key[i]);
		}
		return key;

	}

}
