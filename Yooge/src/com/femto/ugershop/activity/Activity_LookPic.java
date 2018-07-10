package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

import com.bumptech.glide.Glide;
import com.femto.hx.utils.HttpUtils;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_LookPic extends BaseActivity implements OnPageChangeListener {
	private ViewPager vp;
	private int position;
	private ArrayList<String> pics;
	private List<View> vpData;
	private MyVPAdapter adapter;
	private DisplayImageOptions options;
	private View cview;
	private TextView tv_number, tv_allnub, tv_downpic;
	private int flag;
	private int cuPosition;
	private boolean isdown = false;

	@Override
	public void onClick(View v) {
		// TODO Auto-gene rated method stub
		switch (v.getId()) {
		case R.id.tv_downpic:
			showToast("已添加到下载任务!", 0);
			if (pics.get(cuPosition) != null && !isdown) {
				sendMBC();
				isdown = true;
			}

			break;

		default:
			break;
		}
	}

	private void sendMBC() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("picname", pics.get(cuPosition));
		intent.putExtra("picurl", pics.get(cuPosition));
		intent.setAction("com.kqx.down");
		sendBroadcast(intent);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		initParams();
		MyApplication.addActivity(this);
		vp = (ViewPager) findViewById(R.id.vp_lookpic);
		tv_number = (TextView) findViewById(R.id.tv_number);
		tv_allnub = (TextView) findViewById(R.id.tv_allnub);
		tv_downpic = (TextView) findViewById(R.id.tv_downpic);
		tv_allnub.setText("" + pics.size());
		tv_number.setText("" + (position + 1));
		adapter = new MyVPAdapter();
		vp.setAdapter(adapter);
		if (flag == 1) {
			tv_downpic.setVisibility(View.VISIBLE);
		} else {
			tv_downpic.setVisibility(View.GONE);
		}
		initVp();
	}

	private void initParams() {
		position = getIntent().getIntExtra("position", 0);
		flag = getIntent().getIntExtra("flag", 0);
		pics = getIntent().getStringArrayListExtra("pics");
		System.out.println("zuopics.size" + pics.size());
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		cuPosition = position;
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		tv_downpic.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_lookpic);
		vpData = new ArrayList<View>();
	}

	private void initVp() {

		adapter.notifyDataSetChanged();
		vp.setCurrentItem(position);
		vp.setOnPageChangeListener(this);
		vp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	class MyVPAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pics.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			PhotoView photoView = new PhotoView(Activity_LookPic.this);
			container.addView(photoView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (flag == 1) {
				Glide.with(Activity_LookPic.this).load(pics.get(position))
				/*
				 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
				 */
				.crossFade().into(photoView);
				// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
				// pics.get(position), photoView, options);
				PhotoViewAttacher mAttacher = new PhotoViewAttacher(photoView);
				mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

					@Override
					public void onPhotoTap(View view, float x, float y) {
						// TODO Auto-generated method stub
						finish();
					}
				});
			} else if (flag == 2) {
				Glide.with(Activity_LookPic.this).load(pics.get(position))
				/*
				 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
				 */
				.crossFade().into(photoView);
				// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
				// pics.get(position), photoView, options);
				PhotoViewAttacher mAttacher = new PhotoViewAttacher(photoView);
				mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

					@Override
					public void onPhotoTap(View view, float x, float y) {
						// TODO Auto-generated method stub
						finish();
					}
				});
			} else {
				Glide.with(Activity_LookPic.this).load(AppFinalUrl.photoBaseUri + pics.get(position))
				/*
				 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
				 */
				.crossFade().into(photoView);
				// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
				// pics.get(position), photoView, options);
				PhotoViewAttacher mAttacher = new PhotoViewAttacher(photoView);
				mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

					@Override
					public void onPhotoTap(View view, float x, float y) {
						// TODO Auto-generated method stub
						finish();
					}
				});
			}

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// ImageLoader.getInstance().displayImage(
		// AppFinalUrl.BASEURL + pics.get(arg0), im_vp, options);
		tv_number.setText("" + (arg0 + 1));
		cuPosition = arg0;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("查看大图");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("查看大图");
	}
}
