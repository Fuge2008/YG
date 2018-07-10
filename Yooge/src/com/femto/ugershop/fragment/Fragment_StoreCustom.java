package com.femto.ugershop.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_Commends_Store;
import com.femto.ugershop.activity.Activity_GoodsDetails;
import com.femto.ugershop.activity.Activity_LookLogistics;
import com.femto.ugershop.activity.Activity_LookPic;
import com.femto.ugershop.activity.Activity_Order;
import com.femto.ugershop.activity.Activity_ReturnOrBack;
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

public class Fragment_StoreCustom extends BaseFragment implements OnClickListener, OnRefreshListener2<ListView> {
	private View view;
	private RadioButton rb_alls, rb_sendeds, rb_waisends, rb_tradedones, rb_returns;
	private int myId;
	private DisplayImageOptions options;
	int status = 0;
	int type = 0;
	private List<ShopOrder> shopOrder;
	private Map<Integer, Boolean> openpositon;
	private boolean isOpen;
	private MyAdapter adapter;
	private PullToRefreshListView lv_orderde;
	private MyBC mbc;
	int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private String starttime = "";
	private String endtime = "";
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(R.layout.fragment_storeorder, container, false);
		initParams();
		registBC();
		shopOrder = new ArrayList<ShopOrder>();
		openpositon = new HashMap<Integer, Boolean>();
		initView(view);

		MobclickAgent.onResume(getActivity());
		setPageStart("商城订单");

