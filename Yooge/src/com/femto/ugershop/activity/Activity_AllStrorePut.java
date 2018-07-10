package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_AllStrorePut extends SwipeBackActivity implements OnRefreshListener2<ListView> {
	private RelativeLayout rl_back_allstartput, rl_puted, rl_waitput, rl_putdown;
	private MylVAdapter lvadapter;
	private PullToRefreshListView lv_put;
	private View customView;
	private PopupWindow ppw_price;
	private RelativeLayout rl_sele_data;
	private boolean isstarttime = true;
	private String starttime = "", endtime = "";
	private int year;
	private int month;
	private int day;
	private TextView tv_starttime;
	private TextView tv_endtime;
	private int myId;
	private DisplayImageOptions options;
	private List<ProductUp> productUp;
	private int statu = 1;
	private int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private boolean isfirstbanner = true;
	private TextView tv_a1, tv_a2, tv_a3;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_put.onRefreshComplete();
				break;

			default:
				break;
			}

		}

	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_allstartput:
			finish();
			break;
		case R.id.rl_puted:
			statu = 1;
			type = 0;
			productUp.clear();
			starttime = "";
			endtime = "";
			getData(statu, type, "", "", 1, pageSize);
			showTV(tv_a1, tv_a2, tv_a3);
			break;
		case R.id.rl_putdown:
			statu = 3;
			type = 0;
			productUp.clear();
			starttime = "";
			endtime = "";
			getData(statu, type, "", "", 1, pageSize);
			showTV(tv_a3, tv_a2, tv_a1);
			break;
		case R.id.rl_waitput:
			statu = 4;
			type = 0;
			productUp.clear();
			starttime = "";
			endtime = "";
			getData(statu, type, "", "", 1, pageSize);
			showTV(tv_a2, tv_a1, tv_a3);
			break;
		case R.id.rl_sele_data:
			if (ppw_price != null && ppw_price.isShowing()) {
				ppw_price.dismiss();
			} else {
				initPpwPrice();
				ppw_price.showAsDropDown(v, 0, 1);
			}
			break;

		default:
			break;
		}
	}

	private void showTV(TextView ts, TextView tn1, TextView tn2) {
		ts.setVisibility(View.VISIBLE);
		tn1.setVisibility(View.INVISIBLE);
		tn2.setVisibility(View.INVISIBLE);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		MyApplication.addActivity(this);
		productUp = new ArrayList<ProductUp>();
		rl_back_allstartput = (RelativeLayout) findViewById(R.id.rl_back_allstartput);
		rl_puted = (RelativeLayout) findViewById(R.id.rl_puted);
		rl_waitput = (RelativeLayout) findViewById(R.id.rl_waitput);
		rl_putdown = (RelativeLayout) findViewById(R.id.rl_putdown);
		lv_put = (PullToRefreshListView) findViewById(R.id.lv_put);
		tv_a1 = (TextView) findViewById(R.id.tv_a1);
		tv_a2 = (TextView) findViewById(R.id.tv_a2);
		tv_a3 = (TextView) findViewById(R.id.tv_a3);
		lv_put.setOnRefreshListener(this);
		lv_put.setMode(Mode.BOTH);
		rl_sele_data = (RelativeLayout) findViewById(R.id.rl_sele_data);
		lvadapter = new MylVAdapter();
		lv_put.setAdapter(lvadapter);
		getData(1, 0, "", "", 1, pageSize);
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
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetProductUp, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo==response=" + response.toString());
				dismissProgressDialog();

				try {
					JSONArray jsonArray = response.getJSONArray("List");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String productUrl = j.optString("productUrl");
						String createDate = j.optString("createDate");
						String productName = j.optString("productName");
						int productId = j.optInt("productId");
						int status = j.optInt("status");
						productUp.add(new ProductUp(productUrl, createDate, productName, productId, status));
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					lv_put.onRefreshComplete();
					lvadapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class ProductUp {
		String productUrl, createDate, productName;

		int productId, status;

		public ProductUp(String productUrl, String createDate, String productName, int productId, int status) {
			super();
			this.productUrl = productUrl;
			this.createDate = createDate;
			this.productName = productName;
			this.productId = productId;
			this.status = status;
		}

	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_allstartput.setOnClickListener(this);
		rl_puted.setOnClickListener(this);
		rl_waitput.setOnClickListener(this);
		rl_putdown.setOnClickListener(this);
		rl_sele_data.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_allstoreput);
		initParams();
		Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
		Date mydate = new Date(); // 获取当前日期Date对象
		mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期
		year = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年
		month = mycalendar.get(Calendar.MONTH);// 获取Calendar对象中的月
		day = mycalendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
	}

	class MylVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return productUp == null ? 0 : productUp.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return productUp.get(position);
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
				v = View.inflate(Activity_AllStrorePut.this, R.layout.item_lv_put, null);
				h.im_storeput = (ImageView) v.findViewById(R.id.im_storeput);
				h.tv_spname = (TextView) v.findViewById(R.id.tv_spname);
				h.tv_sptime = (TextView) v.findViewById(R.id.tv_sptime);
				h.tv_spstate = (TextView) v.findViewById(R.id.tv_spstate);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_spname.setText("" + productUp.get(position).productName);
			h.tv_sptime.setText("" + productUp.get(position).createDate);
			if (productUp.get(position).status == 0) {
				h.tv_spstate.setText("待审核");
			}
			if (productUp.get(position).status == 1) {
				h.tv_spstate.setText("已上架");
			}
			if (productUp.get(position).status == 3) {
				h.tv_spstate.setText("已下架");
			}
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + productUp.get(position).productUrl + "-small",
					h.im_storeput, options);
			return v;
		}

	}

	class MyHolder {
		ImageView im_storeput;
		TextView tv_spname, tv_sptime, tv_spstate;
	}

	public void initPpwPrice() {
		customView = View.inflate(Activity_AllStrorePut.this, R.layout.popuseledata, null);
		ppw_price = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		ppw_price.setFocusable(true);
		ppw_price.setBackgroundDrawable(new BitmapDrawable());

		ppw_price.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		RelativeLayout rl_starttime = (RelativeLayout) customView.findViewById(R.id.rl_starttime);
		RelativeLayout rl_endtime = (RelativeLayout) customView.findViewById(R.id.rl_endtime);
		TextView tv_month = (TextView) customView.findViewById(R.id.tv_month);
		TextView tv_week = (TextView) customView.findViewById(R.id.tv_week);
		Button btn_sure = (Button) customView.findViewById(R.id.btn_sure);
		tv_starttime = (TextView) customView.findViewById(R.id.tv_starttime);
		tv_endtime = (TextView) customView.findViewById(R.id.tv_endtime);
		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				type = 1;
				productUp.clear();
				pageIndex = 1;
				getData(statu, type, starttime, endtime, 1, pageSize);
				ppw_price.dismiss();
			}
		});
		tv_week.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				type = 2;
				productUp.clear();
				pageIndex = 1;
				starttime = "";
				endtime = "";
				getData(statu, type, "", "", 1, pageSize);
				ppw_price.dismiss();
			}
		});

		tv_month.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				type = 3;
				productUp.clear();
				pageIndex = 1;
				starttime = "";
				endtime = "";
				getData(statu, type, "", "", 1, pageSize);
				ppw_price.dismiss();
			}
		});
		rl_starttime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDataDialog();
				isstarttime = true;
			}
		});
		rl_endtime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDataDialog();
				isstarttime = false;
			}
		});
	}

	private void showDataDialog() {
		DatePickerDialog dpd = new DatePickerDialog(Activity_AllStrorePut.this, Datelistener, year, month, day);
		dpd.show();
	}

	private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {

		/**
		 * params：view：该事件关联的组件 params：myyear：当前选择的年 params：monthOfYear：当前选择的月
		 * params：dayOfMonth：当前选择的日
		 */
		@Override
		public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {

			// 修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
			year = myyear;
			month = monthOfYear;
			day = dayOfMonth;
			// 更新日期
			updateDate();

		}

		// 当DatePickerDialog关闭时，更新日期显示
		private void updateDate() {
			// 在TextView上显示日期
			// birthDay = year + "-" + (month + 1) + "-" + day;
			// tv_birthday.setText(year + "-" + (month + 1) + "-" + day);
			if (isstarttime) {
				starttime = year + "-" + (month + 1) + "-" + day;
				tv_starttime.setText(starttime);
			} else {
				endtime = year + "-" + (month + 1) + "-" + day;
				tv_endtime.setText(endtime);
			}
		}
	};
	private int type = 0;

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
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
		productUp.clear();
		getData(statu, type, starttime, endtime, 1, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			handler.sendEmptyMessage(1);
			Toast.makeText(this, "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getData(statu, type, starttime, endtime, pageIndex, pageSize);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("商城上架");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("商城上架");
	}
}
