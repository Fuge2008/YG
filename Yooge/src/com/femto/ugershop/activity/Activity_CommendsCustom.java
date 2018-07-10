package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_Commends_Store.MBC;
import com.femto.ugershop.activity.Activity_Commends_Store.MyGVAdapter;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.selepic.ImgFileListActivity;
import com.femto.ugershop.view.MyGridView;
import com.umeng.analytics.MobclickAgent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_CommendsCustom extends SwipeBackActivity {
	private RelativeLayout rl_back_cutomcommends;
	private List<String> filePath;
	private List<String> upFilePath;
	private MBC mbc;
	String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private MyGVAdapter gvadapter;
	private MyGridView gv_photoadd_custom;
	private int w;
	private ImageView im_start1, im_start2, im_start3, im_start4, im_start5;
	private ImageView im_start1_d, im_start2_d, im_start3_d, im_start4_d, im_start5_d;
	private TextView tv_surecommends_custom, tv_agree, tv_noagree;
	private EditText ed_customcommends;
	private int score1 = 1;
	private int score2 = 1;
	private int agree = -1;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				dismissProgressDialog();
				Toast.makeText(Activity_CommendsCustom.this, "评价成功", Toast.LENGTH_SHORT).show();
				finish();
				break;

			default:
				break;
			}
		};
	};
	private int orderId;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_cutomcommends:
			finish();
			break;
		case R.id.tv_agree:
			agree = 1;
			tv_agree.setBackgroundResource(R.drawable.image_bgsele);
			tv_noagree.setBackgroundResource(R.drawable.image_bg);
			break;
		case R.id.tv_noagree:
			agree = 0;
			tv_noagree.setBackgroundResource(R.drawable.image_bgsele);
			tv_agree.setBackgroundResource(R.drawable.image_bg);
			break;
		case R.id.im_start1_c:
			score1 = 1;
			im_start1.setImageResource(R.drawable.newstar2);
			im_start2.setImageResource(R.drawable.newstar1);
			im_start3.setImageResource(R.drawable.newstar1);
			im_start4.setImageResource(R.drawable.newstar1);
			im_start5.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start2_c:
			score1 = 2;
			im_start1.setImageResource(R.drawable.newstar2);
			im_start2.setImageResource(R.drawable.newstar2);
			im_start3.setImageResource(R.drawable.newstar1);
			im_start4.setImageResource(R.drawable.newstar1);
			im_start5.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start3_c:
			score1 = 3;
			im_start1.setImageResource(R.drawable.newstar2);
			im_start2.setImageResource(R.drawable.newstar2);
			im_start3.setImageResource(R.drawable.newstar2);
			im_start4.setImageResource(R.drawable.newstar1);
			im_start5.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start4_c:
			score1 = 4;
			im_start1.setImageResource(R.drawable.newstar2);
			im_start2.setImageResource(R.drawable.newstar2);
			im_start3.setImageResource(R.drawable.newstar2);
			im_start4.setImageResource(R.drawable.newstar2);
			im_start5.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start5_c:
			score1 = 5;
			im_start1.setImageResource(R.drawable.newstar2);
			im_start2.setImageResource(R.drawable.newstar2);
			im_start3.setImageResource(R.drawable.newstar2);
			im_start4.setImageResource(R.drawable.newstar2);
			im_start5.setImageResource(R.drawable.newstar2);
			break;
		case R.id.im_start1_d:
			score2 = 1;
			im_start1_d.setImageResource(R.drawable.newstar2);
			im_start2_d.setImageResource(R.drawable.newstar1);
			im_start3_d.setImageResource(R.drawable.newstar1);
			im_start4_d.setImageResource(R.drawable.newstar1);
			im_start5_d.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start2_d:
			score2 = 2;
			im_start1_d.setImageResource(R.drawable.newstar2);
			im_start2_d.setImageResource(R.drawable.newstar2);
			im_start3_d.setImageResource(R.drawable.newstar1);
			im_start4_d.setImageResource(R.drawable.newstar1);
			im_start5_d.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start3_d:
			score2 = 3;
			im_start1_d.setImageResource(R.drawable.newstar2);
			im_start2_d.setImageResource(R.drawable.newstar2);
			im_start3_d.setImageResource(R.drawable.newstar2);
			im_start4_d.setImageResource(R.drawable.newstar1);
			im_start5_d.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start4_d:
			score2 = 4;
			im_start1_d.setImageResource(R.drawable.newstar2);
			im_start2_d.setImageResource(R.drawable.newstar2);
			im_start3_d.setImageResource(R.drawable.newstar2);
			im_start4_d.setImageResource(R.drawable.newstar2);
			im_start5_d.setImageResource(R.drawable.newstar1);
			break;
		case R.id.im_start5_d:
			score2 = 5;
			im_start1_d.setImageResource(R.drawable.newstar2);
			im_start2_d.setImageResource(R.drawable.newstar2);
			im_start3_d.setImageResource(R.drawable.newstar2);
			im_start4_d.setImageResource(R.drawable.newstar2);
			im_start5_d.setImageResource(R.drawable.newstar2);
			break;
		case R.id.tv_surecommends_custom:

			if (agree == -1) {
				Toast.makeText(Activity_CommendsCustom.this, "请选择是否同意设计师发布作品!", Toast.LENGTH_SHORT).show();
				return;
			}
			if (score1 == 0) {
				Toast.makeText(Activity_CommendsCustom.this, "请选择定制评分!", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_customcommends.getText().toString().length() == 0) {
				Toast.makeText(Activity_CommendsCustom.this, "请输入评论!", Toast.LENGTH_SHORT).show();
				return;
			}
			showProgressDialog("上传中...");
			new Thread() {
				public void run() {
					for (int i = 1; i < filePath.size(); i++) {
						float yaso = 0;
						if ((new File(filePath.get(i))).length() > 2000000) {
							yaso = 0.3f;
						} else if ((new File(filePath.get(i))).length() > 1000000) {
							yaso = 0.5f;
						} else {
							yaso = 0.8f;
						}
						Bitmap resize_img = resize_img(BitmapFactory.decodeFile(filePath.get(i)), yaso);
						File saveMyBitmap = saveMyBitmap("image" + i + ".jpg", resize_img);
						upFilePath.add(saveMyBitmap.getPath().toString());
						System.out.println("zuo===file==" + saveMyBitmap.getPath().toString() + "      size="
								+ (new File(filePath.get(i))).length() + "   后=" + saveMyBitmap.length());

					}
					new Thread() {
						public void run() {
							post(AppFinalUrl.useraddDiscussForShopOrMake, ddd());
						};
					}.start();
				};
			}.start();

			break;
		default:
			break;
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
				}
			}

			// params.add(new BasicNameValuePair("discuss.makeDeails.id", "" +
			// orderId));
			// params.add(new BasicNameValuePair("discuss.content", "评论内容"));
			// params.add(new BasicNameValuePair("discuss.score", "评论内容"));
			// params.add(new BasicNameValuePair("discuss.isSupport", "评论内容"));

			entity.addPart("discuss.makeDeails.id", new StringBody("" + orderId, Charset.forName("UTF-8")));
			entity.addPart("discuss.content",
					new StringBody("" + ed_customcommends.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("discuss.score", new StringBody("" + score1, Charset.forName("UTF-8")));
			entity.addPart("discuss.teacherScore", new StringBody("" + score2, Charset.forName("UTF-8")));
			entity.addPart("discuss.isSupport", new StringBody("" + agree, Charset.forName("UTF-8")));
			entity.addPart("token", new StringBody("" + MyApplication.token, Charset.forName("UTF-8")));
			System.out.println("zuo==agree=" + agree);
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

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		filePath = new ArrayList<String>();
		MyApplication.addActivity(this);
		upFilePath = new ArrayList<String>();
		filePath.add("aa");
		rl_back_cutomcommends = (RelativeLayout) findViewById(R.id.rl_back_cutomcommends);
		gv_photoadd_custom = (MyGridView) findViewById(R.id.gv_photoadd_custom);
		gvadapter = new MyGVAdapter();
		gv_photoadd_custom.setAdapter(gvadapter);
		// 评分
		im_start1 = (ImageView) findViewById(R.id.im_start1_c);
		im_start2 = (ImageView) findViewById(R.id.im_start2_c);
		im_start3 = (ImageView) findViewById(R.id.im_start3_c);
		im_start4 = (ImageView) findViewById(R.id.im_start4_c);
		im_start5 = (ImageView) findViewById(R.id.im_start5_c);
		im_start1_d = (ImageView) findViewById(R.id.im_start1_d);
		im_start2_d = (ImageView) findViewById(R.id.im_start2_d);
		im_start3_d = (ImageView) findViewById(R.id.im_start3_d);
		im_start4_d = (ImageView) findViewById(R.id.im_start4_d);
		im_start5_d = (ImageView) findViewById(R.id.im_start5_d);

		tv_surecommends_custom = (TextView) findViewById(R.id.tv_surecommends_custom);
		ed_customcommends = (EditText) findViewById(R.id.ed_customcommends);
		tv_agree = (TextView) findViewById(R.id.tv_agree);
		tv_noagree = (TextView) findViewById(R.id.tv_noagree);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_cutomcommends.setOnClickListener(this);
		im_start1.setOnClickListener(this);
		im_start2.setOnClickListener(this);
		im_start3.setOnClickListener(this);
		im_start4.setOnClickListener(this);
		im_start5.setOnClickListener(this);
		im_start1_d.setOnClickListener(this);
		im_start2_d.setOnClickListener(this);
		im_start3_d.setOnClickListener(this);
		im_start4_d.setOnClickListener(this);
		im_start5_d.setOnClickListener(this);
		tv_surecommends_custom.setOnClickListener(this);
		tv_noagree.setOnClickListener(this);
		tv_agree.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_commendscustom);
		initParams();
		registMBC();
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(this, 120);
		w = (screenWidth - dp2px) / 3;

	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void initParams() {
		orderId = getIntent().getIntExtra("orderId", 0);
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
			v = View.inflate(Activity_CommendsCustom.this, R.layout.item_gv_pic, null);
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
						intent.putExtra("nub", 3);
						intent.setClass(Activity_CommendsCustom.this, ImgFileListActivity.class);
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("评价定制");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("评价定制");
	}
}
