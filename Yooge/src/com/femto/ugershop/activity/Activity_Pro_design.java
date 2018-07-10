package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
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

public class Activity_Pro_design extends BaseActivity {
	private RelativeLayout rl_back_oderdetails;
	String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private List<String> filePath1;
	private List<String> filePath2;
	private List<String> filePath3;
	private List<String> upFilePath;
	private int w;
	private MBC mbc;
	private MyGVAdapter1 gvadapter1;
	private MyGVAdapter2 gvadapter2;
	private MyGVAdapter3 gvadapter3;
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
	private int hight = 1;
	private int myId;
	private DisplayImageOptions options;
	private List<ListOne> listOne;
	private List<ListOne> listTwo;
	private List<ListOne> listThree;
	private LinearLayout ll_gv1, ll_gv2, ll_gv3;
	private TextView tv_name_ocd, tv_time_ocd, tv_price_ocd, tv_nub_ocd, tv_count_ocd, tv_math_ocd, tv_status_ocd, tv_sure_ocd,
			tv_title_oderdetails, tv_faqi, tv_fist;
	private EditText tv_ordercode_ocd;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				dismissProgressDialog();
				filePath1.clear();
				listOne.clear();
				listThree.clear();
				listTwo.clear();
				Toast.makeText(Activity_Pro_design.this, "提交成功", Toast.LENGTH_SHORT).show();
				getData();
				break;

