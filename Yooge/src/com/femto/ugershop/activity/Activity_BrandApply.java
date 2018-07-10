package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.selepic.ImgFileListActivity;
import com.femto.ugershop.view.MyGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
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
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_BrandApply extends SwipeBackActivity {
	private RelativeLayout rl_back_brand;
	private ImageView im_sele_dp;
	private View view;
	private RelativeLayout im_arraleft, rl_sele_dp;
	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int CAMERA_WITH_DATA = 3023;
	private File mCurrentPhotoFile;// 照相机拍照得到的图片
	private PopupWindow ppw_price;
	private View customView;
	private List<String> data;
	private MyAdapter adapter;
	private TextView tv_seletype, tv_sure_pay;
	private ListView lv;
	private List<String> filePath;
	private List<String> upFilePath;
	private MyGridView gv_apply;
	private String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private MyGVAdapter gvAdapter;
	private MBC mbc;
	private int myId;
	private EditText ed_bradname, ed_nub_brand;
	private List<Brand> brand;
	private int type = -1;
	private String price = "";
	private String orderCode;
	private int count;
	private double doubleprice;
	private String productName;
	private int w;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_brand:
			finish();
			break;

		case R.id.im_sele_dp:

			break;
		case R.id.tv_sure_pay:
			if (ed_bradname.getText().toString().length() == 0) {
				Toast.makeText(Activity_BrandApply.this, "请输入品牌名称", Toast.LENGTH_SHORT).show();
				return;
			}
			if (price.equals("")) {
				Toast.makeText(Activity_BrandApply.this, "请选择吊牌", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_nub_brand.getText().toString().length() == 0) {
				Toast.makeText(Activity_BrandApply.this, "请选择数量", Toast.LENGTH_SHORT).show();
				return;
			}
			if (filePath.size() == 0) {
				Toast.makeText(Activity_BrandApply.this, "请选择logo图片", Toast.LENGTH_SHORT).show();
				return;
			}
			showdialog();
			// upmessge();
			break;
		case R.id.im_arraleft:
			Intent intent = new Intent();
			intent.putExtra("nub", 1);
			intent.setClass(Activity_BrandApply.this, ImgFileListActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_sele_dp1:
			if (ppw_price != null && ppw_price.isShowing()) {
				// ppw_price.setFocusable(false);
				ppw_price.dismiss();
			} else {
				initPpwPrice();
				ppw_price.showAsDropDown(tv_seletype, 0, 1);
			}
			break;
		default:
			break;
		}
	}

	private void upmessge() {
		RequestParams params = new RequestParams();
		if (ed_bradname.getText().toString().length() == 0) {
			Toast.makeText(Activity_BrandApply.this, "请输入品牌名称", Toast.LENGTH_SHORT).show();
			return;
		} else {
			params.put("userBrand.name", ed_bradname.getText().toString());
		}

		if (ed_nub_brand.getText().toString().length() != 0 && Integer.parseInt(ed_nub_brand.getText().toString()) > 0) {
			params.put("userBrand.count", Integer.parseInt(ed_nub_brand.getText().toString()));
		} else {
			Toast.makeText(Activity_BrandApply.this, "请输入数量", Toast.LENGTH_SHORT).show();
			return;
		}

		params.put("userBrand.user.id", myId);
		if (filePath.size() == 0) {
			Toast.makeText(Activity_BrandApply.this, "请选择一张图片", Toast.LENGTH_SHORT).show();
			return;
		} else {
			try {
				params.put("doc", new File(filePath.get(0)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (type == -1) {
			Toast.makeText(Activity_BrandApply.this, "请选择吊牌", Toast.LENGTH_SHORT).show();
			return;
		} else {
			params.put("userBrand.type", type);
		}
		params.put("userBrand.price", price);
		showProgressDialog("申请中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddUserBrand, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo===response=" + response.toString());
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						orderCode = response.optString("orderCode");
						count = response.optInt("count");
						doubleprice = response.optDouble("price");
						if (doubleprice != 0 && count != 0 && orderCode.length() != 0) {
							Intent intent = new Intent(Activity_BrandApply.this, Activity_SelectPay.class);
							intent.putExtra("orderCode", orderCode);
							intent.putExtra("price", "" + doubleprice * count);
							intent.putExtra("productname", productName);
							startActivity(intent);
						} else {
							Toast.makeText(Activity_BrandApply.this, "申请失败", Toast.LENGTH_SHORT).show();
						}

					} else {
						Toast.makeText(Activity_BrandApply.this, "申请失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	private void showdialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示")
				.setMessage("您确定支付" + (Double.parseDouble(price) * Integer.parseInt(ed_nub_brand.getText().toString())) + "元")

				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						upmessge();
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
		MyApplication.addActivity(this);
		rl_back_brand = (RelativeLayout) findViewById(R.id.rl_back_brand);
		im_sele_dp = (ImageView) findViewById(R.id.im_sele_dp);
		im_arraleft = (RelativeLayout) findViewById(R.id.im_arraleft);
		rl_sele_dp = (RelativeLayout) findViewById(R.id.rl_sele_dp1);
		tv_seletype = (TextView) findViewById(R.id.tv_seletype);
		gv_apply = (MyGridView) findViewById(R.id.gv_apply);
		ed_bradname = (EditText) findViewById(R.id.ed_bradname);
		ed_nub_brand = (EditText) findViewById(R.id.ed_nub_brand);
		tv_sure_pay = (TextView) findViewById(R.id.tv_sure_pay);
		gvAdapter = new MyGVAdapter();
		adapter = new MyAdapter();
		gv_apply.setAdapter(gvAdapter);
		getData();
	}

	private void getData() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetLogoImg, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo==response==" + response.toString());
				try {
					JSONArray jsonArray = response.getJSONArray("list");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String name = j.optString("name");
						String price = j.optString("price");
						int type = j.optInt("type");
						brand.add(new Brand(name, price, type));
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
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
				filePath.clear();

				for (int i = 0; i < f.size(); i++) {
					filePath.add(f.get(i));
				}

			}
			System.out.println("zuo==" + filePath.size());
			gvAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_brand.setOnClickListener(this);
		im_sele_dp.setOnClickListener(this);
		tv_sure_pay.setOnClickListener(this);
		im_arraleft.setOnClickListener(this);
		rl_sele_dp.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_brandapply);
		initParams();
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(this, 16);
		w = (screenWidth - dp2px) / 3;
		data = new ArrayList<String>();
		filePath = new ArrayList<String>();
		upFilePath = new ArrayList<String>();
		brand = new ArrayList<Brand>();

		registMBC();
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
	}

	public void closekey() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public void initPpwPrice() {
		customView = View.inflate(Activity_BrandApply.this, R.layout.lsitview, null);
		ppw_price = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		ppw_price.setFocusable(true);
		ppw_price.setBackgroundDrawable(new BitmapDrawable());
		ppw_price.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		lv = (ListView) customView.findViewById(R.id.lv);

		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				tv_seletype.setText(brand.get(position).name + "/" + brand.get(position).price + "元");
				ppw_price.dismiss();
				type = brand.get(position).type;
				price = brand.get(position).price;
				productName = brand.get(position).name;
			}
		});
	}

	class Brand {
		String name, price;
		int type;

		public Brand(String name, String price, int type) {
			super();
			this.name = name;
			this.price = price;
			this.type = type;
		}
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return brand == null ? 0 : brand.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return brand.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_BrandApply.this, R.layout.item_selepd, null);
			TextView tv_dp = (TextView) v.findViewById(R.id.tv_dp);
			TextView tv_price = (TextView) v.findViewById(R.id.tv_price);
			// cb_dp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//
			// @Override
			// public void onCheckedChanged(CompoundButton buttonView, boolean
			// isChecked) {
			// if (isChecked) {
			//
			// }
			// }
			// });
			tv_dp.setText(brand.get(position).name);
			tv_price.setText("" + brand.get(position).price + "元");
			return v;
		}
	}

	class MyGVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return filePath == null ? 0 : filePath.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return filePath.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			v = View.inflate(Activity_BrandApply.this, R.layout.item_gv_pic, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_gvitem);

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath.get(position), opts);
			opts.inSampleSize = computeInitialSampleSize(opts, -1, 299 * 299);
			// 这里一定要将其设置回false，因为之前我们将其设置成了true
			opts.inJustDecodeBounds = false;
			try {
				Bitmap bmp = BitmapFactory.decodeFile(filePath.get(position), opts);
				im.setImageBitmap(bmp);
			} catch (OutOfMemoryError err) {
			}

			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) im.getLayoutParams();
			params.height = w;
			params.width = w;
			im.setLayoutParams(params);
			im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					Intent intent = new Intent();
					intent.putExtra("nub", 1);
					intent.setClass(Activity_BrandApply.this, ImgFileListActivity.class);
					startActivity(intent);

				}
			});
			return v;
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("品牌申请");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("品牌申请");
	}
}
