package com.yikang.ykmusix;

import com.yikang.ykmusix.been.MusicStaticPool;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity {

	@Override
	protected void onResume() {
		super.onResume();
		if (MusicStaticPool.isExitApp()) {
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_about);
	}

}
