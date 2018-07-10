package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_Integraldetail.MyHolder;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.view.CircleImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Ativity_GetMoney extends BaseActivity implements OnRefreshListener2<ListView> {
	private RelativeLayout rl_back_getcash, rl_getcash;
	private PullToRefreshListView lv_getmoney;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	int size = 0;
	private List<StoreDetails> storeDetails;
	private MylvStoreAdapter adapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_getmoney.onRefreshComplete();
				break;

			default:
				break;
			}

		}

	};
	private AlertDialog dialog;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_getcash:
			finish();
			break;
		case R.id.rl_getcash:
			showGetCashDia();
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
		setPageEnd("我的余额");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("我的余额");
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_getcash = (RelativeLayout) findViewById(R.id.rl_back_getcash);
		rl_getcash = (RelativeLayout) findViewById(R.id.rl_getcash);
		lv_getmoney = (PullToRefreshListView) findViewById(R.id.lv_getmoney);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		storeDetails = new ArrayList<StoreDetails>();
		rl_back_getcash.setOnClickListener(this);
		rl_getcash.setOnClickListener(this);
		lv_getmoney.setOnRefreshListener(this);
		lv_getmoney.setMode(Mode.BOTH);
		adapter = new MylvStoreAdapter();
		lv_getmoney.setAdapter(adapter);
		getData(pageIndex, pageSize);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.actiivty_getmoney);
	}

	private void showGetCashDia() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dia_getcash, null);
		final EditText ed_phonenub = (EditText) view.findViewById(R.id.ed_phonenub);
		final EditText ed_zfbcount = (EditText) view.findViewById(R.id.ed_zfbcount);
		builder.setCancelable(false);
		builder.setView(view).setTitle("发起提现!").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				if (ed_zfbcount.getText().toString().trim().length() < 1) {
					showToast("请输入支付宝账号!", 0);
					return;
				}
				if (ed_phonenub.getText().toString().trim().length() != 11) {
					showToast("请输入正确的手机号码!", 0);

					return;
				}
				if (ed_phonenub.getText().toString().trim().length() == 11
						&& ed_zfbcount.getText().toString().trim().length() > 0) {
					getCash(ed_zfbcount.getText().toString().trim(), ed_phonenub.getText().toString().trim());
				}
				dialog.dismiss();

			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
	}

	private void getCash(String alinub, String phone) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("userId", MyApplication.userId);
		params.put("token", MyApplication.token);
		params.put("payInfo", alinub);
		params.put("phone", phone);
		showProgressDialog("申请中...");
		MyApplication.ahc.post(AppFinalUrl.usergetMoneyByUserId, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				String result = response.optString("result");
				if (result.equals("0")) {
					showToast("申请成功!", 0);
					Intent intent = new Intent();
					setResult(1, intent);
					finish();
				} else {
					showToast("提现失败", 0);
				}
			}
		});
	}

	private void getData(int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		showProgressDialog("加载中...");
		params.put("userId", MyApplication.userId);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		System.out.println("zuo===params=" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetUserMoneyInfo, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				lv_getmoney.onRefreshComplete();
				System.out.println("zuo===" + response.toString());
				try {
					JSONArray jsonArray = response.getJSONArray("list");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String info = j.optString("info");
						String createDate = j.optString("createDate");
						String money = j.optString("money");
						storeDetails.add(new StoreDetails(info, money, createDate));
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
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class MylvStoreAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return storeDetails == null ? 0 : storeDetails.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return storeDetails.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(Ativity_GetMoney.this, R.layout.item_integraldetail, null);
				h.tv_info_score = (TextView) v.findViewById(R.id.tv_info_score);
				h.tv_score_score = (TextView) v.findViewById(R.id.tv_score_score1);
				h.tv_time_score = (TextView) v.findViewById(R.id.tv_time_score);
				h.tv_getscore = (TextView) v.findViewById(R.id.tv_getscore);
				h.im_lv_head = (CircleImageView) v.findViewById(R.id.im_lv_head);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_getscore.setVisibility(View.GONE);
			h.tv_info_score.setText("" + storeDetails.get(position).info);
			h.tv_score_score.setText("" + storeDetails.get(position).money);
			h.tv_time_score.setText("" + storeDetails.get(position).createDate);
			// ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri +
			// StoreDetails.get(position).url, h.im_lv_head, options);
			return v;
		}
	}

	class MyHolder {
		TextView tv_score_score, tv_time_score, tv_info_score, tv_getscore;
		CircleImageView im_lv_head;
	}

	class StoreDetails {
		String info, money, createDate;

		public StoreDetails(String info, String money, String createDate) {
			super();
			this.info = info;
			this.money = money;
			this.createDate = createDate;
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		storeDetails.clear();
		pageIndex = 1;
		getData(pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			handler.sendEmptyMessage(1);
			Toast.makeText(Ativity_GetMoney.this, "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getData(pageIndex, pageSize);
		}
	}
}
