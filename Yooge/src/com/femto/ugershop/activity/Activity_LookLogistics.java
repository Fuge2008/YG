package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.view.MyScrollView;
import com.femto.ugershop.view.ScrollViewWithListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_LookLogistics extends BaseActivity {
	private RelativeLayout rl_back_logist;
	private int orderId, type;
	private ScrollViewWithListView lv;
	private List<Logistis> logistis;
	private MylvAdapter lvadapter;
	private TextView tv_company, tv_no, tv_status;
	private MyScrollView sll;
	private String postCode = "";
	private int flag = 0;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_logist:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		initParams();
		MyApplication.addActivity(this);
		logistis = new ArrayList<Logistis>();
		rl_back_logist = (RelativeLayout) findViewById(R.id.rl_back_logist);
		lv = (ScrollViewWithListView) findViewById(R.id.lv_logist);
		tv_company = (TextView) findViewById(R.id.tv_company);
		tv_no = (TextView) findViewById(R.id.tv_no);
		tv_status = (TextView) findViewById(R.id.tv_status);
		sll = (MyScrollView) findViewById(R.id.sll);
		tv_no.setText("" + postCode);
		lvadapter = new MylvAdapter();
		lv.setAdapter(lvadapter);
		getdata();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("积分明细");
	}

	private String logURL = AppFinalUrl.usergetExpByOrderId;

	private void getdata() {
		showProgressDialog("查询中...");
		RequestParams params = new RequestParams();
		params.put("orderId", orderId);
		if (flag != 1) {
			params.put("type", type);
		}

		System.out.println("zuo=params=" + AppFinalUrl.usergetExpByOrderId + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(logURL, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuoresponse==" + response.toString());
				try {

					String resultcode = response.getString("resultcode");

					if (resultcode.equals("200")) {
						JSONObject jsonObject = response.getJSONObject("result");
						String company = jsonObject.optString("company");
						String com = jsonObject.optString("com");
						String no = jsonObject.optString("no");
						int status = jsonObject.optInt("status");
						tv_company.setText("" + company);
						tv_no.setText("" + no);
						if (status == 1) {
							tv_status.setText("已签收");
						} else {
							tv_status.setText("运输中");
						}
						JSONArray jsonArray = jsonObject.getJSONArray("list");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject j = jsonArray.getJSONObject(i);
							String datetime = j.optString("datetime");
							String remark = j.optString("remark");
							String zone = j.optString("zone");
							logistis.add(new Logistis(datetime, remark, zone));
						}
						lvadapter.notifyDataSetChanged();
						sll.smoothScrollTo(0, 0);
					} else {
						Toast.makeText(Activity_LookLogistics.this, "暂无数据", Toast.LENGTH_SHORT).show();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void initParams() {
		orderId = getIntent().getIntExtra("orderId", 0);
		type = getIntent().getIntExtra("type", 0);
		flag = getIntent().getIntExtra("flag", 0);
		postCode = getIntent().getStringExtra("postCode");
		if (flag == 1) {
			logURL = AppFinalUrl.usergetExpByMakeOrderId;
		}
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_logist.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_logistics);
	}

	class Logistis {
		String datetime, remark, zone;

		public Logistis(String datetime, String remark, String zone) {
			super();
			this.datetime = datetime;
			this.remark = remark;
			this.zone = zone;
		}

	}

	class MylvAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return logistis == null ? 0 : logistis.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return logistis.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(Activity_LookLogistics.this, R.layout.item_logist, null);
				h.tv_datetime = (TextView) v.findViewById(R.id.tv_datetime);
				h.tv_remark = (TextView) v.findViewById(R.id.tv_remark);
				h.v1 = v.findViewById(R.id.v1);
				h.v2 = v.findViewById(R.id.v2);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_datetime.setText("" + logistis.get(logistis.size() - position - 1).datetime);
			h.tv_remark.setText("" + logistis.get(logistis.size() - position - 1).remark);
			if (position == 0) {
				h.v1.setVisibility(View.INVISIBLE);
			} else {
				h.v1.setVisibility(View.VISIBLE);
			}
			if (position == logistis.size() - 1) {
				h.v2.setVisibility(View.INVISIBLE);
			} else {
				h.v2.setVisibility(View.VISIBLE);
			}
			return v;
		}
	}

	class MyHolder {
		TextView tv_remark, tv_datetime;
		View v1, v2;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("查看物流");
	}

}
