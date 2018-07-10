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

import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_LookLogistics;
import com.femto.ugershop.activity.Activity_LookPic;
import com.femto.ugershop.activity.Activity_OderDetails;
import com.femto.ugershop.activity.Activity_Pro_design;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
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

import android.app.AlertDialog;
import android.app.Fragment;
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
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_CustomOrder extends BaseFragment implements OnClickListener, OnRefreshListener2<ListView> {
	private View view;
	private RadioButton rb_design, rb_product, rb_sended, rb_waitsend, rb_tradedone, rb_returngood;
	private PullToRefreshListView lv_customorder;
	private MyAdapter adapter;
	private DisplayImageOptions options;
	private int myId;
	private List<CustomOder> customOder;
	private Map<Integer, Boolean> openpositon;
	private boolean isOpen;
	private int hight = 1;
	private int status = 1;
	private int type = 0;
	private MyBroadCase mbr;
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
	private DecimalFormat df;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_customorder, container, false);
		initParams();
		mregisterReceiver();
		customOder = new ArrayList<CustomOder>();
		openpositon = new HashMap<Integer, Boolean>();
		initView(view);
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
		df = new DecimalFormat("######0.00");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rb_design:
			hight = 1;
			customOder.clear();
			pageIndex = 1;
			getData(status, type, "", "", hight, 1, pageSize);
			break;
		case R.id.rb_product:
			hight = 2;
			customOder.clear();
			pageIndex = 1;
			getData(status, type, "", "", hight, 1, pageSize);
			break;
		case R.id.rb_sended:
			status = 2;
			customOder.clear();
			pageIndex = 1;
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

		default:
			break;
		}
	}

	private void initView(View v) {
		lv_customorder = (PullToRefreshListView) v.findViewById(R.id.lv_customorder);
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
		lv_customorder.setOnRefreshListener(this);
		lv_customorder.setMode(Mode.BOTH);
		adapter = new MyAdapter();
		lv_customorder.setAdapter(adapter);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden && customOder.size() == 0) {
			getData(status, type, "", "", hight, 1, pageSize);
		}
		if (!hidden) {
			MobclickAgent.onResume(getActivity());
			setPageStart("定制订单");
		} else {
			MobclickAgent.onPause(getActivity());
			setPageEnd("定制订单");
		}
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
		System.out.println("zuo=sfsdvfsdvSV======" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMakeOrder, params, new JsonHttpResponseHandler() {
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
						for (int l = 0; l < optJSONArray2.length(); l++) {
							JSONObject jjj = optJSONArray2.getJSONObject(l);
							String url = jjj.optString("url");
							discussPhoto.add(new DiscussPhoto(url));
						}
						discussList.add(new DiscussList(content, discussDate, discussPhoto));

						customOder.add(new CustomOder(desinUrl, desinTwo, orderCode, desinThree, desinOne, desinOrderCode,
								userName, createDate, productName, userImg, isSupport, count, status, type, desinCount, scorce,
								price, userId, orderId, percentage, discussList, postCode));
						size++;
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
				v = View.inflate(getActivity(), R.layout.item_lv_customorder, null);
				h.im_head_cd = (CircleImageView) v.findViewById(R.id.im_head_cd);
				h.im_more = (ImageView) v.findViewById(R.id.im_more);
				h.tv_name_cd = (TextView) v.findViewById(R.id.tv_name_cd);
				h.tv_proname_cd = (TextView) v.findViewById(R.id.tv_proname_cd);
				h.tv_price_cd = (TextView) v.findViewById(R.id.tv_price_cd);
				h.tv_count_cd = (TextView) v.findViewById(R.id.tv_count_cd);
				h.tv_math_cd = (TextView) v.findViewById(R.id.tv_math_cd);
				h.tv_status_cd = (TextView) v.findViewById(R.id.tv_status_cd);
				h.tv_odernub = (TextView) v.findViewById(R.id.tv_odernub);
				h.tv_commends = (TextView) v.findViewById(R.id.tv_commends);
				h.tv_score = (TextView) v.findViewById(R.id.tv_score);
				h.tv_dorp = (TextView) v.findViewById(R.id.tv_dorp);
				h.ll_more = (LinearLayout) v.findViewById(R.id.ll_more);
				h.ll_contain_im = (LinearLayout) v.findViewById(R.id.ll_contain_im);
				h.ll_showmore = (LinearLayout) v.findViewById(R.id.ll_showmore);
				h.ll_contain_star = (LinearLayout) v.findViewById(R.id.ll_contain_star);
				h.rl_orderdetails = (RelativeLayout) v.findViewById(R.id.rl_orderdetails);
				h.rl_accepter = (RelativeLayout) v.findViewById(R.id.rl_accepter);
				h.rl_agreeapply = (RelativeLayout) v.findViewById(R.id.rl_agreeapply);
				h.rl_logist = (RelativeLayout) v.findViewById(R.id.rl_logist);
				h.rl_updesign = (RelativeLayout) v.findViewById(R.id.rl_updesign);
				h.ll_hascommends = (LinearLayout) v.findViewById(R.id.ll_hascommends);
				h.tv_details = (TextView) v.findViewById(R.id.tv_details);
				h.tv_count_deod = (TextView) v.findViewById(R.id.tv_count_deod);
				h.im_tochat_cd = (ImageView) v.findViewById(R.id.im_tochat_cd);
				h.rl_looklogist = (RelativeLayout) v.findViewById(R.id.rl_looklogist);
				h.tv_dzsj = (TextView) v.findViewById(R.id.tv_dzsj);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_count_deod.setText("X" + customOder.get(position).count);
			h.im_tochat_cd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("name",
							customOder.get(position).userName).putExtra("userId", "" + customOder.get(position).userId));
				}
			});
			if (customOder.get(position).discussList != null && customOder.get(position).discussList.get(0).content.length() != 0) {
				h.ll_hascommends.setVisibility(View.VISIBLE);
			} else {
				h.ll_hascommends.setVisibility(View.GONE);
			}
			h.rl_orderdetails.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (hight == 2) {
						// 生产
						Intent intent = new Intent(getActivity(), Activity_Pro_design.class);
						intent.putExtra("makeId", customOder.get(position).orderId);
						intent.putExtra("name", "定制设计:" + customOder.get(position).productName);
						intent.putExtra("hight", hight);
						startActivity(intent);
						System.out.println("zuoActivity_Pro_design");
					} else {
						// 设计
						System.out.println("zuoActivity_OderDetails");
						Intent intent = new Intent(getActivity(), Activity_OderDetails.class);
						intent.putExtra("makeId", customOder.get(position).orderId);
						intent.putExtra("name", "定制设计:" + customOder.get(position).productName);
						intent.putExtra("hight", hight);
						startActivity(intent);
					}

				}
			});
			h.rl_updesign.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Activity_OderDetails.class);
					intent.putExtra("makeId", customOder.get(position).orderId);
					intent.putExtra("name", "定制设计:" + customOder.get(position).productName);
					intent.putExtra("hight", hight);
					startActivity(intent);
				}
			});
			if (hight == 1) {
				h.tv_dorp.setText("设计");
				h.tv_dzsj.setText("定制设计:");
			} else {
				h.tv_dorp.setText("生产");
				h.tv_dzsj.setText("定制生产:");
			}
			if (customOder.get(position).status == 0) {
				h.tv_status_cd.setText("待付款");
			}
			if (customOder.get(position).status == 1) {
				h.tv_status_cd.setText("待发货");
			}
			if (customOder.get(position).status == 2) {
				h.tv_status_cd.setText("已发货");
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

			if (customOder.get(position).status == 5) {
				h.rl_agreeapply.setVisibility(View.VISIBLE);
			} else {
				h.rl_agreeapply.setVisibility(View.GONE);
			}
			if (hight == 1) {

				h.rl_logist.setVisibility(View.GONE);
				if (customOder.get(position).status == 1) {
					h.rl_orderdetails.setVisibility(View.GONE);
					h.rl_updesign.setVisibility(View.VISIBLE);
				} else {
					h.rl_orderdetails.setVisibility(View.VISIBLE);
					h.rl_updesign.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 7 || customOder.get(position).status == 9
						|| customOder.get(position).status == 11) {
					h.tv_details.setText("立即修改");
				} else {
					h.tv_details.setText("详情");
				}
				if (customOder.get(position).status == 5 && customOder.get(position).status != 15) {
					h.rl_agreeapply.setVisibility(View.VISIBLE);
				} else {
					h.rl_agreeapply.setVisibility(View.GONE);
				}

			} else {
				System.out.println("zuo=hight==" + hight);
				h.rl_orderdetails.setVisibility(View.VISIBLE);
				h.rl_updesign.setVisibility(View.GONE);
				h.tv_details.setText("详情");
				if (customOder.get(position).status == 1) {
					// h.rl_logist.setVisibility(View.VISIBLE);
					h.tv_details.setText("立即发货");
				} else {
					// h.rl_logist.setVisibility(View.GONE);
					h.tv_details.setText("详情");
				}
				if (customOder.get(position).status == 5) {
					h.rl_agreeapply.setVisibility(View.VISIBLE);

				} else {
					h.rl_agreeapply.setVisibility(View.GONE);
				}
				if (customOder.get(position).status == 0 || customOder.get(position).status == 1) {
					h.rl_looklogist.setVisibility(View.GONE);
				} else {
					h.rl_looklogist.setVisibility(View.VISIBLE);
				}
				if (customOder.get(position).status == 15) {
					// h.rl_logist.setVisibility(View.VISIBLE);
					h.tv_details.setText("立即换货");
				} else {
					// h.rl_logist.setVisibility(View.GONE);
					h.tv_details.setText("详情");
				}
			}

			h.tv_name_cd.setText("" + customOder.get(position).userName);
			h.tv_count_cd.setText("" + customOder.get(position).count);
			h.tv_price_cd.setText("" + customOder.get(position).price);
			h.tv_proname_cd.setText("" + customOder.get(position).productName);
			h.tv_commends.setText("" + customOder.get(position).discussList.get(0).content);
			h.tv_odernub.setText("" + customOder.get(position).orderCode);
			h.tv_score.setText("" + customOder.get(position).scorce + "分");
			h.tv_math_cd.setText(""
					+ customOder.get(position).price
					* customOder.get(position).count
					+ "*"
					+ (customOder.get(position).percentage)
					* 100
					+ "%="
					+ df.format(customOder.get(position).price * customOder.get(position).count
							* customOder.get(position).percentage) + "元");
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
				View vvv = View.inflate(getActivity(), R.layout.item_image, null);
				ImageView im = (ImageView) vvv.findViewById(R.id.im_commends);
				pics.add(customOder.get(position).discussList.get(0).discussPhoto.get(i).url);
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
			h.rl_agreeapply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					agreeTuikuan(customOder.get(position).orderId);
				}

			});
			h.rl_looklogist.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Activity_LookLogistics.class);
					intent.putExtra("orderId", customOder.get(position).orderId);
					intent.putExtra("type", 2);
					intent.putExtra("postCode", customOder.get(position).postCode);
					startActivity(intent);
				}
			});
			return v;
		}
	}

	class MyHolder {
		CircleImageView im_head_cd;
		ImageView im_more, im_tochat_cd;
		RelativeLayout rl_orderdetails, rl_accepter, rl_agreeapply, rl_logist, rl_updesign, rl_looklogist;
		LinearLayout ll_more, ll_showmore, ll_contain_star, ll_contain_im, ll_hascommends;
		TextView tv_name_cd, tv_proname_cd, tv_price_cd, tv_count_cd, tv_math_cd, tv_status_cd, tv_odernub, tv_commends,
				tv_score, tv_dorp, tv_details, tv_count_deod, tv_dzsj;
	}

	private void agreeTuikuan(final int orderId) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示").setMessage("确认同意退款?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				tongyi(orderId);
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.show();
	}

	private void tongyi(int orderId) {
		RequestParams params = new RequestParams();
		params.put("makeId", orderId);
		showProgressDialog("确定中...");
		System.out.println("zuo" + "   orderId= " + orderId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userdesinSureBackMoney, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				String result;
				dismissProgressDialog();
				System.out.println("zuo" + response.toString());
				try {
					result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(getActivity(), "已同意", Toast.LENGTH_SHORT).show();
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

	public void showorhint(RadioButton rbshow, RadioButton rb1, RadioButton rb2, RadioButton rb3) {
		rbshow.setChecked(true);
		rb1.setChecked(false);
		rb2.setChecked(false);
		rb3.setChecked(false);
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	class MyBroadCase extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.search.customoder.info")) {
				starttime = intent.getStringExtra("starttime");
				endtime = intent.getStringExtra("endtime");
				type = intent.getIntExtra("type", 0);
				customOder.clear();
				getData(status, type, starttime, endtime, hight, 1, pageSize);
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
			getData(status, type, starttime, endtime, hight, pageIndex, pageSize);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(mbr);
	}
}
