package com.yikang.ykmusix.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import com.yikang.ykmusix.R;
import com.yikang.ykmusix.been.LRCTextView;
import com.yikang.ykmusix.model.LRCParser;
import com.yikang.ykmusix.model.LRCParser.LrcInfo;
import com.yikang.ykmusix.model.LRCSearch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ShowLRCTextViewActivity extends Activity {

	LRCTextView textView;
	Context context;
	private final static int MSG_UPDATA_LRC_TEXT = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_show_lrc_test);
		textView = (LRCTextView) findViewById(R.id.tv_lrc_test);
		context = this;
//		new ThreadUI().start();
	}

	LrcInfo info;
	int index = 0;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_UPDATA_LRC_TEXT) {
				textView.updata(index, info.getInfos(), info.getKey());
				textView.invalidate();
			}

		}

	};

	class ThreadUI extends Thread {
		LRCParser parser = new LRCParser();
		List<String> lrcURLs;
		InputStream is;

		@Override
		public void run() {
			super.run();

			LRCSearch lrcSearch = new LRCSearch();
			try {
//				lrcURLs = lrcSearch.getMusicLrcURL("奔跑", "Beyond");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				is = lrcSearch.getOnlineLrcInputStream(lrcURLs.get(0));
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				info = parser.parser(is);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (index < info.getKey().length) {
				index++;
				System.out.println(" info.getKey() " + index);
				handler.sendEmptyMessage(0);
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
