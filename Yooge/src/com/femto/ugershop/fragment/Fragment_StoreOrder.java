package com.femto.ugershop.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_LookLogistics;
import com.femto.ugershop.activity.Activity_LookPic;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.fragment.Fragment_CustomOrder.DiscussList;
import com.femto.ugershop.fragment.Fragment_CustomOrder.DiscussPhoto;
import com.femto.ugershop.view.CircleImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class Fragment_StoreOrder extends BaseFragment implements OnClickListener, OnRefreshListener2<ListView> {
	private View view;
	private RadioButton rb_alls, rb_sendeds, rb_waisends, rb_tradedones, rb_returns;
	private MyAdapter adapter;
	private PullToRefreshListView lv_orderde;
	private int myId;
	private DisplayImageOptions options;
	private List<ShopOrder> shopOrder;
	private MyBroadCase mbr;
	private int currentstatus = 0;
	private Map<Integer, Boolean> openpositon;
	private boolean isOpen;
	int size = 0;
	private String starttime = "";
	private String endtime = "";
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_orderde.onRefreshComplete();
				break;

			default:
				break;
			}
		};
	};
	DecimalFormat df;

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			MobclickAgent.onResume(getActivity());
			setPageStart("商城订单");
		} else {
			MobclickAgent.onPause(getActivity());
			setPageEnd("商城订单");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_storeorder, container, false);
		initParams();
		df = new DecimalFormat("######0.00");
		openpositon = new HashMap<Integer, Boolean>();
		mregisterReceiver();
		shopOrder = new ArrayList<ShopOrder>();
		initView(view);
		MobclickAgent.onResume(getActivity());
		setPageStart("商城订单");
		return view;
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
			if (action.equals("com.search.storeoder.info")) {
				// intent.putExtra("starttime", starttime);
				// intent.putExtra("endtime", endtime);
				// intent.putExtra("type", typetime);
				starttime = intent.getStringExtra("starttime");
				endtime = intent.getStringExtra("endtime");
				int type = intent.getIntExtra("type", 0);
				shopOrder.clear();
				getData(currentstatus, type, starttime, endtime, 1, pageSize);
			}
		}

	}

	private void initView(View v) {
		rb_alls = (RadioButton) v.findViewById(R.id.rb_alls);
		rb_sendeds = (RadioButton) v.findViewById(R.id.rb_sendeds);
		rb_waisends = (RadioButton) v.findViewById(R.id.rb_waisends);
		rb_tradedones = (RadioButton) v.findViewById(R.id.rb_tradedones);
		rb_returns = (RadioButton) v.findViewById(R.id.rb_returns);
		lv_orderde = (PullToRefreshListView) v.findViewById(R.id.lv_orderde);
		lv_orderde.setOnRefreshListener(this);
		lv_orderde.setMode(Mode.BOTH);
		rb_returns.setOnClickListener(this);
		rb_tradedones.setOnClickListener(this);
		rb_waisends.setOnClickListener(this);
		rb_sendeds.setOnClickListener(this);
		rb_alls.setOnClickListener(this);
		adapter = new MyAdapter();
		lv_orderde.setAdapter(adapter);
		getData(currentstatus, 0, "", "", 1, pageSize);
	}

	private void getData(int status, int type, String starttime, String endtime, int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		params.put("user.id", myId);
		params.put("user.status", status);
		params.put("user.type", type);
		if (type == 1) {
			params.put("user.createDate", starttime);
			params.put("user.lastLoginDate", endtime);
		}
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		showProgressDialog("加载中...");
		System.out.println("zuo==" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetShopOrderByUserId, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuo==" + response.toString());

				try {
					JSONArray jsonArray = response.getJSONArray("List");

					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						int count = j.optInt("count");
						int status = j.optInt("status");
						int productId = j.optInt("productId");
						int price = j.optInt("price");
						int orderId = j.optInt("orderId");
						int userId = j.optInt("userId");
						String postCode = j.optString("postCode");
						String imgUrl = j.optString("imgUrl");
						String dicuss = j.optString("dicuss");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String productName = j.optString("productName");
						String userImg = j.optString("userImg");
						String orderCode = j.optString("orderCode");
						double percentage = j.optDouble("percentage");
						List<DiscussList> discussList = new ArrayList<DiscussList>();
						JSONObject jj = j.getJSONObject("discussList");
						String content = jj.optString("content");
						String discussDate = jj.optString("discussDate");
						int score = jj.optInt("score");
						List<DiscussPhoto> discussPhoto = new ArrayList<DiscussPhoto>();
						JSONArray optJSONArray2 = jj.getJSONArray("discussPhoto");
						if (optJSONArray2 != null) {
							for (int l = 0; l < optJSONArray2.length(); l++) {
								JSONObject jjj = optJSONArray2.getJSONObject(l);
								String url = jjj.optString("url");
								discussPhoto.add(new DiscussPhoto(url));
							}
						}
						discussList.add(new DiscussList(content, discussDate, discussPhoto, score));

						shopOrder.add(new ShopOrder(count, status, productId, price, userId, postCode, imgUrl, dicuss, userName,
								createDate, productName, userImg, orderCode, percentage, orderId, discussList));
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					lv_orderde.onRefreshComplete();
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rb_returns:
			showorhint(rb_returns, rb_alls, rb_sendeds, rb_waisends, rb_tradedones);
			currentstatus = 4;
			pageIndex = 1;
			shopOrder.clear();
			getData(currentstatus, 0, "", "", 1, pageSize);
			break;
		case R.id.rb_alls:
			showorhint(rb_alls, rb_returns, rb_sendeds, rb_waisends, rb_tradedones);
			currentstatus = 0;
			shopOrder.clear();

			pageIndex = 1;

			getData(currentstatus, 0, "", "", 1, pageSize);
			break;
		case R.id.rb_sendeds:
			showorhint(rb_sendeds, rb_returns, rb_alls, rb_waisends, rb_tradedones);
			currentstatus = 2;
			shopOrder.clear();
			getData(currentstatus, 0, "", "", 1, pageSize);
			break;
		case R.id.rb_waisends:
			showorhint(rb_waisends, rb_sendeds, rb_returns, rb_alls, rb_tradedones);
			currentstatus = 1;
			shopOrder.clear();
			pageIndex = 1;
			getData(currentstatus, 0, "", "", 1, pageSize);
			break;
		case R.id.rb_tradedones:
			showorhint(rb_tradedones, rb_returns, rb_alls, rb_sendeds, rb_waisends);
			currentstatus = 3;
			shopOrder.clear();
			pageIndex = 1;
			getData(currentstatus, 0, "", "", 1, pageSize);
			break;

		default:
			break;
		}
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return shopOrder == null ? 0 : shopOrder.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return shopOrder.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			final MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(getActivity(), R.layout.item_lv_oder, null);
				h.im_goods_sd = (ImageView) v.findViewById(R.id.im_goods_sd);
				h.tv_name_sd = (TextView) v.findViewById(R.id.tv_name_sd);
				h.tv_price_sd = (TextView) v.findViewById(R.id.tv_price_sd);
				h.tv_count_sd = (TextView) v.findViewById(R.id.tv_count_sd);
				h.tv_all_sd = (TextView) v.findViewById(R.id.tv_all_sd);
				h.tv_status_sd = (TextView) v.findViewById(R.id.tv_status_sd);
				h.tv_time_sd = (TextView) v.findViewById(R.id.tv_time_sd);
				h.ll_mores = (LinearLayout) v.findViewById(R.id.ll_mores);
				h.ll_showmores = (LinearLayout) v.findViewById(R.id.ll_showmores);
				h.tv_looklogist = (TextView) v.findViewById(R.id.tv_looklogist);
				h.im_mores = (ImageView) v.findViewById(R.id.im_mores);
				h.tv_commendss = (TextView) v.findViewById(R.id.tv_commendss);
				h.tv_scores = (TextView) v.findViewById(R.id.tv_scores);
				h.ll_contain_stars = (LinearLayout) v.findViewById(R.id.ll_contain_stars);
				h.ll_contain_ims = (LinearLayout) v.findViewById(R.id.ll_contain_ims);
				h.tv_odernubs = (TextView) v.findViewById(R.id.tv_odernubs);
				h.tv_buyname = (TextView) v.findViewById(R.id.tv_buyname);
				h.im_head_so = (CircleImageView) v.findViewById(R.id.im_head_so);
				h.ll_showcommends = (LinearLayout) v.findViewById(R.id.ll_showcommends);
				h.tv_nub_de = (TextView) v.findViewById(R.id.tv_nub_de);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_nub_de.setText("X" + shopOrder.get(position).count);
			if (shopOrder.get(position).discussList != null && shopOrder.get(position).discussList.get(0).content.length() != 0) {
				h.ll_showcommends.setVisibility(View.VISIBLE);
			} else {
				h.ll_showcommends.setVisibility(View.GONE);
			}
			h.tv_odernubs.setText("" + shopOrder.get(position).orderCode);
			h.tv_buyname.setText("" + shopOrder.get(position).userName);
			h.tv_commendss.setText("" + shopOrder.get(position).discussList.get(0).content);
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + shopOrder.get(position).userImg, h.im_head_so,
					options);
			if (shopOrder.get(position).status == 0) {
				h.tv_status_sd.setText("待付款");
			} else {
			}
			if (shopOrder.get(position).status == 0 || shopOrder.get(position).status == 1) {
				h.tv_looklogist.setVisibility(View.GONE);
			} else {
				h.tv_looklogist.setVisibility(View.VISIBLE);
			}

			if (shopOrder.get(position).status == 1) {
				h.tv_status_sd.setText("待发货");
			} else {

			}
			if (shopOrder.get(position).status == 2) {
				h.tv_status_sd.setText("待收货");
			}
			if (shopOrder.get(position).status == 3) {
				h.tv_status_sd.setText("交易完成");
			}
			if (shopOrder.get(position).status == 4) {
				h.tv_status_sd.setText("交易关闭");
			}
			if (shopOrder.get(position).status == 5) {
				h.tv_status_sd.setText("退款中");
			}
			if (shopOrder.get(position).status == 6) {
				h.tv_status_sd.setText("退款成功");
			}
			if (shopOrder.get(position).status == 7) {
				h.tv_status_sd.setText("换货中");
			}
			if (shopOrder.get(position).status == 8) {
				h.tv_status_sd.setText("换货成功");
			}
			// if (openpositon.size() != 0 && openpositon.get(position) != null
			// && openpositon.get(position)) {
			// h.ll_mores.setVisibility(View.VISIBLE);
			// h.im_mores.setImageResource(R.drawable.arrows_up);
			// } else {
			// h.ll_mores.setVisibility(View.GONE);
			// h.im_mores.setImageResource(R.drawable.arrows_down);
			// }
			h.ll_showmores.setVisibility(View.GONE);
			h.tv_name_sd.setText("" + shopOrder.get(position).productName);
			h.tv_time_sd.setText("" + shopOrder.get(position).createDate);
			h.tv_price_sd.setText("" + shopOrder.get(position).price + "元");
			h.tv_count_sd.setText("" + shopOrder.get(position).count);
			h.tv_all_sd
					.setText(""
							+ shopOrder.get(position).count
							+ "*"
							+ shopOrder.get(position).price
							+ "*"
							+ (shopOrder.get(position).percentage * 100)
							+ "%="
							+ df.format((shopOrder.get(position).price * shopOrder.get(position).percentage * shopOrder
									.get(position).count)) + "元");
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + shopOrder.get(position).imgUrl, h.im_goods_sd,
					options);
			// h.ll_showmores.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// if (openpositon.size() != 0 && openpositon.get(position) != null)
			// {
			// isOpen = openpositon.get(position);
			// } else {
			// isOpen = false;
			// }
			// if (isOpen) {
			// openpositon.put(position, false);
			// h.ll_mores.setVisibility(View.GONE);
			// h.im_mores.setImageResource(R.drawable.arrows_down);
			//
			// } else {
			//
			// h.ll_mores.setVisibility(View.VISIBLE);
			// openpositon.put(position, true);
			// h.im_mores.setImageResource(R.drawable.arrows_up);
			// }
			// }
			// });
			h.tv_looklogist.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Activity_LookLogistics.class);
					intent.putExtra("orderId", shopOrder.get(position).orderId);
					intent.putExtra("type", 1);
					startActivity(intent);
				}
			});

			h.ll_contain_stars.removeAllViews();
			for (int i = 0; i < shopOrder.get(position).count; i++) {

				// View vv = View.inflate(getActivity(), R.layout.image_star,
				// null);
				// h.ll_contain_stars.addView(vv);
			}
			h.tv_scores.setText("" + shopOrder.get(position).discussList.get(0).score + "分");
			h.ll_contain_ims.removeAllViews();
			final ArrayList<String> pics = new ArrayList<String>();
			for (int i = 0; i < shopOrder.get(position).discussList.get(0).discussPhoto.size(); i++) {
				View vvv = View.inflate(getActivity(), R.layout.item_image, null);
				pics.add(shopOrder.get(position).discussList.get(0).discussPhoto.get(i).url);
				ImageView im = (ImageView) vvv.findViewById(R.id.im_commends);
				im.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), Activity_LookPic.class);
						intent.putExtra("position", 0);
						intent.putStringArrayListExtra("pics", pics);
						startActivity(intent);
					}
				});
				ImageLoader.getInstance().displayImage(
						AppFinalUrl.BASEURL + shopOrder.get(position).discussList.get(0).discussPhoto.get(i).url, im, options);
				h.ll_contain_ims.addView(vvv);
			}
			h.ll_contain_stars.removeAllViews();
			for (int i = 0; i < shopOrder.get(position).discussList.get(0).score; i++) {
				View vv = View.inflate(getActivity(), R.layout.image_star, null);
				h.ll_contain_stars.addView(vv);
			}
			return v;
		}
	}

	class MyHolder {
		ImageView im_goods_sd, im_mores;
		TextView tv_name_sd, tv_time_sd, tv_price_sd, tv_count_sd, tv_all_sd, tv_allfee_sd, tv_status_sd, tv_looklogist,
				tv_commendss, tv_scores, tv_odernubs, tv_buyname, tv_nub_de;
		LinearLayout ll_showmores, ll_mores, ll_contain_stars, ll_contain_ims, ll_showcommends;
		CircleImageView im_head_so;
	}

	class ShopOrder {
		int count, status, productId, price, userId, orderId;
		String postCode, imgUrl, dicuss, userName, createDate, productName, userImg, orderCode;
		double percentage;
		List<DiscussList> discussList;

		public ShopOrder(int count, int status, int productId, int price, int userId, String postCode, String imgUrl,
				String dicuss, String userName, String createDate, String productName, String userImg, String orderCode,
				double percentage, int orderId, List<DiscussList> discussList) {
			super();
			this.count = count;
			this.status = status;
			this.productId = productId;
			this.price = price;
			this.userId = userId;
			this.postCode = postCode;
			this.imgUrl = imgUrl;
			this.dicuss = dicuss;
			this.userName = userName;
			this.createDate = createDate;
			this.productName = productName;
			this.userImg = userImg;
			this.orderCode = orderCode;
			this.percentage = percentage;
			this.orderId = orderId;
			this.discussList = discussList;
		}

	}

	class DiscussList {
		String content, discussDate;
		List<DiscussPhoto> discussPhoto;
		int score;

		public DiscussList(String content, String discussDate, List<DiscussPhoto> discussPhoto, int score) {
			super();
			this.content = content;
			this.discussDate = discussDate;
			this.discussPhoto = discussPhoto;
			this.score = score;
		}
	}

	class DiscussPhoto {
		String url;

		public DiscussPhoto(String url) {
			super();
			this.url = url;
		}

	}

	public void showorhint(RadioButton rbshow, RadioButton rb1, RadioButton rb2, RadioButton rb3, RadioButton rb4) {
		rbshow.setChecked(true);
		rb1.setChecked(false);
		rb2.setChecked(false);
		rb3.setChecked(false);
		rb4.setChecked(false);
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
		shopOrder.clear();
		getData(currentstatus, 0, "", "", 1, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			handler.sendEmptyMessage(1);
			Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getData(currentstatus, 0, starttime, endtime, pageIndex, pageSize);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(mbr);
	}
}
