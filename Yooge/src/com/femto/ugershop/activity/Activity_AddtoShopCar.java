package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chatuidemo.activity.ImageGridActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.view.MyGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_AddtoShopCar extends SwipeBackActivity implements OnItemClickListener {
	private RelativeLayout rl_back_purchase;
	private TextView tv_surebuy, tv_title, tv_tomodecar;
	private MyGridView gv_size, gv_color;
	private MyGVSizeAdapter gvsizeadapter;
	private MyGVColorAdapter gvcoloradapter;
	private ImageView im_count, im_reduce;
	private EditText ed_count;
	private int count = 1;
	private List<String> datas_size;
	private int flag = 0;
	private int myId, productId;
	private DisplayImageOptions options;
	private List<String> data_color;
	private int flag1 = 0;
	private CheckBox cb_celefit;
	private int flag2 = 0;
	private boolean isclick = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back_purchase:
			finish();
			break;
		case R.id.tv_surebuy:
			if (count > 0) {
				if (cb_celefit.isChecked()) {
					isclick = false;
					checkMode();
				} else {

					addtoshopcar();
				}

			} else {
				Toast.makeText(Activity_AddtoShopCar.this, "请选择数量", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.im_reduce:

			if (count > 0) {
				ed_count.setText("" + (--count));
			}
			break;
		case R.id.im_count:
			ed_count.setText("" + (++count));
			break;
		case R.id.cb_celefit:
			if (cb_celefit.isChecked()) {
				flag = -1;
				flag2 = 1;
				gvcoloradapter.notifyDataSetChanged();
				gvsizeadapter.notifyDataSetChanged();
			} else {
				flag = 0;
				flag2 = 0;
				gvcoloradapter.notifyDataSetChanged();
				gvsizeadapter.notifyDataSetChanged();
			}
			break;
		case R.id.tv_tomodecar:
			isclick = true;
			Intent intent_mode_c = new Intent(Activity_AddtoShopCar.this, Activity_NewDZ.class);
			startActivityForResult(intent_mode_c, 102);
			break;
		default:
			break;
		}

	}

	@Override
	public void initView() {
		MyApplication.addActivity(this);
		datas_size = new ArrayList<String>();

		data_color = new ArrayList<String>();
		rl_back_purchase = (RelativeLayout) findViewById(R.id.rl_back_purchase);
		tv_surebuy = (TextView) findViewById(R.id.tv_surebuy);

		gv_size = (MyGridView) findViewById(R.id.gv_size);
		gv_color = (MyGridView) findViewById(R.id.gv_color);
		im_reduce = (ImageView) findViewById(R.id.im_reduce);
		im_count = (ImageView) findViewById(R.id.im_count);
		ed_count = (EditText) findViewById(R.id.ed_count);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_tomodecar = (TextView) findViewById(R.id.tv_tomodecar);
		cb_celefit = (CheckBox) findViewById(R.id.cb_celefit);
		tv_title.setText("添加商品");
		ed_count.setText("" + count);
		gvcoloradapter = new MyGVColorAdapter();
		gvsizeadapter = new MyGVSizeAdapter();
		gv_size.setAdapter(gvsizeadapter);
		gv_color.setAdapter(gvcoloradapter);
		gv_size.setOnItemClickListener(this);
		gv_color.setOnItemClickListener(this);
		gv_size.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gv_color.setSelector(new ColorDrawable(Color.TRANSPARENT));
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	public void getColor() {
		RequestParams params = new RequestParams();
		params.put("product.id", myId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetProductColor, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuocolor" + response.toString());
				JSONArray optJSONArray = response.optJSONArray("List");
				for (int i = 0; i < optJSONArray.length(); i++) {
					JSONObject j = optJSONArray.optJSONObject(i);
					data_color.add(j.optString("color"));
				}
				if (data_color.size() == 0) {
					data_color.add("无");
				}
				gvcoloradapter.notifyDataSetChanged();

				JSONArray ChiMaJSONArray = response.optJSONArray("colorList");
				if (ChiMaJSONArray != null) {
					for (int k = 0; k < ChiMaJSONArray.length(); k++) {
						JSONObject jj = ChiMaJSONArray.optJSONObject(k);
						String chiCun = jj.optString("chiCun");
						datas_size.add(chiCun);
					}
				}

				if (datas_size.size() == 0) {
					datas_size.add("无");
				}
				gvsizeadapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void Control() {
		rl_back_purchase.setOnClickListener(this);
		tv_surebuy.setOnClickListener(this);
		im_count.setOnClickListener(this);
		im_reduce.setOnClickListener(this);
		cb_celefit.setOnClickListener(this);
		tv_tomodecar.setOnClickListener(this);
		getColor();
		if (flag2 == 1) {
			cb_celefit.setChecked(true);
			flag = -1;

		}
	}

	private void addtoshopcar() {
		RequestParams params = new RequestParams();
		params.put("shopCar.user.id", myId);
		params.put("shopCar.product.id", productId);
		params.put("shopCar.nub", count);
		if (flag2 != 1) {
			params.put("shopCar.chiMa", datas_size.get(flag));
		} else {
			params.put("shopCar.chiMa", "-1");
		}

		if (data_color.size() != 0) {
			params.put("shopCar.colour", data_color.get(flag1));
		}

		showProgressDialog("提交中...");
		System.out.println("zuo" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userpushProductToMyShopCar, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_AddtoShopCar.this, "添加成功", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(Activity_AddtoShopCar.this, "添加失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
		productId = getIntent().getIntExtra("productId", 0);
		flag2 = getIntent().getIntExtra("flag", 0);
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
	public void setContentView() {
		setContentView(R.layout.acitivity_purchase);
		initParams();
	}

	class MyGVSizeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas_size == null ? 0 : datas_size.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return datas_size.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_AddtoShopCar.this, R.layout.item_purchase, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_purchasename);
			tv_sort_name.setText(datas_size.get(position));
			if (flag == position) {
				tv_sort_name.setBackgroundResource(R.drawable.bg_purchase);
				tv_sort_name.setTextColor(getResources().getColor(R.color.white));
			} else {
				tv_sort_name.setBackgroundColor(getResources().getColor(R.color.white));
				tv_sort_name.setTextColor(getResources().getColor(R.color.black));
			}
			return v;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		if (parent == gv_size) {
			if (flag2 != 1) {
				flag = position;
				gvsizeadapter.notifyDataSetChanged();
			}

		} else if (parent == gv_color) {
			flag1 = position;
			gvcoloradapter.notifyDataSetChanged();
		}

	}

	class MyGVColorAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data_color == null ? 0 : data_color.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data_color.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_AddtoShopCar.this, R.layout.item_purchase, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_purchasename);
			tv_sort_name.setText(data_color.get(position));
			if (flag1 == position) {
				tv_sort_name.setBackgroundResource(R.drawable.bg_purchase);
				tv_sort_name.setTextColor(getResources().getColor(R.color.white));
			} else {
				tv_sort_name.setBackgroundColor(getResources().getColor(R.color.white));
				tv_sort_name.setTextColor(getResources().getColor(R.color.black));
			}
			return v;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("购买");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("购买");
	}

	private void checkMode() {
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		showProgressDialog("正在检查模特卡是否完善...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyMedolCardIsOk, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						// cb_checkmode.setChecked(true);
						showToast("您的模特卡已完善", 0);
						if (!isclick) {
							addtoshopcar();
						}

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
				isclick = true;
				Intent intent_mode_c = new Intent(Activity_AddtoShopCar.this, Activity_NewDZ.class);
				startActivityForResult(intent_mode_c, 102);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				flag = 0;
				flag2 = 0;
				gvcoloradapter.notifyDataSetChanged();
				gvsizeadapter.notifyDataSetChanged();
			}
		}).show();
	}

	private Bitmap getVideoThumbnail2(String videoPath, int width, int height, int kind) {
		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	public void seleVideo() {
		Intent intent = new Intent(Activity_AddtoShopCar.this, ImageGridActivity.class);
		startActivityForResult(intent, 23);
	}
}
