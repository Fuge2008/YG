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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月19日 下午3:34:04 类说明
 */
public class Activity_Address extends SwipeBackActivity implements OnItemClickListener, OnItemLongClickListener {
	private RelativeLayout rl_back_address;
	private TextView tv_new;
	private List<Address> address;
	private int myId;
	private LVAdapter adapter;
	private ListView lv_address;
	private String addres = "";
	private int currentPosition = -1;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_address:
			finish();
			break;
		// case R.id.tv_sure_address:
		// for (int i = 0; i < address.size(); i++) {
		// if (address.get(i).ischeck) {
		// addres = address.get(i).name;
		// Intent intent_a = new Intent();
		// intent_a.putExtra("address", addres);
		// setResult(101, intent_a);
		// finish();
		// }
		// }
		// if (addres.equals("")) {
		// Toast.makeText(Activity_Address.this, "请选择地址",
		// Toast.LENGTH_SHORT).show();
		// }
		// break;
		case R.id.tv_new:
			Intent intent = new Intent(this, Activity_CreatAddress.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		address = new ArrayList<Address>();
		rl_back_address = (RelativeLayout) findViewById(R.id.rl_back_address);
		tv_new = (TextView) findViewById(R.id.tv_new);
		lv_address = (ListView) findViewById(R.id.lv_address);
		adapter = new LVAdapter();
		lv_address.setAdapter(adapter);
		lv_address.setOnItemClickListener(this);
		lv_address.setOnItemLongClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getdata();
		MobclickAgent.onResume(this);
		setPageStart("账表统计");
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_address.setOnClickListener(this);
		tv_new.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_address);
		initParams();
		MyApplication.addActivity(this);
	}

	private void getdata() {
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		address.clear();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetAddressList, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("list");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						int addressId = j.optInt("addressId");
						String name = j.optString("name");
						address.add(new Address(addressId, name, false));
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class LVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return address == null ? 0 : address.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return address.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_Address.this, R.layout.item_address, null);
			TextView tv_address = (TextView) v.findViewById(R.id.tv_address_ad);
			tv_address.setText("" + address.get(address.size() - position - 1).name);
			// tv_address_dele.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// showDeleDialog(address.get(position).addressId, position);
			// }
			//
			// });

			return v;
		}
	}

	private void showDeleDialog(final int addressId, final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确定删除此地址?").setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleAddress(addressId, position);

			}

		}).show();
	}

	private void deleAddress(int addressId, final int position) {
		RequestParams params = new RequestParams();
		params.put("addressId", addressId);
		showProgressDialog("正在删除...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userdeleteAddressByAddressId, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_Address.this, "删除成功", Toast.LENGTH_SHORT).show();
						address.remove(position);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(Activity_Address.this, "删除失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class Address {
		int addressId;
		String name;
		boolean ischeck;

		public Address(int addressId, String name, boolean ischeck) {
			super();
			this.addressId = addressId;
			this.name = name;
			this.ischeck = ischeck;
		}

		public boolean isIscheck() {
			return ischeck;
		}

		public void setIscheck(boolean ischeck) {
			this.ischeck = ischeck;
		}

		public int getAddressId() {
			return addressId;
		}

		public void setAddressId(int addressId) {
			this.addressId = addressId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
		myId = sp.getInt("userId", 0);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		addres = address.get(address.size() - position - 1).name;
		Intent intent_a = new Intent();
		intent_a.putExtra("address", addres);
		setResult(101, intent_a);
		finish();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		showDeleDialog(address.get(address.size() - position - 1).addressId, address.size() - position - 1);
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("选择收货地址");
	}

}
