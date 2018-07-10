package com.femto.ugershop.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_GoodsDetails;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.fragment.Fragment_NewGoods.MyHolder;
import com.femto.ugershop.fragment.Fragment_Production.ProductList;
import com.femto.ugershop.view.CircleImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AbsListView.OnScrollListener;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.huewu.pla.sample.ScaleImageView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class Fragment_LastProd extends BaseFragment implements OnItemClickListener, OnRefreshListener2<ListView> {
	private MultiColumnListView lv_product;
	private MyAdapter adapter;
	private View view;
	private int userId;
	private List<ProductList> productList;
	private DisplayImageOptions options;
	private int pageSize = 10;
	private int pageIndex = 1;
	private int size = 0;
	private int type = 2;
	private boolean isloading;
	private boolean isend = true;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				// lv_product.onRefreshComplete();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_salseprod, container, false);
		productList = new ArrayList<ProductList>();
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(false) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		initView(view);

		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		System.out.println("zuo===salse=hidden=" + hidden);
		if (!hidden && productList.size() == 0) {
			isloading = true;
			showProgressDialog("加载中...");
			getData(type, pageIndex, pageSize);
		}
		if (!hidden) {
			MobclickAgent.onResume(getActivity());
			setPageStart("往期作品");
		} else {
			MobclickAgent.onPause(getActivity());
			setPageEnd("往期作品");
		}
	}

	public Fragment_LastProd(int userId) {
		super();
		this.userId = userId;
	}

	private void initView(View v) {
		lv_product = (MultiColumnListView) v.findViewById(R.id.lv_product);
		adapter = new MyAdapter();
		lv_product.setAdapter(adapter);
		lv_product.setOnItemClickListener(this);
		lv_product.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				int lastItem = firstVisibleItem + visibleItemCount;
				if (lastItem == totalItemCount) {
					System.out.println("zuoScroll to the listview last item");
					if (isloading) {

					} else {
						if (isend) {
							// sv_hot.onRefreshComplete();
							// Toast.makeText(getActivity(), "没有更多了",
							// Toast.LENGTH_SHORT).show();
						} else {
							// pics.clear();
							// vpData.clear();
							isloading = true;
							getData(type, pageIndex, pageSize);
						}
					}

				}
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return productList == null ? 0 : productList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return productList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(getActivity(), R.layout.item_gv_hot, null);
				h.im_head_show = (ScaleImageView) v.findViewById(R.id.im_head_show);
				h.tv_goods_title = (TextView) v.findViewById(R.id.tv_goods_title);
				h.tv_price = (TextView) v.findViewById(R.id.tv_price_new1);
				h.tv_topcount = (TextView) v.findViewById(R.id.tv_topcount);
				h.tv_username_fist = (TextView) v.findViewById(R.id.tv_username_fist);
				h.llceshi = (LinearLayout) v.findViewById(R.id.llceshi);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			if (productList.get(position).title != null && !productList.get(position).title.equals("null")
					&& !productList.get(position).title.equals("")) {
				h.tv_goods_title.setText(productList.get(position).title);
			} else {
				h.tv_goods_title.setText("未填写");
			}

			if (productList.get(position).userName != null && !productList.get(position).userName.equals("null")
					&& !productList.get(position).userName.equals("")) {
				h.tv_username_fist.setText("by " + productList.get(position).userName);
			} else {
				h.tv_username_fist.setText("");
			}
			h.tv_price.setVisibility(View.GONE);

			Glide.with(getActivity()).load(AppFinalUrl.photoBaseUri + productList.get(position).productUrl + "-middle")
					.centerCrop().crossFade()
					/*
					 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
					 */
					.placeholder(R.drawable.tianc).crossFade().into(h.im_head_show);
			h.tv_topcount.setText("" + productList.get(position).topCount);
			return v;
		}
	}

	class MyHolder {
		TextView tv_topcount, tv_price, tv_username_fist, tv_goods_title;
		ScaleImageView im_head_show;
		LinearLayout llceshi;
	}

	private void getData(int type, int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		params.put("type", type);
		params.put("userId", userId);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetProductByUserIdAndType, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					JSONArray jsonArray = response.getJSONArray("productList");

					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String title = j.optString("title");
						String productUrl = j.optString("productUrl");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String url = j.optString("url");
						int shareCount = j.optInt("shareCount");
						int topCount = j.optInt("topCount");
						int discussCount = j.optInt("discussCount");
						int productId = j.optInt("productId");
						int price = j.optInt("price");
						productList.add(new ProductList(title, userName, createDate, url, shareCount, topCount, discussCount,
								productId, productUrl, price));
						size++;
					}
					if (size == 10) {
						isend = false;
						pageIndex++;
					} else {
						isend = true;
					}
					// lv_product.onRefreshComplete();

					adapter.notifyDataSetChanged();
					isloading = false;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	class ProductList {
		String title, userName, createDate, url, productUrl;
		int shareCount, topCount, discussCount, productId, price;

		public ProductList(String title, String userName, String createDate, String url, int shareCount, int topCount,
				int discussCount, int productId, String productUrl, int price) {
			super();
			this.title = title;
			this.userName = userName;
			this.createDate = createDate;
			this.url = url;
			this.shareCount = shareCount;
			this.topCount = topCount;
			this.discussCount = discussCount;
			this.productId = productId;
			this.productUrl = productUrl;
			this.price = price;
		}
	}

	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	//
	// }

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		productList.clear();
		pageIndex = 1;
		getData(type, pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			handler.sendEmptyMessage(1);
			Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getData(type, pageIndex, pageSize);
		}
	}

	@Override
	public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), Activity_GoodsDetails.class);
		intent.putExtra("title", productList.get(position).title);
		intent.putExtra("productId", productList.get(position).productId);
		startActivity(intent);
	}

}
