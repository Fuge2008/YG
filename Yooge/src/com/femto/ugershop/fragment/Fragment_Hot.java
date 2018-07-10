package com.femto.ugershop.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.femto.ugershop.R;
import com.femto.ugershop.view.MyGridView;
import com.umeng.analytics.MobclickAgent;

public class Fragment_Hot extends BaseFragment implements OnPageChangeListener {
	private static Fragment_Hot instance;
	private View view;
	private ViewPager vp_hot;
	// private MyGridView gv_hot;
	private MyGVAdapter adapter;
	private List<View> vpData;
	View cview;
	private MyVPAdapter vpadapter;
	private ScrollView sv_hot;
	private LinearLayout dots_group;

	// public Fragment_Hot() {
	// instance = this;
	// }
	//
	// public static Fragment_Hot getInstance() {
	// return instance;
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_hot, container, false);
		vpData = new ArrayList<View>();
		initView(view);
		return view;
	}

	private void initView(View v) {
		initVp();
		vp_hot = (ViewPager) v.findViewById(R.id.vp_hot);
		// gv_hot = (MyGridView) v.findViewById(R.id.gv_hot);
		// sv_hot = (ScrollView) v.findViewById(R.id.sv_hot);
		dots_group = (LinearLayout) v.findViewById(R.id.dots_group);
		vpadapter = new MyVPAdapter();
		vp_hot.setAdapter(vpadapter);
		sv_hot.smoothScrollTo(0, 0);
		adapter = new MyGVAdapter();
		// gv_hot.setAdapter(adapter);
		vp_hot.setOnPageChangeListener(this);
		initSmallDot(0);

	}

	@Override
	public void onDetach() {
		super.onDetach();
		sv_hot.smoothScrollTo(0, 0);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			sv_hot.smoothScrollTo(0, 0);
		}
		if (!hidden) {
			MobclickAgent.onResume(getActivity());
			setPageStart("热门");
		} else {
			MobclickAgent.onPause(getActivity());
			setPageEnd("热门");
		}
	}

	// @Override
	// public void onResume() {
	// super.onResume();
	// scrollTo00();
	// }

	// public void scrollTo00() {
	// new Thread() {
	// public void run() {
	// try {
	// Thread.sleep(10);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// handler.sendEmptyMessage(0x001);
	// };
	// }.start();
	// }

	// private Handler handler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// sv_hot.fullScroll(ScrollView.FOCUS_UP);
	// };
	// };

	private void initSmallDot(int index) {
		dots_group.removeAllViews();
		for (int i = 0; i < 3; i++) {
			ImageView imageView = new ImageView(getActivity());
			imageView.setImageResource(R.drawable.round);
			imageView.setPadding(5, 0, 5, 0);
			dots_group.addView(imageView);
		}

		// 设置选中项
		((ImageView) dots_group.getChildAt(index)).setImageResource(R.drawable.round_select);
	}

	private void initVp() {

		for (int i = 0; i < 3; i++) {
			cview = View.inflate(getActivity(), R.layout.vp_item, null);
			ImageView im_vp = (ImageView) cview.findViewById(R.id.im_vp);
			im_vp.setImageResource(R.drawable.picture1);
			vpData.add(cview);
		}

	}

	class MyGVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			v = View.inflate(getActivity(), R.layout.item_gv_hot, null);
			return v;
		}

	}

	class MyVPAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return vpData.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(vpData.get(position));
			return vpData.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(vpData.get(position));
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		initSmallDot(arg0);

	}
}
