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
import com.femto.ugershop.view.MyGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Deep
 * 
 *         2015年9月18日
 */
public class Activity_Customize extends SwipeBackActivity implements OnItemClickListener {
	private RelativeLayout rl_back_c;
	private MyGridView gv_mode_custom, gv_type_custom;
	private List<String> data;
	private ImageView im_reduce_c, im_count_c;
	private EditText ed_count_c;
	private int count = 1;
	private int userId;
	private List<MakeList> makeList;
	private MYTypeAdapter typeadapter;
	private int flag = 0;
	private TextView tv_sure_dingzhi;
	private int myId;
	private RelativeLayout ll_check_mode;

	// private CheckBox cb_checkmode;
	private boolean iscom = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back_c:
			finish();
			break;
		case R.id.im_reduce_c:
			if (count > 1) {
				count = Integer.parseInt(ed_count_c.getText().toString());
				count--;
				ed_count_c.setText("" + count);

			}
			break;
		case R.id.im_count_c:
			count = Integer.parseInt(ed_count_c.getText().toString());
			count++;
			ed_count_c.setText("" + count);

			break;
		case R.id.tv_sure_dingzhi:
			if (makeList.size() == 0) {
				Toast.makeText(this, "暂无定制可选择", Toast.LENGTH_SHORT).show();
				return;
			}
			if (iscom) {
				upmessge();
			} else {
				Toast.makeText(this, "请选择模特卡", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.ll_check_mode1:

			// if (cb_checkmode.isChecked()) {
			// cb_checkmode.setChecked(false);
			// } else {
			// checkMode();
			// }
			Intent intent_mode_c = new Intent(Activity_Customize.this, Activity_NewDZ.class);
			startActivity(intent_mode_c);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkMode();
		MobclickAgent.onResume(this);
		setPageStart("账表统计");
	}

	private void checkMode() {
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyMedolCardIsOk, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);

				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						// cb_checkmode.setChecked(true);
						iscom = true;
					} else {
						showDialogMode();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	private void showDialogMode() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("您的模特卡尚未完善!").setMessage("确定去完善您的模特卡?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent intent_mode_c = new Intent(Activity_Customize.this, Activity_NewDZ.class);
				startActivity(intent_mode_c);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	private void upmessge() {
		showProgressDialog("正在生产订单...");
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("makeId", makeList.get(flag).makeId);
		System.out.println("zuo==params==" + params);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercreateMakeOrder, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					String orderCode = response.getString("orderCode");
					String productName = response.getString("productName");
					int price = response.getInt("price");
					if (result.equals("0")) {
						showNotiDialog(price, orderCode, productName);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	private void showNotiDialog(final int price, final String orderCode, final String productName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示").setMessage("您确定支付" + price + "元").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent intent = new Intent(Activity_Customize.this, Activity_SelectPay.class);
				intent.putExtra("orderCode", orderCode);
				intent.putExtra("price", "" + price);
				intent.putExtra("productname", productName);
				startActivity(intent);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		data = new ArrayList<String>();
		MyApplication.addActivity(this);
		makeList = new ArrayList<MakeList>();
		data.add("1");
		data.add("2");
		data.add("3");
		data.add("4");
		data.add("5");
		data.add("6");
		data.add("7");
		im_reduce_c = (ImageView) findViewById(R.id.im_reduce_c);
		im_count_c = (ImageView) findViewById(R.id.im_count_c);
		tv_sure_dingzhi = (TextView) findViewById(R.id.tv_sure_dingzhi);
		rl_back_c = (RelativeLayout) findViewById(R.id.rl_back_c);
		gv_mode_custom = (MyGridView) findViewById(R.id.gv_mode_custom);
		gv_type_custom = (MyGridView) findViewById(R.id.gv_type_custom);
		ed_count_c = (EditText) findViewById(R.id.ed_count_c);
		ll_check_mode = (RelativeLayout) findViewById(R.id.ll_check_mode1);
		// cb_checkmode = (CheckBox) findViewById(R.id.cb_checkmode);
		typeadapter = new MYTypeAdapter();
		gv_type_custom.setAdapter(typeadapter);
		gv_type_custom.setOnItemClickListener(this);
		getData();
	}

	private void getData() {
		RequestParams params = new RequestParams();
		params.put("userId", userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetDesinMakePrice, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("MakeList");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						int makeId = j.optInt("makeId");
						int makePice = j.optInt("makePice");
						String makeName = j.optString("makeName");
						makeList.add(new MakeList(makeId, makePice, makeName));

					}
					if (makeList.size() != 0) {
						typeadapter.notifyDataSetChanged();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class MakeList {
		int makeId, makePice;
		String makeName;

		public MakeList(int makeId, int makePice, String makeName) {
			super();
			this.makeId = makeId;
			this.makePice = makePice;
			this.makeName = makeName;
		}

	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_c.setOnClickListener(this);
		im_count_c.setOnClickListener(this);
		im_reduce_c.setOnClickListener(this);
		tv_sure_dingzhi.setOnClickListener(this);
		ll_check_mode.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_customize);
		MyApplication.addpaycct(this);
		initParams();
	}

	private void initParams() {
		userId = getIntent().getIntExtra("desiId", 0);
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
	}

	class MYTypeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return makeList == null ? 0 : makeList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return makeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			v = View.inflate(Activity_Customize.this, R.layout.item_dingzhitype, null);
			RelativeLayout rl_selesize = (RelativeLayout) v.findViewById(R.id.rl_selesize_c);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_sort_name_c);
			tv_sort_name.setText(makeList.get(position).makeName + ":" + makeList.get(position).makePice + "元");
			if (flag == position) {
				tv_sort_name.setTextColor(getResources().getColor(R.color.white));
				tv_sort_name.setBackgroundResource(R.drawable.sele_clothtype);
			} else {
				tv_sort_name.setTextColor(getResources().getColor(R.color.sblack));
				tv_sort_name.setBackgroundResource(R.drawable.sele_cloths_type_no);
			}
			return v;
		}
	}

	class MYModeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data == null ? 0 : data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			return v;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		flag = position;
		typeadapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("定制");
	}

}
