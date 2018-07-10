package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import com.example.AndroidCaptureCropTags.model.TagInfoModel;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.Flags;
import com.femto.ugershop.selepic.ImgFileListActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Activity_Send_Flags extends BaseActivity {
	private RelativeLayout rl_back_sendflag;
	private GridView gv_sendflag;
	private int w;
	private MyGVAdapter adapter;
	private TextView tv_sendflag;
	private int position = 1;
	private EditText ed_des;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_sendflag:
			finish();
			break;
		case R.id.tv_sendflag:

			ids = "";
			upPic(getFile(position), getStr(position));
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("申请报价");
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_sendflag = (RelativeLayout) findViewById(R.id.rl_back_sendflag);
		gv_sendflag = (GridView) findViewById(R.id.gv_sendflag);
		tv_sendflag = (TextView) findViewById(R.id.tv_sendflag);
		ed_des = (EditText) findViewById(R.id.ed_des);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_sendflag.setOnClickListener(this);
		tv_sendflag.setOnClickListener(this);
		adapter = new MyGVAdapter();
		gv_sendflag.setAdapter(adapter);

	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.acitivity_sendflags);
		MyApplication.addActivity(this);
		MyApplication.addFlagActivity(this);
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(this, 38);
		w = (screenWidth - dp2px) / 3;
		regisMBC();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adapter.notifyDataSetChanged();
		System.out.println("zuosize=" + MyApplication.flags.size());
		MobclickAgent.onResume(this);
		setPageStart("申请报价");
	}

	private String ids = "";

	private void upPic(File file, String str) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();

		try {
			params.put("doc", file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		params.put("str", str);
		System.out.println("zuo===params==" + params.toString());
		showProgressDialog("上传中...");
		MyApplication.ahc.post(AppFinalUrl.useruploadMakeProductFile, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo--" + response.toString());
				int result = response.optInt("result");
				if (result == 0) {
					int id = response.optInt("id");
					ids = ids + id + ",";
					position++;
					if (position < MyApplication.flags.size()) {
						upPic(getFile(position), getStr(position));
					} else {
						upFlags();
					}

				}
			}
		});
	}

	private void upFlags() {
		RequestParams params = new RequestParams();
		params.put("str", ed_des.getText().toString());
		params.put("user.id", MyApplication.userId);
		params.put("ids", ids);
		System.out.println("zuo==flag==" + params.toString());
		MyApplication.ahc.post(AppFinalUrl.useruploadPicAndLabelToMake, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuo" + response.toString());
				String resule = response.optString("resule");
				if (resule.equals("0")) {
					showToast("上传成功!", 0);
					MyApplication.flags.clear();
					Intent intent = new Intent(Activity_Send_Flags.this, Activity_MyCustom.class);
					startActivity(intent);
					finish();
					MyApplication.exitFlagact();
				} else {
					showToast("上传失败!", 0);
				}
			}
		});
	}

	private String getStr(int position) {
		String str = "";
		System.out.println("zuo===" + MyApplication.flags.size());
		List<TagInfoModel> tagInfos = MyApplication.flags.get(position).getTagInfos();
		if (tagInfos != null) {
			for (int i = 0; i < tagInfos.size(); i++) {
				str += "labelName=" + tagInfos.get(i).tag_name + ",x=" + tagInfos.get(i).x + ",y=" + tagInfos.get(i).y
						+ ",direction=" + 0 + ";";
			}
		}

		if (str.length() > 0) {
			str = str.substring(0, str.length() - 1);
		}

		return str;
	}

	private File getFile(int position) {
		String path = "";
		path = MyApplication.flags.get(position).getPicPath();
		File file = new File(path);
		return file;
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	class MyGVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return MyApplication.flags == null ? 0 : MyApplication.flags.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return MyApplication.flags.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			v = View.inflate(Activity_Send_Flags.this, R.layout.item_gv_pic, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_gvitem);
			if (position == 0) {
				im.setImageResource(R.drawable.addoder);
			} else {
				File imageFile = new File(MyApplication.flags.get(position).getPicPath());
				Picasso.with(Activity_Send_Flags.this).load(imageFile).placeholder(R.drawable.tianc).error(R.drawable.tianc)
						.resize(180, 180).centerCrop().into(im);
			}
			LayoutParams params = (LayoutParams) im.getLayoutParams();
			params.height = w;
			params.width = w;
			im.setLayoutParams(params);
			im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (position == 0) {
						showSettingFaceDialog();
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

	private void showExit(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确定删除此图片?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				MyApplication.flags.remove(position);
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

	private void regisMBC() {
		mbc = new MBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.femto.hasfocus");
		filter.addAction("com.select.pic");
		registerReceiver(mbc, filter);
	}

	private MBC mbc;
	private int i;
	private String filepath;

	class MBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("com.femto.hasfocus")) {

			} else if (action.equals("com.select.pic") && MyApplication.pictype == 2) {
				ArrayList<String> f = intent.getStringArrayListExtra("files");
				for (int i = 0; i < f.size(); i++) {
					filepath = f.get(i);
					Intent intent_ = new Intent(Activity_Send_Flags.this, ActivityAddTags.class);
					intent_.putExtra("image_path", filepath);
					startActivity(intent_);
				}

			}
		}
	}

	private String[] items = new String[] { "图库", "拍照" };

	private void showSettingFaceDialog() {

		new AlertDialog.Builder(Activity_Send_Flags.this).setTitle("图片来源").setCancelable(true)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:// Local Image
							MyApplication.pictype = 2;
							Intent intent = new Intent();
							intent.putExtra("nub", 1);
							intent.setClass(Activity_Send_Flags.this, ImgFileListActivity.class);
							startActivity(intent);
							break;
						case 1:// Take Picture
							photo();
							break;
						}
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	// 拍照
	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		IMAGE_FILE = getPhotoFileName();
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE)));
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	private static final int TAKE_PICTURE = 0x000001;
	private String IMAGE_FILE;

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	public String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		return dateFormat.format(date) + ".jpg";
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TAKE_PICTURE:

			if (resultCode == RESULT_OK) {
				File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE);
				IMAGE_FILE = getPath(Activity_Send_Flags.this, Uri.fromFile(tempFile));
				String filepath = IMAGE_FILE;
				Intent intent_ = new Intent(Activity_Send_Flags.this, ActivityAddTags.class);
				intent_.putExtra("image_path", filepath);
				startActivity(intent_);
			}

			break;
		}
	}

	// 以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}
