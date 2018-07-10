package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_ConsultCen extends SwipeBackActivity implements OnItemClickListener {
	private RelativeLayout rl_back_consult;
	private ListView lv_customservice;
	private MyAdapter adapter;
	private List<CustomService> customService;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_consult:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		customService = new ArrayList<CustomService>();
		MyApplication.addActivity(this);
		rl_back_consult = (RelativeLayout) findViewById(R.id.rl_back_consult);
		lv_customservice = (ListView) findViewById(R.id.lv_customservice);

		adapter = new MyAdapter();
		lv_customservice.setAdapter(adapter);
		lv_customservice.setOnItemClickListener(this);
		getdata();
	}

	class CustomService {
		String name, userName;

		public CustomService(String name, String userName) {
			super();
			this.name = name;
			this.userName = userName;
		}

	}

	private void getdata() {
		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetAllCust, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("list");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String name = j.optString("name");
						String userName = j.optString("userName");
						customService.add(new CustomService(name, userName));
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		rl_back_consult.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_consultcen);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return customService == null ? 0 : customService.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return customService.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			v = View.inflate(Activity_ConsultCen.this, R.layout.item_lv_kefu, null);
			TextView tv_servicename = (TextView) v.findViewById(R.id.tv_servicename);
			tv_servicename.setText("" + customService.get(arg0).userName);
			return v;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		startActivity(new Intent(Activity_ConsultCen.this, ChatActivity.class).putExtra("name",
				customService.get(position).userName).putExtra("userId", "" + customService.get(position).name));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("咨询中心");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("咨询中心");
	}
}
