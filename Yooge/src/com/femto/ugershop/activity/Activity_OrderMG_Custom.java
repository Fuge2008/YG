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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.fragment.Fragment_CustomOrderCustom;
import com.femto.ugershop.fragment.Fragment_NewCustomOrder;
import com.femto.ugershop.fragment.Fragment_StoreCustom;
import com.umeng.analytics.MobclickAgent;

public class Activity_OrderMG_Custom extends BaseActivity {
	private RelativeLayout rl_newgoods1_c, rl_designer1_c, rl_back_omg_c, rl_datasele_cdd;

	private Fragment_NewCustomOrder fcoc;
	private Fragment_StoreCustom fsc;
	private FragmentTransaction transaction;
	private View customView;
	private PopupWindow ppw_price;
	private TextView tv_starttime;
	private TextView tv_endtime, tv_sorder, tv_corder;
	private boolean isstarttime;
	private String starttime;
	private String endtime;
	private int year;
	private int month;
	private int day;
	private boolean isstoreorder = true;
	private int typetime = 0;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_newgoods1_c:
			isstoreorder = true;
			// im_left1_c.setBackgroundResource(R.drawable.frame_select_left);
			// im_right1_c.setBackgroundColor(Color.TRANSPARENT);
			fragmentShowOrHide(fsc, fcoc, false);
			showText(tv_sorder, tv_corder);
			break;
		case R.id.rl_back_omg_c:
			finish();
			break;
		case R.id.rl_designer1_c:
			isstoreorder = false;
			// im_right1_c.setBackgroundResource(R.drawable.frame_select_righ);
			// im_left1_c.setBackgroundColor(Color.TRANSPARENT);
			fragmentShowOrHide(fcoc, fsc, false);
			showText(tv_corder, tv_sorder);
			break;
		case R.id.rl_datasele_cdd:
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
		rl_newgoods1_c = (RelativeLayout) findViewById(R.id.rl_newgoods1_c);
		rl_designer1_c = (RelativeLayout) findViewById(R.id.rl_designer1_c);
		rl_back_omg_c = (RelativeLayout) findViewById(R.id.rl_back_omg_c);
		rl_datasele_cdd = (RelativeLayout) findViewById(R.id.rl_datasele_cdd);
		// im_left1_c = (ImageView) findViewById(R.id.im_left1_c);
		// im_right1_c = (ImageView) findViewById(R.id.im_right1_c);
		tv_sorder = (TextView) findViewById(R.id.tv_sorder);
		tv_corder = (TextView) findViewById(R.id.tv_corder);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_designer1_c.setOnClickListener(this);
		rl_newgoods1_c.setOnClickListener(this);
		rl_back_omg_c.setOnClickListener(this);
		rl_datasele_cdd.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.acitivity_ordermgcustom);
		initFragment();
	}

	private void showText(TextView tshow, TextView tnoshow1) {
		tshow.setVisibility(View.VISIBLE);
		tnoshow1.setVisibility(View.INVISIBLE);
	}

	private void initFragment() {
		transaction = getSupportFragmentManager().beginTransaction();
		fcoc = new Fragment_NewCustomOrder();
		fsc = new Fragment_StoreCustom();
		transaction.add(R.id.fl_containmgcustom, fcoc);
		transaction.add(R.id.fl_containmgcustom, fsc);
		fragmentShowOrHide(fcoc, fsc, true);
	}

	private void fragmentShowOrHide(Fragment showFragment, Fragment hideFragment1, boolean isInit) {
		if (!isInit) {
			transaction = getSupportFragmentManager().beginTransaction();
		}
		transaction.show(showFragment);
		transaction.hide(hideFragment1);
		transaction.commit();
	}

	public void initPpwPrice() {
		customView = View.inflate(Activity_OrderMG_Custom.this, R.layout.popuseledata, null);
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
					intent.setAction("com.search.storeodercustom.info");
					sendBroadcast(intent);
				} else {
					Intent intent = new Intent();
					intent.putExtra("starttime", starttime);
					intent.putExtra("endtime", endtime);
					intent.putExtra("type", 1);
					intent.setAction("com.search.customodercustom.info");
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
					intent.setAction("com.search.storeodercustom.info");
					sendBroadcast(intent);
				} else {
					Intent intent = new Intent();
					intent.putExtra("type", 2);
					intent.setAction("com.search.customodercustom.info");
					sendBroadcast(intent);

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
					intent.setAction("com.search.storeodercustom.info");
					sendBroadcast(intent);
				} else {
					Intent intent = new Intent();
					intent.putExtra("type", 3);
					intent.setAction("com.search.customodercustom.info");
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
		DatePickerDialog dpd = new DatePickerDialog(Activity_OrderMG_Custom.this, Datelistener, year, month, day);
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
		setPageEnd("订单管理");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("订单管理");
	}
}