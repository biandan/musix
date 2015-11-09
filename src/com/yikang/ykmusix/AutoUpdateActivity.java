package com.yikang.ykmusix;

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.CPUpdateDownloadCallback;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


public class AutoUpdateActivity extends Activity implements View.OnClickListener {
	
	private TextView txt_log;
	
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.autoupdate_main);
//		findViewById(R.id.btn_ui).setOnClickListener(this);
//		findViewById(R.id.btn_silence).setOnClickListener(this);
//		findViewById(R.id.btn_as).setOnClickListener(this);
		findViewById(R.id.btn_noui).setOnClickListener(this);
		txt_log = (TextView) findViewById(R.id.txt_log);
		dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.btn_ui:
//			txt_log.setText("");
//			dialog.show();
//			BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());
//			break;
//		case R.id.btn_silence:
//			txt_log.setText("");
//			BDAutoUpdateSDK.silenceUpdateAction(this);
//			break;
//		case R.id.btn_as:
//			txt_log.setText("");
//			dialog.show();
//			BDAutoUpdateSDK.asUpdateAction(this, new MyUICheckUpdateCallback());
//			break;
		case R.id.btn_noui:
			txt_log.setText("");
			dialog.show();
			BDAutoUpdateSDK.cpUpdateCheck(this, new MyCPCheckUpdateCallback());
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		dialog.dismiss();
		super.onDestroy();
	}
	
	
	private class MyUICheckUpdateCallback implements UICheckUpdateCallback {

		@Override
		public void onCheckComplete() {
			dialog.dismiss();
		}

	}
	
	
	private class MyCPCheckUpdateCallback implements CPCheckUpdateCallback {

		@Override
		public void onCheckUpdateCallback(AppUpdateInfo info, AppUpdateInfoForInstall infoForInstall) {
			if(infoForInstall != null && !TextUtils.isEmpty(infoForInstall.getInstallPath())) {
				txt_log.setText(txt_log.getText() + "\n install info: " + infoForInstall.getAppSName() + ", \nverion=" + infoForInstall.getAppVersionName() + ", \nchange log=" + infoForInstall.getAppChangeLog());
				txt_log.setText(txt_log.getText() + "\n we can install the apk file in: " + infoForInstall.getInstallPath());
				BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), infoForInstall.getInstallPath());
			}else if(info != null) {
				BDAutoUpdateSDK.cpUpdateDownload(AutoUpdateActivity.this, info, new UpdateDownloadCallback());
			}else {
				txt_log.setText(txt_log.getText() + "\n 暂时没有更新，谢谢关注.");
			}
			dialog.dismiss();
		}

	}
	
	
	private class UpdateDownloadCallback implements CPUpdateDownloadCallback {

		@Override
		public void onDownloadComplete(String apkPath) {
			txt_log.setText(txt_log.getText() + "\n onDownloadComplete: " + apkPath);
			BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), apkPath);
		}

		@Override
		public void onStart() {
			txt_log.setText(txt_log.getText() + "\n Download onStart");
		}

		@Override
		public void onPercent(int percent, long rcvLen, long fileSize) {
			txt_log.setText(txt_log.getText() + "\n Download onPercent: " + percent + "%");
		}

		@Override
		public void onFail(Throwable error, String content) {
			txt_log.setText(txt_log.getText() + "\n Download onFail: " + content);
		}

		@Override
		public void onStop() {
			txt_log.setText(txt_log.getText() + "\n Download onStop");
		}
		
	}
	
}
