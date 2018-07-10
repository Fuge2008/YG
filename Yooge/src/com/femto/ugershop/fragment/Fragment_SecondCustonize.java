package com.femto.ugershop.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_CustomGoodsDetails;
import com.femto.ugershop.activity.Activity_Login;
import com.femto.ugershop.activity.MainActivity;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.view.MyGridView;
import com.femto.ugershop.view.ScrollViewWithListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class Fragment_SecondCustonize extends BaseFragment implements OnItemClickListener, OnRefreshListener2<GridView> {
	private View view;
	private PullToRefreshGridView lv_secondvustom;
	private MyAdapter adapter;
	private MBC mbc;
	private int sWidth = 0;
	private int sortId;
	private List<MakeList> ms;
	private boolean isinit = false;
	private boolean isload = true;
	private int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;

	@SuppressLint("ValidFragment")
	public Fragment_SecondCustonize(int sortId) {
		super();
		this.sortId = sortId;
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_secondvustom.onRefreshComplete();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_secondcustomize, container, false);
		initParams();
		regMBC();
		initView(view);
		initCon();
		return view;
	}

	// 初始化参数
	private void initParams() {
		int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		sWidth = (int) ((screenWidth - dp2px(getActivity(), 30)) / 2.0);
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void initView(View v) {
		// TODO Auto-generated method stub
		lv_secondvustom = (PullToRefreshGridView) v.findViewById(R.id.lv_secondvustom);
	}

	private void initCon() {
		// TODO Auto-generated method stub
		ms = new ArrayList<MakeList>();
		lv_secondvustom.setOnItemClickListener(this);
		lv_secondvustom.setOnRefreshListener(this);
		lv_secondvustom.setMode(Mode.BOTH);
		// Activity_CustomGoodsDetails
		if (MyApplication.issingle) {
			lv_secondvustom.getRefreshableView().setNumColumns(1);
		} else {
			lv_secondvustom.getRefreshableView().setNumColumns(2);
		}
		lv_secondvustom.setFocusable(false);
		adapter = new MyAdapter();
		lv_secondvustom.setAdapter(adapter);
		getData(pageIndex, pageSize);
	}

	// 注册广播
	private void regMBC() {
		mbc = new MBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.femto.single");
		filter.addAction("com.femto.refresh");
		filter.addAction("com.femto.totop");
		getActivity().registerReceiver(mbc, filter);
	}

	class MBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("com.femto.single")) {
				// 判断是否是单列显示
				if (MyApplication.issingle) {
					lv_secondvustom.getRefreshableView().setNumColumns(1);
				} else {
					lv_secondvustom.getRefreshableView().setNumColumns(2);
				}
				adapter.notifyDataSetChanged();
			} else if (action.equals("com.femto.refresh")) {

			} else if (action.equals("com.femto.totop")) {
				if (ms != null && ms.size() != 0) {
					lv_secondvustom.getRefreshableView().setSelection(0);
				}

			}

		}
	}

	// 获取数据
	private void getData(int pi, int ps) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("userProduct.makeSort.id", sortId);
		params.put("pageModel.pageIndex", pi);
		params.put("pageModel.pageSize", ps);
		isload = true;
		MyApplication.ahc.post(AppFinalUrl.usergetMakeShow, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				JSONArray optJSONArray = response.optJSONArray("list");
				lv_secondvustom.onRefreshComplete();
				if (optJSONArray != null) {
					for (int i = 0; i < optJSONArray.length(); i++) {
						JSONObject j = optJSONArray.optJSONObject(i);
						String name = j.optString("name");
						String userName = j.optString("userName");
						String smallUrl = j.optString("smallUrl");
						String productname = j.optString("productname");
						String productpirce = j.optString("productpirce");
						String url = j.optString("url");
						int makeProductId = j.optInt("makeProductId");
						int userId = j.optInt("userId");
						int width = j.optInt("width");
						int high = j.optInt("high");
						ms.add(new MakeList(name, userName, smallUrl, url, makeProductId, userId, width, high, productname,
								productpirce));

						size++;
					}
					if (size == 10) {
						isend = false;
						pageIndex++;
					} else {
						isend = true;
					}

					adapter.notifyDataSetChanged();
					size = 0;

				}
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ms == null ? 0 : ms.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ms.get(position);
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
				v = View.inflate(getActivity(), R.layout.item_nsc, null);
				h.im_cgd = (ImageView) v.findViewById(R.id.im_cgd);
				h.tv_cgdname = (TextView) v.findViewById(R.id.tv_cgdname);
				h.tv_cgdprice = (TextView) v.findViewById(R.id.tv_cgdprice);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			// 判断是否是单列显示
			if (MyApplication.issingle) {
				LayoutParams params = h.im_cgd.getLayoutParams();
				params.height = (int) (sWidth * 1.5) * 2;
				h.im_cgd.setLayoutParams(params);
			} else {
				LayoutParams params = h.im_cgd.getLayoutParams();
				params.height = (int) (sWidth * 1.5);
				// params.width = sWidth;
				h.im_cgd.setLayoutParams(params);
			}
			Glide.with(getActivity()).load(ms.get(position).smallUrl)

			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(h.im_cgd);
			h.tv_cgdname.setText("" + ms.get(position).productname);
			h.tv_cgdprice.setText("¥ " + ms.get(position).productpirce);
			return v;
		}
	}

	class MyHolder {
		ImageView im_cgd;
		TextView tv_cgdname, tv_cgdprice;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (MyApplication.islogin) {
			Intent intent = new Intent(getActivity(), Activity_CustomGoodsDetails.class);
			intent.putExtra("makeProductId", ms.get(position).makeProductId);
			startActivity(intent);
		} else {
			Intent intent = new Intent(getActivity(), Activity_Login.class);
			startActivity(intent);
		}

	}

	class MakeList {
		String name, userName, smallUrl, url, productname, productpirce;
		int makeProductId, userId, width, high;

		public MakeList(String name, String userName, String smallUrl, String url, int makeProductId, int userId, int width,
				int high, String productname, String productpirce) {
			super();
			this.name = name;
			this.userName = userName;
			this.smallUrl = smallUrl;
			this.url = url;
			this.makeProductId = makeProductId;
			this.userId = userId;
			this.width = width;
			this.high = high;
			this.productname = productname;
			this.productpirce = productpirce;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
		// TODO Auto-generated method stub
		ms.clear();
		pageIndex = 1;
		getData(pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			handler.sendEmptyMessage(1);
			Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getData(pageIndex, pageSize);
		}
	}
}
