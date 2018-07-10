package com.femto.ugershop.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.fragment.Fragment_CustomDetails;
import com.femto.ugershop.fragment.Fragment_StoreDetails;
import com.umeng.analytics.MobclickAgent;

public class Activity_Earnings extends SwipeBackActivity {

	private RelativeLayout rl_back_omg;
	private RelativeLayout rl_designer, rl_newgoods;
	private ListView lv_details;
	private int type = 1;// type=1时为商城明细，type=2时为定制明细
	private View customView;
	private PopupWindow ppw_price;
	private RelativeLayout rl_datasele;
	private FrameLayout fl_contain;
	private Fragment_StoreDetails fsd;
	private Fragment_CustomDetails fcd;
	private FragmentTransaction beginTransaction;
	private boolean isstarttime;
	private boolean isstoreorder = true;
	private String starttime;
	private String endtime;
	private int year;
	private int month;
	private int day;
	private TextView tv_starttime;
	private TextView tv_endtime;
	private TextView tv_s, tv_c;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_omg:
			finish();
			break;
		case R.id.rl_datasele:
			if (ppw_price != null && ppw_price.isShowing()) {
				ppw_price.dismiss();
			} else {
				initPpwPrice();
				ppw_price.showAsDropDown(v, 0, 1);
			}
			break;
		case R.id.rl_newgoods1:
			isstoreorder = true;
			fragmentShowOrHide(fsd, fcd, false);
			showT(tv_s, tv_c);
			break;
		case R.id.rl_designer1:
			isstoreorder = false;
			fragmentShowOrHide(fcd, fsd, false);
			showT(tv_c, tv_s);
			break;

		default:
			break;
		}
	}

	private void showT(TextView tshow, TextView noshow) {
		tshow.setVisibility(View.VISIBLE);
		noshow.setVisibility(View.INVISIBLE);

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
		MyApplication.addActivity(this);
		Date mydate = new Date(); // 获取当前日期Date对象
		mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期
		year = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年
		month = mycalendar.get(Calendar.MONTH);// 获取Calendar对象中的月
		day = mycalendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
		rl_back_omg = (RelativeLayout) findViewById(R.id.rl_back_omg);

		rl_newgoods = (RelativeLayout) findViewById(R.id.rl_newgoods1);
		rl_designer = (RelativeLayout) findViewById(R.id.rl_designer1);
		rl_datasele = (RelativeLayout) findViewById(R.id.rl_datasele);
		tv_s = (TextView) findViewById(R.id.tv_s);
		tv_c = (TextView) findViewById(R.id.tv_c);
		rl_designer.setOnClickListener(this);
		rl_newgoods.setOnClickListener(this);
		rl_datasele.setOnClickListener(this);
		fl_contain = (FrameLayout) findViewById(R.id.fl_contain);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_omg.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_ordermg);
		initFragment();
	}

	private void initFragment() {
		beginTransaction = getSupportFragmentManager().beginTransaction();
		fsd = new Fragment_StoreDetails();
		fcd = new Fragment_CustomDetails();
		beginTransaction.add(R.id.fl_contain, fsd);
		beginTransaction.add(R.id.fl_contain, fcd);
		fragmentShowOrHide(fsd, fcd, true);
	}

	private void fragmentShowOrHide(Fragment showFragment, Fragment hideFragment1, boolean isInit) {
		if (!isInit) {
			beginTransaction = getSupportFragmentManager().beginTransaction();
		}
		beginTransaction.show(showFragment);
		beginTransaction.hide(hideFragment1);
		beginTransaction.commit();
	}

	public void initPpwPrice() {
		customView = View.inflate(Activity_Earnings.this, R.layout.popuseledata, null);
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
				// getData(statu, 1, starttime, endtime);
				ppw_price.dismiss();
				if (isstoreorder) {
					Intent intent = new Intent();
					intent.putExtra("starttime", starttime);
					intent.putExtra("endtime", endtime);
					intent.putExtra("type", 1);
					intent.setAction("com.search.storeoder.info");
					sendBroadcast(intent);
				} else {
					Intent intent = new Intent();
					intent.putExtra("starttime", starttime);
					intent.putExtra("endtime", endtime);
					intent.putExtra("type", 1);
					intent.setAction("com.search.customoder.info");
					sendBroadcast(intent);
				}

			}
		});
		tv_week.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// getData(statu, 2, "", "");
				if (isstoreorder) {
					Intent intent = new Intent();
					intent.putExtra("type", 2);
					intent.setAction("com.search.storeoder.info");
					sendBroadcast(intent);
					System.out.println("zuo发出一周广播");
				} else {
					Intent intent = new Intent();
					intent.putExtra("type", 2);
					intent.setAction("com.search.customoder.info");
					sendBroadcast(intent);
					System.out.println("zuoweek");

				}
				ppw_price.dismiss();
			}
		});

		tv_month.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// getData(statu, 3, "", "");
				if (isstoreorder) {
					Intent intent = new Intent();
					intent.putExtra("type", 3);
					intent.setAction("com.search.storeoder.info");
					sendBroadcast(intent);
					System.out.println("zuo发出一月广播");
				} else {
					Intent intent = new Intent();
					intent.putExtra("type", 3);
					intent.setAction("com.search.customoder.info");
					sendBroadcast(intent);
					System.out.println("zuomonth");
				}
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
		DatePickerDialog dpd = new DatePickerDialog(Activity_Earnings.this, Datelistener, year, month, day);
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("收益明细");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("收益明细");
	}
}
