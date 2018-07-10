package com.femto.ugershop.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.fragment.Fragment_CustomOrder.MyBroadCase;
import com.femto.ugershop.view.CircleImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_CustomDetails extends BaseFragment implements OnRefreshListener2<ListView> {
	private PullToRefreshListView lv_product;
	private MylvStoreAdapter adapter;
	private int myId;
	private DisplayImageOptions options;
	private List<DesinMake> desinMake;
	int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private boolean isfirstbanner = true;
	private MyBroadCase mbr;
	private String starttime = "";
	private String endtime = "";
	private int type = 0;
	DecimalFormat df = new DecimalFormat("######0.00");
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_product.onRefreshComplete();
				break;

			default:
				break;
			}

		}

	};

	class MyBroadCase extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.search.customoder.info")) {
				starttime = intent.getStringExtra("starttime");
				endtime = intent.getStringExtra("endtime");
				if (starttime != null && starttime.length() != 0) {

					if (endtime == null) {
						endtime = "";
					}
					if (starttime == null) {
						starttime = "";
					}

					type = intent.getIntExtra("type", 0);
					desinMake.clear();
					getData(type, starttime, endtime, 1, pageSize);
				}

			}
		}

	}

	private void mregisterReceiver() {
		mbr = new MyBroadCase();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.search.customoder.info");
		getActivity().registerReceiver(mbr, filter);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_sroredetails, container, false);
		mregisterReceiver();
		initParams();
		desinMake = new ArrayList<DesinMake>();
		initView(view);
		return view;
	}

	private void initView(View v) {
		lv_product = (PullToRefreshListView) v.findViewById(R.id.lv_pustoredetails);
		lv_product.setOnRefreshListener(this);
		lv_product.setMode(Mode.BOTH);
		adapter = new MylvStoreAdapter();
		lv_product.setAdapter(adapter);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden == false && desinMake.size() == 0) {
			getData(0, "", "", 1, pageSize);
		}
		if (!hidden) {
			MobclickAgent.onResume(getActivity());
			setPageStart("我的粉丝");
		} else {
			MobclickAgent.onPause(getActivity());
			setPageEnd("我的粉丝");
		}
	}

	private void getData(int type, String starttime, String endtime, int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		params.put("user.id", myId);
		params.put("user.type", type);
		if (type == 1) {
			params.put("user.createDate", starttime + " 00:00:00");
			params.put("user.lastLoginDate", endtime + " 23:59:59");
		}
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		showProgressDialog("加载中...");
		System.out.println("zuo===" + params);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetDesinMake, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo==cde" + response.toString());
				dismissProgressDialog();
				try {
					JSONArray jsonArray = response.getJSONArray("List");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						int price = j.optInt("price");
						int count = j.optInt("count");
						int userId = j.optInt("userId");
						double percentage = j.optDouble("percentage");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String productName = j.optString("productName");
						String userImg = j.optString("userImg");
						desinMake
								.add(new DesinMake(price, count, userId, percentage, userName, createDate, productName, userImg));
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					lv_product.onRefreshComplete();
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onDetach() {
		super.onDetach();

	}

	class MylvStoreAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return desinMake == null ? 0 : desinMake.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return desinMake.get(position);
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
				v = View.inflate(getActivity(), R.layout.item_lv_cd, null);
				h.im_pic_head = (CircleImageView) v.findViewById(R.id.im_pic_head);
				h.tv_goods_name_cd = (TextView) v.findViewById(R.id.tv_goods_name_cd);
				h.tv_time_store_cd = (TextView) v.findViewById(R.id.tv_time_store_cd);
				h.tv_price_s_cd = (TextView) v.findViewById(R.id.tv_price_s_cd);
				h.tv_count_cd = (TextView) v.findViewById(R.id.tv_count_cd);
				h.tv_math_cd = (TextView) v.findViewById(R.id.tv_math_cd);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_goods_name_cd.setText("" + desinMake.get(position).productName);
			h.tv_time_store_cd.setText("" + desinMake.get(position).createDate);
			h.tv_price_s_cd.setText("" + desinMake.get(position).price);
			h.tv_count_cd.setText("" + desinMake.get(position).count);

			h.tv_math_cd
					.setText(""
							+ desinMake.get(position).price
							* desinMake.get(position).count
							+ "*"
							+ desinMake.get(position).percentage
							* 100
							+ "%="
							+ df.format((desinMake.get(position).percentage * desinMake.get(position).price * desinMake
									.get(position).count)) + "元");
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + desinMake.get(position).userImg, h.im_pic_head,
					options);
			return v;
		}
	}

	class MyHolder {
		CircleImageView im_pic_head;
		TextView tv_goods_name_cd, tv_time_store_cd, tv_price_s_cd, tv_count_cd, tv_math_cd;
	}

	class DesinMake {
		int price, count, userId;
		double percentage;
		String userName, createDate, productName, userImg;

		public DesinMake(int price, int count, int userId, double percentage, String userName, String createDate,
				String productName, String userImg) {
			super();
			this.price = price;
			this.count = count;
			this.userId = userId;
			this.percentage = percentage;
			this.userName = userName;
			this.createDate = createDate;
			this.productName = productName;
			this.userImg = userImg;
		}

	}

	private void initParams() {
		SharedPreferences sp = getActivity().getSharedPreferences("Login", getActivity().MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		desinMake.clear();
		type = 0;
		getData(0, "", "", 1, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			handler.sendEmptyMessage(1);
			Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getData(type, starttime, endtime, pageIndex, pageSize);
		}
	}
}
