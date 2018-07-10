package com.femto.ugershop.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import u.aly.bm;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.twitter.Twitter;

import com.bumptech.glide.Glide;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.activity.ImageGridActivity;
import com.easemob.chatuidemo.widget.photoview.PhotoView;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_Customize;
import com.femto.ugershop.activity.Activity_FansNub;
import com.femto.ugershop.activity.Activity_FocusMe;
import com.femto.ugershop.activity.Activity_GoodUp;
import com.femto.ugershop.activity.Activity_GoodsDetails;
import com.femto.ugershop.activity.Activity_Login;
import com.femto.ugershop.activity.Activity_LookPic;
import com.femto.ugershop.activity.Activity_PostDetails;
import com.femto.ugershop.activity.Activity_Video;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.easemob.applib.utils.GetPathFromUri4kitkat;
import com.femto.ugershop.selepic.ImgFileListActivity;
import com.femto.ugershop.view.CircleImageView;
import com.femto.ugershop.view.MyGridView;
import com.femto.ugershop.view.ScrollViewWithListView;
import com.iqiyi.sdk.android.vcop.api.ReturnCode;
import com.iqiyi.sdk.android.vcop.api.UploadResultListener;
import com.iqiyi.sdk.android.vcop.api.VCOPClient;
import com.iqiyi.sdk.android.vcop.api.VCOPException;
import com.iqiyi.sdk.android.vcop.authorize.Authorize2AccessToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

