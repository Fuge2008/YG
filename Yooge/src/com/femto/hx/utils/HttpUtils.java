package com.femto.hx.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.easemob.chatuidemo.widget.photoview.PhotoView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

public class HttpUtils {
	public interface OnGetNetMessage {
		public void onGetNetMessage(String netMessage);
	}

	public static void getMessage(final String path,
			final OnGetNetMessage listenner) {
		final Handler handler = new Handler();
		new Thread() {
			@Override
			public void run() {
				InputStream is;
				final ByteArrayOutputStream bos;
				try {
					URL url = new URL(path);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setDoInput(true);
					connection.connect();
					if (connection.getResponseCode() == 200) {
						is = connection.getInputStream();
						bos = new ByteArrayOutputStream();
						int len;
						byte[] b = new byte[1024];
						while ((len = is.read(b)) != -1) {
							bos.write(b, 0, len);
						}
						handler.post(new Runnable() {
							public void run() {
								listenner.onGetNetMessage(new String(bos
										.toByteArray()));
							}
						});
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void getBitMap(final String path, Context context,
			final PhotoView imv) {
		final Handler handler = new Handler();
		final String imgName = path.substring(path.lastIndexOf("/") + 1,
				path.length());
		File file = context.getFilesDir();
		final File imgFile = new File(file, imgName);
		if (imgFile.exists()) {// �����Ż�
			System.out.println("====ͼƬ����===========");
			// ��ȡ����ͼƬ
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(imgFile);
				Bitmap bitmap = BitmapFactory.decodeStream(fis);
				imv.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			new Thread() {
				public void run() {
					URL url;
					InputStream inputStream = null;
					FileOutputStream fos = null;
					HttpURLConnection connection = null;
					FileInputStream fis = null;
					try {
						url = new URL(path);
						connection = (HttpURLConnection) url.openConnection();
						connection.setDoInput(true);
						connection.connect();
						if (connection.getResponseCode() == 200) {
							// ��ȡ���������
							inputStream = connection.getInputStream();

							// ����ͼƬ�洢���洢��ͼƬ���֣���url��....jgp
							// ������ݴ洢��ʽ1��SP 2���ڲ��洢 3���ⲿ�洢 4������洢
							// 5��SQLite
							// �洢��File
							fos = new FileOutputStream(imgFile);
							byte[] buf = new byte[1024];
							int len;
							while ((len = inputStream.read(buf)) != -1) {
								fos.write(buf, 0, len);
							}
							// ��ȡ����ͼƬ
							fis = new FileInputStream(imgFile);
							final Bitmap bitmap = BitmapFactory
									.decodeStream(fis);
							handler.post(new Runnable() {

								@Override
								public void run() {
									imv.setImageBitmap(bitmap);
								}
							});
						}

					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if (fos != null) {
							try {
								fos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if (connection != null) {
							connection.disconnect();
						}
						if (fis != null) {
							try {
								fis.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

				}
			}.start();

		}
	}
}
