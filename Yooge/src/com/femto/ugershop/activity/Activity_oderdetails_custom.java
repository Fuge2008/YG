package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_Commends_Store.MBC;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.selepic.ImgFileListActivity;
import com.femto.ugershop.view.MyGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_oderdetails_custom extends BaseActivity {
	private RelativeLayout rl_back_oderdetails;
	String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private List<String> filePath1;
	private List<String> filePath2;
	private List<String> filePath3;
	private List<String> upFilePath;
	private int w;
	private MBC mbc;

	private MyGridView gv_odertailsefirst, gv_odertailsesecond, gv_odertailsthree;
	private int flag = 1;
	private int makeId;
	private String userImgUrl;
	private String postCode;
	private String desinImgUrl;
	private String userName;
	private String createDate;
	private String desinUserName;
	private int count;
	private int status;
	private int desinCount;
	private int desinUserId;
	private int price;
	private int userId;
	private int myId;
	private int hight = 1;
	private LinearLayout ll_oc;
	private DisplayImageOptions options;
	private List<ListOne> listOne;
	private List<ListOne> listTwo;
	private List<ListOne> listThree;
	private LinearLayout ll_gv1, ll_gv2, ll_gv3;
	private EditText tv_ordercode_ocd;
	private TextView tv_ordercode, tv_all;
	private LinearLayout rl_ordercode, ll_wuliu;
	private TextView tv_name_ocd, tv_time_ocd, tv_price_ocd, tv_nub_ocd, tv_count_ocd, tv_math_ocd, tv_status_ocd, tv_sure_ocd,
			tv_sure_oc, tv_sure_xiugai, tv_sure_tuikuan, tv_title_oderdetails;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				dismissProgressDialog();
				filePath1.clear();
				listOne.clear();
				listThree.clear();
				listTwo.clear();
				Toast.makeText(Activity_oderdetails_custom.this, "提交成功", Toast.LENGTH_SHORT).show();
				getData();
				break;

			default:
				break;
			}
		};
	};
	private String name;
	private int countNub = 1;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_oderdetails:
			finish();
			break;
		case R.id.tv_sure_oc:
			showsDialog(makeId);
			break;
		case R.id.tv_sure_xiugai:
			if (countNub == 4) {
				Toast.makeText(Activity_oderdetails_custom.this, "已有三次修改,不可再修改", Toast.LENGTH_SHORT).show();
				return;
			}
			showsureDialog();

			break;
		case R.id.tv_sure_tuikuan:
			showTuiKuang(makeId);
			break;

		default:
			break;
		}
	}

	private void showTuiKuang(final int makeId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示").setMessage("确认申请退款?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				sureTuik(makeId);
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.show();
	}

	private void sureTuik(int orderId) {
		RequestParams params = new RequestParams();
		params.put("makeId", orderId);
		params.put("type", 1);
		showProgressDialog("确定中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userchangeMakeProduct, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				String result;
				dismissProgressDialog();
				try {
					result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_oderdetails_custom.this, "申请成功", Toast.LENGTH_SHORT).show();
						tv_sure_tuikuan.setVisibility(View.GONE);
						tv_sure_xiugai.setVisibility(View.GONE);
						tv_sure_oc.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	private void showsureDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(Activity_oderdetails_custom.this);
		builder.setMessage("确定申请修改?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				upXiugai();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();

	}

	private void upXiugai() {
		RequestParams params = new RequestParams();
		params.put("makeId", makeId);
		params.put("count", countNub);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usershenQingUpdatePic, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_oderdetails_custom.this, "申请成功", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		MyApplication.addActivity(this);
		filePath1 = new ArrayList<String>();
		filePath1.add("aa");
		filePath2 = new ArrayList<String>();
		filePath2.add("aa");
		filePath3 = new ArrayList<String>();
		filePath3.add("aa");
		upFilePath = new ArrayList<String>();
		listOne = new ArrayList<ListOne>();
		listTwo = new ArrayList<ListOne>();
		listThree = new ArrayList<ListOne>();
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(this, 40);
		w = (screenWidth - dp2px) / 3;
		ll_gv1 = (LinearLayout) findViewById(R.id.ll_gv1);
		ll_gv2 = (LinearLayout) findViewById(R.id.ll_gv2);
		ll_gv3 = (LinearLayout) findViewById(R.id.ll_gv3);
		ll_gv1.setVisibility(View.VISIBLE);
		ll_gv2.setVisibility(View.VISIBLE);
		ll_gv3.setVisibility(View.VISIBLE);
		// tv_name_ocd, tv_time_ocd, tv_price_ocd, tv_nub_ocd, tv_count_ocd,
		// tv_math_ocd, tv_status_ocd,
		tv_name_ocd = (TextView) findViewById(R.id.tv_name_ocd);
		tv_time_ocd = (TextView) findViewById(R.id.tv_time_ocd);
		tv_price_ocd = (TextView) findViewById(R.id.tv_price_ocd);
		tv_nub_ocd = (TextView) findViewById(R.id.tv_nub_ocd);
		tv_count_ocd = (TextView) findViewById(R.id.tv_count_ocd);
		tv_math_ocd = (TextView) findViewById(R.id.tv_math_ocd);
		tv_status_ocd = (TextView) findViewById(R.id.tv_status_ocd);
		tv_ordercode_ocd = (EditText) findViewById(R.id.tv_ordercode_ocd);
		tv_ordercode_ocd.setVisibility(View.GONE);
		tv_sure_ocd = (TextView) findViewById(R.id.tv_sure_ocd);
		rl_back_oderdetails = (RelativeLayout) findViewById(R.id.rl_back_oderdetails);
		gv_odertailsefirst = (MyGridView) findViewById(R.id.gv_odertailsefirst);
		gv_odertailsesecond = (MyGridView) findViewById(R.id.gv_odertailsesecond);
		gv_odertailsthree = (MyGridView) findViewById(R.id.gv_odertailsthree);
		// tv_sure_oc, tv_sure_xiugai, tv_sure_tuikuan
		tv_sure_oc = (TextView) findViewById(R.id.tv_sure_oc);
		tv_sure_xiugai = (TextView) findViewById(R.id.tv_sure_xiugai);
		tv_sure_tuikuan = (TextView) findViewById(R.id.tv_sure_tuikuan);
		tv_ordercode = (TextView) findViewById(R.id.tv_ordercode);
		rl_ordercode = (LinearLayout) findViewById(R.id.ll_ordercode);
		ll_wuliu = (LinearLayout) findViewById(R.id.ll_wuliu);
		tv_all = (TextView) findViewById(R.id.tv_all);
		tv_all.setText("合计");
		// rl_ordercode.setVisibility(View.GONE);
		ll_wuliu.setVisibility(View.GONE);
		tv_ordercode.setVisibility(View.VISIBLE);
		tv_title_oderdetails = (TextView) findViewById(R.id.tv_title_oderdetails);
		tv_title_oderdetails.setText("" + name);
		ll_oc = (LinearLayout) findViewById(R.id.ll_oc);
		ll_oc.setVisibility(View.VISIBLE);
		tv_sure_ocd.setVisibility(View.GONE);

		getData();
	}

	private void getData() {
		RequestParams params = new RequestParams();
		params.put("makeId", makeId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMakeOrderInfoList, params, new JsonHttpResponseHandler() {

			private double percentage;

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);

				userImgUrl = response.optString("userImgUrl");
				postCode = response.optString("postCode");
				desinImgUrl = response.optString("desinImgUrl");
				userName = response.optString("userName");
				createDate = response.optString("createDate");
				desinUserName = response.optString("desinUserName");
				count = response.optInt("count");
				status = response.optInt("status");
				desinCount = response.optInt("desinCount");
				desinUserId = response.optInt("desinUserId");
				price = response.optInt("price");
				userId = response.optInt("userId");
				percentage = response.optDouble("percentage");
				// tv_name_ocd, tv_time_ocd, tv_price_ocd, tv_nub_ocd,
				// tv_count_ocd,
				// tv_math_ocd, tv_status_ocd,tv_ordercode_ocd
				tv_name_ocd.setText("" + desinUserName);
				tv_time_ocd.setText("" + createDate);
				tv_price_ocd.setText("" + price + "元");
				tv_nub_ocd.setText("" + count);
				tv_count_ocd.setText("" + count);
				tv_math_ocd.setText("" + price * count + "元");
				tv_ordercode.setText("" + postCode);
				if (status == 0) {
					tv_status_ocd.setText("待付款");
					tv_sure_oc.setVisibility(View.GONE);
					tv_sure_tuikuan.setVisibility(View.GONE);
					tv_sure_xiugai.setVisibility(View.GONE);
				}
				if (status == 1) {
					tv_status_ocd.setText("待发货");
				}
				if (status == 2) {
					tv_status_ocd.setText("待收货");
				}
				if (status == 3) {
					tv_status_ocd.setText("交易完成");
					tv_sure_oc.setVisibility(View.GONE);
					tv_sure_tuikuan.setVisibility(View.GONE);
					tv_sure_xiugai.setVisibility(View.GONE);
				}
				if (status == 4) {
					tv_status_ocd.setText("交易关闭");
					tv_sure_oc.setVisibility(View.GONE);
					tv_sure_tuikuan.setVisibility(View.GONE);
					tv_sure_xiugai.setVisibility(View.GONE);
				}
				if (status == 5) {
					tv_status_ocd.setText("申请退款中");
					tv_sure_oc.setVisibility(View.GONE);
					tv_sure_tuikuan.setVisibility(View.GONE);
					tv_sure_xiugai.setVisibility(View.GONE);
				}
				if (status == 6) {
					tv_status_ocd.setText("退款成功");
					tv_sure_oc.setVisibility(View.GONE);
					tv_sure_tuikuan.setVisibility(View.GONE);
					tv_sure_xiugai.setVisibility(View.GONE);
				}
				if (status == 7) {
					tv_status_ocd.setText("申请第一次修改");
				}
				if (status == 8) {
					tv_status_ocd.setText("第一次修改完成");
				}
				if (status == 9) {
					tv_status_ocd.setText("申请第二次修改");
				}
				if (status == 10) {
					tv_status_ocd.setText("申请第二次修改完成");
				}
				if (status == 11) {
					tv_status_ocd.setText("申请第三次修改");
				}
				if (status == 12) {
					tv_status_ocd.setText("申请第三次修改完成");
				}
				if (status == 13) {
					tv_status_ocd.setText("申请退款中");
					tv_sure_oc.setVisibility(View.GONE);
					tv_sure_tuikuan.setVisibility(View.GONE);
					tv_sure_xiugai.setVisibility(View.GONE);
				}
				if (status == 14) {
					tv_status_ocd.setText("退款中");
					tv_sure_oc.setVisibility(View.GONE);
					tv_sure_tuikuan.setVisibility(View.GONE);
					tv_sure_xiugai.setVisibility(View.GONE);
				}
				if (status == 15) {
					tv_status_ocd.setText("退款成功");
					tv_sure_oc.setVisibility(View.GONE);
					tv_sure_tuikuan.setVisibility(View.GONE);
					tv_sure_xiugai.setVisibility(View.GONE);
				}

				JSONArray optJSONArray = response.optJSONArray("listOne");
				for (int i = 0; i < optJSONArray.length(); i++) {
					JSONObject j = optJSONArray.optJSONObject(i);
					int photoId = j.optInt("photoId");
					String photoUrl = j.optString("photoUrl");
					listOne.add(new ListOne(photoId, photoUrl));
				}

				JSONArray optJSONArray1 = response.optJSONArray("listTwo");
				for (int i = 0; i < optJSONArray1.length(); i++) {
					JSONObject j = optJSONArray1.optJSONObject(i);
					int photoId = j.optInt("photoId");
					String photoUrl = j.optString("photoUrl");
					listTwo.add(new ListOne(photoId, photoUrl));
				}

				JSONArray optJSONArray2 = response.optJSONArray("listThree");
				for (int i = 0; i < optJSONArray2.length(); i++) {
					JSONObject j = optJSONArray2.optJSONObject(i);
					int photoId = j.optInt("photoId");
					String photoUrl = j.optString("photoUrl");
					listThree.add(new ListOne(photoId, photoUrl));
				}
				setListOne();
			}

		});
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	private void setListOne() {

		MyGVOnlineAdapter onlineAdapter = new MyGVOnlineAdapter(listOne, 1);
		gv_odertailsefirst.setAdapter(onlineAdapter);
		setListTwo();

	}

	private void setListTwo() {
		// TODO Auto-generated method stub
		if (listOne.size() == 0) {
			countNub = 1;
		} else if (listTwo.size() == 0 && listOne.size() != 0) {
			countNub = 2;
		} else if (listThree.size() == 0 && listOne.size() != 0 && listTwo.size() != 0) {
			countNub = 3;
		} else if (listThree.size() != 0 && listOne.size() != 0 && listTwo.size() != 0) {
			countNub = 4;
		}
		MyGVOnlineAdapter onlineAdapter = new MyGVOnlineAdapter(listTwo, 2);
		gv_odertailsesecond.setAdapter(onlineAdapter);
		setListThree();
		if (status == 9 || status == 11 || countNub == 4 || status == 5 || status == 15 || status == 6) {
			tv_sure_oc.setVisibility(View.GONE);
			tv_sure_tuikuan.setVisibility(View.GONE);
			tv_sure_xiugai.setVisibility(View.GONE);
		}
	}

	public void showsDialog(final int orderId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示").setMessage("确认收货?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				sureGet(orderId);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.show();
	}

	private void sureGet(int orderId) {
		RequestParams params = new RequestParams();
		params.put("orderId", orderId);
		params.put("type", 2);
		showProgressDialog("确定中...");
		System.out.println("zuo==params=" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usersureGetProduct, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_oderdetails_custom.this, "收货成功", Toast.LENGTH_SHORT).show();
						tv_sure_oc.setVisibility(View.GONE);
						tv_sure_tuikuan.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void setListThree() {
		// TODO Auto-generated method stub
		MyGVOnlineAdapter onlineAdapter = new MyGVOnlineAdapter(listThree, 3);
		gv_odertailsthree.setAdapter(onlineAdapter);

	}

	class ListOne {
		int photoId;
		String photoUrl;

		public ListOne(int photoId, String photoUrl) {
			super();
			this.photoId = photoId;
			this.photoUrl = photoUrl;
		}
	}

	class ListThree {
		int photoId;
		String photoUrl;

		public ListThree(int photoId, String photoUrl) {
			super();
			this.photoId = photoId;
			this.photoUrl = photoUrl;
		}
	}

	class ListTwo {
		int photoId;
		String photoUrl;

		public ListTwo(int photoId, String photoUrl) {
			super();
			this.photoId = photoId;
			this.photoUrl = photoUrl;
		}
	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_oderdetails.setOnClickListener(this);
		tv_sure_ocd.setOnClickListener(this);
		tv_sure_oc.setOnClickListener(this);
		tv_sure_xiugai.setOnClickListener(this);
		tv_sure_tuikuan.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_oderdetails);

		initParams();
	}

	class MyGVOnlineAdapter extends BaseAdapter {
		List<ListOne> data;
		int flag;
		ArrayList<String> pics = new ArrayList<String>();

		public MyGVOnlineAdapter(List<ListOne> data, int flag) {
			super();
			this.data = data;
			this.flag = flag;
			for (int i = 0; i < data.size(); i++) {
				pics.add(data.get(i).photoUrl);
			}
		}

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
		public View getView(final int position, View v, ViewGroup parent) {
			v = View.inflate(Activity_oderdetails_custom.this, R.layout.item_image_post, null);
			ImageView im_commends = (ImageView) v.findViewById(R.id.im_post_pic);
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + data.get(position).photoUrl + "-small",
					im_commends, options);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) im_commends.getLayoutParams();
			params.width = w;
			im_commends.setLayoutParams(params);
			im_commends.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Activity_oderdetails_custom.this, Activity_LookPic.class);
					intent.putExtra("position", position);
					intent.putStringArrayListExtra("pics", pics);
					startActivity(intent);
				}
			});
			return v;
		}
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
		makeId = getIntent().getIntExtra("makeId", 0);
		name = getIntent().getStringExtra("name");
		hight = getIntent().getIntExtra("hight", 1);
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
		setPageEnd("定制详情");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("定制详情");
	}
}
