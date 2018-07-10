package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class Activity_Custom_Rule extends SwipeBackActivity {
	private RelativeLayout rl_back_cr;
	private int with;
	private DisplayImageOptions options;
	private MyLvadapter adapter;
	private List<Integer> data;
	private ListView lv_rule;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_cr:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_cr = (RelativeLayout) findViewById(R.id.rl_back_cr);
		lv_rule = (ListView) findViewById(R.id.lv_rule);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_cr.setOnClickListener(this);
		data = new ArrayList<Integer>();
		data.add(R.drawable.rule1);
		data.add(R.drawable.rule2);
		data.add(R.drawable.rule3);
		data.add(R.drawable.rule4);
		// im_rule.setImageResource(R.drawable.pic_rule);
		with = getWith();
		// LayoutParams params = im_rule.getLayoutParams();
		// params.height = (int) (with * 7.0);
		// im_rule.setLayoutParams(params);
		// ImageLoader.getInstance().displayImage("", im_rule, options);
		adapter = new MyLvadapter();
		lv_rule.setAdapter(adapter);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_customrule);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.newpic_rule) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.newpic_rule) // image连接地址为空时
				.showImageOnFail(R.drawable.newpic_rule) // image加载失败
				.cacheInMemory(false) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(false) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		MyApplication.addActivity(this);
	}

	class MyLvadapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data == null ? 0 : data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_Custom_Rule.this, R.layout.item_image_newother, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_other);
			LayoutParams params = im.getLayoutParams();
			params.height = (int) (with * 1.78);
			im.setLayoutParams(params);
			im.setImageResource(data.get(position));
			return v;
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("定制规则");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("定制规则");
	}
}
