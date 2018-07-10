package com.femto.ugershop.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;

public class DownLoadService extends Service {
	private MyBC mbc;
	private String musicurl;
	private String musicname;
	private String musicpic;
	private List<M> m;
	private int musicid;
	private String mzdd;
	private File out;
	private FileOutputStream fos;
	private String videopath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";

	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		out = new File(videopath);

		if (!out.exists()) {
			out.mkdirs();
		}
		videopath = out.getPath();
		m = new ArrayList<M>();
		mregisterReceiver();
	}

	private void mregisterReceiver() {
		mbc = new MyBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.kqx.down");
		registerReceiver(mbc, filter);
	}

	class M {
		String musicurl, musicname, musicpic;
	}

	class MyBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, final Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.kqx.down")) {
				// down(intent.getStringExtra("picname"),
				// intent.getStringExtra("picurl"));
				// new Thread() {
				// public void run() {
				//
				// };
				// }.start();
				savePic(intent.getStringExtra("picurl"));
			}
		}

	}

	private File dir;

	private void savePic(String urlPath) {
		dir = new File(videopath, "ewm" + urlPath.substring(urlPath.length() - 10, urlPath.length() - 4) + ".jpg");
		try {
			fos = new FileOutputStream(dir);
			MyApplication.ahc.post(urlPath, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] data) {
					// TODO Auto-generated method stub
					System.out.println("zuo====arg0=" + arg0);
					try {
						fos.write(data);
						fos.flush();
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					// TODO Auto-generated method stub
				}
			});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

}