			default:
				break;
			}
		};
	};
	private String name;
	private AlertDialog dialog;
	private DecimalFormat df;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("设计订单");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("设计订单");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_oderdetails:
			finish();
			break;
		case R.id.tv_faqi:
			showFaqidialog();

			break;
		case R.id.tv_sure_ocd:

			// showProgressDialog("上传中...");
			// new Thread() {
			// public void run() {
			// for (int i = 1; i < filePath1.size(); i++) {
			// float yaso = 0;
			// if ((new File(filePath1.get(i))).length() > 2000000) {
			// yaso = 0.3f;
			// } else if ((new File(filePath1.get(i))).length() > 1000000) {
			// yaso = 0.5f;
			// } else {
			// yaso = 0.8f;
			// }
			// Bitmap resize_img =
			// resize_img(BitmapFactory.decodeFile(filePath1.get(i)), yaso);
			// File saveMyBitmap = saveMyBitmap("image" + i + ".jpg",
			// resize_img);
			// upFilePath.add(saveMyBitmap.getPath().toString());
			// System.out.println("zuo===file==" +
			// saveMyBitmap.getPath().toString() + "      size="
			// + (new File(filePath1.get(i))).length() + "   后=" +
			// saveMyBitmap.length());
			//
			// }
			// new Thread() {
			// public void run() {
			// post(AppFinalUrl.useruploadDesinImg, ddd());
			// };
			// }.start();
			// };
			// }.start();
			sendGoods();
			break;
		default:
			break;
		}
	}

	private void sendGoods() {
		RequestParams params = new RequestParams();
		params.put("orderId", makeId);
		params.put("postCode", tv_ordercode_ocd.getText().toString());
		params.put("postHome", "sf");
		if (tv_ordercode_ocd.getText().toString().length() == 0) {
			Toast.makeText(this, "请填写物流单号", Toast.LENGTH_SHORT).show();
			return;
		}
		showProgressDialog("发货中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usersendMakeProduct, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_Pro_design.this, "发货成功", Toast.LENGTH_SHORT).show();
						filePath1.clear();
						listOne.clear();
						listThree.clear();
						listTwo.clear();
						getData();
					} else {
						Toast.makeText(Activity_Pro_design.this, "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void showFaqidialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_faqidingdan, null);
		final EditText ed_danjia = (EditText) view.findViewById(R.id.ed_danjia);
		builder.setView(view).setTitle("发起生产订单!").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				if (ed_danjia.getText().length() == 0) {
					Toast.makeText(Activity_Pro_design.this, "请输入单价", Toast.LENGTH_SHORT).show();
				} else {
					dialog.dismiss();
					faqidingdan(Integer.parseInt(ed_danjia.getText().toString()));
				}

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

	private void faqidingdan(int price) {
		RequestParams params = new RequestParams();
		params.put("orderId", makeId);
		params.put("price", price);
		params.put("count", 1);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercreateOrderToBuyer, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_Pro_design.this, "发起成功", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					} else {
						Toast.makeText(Activity_Pro_design.this, "发起失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public List<NameValuePair> ddd() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("makeDeails.id", "" + makeId));
		params.add(new BasicNameValuePair("postCode", "" + tv_ordercode_ocd.getText().toString()));

		for (int i = 0; i < upFilePath.size(); i++) {
			if (listOne.size() == 0) {
				params.add(new BasicNameValuePair("other", upFilePath.get(i).toString()));
			} else if (listTwo.size() == 0 && listOne.size() != 0) {
				params.add(new BasicNameValuePair("pic", upFilePath.get(i).toString()));
			} else if (listThree.size() == 0 && listOne.size() != 0 && listTwo.size() != 0) {
				params.add(new BasicNameValuePair("mo", upFilePath.get(i).toString()));
			}

		}

		return params;
	}

	public String post(String url, List<NameValuePair> pairs) {

		String content = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext locaContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);

		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			for (int i = 0; i < pairs.size(); i++) {
				if (pairs.get(i).getName().equalsIgnoreCase("pic") || pairs.get(i).getName().equalsIgnoreCase("other")
						|| pairs.get(i).getName().equalsIgnoreCase("mo")) {
					if (pairs.get(i).getValue() != null) {
						entity.addPart(pairs.get(i).getName(), new FileBody(new File(pairs.get(i).getValue())));
					}
				} else {
					entity.addPart(pairs.get(i).getName(), new StringBody(pairs.get(i).getValue()));
				}
			}
			entity.addPart("token", new StringBody("" + MyApplication.token, Charset.forName("UTF-8")));
			httpPost.setEntity(entity);

			HttpEntity re = null;
			HttpResponse response = httpClient.execute(httpPost, locaContext);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				re = response.getEntity();
			}
			if (re != null) {
				content = EntityUtils.toString(re);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			JSONObject ojj = new JSONObject(content);
			String result = ojj.getString("result");
			if (result.equals("0")) {
				handler.sendEmptyMessage(1);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;

	}

	private void registMBC() {
		mbc = new MBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.select.pic");
		registerReceiver(mbc, filter);
	}

	class MBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("com.select.pic")) {

				ArrayList<String> f = intent.getStringArrayListExtra("files");
				for (int i = 0; i < f.size(); i++) {
					filePath1.add(f.get(i));
				}
				gvadapter1.notifyDataSetChanged();
				//
				// if (flag == 2) {
				// ArrayList<String> f =
				// intent.getStringArrayListExtra("files");
				// for (int i = 0; i < f.size(); i++) {
				// filePath2.add(f.get(i));
				// }
				// gvadapter2.notifyDataSetChanged();
				// }
				// if (flag == 3) {
				// ArrayList<String> f =
				// intent.getStringArrayListExtra("files");
				// for (int i = 0; i < f.size(); i++) {
				// filePath3.add(f.get(i));
				// }
				// gvadapter3.notifyDataSetChanged();
				// }
			}

		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		filePath1 = new ArrayList<String>();
		MyApplication.addActivity(this);
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
		tv_sure_ocd = (TextView) findViewById(R.id.tv_sure_ocd);
		rl_back_oderdetails = (RelativeLayout) findViewById(R.id.rl_back_oderdetails);
		gv_odertailsefirst = (MyGridView) findViewById(R.id.gv_odertailsefirst);
		gv_odertailsesecond = (MyGridView) findViewById(R.id.gv_odertailsesecond);
		gv_odertailsthree = (MyGridView) findViewById(R.id.gv_odertailsthree);
		tv_title_oderdetails = (TextView) findViewById(R.id.tv_title_oderdetails);
		tv_faqi = (TextView) findViewById(R.id.tv_faqi);
		tv_fist = (TextView) findViewById(R.id.tv_fist);
		df = new DecimalFormat("######0.00");
		tv_title_oderdetails.setText("" + name);
		tv_fist.setText("设计稿");
		getData();
	}

	private void getData() {
		RequestParams params = new RequestParams();
		params.put("makeId", makeId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMakeOrderInfoByType2, params, new JsonHttpResponseHandler() {

			private double percentage;

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo===" + response.toString());
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
				tv_name_ocd.setText("" + userName);
				tv_time_ocd.setText("" + createDate);
				tv_price_ocd.setText("" + price + "元");
				tv_nub_ocd.setText("" + count);
				tv_count_ocd.setText("" + count);
				tv_math_ocd.setText("" + price * count + "*" + percentage * 100 + "%=" + df.format(price * count * percentage)
						+ "元");
				if (postCode == null || postCode.equals("null")) {
					tv_ordercode_ocd.setHint("未填写");
				} else {
					tv_ordercode_ocd.setText("" + postCode);
				}
				if (status == 0) {
					tv_status_ocd.setText("待付款");
					tv_faqi.setVisibility(View.GONE);
					tv_sure_ocd.setVisibility(View.GONE);
					tv_ordercode_ocd.setVisibility(View.GONE);
				}
				if (status == 1) {
					tv_status_ocd.setText("待发货");
				}
				if (status == 2) {
					tv_status_ocd.setText("待收货");
				}
				if (status == 3) {
					tv_status_ocd.setText("交易完成");
					tv_faqi.setVisibility(View.GONE);
					tv_sure_ocd.setVisibility(View.GONE);
				}
				if (status == 4) {
					tv_status_ocd.setText("交易关闭");
					tv_sure_ocd.setVisibility(View.GONE);
				}
				if (status == 5) {
					tv_status_ocd.setText("申请退款中");
					tv_sure_ocd.setVisibility(View.GONE);
				}
				if (status == 6) {
					tv_status_ocd.setText("退款成功");
					tv_sure_ocd.setVisibility(View.GONE);
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
					tv_status_ocd.setText("申请退款(修改后的退款)");
				}
				if (status == 14) {
					tv_status_ocd.setText("退款成功(修改后的退款)");
					tv_faqi.setVisibility(View.GONE);
					tv_sure_ocd.setVisibility(View.GONE);

				}
				if (status == 15) {
					tv_status_ocd.setText("换货中");
					tv_faqi.setVisibility(View.GONE);

				}
				if (status == 16) {
					tv_status_ocd.setText("换货成功");
					tv_faqi.setVisibility(View.GONE);

				}
				JSONArray optJSONArray = response.optJSONArray("list");
				if (optJSONArray != null) {
					for (int i = 0; i < optJSONArray.length(); i++) {
						JSONObject j = optJSONArray.optJSONObject(i);
						int photoId = j.optInt("photoId");
						String photoUrl = j.optString("photoUrl");
						listOne.add(new ListOne(photoId, photoUrl));
					}
				}

				// JSONArray optJSONArray1 = response.optJSONArray("listTwo");
				// for (int i = 0; i < optJSONArray1.length(); i++) {
				// JSONObject j = optJSONArray1.optJSONObject(i);
				// int photoId = j.optInt("photoId");
				// String photoUrl = j.optString("photoUrl");
				// listTwo.add(new ListOne(photoId, photoUrl));
				// }
				//
				// JSONArray optJSONArray2 = response.optJSONArray("listThree");
				// for (int i = 0; i < optJSONArray2.length(); i++) {
				// JSONObject j = optJSONArray2.optJSONObject(i);
				// int photoId = j.optInt("photoId");
				// String photoUrl = j.optString("photoUrl");
				// listThree.add(new ListOne(photoId, photoUrl));
				// }
				setListOne();
			}

		});
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	private void setListOne() {

		if (listOne.size() != 0) {
			MyGVOnlineAdapter onlineAdapter = new MyGVOnlineAdapter(listOne, 1);
			gv_odertailsefirst.setAdapter(onlineAdapter);
			// setListTwo();
			ll_gv2.setVisibility(View.GONE);
			ll_gv3.setVisibility(View.GONE);
			ll_gv1.setVisibility(View.VISIBLE);
		} else {

			// ll_gv1.setVisibility(View.VISIBLE);
			// ll_gv2.setVisibility(View.GONE);
			// ll_gv3.setVisibility(View.GONE);
			// gvadapter1 = new MyGVAdapter1(filePath1);
			// gv_odertailsefirst.setAdapter(gvadapter1);

		}

	}

	private void setListTwo() {
		// TODO Auto-generated method stub
		if (listTwo.size() != 0) {
			MyGVOnlineAdapter onlineAdapter = new MyGVOnlineAdapter(listTwo, 2);
			gv_odertailsesecond.setAdapter(onlineAdapter);
			setListThree();
			ll_gv2.setVisibility(View.VISIBLE);
		} else {
			if (status == 7) {
				ll_gv2.setVisibility(View.VISIBLE);
			} else {
				ll_gv2.setVisibility(View.GONE);
			}
			ll_gv1.setVisibility(View.VISIBLE);

			ll_gv3.setVisibility(View.GONE);
			gvadapter1 = new MyGVAdapter1(filePath1);
			gv_odertailsesecond.setAdapter(gvadapter1);

		}
	}

	private void setListThree() {
		// TODO Auto-generated method stub
		if (listThree.size() != 0) {
			MyGVOnlineAdapter onlineAdapter = new MyGVOnlineAdapter(listThree, 3);
			gv_odertailsthree.setAdapter(onlineAdapter);
			ll_gv3.setVisibility(View.VISIBLE);
		} else {
			if (status == 9) {
				ll_gv3.setVisibility(View.VISIBLE);
			} else {
				ll_gv3.setVisibility(View.GONE);
			}
			ll_gv1.setVisibility(View.VISIBLE);
			ll_gv2.setVisibility(View.VISIBLE);
			gvadapter1 = new MyGVAdapter1(filePath1);
			gv_odertailsthree.setAdapter(gvadapter1);
		}
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
		tv_faqi.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_oderdetails);
		registMBC();
		initParams();
	}

	// public String post(String url, List<NameValuePair> pairs) {
	//
	// String content = null;
	// HttpClient httpClient = new DefaultHttpClient();
	// HttpContext locaContext = new BasicHttpContext();
	// HttpPost httpPost = new HttpPost(url);
	//
	// try {
	// MultipartEntity entity = new
	// MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	// for (int i = 0; i < pairs.size(); i++) {
	// if (pairs.get(i).getName().equalsIgnoreCase("pic") ||
	// pairs.get(i).getName().equalsIgnoreCase("cardfile")) {
	// if (pairs.get(i).getValue() != null) {
	// entity.addPart(pairs.get(i).getName(), new FileBody(new
	// File(pairs.get(i).getValue())));
	// }
	// } else {
	// entity.addPart(pairs.get(i).getName(), new
	// StringBody(pairs.get(i).getValue()));
	// }
	// }
	//
	// httpPost.setEntity(entity);
	//
	// HttpEntity re = null;
	// HttpResponse response = httpClient.execute(httpPost, locaContext);
	// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	// re = response.getEntity();
	// }
	// if (re != null) {
	// content = EntityUtils.toString(re);
	// }
	// } catch (ClientProtocolException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// try {
	// JSONObject ojj = new JSONObject(content);
	// String result = ojj.getString("result");
	// if (result.equals("0")) {
	// handler.sendEmptyMessage(1);
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return content;
	//
	// }

	// public List<NameValuePair> ddd() {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("discuss.indent.id", "" + orderId));
	// params.add(new BasicNameValuePair("discuss.content", "评论内容"));
	// for (int i = 0; i < upFilePath.size(); i++) {
	// params.add(new BasicNameValuePair("pic", upFilePath.get(i).toString()));
	// }
	//
	// return params;
	// }
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
			v = View.inflate(Activity_Pro_design.this, R.layout.item_image_post, null);
			ImageView im_commends = (ImageView) v.findViewById(R.id.im_post_pic);
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + data.get(position).photoUrl + "-small",
					im_commends, options);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) im_commends.getLayoutParams();
			params.width = w;
			im_commends.setLayoutParams(params);
			im_commends.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Activity_Pro_design.this, Activity_LookPic.class);
					intent.putExtra("position", position);
					intent.putStringArrayListExtra("pics", pics);
					startActivity(intent);
				}
			});
			return v;
		}
	}

	class MyGVAdapter1 extends BaseAdapter {
		List<String> data;

		public MyGVAdapter1(List<String> data) {
			super();
			this.data = data;
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
			v = View.inflate(Activity_Pro_design.this, R.layout.item_gv_pic, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_gvitem);
			if (position == 0) {
				im.setImageResource(R.drawable.addoder);
			} else {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(data.get(position), opts);
				opts.inSampleSize = computeInitialSampleSize(opts, -1, 299 * 299);
				// 这里一定要将其设置回false，因为之前我们将其设置成了true
				opts.inJustDecodeBounds = false;
				try {
					Bitmap bmp = BitmapFactory.decodeFile(data.get(position), opts);
					im.setImageBitmap(bmp);
				} catch (OutOfMemoryError err) {
				}
			}
			LayoutParams params = im.getLayoutParams();
			params.height = w;
			im.setLayoutParams(params);
			im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (position == 0) {
						// flag = 1;
						Intent intent = new Intent();
						intent.putExtra("nub", 9);
						intent.setClass(Activity_Pro_design.this, ImgFileListActivity.class);
						startActivity(intent);
					}

				}
			});
			return v;
		}
	}

	class MyGVAdapter2 extends BaseAdapter {
		List<String> data;

		public MyGVAdapter2(List<String> data) {
			super();
			this.data = data;
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
			v = View.inflate(Activity_Pro_design.this, R.layout.item_gv_pic, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_gvitem);
			if (position == 0) {
				im.setImageResource(R.drawable.addoder);
			} else {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(data.get(position), opts);
				opts.inSampleSize = computeInitialSampleSize(opts, -1, 299 * 299);
				// 这里一定要将其设置回false，因为之前我们将其设置成了true
				opts.inJustDecodeBounds = false;
				try {
					Bitmap bmp = BitmapFactory.decodeFile(data.get(position), opts);
					im.setImageBitmap(bmp);
				} catch (OutOfMemoryError err) {
				}
			}
			LayoutParams params = im.getLayoutParams();
			params.height = w;
			im.setLayoutParams(params);
			im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (position == 0) {
						// flag = 2;
						Intent intent = new Intent();
						intent.putExtra("nub", 9);
						intent.setClass(Activity_Pro_design.this, ImgFileListActivity.class);
						startActivity(intent);
					}

				}
			});
			return v;
		}
	}

	class MyGVAdapter3 extends BaseAdapter {
		List<String> data;

		public MyGVAdapter3(List<String> data) {
			super();
			this.data = data;
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
			v = View.inflate(Activity_Pro_design.this, R.layout.item_gv_pic, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_gvitem);
			if (position == 0) {
				im.setImageResource(R.drawable.addoder);
			} else {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(data.get(position), opts);
				opts.inSampleSize = computeInitialSampleSize(opts, -1, 299 * 299);
				// 这里一定要将其设置回false，因为之前我们将其设置成了true
				opts.inJustDecodeBounds = false;
				try {
					Bitmap bmp = BitmapFactory.decodeFile(data.get(position), opts);
					im.setImageBitmap(bmp);
				} catch (OutOfMemoryError err) {
				}
			}
			LayoutParams params = im.getLayoutParams();
			params.height = w;
			im.setLayoutParams(params);
			im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (position == 0) {
						// flag = 3;
						Intent intent = new Intent();
						intent.putExtra("nub", 9);
						intent.setClass(Activity_Pro_design.this, ImgFileListActivity.class);
						startActivity(intent);
					}

				}
			});
			return v;
		}
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,

	int minSideLength, int maxNumOfPixels) {

		double w = options.outWidth;

		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 :

		(int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

		int upperBound = (minSideLength == -1) ? 128 :

		(int) Math.min(Math.floor(w / minSideLength),

		Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {

			return lowerBound;

		}

		if ((maxNumOfPixels == -1) &&

		(minSideLength == -1)) {

			return 1;

		} else if (minSideLength == -1) {

			return lowerBound;

		} else {

			return upperBound;

		}

	}

	// 压缩bitmap
	private Bitmap resize_img(Bitmap bitmap, float pc) {
		System.out.println("zuo====压缩前width=" + bitmap.getWidth() + "   height=" + bitmap.getHeight());
		Matrix matrix = new Matrix();
		// Log.i("mylog2", "缩放比例--" + pc);
		matrix.postScale(pc, pc); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		bitmap.recycle();
		bitmap = null;
		System.gc();

		int width = resizeBmp.getWidth();
		int height = resizeBmp.getHeight();
		System.out.println("zuo====压缩后width=" + width + "   height=" + height);
		// Log.i("mylog2", "按比例缩小后宽度--" + width);
		// Log.i("mylog2", "按比例缩小后高度--" + height);

		return resizeBmp;
	}

	// 将压缩的bitmap保存到sdcard卡临时文件夹img_interim，用于上传

	public File saveMyBitmap(String filename, Bitmap bit) {
		System.out.println("zuo保存前with=" + bit.getWidth() + "  hight=" + bit.getHeight());
		File dir = new File(videopath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File f = new File(videopath + filename);
		try {
			f.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(f);
			bit.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			f = null;
			e1.printStackTrace();
		}

		return f;
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
}
