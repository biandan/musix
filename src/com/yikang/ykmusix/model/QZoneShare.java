package com.yikang.ykmusix.model;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class QZoneShare {

	Context context;
	public static Tencent mTencent;
	String APP_ID = "1104935042";
	Activity what;

	public QZoneShare(Context context, Activity what) {
		this.context = context;
		this.what = what;
		mTencent = Tencent.createInstance(APP_ID,
				context.getApplicationContext());
		
	}

	/**
	 * 
	 * @param title
	 * @param summary
	 * @param url
	 * @param imgUrl
	 *            目前就先放一张图即可
	 */
	public void shareToQZone(String title, String summary, String url, String imgUrl) {

		final Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
				QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
		ArrayList<String> imageUrls = new ArrayList<String>();
		imageUrls.add(imgUrl);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT,
				QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);

		mTencent.shareToQzone(what, params, new BaseUiListener());
		// new QZoneShareThread().start();

	}

	class BaseUiListener implements IUiListener {

		@Override
		public void onCancel() {
			System.out.println("Tencent onCancel");
		}

		@Override
		public void onComplete(Object arg0) {
			System.out.println("Tencent onComplete");
		}

		@Override
		public void onError(UiError arg0) {
			System.out.println("Tencent onError");
		}

	}

}
