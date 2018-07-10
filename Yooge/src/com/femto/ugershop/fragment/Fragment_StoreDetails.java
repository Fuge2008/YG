package com.femto.ugershop.fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_GoodsDetails;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.Contacts.Data;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_StoreDetails extends BaseFragment implements OnRefreshListener2<ListView> {
	private PullToRefreshListView lv_product;
	private MylvStoreAdapter adapter;
	private DisplayImageOptions options;
	private int myId;
	private List<StoreDetails> storeDetails;
	
	private String starttime = "";
	private String endtime = "";
	private MyBroadCase mbr;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	int size = 0;
	private boolean isfirstbanner = true;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_sroredetails, container, false);
		mregisterReceiver();
		initParams();
		storeDetails = new ArrayList<StoreDetails>();
		initView(view);
		getData(0, "", "", 1, pageSize);
		MobclickAgent.onResume(getActivity());
		setPageStart("商城明细");
		return view;
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

	private void mregisterReceiver() {
		mbr = new MyBroadCase();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.search.storeoder.info");
		getActivity().registerReceiver(mbr, filter);
	}

	class MyBroadCase extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			System.out.println("zuo收到广播");
			if (action.equals("com.search.storeoder.info")) {
				if (starttime != null && starttime.length() != 0) {
					starttime = intent.getStringExtra("starttime");
					endtime = intent.getStringExtra("endtime");
					if (endtime == null) {
						endtime = "";
					}
					if (starttime == null) {
						starttime = "";
					}
					type = intent.getIntExtra("type", 0);
					storeDetails.clear();
					getData(type, starttime, endtime, 1, pageSize);
				}

			}
		}

	}

	private static String format = "yyyy-MM-dd HH:mm:ss";

	public static Date toDate(String date) throws Exception {
		if (null == date) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(date);
	}

	private void getData(int status, String starttime, String endtime, int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		showProgressDialog("加载中...");
		params.put("indent.user.id", myId);
		if (status == 1) {
			try {
				params.put("indent.createDate", starttime + " 00:00:00");
				params.put("indent.endDate", endtime + " 23:59:59");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		params.put("indent.status", status);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		System.out.println("zuo===params=" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetDesinProductDeails, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuo===" + response.toString());
				try {
					JSONArray jsonArray = response.getJSONArray("List");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String imgUrl = j.optString("imgUrl");
						String createDate = j.optString("createDate");
						String productName = j.optString("productName");
						int price = j.optInt("price");
						int count = j.optInt("count");
						int productId = j.optInt("productId");
						double percentage = j.optDouble("percentage");
						storeDetails.add(new StoreDetails(imgUrl, createDate, productName, price, count, percentage, productId));
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
		if (!hidden) {
			MobclickAgent.onResume(getActivity());
			setPageStart("商城明细");
		} else {
			MobclickAgent.onPause(getActivity());
			setPageEnd("商城明细");
		}

	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();

	}

	class MylvStoreAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return storeDetails == null ? 0 : storeDetails.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return storeDetails.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(getActivity(), R.layout.item_lv_odermg, null);
				h.ll_store_hint = (LinearLayout) v.findViewById(R.id.ll_store_hint);
				h.im_pic_show = (ImageView) v.findViewById(R.id.im_pic_show);
				h.tv_time_store = (TextView) v.findViewById(R.id.tv_time_store);
				h.tv_price_s = (TextView) v.findViewById(R.id.tv_price_s);
				h.tv_count_s = (TextView) v.findViewById(R.id.tv_count_s);
				h.tv_math_s = (TextView) v.findViewById(R.id.tv_math_s);
				h.tv_nub_odermg = (TextView) v.findViewById(R.id.tv_nub_odermg);
				h.tv_goods_name = (TextView) v.findViewById(R.id.tv_goods_name);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_nub_odermg.setText("" + storeDetails.get(position).count);
			h.ll_store_hint.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Activity_GoodsDetails.class);

					intent.putExtra("title", storeDetails.get(position).productName);
					intent.putExtra("productId", storeDetails.get(position).productId);
					startActivity(intent);
				}
			});
			h.tv_time_store.setText("" + storeDetails.get(position).createDate);
			h.tv_price_s.setText("" + storeDetails.get(position).price);
			h.tv_count_s.setText("" + storeDetails.get(position).count);
			h.tv_goods_name.setText("" + storeDetails.get(position).productName);
			h.tv_math_s
					.setText(""
							+ storeDetails.get(position).price
							* storeDetails.get(position).count
							+ "*"
							+ storeDetails.get(position).percentage
							* 100
							+ "%="
							+ df.format((storeDetails.get(position).price * storeDetails.get(position).count * storeDetails
									.get(position).percentage)) + "元");
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + storeDetails.get(position).imgUrl, h.im_pic_show,
					options);
			return v;
		}
	}

	class MyHolder {
		LinearLayout ll_store_hint;
		ImageView im_pic_show;

		TextView tv_goods_name, tv_time_store, tv_price_s, tv_count_s, tv_math_s, tv_nub_odermg;
	}

	class StoreDetails {
		String imgUrl, createDate, productName;
		int price, count, productId;
		double percentage;

		public StoreDetails(String imgUrl, String createDate, String productName, int price, int count, double percentage,
				int productId) {
			super();
			this.imgUrl = imgUrl;
			this.createDate = createDate;
			this.productName = productName;
			this.price = price;
			this.count = count;
			this.percentage = percentage;
			this.productId = productId;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		storeDetails.clear();
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
