package com.yikang.ykmusix;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yikang.ykmusix.been.MusicStaticPool;
import com.yikang.ykmusix.model.QZoneShare;

/**
 * 侧滑的Fragment
 * 
 * @author Administrator
 * 
 */
public class FragmentSlidingMenu extends Fragment {

	Context mContext;

	public FragmentSlidingMenu() {
		mContext = getActivity();
	}

	static Timer timer = new Timer();
	TimerTask task = new TimerTask() {

		//
		@Override
		public void run() {

			getActivity().getApplicationContext().stopService(
					MainMusixActivity.service);
			getActivity().finish();
			MusicStaticPool.setExitApp(true);
		}
	};

	RelativeLayout rl_exit;
	RelativeLayout rl_setting_qzone_share;
	RelativeLayout rl_setting_time_off;
	RelativeLayout rl_setting_about;
	RelativeLayout rl_setting, rl_setting_updata;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.f_sliding_menu, null);
		/*-----------------*/
		rl_setting_qzone_share = (RelativeLayout) view
				.findViewById(R.id.rl_setting_qzone_share);
		rl_setting_qzone_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QZoneShare qZoneShare = new QZoneShare(getActivity(),
						getActivity());
				qZoneShare
						.shareToQZone(
								"Hello QZone ! 我来了！",
								"hello,大家好，我是musix player，来混个脸熟  ヾ(≧O≦)〃嗷~ \n 其实俺只是一款安静的音乐播放器~~",
								"http://shouji.baidu.com/software/item?docid=8079648&from=as",
								"https://avatars2.githubusercontent.com/u/9492757?v=3&u=614a5ec53801e4a275802dad8c7359db0509a7dc&s=140");
			}
		});
		/*-----------------*/
		rl_exit = (RelativeLayout) view.findViewById(R.id.rl_setting_exit);

		rl_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MainMusixActivity.service == null) {
					return;
				}
				getActivity().getApplicationContext().stopService(
						MainMusixActivity.service);
				Intent intent = new Intent(getActivity(), MusicPlayerService.class);
				getActivity().stopService(intent) ;
				getActivity().finish();
				MusicStaticPool.setExitApp(true);
//				android.os.Process.killProcess(android.os.Process.myPid());
//				System.exit(0); // 常规java、c#的标准退出法，返回值为0代表正常退出
			}
		});

		rl_setting_updata = (RelativeLayout) view
				.findViewById(R.id.rl_setting_updata);// 更新
		rl_setting_updata.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						BaiduAutoUpdateActivity.class);
				startActivity(intent);
			}
		});
		rl_setting_about = (RelativeLayout) view
				.findViewById(R.id.rl_setting_about);
		rl_setting_about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AboutActivity.class);
				startActivity(intent);

			}
		});
		rl_setting = (RelativeLayout) view.findViewById(R.id.rl_setting);

		rl_setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "更多功能尚未实现，将在下一个版本中实现 〒▽〒",
						Toast.LENGTH_SHORT).show();
			}
		});

		rl_setting_time_off = (RelativeLayout) view
				.findViewById(R.id.rl_setting_time_off);
		rl_setting_time_off.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showInputTimeDialog();
			}
		});
		return view;
	}

	// 显示对话框
	public void showInputTimeDialog() {
		LayoutInflater factory = LayoutInflater.from(getActivity());

		final View textEntryView = factory.inflate(
				R.layout.dialog_input_siling, null);

		// 控件显示在对话框中
		new AlertDialog.Builder(getActivity())
		// 对话框的标题
				.setTitle("输入停止退出音乐播放时间：").setView(textEntryView)
				// 设定显示的View
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					public void onClick(DialogInterface dialog, int whichButton) {
						final EditText etInput = (EditText) textEntryView
								.findViewById(R.id.et_dialog_s_input);

						String input = etInput.getText().toString().trim();
						if (input == null | input.equals("")) {
							Toast.makeText(getActivity(), "输入时间不能为空",
									Toast.LENGTH_SHORT).show();
							showInputTimeDialog();
						} else {
							try {
								int time = Integer.parseInt(input);
								timer.schedule(task, time * 60 * 1000);
							} catch (Exception exception) {
								Toast.makeText(getActivity(), "请输入纯数字",
										Toast.LENGTH_SHORT).show();
								showInputTimeDialog();
							}

						}

					}
				})
				// 对话框的“退出”单击事件
				.setNegativeButton("否", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				})

				.create().show();
	}

}
