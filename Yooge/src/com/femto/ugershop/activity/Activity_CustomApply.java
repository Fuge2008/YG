package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.modle.imte;
import com.femto.ugershop.view.ScrollViewWithListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

public class Activity_CustomApply extends SwipeBackActivity {
	private RelativeLayout rl_back_ca, rl_applycustom;
	private ScrollViewWithListView lv_category;
	private MylvAdapter lvadapter;
	private List<imte> mList;
	private Context mContext;
	private List<String> ll;
	private List<LableSort> lableSort;
	private Map<Integer, Boolean> openpositon;
	private List<Integer> cbse;
	private Map<Integer, String> ps;
	private boolean iscontain = false;
	private List<Dataa> datas;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_ca:
			finish();
			break;
		case R.id.rl_applycustom:
			useraddStyle();
			break;

		default:
			break;
		}
	}

	// public Activity_CustomApply(Context context, List<imte> list) {
	// mContext = context;
	// mList = list;
	// }
	class Dataa {
		int userId, labelId, price;

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public int getLabelId() {
			return labelId;
		}

		public void setLabelId(int labelId) {
			this.labelId = labelId;
		}

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		MyApplication.addActivity(this);
		datas = new ArrayList<Dataa>();
		for (int i = 0; i < 3; i++) {
			Dataa aa = new Dataa();
			aa.setLabelId((i + 1));
			aa.setPrice(i * 200);
		}
		rl_back_ca = (RelativeLayout) findViewById(R.id.rl_back_ca);
		lv_category = (ScrollViewWithListView) findViewById(R.id.lv_category);
		rl_applycustom = (RelativeLayout) findViewById(R.id.rl_applycustom);
		ll = new ArrayList<String>();
		mList = new ArrayList<imte>();
		lableSort = new ArrayList<LableSort>();
		openpositon = new HashMap<Integer, Boolean>();
		ps = new HashMap<Integer, String>();
		cbse = new ArrayList<Integer>();
		lvadapter = new MylvAdapter();
		lv_category.setAdapter(lvadapter);
		gatLableSort();
		// useraddStyle();
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_ca.setOnClickListener(this);
		rl_applycustom.setOnClickListener(this);
	}

	private void gatLableSort() {
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		showProgressDialog("加载中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetLables, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					JSONArray jsonArray = response.getJSONArray("LableSort");
					LableSort ls;
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						ls = new LableSort();
						int sortId = j.optInt("sortId");
						int price = j.optInt("price");
						int styleId = j.optInt("styleId");

						String sortName = j.getString("sortName");
						String status = j.getString("status");
						if (status.equals("1")) {
							ls.setIssele(true);
						} else {
							ls.setIssele(false);
						}

						ls.setPrice(price);
						ls.setSortId(sortId);
						ls.setSortName(sortName);
						ls.setStyleId(styleId);
						lableSort.add(ls);

					}
					lvadapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	private void useraddStyle() {
		int flag = 0;
		List<LableSort> ll = new ArrayList<LableSort>();
		RequestParams params = new RequestParams();

		for (int i = 0; i < lableSort.size(); i++) {
			if (lableSort.get(i).isIssele()) {
				ll.add(lableSort.get(i));
			}
		}
		if (ll.size() == 0) {
			return;
		}
		String[] str = new String[ll.size()];
		for (int i = 0; i < ll.size(); i++) {
			if (ll.get(i).isIssele()) {
				if (ll.get(i).getPrice() != 0) {
					str[i] = "{\"userId\":" + myId + ",\"labelId\":" + ll.get(i).getSortId() + ",\"price\":"
							+ ll.get(i).getPrice() + "}";
				} else {
					Toast.makeText(Activity_CustomApply.this, "请输入选择分类对应的价格", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		}
		// for (int i = 0; i < str.length; i++) {
		// str[i] = "{\"userId\":1,\"labelId\":1,\"price\":100}";
		// System.out.println("zuo==" + str[i]);
		// params.put("str", Arrays.toString(str));
		// }
		System.out.println("zuo123" + Arrays.toString(str));
		params.put("str", Arrays.toString(str));
		showProgressDialog("正在提交...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddStyle, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_CustomApply.this, "提交成功", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(Activity_CustomApply.this, "提交失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_cutomapply);
		initParams();
		showToast(new CallBackInterface() {

			@Override
			public void callBackFunction(int i) {
				Toast.makeText(Activity_CustomApply.this, "" + i, 0).show();
			}
		});
	}

	class MylvAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return lableSort == null ? 0 : lableSort.size();
		}

		@Override
		public Object getItem(int position) {
			return lableSort.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {

			v = View.inflate(Activity_CustomApply.this, R.layout.item_lv_category, null);
			TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
			CheckBox cb = (CheckBox) v.findViewById(R.id.cb);
			EditText et = (EditText) v.findViewById(R.id.et);
			TextView tv_cancel_apply = (TextView) v.findViewById(R.id.tv_cancel_apply);
			tv_name.setText("" + lableSort.get(position).sortName);
			if (lableSort.get(position).isIssele()) {
				cb.setChecked(true);
				tv_cancel_apply.setVisibility(View.VISIBLE);
				et.setFocusable(false);
				et.setEnabled(false);
			} else {
				cb.setChecked(false);
				tv_cancel_apply.setVisibility(View.GONE);
				et.setFocusable(true);
				et.setEnabled(true);
			}
			cb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (lableSort.get(position).isIssele()) {
						lableSort.get(position).setIssele(false);
					} else {
						lableSort.get(position).setIssele(true);
					}

				}
			});
			tv_cancel_apply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showCancelApply(lableSort.get(position).styleId, position);
				}

			});
			et.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					System.out.println("zuo=onTextChanged==" + s);
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					if (s != null && s.length() != 0) {
						String p = s.toString();
						if (p != null) {
							lableSort.get(position).setPrice(Integer.parseInt(p));
						}
					} else {
						lableSort.get(position).setPrice(0);
					}

					// ps.put(position, p);
				}
			});
			// if (ps != null && ps.containsKey(position)) {
			// h.et.setText(ps.get(position));
			// } else {
			// h.et.setHint("请输入 ");
			// }
			// if (et.getText() != null && et.getText().toString().length() !=
			// 0) {
			// lableSort.get(position).setPrice(
			// Integer.parseInt(et.getText().toString().trim()));
			// } else {
			// lableSort.get(position).setPrice(0);
			// }

			if (lableSort.get(position).getPrice() == 0) {
				et.setHint("请输入设计费用");
			} else {
				et.setText("" + lableSort.get(position).getPrice());
			}
			return v;
		}
	}

	class ViewHolder {
		CheckBox cb;
		EditText et;
		TextView tv_name;

	}

	// 删除申请的
	private void cancelApply(int sortId, final int position) {
		RequestParams params = new RequestParams();
		params.put("styleId", sortId);
		params.put("userId", myId);
		System.out.println("zuoparams=" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userdeleteStyle, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo==" + response.toString());
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						lableSort.get(position).setIssele(false);
						lableSort.get(position).setPrice(0);
						lvadapter.notifyDataSetChanged();
					} else {
						Toast.makeText(Activity_CustomApply.this, "删除失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	// 提示是否删除
	private void showCancelApply(final int sortId, final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确定取消该定制?").setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		}).setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				cancelApply(sortId, position);
			}
		}).show();
	}

	CallBackInterface cc;
	private DisplayImageOptions options;
	private int myId;

	// 定义函数,其中一个参数为CallBackInterface类型
	public void showToast(CallBackInterface callBackInterface) {
		cc = callBackInterface;
	}

	// 定义接口.且在接口中定义一个方法
	public interface CallBackInterface {
		public void callBackFunction(int i);
	}

	class LableSort {
		int sortId, price, styleId;
		String sortName;
		boolean issele;

		public int getSortId() {
			return sortId;
		}

		public int getStyleId() {
			return styleId;
		}

		public void setStyleId(int styleId) {
			this.styleId = styleId;
		}

		public void setSortId(int sortId) {
			this.sortId = sortId;
		}

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}

		public String getSortName() {
			return sortName;
		}

		public void setSortName(String sortName) {
			this.sortName = sortName;
		}

		public boolean isIssele() {
			return issele;
		}

		public void setIssele(boolean issele) {
			this.issele = issele;
		}

	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("定制申请");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("定制申请");
	}
}
