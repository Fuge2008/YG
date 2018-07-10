package com.femto.ugershop.activity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.selepic.ImgFileListActivity;
import com.femto.ugershop.view.MyGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_UpLoadPost extends BaseActivity {
	private RelativeLayout rl_back_uppost;
	private MBC mbc;
	private int w;
	private int maxHeight;
	private Drawable drawable;
	private TextView tv_suresendpost;
	private View view;
	private EditText ed_postmessage;
	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int CAMERA_WITH_DATA = 3023;
	private File mCurrentPhotoFile;// 照相机拍照得到的图片
	private static File mFile;
	private MyGridView gv_photo;
	private List<File> pics;
	private ArrayList<HashMap<String, Object>> imageItem;
	private Bitmap bmp;
	private SimpleAdapter simpleAdapter;
	private final int IMAGE_OPEN = 1;
	private String pathImage;
	private int myId;
	private List<String> filePath;
	private List<String> upFilePath;
	private CheckBox cb_apply;
	String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				dismissProgressDialog();
				Toast.makeText(Activity_UpLoadPost.this, "发表成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setAction("com.refresh.post");
				sendBroadcast(intent);
				finish();
				break;

			default:
				break;
			}
		};
	};
	private int type;
	private MyGVAdapter adapter;
	private int isGood = 0;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("上传帖子");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("上传帖子");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_uppost:
			finish();
			break;

		case R.id.tv_suresendpost:

			if (ed_postmessage.getText().toString().length() == 0) {
				Toast.makeText(this, "请输入帖子内容", Toast.LENGTH_SHORT).show();
				return;
			}
			if (filePath.size() == 1) {
				type = 1;
			} else {
				type = 2;
			}
			if (cb_apply.isChecked()) {
				isGood = 1;
			} else {
				isGood = 0;
			}
			showProgressDialog("上传中");
			new Thread() {
				public void run() {
					for (int i = 1; i < filePath.size(); i++) {
						int yaso = 0;
						if ((new File(filePath.get(i))).length() > 4000000) {
							yaso = 10;
						} else if ((new File(filePath.get(i))).length() > 2000000) {
							yaso = 5;
						} else if ((new File(filePath.get(i))).length() > 1000000) {
							yaso = 3;
						} else if ((new File(filePath.get(i))).length() > 200000) {
							yaso = 1;
						} else {
							yaso = 1;
						}

						// Bitmap resize_img =
						// resize_img(convertToBitmap(filePath.get(i), 0, 0),
						// 0.99f);
						File saveMyBitmap = saveMyBitmap("image" + i + ".jpg", convertToBitmap(filePath.get(i), yaso));
						upFilePath.add(saveMyBitmap.getPath().toString());
						// System.out.println("zuo===file==" +
						// saveMyBitmap.getPath().toString() + "      size="
						// + (new File(filePath.get(i))).length() + "   后=" +
						// saveMyBitmap.length());

					}
					new Thread() {
						public void run() {

							post(AppFinalUrl.userpostFriendCircle,
									ddd(type, ed_postmessage.getText().toString(), isGood, upFilePath));

						};
					}.start();
				};
			}.start();

			// new Thread() {
			// public void run() {
			// try {
			// post(AppFinalUrl.userpostFriendCircle, getParams(),
			// getFiles());
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// };
			// }.start();
			break;

		default:
			break;
		}
	}

	// 压缩
	public Bitmap convertToBitmap(String path, int w) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = w;// 压缩比例
		// 这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, opts);
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
			adapter.notifyDataSetChanged();
		}
	}

	public String post(String url, List<NameValuePair> pairs) {

		String content = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext locaContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));

		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			for (int i = 0; i < pairs.size(); i++) {
				if (pairs.get(i).getName().equalsIgnoreCase("pic") || pairs.get(i).getName().equalsIgnoreCase("cardfile")) {
					if (pairs.get(i).getValue() != null) {
						entity.addPart(pairs.get(i).getName(), new FileBody(new File(pairs.get(i).getValue())));
					}
				} else {
					entity.addPart(pairs.get(i).getName(), new StringBody(pairs.get(i).getValue()));
				}
			}
			entity.addPart("friendCircle.msg",
					new StringBody(Base64.encodeToString(ed_postmessage.getText().toString().getBytes("UTF-8"), Base64.DEFAULT),
							Charset.forName("UTF-8")));
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
		System.out.println("zuo====" + content);

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

	public List<NameValuePair> ddd(int type, String msg, int isgood, List<String> fPath) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("type", "" + type));
		// params.add(new BasicNameValuePair("friendCircle.msg", msg));
		// params.add(new BasicNameValuePair("phoneType", "xiaowang"));
		params.add(new BasicNameValuePair("friendCircle.user.id", "" + myId));
		params.add(new BasicNameValuePair("friendCircle.isGood", "" + isgood));
		if (type != 0) {
			for (int i = 0; i < fPath.size(); i++) {
				params.add(new BasicNameValuePair("pic", fPath.get(i).toString()));
			}
		}

		return params;
	}

	@Override
	public void initView() {

		registMBC();
		// TODO Auto-generated method stub
		filePath = new ArrayList<String>();
		upFilePath = new ArrayList<String>();
		filePath.add("aa");
		rl_back_uppost = (RelativeLayout) findViewById(R.id.rl_back_uppost);
		ed_postmessage = (EditText) findViewById(R.id.ed_postmessage);
		gv_photo = (MyGridView) findViewById(R.id.gv_photo1);
		tv_suresendpost = (TextView) findViewById(R.id.tv_suresendpost);
		cb_apply = (CheckBox) findViewById(R.id.cb_apply);
		adapter = new MyGVAdapter();
		gv_photo.setAdapter(adapter);
	}

	private void registMBC() {
		mbc = new MBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.select.pic");
		registerReceiver(mbc, filter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mbc);
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
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_uppost.setOnClickListener(this);
		tv_suresendpost.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_uploadpost);
		initParams();
		MyApplication.addActivity(this);
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(this, 24);
		w = (screenWidth - dp2px) / 3;

	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public void closekey() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
			v = View.inflate(Activity_UpLoadPost.this, R.layout.item_gv_pic, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_gvitem);
			if (position == 0) {
				im.setImageResource(R.drawable.addoder);
			} else {
				File imageFile = new File(filePath.get(position));
				Picasso.with(Activity_UpLoadPost.this).load(imageFile).placeholder(R.drawable.tianc).error(R.drawable.tianc)
						.resize(180, 180).centerCrop().into(im);
			}
			LayoutParams params = (LayoutParams) im.getLayoutParams();
			params.height = w;
			im.setLayoutParams(params);
			im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (position == 0) {
						Intent intent = new Intent();
						intent.putExtra("nub", 9);
						intent.setClass(Activity_UpLoadPost.this, ImgFileListActivity.class);
						startActivity(intent);
					}

				}
			});
			im.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					if (position != 0) {
						showExit(position);
					}
					return true;
				}
			});
			return v;
		}
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);

	}

	private void showExit(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Activity_UpLoadPost.this);
		builder.setMessage("确定删除此图片?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				filePath.remove(position);
				adapter.notifyDataSetChanged();

			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();
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
			bit.compress(Bitmap.CompressFormat.JPEG, 99, fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			f = null;
			e1.printStackTrace();
		}

		return f;
	}

}
