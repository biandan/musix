package com.yikang.ykmusix;

import java.util.ArrayList;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yikang.ykmusix.been.MusicStaticPool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 集成了腾讯的分享功能；
 * 注意：这个APP_ID（APP_ID = "1104935042"）是个人开发的，而非企业APP_ID
 * 官方资料：http://wiki.connect.qq.com/com-tencent-tauth-tencent-login
 * @author Administrator
 *
 */
public class QZoneShareActivity extends Activity {

	Button bt_share;
	Tencent mTencent;
	String APP_ID = "1104935042";

	@Override
	protected void onResume() {
		super.onResume();
		if (MusicStaticPool.isExitApp()) {
			finish();
		}
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.a_qzone_share);
		mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
		bt_share = (Button) findViewById(R.id.bt_share);
		bt_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				final Bundle params = new Bundle();
				params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
						QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
				params.putString(QzoneShare.SHARE_TO_QQ_TITLE,
						"Hello QZone ! 我来了！");
				params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,
						"咱这是第一次测试，还不知道什么情况，先试试能不能用先~~");
				params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,
						"https://github.com/biandan/musix");
				ArrayList imageUrls = new ArrayList();
				imageUrls.add("http://media-cdn.tripadvisor.com/media/photo-s/01/3e/05/40/the-sandbar-that-links.jpg");
				params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
						imageUrls);
				params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT,
						QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);

				mTencent.shareToQzone(QZoneShareActivity.this, params,
						new BaseUiListener());
				// new QZoneShareThread().start();
			}
		});
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Tencent.onActivityResultData(requestCode, resultCode, data,
//				qZoneShareListener);
		mTencent.onActivityResult(requestCode, resultCode, data);
		System.out
				.println("mTencent.onActivityResult(requestCode, resultCode, data)");
	}

	// QZone分享， SHARE_TO_QQ_TYPE_DEFAULT 图文，SHARE_TO_QQ_TYPE_IMAGE 纯图
	private int shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;

	// 设置分享类型：图文并茂加链接。其他类型见帮助文档

	class QZoneShareThread extends Thread {

		@Override
		public void run() {

			final Bundle params = new Bundle();
			params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
					QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "Hello QZone ! 我来了！");
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,
					"咱这是第一次测试，还不知道什么情况，先试试能不能用先~~");
			params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,
					"https://github.com/biandan/musix");
			ArrayList imageUrls = new ArrayList();
			imageUrls
					.add("http://media-cdn.tripadvisor.com/media/photo-s/01/3e/05/40/the-sandbar-that-links.jpg");
			params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
					imageUrls);
			params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT,
					QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);

			mTencent.shareToQzone(QZoneShareActivity.this, params,
					new BaseUiListener());

			super.run();
		}
	}

	private class BaseUiListener implements IUiListener {

		
		
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
