package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.MyCoupon;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_MyCoupon extends BaseActivity implements OnItemClickListener, OnRefreshListener2<ListView> {
	private RelativeLayout rl_back_mycoupon;
	private List<MyCoupon> cs;
	private MyAdapter adapter;
	private PullToRefreshListView lv_mycoupon;
	private int type = 0;
	private TextView tv_yhm;
	private String pushCode = "";
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private int size = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_mycoupon.onRefreshComplete();
				break;

			default:
				break;
			}

		};
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("我的优惠券");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("我的优惠券");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_mycoupon:
			finish();
			break;
		case R.id.tv_yhm:
			Intent intent = new Intent(this, Activity_CouponCode.class);
			startActivityForResult(intent, 309);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int arg1, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, arg1, data);
		if (data != null) {
			if (requestCode == 309) {
				pushCode = data.getStringExtra("pushCode");

				if (type == 1) {
					return;
				}
				if (pushCode.equals("0")) {
					pageIndex = 1;
					getData(pageIndex, pageSize);
				} else {
					Intent intent = new Intent();
					intent.putExtra("pushCode", pushCode);
					setResult(208, intent);
					finish();
				}

			}
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_mycoupon = (RelativeLayout) findViewById(R.id.rl_back_mycoupon);
		lv_mycoupon = (PullToRefreshListView) findViewById(R.id.lv_mycoupon);
		tv_yhm = (TextView) findViewById(R.id.tv_yhm);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_mycoupon.setOnClickListener(this);
		tv_yhm.setOnClickListener(this);
		lv_mycoupon.setOnRefreshListener(this);
		lv_mycoupon.setOnItemClickListener(this);
		lv_mycoupon.setMode(Mode.BOTH);
		cs = new ArrayList<MyCoupon>();
		adapter = new MyAdapter();
		lv_mycoupon.setAdapter(adapter);
		getData(pageIndex, pageSize);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.acitivity_mycoupon);
		MyApplication.addActivity(this);
		initParams();
	}

	private void initParams() {
		// TODO Auto-generated method stub
		type = getIntent().getIntExtra("type", 0);
	}

	private void getData(int pi, int ps) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("userId", MyApplication.userId);
		params.put("pageModel.pageIndex", pi);
		params.put("pageModel.pageSize", ps);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyCounps, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo==response==" + response.toString());
				JSONArray optJSONArray = response.optJSONArray("list");
				lv_mycoupon.onRefreshComplete();
				if (optJSONArray != null) {
					for (int i = 0; i < optJSONArray.length(); i++) {
						JSONObject j = optJSONArray.optJSONObject(i);
						String name = j.optString("name");
						String lastTime = j.optString("lastTime");
						String info = j.optString("info");
						int money = j.optInt("money");
						int type = j.optInt("type");
						int fullMoney = j.optInt("fullMoney");
						int id = j.optInt("id");

						cs.add(new MyCoupon(name, lastTime, info, id, money, type, fullMoney));
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cs == null ? 0 : cs.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return cs.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View v, ViewGroup arg2) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_MyCoupon.this, R.layout.item_coupons, null);
			TextView tv_cinfo = (TextView) v.findViewById(R.id.tv_cinfo);
			TextView tv_fullmoney = (TextView) v.findViewById(R.id.tv_fullmoney);
			TextView tv_lasttime = (TextView) v.findViewById(R.id.tv_lasttime);
			tv_lasttime.setText("" + cs.get(position).getLastTime());
			tv_fullmoney.setText("¥ " + cs.get(position).getMoney());
			tv_cinfo.setText("" + cs.get(position).getInfo());
			return v;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if (type == 1) {
			return;
		}

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("cs", cs.get(position - 1));
		intent.putExtras(bundle);
		intent.putExtra("pushCode", pushCode);
		setResult(208, intent);
		finish();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		cs.clear();
		getData(pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			Toast.makeText(Activity_MyCoupon.this, "没有更多", Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(1);
		} else {
			getData(pageIndex, pageSize);
		}
	}
}
