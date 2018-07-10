package com.femto.ugershop.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.easemob.chat.core.ac;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_ActivityMain;
import com.femto.ugershop.activity.Activity_Custom_Rule;
import com.femto.ugershop.activity.Activity_Designer;
import com.femto.ugershop.activity.Activity_LookPic;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.PhotoList;
import com.femto.ugershop.fragment.Fragment_NewGoods.BannerList;
import com.femto.ugershop.view.CircleImageView;
import com.femto.ugershop.view.HorizontalListView;
import com.femto.ugershop.view.MyGridView;
import com.femto.ugershop.view.ParentListView;
import com.femto.ugershop.view.ScrollViewWithListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_Customization extends BaseFragment implements OnPageChangeListener, OnClickListener, OnItemClickListener,
		OnRefreshListener2<ScrollView>, OnScrollListener {
	private View view;
	private ViewPager vp_hot_cus;
	private List<View> vpData;
	private View cview;
	private LinearLayout dots_group_cus, ll_contain_hsll;
	private MyVPAdapter vpadapter;
	private ListView sllv_hot_cus;
	private MyLVAdapter lvadapter;
	private PopupWindow ppw_price;
	private View customView;
	private List<BannerList> bannerList;
	private DisplayImageOptions options;
	private List<MakeList> makeList;
	private RadioButton rb_inland_cus, rb_japan_cus, rb_rank_cus, rb_hotd_cus;
	private ProgressDialog pdd;
	private List<LableSort> lableSort;
	private HorizontalListView holv;
	private TextView tv_custom_rule;
	private MyHOLVAdapter holvadapter;
	private Timer timer = new Timer();
	private int p = 0;
	int i = 1;
	private boolean isfirsttovp = true;
	int totalVp = 3;
	int size = 0;

	private View headview;
	private int currposition = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private int type = 0;
	private int fromprice = 0;
	private int endprice = 0;
	private String currsortName;
	private MyGridView gv_fde;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (vpData.size() != 0) {
				int j = i % vpData.size();
				vp_hot_cus.setCurrentItem(j);
				i++;
			}

		};
	};
	private int w;
	private boolean isloading;
	private int screenWidth;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_customization, container, false);
		vpData = new ArrayList<View>();
		bannerList = new ArrayList<BannerList>();
		makeList = new ArrayList<MakeList>();
		lableSort = new ArrayList<LableSort>();
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(getActivity(), 4);
		w = (screenWidth - dp2px) / 2;
		pdd = new ProgressDialog(getActivity());
		pdd.setMessage("加载中");
		initView(view);
		gatLableSort();
		return view;
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void gatLableSort() {
		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetLables, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);

				try {
					JSONArray jsonArray = response.getJSONArray("LableSort");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						int sortId = j.optInt("sortId");
						String sortName = j.optString("sortName");
						String url = j.optString("url");
						lableSort.add(new LableSort(sortId, sortName, url));
					}
					holvadapter = new MyHOLVAdapter();
					gv_fde.setAdapter(holvadapter);
					// setLableSort();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	// private void setLableSort() {
	// ll_contain_hsll.removeAllViews();
	// View vv;
	// for (int i = 0; i < lableSort.size(); i++) {
	// vv = View.inflate(getActivity(), R.layout.custom_hori, null);
	// LinearLayout ll_mode = (LinearLayout) vv.findViewById(R.id.ll_mode);
	//
	// TextView tv = (TextView) vv.findViewById(R.id.tv_horitype);
	// ImageView im_horitype = (ImageView) vv.findViewById(R.id.im_horitype);
	// tv.setText(lableSort.get(i).sortName);
	// ll_contain_hsll.getChildAt(i).setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	//
	// }
	// });
	// ll_mode.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// if (ppw_price != null && ppw_price.isShowing()) {
	// // ppw_price.setFocusable(false);
	// ppw_price.dismiss();
	// } else {
	//
	// System.out.println("zuo=sortName=" + lableSort.get(p).sortName + "  p=" +
	// p);
	// initPpwPrice(lableSort.get(p).sortName);
	// ppw_price.showAsDropDown(v, 1, 1);
	// }
	//
	// }
	// });
	//
	// ll_contain_hsll.addView(vv);
	// }
	// }

	public void initPpwPrice(final String sortName) {
		customView = View.inflate(getActivity(), R.layout.ppwprice, null);
		ppw_price = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		ppw_price.setFocusable(true);
		ppw_price.setBackgroundDrawable(new BitmapDrawable());
		currsortName = sortName;
		ppw_price.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		final EditText ed_fromp = (EditText) customView.findViewById(R.id.ed_fromp);
		final EditText ed_endp = (EditText) customView.findViewById(R.id.ed_endp);
		LinearLayout ll_seleprice = (LinearLayout) customView.findViewById(R.id.ll_seleprice);
		LayoutParams params = ll_seleprice.getLayoutParams();
		params.width = (int) (screenWidth / 2.0);
		ll_seleprice.setLayoutParams(params);
		TextView tv_fromlow = (TextView) customView.findViewById(R.id.tv_fromlow);
		TextView tv_fromhight = (TextView) customView.findViewById(R.id.tv_fromhight);
		Button btn_sureprice = (Button) customView.findViewById(R.id.btn_sureprice);
		btn_sureprice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closekey();
				if (ed_endp.getText().toString().length() != 0) {
					// getData(0,
					// Integer.parseInt(ed_fromp.getText().toString()),
					// 0, Integer.parseInt(ed_endp.getText().toString()));
					type = 5;
					fromprice = Integer.parseInt(ed_fromp.getText().toString());
					endprice = Integer.parseInt(ed_endp.getText().toString());
					makeList.clear();
					getdate(type, fromprice, endprice, "" + sortName, 1, pageSize);
					ppw_price.dismiss();
				}
			}
		});
		tv_fromlow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				type = 6;
				fromprice = 0;
				endprice = 0;
				makeList.clear();
				getdate(type, fromprice, endprice, "" + sortName, 1, pageSize);
				ppw_price.dismiss();
			}
		});
		tv_fromhight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				type = 5;
				fromprice = 0;
				endprice = 0;
				makeList.clear();
				getdate(type, fromprice, endprice, "" + sortName, 1, pageSize);
				ppw_price.dismiss();
			}
		});
	}

	public void closekey() {
		View view = getActivity().getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private void getdate(int type, int downPrice, int highPrice, String style, int pageIndex1, int pageSize) {

		RequestParams params = new RequestParams();
		params.put("type", type);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		if (type == 5 || type == 6) {
			params.put("user.style", style);
			if (highPrice != 0) {
				params.put("user.downPrice", downPrice);
				params.put("user.highPrice", highPrice);
			}
		}
		System.out.println("zuoparams =" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMake, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo--" + response.toString());
				dismissProgressDialog();
				try {
					if (isfirsttovp) {
						JSONArray jsonArray = response.getJSONArray("bannerList");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject j = jsonArray.getJSONObject(i);
							String url = j.optString("url");
							String info = j.optString("info");
							JSONArray optJSONArray = j.optJSONArray("PhotoList");
							List<PhotoList> photoList = new ArrayList<PhotoList>();
							for (int k = 0; k < optJSONArray.length(); k++) {
								JSONObject jj = optJSONArray.getJSONObject(k);
								String photoUrl = jj.optString("photoUrl");
								String high = jj.optString("high");
								String width = jj.optString("width");
								photoList.add(new PhotoList(photoUrl, high, width));
							}

							bannerList.add(new BannerList(url, info, photoList));
						}
						vpData.clear();
						initVp();
						initSmallDot(0);
						isfirsttovp = false;
					}

					JSONArray jsonArray2 = response.getJSONArray("makeList");
					for (int i = 0; i < jsonArray2.length(); i++) {
						JSONObject j = jsonArray2.getJSONObject(i);
						String imgUrl = j.optString("imgUrl");
						String address = j.optString("address");
						String userName = j.optString("userName");
						String style = j.optString("style");
						int price = j.optInt("price");
						String level = j.optString("level");
						int userId = j.optInt("userId");
						int hotSocre = j.optInt("hotSocre");
						int nub = j.optInt("nub");
						makeList.add(new MakeList(imgUrl, style, address, userName, price, level, userId, hotSocre, nub));
						size++;
					}
					if (size == 10) {
						isend = false;
						pageIndex++;
					} else {
						isend = true;
					}
					size = 0;

					lvadapter.notifyDataSetChanged();
					isloading = false;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void initView(View v) {
		// rb_inland_cus, rb_japan_cus, rb_stye_cus, rb_rank_cus,
		// rb_hotd_cus
		LunBo();
		rb_inland_cus = (RadioButton) v.findViewById(R.id.rb_inland_cus);
		rb_japan_cus = (RadioButton) v.findViewById(R.id.rb_japan_cus);
		// rb_stye_cus = (RadioButton) v.findViewById(R.id.rb_stye_cus);
		rb_rank_cus = (RadioButton) v.findViewById(R.id.rb_rank_cus);
		rb_hotd_cus = (RadioButton) v.findViewById(R.id.rb_hotd_cus);
		rb_inland_cus.setOnClickListener(this);
		rb_japan_cus.setOnClickListener(this);
		// rb_stye_cus.setOnClickListener(this);
		rb_rank_cus.setOnClickListener(this);
		rb_hotd_cus.setOnClickListener(this);
		ll_contain_hsll = (LinearLayout) v.findViewById(R.id.ll_contain_hsll);
		sllv_hot_cus = (ListView) v.findViewById(R.id.sllv_hot_cus);
		headview = View.inflate(getActivity(), R.layout.headview_customization, null);
		initHeadView();
		sllv_hot_cus.addHeaderView(headview, null, false);
		lvadapter = new MyLVAdapter();
		sllv_hot_cus.setAdapter(lvadapter);
		// sv_hot_cus = (PullToRefreshScrollView)
		// v.findViewById(R.id.sv_hot_cus);
		customView = View.inflate(getActivity(), R.layout.ppwprice, null);
		ppw_price = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		sllv_hot_cus.setOnItemClickListener(this);
		sllv_hot_cus.setOnScrollListener(this);
		type = 1;
		fromprice = 0;
		endprice = 0;
		isloading = true;
		showProgressDialog("加载中...");
		getdate(type, fromprice, endprice, "", 1, pageSize);
	}

	private void initHeadView() {
		dots_group_cus = (LinearLayout) headview.findViewById(R.id.dots_group_cus);
		vp_hot_cus = (ViewPager) headview.findViewById(R.id.vp_hot_cus);
		tv_custom_rule = (TextView) headview.findViewById(R.id.tv_custom_rule);
		vp_hot_cus.setOnPageChangeListener(this);
		holv = (HorizontalListView) headview.findViewById(R.id.holv);
		gv_fde = (MyGridView) headview.findViewById(R.id.gv_fde);
		gv_fde.setVisibility(View.VISIBLE);
		tv_custom_rule.setOnClickListener(this);
	}

	class MyHOLVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lableSort == null ? 0 : lableSort.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lableSort.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(getActivity(), R.layout.custom_hori, null);
			LinearLayout ll_mode = (LinearLayout) v.findViewById(R.id.ll_mode);
			TextView tv = (TextView) v.findViewById(R.id.tv_horitype);
			ImageView im_horitype = (ImageView) v.findViewById(R.id.im_horitype);
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + lableSort.get(position).url, im_horitype, options);
			tv.setText(lableSort.get(position).sortName);
			ll_mode.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (ppw_price != null && ppw_price.isShowing()) {
						// ppw_price.setFocusable(false);
						ppw_price.dismiss();
					} else {

						initPpwPrice(lableSort.get(position).sortName);
						ppw_price.showAsDropDown(v, 1, 1);
					}

				}
			});
			return v;
		}
	}

	private void initVp() {

		if (getActivity() != null) {
			final ArrayList<String> data = new ArrayList<String>();
			for (int i = 0; i < bannerList.size(); i++) {
				cview = View.inflate(getActivity(), R.layout.vp_item, null);
				ImageView im_vp = (ImageView) cview.findViewById(R.id.im_vp);

				Glide.with(getActivity()).load(AppFinalUrl.photoBaseUri + bannerList.get(i).url)
				/*
				 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
				 */
				.crossFade().into(im_vp);

				// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
				// bannerList.get(i), im_vp, options);
				data.add(bannerList.get(i).url);
				im_vp.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(), Activity_ActivityMain.class);
						intent.putExtra("info", bannerList.get(currposition).info);
						intent.putExtra("photolist", (Serializable) bannerList.get(currposition).photoList);
						startActivity(intent);
					}
				});
				vpData.add(cview);
			}
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vp_hot_cus.getLayoutParams();
			layoutParams.height = (int) (w * 5 * 2 / (double) 14);
			vp_hot_cus.setLayoutParams(layoutParams);
			vpadapter = new MyVPAdapter();
			vp_hot_cus.setAdapter(vpadapter);
		}

	}

	// @Override
	// public void onPause() {
	// // TODO Auto-generated method stub
	// super.onPause();
	// MobclickAgent.onPageEnd("定制");
	// }

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	// @Override
	// public void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// MobclickAgent.onPageStart("定制"); // 统计页面，"MainScreen"为页面名称，可自定义
	// }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeMessages(2);
	}

	private void initSmallDot(int index) {
		if (getActivity() != null && bannerList.size() != 0) {
			dots_group_cus.removeAllViews();
			for (int i = 0; i < bannerList.size(); i++) {

				ImageView imageView = new ImageView(getActivity());
				imageView.setImageResource(R.drawable.round);
				imageView.setPadding(5, 0, 5, 0);
				dots_group_cus.addView(imageView);
			}

			// 设置选中项
			((ImageView) dots_group_cus.getChildAt(index)).setImageResource(R.drawable.round_select);
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
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		System.out.println("zuo定制==" + hidden);
		if (!hidden) {
			if (makeList.size() == 0) {

			}

			MobclickAgent.onPageStart("定制");
		} else {
			MobclickAgent.onPageEnd("定制");
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
		currposition = arg0;
	}

	public void LunBo() {
		i = 0;
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(2);
			}
		}, 3000, 3000);
	}

	class MyLVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return makeList == null ? 0 : makeList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return makeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(getActivity(), R.layout.item_cuslv2, null);
				h.rl_chart = (RelativeLayout) v.findViewById(R.id.rl_chart);
				h.im_head_cus = (CircleImageView) v.findViewById(R.id.im_head_cus);
				h.tv_name_cus = (TextView) v.findViewById(R.id.tv_name_cus);
				h.tv_info_cus = (TextView) v.findViewById(R.id.tv_info_cus);
				h.tv_price_cus = (TextView) v.findViewById(R.id.tv_price_cus);
				h.tv_rank_cus = (TextView) v.findViewById(R.id.tv_rank_cus);
				h.ll_contain_trust_studio = (LinearLayout) v.findViewById(R.id.ll_contain_trust_studio1);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			if (makeList.get(position).address == null || makeList.get(position).address.equals("null")
					|| makeList.get(position).address.equals("") || makeList.get(position).address.equals("未填写")) {
				h.tv_info_cus.setVisibility(View.INVISIBLE);
			} else {
				h.tv_info_cus.setText(makeList.get(position).address);
				h.tv_info_cus.setVisibility(View.VISIBLE);
			}
			if (makeList.get(position).userName == null || makeList.get(position).userName.equals("null")
					|| makeList.get(position).userName.equals("未填写")) {
				h.tv_name_cus.setText("");
			} else {
				h.tv_name_cus.setText(makeList.get(position).userName);
			}

			if (makeList.get(position).price == 0) {
				h.tv_price_cus.setVisibility(View.GONE);
			} else {
				h.tv_price_cus.setVisibility(View.VISIBLE);
				h.tv_price_cus.setText("￥" + makeList.get(position).price);
			}
			if (makeList.get(position).level == null || makeList.get(position).level.equals("null")
					|| makeList.get(position).level.equals("")) {
				h.tv_rank_cus.setText("");
			} else {

				// h.tv_rank_cus.setText("" + makeList.get(position).level);
				// if (makeList.get(position).level.contains("黑")) {
				// h.tv_rank_cus.setBackgroundResource(R.drawable.black);
				// h.tv_rank_cus.setTextColor(getResources().getColor(R.color.white));
				// } else if (makeList.get(position).level.contains("灰")) {
				// h.tv_rank_cus.setBackgroundResource(R.drawable.grey);
				// } else if (makeList.get(position).level.contains("白")) {
				// h.tv_rank_cus.setBackgroundResource(R.drawable.write);
				// h.tv_rank_cus.setTextColor(getResources().getColor(R.color.black));
				// } else if (makeList.get(position).level.contains("金")) {
				// h.tv_rank_cus.setBackgroundResource(R.drawable.gold);
				// }

				if (makeList.get(position).level.contains("合一")) {
					h.tv_rank_cus.setBackgroundResource(R.drawable.heyi);
				} else if (makeList.get(position).level.contains("黑一")) {
					h.tv_rank_cus.setBackgroundResource(R.drawable.bl1);
				} else if (makeList.get(position).level.contains("黑二")) {
					h.tv_rank_cus.setBackgroundResource(R.drawable.bl2);
				} else if (makeList.get(position).level.contains("黑三")) {
					h.tv_rank_cus.setBackgroundResource(R.drawable.bl3);
				} else if (makeList.get(position).level.contains("灰一")) {
					h.tv_rank_cus.setBackgroundResource(R.drawable.g1);
				} else if (makeList.get(position).level.contains("灰二")) {
					h.tv_rank_cus.setBackgroundResource(R.drawable.g2);
				} else if (makeList.get(position).level.contains("灰三")) {
					h.tv_rank_cus.setBackgroundResource(R.drawable.g3);
				} else if (makeList.get(position).level.contains("白一")) {
					h.tv_rank_cus.setBackgroundResource(R.drawable.w1);
				} else if (makeList.get(position).level.contains("白二")) {
					h.tv_rank_cus.setBackgroundResource(R.drawable.w2);
				} else if (makeList.get(position).level.contains("白三")) {
					h.tv_rank_cus.setBackgroundResource(R.drawable.w3);
				}

			}

			setStart(h.ll_contain_trust_studio, makeList.get(position).nub);

			// Glide.with(getActivity()).load(AppFinalUrl.BASEURL +
			// makeList.get(position).imgUrl)
			// /*
			// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			// */
			// .placeholder(R.drawable.tianc).crossFade().into(h.im_head_cus);

			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + makeList.get(position).imgUrl, h.im_head_cus,
					options);
			h.rl_chart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("name", makeList.get(position).userName)
							.putExtra("userId", "" + makeList.get(position).userId));
				}
			});
			return v;
		}
	}

	class MyHolder {
		LinearLayout ll_contain_trust_studio;
		CircleImageView im_head_cus;
		TextView tv_name_cus, tv_info_cus, tv_price_cus, tv_rank_cus;
		RelativeLayout rl_chart;
	}

	class MakeList {
		String imgUrl, style, address, userName, level;
		int price, userId, hotSocre, nub;

		public MakeList(String imgUrl, String style, String address, String userName, int price, String level, int userId,
				int hotSocre, int nub) {
			super();
			this.imgUrl = imgUrl;
			this.style = style;
			this.address = address;
			this.userName = userName;
			this.price = price;
			this.level = level;
			this.userId = userId;
			this.hotSocre = hotSocre;
			this.nub = nub;
		}
	}

	class LableSort {
		int sortId;
		String sortName, url;

		public LableSort(int sortId, String sortName, String url) {
			super();
			this.sortId = sortId;
			this.sortName = sortName;
			this.url = url;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// rb_inland_cus, rb_japan_cus, rb_stye_cus, rb_rank_cus,
		// rb_hotd_cus
		case R.id.rb_inland_cus:
			type = 1;
			fromprice = 0;
			endprice = 0;
			makeList.clear();
			pageIndex = 1;
			getdate(type, fromprice, endprice, "", 1, pageSize);

			break;
		case R.id.rb_japan_cus:
			type = 2;
			fromprice = 0;
			endprice = 0;
			makeList.clear();
			pageIndex = 1;
			getdate(type, fromprice, endprice, "", 1, pageSize);
			break;
		// case R.id.rb_stye_cus:
		// getdate(1, 0, 0, "");
		// break;
		case R.id.rb_rank_cus:
			type = 3;
			fromprice = 0;
			endprice = 0;
			makeList.clear();
			pageIndex = 1;
			getdate(type, fromprice, endprice, "", 1, pageSize);
			break;
		case R.id.rb_hotd_cus:
			type = 4;
			fromprice = 0;
			endprice = 0;
			makeList.clear();
			pageIndex = 1;
			getdate(type, fromprice, endprice, "", 1, pageSize);
			break;
		case R.id.tv_custom_rule:
			startActivity(new Intent(getActivity(), Activity_Custom_Rule.class));
			break;
		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), Activity_Designer.class);
		intent.putExtra("userId", makeList.get(position - 1).userId);
		startActivity(intent);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		makeList.clear();
		getdate(type, 0, 0, currsortName, 1, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		if (isend) {

			Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getdate(type, fromprice, endprice, currsortName, pageIndex, pageSize);
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int lastItem = firstVisibleItem + visibleItemCount;
		if (lastItem == totalItemCount) {
			if (isend) {
				// sv_hot.onRefreshComplete();
				// Toast.makeText(getActivity(), "没有更多了",
				// Toast.LENGTH_SHORT).show();
			} else {
				// pics.clear();
				// vpData.clear();
				if (isloading) {

				} else {
					isloading = true;
					getdate(type, fromprice, endprice, currsortName, pageIndex, pageSize);
				}

			}
		}
	};

	class BannerList {
		String url, info;
		List<PhotoList> photoList;

		public BannerList(String url, String info, List<PhotoList> photoList) {
			super();
			this.url = url;
			this.info = info;
			this.photoList = photoList;
		}
	}

	private int truststar = 3;

	private void setStart(LinearLayout ll_contain_trust_studio, int nub) {
		ll_contain_trust_studio.removeAllViews();
		if (nub == 0) {
			nub = 1;
		}
		for (int i = 0; i < nub; i++) {
			View v = View.inflate(getActivity(), R.layout.item_star, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_star);
			im.setPadding(4, 0, 4, 0);
			im.setImageResource(R.drawable.newstar_orange);
			ll_contain_trust_studio.addView(v);
		}
	}

}
