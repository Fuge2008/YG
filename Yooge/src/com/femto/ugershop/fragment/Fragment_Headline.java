package com.femto.ugershop.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.tabpage.TabPageIndicator;
import com.femto.ugershop.view.HorizontalListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Fragment_Headline extends BaseFragment {
	private View view;
	private List<String> data;
	// private MyAdapter adapter;
	private FragmentTransaction transaction;
	private List<Fragment> fs;
	private MyVpFragmentAdapter vpadapter;
	private ViewPager vp_head;
	private TabPageIndicator tp;
	private List<MYSort> ss;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_headline, container, false);
		initView(view);
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			MobclickAgent.onResume(getActivity());
			setPageStart("头条");
		} else {
			MobclickAgent.onPause(getActivity());
			setPageEnd("头条");
		}

	}

	private void initView(View v) {

		data = new ArrayList<String>();
		ss = new ArrayList<MYSort>();
		fs = new ArrayList<Fragment>();
		vp_head = (ViewPager) v.findViewById(R.id.vp_head);
		tp = (TabPageIndicator) v.findViewById(R.id.tp);
		getSort();
	}

	private void getSort() {
		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetSortList, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				JSONArray optJSONArray = response.optJSONArray("list");
				for (int i = 0; i < optJSONArray.length(); i++) {
					JSONObject j = optJSONArray.optJSONObject(i);
					String sortName = j.optString("sortName");
					int sortId = j.optInt("sortId");
					ss.add(new MYSort(sortName, sortId));
				}
				for (int i = 0; i < ss.size(); i++) {
					fs.add(new Fragment_Head(ss.get(i).sortName, ss.get(i).sortId));
				}
				vpadapter = new MyVpFragmentAdapter(getFragmentManager(), fs);
				vp_head.setAdapter(vpadapter);
				tp.setViewPager(vp_head);
			}
		});
	}

	class MyVpFragmentAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;

		public MyVpFragmentAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		public MyVpFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {

			return ss.get(position).sortName;
		}

	}

	class MYSort {
		String sortName;
		int sortId;

		public MYSort(String sortName, int sortId) {
			super();
			this.sortName = sortName;
			this.sortId = sortId;
		}

	}

	// class MyAdapter extends BaseAdapter {
	//
	// @Override
	// public int getCount() {
	// // TODO Auto-generated method stub
	// return data == null ? 0 : data.size();
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// // TODO Auto-generated method stub
	// return data.get(position);
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// // TODO Auto-generated method stub
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View v, ViewGroup parent) {
	// // TODO Auto-generated method stub
	// v = View.inflate(getActivity(), R.layout.item_head, null);
	// TextView tv_item_head = (TextView) v.findViewById(R.id.tv_item_head);
	// tv_item_head.setText(data.get(position));
	// return v;
	// }
	// }

	// private void initFragment() {
	// transaction = getFragmentManager().beginTransaction();
	// for (int i = 0; i < data.size(); i++) {
	// Fragment_Head f = new Fragment_Head(data.get(i));
	// fs.add(f);
	// transaction.add(R.id.fl_contain_headline, f);
	// }
	//
	// fragmentShowOrHide(0, true);
	// }
	//
	// private void fragmentShowOrHide(int position, boolean isInit) {
	// if (!isInit) {
	// transaction = getFragmentManager().beginTransaction();
	// }
	// for (int i = 0; i < data.size(); i++) {
	// if (i == position) {
	// transaction.show(fs.get(i));
	// } else {
	// transaction.hide(fs.get(i));
	// }
	//
	// }
	// transaction.commit();
	// }
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// // TODO Auto-generated method stub
	// fragmentShowOrHide(position, false);
	// }

}
