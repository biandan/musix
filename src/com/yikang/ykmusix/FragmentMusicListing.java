package com.yikang.ykmusix;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;


import com.yikang.ykmusix.been.MusicListingItem;
import com.yikang.ykmusix.been.MusicStaticPool;
import com.yikang.ykmusix.model.MusicListing;


/**
 * 音乐列表 在MusicListing 中使用
 * @author Administrator
 *
 */
public class FragmentMusicListing extends Fragment implements OnClickListener {

	private ListView listView;
	// private Button bt_ok;
	// private int CurListingItemPS;
	private Button bt_add;
	private MusicListingAdapter mListingAdapter;
	private boolean isShowDelBT = false;
	private int SelectListingPS = -1; // 记录当前选中的状态
	private List<MusicListingItem> SelectListings;

	private int SelectListingsPS;
	MusicListing mListingMode;
	private Context mContext;
	MusicSelcetCallBack mCallBack;

	public interface MusicSelcetCallBack {
		public void openPlayListing(MusicListingItem mItem,int MusicItemPS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.f_music_listing, null);
		// bt_ok = (Button) view.findViewById(R.id.bt_music_listing_ok);
		bt_add = (Button) view.findViewById(R.id.bt_music_listing_add);
		bt_add.setOnClickListener(this);
		// bt_ok.setOnClickListener(this);
		listView = (ListView) view.findViewById(R.id.lv_music_listing);
		mContext = getActivity();
		mListingMode = new MusicListing(mContext);

		SelectListings = mListingMode.reSetLoadMusicListings();// 重新加载
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (isShowDelBT) {
					// Toast.makeText(mContext, "别闹，" + position + "~",
					// Toast.LENGTH_SHORT).show();
				} else {
					SelectListingPS = position;
					// CurSelectMusicListingID =
					// MusicStaticPool.getCurListing().get(position).getId();
					mCallBack.openPlayListing(SelectListings.get(position),position);
				}
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				isShowDelBT = true;
				mListingAdapter.notifyDataSetChanged();

				return false;
			}
		});

		// mMusicListings = reSetLoadMusicListings();
		mListingAdapter = new MusicListingAdapter(mContext);
		listView.setAdapter(mListingAdapter);
		return view;

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallBack = (MusicSelcetCallBack) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " 还没实现MusicSelcetCallBack接口");
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isShowDelBT = false;
		getActivity().setTitle("音乐列表：");

		mListingAdapter.notifyDataSetChanged();

	}

	@Override
	public void onClick(View v) {

		// if (v == bt_ok) {
		// isShowDelBT = false;
		// mListingAdapter.notifyDataSetChanged();
		// bt_ok.setVisibility(View.INVISIBLE);
		//
		// // bt_add.setVisibility(View.INVISIBLE);
		// }

		if (v == bt_add) {
			// isShowDelBT = true;
			// mListingAdapter.notifyDataSetChanged();
			showInputTitleDialog();
		}
	}

	void showDelListingDialog(String title, int listingId) {

		final int id = listingId;
		final String t = title;
		new AlertDialog.Builder(mContext)
		// 对话框的标题
				.setTitle("删除列表:" + title + "？")
				// 设定显示的View
				.setPositiveButton("删除", new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					public void onClick(DialogInterface dialog, int whichButton) {
						mListingMode.deleteAnListing(id);
						mListingMode.deleteMusicInfos(id);
						Toast.makeText(mContext, "已删除 " + t + "列表", Toast.LENGTH_SHORT).show();

						isShowDelBT = false;
						SelectListings = mListingMode.reSetLoadMusicListings();
						MusicStaticPool.setCurListing(mListingMode.reSetLoadMusicListings());
						mListingAdapter.notifyDataSetChanged();
					}
				})
				// 对话框的“退出”单击事件
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						isShowDelBT = false;
						mListingAdapter.notifyDataSetChanged();
					}
				})

				.create().show();

	}

	// 显示对话框
	public void showInputTitleDialog() {

		LayoutInflater factory = LayoutInflater.from(mContext);
		// 把activity_login中的控件定义在View中
		final View textEntryView = factory.inflate(R.layout.dialog_input_listing_title, null);

		// 控件显示在对话框中
		new AlertDialog.Builder(mContext)
		// 对话框的标题
				.setTitle("新建列表")
				// 设定显示的View
				.setView(textEntryView).setPositiveButton("完成", new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					public void onClick(DialogInterface dialog, int whichButton) {
						final EditText etInput = (EditText) textEntryView.findViewById(R.id.et_dialog_input_title);
						String title = etInput.getText().toString().trim();

						if (title == null | title.equals("")) {
							Toast.makeText(mContext, "新建标题不能为空", Toast.LENGTH_SHORT).show();
							showInputTitleDialog();
						} else {
							mListingMode.saveAnListing(title);
							MusicStaticPool.setCurListing(SelectListings);
							SelectListings = mListingMode.reSetLoadMusicListings();
							mListingAdapter.notifyDataSetChanged();
						}

					}
				})
				// 对话框的“退出”单击事件
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				})

				.create().show();
	}

	class MusicListingAdapter extends BaseAdapter {
		Context context;
		LayoutInflater inflater;

		public MusicListingAdapter() {

		}

		public MusicListingAdapter(Context context) {
			this.context = context;

			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return SelectListings.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return SelectListings.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.item_music_listing, null);
				holder.mET_title = (TextView) convertView.findViewById(R.id.et_listing_title);
				holder.mBT_del = (Button) convertView.findViewById(R.id.bt_listing_del);
				holder.mTV_count = (TextView) convertView.findViewById(R.id.tv_listing_count);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (isShowDelBT) {
				holder.mBT_del.setVisibility(View.VISIBLE);
				holder.mET_title.setFocusable(true);
				// holder.mET_title.requestFocus() ;
				// holder.mET_title.setOnClickListener(new
				// OnDelItemClick(position));

				holder.mET_title.setFocusableInTouchMode(true);

				holder.mBT_del.setOnClickListener(new OnDelItemClick(position));
			} else {
				holder.mBT_del.setVisibility(View.INVISIBLE);
				holder.mET_title.setFocusable(false);
				holder.mET_title.setFocusableInTouchMode(false);
				// holder.mET_title.clearFocus();

			}

			holder.mET_title.setText(SelectListings.get(position).getTitle());

			if (MusicStaticPool.getCurListingPS() == position) {
				holder.mET_title.setTextColor(Color.RED);
			} else {
				holder.mET_title.setTextColor(Color.BLACK);
			}
			holder.mTV_count.setText(SelectListings.get(position).getCount() + "");

			for (MusicListingItem item : SelectListings) {
				System.out.println("MusicListingItem getCount" + item.getCount() + " getId" + item.getId());
			}
			return convertView;
		}

	}

	class OnDelItemClick implements OnClickListener {
		int ps;

		public OnDelItemClick(int p) {
			ps = p;
		}

		@Override
		public void onClick(View v) {
			showDelListingDialog(SelectListings.get(ps).getTitle(), SelectListings.get(ps).getId());
		}

	}

	class ViewHolder {
		TextView mET_title;
		TextView mTV_count;
		Button mBT_del;
	}

}
