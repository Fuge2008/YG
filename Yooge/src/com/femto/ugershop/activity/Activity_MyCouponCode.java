package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_MyCouponCode extends BaseActivity implements OnRefreshListener2<ListView> {
	private RelativeLayout rl_back_mycoupon;
	private List<String> data;
	private int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private TextView tv_mycodede;
	private MyAdapter adapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_mycoupon:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("我的优惠码");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("我的优惠码");
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_mycoupon = (RelativeLayout) findViewById(R.id.rl_back_mycoupon);
		tv_mycodede = (TextView) findViewById(R.id.tv_mycodede);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_mycoupon.setOnClickListener(this);
		getData();
	}

	private void getData() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("userId", MyApplication.userId);
		showProgressDialog("获取中...");
		MyApplication.ahc.post(AppFinalUrl.usergetMyCode, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				String pushCode = response.optString("pushCode");
				dismissProgressDialog();
				if (pushCode.length() > 0) {
					tv_mycodede.setText(pushCode);
				} else {
					tv_mycodede.setText("暂无优惠码");
				}
			}
		});
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_mycouponcode);

		data = new ArrayList<String>();
		data.add("214124142");
		data.add("214124142");
		data.add("214124142");
		data.add("214124142");
		data.add("214124142");
		data.add("214124142");

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data == null ? 0 : data.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_MyCouponCode.this, R.layout.item_mycoucode, null);
			TextView tv_mycode = (TextView) v.findViewById(R.id.tv_mycode);
			tv_mycode.setText("" + data.get(arg0));
			return v;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}
}
