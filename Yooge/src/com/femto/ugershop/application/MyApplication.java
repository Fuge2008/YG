package com.femto.ugershop.application;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_GoodsDetails;
import com.femto.ugershop.easemob.applib.domain.User;
import com.femto.ugershop.entity.Flags;
import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MyApplication extends Application {
	public static AsyncHttpClient ahc = new AsyncHttpClient();
	private static List<Activity> activityList = new LinkedList<Activity>();
	private static List<Activity> activityaddflag = new LinkedList<Activity>();
	public static Context applicationContext;
	private static MyApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";

	private static List<Activity> listpay = new LinkedList<Activity>();
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
	public static boolean isback = false;
	public static String token;
	public static int userId;
	public static int type;
	public static boolean islogin;
	public static String headImage;
	public static int pictype = 0;
	public static List<Flags> flags = new ArrayList<Flags>();
	/* 第三方登录 */
	public static String other_loginType;
	public static String other_type;
	public static String other_token;
	public static String other_username;
	public static String other_url;

	/* 第三方登录 */
	public static boolean issingle = false;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		applicationContext = this;
		instance = this;
		getInfo();
		flags.add(new Flags("", null));
		// MobclickAgent.setDebugMode(true);
		MobclickAgent.openActivityDurationTrack(false);
		// UnCeHandler catchExcep = new UnCeHandler(this);
		// Thread.setDefaultUncaughtExceptionHandler(catchExcep);
		hxSDKHelper.onInit(applicationContext);
		initImageLoader(applicationContext);
		String deviceInfo = getDeviceInfo(applicationContext);
		System.out.println("zuo-deviceInfo==" + deviceInfo);
	}

	public static Animation getAni(Context context, int anim) {
		return AnimationUtils.loadAnimation(context, anim);
	}

	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MyApplication getInstance() {
		return instance;
	}

	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public static void addFlagActivity(Activity activity) {
		activityaddflag.add(activity);
	}

	public static void addpaycct(Activity activity) {
		listpay.add(activity);
	}

	public static void exitpayact() {

		for (Activity a : listpay) {
			a.finish();
		}
	}

	public static void exitFlagact() {

		for (Activity a : activityaddflag) {
			a.finish();
		}
	}

	public static void exit() {

		for (Activity a : activityList) {
			a.finish();
		}
		System.out.println("zuo==退出了killeralliance=");
		if (isback) {

		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		isback = false;
	}

	public static void initImageLoader(Context context) {
		File cacheDir;
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			System.out.println("zuo===SD卡存在");
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/cardImages";
			File path1 = new File(path);
			cacheDir = new File(Environment.getExternalStorageDirectory().getPath() + "/cardImages");
			if (!cacheDir.exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				cacheDir.mkdirs();
			}

		} else {
			cacheDir = StorageUtils.getOwnCacheDirectory(context, "imageloader/Cache");
		}
		// diskCacheExtraOptions
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.memoryCacheExtraOptions(480, 800)
				// maxwidth, max height，即保存的每个缓存文件的最大长宽
				.threadPoolSize(3)

				// default = device screen dimensions
				// .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75,
				// null)
				// 线程池内加载的数量
				.threadPriority(2)
				.denyCacheImageMultipleSizesInMemory()
				// .memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 *
				// 1024))
				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				// .memoryCacheSize(5 * 1024 * 1024)
				.discCacheSize(200 * 1024 * 1024).discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO).discCacheFileCount(1000)
				// 缓存的文件数量
				.discCache(new UnlimitedDiscCache(cacheDir))
				// 自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout
																						// (5
																						// s),
																						// readTimeout
																						// (30
																						// s)超时时间
				.writeDebugLogs() // Remove for releaseapp
				.build();// 开始构建
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {

		return hxSDKHelper.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名
	 * 
	 * @return
	 */
	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 * 
	 * @param user
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 * 
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSDKHelper.logout(emCallBack);
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
		} else {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void getInfo() {
		SharedPreferences sp = applicationContext.getSharedPreferences("Login", Context.MODE_PRIVATE);
		token = sp.getString("token", "");
		if (token != null && token.length() != 0 && !token.equals("")) {
			token = encode(token);
		}
		headImage = sp.getString("headImage", "");
		userId = sp.getInt("userId", -1);
		type = sp.getInt("type", -1);
		islogin = sp.getBoolean("islogin", false);
		if (userId != -1) {
			MobclickAgent.onProfileSignIn("" + userId);
		}

	}

	private static final String key0 = "femtosoft";
	private static final Charset charset = Charset.forName("UTF-8");
	private static byte[] keyBytes = key0.getBytes(charset);

	public static String encode(String enc) {
		byte[] b = enc.getBytes(charset);
		for (int i = 0, size = b.length; i < size; i++) {
			for (byte keyBytes0 : keyBytes) {
				b[i] = (byte) (b[i] ^ keyBytes0);
			}
		}
		return new String(b);
	}

	public static DisplayImageOptions getOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				.displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		return options;
	}
}
