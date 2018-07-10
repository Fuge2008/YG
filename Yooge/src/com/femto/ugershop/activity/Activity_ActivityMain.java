package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.PhotoList;
import com.femto.ugershop.view.ScrollViewWithListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月19日 上午11:20:59 类说明
 */
public class Activity_ActivityMain extends SwipeBackActivity implements OnItemClickListener {
	private RelativeLayout rl_back_activity;
	private ScrollViewWithListView lv_activitypic;
	private TextView tv_activityinfo;
	private String info;
	private List<PhotoList> photoList;
	private ArrayList<String> pic;
	private MyAdapter adapter;
	private DisplayImageOptions options;
	private int w;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_activity:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_activity = (RelativeLayout) findViewById(R.id.rl_back_activity);
		lv_activitypic = (ScrollViewWithListView) findViewById(R.id.lv_activitypic);
		tv_activityinfo = (TextView) findViewById(R.id.tv_activityinfo);
		adapter = new MyAdapter();
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_activity.setOnClickListener(this);
		if (info != null) {
			tv_activityinfo.setText("" + info);
		}

		lv_activitypic.setAdapter(adapter);
		lv_activitypic.setOnItemClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_activitymain);
		MyApplication.addActivity(this);
		initParams();
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(this, 4);
		w = (screenWidth - dp2px);
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void initParams() {
		// TODO Auto-generated method stub
		info = getIntent().getStringExtra("info");
		photoList = (List<PhotoList>) getIntent().getSerializableExtra("photolist");
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(false) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				.imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.ARGB_8888)
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		pic = new ArrayList<String>();
		for (int i = 0; i < photoList.size(); i++) {
			pic.add(photoList.get(i).getPhotoUrl());
		}
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return photoList == null ? 0 : photoList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return photoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(Activity_ActivityMain.this, R.layout.vp_item, null);
				h.im_vp = (ImageView) v.findViewById(R.id.im_vp);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}

			LayoutParams params = h.im_vp.getLayoutParams();
			params.width = w;
			if (photoList.get(position).getWidth() != null && !photoList.get(position).getWidth().equals("")
					&& photoList.get(position).getHigh() != null && !photoList.get(position).getHigh().equals("")) {
				params.height = (int) (w * Integer.parseInt(photoList.get(position).getHigh()) / (double) Integer
						.parseInt(photoList.get(position).getWidth()));
			}

			h.im_vp.setLayoutParams(params);
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + photoList.get(position).getPhotoUrl(), h.im_vp,
					options);
			return v;
		}
	}

	class MyHolder {
		ImageView im_vp;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if (pic != null && pic.size() != 0) {
			Intent intent = new Intent(Activity_ActivityMain.this, Activity_LookPic.class);
			intent.putExtra("position", position);
			intent.putStringArrayListExtra("pics", pic);
			startActivity(intent);
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("消费者主页");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("消费者主页");
	}
}
