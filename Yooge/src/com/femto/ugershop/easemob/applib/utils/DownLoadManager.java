package com.femto.ugershop.easemob.applib.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.os.Environment;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年9月24日 上午9:15:40 类说明
 */
public class DownLoadManager {
	public static File getFileFromServer(String path, ProgressDialog pd) throws Exception {

		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			URL url = new URL(path);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setConnectTimeout(5000);

			// 获取到文件的大小

			pd.setMax(conn.getContentLength());

			InputStream is = conn.getInputStream();

			File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");

			FileOutputStream fos = new FileOutputStream(file);

			BufferedInputStream bis = new BufferedInputStream(is);

			byte[] buffer = new byte[1024];

			int len;

			int total = 0;

			while ((len = bis.read(buffer)) != -1) {

				fos.write(buffer, 0, len);

				total += len;

				// 获取当前下载量

				pd.setProgress(total);

			}

			fos.close();

			bis.close();

			is.close();

			return file;

		}

		else {

			return null;

		}

	}

}
