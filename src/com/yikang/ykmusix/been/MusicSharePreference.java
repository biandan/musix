package com.yikang.ykmusix.been;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import org.apache.commons.codec.binary.Base64;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

public class MusicSharePreference {
	MusicSharePreference mPreference;
	Context context;
	String databasename = "ykmusix";
	String fileName="musiclist" ;
	SharedPreferences mPreferences;

	public MusicSharePreference(Context context) {
		mPreferences = context.getSharedPreferences(databasename, Context.MODE_PRIVATE);
	}

	void saveOneMusicList(MusicListingItem item) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 将Product对象放到OutputStream中
		try {
			oos.writeObject(item);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String productBase64 = new String(Base64.encodeBase64(baos.toByteArray()));
		SharedPreferences.Editor editor = mPreferences.edit();
		// 将编码后的字符串写到base64.xml文件中

		Long musicID = System.currentTimeMillis();
		editor.putString(musicID+"", productBase64);
		editor.commit();
	}

	void getMusicListItem() {
		String productBase64 = mPreferences.getString("product", "");
		// 对Base64格式的字符串进行解码
		byte[] base64Bytes = Base64.decodeBase64(productBase64.getBytes());
		ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 从ObjectInputStream中读取Product对象
		try {
			MusicListingItem product = (MusicListingItem) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void saveMusicLists(Long musicID) {
		SharedPreferences.Editor editor = mPreferences.edit();
		//editor.
	}

}