		return view;
	}

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

	private void registBC() {
		mbc = new MyBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.search.storeodercustom.info");
		getActivity().registerReceiver(mbc, filter);

	}

	class MyBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.search.storeodercustom.info")) {
				starttime = intent.getStringExtra("starttime");
				endtime = intent.getStringExtra("endtime");
				type = intent.getIntExtra("type", 0);
				getData(status, type, starttime, endtime, 1, pageSize);
			}

		}

	}

	private void initView(View v) {
		// TODO Auto-generated method stub
		rb_alls = (RadioButton) v.findViewById(R.id.rb_alls);
		rb_sendeds = (RadioButton) v.findViewById(R.id.rb_sendeds);
		rb_waisends = (RadioButton) v.findViewById(R.id.rb_waisends);
		rb_tradedones = (RadioButton) v.findViewById(R.id.rb_tradedones);
		rb_returns = (RadioButton) v.findViewById(R.id.rb_returns);
		lv_orderde = (PullToRefreshListView) v.findViewById(R.id.lv_orderde);
		lv_orderde.setOnRefreshListener(this);
		lv_orderde.setMode(Mode.BOTH);
		// lv_orderde = (ListView) v.findViewById(R.id.lv_orderde);
		rb_returns.setOnClickListener(this);
		rb_tradedones.setOnClickListener(this);
		rb_waisends.setOnClickListener(this);
		rb_sendeds.setOnClickListener(this);
		rb_alls.setOnClickListener(this);
		adapter = new MyAdapter();
		lv_orderde.setAdapter(adapter);
		getData(status, type, "", "", pageIndex, pageSize);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rb_returns:
			shopOrder.clear();
			showorhint(rb_returns, rb_alls, rb_sendeds, rb_waisends, rb_tradedones);
			status = 4;
			pageIndex = 1;
			getData(status, type, "", "", 1, pageSize);
			//
			break;
		case R.id.rb_alls:
			shopOrder.clear();
			showorhint(rb_alls, rb_returns, rb_sendeds, rb_waisends, rb_tradedones);
			status = 0;
			pageIndex = 1;
			type = 0;
			getData(status, type, "", "", 1, pageSize);
			// getData(0, 0, "", "");
			//
			break;
		case R.id.rb_sendeds:
			showorhint(rb_sendeds, rb_returns, rb_alls, rb_waisends, rb_tradedones);
			status = 2;
			pageIndex = 1;
			shopOrder.clear();
			getData(status, type, "", "", 1, pageSize);
			// getData(2, 0, "", "");
			//
			break;
		case R.id.rb_waisends:
			showorhint(rb_waisends, rb_sendeds, rb_returns, rb_alls, rb_tradedones);
			status = 1;
			shopOrder.clear();
			pageIndex = 1;
			getData(status, type, "", "", 1, pageSize);
			//
			break;
		case R.id.rb_tradedones:
			showorhint(rb_tradedones, rb_returns, rb_alls, rb_sendeds, rb_waisends);
			status = 3;
			shopOrder.clear();
			pageIndex = 1;
			getData(status, type, "", "", 1, pageSize);
			//
			break;

		default:
			break;
		}
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
		System.out.println("zuo" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetOrderByUserId, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuo=ddd订单=" + response.toString());
				lv_orderde.onRefreshComplete();

				JSONArray jsonArray = response.optJSONArray("List");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject j = jsonArray.optJSONObject(i);
					int count = j.optInt("count");
					int status = j.optInt("status");
					int productId = j.optInt("productId");
					Double price = j.optDouble("price");
					int userId = j.optInt("userId");
					int orderId = j.optInt("orderId");
					String postCode = j.optString("postCode");
					String codeUrl = j.optString("codeUrl");
					String imgUrl = j.optString("imgUrl");
					String dicuss = j.optString("dicuss");
					String userName = j.optString("userName");
					String createDate = j.optString("createDate");
					String productName = j.optString("productName");
					String userImg = j.optString("userImg");
					String orderCode = j.optString("orderCode");
					double percentage = j.optDouble("percentage");
					JSONArray jj = j.optJSONArray("customList");
					String userNameY = "";
					String nameY = "";
					if (jj != null && jj.length() != 0) {
						userNameY = jj.optJSONObject(0).optString("userName");
						nameY = jj.optJSONObject(0).optString("name");
					}

					shopOrder.add(new ShopOrder(count, status, productId, price, userId, postCode, imgUrl, dicuss, userName,
							createDate, productName, userImg, orderCode, percentage, orderId, userNameY, nameY, codeUrl));
					size++;
				}
				if (size == 10) {
					pageIndex++;
					isend = false;
				} else {
					isend = true;
				}
				size = 0;
				System.out.println("zuodingdan===shopOrder.size===" + shopOrder.size());
				adapter.notifyDataSetChanged();

			}
		});
	}

	public void showorhint(RadioButton rbshow, RadioButton rb1, RadioButton rb2, RadioButton rb3, RadioButton rb4) {
		rbshow.setChecked(true);
		rb1.setChecked(false);
		rb2.setChecked(false);
		rb3.setChecked(false);
		rb4.setChecked(false);
	}

	class ShopOrder {
		int count, status, productId, userId, orderId;
		String postCode, imgUrl, dicuss, userName, createDate, productName, userImg, orderCode, nameY, userNameY, codeUrl;
		double percentage, price;

		public ShopOrder(int count, int status, int productId, double price, int userId, String postCode, String imgUrl,
				String dicuss, String userName, String createDate, String productName, String userImg, String orderCode,
				double percentage, int orderId, String nameY, String userNameY, String codeUrl) {
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
			this.nameY = nameY;
			this.userNameY = userNameY;
			this.codeUrl = codeUrl;
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
				v = View.inflate(getActivity(), R.layout.item_lv_sdcustom, null);
				h.im_goods_sd = (ImageView) v.findViewById(R.id.im_goods_sd_c);
				h.tv_name_sd = (TextView) v.findViewById(R.id.tv_name_sd_c);
				h.tv_price_sd = (TextView) v.findViewById(R.id.tv_price_sd_c);
				h.tv_count_sd = (TextView) v.findViewById(R.id.tv_count_sd_c);
				h.tv_all_sd = (TextView) v.findViewById(R.id.tv_all_sd_c);
				h.tv_tuik = (TextView) v.findViewById(R.id.tv_tuik);
				h.tv_status_sd = (TextView) v.findViewById(R.id.tv_status_sd_c);
				h.tv_time_sd = (TextView) v.findViewById(R.id.tv_time_sd_c);
				h.ll_mores = (LinearLayout) v.findViewById(R.id.ll_mores_c);
				h.ll_showmores = (LinearLayout) v.findViewById(R.id.ll_showmores_c);
				h.tv_looklogist_custom = (TextView) v.findViewById(R.id.tv_looklogist_custom);
				h.im_mores = (ImageView) v.findViewById(R.id.im_mores_c);
				h.tv_arb = (TextView) v.findViewById(R.id.tv_arb);
				h.im_tochat = (ImageView) v.findViewById(R.id.im_tochat);
				h.tv_prise_sc = (TextView) v.findViewById(R.id.tv_prise_sc);
				h.tv_suregetg = (TextView) v.findViewById(R.id.tv_suregetg);
				h.tv_surepay = (TextView) v.findViewById(R.id.tv_surepay);
				h.tv_cancleorder = (TextView) v.findViewById(R.id.tv_cancleorder);
				h.tv_notif = (TextView) v.findViewById(R.id.tv_notif);
				h.tv_deletorder = (TextView) v.findViewById(R.id.tv_deletorder);
				h.tv_looklogist = (TextView) v.findViewById(R.id.tv_looklogist);
				h.tv_nub_lcs = (TextView) v.findViewById(R.id.tv_nub_lcs);
				h.ll_contain_stars_c = (LinearLayout) v.findViewById(R.id.ll_contain_stars_c);
				h.im_cq_order = (ImageView) v.findViewById(R.id.im_cq_order);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_nub_lcs.setText("X" + shopOrder.get(position).count);
			if (shopOrder.get(position).status == 0) {
				h.tv_status_sd.setText("待付款");
			}
			if (shopOrder.get(position).status == 1) {
				h.tv_status_sd.setText("待发货");
				h.tv_looklogist_custom.setVisibility(View.GONE);
			} else {
				h.tv_looklogist_custom.setVisibility(View.VISIBLE);
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

			if (shopOrder.get(position).status == 0) {
				h.tv_surepay.setVisibility(View.VISIBLE);
				h.tv_cancleorder.setVisibility(View.VISIBLE);
				h.tv_looklogist.setVisibility(View.GONE);
				h.tv_arb.setVisibility(View.GONE);
				h.tv_suregetg.setVisibility(View.GONE);
				h.tv_prise_sc.setVisibility(View.GONE);
				h.tv_notif.setVisibility(View.GONE);
				h.tv_deletorder.setVisibility(View.GONE);
				h.tv_looklogist.setVisibility(View.GONE);
			} else {

			}
			if (shopOrder.get(position).status == 1) {
				h.tv_notif.setVisibility(View.VISIBLE);
				h.tv_arb.setVisibility(View.GONE);
				h.tv_tuik.setVisibility(View.VISIBLE);
				h.tv_suregetg.setVisibility(View.GONE);
				h.tv_prise_sc.setVisibility(View.GONE);
				h.tv_deletorder.setVisibility(View.GONE);
				h.tv_looklogist.setVisibility(View.GONE);
				h.tv_surepay.setVisibility(View.GONE);
				h.tv_cancleorder.setVisibility(View.GONE);
			} else {

			}
			if (shopOrder.get(position).status == 2) {
				h.tv_suregetg.setVisibility(View.VISIBLE);
				h.tv_arb.setVisibility(View.VISIBLE);
				h.tv_prise_sc.setVisibility(View.GONE);
				h.tv_tuik.setVisibility(View.GONE);
				h.tv_notif.setVisibility(View.GONE);
				h.tv_deletorder.setVisibility(View.GONE);
				h.tv_looklogist.setVisibility(View.GONE);
				h.tv_surepay.setVisibility(View.GONE);
				h.tv_cancleorder.setVisibility(View.GONE);

			} else {

			}
			if (shopOrder.get(position).status == 3) {
				h.tv_deletorder.setVisibility(View.GONE);
				h.tv_looklogist.setVisibility(View.GONE);
				h.tv_prise_sc.setVisibility(View.VISIBLE);
				h.tv_tuik.setVisibility(View.GONE);
				h.tv_suregetg.setVisibility(View.GONE);
				h.tv_arb.setVisibility(View.GONE);
				h.tv_notif.setVisibility(View.GONE);
				h.tv_surepay.setVisibility(View.GONE);
				h.tv_cancleorder.setVisibility(View.GONE);
			}
			if (shopOrder.get(position).status == 4) {
				h.tv_deletorder.setVisibility(View.GONE);
				h.tv_tuik.setVisibility(View.GONE);
				h.tv_deletorder.setVisibility(View.GONE);
				h.tv_notif.setVisibility(View.GONE);
				h.tv_arb.setVisibility(View.GONE);
				h.tv_suregetg.setVisibility(View.GONE);
				h.tv_prise_sc.setVisibility(View.GONE);
				h.tv_looklogist.setVisibility(View.GONE);
				h.tv_surepay.setVisibility(View.GONE);
				h.tv_cancleorder.setVisibility(View.GONE);

			} else {

			}
			if (shopOrder.get(position).status == 5) {
				h.tv_looklogist.setVisibility(View.GONE);
				h.tv_tuik.setVisibility(View.GONE);
				h.tv_deletorder.setVisibility(View.GONE);
				h.tv_notif.setVisibility(View.GONE);
				h.tv_arb.setVisibility(View.GONE);
				h.tv_suregetg.setVisibility(View.GONE);
				h.tv_prise_sc.setVisibility(View.GONE);
				h.tv_surepay.setVisibility(View.GONE);
				h.tv_cancleorder.setVisibility(View.GONE);
			} else {

			}
			if (shopOrder.get(position).status == 6) {
				h.tv_deletorder.setVisibility(View.GONE);
				h.tv_notif.setVisibility(View.GONE);
				h.tv_arb.setVisibility(View.GONE);
				h.tv_tuik.setVisibility(View.GONE);
				h.tv_suregetg.setVisibility(View.GONE);
				h.tv_prise_sc.setVisibility(View.GONE);
				h.tv_looklogist.setVisibility(View.GONE);
				h.tv_surepay.setVisibility(View.GONE);
				h.tv_cancleorder.setVisibility(View.GONE);
			} else {

			}
			if (shopOrder.get(position).status == 7) {
				h.tv_looklogist.setVisibility(View.GONE);
				h.tv_suregetg.setVisibility(View.VISIBLE);
				h.tv_tuik.setVisibility(View.GONE);
				h.tv_deletorder.setVisibility(View.GONE);
				h.tv_notif.setVisibility(View.GONE);
				h.tv_arb.setVisibility(View.GONE);
				h.tv_prise_sc.setVisibility(View.GONE);
				h.tv_surepay.setVisibility(View.GONE);
				h.tv_cancleorder.setVisibility(View.GONE);
			} else {

			}
			if (shopOrder.get(position).status == 8) {
				h.tv_looklogist.setVisibility(View.GONE);
				h.tv_suregetg.setVisibility(View.VISIBLE);
				h.tv_prise_sc.setVisibility(View.VISIBLE);
				h.tv_tuik.setVisibility(View.GONE);
				h.tv_deletorder.setVisibility(View.GONE);
				h.tv_notif.setVisibility(View.GONE);
				h.tv_arb.setVisibility(View.GONE);
				h.tv_surepay.setVisibility(View.GONE);
				h.tv_cancleorder.setVisibility(View.GONE);
			} else {
			}

			if (openpositon.size() != 0 && openpositon.get(position) != null && openpositon.get(position)) {
				h.ll_mores.setVisibility(View.VISIBLE);
				h.im_mores.setImageResource(R.drawable.arrows_up);
			} else {
				h.ll_mores.setVisibility(View.GONE);
				h.im_mores.setImageResource(R.drawable.arrows_down);
			}
			h.tv_name_sd.setText("" + shopOrder.get(position).productName);
			h.tv_time_sd.setText("" + shopOrder.get(position).createDate);
			h.tv_price_sd.setText("" + shopOrder.get(position).price);
			h.tv_count_sd.setText("" + shopOrder.get(position).count);
			h.tv_all_sd.setText("" + shopOrder.get(position).price * shopOrder.get(position).count + "元");
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + shopOrder.get(position).imgUrl, h.im_goods_sd,
					options);
			ImageLoader.getInstance().displayImage(shopOrder.get(position).codeUrl, h.im_cq_order, options);

			h.ll_showmores.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (openpositon.size() != 0 && openpositon.get(position) != null) {
						isOpen = openpositon.get(position);
					} else {
						isOpen = false;
					}
					if (isOpen) {
						openpositon.put(position, false);
						h.ll_mores.setVisibility(View.GONE);
						h.im_mores.setImageResource(R.drawable.arrows_down);

					} else {

						h.ll_mores.setVisibility(View.VISIBLE);
						openpositon.put(position, true);
						h.im_mores.setImageResource(R.drawable.arrows_up);
					}
				}
			});

			h.tv_surepay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Activity_Order.class);
					intent.putExtra("oderId", "" + shopOrder.get(position).orderId);
					intent.putExtra("title", shopOrder.get(position).productName);
					intent.putExtra("price", "" + (shopOrder.get(position).price * shopOrder.get(position).count));
					startActivity(intent);
				}
			});
			h.tv_arb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Activity_ReturnOrBack.class);
					intent.putExtra("orderId", shopOrder.get(position).orderId);
					startActivity(intent);

				}
			});
			h.tv_prise_sc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), Activity_Commends_Store.class);
					intent.putExtra("orderId", shopOrder.get(position).orderId);
					intent.putExtra("pic", shopOrder.get(position).imgUrl);
					intent.putExtra("name", shopOrder.get(position).productName);
					intent.putExtra("price", shopOrder.get(position).price);
					System.out.println("zuo==price=" + shopOrder.get(position).price);
					startActivity(intent);
				}
			});
			h.tv_suregetg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDialog(shopOrder.get(position).orderId);

				}
			});
			h.tv_looklogist_custom.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Activity_LookLogistics.class);
					intent.putExtra("orderId", shopOrder.get(position).orderId);
					intent.putExtra("type", 1);
					startActivity(intent);
				}
			});
			h.im_tochat.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("name",
							shopOrder.get(position).userNameY).putExtra("userId", "" + shopOrder.get(position).nameY));
				}
			});
			h.tv_notif.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					notifiSend(shopOrder.get(position).orderId);
				}
			});
			if (shopOrder.get(position).dicuss.equals("1") || shopOrder.get(position).status == 0
					|| shopOrder.get(position).status == 1 || shopOrder.get(position).status == 2) {
				h.tv_prise_sc.setVisibility(View.GONE);
			} else {
				h.tv_prise_sc.setVisibility(View.VISIBLE);
			}
			h.tv_cancleorder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDiaologcancel(shopOrder.get(position).orderId);
				}

			});
			h.im_cq_order.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ArrayList<String> data = new ArrayList<String>();
					data.clear();
					data.add(shopOrder.get(position).codeUrl);
					Intent intent = new Intent(getActivity(), Activity_LookPic.class);
					intent.putExtra("position", position);
					intent.putExtra("flag", 1);
					intent.putStringArrayListExtra("pics", data);
					startActivity(intent);
				}
			});
			h.tv_tuik.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					showDiaologBack(shopOrder.get(position).orderId);
				}
			});
			return v;
		}
	}

	class MyHolder {
		ImageView im_goods_sd, im_mores, im_tochat, im_cq_order;
		TextView tv_name_sd, tv_time_sd, tv_price_sd, tv_count_sd, tv_all_sd, tv_allfee_sd, tv_status_sd, tv_arb, tv_prise_sc,
				tv_suregetg, tv_looklogist_custom, tv_cancleorder, tv_surepay, tv_notif, tv_deletorder, tv_looklogist,
				tv_nub_lcs, tv_tuik;
		LinearLayout ll_showmores, ll_mores, ll_contain_stars_c;
	}

	private void showDiaologBack(final int orderId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示").setMessage("确认退款？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				backMoney(orderId);
				dialog.dismiss();
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.show();
	}

	private void backMoney(int orderId) {
		RequestParams params = new RequestParams();
		params.put("id", orderId);
		params.put("type", 1);
		params.put("token", MyApplication.token);
		showProgressDialog("申请中...");
		MyApplication.ahc.post(AppFinalUrl.userbackMoney, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				String result;
				try {
					result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(getActivity(), "申请成功", Toast.LENGTH_SHORT).show();
						shopOrder.clear();
						pageIndex = 1;
						getData(status, type, "", "", pageIndex, pageSize);
					} else {
						Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	private void showDiaologcancel(final int orderId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示").setMessage("确认取消订单？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancelorder(orderId);
				dialog.dismiss();
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.show();
	}

	private void cancelorder(int orderId) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("id", orderId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercancleOrder, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				String result;
				try {
					result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
						shopOrder.clear();
						getData(status, type, "", "", 1, pageSize);
					} else {
						Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	// 提醒商城发货
	private void notifiSend(int orderId) {
		RequestParams params = new RequestParams();
		params.put("orderId", orderId);
		params.put("type", 1);
		showProgressDialog("提交中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usernotifyShop, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(getActivity(), "提醒成功", Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void initParams() {
		SharedPreferences sp = getActivity().getSharedPreferences("Login", getActivity().MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
	}

	public void showDialog(final int orderId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示").setMessage("确认收货?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				sureGet(orderId);
				dialog.dismiss();
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.show();
	}

	private void sureGet(int orderId) {
		RequestParams params = new RequestParams();
		params.put("type", 1);
		params.put("orderId", orderId);
		showProgressDialog("确定中...");
		System.out.println("zuoparams===" + params);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usersureGetProduct, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuoparams=response==" + response.toString());
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(getActivity(), "收货成功", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		shopOrder.clear();
		getData(status, type, "", "", 1, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (isend) {
			handler.sendEmptyMessage(1);
			Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getData(status, type, "", "", pageIndex, pageSize);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(mbc);
	}
}