@SuppressLint("ValidFragment")
public class Fragment_Studio extends BaseFragment implements OnClickListener, OnItemClickListener {
	private View view;
	private ScrollViewWithListView lv_photo_studio, lv_photo;
	private MyAdapter adapter;
	private List<String> data;
	private List<PhotoList> photoList;
	private int userId;
	private int isFrist = 0;
	private Authorize2AccessToken currtoken;
	private static final String ACCESS_TOKEN_PATH = "/sdcard/Android/data/com.iqiyi.sdk.android.demo/";
	private String mTestKey = "b5be2e81706541f2880202c04037ac61";
	private String mTestKeySecret = "99409cea12c2367c81f0eb0d4fc7dce8";
	private CircleImageView im_headstudio;
	private TextView tv_name_de, tv_focus_de, tv_isopen_de, tv_focusnub_de, tv_fansnub_de, tv_rank_de, tv_score_de, tv_heart,
			im_revise, im_revise_p;
	private DisplayImageOptions options;
	private MyLVPhotoadapter lvPhotoadater;
	private String imgUrl;
	private MyGridView gv_type, gv_stye;
	private int width;
	private RelativeLayout rl_revise, rl_video_studio;
	private int height;
	private MyVPOnline vponlin;
	private int myId = 0, isTop;
	private ImageView im_sjsjs, im_selephoto, im_devideo, im_addvideo, im_start_studio, im_play_mi, im_snt, im_focus_studio;
	private TextView tv_sure_revise;
	private EditText tv_jianjie, tv_zyfx, ed_stye;
	private RelativeLayout rl_focus, rl_customizesoon, rl_sharemain;
	private LinearLayout ll_bottom_studio, ll_message, ll_afterrevise, ll_contain_trust_studio;
	private ViewPager vp_sjsjs, vp_sjsjs_online;
	private EditText ed_info, tv_zcsjs;
	private String userName;
	private boolean iscanvideo = false;
	private boolean islogin;
	private RelativeLayout rl_revise_p;
	private boolean iscandelePhoto = false;
	private boolean iscandeledephoto = false;
	private boolean isvideoup = false;
	private String videopathshare = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				// ImageLoader.getInstance().displayImage(
				// AppFinalUrl.BASEURL + imgUrl, im_de_pic, options);
				// RelativeLayout.LayoutParams params =
				// (android.widget.RelativeLayout.LayoutParams) im_de_pic
				// .getLayoutParams();
				// params.height = (height * w) / width;
				// im_de_pic.setLayoutParams(params);
				break;
			case 2:
				dismissProgressDialog();
				Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT).show();
				pic_readdde.clear();
				up_readdde.clear();
				pic_addgzphoto.clear();
				up_addgzphoto.clear();
				isXiugai = true;
				im_revise.setText("编辑");
				getData();
				tv_jianjie.setFocusable(false);
				tv_jianjie.setEnabled(false);
				tv_zyfx.setFocusable(false);
				tv_zyfx.setEnabled(false);
				tv_zcsjs.setFocusable(false);
				tv_zcsjs.setEnabled(false);
				ed_stye.setFocusable(false);
				ed_stye.setEnabled(false);
				iscandeledephoto = false;
				break;
			case 3:
				dismissProgressDialog();
				Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				if (createVideoThumbnail != null) {
					im_snt.setImageBitmap(createVideoThumbnail);
				}
				break;
			default:
				break;
			}

		};
	};
	private int w;
	private List<String> type;
	private List<String> stye;
	private MyGVStyeAdapter styeadater;
	private MyGVTypeAdapter typeadater;
	private List<LableSort> lableSort;
	private Map<Integer, Boolean> cbtype;
	private List<String> pic_gz;
	private List<String> pic_sjsjs;
	private List<String> up_gz;
	private List<String> up_sjsjs;
	private List<String> pic_addgzphoto;
	private List<String> up_addgzphoto;
	private List<String> pic_readdde;
	private List<String> up_readdde;
	private int flag = 1;// 1为设计师介绍,2为工作室相册,3,修改工作室相册,4为修改设计师介绍
	private MyBC mbc;
	private int screenWidth;
	private int screenHeight;
	private int isvideo;

	private String VvvideoPath;
	String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private int sstype = 2;// 2为视频，3为图片
	private String seletype = "";
	private String fileid;
	public static VCOPClient vcopClient;
	private List<DesinList> desinList;
	private ImageView im_sharemain;
	private LinearLayout ll_tochat_studio, ll_removevideo, ll_dzpf, ll_myfans_studio, ll_focus_studio;
	private int shenfentype = 2;
	private boolean isXiugai = true;
	private File out;

	@SuppressLint("ValidFragment")
	public Fragment_Studio(int userId) {
		super();
		this.userId = userId;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			MobclickAgent.onResume(getActivity());
			setPageStart("设计师主页");
		} else {
			MobclickAgent.onPause(getActivity());
			setPageEnd("设计师主页");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_studio, container, false);
		data = new ArrayList<String>();
		type = new ArrayList<String>();
		stye = new ArrayList<String>();
		pic_gz = new ArrayList<String>();
		up_gz = new ArrayList<String>();
		pic_sjsjs = new ArrayList<String>();
		up_sjsjs = new ArrayList<String>();
		lableSort = new ArrayList<LableSort>();
		cbtype = new HashMap<Integer, Boolean>();
		photoList = new ArrayList<PhotoList>();
		desinList = new ArrayList<DesinList>();
		pic_addgzphoto = new ArrayList<String>();
		pic_readdde = new ArrayList<String>();
		up_readdde = new ArrayList<String>();
		up_addgzphoto = new ArrayList<String>();
		MobclickAgent.onResume(getActivity());
		setPageStart("设计师主页");
		registBC();
		initParams();
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.person) // image连接地址为空时
				.showImageOnFail(R.drawable.person) // image加载失败
				.cacheInMemory(false) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		initView(view);
		return view;
	}

	private Bitmap createVideoThumbnail(String url, int width, int height) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		int kind = MediaStore.Video.Thumbnails.MINI_KIND;
		try {
			if (Build.VERSION.SDK_INT >= 14) {
				retriever.setDataSource(url, new HashMap<String, String>());
			} else {
				retriever.setDataSource(url);
			}
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
				// Ignore failures while cleaning up.
			}
		}
		if (kind == Images.Thumbnails.MICRO_KIND && bitmap != null) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

	private void registBC() {
		mbc = new MyBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.select.pic");
		filter.addAction("shareDesigner");
		getActivity().registerReceiver(mbc, filter);

	}

	private void initParams() {
		out = new File(videopathshare);
		if (!out.exists()) {
			out.mkdirs();
		}
		videopathshare = out.getPath();
		SharedPreferences sp = getActivity().getSharedPreferences("Login", getActivity().MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
		shenfentype = sp.getInt("type", 0);
		islogin = sp.getBoolean("islogin", false);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(mbc);
		if (createVideoThumbnail != null) {
			createVideoThumbnail.recycle();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_focus:
			if (!islogin) {
				Intent intent = new Intent(getActivity(), Activity_Login.class);
				startActivity(intent);
				return;
			}

			if (isAction == 1) {
				cancelPeople();
			} else {
				focusPeople();
			}
			break;
		case R.id.tv_sure_revise:
			seletype = "";
			for (int i = 0; i < lableSort.size(); i++) {
				if (lableSort.get(i).isIssele()) {
					seletype = seletype + lableSort.get(i).sortName + ",";
				}
			}

			if (ed_info.getText().toString().length() == 0) {
				Toast.makeText(getActivity(), "请输入简介", Toast.LENGTH_SHORT).show();
				return;
			}
			if (seletype.length() == 0) {
				Toast.makeText(getActivity(), "请选择类别", Toast.LENGTH_SHORT).show();
				return;
			}
			showProgressDialog("上传中...");
			if (isvideo == 2) {
				upMessage();
			} else {
				new Thread() {
					public void run() {
						upload();
					};
				}.start();
			}

			break;

		case R.id.im_revise:
			if (!islogin) {
				Intent intent = new Intent(getActivity(), Activity_Login.class);
				startActivity(intent);
				return;
			}
			if (isXiugai) {
				isXiugai = false;
				tv_jianjie.setFocusable(true);
				tv_jianjie.setEnabled(true);
				tv_zyfx.setFocusable(true);
				tv_zyfx.setEnabled(true);
				tv_zcsjs.setFocusable(true);
				tv_zcsjs.setEnabled(true);
				ed_stye.setFocusable(true);
				ed_stye.setEnabled(true);
				ed_stye.setFocusableInTouchMode(true);
				tv_zcsjs.setFocusableInTouchMode(true);
				tv_zyfx.setFocusableInTouchMode(true);
				tv_jianjie.setFocusableInTouchMode(true);
				im_revise.setText("保存");
				Toast.makeText(getActivity(), "现在可以修改了", Toast.LENGTH_SHORT).show();
			} else {
				showProgressDialog("上传中...");
				readdSjsPhoto();
				return;
			}

			if (iscanvideo) {
				ll_removevideo.setVisibility(View.VISIBLE);
			} else {
				ll_removevideo.setVisibility(View.GONE);
			}

			if (desinList.size() == 0) {
				if (isXiugai) {

				} else {
					showshejishijisDialog();
				}

			} else {
				iscandeledephoto = true;
				vp_sjsjs_online.setAdapter(vponlin);
			}

			break;
		case R.id.im_sjsjs:
			if (!islogin) {
				Intent intent = new Intent(getActivity(), Activity_Login.class);
				startActivity(intent);
				return;
			}
			showPhotoDialog();
			break;
		case R.id.im_selephoto:
			if (!islogin) {
				Intent intent = new Intent(getActivity(), Activity_Login.class);
				startActivity(intent);
				return;
			}
			flag = 2;
			Intent intent_vp_mode = new Intent();
			intent_vp_mode.putExtra("nub", 5);
			intent_vp_mode.setClass(getActivity(), ImgFileListActivity.class);
			startActivity(intent_vp_mode);
			break;
		case R.id.im_revise_p:
			if (!islogin) {
				Intent intent = new Intent(getActivity(), Activity_Login.class);
				startActivity(intent);
				return;
			}

			if (photoList.size() == 0) {
				flag = 3;
				Intent intent_vp = new Intent();
				intent_vp.putExtra("nub", 5);
				intent_vp.setClass(getActivity(), ImgFileListActivity.class);
				startActivity(intent_vp);
			} else {
				iscandelePhoto = true;
				lvPhotoadater.notifyDataSetChanged();
			}

			break;
		case R.id.ll_tochat_studio:
			if (islogin) {
				startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("name", userName).putExtra("userId",
						"" + userId));
			} else {
				Intent intent = new Intent(getActivity(), Activity_Login.class);
				startActivity(intent);
			}

			break;
		case R.id.rl_video_studio:
			if (desinList.size() != 0 && desinList.get(0).url.length() != 0) {
				Intent intent_video = new Intent(getActivity(), Activity_Video.class);
				intent_video.putExtra("videourl", desinList.get(0).url);
				startActivity(intent_video);
			} else {
				Toast.makeText(getActivity(), "视频暂时无法播放", Toast.LENGTH_SHORT).show();

			}
			break;
		case R.id.im_addvideo:
			if (!islogin) {
				Intent intent = new Intent(getActivity(), Activity_Login.class);
				startActivity(intent);
			} else {
				showshejishijisDialog();
			}

			break;
		case R.id.im_devideo:
			showDialogdele(desinList.get(0).photoId, 0, 3);
			break;
		case R.id.rl_customizesoon:
			System.out.println("zuo===" + myId);
			if (!islogin) {
				Intent intent = new Intent(getActivity(), Activity_Login.class);
				startActivity(intent);
				return;
			} else {
				Intent intent_soon = new Intent(getActivity(), Activity_Customize.class);
				intent_soon.putExtra("desiId", userId);
				startActivity(intent_soon);
			}

			break;
		case R.id.im_sharemain:
			savePic(AppFinalUrl.photoBaseUri + imgUrl);
			break;
		case R.id.rl_sharemain:
			savePic(AppFinalUrl.photoBaseUri + imgUrl);
			break;
		case R.id.ll_myfans_studio:
			Intent intent_myfans = new Intent(getActivity(), Activity_FansNub.class);
			intent_myfans.putExtra("userId", userId);
			startActivity(intent_myfans);
			break;
		case R.id.ll_focus_studio:

			Intent intent_myfocus = new Intent(getActivity(), Activity_FocusMe.class);
			intent_myfocus.putExtra("userId", userId);
			startActivity(intent_myfocus);

			break;
		default:
			break;
		}

	}

	private File dir;
	FileOutputStream fos;

	private void savePic(String urlPath) {

		dir = new File(videopathshare, "showpic.jpg");
		try {
			fos = new FileOutputStream(dir);
			showProgressDialog("分享中...");
			MyApplication.ahc.post(urlPath, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] data) {
					// TODO Auto-generated method stub
					System.out.println("zuo====arg0=" + arg0);
					dismissProgressDialog();
					try {
						fos.write(data);
						fos.flush();
						fos.close();
						showShare();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					dismissProgressDialog();
				}
			});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dismissProgressDialog();
		}
	}

	private void showShare() {
		ShareSDK.initSDK(getActivity());
		OnekeyShare oks = new OnekeyShare();

		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		if (userName != null && !userName.equals("")) {
			oks.setTitle("分享优格设计师：" + userName);
		} else {
			oks.setTitle("优格设计师");
		}

		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://112.74.196.242:8088/shop/ug/designer.jsp?id=" + userId);
		// text是分享文本，所有平台都需要这个字段
		if (userInfo != null && !userInfo.equals("")) {
			oks.setText("" + userInfo);
		} else {
			oks.setText("优格设计师");
		}

		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		if (dir != null) {
			oks.setImagePath(dir.getPath());// 确保SDcard下面存在此张图片
		}
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://112.74.196.242:8088/shop/ug/designer.jsp?id=" + userId);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("优格出品");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://112.74.196.242:8088/shop/ug/designer.jsp?id=" + userId);
		oks.addHiddenPlatform(Twitter.NAME);
		oks.addHiddenPlatform(Facebook.NAME);
		// 启动分享GUI
		oks.show(getActivity());
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {

			}

			@Override
			public void onCancel(Platform arg0, int arg1) {

			}
		});
	}

	private void cancelPeople() {
		RequestParams params = new RequestParams();
		params.put("friend.user.id", myId);
		params.put("friend.beuser.id", userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercancleFocus, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						// addcon();
						tv_focus_de.setText("关注");
						isAction = 0;
						im_focus_studio.setImageResource(R.drawable.newfocus);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void initIQIYI() {
		ApplicationInfo info;
		try {
			info = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(),
					PackageManager.GET_META_DATA);
			mTestKey = info.metaData.getString("APPKEY");
			mTestKeySecret = info.metaData.getString("APPSECRET");

		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Authorize2AccessToken token = getLocalAccessToken();
		if (token != null) {
			// Log.d(TAG, token.toString());
		}
		vcopClient = new VCOPClient(mTestKey, mTestKeySecret, token);
		shouquan();
	}

	public void shouquan() {
		new Thread() {
			public void run() {
				ReturnCode code = vcopClient.authorize("2308108271");
				if (code.isSuccess()) {
					setLocalAccessToken(vcopClient.getToken());
					Authorize2AccessToken token = vcopClient.getToken();
					currtoken = token;
					setLocalAccessToken(token);
					System.out.println("zuotoken成功授权=" + token);
				}

			};
		}.start();

	}

	private void setLocalAccessToken(Authorize2AccessToken accessToken) {
		try {
			File dir = new File(ACCESS_TOKEN_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File f = new File(ACCESS_TOKEN_PATH + "accessToken.o");
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			FileOutputStream ou = new FileOutputStream(f);
			ObjectOutputStream oou = new ObjectOutputStream(ou);
			oou.writeObject(accessToken);
			oou.flush();
			oou.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Authorize2AccessToken getLocalAccessToken() {
		Authorize2AccessToken token = null;
		try {
			File f = new File(ACCESS_TOKEN_PATH + "accessToken.o");
			FileInputStream in = new FileInputStream(f);
			ObjectInputStream oin = new ObjectInputStream(in);
			token = (Authorize2AccessToken) oin.readObject();
			oin.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		return token;
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	public void uploadOlin() {
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("file_name", userName + getPhotoFileName());
		metadata.put("description", userName + getPhotoFileName());
		System.out.println("zuo===VvvideoPath=" + VvvideoPath + "" + "  " + getPhotoFileName());
		fileid = vcopClient.upload(VvvideoPath, metadata, new UploadResultListener() {

			@Override
			public void onProgress(String arg0, int progress) {
				// TODO Auto-generated method stub
				System.out.println("zuo进度=" + progress);
			}

			@Override
			public void onFinish(String arg0, Bundle arg1) {
				// TODO Auto-generated method stub
				if (fileid != "") {
					readdSjsPhoto();
				} else {

				}
			}

			@Override
			public void onError(VCOPException arg0) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				Toast.makeText(getActivity(), "上传视频失败", Toast.LENGTH_SHORT).show();
				System.out.println("zuoonError=" + arg0.toString());
			}
		});

		System.out.println("zuo完成fileid=" + fileid);

	}

	public void upload() {
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("file_name", userName + getPhotoFileName());
		metadata.put("description", userName + getPhotoFileName());
		System.out.println("zuo===VvvideoPath=" + VvvideoPath + "" + "  " + getPhotoFileName());
		fileid = vcopClient.upload(VvvideoPath, metadata, new UploadResultListener() {

			@Override
			public void onProgress(String arg0, int progress) {
				// TODO Auto-generated method stub
				System.out.println("zuo进度=" + progress);
			}

			@Override
			public void onFinish(String arg0, Bundle arg1) {
				// TODO Auto-generated method stub
				if (fileid != "") {
					upMessage();
				} else {

				}
			}

			@Override
			public void onError(VCOPException arg0) {
				// TODO Auto-generated method stub
				System.out.println("zuoonError=" + arg0.toString());
			}
		});

		System.out.println("zuo完成fileid=" + fileid);

	}

	private void upMessage() {
		new Thread() {
			public void run() {
				for (int i = 0; i < pic_gz.size(); i++) {
					float yaso = 0;
					if ((new File(pic_gz.get(i))).length() > 4000000) {
						yaso = 0.1f;
					} else if ((new File(pic_gz.get(i))).length() > 2000000 && (new File(pic_gz.get(i))).length() < 4000000) {
						yaso = 0.2f;
					} else if ((new File(pic_gz.get(i))).length() > 1000000 && (new File(pic_gz.get(i))).length() < 2000000) {
						yaso = 0.2f;
					} else if (new File(pic_gz.get(i)).length() < 1000000 && new File(pic_gz.get(i)).length() > 600000) {
						yaso = 0.6f;
					} else if (new File(pic_gz.get(i)).length() < 600000) {
						yaso = 0.8f;
					}
					Bitmap resize_img = resize_img(BitmapFactory.decodeFile(pic_gz.get(i)), yaso);
					File saveMyBitmap = saveMyBitmap("imagelog" + i + ".jpg", resize_img);
					up_gz.add(saveMyBitmap.getPath().toString());

					System.out.println("zuo===file==" + saveMyBitmap.getPath().toString() + "     前 size="
							+ (new File(pic_gz.get(i))).length() + "   后size=" + saveMyBitmap.length());
				}
				for (int i = 0; i < pic_sjsjs.size(); i++) {
					float yaso = 0;
					if ((new File(pic_sjsjs.get(i))).length() > 4000000) {
						yaso = 0.1f;
					} else if ((new File(pic_sjsjs.get(i))).length() > 2000000 && (new File(pic_sjsjs.get(i))).length() < 4000000) {
						yaso = 0.2f;
					} else if ((new File(pic_sjsjs.get(i))).length() > 1000000 && (new File(pic_sjsjs.get(i))).length() < 2000000) {
						yaso = 0.2f;
					} else if (new File(pic_sjsjs.get(i)).length() < 1000000 && new File(pic_sjsjs.get(i)).length() > 600000) {
						yaso = 0.6f;
					} else if (new File(pic_sjsjs.get(i)).length() < 600000) {
						yaso = 0.8f;
					}
					Bitmap resize_img = resize_img(BitmapFactory.decodeFile(pic_sjsjs.get(i)), yaso);
					File saveMyBitmap = saveMyBitmap("imagesjsjs" + i + ".jpg", resize_img);
					up_sjsjs.add(saveMyBitmap.getPath().toString());
					System.out.println("zuo===file==" + saveMyBitmap.getPath().toString() + "     前 size="
							+ (new File(pic_sjsjs.get(i))).length() + "   后size=" + saveMyBitmap.length());
				}
				new Thread() {
					public void run() {
						post(AppFinalUrl.userupdateMyRoom, ddd(up_sjsjs, up_gz));
					};
				}.start();
			};

		}.start();

	}

	public String post(String url, List<NameValuePair> pairs) {
		System.out.println("zuo开始上传了");
		String content = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext locaContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			if (isvideo == 1) {
				entity.addPart("aiQiYiId", new StringBody("" + fileid, Charset.forName("UTF-8")));
				for (int i = 0; i < pairs.size(); i++) {
					if (pairs.get(i).getName().equalsIgnoreCase("show")) {
						if (pairs.get(i).getValue() != null) {
							entity.addPart(pairs.get(i).getName(), new FileBody(new File(pairs.get(i).getValue())));
						}
					} else {

					}
				}
			} else {
				for (int i = 0; i < pairs.size(); i++) {
					if (pairs.get(i).getName().equalsIgnoreCase("pic") || pairs.get(i).getName().equalsIgnoreCase("show")) {
						if (pairs.get(i).getValue() != null) {
							entity.addPart(pairs.get(i).getName(), new FileBody(new File(pairs.get(i).getValue())));
						}
					} else {

					}
				}
			}
			entity.addPart("user.id", new StringBody("" + myId, Charset.forName("UTF-8")));
			entity.addPart("user.userInfo", new StringBody("" + ed_info.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("user.selfStyle", new StringBody("" + seletype, Charset.forName("UTF-8")));
			entity.addPart("type", new StringBody("" + sstype, Charset.forName("UTF-8")));
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
			if (content != null && content.length() != 0) {
				JSONObject ojj = new JSONObject(content);
				String result = ojj.optString("result");
				dismissProgressDialog();
				if (result.equals("0")) {
					handler.sendEmptyMessage(2);
				} else {
					handler.sendEmptyMessage(3);
				}
			} else {
				handler.sendEmptyMessage(3);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;

	}

	public String postAddPhoto(String url, List<NameValuePair> pairs) {
		System.out.println("zuo开始上传了");
		String content = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext locaContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			for (int i = 0; i < pairs.size(); i++) {
				if (pairs.get(i).getName().equalsIgnoreCase("pic")) {
					if (pairs.get(i).getValue() != null) {
						entity.addPart(pairs.get(i).getName(), new FileBody(new File(pairs.get(i).getValue())));
					}
				} else {

				}
			}
			entity.addPart("token", new StringBody("" + MyApplication.token, Charset.forName("UTF-8")));
			entity.addPart("roomPhoto.user.id", new StringBody("" + myId, Charset.forName("UTF-8")));

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
			if (content != null && content.length() != 0) {
				JSONObject ojj = new JSONObject(content);
				String result = ojj.optString("result");
				dismissProgressDialog();
				if (result.equals("0")) {
					handler.sendEmptyMessage(2);
				} else {
					handler.sendEmptyMessage(3);
				}
			} else {
				handler.sendEmptyMessage(3);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;

	}

	public List<NameValuePair> ddd(List<String> sjsjs, List<String> gzs) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (int i = 0; i < sjsjs.size(); i++) {
			params.add(new BasicNameValuePair("pic", sjsjs.get(i).toString()));
		}

		for (int i = 0; i < gzs.size(); i++) {
			params.add(new BasicNameValuePair("show", gzs.get(i).toString()));
		}

		return params;
	}

	public List<NameValuePair> dddaddPhoto(List<String> addphoto) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (int i = 0; i < addphoto.size(); i++) {
			params.add(new BasicNameValuePair("pic", addphoto.get(i).toString()));
		}

		return params;
	}

	// 压缩bitmap
	private Bitmap resize_img(Bitmap bitmap, float pc) {
		;
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

	class MyBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			List<String> f = new ArrayList<String>();
			if (action.equals("com.select.pic")) {
				if (flag == 1) {
					pic_sjsjs = intent.getStringArrayListExtra("files");
					f = pic_sjsjs;
				} else if (flag == 2) {
					pic_gz = intent.getStringArrayListExtra("files");
					f = pic_gz;
				} else if (flag == 3) {
					pic_addgzphoto = intent.getStringArrayListExtra("files");
					f = pic_gz;
					if (pic_addgzphoto.size() != 0) {
						addGZphoto();
					}
				} else if (flag == 4) {
					pic_readdde = intent.getStringArrayListExtra("files");
					if (pic_readdde.size() != 0) {
						showProgressDialog("上传中...");
						readdSjsPhoto();
					}
				}
				ArrayList<ImageView> vpData = new ArrayList<ImageView>();
				vpData.clear();
				for (int i = 0; i < f.size(); i++) {
					ImageView im = new ImageView(getActivity());
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(f.get(i), opts);
					opts.inSampleSize = computeInitialSampleSize(opts, -1, (int) (screenWidth * 0.8) * (int) (screenHeight * 0.8));
					// 这里一定要将其设置回false，因为之前我们将其设置成了true
					opts.inJustDecodeBounds = false;
					try {
						Bitmap bmp = BitmapFactory.decodeFile(f.get(i), opts);
						im.setImageBitmap(bmp);
					} catch (OutOfMemoryError err) {
					}
					// im.setImageBitmap(BitmapFactory.decodeFile(pic_log.get(i)));

					vpData.add(im);
				}

				if (vpData.size() != 0) {
					if (flag == 1) {
						MyVPAdapter ada = new MyVPAdapter(vpData);
						vp_sjsjs.setAdapter(ada);
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vp_sjsjs.getLayoutParams();
						params.height = (int) (screenHeight * 0.6);
						vp_sjsjs.setLayoutParams(params);
						im_sjsjs.setVisibility(View.GONE);
					} else if (flag == 2) {
						MyAdapter ada = new MyAdapter(vpData);
						lv_photo_studio.setAdapter(ada);
						im_selephoto.setVisibility(View.GONE);
					}

				}

			}

			if (action.equals("shareDesigner")) {
				if (islogin) {
					savePic(AppFinalUrl.BASEURL + imgUrl);
				} else {
					Intent intent_l = new Intent(getActivity(), Activity_Login.class);
					startActivity(intent_l);
				}

			}
		}

	}

	// 添加设计师介绍图片
	private void readdSjsPhoto() {

		new Thread() {
			public void run() {
				for (int i = 0; i < pic_readdde.size(); i++) {
					float yaso = 0;
					if ((new File(pic_readdde.get(i))).length() > 4000000) {
						yaso = 0.1f;
					} else if ((new File(pic_readdde.get(i))).length() > 2000000
							&& (new File(pic_readdde.get(i))).length() < 4000000) {
						yaso = 0.2f;
					} else if ((new File(pic_readdde.get(i))).length() > 1000000
							&& (new File(pic_readdde.get(i))).length() < 2000000) {
						yaso = 0.2f;
					} else if (new File(pic_readdde.get(i)).length() < 1000000 && new File(pic_readdde.get(i)).length() > 600000) {
						yaso = 0.6f;
					} else if (new File(pic_readdde.get(i)).length() < 600000) {
						yaso = 0.8f;
					}
					Bitmap resize_img = resize_img(BitmapFactory.decodeFile(pic_readdde.get(i)), yaso);
					File saveMyBitmap = saveMyBitmap("imagepic_pic_readdde" + i + ".jpg", resize_img);
					up_readdde.add(saveMyBitmap.getPath().toString());

				}

				new Thread() {
					public void run() {
						readddpost(AppFinalUrl.userupdateMyRoom, ddd(up_readdde, up_gz));
					};
				}.start();
			};
		}.start();
	}

	public String readddpost(String url, List<NameValuePair> pairs) {
		System.out.println("zuo开始上传了");
		String content = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext locaContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			if (isvideo == 1) {
				entity.addPart("aiQiYiId", new StringBody("" + fileid, Charset.forName("UTF-8")));
				for (int i = 0; i < pairs.size(); i++) {
					if (pairs.get(i).getName().equalsIgnoreCase("show")) {
						if (pairs.get(i).getValue() != null) {
							entity.addPart(pairs.get(i).getName(), new FileBody(new File(pairs.get(i).getValue())));
						}
					} else {

					}
				}
			} else {
				for (int i = 0; i < pairs.size(); i++) {
					if (pairs.get(i).getName().equalsIgnoreCase("pic") || pairs.get(i).getName().equalsIgnoreCase("show")) {
						if (pairs.get(i).getValue() != null) {
							entity.addPart(pairs.get(i).getName(), new FileBody(new File(pairs.get(i).getValue())));
						}
					} else {

					}
				}
			}
			entity.addPart("user.id", new StringBody("" + myId, Charset.forName("UTF-8")));
			entity.addPart("user.userInfo", new StringBody("" + tv_jianjie.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("user.selfStyle", new StringBody("" + tv_zyfx.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("user.style", new StringBody("" + ed_stye.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("type", new StringBody("" + sstype, Charset.forName("UTF-8")));
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
			if (content != null && content.length() != 0) {
				JSONObject ojj = new JSONObject(content);
				String result = ojj.optString("result");
				dismissProgressDialog();
				if (result.equals("0")) {
					handler.sendEmptyMessage(2);
				} else {
					handler.sendEmptyMessage(3);
				}
			} else {
				handler.sendEmptyMessage(3);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}

	// 添加工作室图片
	private void addGZphoto() {
		showProgressDialog("上传中");
		new Thread() {
			public void run() {
				for (int i = 0; i < pic_addgzphoto.size(); i++) {
					float yaso = 0;
					if ((new File(pic_addgzphoto.get(i))).length() > 4000000) {
						yaso = 0.1f;
					} else if ((new File(pic_addgzphoto.get(i))).length() > 2000000
							&& (new File(pic_addgzphoto.get(i))).length() < 4000000) {
						yaso = 0.2f;
					} else if ((new File(pic_addgzphoto.get(i))).length() > 1000000
							&& (new File(pic_addgzphoto.get(i))).length() < 2000000) {
						yaso = 0.2f;
					} else if (new File(pic_addgzphoto.get(i)).length() < 1000000
							&& new File(pic_addgzphoto.get(i)).length() > 600000) {
						yaso = 0.6f;
					} else if (new File(pic_addgzphoto.get(i)).length() < 600000) {
						yaso = 0.8f;
					}
					Bitmap resize_img = resize_img(BitmapFactory.decodeFile(pic_addgzphoto.get(i)), yaso);
					File saveMyBitmap = saveMyBitmap("imagepic_addgzphoto" + i + ".jpg", resize_img);
					up_addgzphoto.add(saveMyBitmap.getPath().toString());

					System.out.println("zuo===file==" + saveMyBitmap.getPath().toString() + "     前 size="
							+ (new File(pic_addgzphoto.get(i))).length() + "   后size=" + saveMyBitmap.length());
				}

				new Thread() {
					public void run() {
						postAddPhoto(AppFinalUrl.useruploadPicToMyRoom, dddaddPhoto(up_addgzphoto));
					};
				}.start();
			};
		}.start();
	}

	private void focusPeople() {
		RequestParams params = new RequestParams();
		params.put("friend.user.id", myId);
		params.put("friend.beuser.id", userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddFocus, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo" + response.toString());
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						// addcon();
						tv_focus_de.setText("已关注");
						isAction = 1;
						im_focus_studio.setImageResource(R.drawable.newfocusde);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void addcon() {
		new Thread(new Runnable() {
			public void run() {

				try {
					// demo写死了个reason，实际应该让用户手动填入
					String s = getResources().getString(R.string.Add_a_friend);
					EMContactManager.getInstance().addContact("" + userId, s);
					getActivity().runOnUiThread(new Runnable() {
						public void run() {

							String s1 = getResources().getString(R.string.send_successful);
							Toast.makeText(getActivity(), "关注成功", 1).show();
						}
					});
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {

						}
					});
				}
			}
		}).start();
	}

	private void initView(View v) {

		im_headstudio = (CircleImageView) v.findViewById(R.id.im_headstudio);
		tv_name_de = (TextView) v.findViewById(R.id.tv_name_de);
		tv_focus_de = (TextView) v.findViewById(R.id.tv_focus_de);
		tv_isopen_de = (TextView) v.findViewById(R.id.tv_isopen_de);
		tv_focusnub_de = (TextView) v.findViewById(R.id.tv_focusnub_de);
		tv_fansnub_de = (TextView) v.findViewById(R.id.tv_fansnub_dee);
		tv_rank_de = (TextView) v.findViewById(R.id.tv_rank_de1);
		tv_jianjie = (EditText) v.findViewById(R.id.tv_jianjie);
		ed_stye = (EditText) v.findViewById(R.id.ed_stye);
		tv_zyfx = (EditText) v.findViewById(R.id.tv_zyfx);
		tv_zcsjs = (EditText) v.findViewById(R.id.tv_zcsjss);
		tv_score_de = (TextView) v.findViewById(R.id.tv_score_de);
		tv_heart = (TextView) v.findViewById(R.id.tv_heart);
		rl_revise = (RelativeLayout) v.findViewById(R.id.rl_revise);
		im_revise = (TextView) v.findViewById(R.id.im_revise);
		im_snt = (ImageView) v.findViewById(R.id.im_snt);
		im_sjsjs = (ImageView) v.findViewById(R.id.im_sjsjs);
		im_revise_p = (TextView) v.findViewById(R.id.im_revise_p);
		im_start_studio = (ImageView) v.findViewById(R.id.im_start_studio);
		im_focus_studio = (ImageView) v.findViewById(R.id.im_focus_studio);
		im_selephoto = (ImageView) v.findViewById(R.id.im_selephoto);
		im_play_mi = (ImageView) v.findViewById(R.id.im_play_mi);
		tv_sure_revise = (TextView) v.findViewById(R.id.tv_sure_revise);
		gv_type = (MyGridView) v.findViewById(R.id.gv_type);
		gv_stye = (MyGridView) v.findViewById(R.id.gv_stye);
		ll_message = (LinearLayout) v.findViewById(R.id.ll_message);
		ll_contain_trust_studio = (LinearLayout) v.findViewById(R.id.ll_contain_trust_studio);
		ll_afterrevise = (LinearLayout) v.findViewById(R.id.ll_afterrevise);
		ll_dzpf = (LinearLayout) v.findViewById(R.id.ll_dzpf);
		vp_sjsjs = (ViewPager) v.findViewById(R.id.vp_sjsjs);
		ll_removevideo = (LinearLayout) v.findViewById(R.id.ll_removevideo);
		lv_photo_studio = (ScrollViewWithListView) v.findViewById(R.id.lv_photo_select);
		rl_customizesoon = (RelativeLayout) v.findViewById(R.id.rl_customizesoon);
		rl_sharemain = (RelativeLayout) v.findViewById(R.id.rl_sharemain);
		ll_myfans_studio = (LinearLayout) v.findViewById(R.id.ll_myfans_studio);
		ll_focus_studio = (LinearLayout) v.findViewById(R.id.ll_focus_studio);
		rl_customizesoon.setOnClickListener(this);
		ll_focus_studio.setOnClickListener(this);
		ll_myfans_studio.setOnClickListener(this);
		rl_sharemain.setOnClickListener(this);
		// im_devideo,
		im_addvideo = (ImageView) v.findViewById(R.id.im_addvideo);
		im_devideo = (ImageView) v.findViewById(R.id.im_devideo);
		im_sharemain = (ImageView) v.findViewById(R.id.im_sharemain);
		im_devideo.setOnClickListener(this);
		im_addvideo.setOnClickListener(this);
		im_sharemain.setOnClickListener(this);
		lv_photo = (ScrollViewWithListView) v.findViewById(R.id.lv_photo);
		ll_tochat_studio = (LinearLayout) v.findViewById(R.id.ll_tochat_studio);
		vp_sjsjs_online = (ViewPager) v.findViewById(R.id.vp_sjsjs_online);
		ll_bottom_studio = (LinearLayout) v.findViewById(R.id.ll_bottom_studio);
		screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(getActivity(), 8);
		w = screenWidth - dp2px;
		rl_video_studio = (RelativeLayout) v.findViewById(R.id.rl_video_studio);
		rl_video_studio.setOnClickListener(this);
		rl_focus = (RelativeLayout) v.findViewById(R.id.rl_focus);
		rl_focus.setOnClickListener(this);
		im_revise.setOnClickListener(this);
		styeadater = new MyGVStyeAdapter();
		typeadater = new MyGVTypeAdapter();
		gv_stye.setAdapter(styeadater);
		gv_type.setAdapter(typeadater);
		vponlin = new MyVPOnline();
		vp_sjsjs_online.setAdapter(vponlin);
		rl_revise_p = (RelativeLayout) v.findViewById(R.id.rl_revise_p);
		if (myId == userId) {
			ll_bottom_studio.setVisibility(View.GONE);
			rl_focus.setVisibility(View.GONE);
			rl_revise.setVisibility(View.VISIBLE);
		} else {
			ll_afterrevise.setVisibility(View.GONE);
			ll_message.setVisibility(View.VISIBLE);
			rl_revise.setVisibility(View.GONE);
			rl_revise_p.setVisibility(View.GONE);
			tv_jianjie.setFocusable(false);
			tv_jianjie.setEnabled(false);
			tv_zyfx.setFocusable(false);
			tv_zyfx.setEnabled(false);
			tv_zcsjs.setFocusable(false);
			tv_zcsjs.setEnabled(false);
			ed_stye.setFocusable(false);
			ed_stye.setEnabled(false);
		}

		getData();
		gatLableSort();
		im_sjsjs.setOnClickListener(this);
		im_selephoto.setOnClickListener(this);
		im_revise_p.setOnClickListener(this);
		tv_sure_revise.setOnClickListener(this);
		ed_info = (EditText) v.findViewById(R.id.ed_info);
		initIQIYI();
		lvPhotoadater = new MyLVPhotoadapter();
		lv_photo.setAdapter(lvPhotoadater);
		lv_photo.setOnItemClickListener(this);
		ll_tochat_studio.setOnClickListener(this);
		if (shenfentype == 1) {
			rl_customizesoon.setVisibility(View.INVISIBLE);
		} else {
			rl_customizesoon.setVisibility(View.VISIBLE);
		}
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private int isAction;
	private String userInfo;

	public void getData() {
		RequestParams params = new RequestParams();
		params.put("beUserId", myId);
		params.put("userId", userId);
		showProgressDialog("加载中");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyRoomByUserId, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo++gongzuoshi++" + response.toString());
				dismissProgressDialog();
				imgUrl = response.optString("imgUrl");
				String desinPeople = response.optString("desinPeople");
				String style = response.optString("style");
				String roomInfo = response.optString("roomInfo");
				userName = response.optString("userName");
				truststar = response.optInt("nub");
				userInfo = response.optString("userInfo");
				String selfStyle = response.optString("selfStyle");
				String level = response.optString("level");
				isTop = response.optInt("isAtion");
				int isMake = response.optInt("isMake");
				int fansCount = response.optInt("fansCount");
				int scoreLevel = response.optInt("scoreLevel");
				int topCount = response.optInt("topCount");
				int score = response.optInt("score");
				isAction = response.optInt("isAction");
				int myActionCount = response.optInt("myActionCount");
				isFrist = response.optInt("isFrist");
				if (isFrist == 1) {
					if (myId == userId) {
						ll_message.setVisibility(View.VISIBLE);
						ll_afterrevise.setVisibility(View.GONE);
						tv_jianjie.setFocusable(false);
						tv_jianjie.setEnabled(false);
						tv_zyfx.setFocusable(false);
						tv_zyfx.setEnabled(false);
						tv_zcsjs.setFocusable(false);
						tv_zcsjs.setEnabled(false);
						ed_stye.setFocusable(false);
						ed_stye.setEnabled(false);
					} else {
						ll_message.setVisibility(View.VISIBLE);
						ll_afterrevise.setVisibility(View.GONE);
					}

				} else {
					if (myId == userId) {
						ll_message.setVisibility(View.GONE);
						ll_afterrevise.setVisibility(View.VISIBLE);
					} else {
						ll_message.setVisibility(View.VISIBLE);
						ll_afterrevise.setVisibility(View.GONE);
					}

				}
				photoList.clear();
				JSONArray optJSONArray = response.optJSONArray("photoList");
				for (int i = 0; i < optJSONArray.length(); i++) {
					try {
						JSONObject j = optJSONArray.getJSONObject(i);
						int photoId = j.optInt("photoId");
						String height = j.optString("height");
						String width = j.optString("width");
						String url = j.optString("url");
						String fangXing = j.optString("fangXing");
						photoList.add(new PhotoList(photoId, url, width, height, fangXing));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				lvPhotoadater.notifyDataSetChanged();
				desinList.clear();
				// 设计师介绍
				JSONArray optJSONArray2 = response.optJSONArray("desinList");
				for (int i = 0; i < optJSONArray2.length(); i++) {
					try {
						JSONObject jj = optJSONArray2.getJSONObject(i);
						int photoId = jj.optInt("photoId");
						int dtype = jj.optInt("type");
						String height = jj.optString("height");
						String width = jj.optString("width");
						String url = jj.optString("url");
						desinList.add(new DesinList(photoId, dtype, url, width, height));

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				setVpsjsjs();

				// tv_name_de, tv_focus_de, tv_isopen_de,
				// tv_focusnub_de,
				// tv_fansnub_de, tv_rank_de
				if (userName != null && !userName.equals("null") && !userName.equals("")) {
					tv_name_de.setText("" + userName);
				} else {
					tv_name_de.setText("");
				}

				if (isAction == 1) {
					tv_focus_de.setText("已关注");
					im_focus_studio.setImageResource(R.drawable.newfocusde);
				} else {
					tv_focus_de.setText("关注");
					im_focus_studio.setImageResource(R.drawable.newfocus);
				}
				// tv_focus_de.setText(""+tv_focus_de);
				if (isMake == 1) {
					tv_isopen_de.setText("(已开通定制)");
					ll_dzpf.setVisibility(View.VISIBLE);
				} else {
					tv_isopen_de.setText("(未开通定制)");
					ll_dzpf.setVisibility(View.GONE);
				}
				tv_focusnub_de.setText("" + myActionCount);
				tv_heart.setText("" + topCount);
				if (score > 100000) {
					tv_score_de.setText("" + score / 10000 + "万分");
				} else {
					tv_score_de.setText("" + score + "分");
				}

				if (userName != null && !userName.equals("null") && !userName.equals("")) {
					tv_zcsjs.setText("" + userName);
				} else {
					tv_zcsjs.setText("");
				}

				if (userInfo != null && !userInfo.equals("null") && !userInfo.equals("")) {
					tv_jianjie.setText(userInfo.trim());
				} else {
					if (myId == userId) {
						tv_jianjie.setHint("请编辑");
					} else {
						tv_jianjie.setText("");
					}

				}
				if (selfStyle != null && !selfStyle.equals("null") && !selfStyle.equals("")) {
					tv_zyfx.setText(selfStyle);
				} else {
					tv_zyfx.setText("");
				}
				if (style != null && !style.equals("null") && !style.equals("")) {
					ed_stye.setText("" + style);
				} else {
					ed_stye.setText("");
				}
				tv_fansnub_de.setText("" + fansCount);
				// tv_rank_de.setText("" + level);

				if (level.contains("合一")) {
					tv_rank_de.setBackgroundResource(R.drawable.heyi);
				} else if (level.contains("黑一")) {
					tv_rank_de.setBackgroundResource(R.drawable.bl1);
				} else if (level.contains("黑二")) {
					tv_rank_de.setBackgroundResource(R.drawable.bl2);
				} else if (level.contains("黑三")) {
					tv_rank_de.setBackgroundResource(R.drawable.bl3);
				} else if (level.contains("灰一")) {
					tv_rank_de.setBackgroundResource(R.drawable.g1);
				} else if (level.contains("灰二")) {
					tv_rank_de.setBackgroundResource(R.drawable.g2);
				} else if (level.contains("灰三")) {
					tv_rank_de.setBackgroundResource(R.drawable.g3);
				} else if (level.contains("白一")) {
					tv_rank_de.setBackgroundResource(R.drawable.w1);
				} else if (level.contains("白二")) {
					tv_rank_de.setBackgroundResource(R.drawable.w2);
				} else if (level.contains("白三")) {
					tv_rank_de.setBackgroundResource(R.drawable.w3);
				}

				// Glide.with(getActivity()).load(AppFinalUrl.BASEURL + imgUrl)
				//
				// /*
				// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
				// */
				// .placeholder(R.drawable.tianc).crossFade().into(im_headstudio);

				ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + imgUrl, im_headstudio, options);
				setStart();
			}

		});
	}

	private void setVpsjsjs() {
		// TODO Auto-generated method stub
		if (desinList.size() != 0) {
			if (desinList.get(0).height != null && desinList.get(0).width != null && w != 0
					&& Integer.parseInt(desinList.get(0).height) != 0 && Integer.parseInt(desinList.get(0).width) != 0) {
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vp_sjsjs_online.getLayoutParams();
				layoutParams.height = (int) (w * Integer.parseInt(desinList.get(0).height) / (double) Integer.parseInt(desinList
						.get(0).width));
				layoutParams.width = w;
				vp_sjsjs_online.setLayoutParams(layoutParams);
			}

			if (desinList.get(0).type == 3) {
				rl_video_studio.setVisibility(View.GONE);
				vp_sjsjs_online.setVisibility(View.VISIBLE);
				iscanvideo = false;
				vponlin.notifyDataSetChanged();
				dismissProgressDialog();

			} else {
				iscanvideo = true;
				rl_video_studio.setVisibility(View.VISIBLE);
				// 设置视频缩略图
				System.out.println("zuo+++" + desinList.get(0).url);
				if (desinList.get(0).url != null && !desinList.get(0).equals("")) {
					new Thread() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							createVideoThumbnail = createVideoThumbnail(desinList.get(0).url, w, (int) (w * 3 / 4.0));
							handler.sendEmptyMessage(4);
						}
					}.start();

					// createVideoThumbnail.recycle();
					dismissProgressDialog();
				} else {
					im_snt.setImageResource(R.drawable.photo02);
				}
				vp_sjsjs_online.setVisibility(View.GONE);
			}

		}

	}

	Bitmap createVideoThumbnail;
	private int truststar = 1;

	class MyVPOnline extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return desinList == null ? 0 : desinList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {

			View v = View.inflate(getActivity(), R.layout.item_delephoto, null);

			ImageView im = (ImageView) v.findViewById(R.id.im_photooo);
			ImageView im_add = (ImageView) v.findViewById(R.id.im_add);
			ImageView im_delet = (ImageView) v.findViewById(R.id.im_delet);
			LinearLayout ll_removeadd = (LinearLayout) v.findViewById(R.id.ll_removeadd);

			Glide.with(getActivity()).load(AppFinalUrl.photoBaseUri + desinList.get(position).url + "-middle")

			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(im);
			if (w != 0 && Integer.parseInt(desinList.get(position).height) != 0
					&& Integer.parseInt(desinList.get(position).width) != 0) {
				RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) im.getLayoutParams();
				layoutParams.width = w;
				layoutParams.height = (int) (w * Integer.parseInt(desinList.get(position).height) / (double) Integer
						.parseInt(desinList.get(position).width));
				im.setLayoutParams(layoutParams);
			}
			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// desinList.get(position).url, im, options);
			if (iscandeledephoto) {
				ll_removeadd.setVisibility(View.VISIBLE);

			} else {
				ll_removeadd.setVisibility(View.INVISIBLE);

			}
			im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), Activity_LookPic.class);
					intent.putExtra("position", position);
					ArrayList<String> pics = new ArrayList<String>();
					for (int i = 0; i < desinList.size(); i++) {
						pics.add(desinList.get(i).url);
					}
					intent.putStringArrayListExtra("pics", pics);
					startActivity(intent);

				}
			});
			// ll_removeadd.setVisibility(View.VISIBLE);
			im_add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					isvideoup = true;
					showshejishijisDialog();
				}
			});
			im_delet.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showDialogdele(desinList.get(position).photoId, position, 1);
				}

			});
			container.addView(v);
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	class MyLVPhotoadapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return photoList == null ? 0 : photoList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return photoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {

			v = View.inflate(getActivity(), R.layout.item_delephoto, null);

			ImageView im = (ImageView) v.findViewById(R.id.im_photooo);
			ImageView im_add = (ImageView) v.findViewById(R.id.im_add);
			ImageView im_delet = (ImageView) v.findViewById(R.id.im_delet);
			LinearLayout ll_removeadd = (LinearLayout) v.findViewById(R.id.ll_removeadd);

			Glide.with(getActivity()).load(AppFinalUrl.photoBaseUri + photoList.get(position).url + "-middle")
			// .override(photoList.get(position).width,
			// photoList.get(position).height)
					/*
					 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
					 */
					.placeholder(R.drawable.tianc).crossFade().into(im);
			if (w != 0 && Integer.parseInt(photoList.get(position).height) != 0
					&& Integer.parseInt(photoList.get(position).width) != 0) {
				if (photoList.get(position).fangXing.contains("Right")) {
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) im.getLayoutParams();
					layoutParams.width = w;
					layoutParams.height = (int) (w * Integer.parseInt(photoList.get(position).width) / (double) Integer
							.parseInt(photoList.get(position).height));
					im.setLayoutParams(layoutParams);
				} else {
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) im.getLayoutParams();
					layoutParams.width = w;
					layoutParams.height = (int) (w * Integer.parseInt(photoList.get(position).height) / (double) Integer
							.parseInt(photoList.get(position).width));
					im.setLayoutParams(layoutParams);
				}

			}

			// (int) (w *
			// Integer.parseInt(productList.get(position).high) / (double)
			// Integer
			// .parseInt(productList.get(position).wight));
			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// photoList.get(position).url, im, options);

			if (iscandelePhoto) {
				ll_removeadd.setVisibility(View.VISIBLE);
			} else {
				ll_removeadd.setVisibility(View.INVISIBLE);
			}
			im_add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					flag = 3;
					Intent intent_vp_mode = new Intent();
					intent_vp_mode.putExtra("nub", 5);
					intent_vp_mode.setClass(getActivity(), ImgFileListActivity.class);
					startActivity(intent_vp_mode);
				}
			});
			im_delet.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showDialogdele(photoList.get(position).photoId, position, 2);
				}

			});

			// ImageView im = new ImageView(getActivity());
			// ImageLoader.getInstance().displayImage(
			// AppFinalUrl.BASEURL + photoList.get(position).url, im,
			// options);
			return v;
		}
	}

	class MyHolder {
		ImageView im, im_delet, im_add;
		LinearLayout ll_removeadd;
	}

	private void delePhoto(int photoId, final int position, final int f) {
		RequestParams params = new RequestParams();
		params.put("photoId", photoId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userdeleteRoomPhotoByPhotoId, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
						if (f == 2) {
							photoList.remove(position);
							lvPhotoadater.notifyDataSetChanged();
						} else if (f == 1) {
							desinList.remove(position);
							vp_sjsjs_online.setAdapter(vponlin);
						} else if (f == 3) {
							getData();
						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void showDialogdele(final int photoId, final int position, final int f) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示").setMessage("确定删除此照片?").setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		}).setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				delePhoto(photoId, position, f);
			}
		}).show();
	}

	class MyAdapter extends BaseAdapter {
		List<ImageView> vpData;

		public MyAdapter(List<ImageView> vpData) {
			super();
			this.vpData = vpData;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return vpData == null ? 0 : vpData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return vpData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			v = vpData.get(position);
			return v;
		}
	}

	class MyVPAdapter extends PagerAdapter {
		List<ImageView> vpData;

		public MyVPAdapter(List<ImageView> vpData) {
			super();
			this.vpData = vpData;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return vpData.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(vpData.get(position));
			return vpData.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(vpData.get(position));
		}

	}

	private Bitmap getImageBitmap(String url) {
		URL imgUrl = null;
		Bitmap bitmap = null;
		try {
			imgUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	class MyGVStyeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return type == null ? 0 : type.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return type.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			v = View.inflate(getActivity(), R.layout.item_type, null);
			return v;
		}
	}

	class MyGVTypeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lableSort == null ? 0 : lableSort.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lableSort.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			v = View.inflate(getActivity(), R.layout.item_type, null);
			final CheckBox cb = (CheckBox) v.findViewById(R.id.cb_type);
			if (cbtype != null && cbtype.get(position) != null && cbtype.get(position)) {
				cb.setChecked(true);
			} else {
				cb.setChecked(false);
			}
			cb.setText(lableSort.get(position).sortName);
			cb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (cb.isChecked()) {
						lableSort.get(position).setIssele(true);
						cbtype.put(position, true);
						notifyDataSetChanged();
					} else {
						lableSort.get(position).setIssele(false);
						cbtype.put(position, false);
						notifyDataSetChanged();
					}

				}
			});
			return v;
		}
	}

	private void gatLableSort() {
		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetLables, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("LableSort");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						int sortId = j.optInt("sortId");
						String sortName = j.getString("sortName");
						lableSort.add(new LableSort(sortId, sortName, false));
					}
					typeadater.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	class LableSort {
		int sortId;
		String sortName;
		boolean issele;

		public LableSort(int sortId, String sortName, boolean issele) {
			super();
			this.sortId = sortId;
			this.sortName = sortName;
			this.issele = issele;
		}

		/**
		 * @return the sortId
		 */
		public int getSortId() {
			return sortId;
		}

		/**
		 * @param sortId
		 *            the sortId to set
		 */
		public void setSortId(int sortId) {
			this.sortId = sortId;
		}

		/**
		 * @return the sortName
		 */
		public String getSortName() {
			return sortName;
		}

		/**
		 * @param sortName
		 *            the sortName to set
		 */
		public void setSortName(String sortName) {
			this.sortName = sortName;
		}

		/**
		 * @return the issele
		 */
		public boolean isIssele() {
			return issele;
		}

		/**
		 * @param issele
		 *            the issele to set
		 */
		public void setIssele(boolean issele) {
			this.issele = issele;
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

	private void showshejishijisDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View view = View.inflate(getActivity(), R.layout.dialog_accompany, null);

		builder.setView(view);
		final AlertDialog dialog = builder.create();
		RelativeLayout rl_local = (RelativeLayout) view.findViewById(R.id.rl_localaccompany);
		TextView tv_sure = (TextView) view.findViewById(R.id.tv_sure);
		tv_sure.setText("上传图片");
		TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
		tv_cancel.setText("上传视频");
		RelativeLayout rl_sure = (RelativeLayout) view.findViewById(R.id.rl_seachaccompany);
		// 上传图片
		rl_local.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				flag = 4;
				isvideo = 2;
				sstype = 3;
				isvideoup = false;
				Intent intent = new Intent();
				intent.putExtra("nub", 3);
				intent.setClass(getActivity(), ImgFileListActivity.class);
				startActivity(intent);
				dialog.dismiss();

			}
		});
		// 上传视频
		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isvideo = 1;
				sstype = 2;
				seleVideo();
				dialog.dismiss();

			}
		});

		dialog.show();

	}

	private void showPhotoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View view = View.inflate(getActivity(), R.layout.dialog_accompany, null);

		builder.setView(view);
		final AlertDialog dialog = builder.create();
		RelativeLayout rl_local = (RelativeLayout) view.findViewById(R.id.rl_localaccompany);
		TextView tv_sure = (TextView) view.findViewById(R.id.tv_sure);
		tv_sure.setText("上传图片");
		TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
		tv_cancel.setText("上传视频");
		RelativeLayout rl_sure = (RelativeLayout) view.findViewById(R.id.rl_seachaccompany);
		// 上传图片
		rl_local.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				flag = 1;
				isvideo = 2;
				sstype = 3;
				Intent intent = new Intent();
				intent.putExtra("nub", 3);
				intent.setClass(getActivity(), ImgFileListActivity.class);
				startActivity(intent);
				dialog.dismiss();

			}
		});
		// 上传视频
		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isvideo = 1;
				sstype = 2;
				seleVideo();
				dialog.dismiss();

			}
		});

		dialog.show();

	}

	public void seleVideo() {
		Intent intent = new Intent(getActivity(), ImageGridActivity.class);
		startActivityForResult(intent, 23);
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Video.Media.DATA };
		Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 23 && data != null) {
			// Uri uri = data.getData();
			VvvideoPath = data.getStringExtra("path");
			// if (android.os.Build.VERSION.SDK_INT >=
			// android.os.Build.VERSION_CODES.KITKAT) {
			// VvvideoPath = GetPathFromUri4kitkat.getPath(getActivity(), uri);
			// } else {
			// VvvideoPath = getPath(uri);
			// }

			System.out.println("zuo==oojfsijfoiadfjoVvvideoPath==" + VvvideoPath);
			if (isFrist == 1) {
				showProgressDialog("上传中...");
				new Thread() {
					public void run() {
						uploadOlin();
					};
				}.start();

			} else {
				im_play_mi.setVisibility(View.VISIBLE);
			}

		}
	}

	class PhotoList {
		int photoId;
		String url, width, height, fangXing;

		public PhotoList(int photoId, String url, String width, String height, String fangXing) {
			super();
			this.photoId = photoId;
			this.url = url;
			this.width = width;
			this.height = height;
			this.fangXing = fangXing;
		}
	}

	class DesinList {
		int photoId, type;
		String url, width, height;

		public DesinList(int photoId, int type, String url, String width, String height) {
			super();
			this.photoId = photoId;
			this.type = type;
			this.url = url;
			this.width = width;
			this.height = height;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), Activity_LookPic.class);
		intent.putExtra("position", position);
		ArrayList<String> pics = new ArrayList<String>();
		for (int i = 0; i < photoList.size(); i++) {
			pics.add(photoList.get(i).url);
		}
		intent.putStringArrayListExtra("pics", pics);
		startActivity(intent);
	}

	private void setStart() {
		ll_contain_trust_studio.removeAllViews();
		if (truststar == 0) {
			truststar = 1;
		}
		for (int i = 0; i < truststar; i++) {
			View v = View.inflate(getActivity(), R.layout.item_star, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_star);
			im.setPadding(16, 0, 16, 0);
			im.setImageResource(R.drawable.newstar_orange);
			ll_contain_trust_studio.addView(v);
		}
	}
}
