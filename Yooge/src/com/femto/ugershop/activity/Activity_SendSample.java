package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月9日 下午6:33:59 类说明
 */
public class Activity_SendSample extends BaseActivity {
	private RelativeLayout rl_back_sendsample;
	private TextView tv_sure_send, tv_selepost, tv_post, tv_ugeraddress, tv_ugerphone, tv_ugeremail, tv_data, tv_seledata;
	private EditText ed_name_send, ed_phone_send, ed_address_send, ed_postcode_send, ed_count_send, ed_alinub, ed_email_send;
	private List<PostM> postm;
	private View customViewone;
	private PopupWindow ppw;
	private PPListAdapter ppladapter;
	private ListView lv_ppwtow;
	private int myId;
	private String currentNub;
	private String phone;
	private String email;
	private String address;
	private int year;
	private int month;
	private int day;
	private RadioButton rb_yes, rb_no;
	private String isHasPaper;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("寄送样衣");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("寄送样衣");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_sendsample:
			finish();
			break;
		case R.id.tv_selepost:
			if (ppw != null && ppw.isShowing()) {
				ppw.dismiss();

			} else {
				initPpwone();
				ppw.showAsDropDown(v, 0, 0);

			}

			break;
		case R.id.tv_sure_send:
			if (ed_name_send.getText().toString().length() == 0) {
				Toast.makeText(Activity_SendSample.this, "请填写本人姓名", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_phone_send.getText().toString().length() != 11) {
				Toast.makeText(Activity_SendSample.this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_address_send.getText().toString().length() == 0) {
				Toast.makeText(Activity_SendSample.this, "请填写您的地址", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_postcode_send.getText().toString().length() == 0) {
				Toast.makeText(Activity_SendSample.this, "请填写快递单号", Toast.LENGTH_SHORT).show();
				return;
			}
			if (tv_post.getText().toString().length() == 0) {
				Toast.makeText(Activity_SendSample.this, "请选择快递公司", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_count_send.getText().toString().length() == 0) {
				Toast.makeText(Activity_SendSample.this, "请输入件数", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_alinub.getText().toString().length() == 0) {
				Toast.makeText(Activity_SendSample.this, "请输入支付宝账号", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_email_send.getText().toString().length() == 0) {
				Toast.makeText(Activity_SendSample.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
				return;
			}
			if (tv_data.getText().toString().length() == 0) {
				Toast.makeText(Activity_SendSample.this, "请选择寄出日期", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!rb_no.isChecked() && !rb_yes.isChecked()) {
				Toast.makeText(Activity_SendSample.this, "请选择是否有电子版纸", Toast.LENGTH_SHORT).show();
				return;
			}
			if (rb_no.isChecked()) {
				isHasPaper = "否";
			} else {
				isHasPaper = "是";
			}
			upmessage();
			break;
		case R.id.tv_seledata:
			showDataDialog();
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		initPost();
		rl_back_sendsample = (RelativeLayout) findViewById(R.id.rl_back_sendsample);
		tv_sure_send = (TextView) findViewById(R.id.tv_sure_send);
		ed_name_send = (EditText) findViewById(R.id.ed_name_send);
		ed_phone_send = (EditText) findViewById(R.id.ed_phone_send);
		ed_address_send = (EditText) findViewById(R.id.ed_address_send);
		ed_postcode_send = (EditText) findViewById(R.id.ed_postcode_send);
		ed_count_send = (EditText) findViewById(R.id.ed_count_send);
		ed_alinub = (EditText) findViewById(R.id.ed_alinub);
		ed_email_send = (EditText) findViewById(R.id.ed_email_send);
		tv_selepost = (TextView) findViewById(R.id.tv_selepost);
		tv_post = (TextView) findViewById(R.id.tv_post);
		tv_ugeraddress = (TextView) findViewById(R.id.tv_ugeraddress);
		tv_ugerphone = (TextView) findViewById(R.id.tv_ugerphone);
		tv_ugeremail = (TextView) findViewById(R.id.tv_ugeremail);
		tv_data = (TextView) findViewById(R.id.tv_data);
		tv_seledata = (TextView) findViewById(R.id.tv_seledata);
		rb_no = (RadioButton) findViewById(R.id.rb_no);
		rb_yes = (RadioButton) findViewById(R.id.rb_yes);
		Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
		Date mydate = new Date(); // 获取当前日期Date对象
		mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期
		year = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年
		month = mycalendar.get(Calendar.MONTH);// 获取Calendar对象中的月
		day = mycalendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
		tv_data.setText("" + year + "-" + (month + 1) + "-" + day);
		getdata();
	}

	private void initPpwone() {
		customViewone = View.inflate(this, R.layout.popu_newgoodsone, null);
		LinearLayout ll_ppall = (LinearLayout) customViewone.findViewById(R.id.ll_ppall);
		ll_ppall.setVisibility(View.GONE);
		lv_ppwtow = (ListView) customViewone.findViewById(R.id.lv_poputwo);
		lv_ppwtow.setAdapter(ppladapter);
		ppw = new PopupWindow(customViewone, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		ppw.setFocusable(true);
		ppw.setBackgroundDrawable(new BitmapDrawable());
		lv_ppwtow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				tv_post.setText("" + postm.get(position).com);
				currentNub = postm.get(position).no;
				ppw.dismiss();
			}
		});
	}

	private void upmessage() {
		RequestParams params = new RequestParams();
		params.put("sendClotherAddress.name", ed_name_send.getText().toString().trim());
		params.put("sendClotherAddress.phone", ed_phone_send.getText().toString().trim());
		params.put("sendClotherAddress.address", ed_address_send.getText().toString().trim());
		params.put("sendClotherAddress.postCode", ed_postcode_send.getText().toString().trim());
		params.put("sendClotherAddress.post", tv_post.getText().toString().trim());
		params.put("sendClotherAddress.userId", myId);
		params.put("sendClotherAddress.postHome", currentNub);
		params.put("sendClotherAddress.nub", ed_count_send.getText().toString());
		params.put("sendClotherAddress.isHasPaper", isHasPaper);
		params.put("sendClotherAddress.payInfo", ed_alinub.getText().toString());
		params.put("sendClotherAddress.email", ed_email_send.getText().toString());
		params.put("sendClotherAddress.sendDate", tv_data.getText().toString());

		showProgressDialog("正在提交...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddSendClotherAddress, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_SendSample.this, "提交成功", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(Activity_SendSample.this, "提交失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void getdata() {
		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetAppPhoneAndAddress, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				phone = response.optString("phone");
				email = response.optString("email");
				address = response.optString("address");
				tv_ugeraddress.setText("" + address);
				tv_ugerphone.setText("" + phone);
				tv_ugeremail.setText("" + email);

				// tv_ugeraddress, tv_ugerphone, tv_ugeremail
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
			v = View.inflate(Activity_SendSample.this, R.layout.item_lv_popu, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_sort_name);
			tv_sort_name.setText("" + postm.get(position).com);
			return v;
		}

	}

	private void initPost() {
		postm = new ArrayList<PostM>();
		PostM pm1 = new PostM("顺丰", "sf");
		PostM pm2 = new PostM("申通", "sto");
		PostM pm3 = new PostM("圆通", "yt");
		PostM pm4 = new PostM("韵达", "yd");
		PostM pm5 = new PostM("天天", "tt");
		PostM pm6 = new PostM("EMS", "ems");
		PostM pm7 = new PostM("中通", "zto");
		PostM pm8 = new PostM("汇通", "ht");
		postm.add(pm1);
		postm.add(pm2);
		postm.add(pm3);
		postm.add(pm4);
		postm.add(pm5);
		postm.add(pm6);
		postm.add(pm7);
		postm.add(pm8);
		ppladapter = new PPListAdapter();
	}

	class PostM {
		String com, no;

		public PostM(String com, String no) {
			super();
			this.com = com;
			this.no = no;
		}

	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_sendsample.setOnClickListener(this);
		tv_selepost.setOnClickListener(this);
		tv_sure_send.setOnClickListener(this);
		tv_seledata.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_sendsample);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);

	}

	private void showDataDialog() {
		DatePickerDialog dpd = new DatePickerDialog(Activity_SendSample.this, Datelistener, year, month, day);
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
			tv_data.setText(year + "-" + (month + 1) + "-" + day);
		}
	};
}
