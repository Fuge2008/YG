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
import android.app.AlertDialog.Builder;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_CommendsCustom;
import com.femto.ugershop.activity.Activity_Commends_Store;
import com.femto.ugershop.activity.Activity_LookLogistics;
import com.femto.ugershop.activity.Activity_LookPic;
import com.femto.ugershop.activity.Activity_OderDetails;
import com.femto.ugershop.activity.Activity_Order;
import com.femto.ugershop.activity.Activity_PostDetails;
import com.femto.ugershop.activity.Activity_Pro_Custom;
import com.femto.ugershop.activity.Activity_ReturnOrBack;
import com.femto.ugershop.activity.Activity_SelectPay;
import com.femto.ugershop.activity.Activity_oderdetails_custom;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.fragment.Fragment_CustomOrder.CustomOder;
import com.femto.ugershop.fragment.Fragment_CustomOrder.DiscussList;
import com.femto.ugershop.fragment.Fragment_CustomOrder.DiscussPhoto;
import com.femto.ugershop.fragment.Fragment_CustomOrder.MyAdapter;
import com.femto.ugershop.fragment.Fragment_CustomOrder.MyHolder;
import com.femto.ugershop.view.CircleImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class Fragment_CustomOrderCustom extends BaseFragment implements OnClickListener, OnRefreshListener2<ListView> {
	private View view;
	private RadioButton rb_design, rb_product, rb_sended, rb_waitsend, rb_tradedone, rb_returngood;
	private int myId;
	private DisplayImageOptions options;
	private List<CustomOder> customOder;
	private Map<Integer, Boolean> openpositon;
	private MyAdapter adapter;
	private boolean isOpen;
	private int hight = 1;
	private int status = 1;
	private int type = 0;
	private PullToRefreshListView lv_customorder;
	private MyBC mybc;
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
				lv_customorder.onRefreshComplete();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_customorder, container, false);
		initParams();
		registbc();
		customOder = new ArrayList<CustomOder>();
		openpositon = new HashMap<Integer, Boolean>();
		initView(view);
		return view;
	}

	private void registbc() {
		mybc = new MyBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.search.customodercustom.info");
		getActivity().registerReceiver(mybc, filter);
	}

	class MyBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.search.customodercustom.info")) {
				customOder.clear();
				starttime = intent.getStringExtra("starttime");
				endtime = intent.getStringExtra("endtime");
				type = intent.getIntExtra("type", 0);
				getData(status, type, starttime, endtime, hight, 1, pageSize);
			}

		}

	}

	private void initView(View v) {
		rb_design = (RadioButton) v.findViewById(R.id.rb_design);
		rb_product = (RadioButton) v.findViewById(R.id.rb_product);
		rb_sended = (RadioButton) v.findViewById(R.id.rb_sended);
		rb_waitsend = (RadioButton) v.findViewById(R.id.rb_waitsend);
		rb_tradedone = (RadioButton) v.findViewById(R.id.rb_tradedone);
		rb_returngood = (RadioButton) v.findViewById(R.id.rb_returngood);
		rb_design.setOnClickListener(this);
		rb_product.setOnClickListener(this);
		rb_sended.setOnClickListener(this);
		rb_waitsend.setOnClickListener(this);
		rb_tradedone.setOnClickListener(this);
		rb_returngood.setOnClickListener(this);

		lv_customorder = (PullToRefreshListView) v.findViewById(R.id.lv_customorder);
		lv_customorder.setOnRefreshListener(this);
		lv_customorder.setMode(Mode.BOTH);
		adapter = new MyAdapter();
		lv_customorder.setAdapter(adapter);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_design:
			hight = 1;
			pageIndex = 1;
			customOder.clear();
			getData(status, type, "", "", hight, 1, pageSize);
			break;
		case R.id.rb_product:
			hight = 2;
			pageIndex = 1;
			customOder.clear();
			getData(status, type, "", "", hight, 1, pageSize);
			break;
		case R.id.rb_sended:
			status = 2;
			pageIndex = 1;
			customOder.clear();
			getData(status, type, "", "", hight, 1, pageSize);
			showorhint(rb_sended, rb_waitsend, rb_tradedone, rb_returngood);
			break;
		case R.id.rb_waitsend:
			status = 1;
			customOder.clear();
			pageIndex = 1;
			getData(status, type, "", "", hight, 1, pageSize);
			showorhint(rb_waitsend, rb_sended, rb_tradedone, rb_returngood);
			break;
		case R.id.rb_tradedone:
			status = 3;
			customOder.clear();
			pageIndex = 1;
			getData(status, type, "", "", hight, 1, pageSize);
			showorhint(rb_tradedone, rb_sended, rb_waitsend, rb_returngood);
			break;
		case R.id.rb_returngood:
			status = 4;
			customOder.clear();
			pageIndex = 1;
			getData(status, type, "", "", hight, 1, pageSize);
			showorhint(rb_returngood, rb_sended, rb_waitsend, rb_tradedone);
			break;
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden && customOder.size() == 0) {
			getData(1, type, "", "", hight, 1, pageSize);
		}
		if (!hidden) {
			MobclickAgent.onResume(getActivity());
			setPageStart("定制订单");
		} else {
			MobclickAgent.onPause(getActivity());
			setPageEnd("定制订单");
		}
	}

	public void showorhint(RadioButton rbshow, RadioButton rb1, RadioButton rb2, RadioButton rb3) {
		rbshow.setChecked(true);
		rb1.setChecked(false);
		rb2.setChecked(false);
		rb3.setChecked(false);
	}

	private void getData(int status, int type, String starttime, String endtime, int high, int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		params.put("user.id", myId);
		params.put("user.type", type);
		if (type == 1) {
			params.put("user.createDate", starttime);
			params.put("user.lastLoginDate", endtime);
		}
		params.put("user.status", status);
		params.put("user.high", high);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		showProgressDialog("加载中...");
		System.out.println("zuo==" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetUserMakeOrder, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo==" + response.toString());
				dismissProgressDialog();

				try {
					JSONArray jsonArray = response.getJSONArray("List");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String desinUrl = j.optString("desinUrl");
						String desinTwo = j.optString("desinTwo");
						String orderCode = j.optString("orderCode");
						String desinThree = j.optString("desinThree");
						String desinOne = j.optString("desinOne");
						String desinOrderCode = j.optString("desinOrderCode");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String productName = j.optString("productName");
						String userImg = j.optString("userImg");
						String isSupport = j.optString("isSupport");
						String postCode = j.optString("postCode");
						int count = j.optInt("count");
						int status = j.optInt("status");
						int type = j.optInt("type");
						int desinCount = j.optInt("desinCount");
						int scorce = j.optInt("scorce");
						int price = j.optInt("price");
						int userId = j.optInt("userId");
						int orderId = j.optInt("orderId");
						double percentage = j.optDouble("percentage");

						List<DiscussList> discussList = new ArrayList<DiscussList>();
						JSONObject jj = j.getJSONObject("discussList");
						String content = jj.optString("content");
						String discussDate = jj.optString("discussDate");
						List<DiscussPhoto> discussPhoto = new ArrayList<DiscussPhoto>();

						JSONArray optJSONArray2 = jj.getJSONArray("discussPhoto");
						if (optJSONArray2 != null) {
							for (int l = 0; l < optJSONArray2.length(); l++) {
								JSONObject jjj = optJSONArray2.getJSONObject(l);
								String url = jjj.optString("url");
								discussPhoto.add(new DiscussPhoto(url));
							}
						}

						discussList.add(new DiscussList(content, discussDate, discussPhoto));
						size++;
						customOder.add(new CustomOder(desinUrl, desinTwo, orderCode, desinThree, desinOne, desinOrderCode,
								userName, createDate, productName, userImg, isSupport, count, status, type, desinCount, scorce,
								price, userId, orderId, percentage, discussList, postCode));
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					lv_customorder.onRefreshComplete();
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class CustomOder {
		String desinUrl, desinTwo, orderCode, desinThree, desinOne, desinOrderCode, userName, createDate, productName, userImg,
				isSupport, postCode;
		int count, status, type, desinCount, scorce, price, userId, orderId;
		double percentage;
		List<DiscussList> discussList;

		public CustomOder(String desinUrl, String desinTwo, String orderCode, String desinThree, String desinOne,
				String desinOrderCode, String userName, String createDate, String productName, String userImg, String isSupport,
				int count, int status, int type, int desinCount, int scorce, int price, int userId, int orderId,
				double percentage, List<DiscussList> discussList, String postCode) {
			super();
			this.desinUrl = desinUrl;
			this.desinTwo = desinTwo;
			this.orderCode = orderCode;
			this.desinThree = desinThree;
			this.desinOne = desinOne;
			this.desinOrderCode = desinOrderCode;
			this.userName = userName;
			this.createDate = createDate;
			this.productName = productName;
			this.userImg = userImg;
			this.isSupport = isSupport;
			this.count = count;
			this.status = status;
			this.type = type;
			this.desinCount = desinCount;
			this.scorce = scorce;
			this.price = price;
			this.userId = userId;
			this.orderId = orderId;
			this.percentage = percentage;
			this.discussList = discussList;
			this.postCode = postCode;
		}
	}

	class DiscussList {
		String content, discussDate;
		List<DiscussPhoto> discussPhoto;

		public DiscussList(String content, String discussDate, List<DiscussPhoto> discussPhoto) {
			super();
			this.content = content;
			this.discussDate = discussDate;
			this.discussPhoto = discussPhoto;
		}
	}

	class DiscussPhoto {
		String url;

		public DiscussPhoto(String url) {
			super();
			this.url = url;
		}

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return customOder == null ? 0 : customOder.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return customOder.get(position);
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
				v = View.inflate(getActivity(), R.layout.item_lv_customcustom, null);
				h.im_head_cd = (CircleImageView) v.findViewById(R.id.im_head_cd_c);
				h.im_more = (ImageView) v.findViewById(R.id.im_more_c);
				h.tv_name_cd = (TextView) v.findViewById(R.id.tv_name_cd_c);
				h.tv_proname_cd = (TextView) v.findViewById(R.id.tv_proname_cd_c);
				h.tv_price_cd = (TextView) v.findViewById(R.id.tv_price_cd_c);
				h.tv_count_cd = (TextView) v.findViewById(R.id.tv_count_cd_c);
				h.tv_math_cd = (TextView) v.findViewById(R.id.tv_math_cd_c);
				h.tv_status_cd = (TextView) v.findViewById(R.id.tv_status_cd_c);
				h.tv_odernub = (TextView) v.findViewById(R.id.tv_odernub_c);
				h.tv_commends = (TextView) v.findViewById(R.id.tv_commends_c);
				h.tv_score = (TextView) v.findViewById(R.id.tv_score_c);
				h.tv_dorp = (TextView) v.findViewById(R.id.tv_dorp_c);
				h.tv_prise_sc_c = (TextView) v.findViewById(R.id.tv_prise_sc_c);
				h.tv_arb_c = (TextView) v.findViewById(R.id.tv_arb_c);
				h.tv_suregetgoods = (TextView) v.findViewById(R.id.tv_suregetgoods);
				h.ll_more = (LinearLayout) v.findViewById(R.id.ll_more_c);
				h.ll_contain_im = (LinearLayout) v.findViewById(R.id.ll_contain_im_c);
				h.ll_showmore = (LinearLayout) v.findViewById(R.id.ll_showmore_c);
				h.ll_contain_star = (LinearLayout) v.findViewById(R.id.ll_contain_star_c);
				h.rl_orderdetails_c = (RelativeLayout) v.findViewById(R.id.rl_orderdetails_c);
				h.tv_applymake = (TextView) v.findViewById(R.id.tv_applymake);
				h.tv_deleorder_c = (TextView) v.findViewById(R.id.tv_deleorder_c);
				h.tv_pay_c = (TextView) v.findViewById(R.id.tv_pay_c);
				h.tv_looklogist_c = (TextView) v.findViewById(R.id.tv_looklogist_c);
				h.tv_applychange_c = (TextView) v.findViewById(R.id.tv_applychange_c);
				h.im_tochat_cc = (ImageView) v.findViewById(R.id.im_tochat_cc);
				h.ll_iscommends = (LinearLayout) v.findViewById(R.id.ll_iscommends);
				h.tv_ticheng = (TextView) v.findViewById(R.id.tv_ticheng);
				h.tv_count_cuoder = (TextView) v.findViewById(R.id.tv_count_cuoder);
				h.tv_dzsj_c = (TextView) v.findViewById(R.id.tv_dzsj_c);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_ticheng.setText("合计:");
			h.tv_count_cuoder.setText("X" + customOder.get(position).count);
			if (customOder.get(position).discussList.get(0).content.length() != 0 && status != 0) {
				h.ll_iscommends.setVisibility(View.VISIBLE);
				h.tv_prise_sc_c.setVisibility(View.VISIBLE);
			} else {
				h.ll_iscommends.setVisibility(View.GONE);
				h.tv_prise_sc_c.setVisibility(View.GONE);
			}
			h.rl_orderdetails_c.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (hight == 2) {
						// 生产
						Intent intent = new Intent(getActivity(), Activity_Pro_Custom.class);
						intent.putExtra("makeId", customOder.get(position).orderId);
						intent.putExtra("name", customOder.get(position).productName);
						intent.putExtra("hight", hight);
						startActivity(intent);
					} else {
						// 设计
						Intent intent = new Intent(getActivity(), Activity_oderdetails_custom.class);
						intent.putExtra("makeId", customOder.get(position).orderId);
						intent.putExtra("name", customOder.get(position).productName);
						intent.putExtra("hight", hight);
						startActivity(intent);
					}

				}
			});
			h.tv_math_cd.setText("" + customOder.get(position).price * customOder.get(position).count + "元");
			if (hight == 1) {
				h.tv_dorp.setText("设计");
				h.tv_dzsj_c.setText("定制设计:");
			} else {
				h.tv_dorp.setText("生产");
				h.tv_dzsj_c.setText("定制生产:");
			}
			if (customOder.get(position).status == 0) {
				h.tv_status_cd.setText("待付款");
			}

			if (customOder.get(position).status == 1) {
				h.tv_status_cd.setText("待发货");
			}
			if (customOder.get(position).status == 2) {
				h.tv_status_cd.setText("待收货");
			}
			if (customOder.get(position).status == 3) {
				h.tv_status_cd.setText("交易完成");

			}
			if (customOder.get(position).status == 4) {
				h.tv_status_cd.setText("交易关闭");
			}
			if (customOder.get(position).status == 5) {
				h.tv_status_cd.setText("申请退款中");
			}
			if (customOder.get(position).status == 6) {
				h.tv_status_cd.setText("退款成功");
			}
			if (customOder.get(position).status == 7) {
				h.tv_status_cd.setText("申请第一次修改");
			}
			if (customOder.get(position).status == 8) {
				h.tv_status_cd.setText("第一次修改完成");
			}
			if (customOder.get(position).status == 9) {
				h.tv_status_cd.setText("申请第二次修改");
			}
			if (customOder.get(position).status == 10) {
				h.tv_status_cd.setText("申请第二次修改完成");
			}
			if (customOder.get(position).status == 11) {
				h.tv_status_cd.setText("申请第三次修改");
			}
			if (customOder.get(position).status == 12) {
				h.tv_status_cd.setText("申请第三次修改完成");
			}
			if (customOder.get(position).status == 13) {
				h.tv_status_cd.setText("申请退款中");
			}

			if (customOder.get(position).status == 14) {
				h.tv_status_cd.setText("退款中");
			}
			if (customOder.get(position).status == 15) {
				h.tv_status_cd.setText("换货中");
			}
			if (customOder.get(position).status == 16) {
				h.tv_status_cd.setText("换货成功");
			}

			if (hight == 1) {
				h.tv_looklogist_c.setVisibility(View.GONE);
				h.tv_arb_c.setText("申请退款");

				if (customOder.get(position).status == 0) {
					h.tv_pay_c.setVisibility(View.VISIBLE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_deleorder_c.setVisibility(View.VISIBLE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.rl_orderdetails_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 1) {
					h.tv_arb_c.setVisibility(View.VISIBLE);
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
					h.rl_orderdetails_c.setVisibility(View.GONE);
				} else {
					h.rl_orderdetails_c.setVisibility(View.VISIBLE);
				}
				if (customOder.get(position).status == 2) {
					h.tv_arb_c.setVisibility(View.VISIBLE);
					h.tv_suregetgoods.setVisibility(View.VISIBLE);
					h.tv_prise_sc_c.setVisibility(View.VISIBLE);
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 3) {
					h.tv_prise_sc_c.setVisibility(View.VISIBLE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 4) {
					h.tv_deleorder_c.setVisibility(View.VISIBLE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);

				}
				if (customOder.get(position).status == 5) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 6) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 7) {
					h.tv_arb_c.setVisibility(View.VISIBLE);
					h.tv_pay_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.VISIBLE);
					h.tv_prise_sc_c.setVisibility(View.VISIBLE);
					h.tv_pay_c.setVisibility(View.GONE);
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 8) {
					h.tv_arb_c.setVisibility(View.VISIBLE);
					h.tv_suregetgoods.setVisibility(View.VISIBLE);
					h.tv_prise_sc_c.setVisibility(View.VISIBLE);
					h.tv_pay_c.setVisibility(View.GONE);
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 9) {
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.VISIBLE);
					h.tv_prise_sc_c.setVisibility(View.VISIBLE);
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 10) {
					h.tv_arb_c.setVisibility(View.VISIBLE);
					h.tv_pay_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.VISIBLE);
					h.tv_prise_sc_c.setVisibility(View.VISIBLE);
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 11) {
					h.tv_arb_c.setVisibility(View.VISIBLE);
					h.tv_pay_c.setVisibility(View.GONE);
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 12) {
					h.tv_arb_c.setVisibility(View.VISIBLE);
					h.tv_suregetgoods.setVisibility(View.VISIBLE);
					h.tv_prise_sc_c.setVisibility(View.VISIBLE);
					h.tv_pay_c.setVisibility(View.GONE);
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 13) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 14) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 15) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
			} else {
				if (customOder.get(position).status == 0) {
					h.tv_pay_c.setVisibility(View.VISIBLE);
					h.tv_deleorder_c.setVisibility(View.VISIBLE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 1) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 2) {
					h.tv_arb_c.setVisibility(View.VISIBLE);
					h.tv_suregetgoods.setVisibility(View.VISIBLE);
					h.tv_prise_sc_c.setVisibility(View.VISIBLE);
					h.tv_looklogist_c.setVisibility(View.VISIBLE);

					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 3) {
					h.tv_prise_sc_c.setVisibility(View.VISIBLE);
					h.tv_deleorder_c.setVisibility(View.GONE);

					h.tv_applymake.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.VISIBLE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 4) {
					h.tv_deleorder_c.setVisibility(View.VISIBLE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 5) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 6) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 7) {
					h.tv_pay_c.setVisibility(View.GONE);

					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 8) {
					h.tv_pay_c.setVisibility(View.GONE);

					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 9) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.GONE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 13) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.VISIBLE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 14) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.VISIBLE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 15) {
					h.tv_deleorder_c.setVisibility(View.GONE);
					h.tv_applymake.setVisibility(View.GONE);
					h.tv_prise_sc_c.setVisibility(View.GONE);
					h.tv_suregetgoods.setVisibility(View.GONE);
					h.tv_arb_c.setVisibility(View.GONE);
					h.tv_applychange_c.setVisibility(View.GONE);
					h.tv_looklogist_c.setVisibility(View.VISIBLE);
					h.tv_pay_c.setVisibility(View.GONE);
				}
			}

			h.tv_name_cd.setText("" + customOder.get(position).userName);
			h.tv_count_cd.setText("" + customOder.get(position).count);
			h.tv_price_cd.setText("" + customOder.get(position).price + "元");
			h.tv_proname_cd.setText("" + customOder.get(position).productName);
			h.tv_commends.setText("" + customOder.get(position).discussList.get(0).content);
			h.tv_odernub.setText("" + customOder.get(position).orderCode);
			h.tv_score.setText("" + customOder.get(position).scorce + "分");
			h.ll_contain_star.removeAllViews();
			for (int i = 0; i < customOder.get(position).scorce; i++) {
				View vv = View.inflate(getActivity(), R.layout.image_star, null);
				h.ll_contain_star.addView(vv);
			}
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + customOder.get(position).userImg, h.im_head_cd,
					options);
			// if (openpositon.size() != 0 && openpositon.get(position) != null
			// && openpositon.get(position)) {
			// h.ll_more.setVisibility(View.VISIBLE);
			// h.im_more.setImageResource(R.drawable.arrows_up);
			// } else {
			// h.ll_more.setVisibility(View.GONE);
			// h.im_more.setImageResource(R.drawable.arrows_down);
			// }
			// h.ll_showmore.setOnClickListener(new OnClickListener() {
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
			// h.ll_more.setVisibility(View.GONE);
			// h.im_more.setImageResource(R.drawable.arrows_down);
			//
			// } else {
			//
			// h.ll_more.setVisibility(View.VISIBLE);
			// openpositon.put(position, true);
			// h.im_more.setImageResource(R.drawable.arrows_up);
			// }
			// }
			// });
			h.ll_showmore.setVisibility(View.GONE);
			h.ll_contain_im.removeAllViews();
			final ArrayList<String> pics = new ArrayList<String>();

			for (int i = 0; i < customOder.get(position).discussList.get(0).discussPhoto.size(); i++) {
				pics.add(customOder.get(position).discussList.get(0).discussPhoto.get(i).url);
				View vvv = View.inflate(getActivity(), R.layout.item_image, null);
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
						AppFinalUrl.BASEURL + customOder.get(position).discussList.get(0).discussPhoto.get(i).url, im, options);
				h.ll_contain_im.addView(vvv);
			}
			h.tv_arb_c.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (hight == 1) {
						showTuiKuang(customOder.get(position).orderId);
					} else {
						Intent intent = new Intent(getActivity(), Activity_ReturnOrBack.class);
						intent.putExtra("orderId", customOder.get(position).orderId);
						intent.putExtra("type", 1);
						intent.putExtra("postCode", customOder.get(position).postCode);
						startActivity(intent);
					}

				}

			});
			h.tv_prise_sc_c.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Activity_CommendsCustom.class);
					intent.putExtra("orderId", customOder.get(position).orderId);
					startActivity(intent);
				}
			});
			h.tv_suregetgoods.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDialog(customOder.get(position).orderId);
				}
			});
			h.im_tochat_cc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("name",
							customOder.get(position).userName).putExtra("userId", "" + customOder.get(position).userId));
				}
			});
			h.tv_looklogist_c.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Activity_LookLogistics.class);
					intent.putExtra("orderId", customOder.get(position).orderId);
					intent.putExtra("type", 2);
					startActivity(intent);
				}
			});

			// if (customOder.get(position).discussList.get(0).content.length()
			// != 0 && status != 0) {
			// h.ll_iscommends.setVisibility(View.VISIBLE);
			// h.tv_prise_sc_c.setVisibility(View.VISIBLE);
			// } else {
			// h.ll_iscommends.setVisibility(View.GONE);
			// h.tv_prise_sc_c.setVisibility(View.GONE);
			// }
			if (customOder.get(position).discussList.get(0).content.length() != 0) {
				h.ll_iscommends.setVisibility(View.VISIBLE);
				h.tv_prise_sc_c.setVisibility(View.GONE);
			} else {
				h.ll_iscommends.setVisibility(View.GONE);
				if (customOder.get(position).status == 0 || customOder.get(position).status == 15
						|| customOder.get(position).status == 6 || customOder.get(position).status == 1) {
					h.tv_prise_sc_c.setVisibility(View.GONE);
				} else {
					h.tv_prise_sc_c.setVisibility(View.VISIBLE);
				}

			}
			h.tv_pay_c.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (hight == 1) {
						showDialogPay(customOder.get(position).orderCode,
								"" + customOder.get(position).price * customOder.get(position).count,
								customOder.get(position).productName);
					} else {
						Intent intent = new Intent(getActivity(), Activity_Order.class);
						intent.putExtra("oderId", "" + customOder.get(position).orderId);
						intent.putExtra("title", customOder.get(position).productName);
						intent.putExtra("type", 3);
						intent.putExtra("image", customOder.get(position).userImg);

						intent.putExtra("price", "" + (customOder.get(position).price * customOder.get(position).count));
						startActivity(intent);
					}

				}

			});
			h.tv_deleorder_c.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleorderDialog(customOder.get(position).orderId);
				}
			});
			System.out.println("zuo---status=" + status);
			return v;
		}
	}

	private void deleorderDialog(final int orderId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提醒!").setMessage("您确定删除此订单？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				deleorder(orderId);
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.show();
	}

	private void deleorder(int orderId) {
		RequestParams params = new RequestParams();
		params.put("id", orderId);
		showProgressDialog("删除中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercancleMakeOrder, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				String result;
				dismissProgressDialog();
				try {
					result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
						customOder.clear();
						getData(status, type, "", "", hight, 1, pageSize);
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

	private void showDialogPay(final String orderCode, final String price, final String productname) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("付款确认！").setMessage("您需要支付" + price + "元!")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
						Intent intent = new Intent(getActivity(), Activity_SelectPay.class);
						intent.putExtra("orderCode", orderCode);
						intent.putExtra("price", "" + price);
						intent.putExtra("productname", productname);
						startActivity(intent);
					}

				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
		builder.show();
	}

	class MyHolder {
		CircleImageView im_head_cd;
		RelativeLayout rl_orderdetails_c;
		ImageView im_more, im_tochat_cc;
		LinearLayout ll_more, ll_showmore, ll_contain_star, ll_contain_im, ll_iscommends;
		TextView tv_name_cd, tv_proname_cd, tv_price_cd, tv_count_cd, tv_math_cd, tv_status_cd, tv_odernub, tv_commends,
				tv_score, tv_dorp, tv_arb_c, tv_prise_sc_c, tv_suregetgoods, tv_applymake, tv_deleorder_c, tv_pay_c,
				tv_looklogist_c, tv_applychange_c, tv_ticheng, tv_count_cuoder, tv_dzsj_c;

	}

	private void showTuiKuang(final int orderId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示").setMessage("确认申请退款?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				sureTuik(orderId);
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.show();
	}

	private void sureTuik(int orderId) {
		RequestParams params = new RequestParams();
		params.put("orderId", orderId);
		params.put("type", 1);
		System.out.println("zuo===orderId=" + orderId);
		showProgressDialog("确定中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userchangeMakeProduct, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				String result;
				dismissProgressDialog();
				try {
					result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(getActivity(), "申请成功", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	private void sureGet(int orderId) {
		RequestParams params = new RequestParams();
		params.put("orderId", orderId);
		params.put("type", 2);
		showProgressDialog("确定中...");
		System.out.println("zuo==params=" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usersureGetProduct, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(getActivity(), "收货成功", Toast.LENGTH_SHORT).show();
						customOder.clear();
						getData(status, type, "", "", hight, 1, pageSize);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void showDialog(final int orderId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示").setMessage("确认收货?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				sureGet(orderId);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.show();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		customOder.clear();
		getData(status, type, "", "", hight, 1, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			handler.sendEmptyMessage(1);
			Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getData(status, type, starttime, endtime, hight, 1, pageSize);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(mybc);
	}
}
