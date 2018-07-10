package com.femto.ugershop.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
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

import com.bumptech.glide.Glide;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_ActivityMain;
import com.femto.ugershop.activity.Activity_FilterCloth;
import com.femto.ugershop.activity.Activity_GoodsDetails;
import com.femto.ugershop.activity.Activity_Login;
import com.femto.ugershop.activity.Activity_LookPic;
import com.femto.ugershop.activity.Activity_PersonData;
import com.femto.ugershop.activity.Activity_Search;
import com.femto.ugershop.activity.Activity_ShoppingCar;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.PhotoList;
import com.femto.ugershop.fragment.Fragment_Store.CothTypy;
import com.femto.ugershop.fragment.Fragment_Store.MyPopuLVAdapter;
import com.femto.ugershop.fragment.Fragment_Store.ProductJson;
import com.femto.ugershop.fragment.Fragment_Store.SmallProductJy;
import com.femto.ugershop.view.CustomProgressDialog;
import com.femto.ugershop.view.MyGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView.OnRefreshListener;
import com.huewu.pla.sample.ImageGridAdapter;
import com.huewu.pla.sample.ScaleImageView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;

public class Fragment_NewGoods extends BaseFragment implements OnClickListener, OnPageChangeListener,
		OnRefreshListener2<ListView>, OnRefreshListener, OnItemClickListener {
	private View view;
	private ViewPager vp_hot;
	// private MyGridView gv_hot;
	private MyGVAdapter adapter;
	private List<View> vpData;
	View cview;
	private MyVPAdapter vpadapter;
	// private PullToRefreshScrollView sv_hot;
	private LinearLayout dots_group;
	private PopupWindow ppw_price, ppwsort;
	private View customView, viewppwsort;
	private Context mContext;
	private boolean isShow = false;
	private List<BannerList> pics;
	private List<ProductList> productList;
	private DisplayImageOptions options;
	private int w;
	private int width;
	private int height;
	private RelativeLayout rl_sreach_new, rl_menu_new, rl_sort, rl_alltype;
	private TextView tv_loading;
	private boolean isloading = true;
	private PullToRefreshListView mAdapterView = null;
	private ProgressDialog pdd;
	int i = 1;
	private boolean isfirsttovp = true;

	private int currposition = 0;
	int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private int clothId = 0;
	private Timer timer = new Timer();
	private ImageGridAdapter igvadapter;
	private RadioButton rb_hot, rb_time, rb_grade, rb_book, rb_price;
	private List<String> postm;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				adapter.notifyDataSetChanged();
				break;
			case 2:
				if (vpData.size() != 0) {

					int j = i % vpData.size();
					vp_hot.setCurrentItem(j);
					i++;
				}

				break;
			case 3:
				mAdapterView.onRefreshComplete();
				break;
			default:
				break;
			}

		};
	};
	private boolean isfirst = false;
	private CustomProgressDialog pd = null;
	private boolean islogin;
	private int userid;
	private MBC mbc;
	private int fromprice = 0;
	private int endprice = 0;
	private int type = 6;
	private boolean isend = false;
	private View headview;
	private TextView tv_alltype, tv_sort;
	private LinearLayout ll_store;
	private ImageView im_totop_newgoods;
	private int picH, picW;

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
		view = inflater.inflate(R.layout.fragment_hot, container, false);
		SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
		islogin = sp.getBoolean("islogin", false);
		userid = sp.getInt("userid", 0);
		registBRC();
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(false) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				.imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.ARGB_8888)
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(getActivity(), 4);
		int dp2pxH = dp2px(getActivity(), 180);
		picH = screenHeight - dp2pxH;
		w = (screenWidth - dp2px) / 2;
		productJson = new ArrayList<ProductJson>();
		coths = new ArrayList<CothTypy>();
		/*
		 * LinearLayout.LayoutParams params = (LayoutParams) im_photo
		 * .getLayoutParams(); params.height = (drawable.getMinimumHeight() * w)
		 * / drawable.getMinimumWidth(); im_photo.setLayoutParams(params);
		 */
		pdd = new ProgressDialog(getActivity());
		pdd.setMessage("加载中");
		vpData = new ArrayList<View>();
		pics = new ArrayList<BannerList>();
		postm = new ArrayList<String>();
		postm.add("预定数");
		postm.add("热度");
		postm.add("时间");
		postm.add("等级");
		postm.add("价格");
		productList = new ArrayList<ProductList>();
		mContext = getActivity();
		plvadapter = new MyPopuLVAdapter();
		getData();
		initView(view);
		showProgressDialog("加载中");
		isloading = true;
		fromprice = 0;
		endprice = 0;
		getData(type, clothId, 0, 0, 1, 10);
		// MobclickAgent.onPageStart("商城");
		return view;
	}

	private void registBRC() {
		mbc = new MBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.search.cloth");
		getActivity().registerReceiver(mbc, filter);

	}

	// 按标签搜索
	class MBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.search.cloth")) {
				clothId = intent.getIntExtra("clothId", -1);
				showProgressDialog("加载中");
				productList.clear();
				// pics.clear();
				// vpData.clear();
				isend = false;
				pageIndex = 1;
				type = 0;
				System.out.println("zuo=clothId==" + clothId);
				fromprice = 0;
				endprice = 0;
				isloading = true;
				if (clothId == 0) {
					type = 6;
				}
				getData(type, clothId, 0, 0, pageIndex, 10);
			}
		}
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

	private void getData(int type, int sortid, int downPrice, int highPrice, int pageIndex1, int pageSize) {
		if (MyApplication.isNetworkAvailable(getActivity())) {

		} else {
			Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
			dismissProgressDialog();
			return;
		}
		RequestParams params = new RequestParams();
		params.put("type", type);
		params.put("product.sort.id", sortid);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		if (highPrice != 0) {
			params.put("product.downPrice", downPrice);
			params.put("product.highPrice", highPrice);
		}
		System.out.println("zuo==params=" + params);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetAppShow, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo==response=" + response.toString());
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

							pics.add(new BannerList(url, info, photoList));
						}
						initVp();
						initSmallDot(0);
						isfirsttovp = false;
					}

					JSONArray jsonArray2 = response.getJSONArray("productList");
					for (int i = 0; i < jsonArray2.length(); i++) {
						JSONObject j = jsonArray2.getJSONObject(i);
						String title = j.optString("title");
						String price = j.optString("price");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String url = j.optString("url");
						String high = j.optString("high");
						String wight = j.optString("wight");
						int status = j.optInt("status");
						int userId = j.optInt("userId");
						int topCount = j.optInt("topCount");
						int productId = j.optInt("productId");
						productList.add(new ProductList(title, price, userName, createDate, url, status, userId, topCount,
								productId, high, wight));
						size++;
					}

					// new Thread() {
					// public void run() {
					// if (!isfirst && productList.size() != 0 &&
					// productList.get(0).url != null
					// && productList.get(0).url.length() != 0) {
					// System.out.println("zuo==eee=" + productList.get(0).url);
					// Bitmap imageBitmap = getImageBitmap(AppFinalUrl.BASEURL +
					// productList.get(0).url);
					// if (imageBitmap != null) {
					// width = imageBitmap.getWidth();
					// height = imageBitmap.getHeight();
					// isfirst = true;
					// imageBitmap.recycle();
					// }
					//
					// }
					// handler.sendEmptyMessage(1);
					// dismissProgressDialog();
					//
					// };
					// }.start();
					dismissProgressDialog();

					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					isloading = false;
					tv_loading.setVisibility(View.GONE);
					System.out.println("zuosize==" + productList.size());
					mAdapterView.onRefreshComplete();
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void initView(View v) {

		// gv_hot = (MyGridView) v.findViewById(R.id.gv_hot);
		// sv_hot = (PullToRefreshScrollView) v.findViewById(R.id.sv_hot);

		adapter = new MyGVAdapter();
		// gv_hot.setAdapter(adapter);
		// gv_hot.setOnItemClickListener(this);
		tv_loading = (TextView) v.findViewById(R.id.tv_loading);
		rb_price = (RadioButton) v.findViewById(R.id.rb_price);
		rb_hot = (RadioButton) v.findViewById(R.id.rb_hot);
		rb_time = (RadioButton) v.findViewById(R.id.rb_time);
		rb_grade = (RadioButton) v.findViewById(R.id.rb_grade);
		rb_book = (RadioButton) v.findViewById(R.id.rb_book);
		mAdapterView = (PullToRefreshListView) v.findViewById(R.id.list_vp);
		// rb_hot, rb_time, rb_grade, rb_book, rb_price
		rl_sreach_new = (RelativeLayout) v.findViewById(R.id.rl_sreach_new);
		rl_menu_new = (RelativeLayout) v.findViewById(R.id.rl_menu_new);
		im_totop_newgoods = (ImageView) v.findViewById(R.id.im_totop_newgoods);
		// 添加头部
		headview = View.inflate(getActivity(), R.layout.headview, null);
		initHeadView();
		mAdapterView.getRefreshableView().addHeaderView(headview, null, false);
		if (MyApplication.type == 2) {
			rl_menu_new.setVisibility(View.VISIBLE);
		} else {
			rl_menu_new.setVisibility(View.INVISIBLE);
		}
		mAdapterView.setAdapter(adapter);
		rb_price.setOnClickListener(this);
		rb_hot.setOnClickListener(this);
		rb_time.setOnClickListener(this);
		rb_grade.setOnClickListener(this);
		rb_book.setOnClickListener(this);
		rl_menu_new.setOnClickListener(this);
		rl_sreach_new.setOnClickListener(this);
		im_totop_newgoods.setOnClickListener(this);
		// sv_hot.setOnRefreshListener(this);
		// sv_hot.setMode(Mode.BOTH);
		mAdapterView.setOnItemClickListener(this);
		mAdapterView.setOnRefreshListener(this);
		mAdapterView.setMode(Mode.BOTH);
		// mAdapterView.setOnRefreshListener(this);
		// mAdapterView.setOnScrollListener(new OnScrollListener() {
		//
		// @Override
		// public void onScrollStateChanged(PLA_AbsListView view, int
		// scrollState) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onScroll(PLA_AbsListView view, int firstVisibleItem, int
		// visibleItemCount, int totalItemCount) {
		// // TODO Auto-generated method stub
		// int lastItem = firstVisibleItem + visibleItemCount;
		// if (lastItem == totalItemCount) {
		// System.out.println("zuoScroll to the listview last item");
		// if (isloading) {
		//
		// } else {
		// if (isend) {
		// // sv_hot.onRefreshComplete();
		// // Toast.makeText(getActivity(), "没有更多了",
		// // Toast.LENGTH_SHORT).show();
		// } else {
		// // pics.clear();
		// // vpData.clear();
		// isloading = true;
		// tv_loading.setVisibility(View.VISIBLE);
		// getData(type, clothId, fromprice, endprice, pageIndex, 10);
		// }
		// }
		// View lastItemView = (View)
		// mAdapterView.getChildAt(mAdapterView.getChildCount() - 1);
		// if ((mAdapterView.getBottom()) == lastItemView.getBottom()) {
		//
		// }
		// }
		//
		// }
		// });

	}

	private void initHeadView() {
		vp_hot = (ViewPager) headview.findViewById(R.id.vp_hot);
		dots_group = (LinearLayout) headview.findViewById(R.id.dots_group);
		ll_store = (LinearLayout) headview.findViewById(R.id.ll_store);
		tv_alltype = (TextView) headview.findViewById(R.id.tv_alltype);
		tv_sort = (TextView) headview.findViewById(R.id.tv_sort);
		rl_alltype = (RelativeLayout) headview.findViewById(R.id.rl_alltype);
		rl_sort = (RelativeLayout) headview.findViewById(R.id.rl_sort);
		ll_store.setVisibility(View.VISIBLE);
		vp_hot.setOnPageChangeListener(this);
		tv_alltype.setOnClickListener(this);
		tv_sort.setOnClickListener(this);
		rl_alltype.setOnClickListener(this);
		rl_sort.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.im_totop_newgoods:
			if (productList.size() != 0) {
				mAdapterView.getRefreshableView().setSelection(1);
			}
			break;
		case R.id.rb_price:

			break;
		// rb_hot, rb_time, rb_grade, rb_book, rb_price
		case R.id.rb_hot:
			type = 6;
			showProgressDialog("加载中");
			productList.clear();
			// pics.clear();
			// vpData.clear();
			isend = false;
			pageIndex = 1;
			clothId = 0;
			fromprice = 0;
			endprice = 0;
			getData(type, clothId, 0, 0, pageIndex, 10);
			break;
		case R.id.rb_time:
			type = 1;
			productList.clear();
			// pics.clear();
			// vpData.clear();
			showProgressDialog("加载中");
			isend = false;
			pageIndex = 1;
			clothId = 0;
			fromprice = 0;
			endprice = 0;
			getData(type, clothId, 0, 0, pageIndex, 10);
			break;
		case R.id.rb_grade:
			type = 2;
			productList.clear();
			// pics.clear();
			// vpData.clear();
			clothId = 0;
			showProgressDialog("加载中");
			isend = false;
			pageIndex = 1;
			fromprice = 0;
			endprice = 0;
			getData(type, clothId, 0, 0, pageIndex, 10);
			break;
		case R.id.rb_book:
			showProgressDialog("加载中");
			type = 3;
			productList.clear();
			// pics.clear();
			// vpData.clear();
			isend = false;
			pageIndex = 1;
			clothId = 0;
			fromprice = 0;
			endprice = 0;
			System.out.println("zuo==type=" + type);
			getData(type, clothId, 0, 0, pageIndex, 10);
			break;
		// case R.id.tv_clicktomore:
		//
		// getMore();
		// break;
		case R.id.rl_menu_new:
			// if (ppw_one != null && ppw_one.isShowing()) {
			// ppw_one.dismiss();
			//
			// } else {
			// initPpwone();
			// ppw_one.showAsDropDown(v, 0, 0);
			//
			// }
			// Intent intent_filter = new Intent(getActivity(),
			// Activity_FilterCloth.class);
			// startActivity(intent_filter);
			if (islogin) {
				Intent intent_shopcar = new Intent(getActivity(), Activity_ShoppingCar.class);
				startActivity(intent_shopcar);
			} else {
				Intent intent = new Intent(getActivity(), Activity_Login.class);
				startActivity(intent);
			}
			break;
		case R.id.rl_sreach_new:
			Intent intent = new Intent(getActivity(), Activity_Search.class);
			startActivity(intent);
			break;
		case R.id.tv_alltype:
			Intent intent_alltype = new Intent(getActivity(), Activity_FilterCloth.class);
			startActivity(intent_alltype);
			break;
		case R.id.tv_sort:
			if (ppwsort != null && ppwsort.isShowing()) {
				// ppw_price.setFocusable(false);
				ppwsort.dismiss();
			} else {
				initPPwSort();
				ppwsort.showAsDropDown(rl_sort, 0, 1);
			}
			break;
		case R.id.rl_alltype:
			Intent intent_alltype_ = new Intent(getActivity(), Activity_FilterCloth.class);
			startActivity(intent_alltype_);
			break;
		case R.id.rl_sort:
			if (ppwsort != null && ppwsort.isShowing()) {
				// ppw_price.setFocusable(false);
				ppwsort.dismiss();
			} else {
				initPPwSort();
				ppwsort.showAsDropDown(v, 0, 1);
			}
			break;
		default:
			break;
		}

	}

	private void getMore() {

	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		System.out.println("zuo商城==hidden" + hidden);
		if (!hidden) {
			SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
			islogin = sp.getBoolean("islogin", false);
			userid = sp.getInt("userid", 0);

		} else {
			// MobclickAgent.onPageEnd("商城");
		}

	}

	private void initSmallDot(int index) {
		if (getActivity() != null) {
			dots_group.removeAllViews();
			for (int i = 0; i < pics.size(); i++) {
				ImageView imageView = new ImageView(getActivity());
				imageView.setImageResource(R.drawable.round);
				imageView.setPadding(5, 0, 5, 0);
				dots_group.addView(imageView);
			}
			if (pics.size() != 0) {
				// 设置选中项
				((ImageView) dots_group.getChildAt(index)).setImageResource(R.drawable.round_select);
			}

		}

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

	private void initVp() {
		final ArrayList<String> data = new ArrayList<String>();
		if (getActivity() != null) {
			for (int i = 0; i < pics.size(); i++) {
				cview = View.inflate(getActivity(), R.layout.vp_item, null);
				ImageView im_vp = (ImageView) cview.findViewById(R.id.im_vp);

				Glide.with(getActivity()).load(AppFinalUrl.photoBaseUri + pics.get(i).url)
						.override(2 * w, (int) (w * 5 * 2 / (double) 14))
						/*
						 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
						 */
						.crossFade().into(im_vp);

				// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
				// pics.get(i), im_vp, options);
				data.add(pics.get(i).url);
				vpData.add(cview);
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
			}
			vpadapter = new MyVPAdapter();
			vp_hot.setAdapter(vpadapter);
		}
		vp_hot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		LayoutParams layoutParams = vp_hot.getLayoutParams();
		layoutParams.height = (int) (w * 5 * 2 / (double) 14);
		vp_hot.setLayoutParams(layoutParams);
		LunBo();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// MobclickAgent.onPageEnd("商城");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeMessages(2);
		getActivity().unregisterReceiver(mbc);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	class MyGVAdapter extends BaseAdapter {

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
				h.im_firstpic = (ImageView) v.findViewById(R.id.im_firstpic);
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

			if (productList.get(position).price != null && !productList.get(position).price.equals("")
					&& !productList.get(position).price.equals("null")) {
				h.tv_price.setText("¥ " + ((int) Double.parseDouble(productList.get(position).price)));
			} else {
				h.tv_price.setText("¥ " + productList.get(position).price);
			}

			h.tv_topcount.setText("" + productList.get(position).topCount);
			h.im_firstpic.setVisibility(View.VISIBLE);
			h.im_head_show.setVisibility(View.GONE);
			// //
			// LayoutParams layoutParams = h.llceshi.getLayoutParams();
			// layoutParams.height = (int) (dp2px(getActivity(), 56) + w *
			// Integer.parseInt(productList.get(position).high)
			// / (double) Integer.parseInt(productList.get(position).wight));
			// layoutParams.width = w;
			// h.llceshi.setLayoutParams(layoutParams);

			if (w != 0 && productList.get(position).wight != null && productList.get(position).high != null
					&& !productList.get(position).wight.equals("") && !productList.get(position).high.equals("")
					&& Integer.parseInt(productList.get(position).wight) != 0
					&& Integer.parseInt(productList.get(position).high) != 0) {
				if (Integer.parseInt(productList.get(position).wight) > Integer.parseInt(productList.get(position).high)) {
					Glide.with(getActivity())
							.load(AppFinalUrl.photoBaseUri + productList.get(position).url + "-middle")
							.crossFade()
							.override(
									w,
									(int) (w * Integer.parseInt(productList.get(position).high) / (double) Integer
											.parseInt(productList.get(position).wight)))
							/*
							 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
							 */
							.crossFade().into(h.im_firstpic);
				} else {
					Glide.with(getActivity())
							.load(AppFinalUrl.photoBaseUri + productList.get(position).url + "-middle")
							.crossFade()
							.override(
									w,
									(int) (w * Integer.parseInt(productList.get(position).high) / (double) Integer
											.parseInt(productList.get(position).wight)))
							/*
							 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
							 */
							.placeholder(R.drawable.tianc).crossFade().into(h.im_firstpic);
					// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL
					// + productList.get(position).url, h.im_head_show,
					// options);
				}

				// Glide.with(getActivity()).load(AppFinalUrl.BASEURL +
				// productList.get(position).url).centerCrop().crossFade()
				//
				// .override(Integer.parseInt(productList.get(position).wight),
				// Integer.parseInt(productList.get(position).high))
				// /*
				// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
				// */
				// .placeholder(R.drawable.tianc).crossFade().into(h.im_head_show);
				// System.out.println("zuo====="
				// + w
				// + "     "
				// + (int) (w * Integer.parseInt(productList.get(position).high)
				// / (double) Integer.parseInt(productList
				// .get(position).wight)));
			} else {
				Glide.with(getActivity()).load(AppFinalUrl.photoBaseUri + productList.get(position).url + "-middle").centerCrop()
						.crossFade()
						/*
						 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
						 */
						.placeholder(R.drawable.tianc).crossFade().into(h.im_firstpic);
			}
			// if (w != 0 && Integer.parseInt(productList.get(position).wight)
			// != 0) {
			// RelativeLayout.LayoutParams params =
			// (RelativeLayout.LayoutParams) h.im_head_show.getLayoutParams();
			// params.height = (int) (w *
			// Integer.parseInt(productList.get(position).high) / (double)
			// Integer
			// .parseInt(productList.get(position).wight));
			// params.width = w;
			// h.im_head_show.setLayoutParams(params);
			// }
			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// productList.get(position).url, h.im_head_show, options);
			LayoutParams params = h.im_firstpic.getLayoutParams();
			params.height = picH;

			h.im_firstpic.setLayoutParams(params);
			return v;
		}
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	class MyHolder {
		TextView tv_topcount, tv_price, tv_username_fist, tv_goods_title;
		ScaleImageView im_head_show;
		LinearLayout llceshi;
		ImageView im_firstpic;
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

	public void closekey() {
		View view = getActivity().getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private void initPPwSort() {
		viewppwsort = View.inflate(mContext, R.layout.ppw_listview, null);
		ppwsort = new PopupWindow(viewppwsort, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		ppwsort.setFocusable(true);

		ppwsort.setBackgroundDrawable(new BitmapDrawable());
		ppwsort.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		ListView lv_ppw = (ListView) viewppwsort.findViewById(R.id.lv_ppw);
		LayoutParams params = lv_ppw.getLayoutParams();
		params.width = w;
		lv_ppw.setLayoutParams(params);
		PPListAdapter pplistadapter = new PPListAdapter();
		lv_ppw.setAdapter(pplistadapter);
		lv_ppw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ppwsort.dismiss();
				switch (position) {
				case 0:
					showProgressDialog("加载中");
					type = 3;
					productList.clear();
					// pics.clear();
					// vpData.clear();
					isend = false;
					pageIndex = 1;
					clothId = 0;
					fromprice = 0;
					endprice = 0;
					System.out.println("zuo==type=" + type);
					getData(type, clothId, 0, 0, pageIndex, 10);
					break;
				case 1:
					type = 6;
					showProgressDialog("加载中");
					productList.clear();
					// pics.clear();
					// vpData.clear();
					isend = false;
					pageIndex = 1;
					clothId = 0;
					fromprice = 0;
					endprice = 0;
					getData(type, clothId, 0, 0, pageIndex, 10);
					break;
				case 2:
					type = 1;
					productList.clear();
					// pics.clear();
					// vpData.clear();
					showProgressDialog("加载中");
					isend = false;
					pageIndex = 1;
					clothId = 0;
					fromprice = 0;
					endprice = 0;
					getData(type, clothId, 0, 0, pageIndex, 10);
					break;
				case 3:
					type = 2;
					productList.clear();
					// pics.clear();
					// vpData.clear();
					clothId = 0;
					showProgressDialog("加载中");
					isend = false;
					pageIndex = 1;
					fromprice = 0;
					endprice = 0;
					getData(type, clothId, 0, 0, pageIndex, 10);
					break;
				case 4:
					if (ppw_price != null && ppw_price.isShowing()) {
						// ppw_price.setFocusable(false);
						ppw_price.dismiss();
					} else {
						initPpwPrice();
						ppw_price.showAsDropDown(rl_sort, 0, 1);
					}
					break;

				default:
					break;
				}
			}
		});
	}

	class PPListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return postm == null ? 0 : postm.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return postm.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(getActivity(), R.layout.item_lv_popu, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_sort_name);
			tv_sort_name.setText("" + postm.get(position));
			return v;
		}

	}

	public void initPpwPrice() {
		customView = View.inflate(mContext, R.layout.ppwprice, null);
		ppw_price = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		ppw_price.setFocusable(true);
		ppw_price.setBackgroundDrawable(new BitmapDrawable());
		ppw_price.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		final EditText ed_fromp = (EditText) customView.findViewById(R.id.ed_fromp);
		final EditText ed_endp = (EditText) customView.findViewById(R.id.ed_endp);
		LinearLayout ll_seleprice = (LinearLayout) customView.findViewById(R.id.ll_seleprice);
		LayoutParams params = ll_seleprice.getLayoutParams();
		params.width = w;
		ll_seleprice.setLayoutParams(params);
		TextView tv_fromlow = (TextView) customView.findViewById(R.id.tv_fromlow);
		TextView tv_fromhight = (TextView) customView.findViewById(R.id.tv_fromhight);
		Button btn_sureprice = (Button) customView.findViewById(R.id.btn_sureprice);
		btn_sureprice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closekey();
				if (ed_endp.getText().toString().length() != 0) {
					productList.clear();
					// pics.clear();
					// vpData.clear();
					isend = false;
					pageIndex = 1;
					type = 5;
					fromprice = Integer.parseInt(ed_fromp.getText().toString());
					endprice = Integer.parseInt(ed_endp.getText().toString());
					getData(type, clothId, fromprice, endprice, pageIndex, 10);
					ppw_price.dismiss();
					showProgressDialog("加载中");
				}
			}
		});
		tv_fromlow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				productList.clear();
				// pics.clear();
				// vpData.clear();
				type = 5;
				isend = false;
				pageIndex = 1;
				fromprice = 0;
				endprice = 0;
				getData(type, clothId, 0, 0, pageIndex, 10);
				ppw_price.dismiss();
				showProgressDialog("加载中");
			}
		});
		tv_fromhight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				productList.clear();
				// pics.clear();
				// vpData.clear();
				type = 4;
				isend = false;
				pageIndex = 1;
				fromprice = 0;
				endprice = 0;
				getData(type, clothId, 0, 0, pageIndex, 10);
				ppw_price.dismiss();
				showProgressDialog("加载中");
			}
		});
	}

	class ProductList {
		String title, price, userName, createDate, url, high, wight;
		int status, userId, topCount, productId;

		public ProductList(String title, String price, String userName, String createDate, String url, int status, int userId,
				int topCount, int productId, String high, String wight) {
			super();
			this.title = title;
			this.price = price;
			this.userName = userName;
			this.createDate = createDate;
			this.url = url;
			this.status = status;
			this.userId = userId;
			this.topCount = topCount;
			this.productId = productId;
			this.high = high;
			this.wight = wight;
		}

	}

	// @Override
	// public void onItemClick(PLA_AdapterView<?> parent, View view, int
	// position, long id) {
	// // TODO Auto-generated method stub
	// SharedPreferences sp = getActivity().getSharedPreferences("Login",
	// Context.MODE_PRIVATE);
	// islogin = sp.getBoolean("islogin", false);
	// userid = sp.getInt("userid", 0);
	// Intent intent = new Intent(getActivity(), Activity_GoodsDetails.class);
	// intent.putExtra("userid", userid);
	// intent.putExtra("title", productList.get(position - 1).title);
	// intent.putExtra("productId", productList.get(position - 1).productId);
	// startActivity(intent);
	// }

	@Override
	public void onRefresh() {
		// productList.clear();
		// // pics.clear();
		// // vpData.clear();
		// isend = false;
		// pageIndex = 1;
		// getData(type, clothId, fromprice, endprice, pageIndex, 10);
	}

	private RelativeLayout rl_designer, rl_newgoods, rl_menu, rl_sreach;
	private ImageView im_right, im_left;
	private Fragment_NewGoods fnew;
	private Fragment_Designer fdesi;
	private FragmentTransaction transaction;
	private PopupWindow ppw_one, ppw_two;
	private View customViewone, customViewtwo;
	private ListView lv_ppwtow;
	private MyPopuLVAdapter plvadapter;
	private AlertDialog serachdialog;
	private List<ProductJson> productJson;
	private int sex = 2, ctype = 3;
	private List<CothTypy> coths;

	public void initPpwone() {
		customViewone = View.inflate(getActivity(), R.layout.popu_newgoodsone, null);
		lv_ppwtow = (ListView) customViewone.findViewById(R.id.lv_poputwo);

		lv_ppwtow.setAdapter(plvadapter);
		ppw_one = new PopupWindow(customViewone, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		ppw_one.setFocusable(true);
		ppw_one.setBackgroundDrawable(new BitmapDrawable());
		lv_ppwtow.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("clothId", coths.get(position).cotherId);
				intent.setAction("com.search.cloth");
				getActivity().sendBroadcast(intent);
				ppw_one.dismiss();
			}
		});
		RelativeLayout rl_woman = (RelativeLayout) customViewone.findViewById(R.id.rl_woman);
		RelativeLayout rl_man = (RelativeLayout) customViewone.findViewById(R.id.rl_man);
		RelativeLayout rl_up = (RelativeLayout) customViewone.findViewById(R.id.rl_up);
		RelativeLayout rl_down = (RelativeLayout) customViewone.findViewById(R.id.rl_down);
		final ImageView im_women = (ImageView) customViewone.findViewById(R.id.im_women);
		final ImageView im_man = (ImageView) customViewone.findViewById(R.id.im_man);
		final ImageView im_up = (ImageView) customViewone.findViewById(R.id.im_up);
		final ImageView im_down = (ImageView) customViewone.findViewById(R.id.im_down);
		rl_woman.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sex = 2;
				im_women.setVisibility(View.VISIBLE);
				im_man.setVisibility(View.INVISIBLE);
				// coths =
				// productJson.get(sex).smallProductJy.get(ctype).cothTypy;

				for (int i = 0; i < productJson.size(); i++) {
					if (productJson.get(i).sexId == sex) {
						for (int j = 0; j < productJson.get(i).smallProductJy.size(); j++) {
							if (productJson.get(i).smallProductJy.get(j).sortId == ctype) {
								coths = productJson.get(i).smallProductJy.get(j).cothTypy;
								plvadapter.notifyDataSetChanged();
							}
						}
					}

				}

			}
		});
		rl_man.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sex = 1;
				im_man.setVisibility(View.VISIBLE);
				im_women.setVisibility(View.INVISIBLE);
				// coths =
				// productJson.get(sex).smallProductJy.get(ctype).cothTypy;

				for (int i = 0; i < productJson.size(); i++) {
					if (productJson.get(i).sexId == sex) {
						for (int j = 0; j < productJson.get(i).smallProductJy.size(); j++) {
							if (productJson.get(i).smallProductJy.get(j).sortId == ctype) {
								coths = productJson.get(i).smallProductJy.get(j).cothTypy;
								plvadapter.notifyDataSetChanged();
							}
						}
					}

				}

			}
		});
		rl_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sex == 1) {
					ctype = 1;
				} else {
					ctype = 3;
				}

				im_up.setVisibility(View.VISIBLE);
				im_down.setVisibility(View.INVISIBLE);
				// coths =
				// productJson.get(sex).smallProductJy.get(ctype).cothTypy;

				for (int i = 0; i < productJson.size(); i++) {
					if (productJson.get(i).sexId == sex) {
						for (int j = 0; j < productJson.get(i).smallProductJy.size(); j++) {
							if (productJson.get(i).smallProductJy.get(j).sortId == ctype) {
								coths = productJson.get(i).smallProductJy.get(j).cothTypy;
								plvadapter.notifyDataSetChanged();
							}
						}
					}

				}

			}
		});
		rl_down.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ctype = 1;
				if (sex == 1) {
					ctype = 2;
				} else {
					ctype = 4;
				}
				im_down.setVisibility(View.VISIBLE);
				im_up.setVisibility(View.INVISIBLE);
				// coths =
				// productJson.get(sex).smallProductJy.get(ctype).cothTypy;
				for (int i = 0; i < productJson.size(); i++) {
					if (productJson.get(i).sexId == sex) {
						for (int j = 0; j < productJson.get(i).smallProductJy.size(); j++) {
							if (productJson.get(i).smallProductJy.get(j).sortId == ctype) {
								coths = productJson.get(i).smallProductJy.get(j).cothTypy;
								plvadapter.notifyDataSetChanged();
							}
						}
					}

				}

			}
		});

	};

	// public void initPpwtwo() {
	// customViewtwo = View.inflate(getActivity(), R.layout.popu_newgoodstwo,
	// null);
	// lv_ppwtow = (ListView) customViewtwo.findViewById(R.id.lv_poputwo);
	// plvadapter = new MyPopuLVAdapter();
	// lv_ppwtow.setAdapter(plvadapter);
	// ppw_two = new PopupWindow(customViewtwo, LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT, false);
	// ppw_two.setFocusable(true);
	// ppw_two.setBackgroundDrawable(new BitmapDrawable());
	// };

	class MyPopuLVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return coths == null ? 0 : coths.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return coths.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			v = View.inflate(getActivity(), R.layout.item_lv_popu, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_sort_name);
			tv_sort_name.setText("" + coths.get(position).cotherName);
			return v;
		}
	}

	public void getData() {

		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetShowSort, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);

				try {
					JSONArray jsonArray = response.getJSONArray("productJson");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						int sexId = j.optInt("sexId");
						String sexName = j.optString("sexName");
						List<SmallProductJy> smallProductJy = new ArrayList<SmallProductJy>();
						JSONArray optJSONArray = j.optJSONArray("smallProductJy");
						for (int k = 0; k < optJSONArray.length(); k++) {
							JSONObject jj = optJSONArray.getJSONObject(k);
							int sortId = jj.optInt("sortId");
							String sortName = jj.optString("sortName");
							List<CothTypy> cothTypy = new ArrayList<CothTypy>();
							JSONArray optJSONArray2 = jj.optJSONArray("cothTypy");
							for (int l = 0; l < optJSONArray2.length(); l++) {
								JSONObject jjj = optJSONArray2.getJSONObject(l);
								int cotherId = jjj.optInt("cotherId");
								String cotherName = jjj.optString("cotherName");
								cothTypy.add(new CothTypy(cotherId, cotherName));
							}
							smallProductJy.add(new SmallProductJy(sortId, sortName, cothTypy));
						}
						productJson.add(new ProductJson(sexId, sexName, smallProductJy));
					}
					for (int i = 0; i < productJson.size(); i++) {
						if (productJson.get(i).sexId == 2) {
							for (int j = 0; j < productJson.get(i).smallProductJy.size(); j++) {
								if (productJson.get(i).smallProductJy.get(j).sortId == 3) {
									coths = productJson.get(i).smallProductJy.get(j).cothTypy;
									plvadapter.notifyDataSetChanged();
								}
							}
						}

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class ProductJson {
		int sexId;
		String sexName;
		List<SmallProductJy> smallProductJy;

		public ProductJson(int sexId, String sexName, List<SmallProductJy> smallProductJy) {
			super();
			this.sexId = sexId;
			this.sexName = sexName;
			this.smallProductJy = smallProductJy;
		}

	}

	class SmallProductJy {
		int sortId;
		String sortName;
		List<CothTypy> cothTypy;

		public SmallProductJy(int sortId, String sortName, List<CothTypy> cothTypy) {
			super();
			this.sortId = sortId;
			this.sortName = sortName;
			this.cothTypy = cothTypy;
		}
	}

	class CothTypy {
		int cotherId;
		String cotherName;

		public CothTypy(int cotherId, String cotherName) {
			super();
			this.cotherId = cotherId;
			this.cotherName = cotherName;
		}

	}

	public void onResume() {
		super.onResume();
		// MobclickAgent.onPageStart("商城"); // 统计页面，"MainScreen"为页面名称，可自定义
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
		islogin = sp.getBoolean("islogin", false);
		userid = sp.getInt("userid", 0);
		Intent intent = new Intent(getActivity(), Activity_GoodsDetails.class);
		intent.putExtra("userid", userid);
		intent.putExtra("title", productList.get(position - 2).title);
		intent.putExtra("productId", productList.get(position - 2).productId);
		startActivity(intent);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		productList.clear();
		// pics.clear();
		// vpData.clear();
		isend = false;
		pageIndex = 1;
		fromprice = 0;
		endprice = 0;
		getData(type, clothId, fromprice, endprice, pageIndex, 10);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			// sv_hot.onRefreshComplete();
			Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(3);
		} else {
			getData(type, clothId, fromprice, endprice, pageIndex, 10);
		}
	}

}
