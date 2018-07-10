package com.femto.ugershop.activity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_UpLoadPost.MBC;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.selepic.ImgFileListActivity;
import com.femto.ugershop.view.MyGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class Activity_Commends_Store extends SwipeBackActivity {
	private MyGridView gv_photoadd;
	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int CAMERA_WITH_DATA = 3023;
	private File mCurrentPhotoFile;// 照相机拍照得到的图片
	private static File mFile;
	private MyGridView gv_photo;
	private List<File> pics;
	private MyGVAdapter gvadapter;
	private View view;
	private ImageView im_photoadd, im_goodpic;
	private TextView tv_surecommends, tv_goodname, tv_goodprice;
	private int orderId;
	private EditText ed_commends;
	private RelativeLayout rl_back_commends;
	private List<String> filePath;
	private List<String> upFilePath;
	private MBC mbc;
	private int score = 1;
	private ImageView im_start1, im_start2, im_start3, im_start4, im_start5;
	String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				dismissProgressDialog();
				Toast.makeText(Activity_Commends_Store.this, "评论成功", Toast.LENGTH_SHORT).show();
				finish();
				break;

			default:
				break;
			}
		};
	};
	private String name;
	private String pic;
	private double price;
	private DisplayImageOptions options;
	private int w;
	private int type = 0;

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		pics = new ArrayList<File>();
		MyApplication.addActivity(this);
		filePath = new ArrayList<String>();
		upFilePath = new ArrayList<String>();
		filePath.add("aa");
		gv_photoadd = (MyGridView) findViewById(R.id.gv_photoadd);
		im_photoadd = (ImageView) findViewById(R.id.im_photoadd);
		tv_surecommends = (TextView) findViewById(R.id.tv_surecommends);
		ed_commends = (EditText) findViewById(R.id.ed_commends);
		rl_back_commends = (RelativeLayout) findViewById(R.id.rl_back_commends);
		gvadapter = new MyGVAdapter();
		gv_photoadd.setAdapter(gvadapter);
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(this, 120);
		w = (screenWidth - dp2px) / 3;
		// 评分
		im_start1 = (ImageView) findViewById(R.id.im_start1);
		im_start2 = (ImageView) findViewById(R.id.im_start2);
		im_start3 = (ImageView) findViewById(R.id.im_start3);
		im_start4 = (ImageView) findViewById(R.id.im_start4);
		im_start5 = (ImageView) findViewById(R.id.im_start5);
		im_goodpic = (ImageView) findViewById(R.id.im_goodpic);
		// tv_goodname, tv_goodprice
		tv_goodname = (TextView) findViewById(R.id.tv_goodname);
		tv_goodprice = (TextView) findViewById(R.id.tv_goodprice);
		im_goodpic = (ImageView) findViewById(R.id.im_goodpic);
		tv_goodname.setText("" + name);
		tv_goodprice.setText("¥" + price);
		ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + pic, im_goodpic, options);
		;
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private String upUrl = AppFinalUrl.useraddDiscussForShopOrMake;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.im_photoadd:
			showPhotoDialog();
			break;
		case R.id.tv_surecommends:
			showProgressDialog("上传中...");
			new Thread() {
				public void run() {
					for (int i = 1; i < filePath.size(); i++) {
						float yaso = 0;
						if ((new File(filePath.get(i))).length() > 2000000) {
							yaso = 0.3f;
						} else if ((new File(filePath.get(i))).length() > 1000000) {
							yaso = 0.6f;
						} else {
							yaso = 0.99f;
						}
						Bitmap resize_img = resize_img(BitmapFactory.decodeFile(filePath.get(i)), yaso);
						File saveMyBitmap = saveMyBitmap("image" + i + ".jpg", resize_img);
						upFilePath.add(saveMyBitmap.getPath().toString());
						System.out.println("zuo===file==" + saveMyBitmap.getPath().toString() + "      size="
								+ (new File(filePath.get(i))).length() + "   后=" + saveMyBitmap.length());

					}
					new Thread() {
						public void run() {
							post(upUrl, ddd());
						};
					}.start();
				};
			}.start();

			// commends();

			break;
		case R.id.rl_back_commends:
			finish();
			break;
		case R.id.im_start1:
			score = 1;
			im_start1.setImageResource(R.drawable.newstar2);
			im_start2.setImageResource(R.drawable.newstar1);
			im_start3.setImageResource(R.drawable.newstar1);
			im_start4.setImageResource(R.drawable.newstar1);
			im_start5.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start2:
			score = 2;
			im_start1.setImageResource(R.drawable.newstar2);
			im_start2.setImageResource(R.drawable.newstar2);
			im_start3.setImageResource(R.drawable.newstar1);
			im_start4.setImageResource(R.drawable.newstar1);
			im_start5.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start3:
			score = 3;
			im_start1.setImageResource(R.drawable.newstar2);
			im_start2.setImageResource(R.drawable.newstar2);
			im_start3.setImageResource(R.drawable.newstar2);
			im_start4.setImageResource(R.drawable.newstar1);
			im_start5.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start4:
			score = 4;
			im_start1.setImageResource(R.drawable.newstar2);
			im_start2.setImageResource(R.drawable.newstar2);
			im_start3.setImageResource(R.drawable.newstar2);
			im_start4.setImageResource(R.drawable.newstar2);
			im_start5.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start5:
			score = 5;
			im_start1.setImageResource(R.drawable.newstar2);
			im_start2.setImageResource(R.drawable.newstar2);
			im_start3.setImageResource(R.drawable.newstar2);
			im_start4.setImageResource(R.drawable.newstar2);
			im_start5.setImageResource(R.drawable.newstar2);
			break;
		default:
			break;
		}
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
					filePath.add(f.get(i));
				}

			}
			gvadapter.notifyDataSetChanged();
		}
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		im_photoadd.setOnClickListener(this);
		tv_surecommends.setOnClickListener(this);
		rl_back_commends.setOnClickListener(this);
		im_start1.setOnClickListener(this);
		im_start2.setOnClickListener(this);
		im_start3.setOnClickListener(this);
		im_start4.setOnClickListener(this);
		im_start5.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_commends_store);
		initParams();
		registMBC();
	}

	private void initParams() {
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		orderId = getIntent().getIntExtra("orderId", -1);
		type = getIntent().getIntExtra("type", -1);
		price = getIntent().getDoubleExtra("price", 0);
		pic = getIntent().getStringExtra("pic");
		name = getIntent().getStringExtra("name");
		if (type == 1) {
			upUrl = AppFinalUrl.useraddDiscussToMakeProduct;
		}
	}

	private void showPhotoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Commends_Store.this);
		view = View.inflate(Activity_Commends_Store.this, R.layout.dialog_accompany, null);

		builder.setView(view);
		final AlertDialog dialog = builder.create();
		RelativeLayout rl_local = (RelativeLayout) view.findViewById(R.id.rl_localaccompany);

		RelativeLayout rl_sure = (RelativeLayout) view.findViewById(R.id.rl_seachaccompany);
		// 本地上传
		rl_local.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doPickPhotoFromGallery();
				dialog.dismiss();

			}
		});
		// 拍摄上传
		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doTakePhoto();
				dialog.dismiss();

			}
		});

		dialog.show();

	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyy-MM-ddHH:mm:ss");
		return dateFormat.format(date) + ".jpg";
	}

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
			Bitmap photo = data.getParcelableExtra("data");
			Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoFile.getPath());
			int width = bmp.getWidth();
			int height = bmp.getHeight();
			// LinearLayout.LayoutParams params = (LayoutParams) im_photo
			// .getLayoutParams();
			// params.height = (height * w) / width;
			// im_photo.setLayoutParams(params);
			// im_photo.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoFile
			// .getAbsolutePath().toString()));
			pics.add(mCurrentPhotoFile);
			gvadapter.notifyDataSetChanged();
			break;
		}
		case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			doCropPhoto(mCurrentPhotoFile);
			break;
		}

		}
	}

	protected void doCropPhoto(File f) {
		try {
			// 启动gallery去剪辑这个照片
			final Intent intent = getCropImageIntent(Uri.fromFile(f));
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (Exception e) {
			Toast.makeText(this, "", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Constructs an intent for image cropping. 调用图片剪辑程序
	 */
	public Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 4);
		intent.putExtra("aspectY", 3);
		intent.putExtra("outputX", 299);
		intent.putExtra("outputY", 225);
		intent.putExtra("output", Uri.fromFile(mCurrentPhotoFile));
		intent.putExtra("return-data", true);
		return intent;
	}

	// @Override
	// public boolean onTouch(View arg0, MotionEvent arg1) {
	// closekey();
	// return false;
	// }
	/**
	 * 拍照获取图片
	 * 
	 */
	protected void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
			PHOTO_DIR.mkdirs();// 创建照片的存储目录
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "", Toast.LENGTH_LONG).show();
		}
	}

	// 封装请求Gallery的intent
	public Intent getPhotoPickIntent() {
		mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 4);
		intent.putExtra("aspectY", 3);
		intent.putExtra("outputX", 299);
		intent.putExtra("outputY", 225);
		intent.putExtra("output", Uri.fromFile(mCurrentPhotoFile));
		intent.putExtra("return-data", true);
		return intent;
	}

	// 请求Gallery程序
	protected void doPickPhotoFromGallery() {
		try {
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, " ", Toast.LENGTH_LONG).show();
		}
	}

	public void closekey() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public String post(String url, List<NameValuePair> pairs) {

		String content = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext locaContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);

		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			for (int i = 0; i < pairs.size(); i++) {
				if (pairs.get(i).getName().equalsIgnoreCase("pic") || pairs.get(i).getName().equalsIgnoreCase("cardfile")) {
					if (pairs.get(i).getValue() != null) {
						entity.addPart(pairs.get(i).getName(), new FileBody(new File(pairs.get(i).getValue())));
					}
				} else {
					// entity.addPart(pairs.get(i).getName(), new
					// StringBody(pairs.get(i).getValue()));
				}
			}
			entity.addPart("discuss.content", new StringBody(ed_commends.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("discuss.score", new StringBody("" + score, Charset.forName("UTF-8")));

			if (type == 1) {
				entity.addPart("discuss.makeProductIndent.id", new StringBody("" + orderId, Charset.forName("UTF-8")));
			} else {
				entity.addPart("discuss.indent.id", new StringBody("" + orderId, Charset.forName("UTF-8")));
			}

			// params.add(new BasicNameValuePair("discuss.indent.id", "" +
			// orderId));
			System.out.println("zuo=orderId==" + orderId + "   " + ed_commends.getText().toString());
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

	public List<NameValuePair> ddd() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (int i = 0; i < upFilePath.size(); i++) {
			params.add(new BasicNameValuePair("pic", upFilePath.get(i).toString()));
		}

		return params;
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
			v = View.inflate(Activity_Commends_Store.this, R.layout.item_gv_pic, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_gvitem);
			if (position == 0) {
				im.setImageResource(R.drawable.photo02);
			} else {
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
			}
			LayoutParams params = im.getLayoutParams();
			params.height = w;
			im.setLayoutParams(params);
			im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (position == 0) {
						Intent intent = new Intent();
						intent.putExtra("nub", 5);
						intent.setClass(Activity_Commends_Store.this, ImgFileListActivity.class);
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
			bit.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			f = null;
			e1.printStackTrace();
		}

		return f;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("评价商城商品");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("评价商城商品");
	}
}
