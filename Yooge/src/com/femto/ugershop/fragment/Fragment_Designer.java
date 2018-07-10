package com.femto.ugershop.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_ActivityMain;
import com.femto.ugershop.activity.Activity_Designer;
import com.femto.ugershop.activity.Activity_LookPic;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.PhotoList;
import com.femto.ugershop.fragment.Fragment_NewGoods.BannerList;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AbsListView.OnScrollListener;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.yzr.areawheel.AreaWheel;
import com.yzr.areawheel.ScreenInfo;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_Designer extends BaseFragment implements OnRefreshListener2<ScrollView>, OnClickListener,
		OnPageChangeListener, OnItemClickListener {
	private View view;
	private RadioButton rb_inland, rb_japan, rb_styte, rb_rank, rb_hotd;
	// private PullToRefreshScrollView sv_hot_de;
	private ViewPager vp_hot_de;
	private LinearLayout dots_group_de;
	// private GridView gv_hot_de;
	private View cview;
	private MultiColumnListView gv_hot;
	private List<View> vpData;
	private MyVPAdapter vpadapter;
	private MyGVAdapter gvadapter;
	private List<BannerList> pics;
	private List<TeacherList> t;
	private boolean isloading = true;
	private DisplayImageOptions options;
	private int w;
	private int width;
	private int height;
	private boolean isfirst;
	private ProgressDialog pdd;
	private TextView tv_clicktomore_de;
	int i = 1;
	private int currposition = 0;
	Timer timer = new Timer();
	private int type = 0;
	int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private boolean isfirstbanner = true;
	private View headview;
	// private TextView tv_loading;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				gvadapter.notifyDataSetChanged();
				break;
			case 2:
				if (vpData.size() != 0) {
					int j = i % vpData.size();
					vp_hot_de.setCurrentItem(j);
					i++;

				}
				break;
			case 3:
				if (myArea != null) {
					myArea.setText(areaWheel.getArea());
				}
				handler.sendEmptyMessageDelayed(3, 1000);
				break;

			default:
				break;
			}

		}

	};
	private LinearLayout ll_designer;

	private RelativeLayout rl_hot;
	private RelativeLayout rl_new;
	private RelativeLayout rl_area;
	private PopupWindow popupWindow;
	private AreaWheel areaWheel;
	private TextView myArea;
	private TextView tv_confirm;
	private int screenWidth;

	private void getMore() {

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_designer, container, false);
		vpData = new ArrayList<View>();
		pics = new ArrayList<BannerList>();
		t = new ArrayList<TeacherList>();
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
		int dp2px = dp2px(getActivity(), 34);
		w = (int) ((screenWidth - dp2px) / 2.0);
		pdd = new ProgressDialog(getActivity());
		pdd.setMessage("加载中");
		initView(view);

		return view;
	}

	private void getData(int type, final int pageIndex1, int pageSize) {

		RequestParams params = new RequestParams();

		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		if (type == 6) {
			if (!myArea.getText().equals("")) {
				params.put("type", type);
				params.put("user.address", myArea.getText());
			} else {
				params.put("type", 1);
			}

		} else {

			params.put("type", type);
		}

		System.out.println("zuo-params=" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetTeacher, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();

				System.out.println("zuo===response=" + response.toString());
				try {
					if (isfirstbanner) {
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

							pics.add(new BannerList(url, info, photoList));
						}
						initVp();
						isfirstbanner = false;
					}

					JSONArray jsonArray2 = response.getJSONArray("teacherList");
					for (int i = 0; i < jsonArray2.length(); i++) {
						JSONObject j = jsonArray2.getJSONObject(i);
						String imgUrl = j.optString("imgUrl");
						String style = j.optString("style");
						String address = j.optString("address");
						String userName = j.optString("userName");
						String level = j.optString("level");
						int userId = j.optInt("userId");
						int hotSocre = j.optInt("hotSocre");
						size++;
						t.add(new TeacherList(imgUrl, style, address, userName, level, userId, hotSocre));
					}

					if (size == 10) {
						pageIndex++;
						System.out.println("zuo==pageIndex==" + pageIndex);
						isend = false;

					} else {

						isend = true;
					}
					size = 0;

					tv_clicktomore_de.setVisibility(View.GONE);
					gvadapter.notifyDataSetChanged();
					isloading = false;
					// sv_hot_de.onRefreshComplete();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void initView(View v) {
		rb_inland = (RadioButton) v.findViewById(R.id.rb_inland);
		rb_japan = (RadioButton) v.findViewById(R.id.rb_japan);
		rb_styte = (RadioButton) v.findViewById(R.id.rb_styte);
		rb_rank = (RadioButton) v.findViewById(R.id.rb_rank);
		rb_hotd = (RadioButton) v.findViewById(R.id.rb_hotd);
		// tv_loading = (TextView) v.findViewById(R.id.tv_loading);
		rb_hotd.setOnClickListener(this);
		rb_rank.setOnClickListener(this);
		rb_styte.setOnClickListener(this);
		rb_japan.setOnClickListener(this);
		rb_inland.setOnClickListener(this);
		// sv_hot_de = (PullToRefreshScrollView) v.findViewById(R.id.sv_hot_de);
		// vp_hot_de = (ViewPager) v.findViewById(R.id.vp_hot_de);
		// dots_group_de = (LinearLayout) v.findViewById(R.id.dots_group_de);
		// gv_hot_de = (GridView) v.findViewById(R.id.gv_hot_de);
		gv_hot = (MultiColumnListView) v.findViewById(R.id.gv_hot);
		tv_clicktomore_de = (TextView) v.findViewById(R.id.tv_clicktomore_de);

		// vp_hot_de.setOnPageChangeListener(this);
		// 添加头部
		headview = View.inflate(getActivity(), R.layout.headview, null);
		initHeadView();
		gv_hot.addHeaderView(headview, null, false);
		gvadapter = new MyGVAdapter();
		gv_hot.setAdapter(gvadapter);
		gv_hot.setOnItemClickListener(this);
		gv_hot.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int lastItem = firstVisibleItem + visibleItemCount;
				if (lastItem == totalItemCount) {

					if (isloading) {
						System.out.println("zuo加载中pageIndex=" + pageIndex);
					} else {
						if (isend) {
							// sv_hot.onRefreshComplete();
							// Toast.makeText(getActivity(), "没有更多了",
							// Toast.LENGTH_SHORT).show();
						} else {
							// pics.clear();
							// vpData.clear();
							isloading = true;
							System.out.println("zuo加载中");
							tv_clicktomore_de.setVisibility(View.VISIBLE);
							getData(type, pageIndex, pageSize);
						}
					}
				}
			}
		});
		// sv_hot_de.setOnRefreshListener(this);
		// sv_hot_de.setMode(Mode.BOTH);

		LunBo();
	}

	private void initHeadView() {
		vp_hot_de = (ViewPager) headview.findViewById(R.id.vp_hot);
		dots_group_de = (LinearLayout) headview.findViewById(R.id.dots_group);
		ll_designer = (LinearLayout) headview.findViewById(R.id.ll_designer);
		vp_hot_de.setOnPageChangeListener(this);
		ll_designer.setVisibility(View.VISIBLE);

		rl_hot = (RelativeLayout) headview.findViewById(R.id.rl_hot);
		rl_new = (RelativeLayout) headview.findViewById(R.id.rl_new);
		rl_area = (RelativeLayout) headview.findViewById(R.id.rl_area);

		rl_hot.setOnClickListener(this);
		rl_new.setOnClickListener(this);
		rl_area.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_inland:
			type = 1;
			t.clear();
			isend = false;
			pageIndex = 1;
			getData(type, pageIndex, 10);
			break;
		case R.id.rb_japan:
			type = 2;
			t.clear();
			isend = false;
			pageIndex = 1;
			getData(type, pageIndex, 10);
			break;
		case R.id.rb_styte:
			type = 0;
			isend = false;
			t.clear();
			pageIndex = 1;
			getData(type, pageIndex, 10);
			break;
		case R.id.rb_rank:
			type = 3;
			t.clear();
			isend = false;
			pageIndex = 1;
			getData(type, pageIndex, 10);
			break;
		case R.id.rb_hotd:
			isend = false;
			type = 4;
			t.clear();
			pageIndex = 1;
			getData(type, pageIndex, 10);
			break;
		case R.id.rl_hot:
			showProgressDialog("加载中...");
			isend = false;
			type = 7;
			t.clear();
			pageIndex = 1;
			getData(type, pageIndex, 10);
			break;
		case R.id.rl_new:
			showProgressDialog("加载中...");
			isend = false;
			type = 5;
			t.clear();
			pageIndex = 1;
			getData(type, pageIndex, 10);
			break;
		case R.id.rl_area:

			showPopupWindow();

			// isend = false;
			// type = 5;
			// t.clear();
			// pageIndex = 1;
			// getData(type, pageIndex, 10);
			break;
		case R.id.tv_confirm:
			popupWindow.dismiss();
			showProgressDialog("加载中...");
			isend = false;
			type = 6;
			t.clear();
			pageIndex = 1;
			getData(type, pageIndex, 10);
			break;

		default:
			break;
		}
	}

	private void showPopupWindow() {
		// TODO Auto-generated method stub

		View contentView = view.inflate(getActivity(), R.layout.popu_area, null);
		popupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

		final View timepickerview = View.inflate(getActivity(), R.layout.areawheel, null);
		ScreenInfo screenInfo = new ScreenInfo(getActivity());
		areaWheel = new AreaWheel(getActivity(), timepickerview);
		areaWheel.screenheight = screenInfo.getHeight();

		areaWheel.initAreaPicker();
		((ViewGroup) contentView).addView(timepickerview);
		myArea = (TextView) contentView.findViewById(R.id.myArea);
		tv_confirm = (TextView) contentView.findViewById(R.id.tv_confirm);
		tv_confirm.setOnClickListener(this);
		myArea.setOnClickListener(this);
		handler.sendEmptyMessage(3);

		popupWindow.setTouchable(true);

		popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popu_no));

		// 设置好参数之后再show
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		System.out.println("zuo设计师==" + hidden);
		if (!hidden) {

			if (t.size() == 0) {
				type = 1;
				showProgressDialog("加载中");
				getData(type, pageIndex, pageSize);
			}
			MobclickAgent.onPageStart("设计师");
		} else {
			MobclickAgent.onPageEnd("设计师");
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// MobclickAgent.onPageStart("设计师"); // 统计页面，"MainScreen"为页面名称，可自定义
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	private void initSmallDot(int index) {
		if (getActivity() != null && pics.size() != 0) {
			dots_group_de.removeAllViews();
			for (int i = 0; i < pics.size(); i++) {
				ImageView imageView = new ImageView(getActivity());
				imageView.setImageResource(R.drawable.round);
				imageView.setPadding(5, 0, 5, 0);
				dots_group_de.addView(imageView);
			}

			// 设置选中项
			((ImageView) dots_group_de.getChildAt(index)).setImageResource(R.drawable.round_select);
		}

	}

	private void initVp() {
		final ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; i < pics.size(); i++) {
			cview = View.inflate(getActivity(), R.layout.vp_item, null);
			ImageView im_vp = (ImageView) cview.findViewById(R.id.im_vp);
			data.add(pics.get(i).url);

			Glide.with(getActivity()).load(AppFinalUrl.photoBaseUri + pics.get(i).url)

			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(im_vp);

			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// pics.get(i), im_vp, options);
			im_vp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), Activity_ActivityMain.class);
					intent.putExtra("info", pics.get(currposition).info);
					intent.putExtra("photolist", (Serializable) pics.get(currposition).photoList);
					startActivity(intent);
				}
			});
			vpData.add(cview);
		}
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vp_hot_de.getLayoutParams();
		layoutParams.height = (int) (screenWidth * 5 / (double) 14);
		vp_hot_de.setLayoutParams(layoutParams);
		vpadapter = new MyVPAdapter();
		vp_hot_de.setAdapter(vpadapter);
		initSmallDot(0);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// .onPageEnd("设计师");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeMessages(2);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	class MyGVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return t == null ? 0 : t.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return t.get(position);
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
				v = View.inflate(getActivity(), R.layout.vp_item_designer, null);
				h.im_pic_de = (ImageView) v.findViewById(R.id.im_pic_de);
				h.tv_local_de = (TextView) v.findViewById(R.id.tv_local_de);
				h.tv_username_de = (TextView) v.findViewById(R.id.tv_username_de);
				h.tv_prisenub_de = (TextView) v.findViewById(R.id.tv_prisenub_de);
				h.tv_rank_de = (TextView) v.findViewById(R.id.tv_rank_deee);
				h.tv_location_de = (TextView) v.findViewById(R.id.tv_location_de);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_prisenub_de.setText("" + t.get(position).hotSocre);
			// if (t.get(position).level.contains("黑")) {
			// h.tv_rank_de.setBackgroundResource(R.drawable.black);
			// h.tv_rank_de.setTextColor(getResources().getColor(R.color.white));
			// } else if (t.get(position).level.contains("灰")) {
			// h.tv_rank_de.setBackgroundResource(R.drawable.grey);
			// } else if (t.get(position).level.contains("白")) {
			// h.tv_rank_de.setBackgroundResource(R.drawable.write);
			// h.tv_rank_de.setTextColor(getResources().getColor(R.color.black));
			// } else if (t.get(position).level.contains("金")) {
			// h.tv_rank_de.setBackgroundResource(R.drawable.gold);
			// }

			if (t.get(position).level.contains("合一")) {
				h.tv_rank_de.setBackgroundResource(R.drawable.heyi);
			} else if (t.get(position).level.contains("黑一")) {
				h.tv_rank_de.setBackgroundResource(R.drawable.bl1);
			} else if (t.get(position).level.contains("黑二")) {
				h.tv_rank_de.setBackgroundResource(R.drawable.bl2);
			} else if (t.get(position).level.contains("黑三")) {
				h.tv_rank_de.setBackgroundResource(R.drawable.bl3);
			} else if (t.get(position).level.contains("灰一")) {
				h.tv_rank_de.setBackgroundResource(R.drawable.g1);
			} else if (t.get(position).level.contains("灰二")) {
				h.tv_rank_de.setBackgroundResource(R.drawable.g2);
			} else if (t.get(position).level.contains("灰三")) {
				h.tv_rank_de.setBackgroundResource(R.drawable.g3);
			} else if (t.get(position).level.contains("白一")) {
				h.tv_rank_de.setBackgroundResource(R.drawable.w1);
			} else if (t.get(position).level.contains("白二")) {
				h.tv_rank_de.setBackgroundResource(R.drawable.w2);
			} else if (t.get(position).level.contains("白三")) {
				h.tv_rank_de.setBackgroundResource(R.drawable.w3);
			}

			// h.tv_rank_de.setText("" + t.get(position).level);
			if (t.get(position).address != null && !t.get(position).address.equals("null") && !t.get(position).address.equals("")) {
				h.tv_local_de.setText("" + t.get(position).address);
				h.tv_local_de.setVisibility(View.VISIBLE);
			} else {
				h.tv_local_de.setText("");
				h.tv_location_de.setVisibility(View.INVISIBLE);
			}
			h.tv_username_de.setText(t.get(position).userName);

			Glide.with(getActivity()).load(AppFinalUrl.photoBaseUri + t.get(position).imgUrl + "-middle").override(w, w)
					.centerCrop()
					/*
					 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
					 */
					.crossFade().placeholder(R.drawable.tiancsq).into(h.im_pic_de);

			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// t.get(position).imgUrl, h.im_pic_de, options);
			if (width != 0) {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) h.im_pic_de.getLayoutParams();
				params.height = w;
				params.width = w;
				h.im_pic_de.setLayoutParams(params);
			}
			return v;
		}
	}

	class MyHolder {
		ImageView im_pic_de;
		TextView tv_username_de, tv_prisenub_de, tv_rank_de, tv_local_de, tv_location_de;
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

	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// Intent intent = new Intent(getActivity(), Activity_Designer.class);
	// intent.putExtra("userId", t.get(position).userId);
	// startActivity(intent);
	//
	// }

	class TeacherList {
		String imgUrl, level, style, address, userName;
		int userId, hotSocre;

		public TeacherList(String imgUrl, String style, String address, String userName, String level, int userId, int hotSocre) {
			super();
			this.imgUrl = imgUrl;
			this.style = style;
			this.address = address;
			this.userName = userName;
			this.level = level;
			this.userId = userId;
			this.hotSocre = hotSocre;
		}
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private Bitmap getImageBitmap(String url) {
		URL imgUrl = null;
		Bitmap bitmap = null;
		try {
			imgUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		t.clear();
		isend = false;
		pageIndex = 1;
		getData(type, pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		if (isend) {
			// sv_hot_de.onRefreshComplete();
			Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			tv_clicktomore_de.setText("加载中...");
			getData(type, pageIndex, pageSize);
		}
	}

	@Override
	public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), Activity_Designer.class);
		intent.putExtra("userId", t.get(position - 1).userId);
		startActivity(intent);
	}

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

}
