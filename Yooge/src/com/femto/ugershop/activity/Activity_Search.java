package com.femto.ugershop.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AbsListView.OnScrollListener;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.sample.ScaleImageView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Search extends BaseActivity implements com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener {
	private RelativeLayout rl_back_search;
	private int w;
	private DisplayImageOptions options;
	private int width;
	private int height;
	private EditText ed_searchname;
	private MyGVAdapter adapter;
	private List<String> pics;
	private List<ProductList> productList;
	private boolean isfirst = false;
	// private GridView gv_search;
	private TextView tv_search;
	private boolean isloading = false;
	private boolean isend = false;
	private MultiColumnListView list_vp_search;
	int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private int clothId = 0;
	private RadioButton rb_book_s, rb_hot_s, rb_time_s, rb_price_s, rb_grade_s;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			adapter.notifyDataSetChanged();
		};
	};
	private int userid;
	private boolean islogin;
	private int type = 0;
	private int fromprice;
	private int endprice;
	private PopupWindow ppw_price;
	private View customView;
	private Button btn_change;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("搜索商品");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("搜索商品");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_search:
			finish();
			break;
		case R.id.tv_search:
			if (ed_searchname.getText().toString().length() == 0) {
				Toast.makeText(Activity_Search.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
			} else {
				showProgressDialog("加载中");
				productList.clear();
				pageIndex = 1;
				fromprice = 0;
				endprice = 0;
				getData(type, 0, ed_searchname.getText().toString().trim(), 1, 10, fromprice, endprice);
			}
			break;
		// rb_book_s, rb_hot_s, rb_time_s, rb_price_s, rb_grade_s
		case R.id.rb_book_s:
			if (ed_searchname.getText().toString().trim().length() != 0) {
				type = 3;
				productList.clear();
				pageIndex = 1;
				fromprice = 0;
				endprice = 0;
				showProgressDialog("加载中");
				getData(type, 0, ed_searchname.getText().toString().trim(), pageIndex, pageSize, fromprice, endprice);
			}

			break;
		case R.id.rb_time_s:
			if (ed_searchname.getText().toString().trim().length() != 0) {
				type = 1;
				productList.clear();
				pageIndex = 1;
				fromprice = 0;
				endprice = 0;
				showProgressDialog("加载中");
				getData(type, 0, ed_searchname.getText().toString().trim(), pageIndex, pageSize, fromprice, endprice);
			}

			break;
		case R.id.rb_hot_s:
			if (ed_searchname.getText().toString().trim().length() != 0) {
				type = 6;
				productList.clear();
				pageIndex = 1;
				fromprice = 0;
				endprice = 0;
				showProgressDialog("加载中");
				getData(type, 0, ed_searchname.getText().toString().trim(), pageIndex, pageSize, fromprice, endprice);
			}

			break;

		case R.id.rb_grade_s:
			if (ed_searchname.getText().toString().trim().length() != 0) {
				type = 2;
				productList.clear();
				pageIndex = 1;
				fromprice = 0;
				endprice = 0;
				showProgressDialog("加载中");
				getData(type, 0, ed_searchname.getText().toString().trim(), pageIndex, pageSize, fromprice, endprice);
			}

			break;
		case R.id.rb_price_s:
			if (ed_searchname.getText().toString().trim().length() != 0) {
				if (ppw_price != null && ppw_price.isShowing()) {
					// ppw_price.setFocusable(false);
					ppw_price.dismiss();
				} else {
					initPpwPrice();

					ppw_price.showAsDropDown(v, 0, 1);
				}
			}

			break;
		case R.id.btn_change:
			// //pla:plaLandscapeColumnNumber
			// list_vp_search.
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		MyApplication.addActivity(this);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		pics = new ArrayList<String>();
		productList = new ArrayList<ProductList>();
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(this, 4);
		w = (screenWidth - dp2px) / 2;
		// TODO Auto-generated method stub
		// rb_book_s, rb_hot_s, rb_time_s, rb_price_s, rb_grade_s
		rb_book_s = (RadioButton) findViewById(R.id.rb_book_s);
		rb_hot_s = (RadioButton) findViewById(R.id.rb_hot_s);
		rb_time_s = (RadioButton) findViewById(R.id.rb_time_s);
		rb_price_s = (RadioButton) findViewById(R.id.rb_price_s);
		rb_grade_s = (RadioButton) findViewById(R.id.rb_grade_s);
		rl_back_search = (RelativeLayout) findViewById(R.id.rl_back_search);
		// gv_search = (GridView) findViewById(R.id.gv_search);
		ed_searchname = (EditText) findViewById(R.id.ed_searchname);
		tv_search = (TextView) findViewById(R.id.tv_search);
		btn_change = (Button) findViewById(R.id.btn_change);
		adapter = new MyGVAdapter();
		list_vp_search = (MultiColumnListView) findViewById(R.id.list_vp_search);
		// gv_search.setAdapter(adapter);
		// gv_search.setOnItemClickListener(this);
		list_vp_search.setAdapter(adapter);
		list_vp_search.setOnItemClickListener(this);
		list_vp_search.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				int lastItem = firstVisibleItem + visibleItemCount;
				if (ed_searchname.getText().toString().trim().length() == 0) {

				} else {
					if (lastItem == totalItemCount) {

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
								// tv_loading.setVisibility(View.VISIBLE);
								getData(type, 0, ed_searchname.getText().toString().trim(), pageIndex, pageSize, fromprice,
										endprice);
							}
						}

					}

				}

			}
		});
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_search.setOnClickListener(this);
		tv_search.setOnClickListener(this);
		rb_book_s.setOnClickListener(this);
		rb_hot_s.setOnClickListener(this);
		rb_time_s.setOnClickListener(this);
		rb_price_s.setOnClickListener(this);
		rb_grade_s.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_search);
	}

	private void getData(int type, int sortid, String name, int pageIndex1, int pageSize, int downPrice, int highPrice) {

		RequestParams params = new RequestParams();
		params.put("type", type);
		params.put("product.sort.id", sortid);
		params.put("product.productName", name);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		if (highPrice != 0) {
			params.put("product.downPrice", downPrice);
			params.put("product.highPrice", highPrice);
		}
		System.out.println("zuo=params=" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetAppShow, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();

				System.out.println("zuo=response=" + response.toString());
				try {
					JSONArray jsonArray = response.getJSONArray("bannerList");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						pics.add(j.optString("url"));
					}

					JSONArray jsonArray2 = response.getJSONArray("productList");
					for (int i = 0; i < jsonArray2.length(); i++) {
						JSONObject j = jsonArray2.getJSONObject(i);
						String title = j.optString("title");
						String price = j.optString("price");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String url = j.optString("url");
						int status = j.optInt("status");
						int userId = j.optInt("userId");
						int topCount = j.optInt("topCount");
						int productId = j.optInt("productId");
						size++;
						productList.add(new ProductList(title, price, userName, createDate, url, status, userId, topCount,
								productId));
					}
					if (size == 10) {
						isend = false;
						pageIndex++;
					} else {
						isend = true;
					}
					size = 0;
					isloading = false;
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
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
				v = View.inflate(Activity_Search.this, R.layout.item_gv_hot, null);
				h.im_head_show = (ScaleImageView) v.findViewById(R.id.im_head_show);
				h.tv_goods_title = (TextView) v.findViewById(R.id.tv_goods_title);
				h.tv_price = (TextView) v.findViewById(R.id.tv_price_new1);
				h.tv_topcount = (TextView) v.findViewById(R.id.tv_topcount);
				h.tv_username_fist = (TextView) v.findViewById(R.id.tv_username_fist);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}

			h.tv_goods_title.setText(productList.get(position).title);
			h.tv_price.setText("" + productList.get(position).price);
			h.tv_topcount.setText("" + productList.get(position).topCount);
			h.tv_username_fist.setText(productList.get(position).userName);

			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + productList.get(position).url, h.im_head_show,
					options);

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
	}

	class ProductList {
		String title, price, userName, createDate, url;
		int status, userId, topCount, productId;

		public ProductList(String title, String price, String userName, String createDate, String url, int status, int userId,
				int topCount, int productId) {
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

	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// SharedPreferences sp = getSharedPreferences("Login",
	// Context.MODE_PRIVATE);
	// islogin = sp.getBoolean("islogin", false);
	// userid = sp.getInt("userid", 0);
	// Intent intent = new Intent(this, Activity_GoodsDetails.class);
	// intent.putExtra("userid", userid);
	// intent.putExtra("title", productList.get(position).title);
	// intent.putExtra("productId", productList.get(position).productId);
	// startActivity(intent);
	// }

	@Override
	public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		islogin = sp.getBoolean("islogin", false);
		userid = sp.getInt("userid", 0);
		Intent intent = new Intent(this, Activity_GoodsDetails.class);
		intent.putExtra("userid", userid);
		intent.putExtra("title", productList.get(position).title);
		intent.putExtra("productId", productList.get(position).productId);
		startActivity(intent);
	}

	public void initPpwPrice() {
		customView = View.inflate(this, R.layout.ppwprice, null);
		ppw_price = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		ppw_price.setFocusable(true);
		ppw_price.setBackgroundDrawable(new BitmapDrawable());
		LinearLayout ll_seleprice = (LinearLayout) customView.findViewById(R.id.ll_seleprice);
		LayoutParams params = ll_seleprice.getLayoutParams();
		params.width = (int) (getWith() / 2.0);
		ll_seleprice.setLayoutParams(params);
		ppw_price.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		final EditText ed_fromp = (EditText) customView.findViewById(R.id.ed_fromp);
		final EditText ed_endp = (EditText) customView.findViewById(R.id.ed_endp);

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
					getData(type, 0, ed_searchname.getText().toString().trim(), pageIndex, pageSize, fromprice, endprice);
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
				getData(type, 0, ed_searchname.getText().toString().trim(), pageIndex, pageSize, fromprice, endprice);
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
				getData(type, 0, ed_searchname.getText().toString().trim(), pageIndex, pageSize, fromprice, endprice);
				ppw_price.dismiss();
				showProgressDialog("加载中");
			}
		});
	}

	public void closekey() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
}
