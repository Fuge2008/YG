package com.femto.ugershop.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.AndroidCaptureCropTags.customview.HGAlertDlg;
import com.example.AndroidCaptureCropTags.customview.HGTagPickerView;
import com.example.AndroidCaptureCropTags.customview.HGTipsDlg;
import com.example.AndroidCaptureCropTags.model.TagInfoModel;
import com.example.AndroidCaptureCropTags.tagview.TagInfo;
import com.example.AndroidCaptureCropTags.tagview.TagView;
import com.example.AndroidCaptureCropTags.tagview.TagView.TagViewMoveListener;
import com.example.AndroidCaptureCropTags.tagview.TagViewLeft;
import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.Flags;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by sreay on 14-9-22.
 */
public class ActivityAddTags extends Activity implements View.OnClickListener, View.OnTouchListener, TagView.TagViewListener,
		TagViewMoveListener, HGAlertDlg.HGAlertDlgClickListener, HGTagPickerView.HGTagPickerViewListener {
	private ImageView headLeft;
	private TextView headTitle;
	private TextView headRight;
	private ImageView image;
	private FrameLayout tagsContainer;

	private float positionX = 0;
	private float positionY = 0;
	private int width;
	private int height;
	private List<TagView> tagViews = new ArrayList<TagView>();
	private List<TagInfo> tagInfos = new ArrayList<TagInfo>();

	private static final String KImagePath = "image_path";
	private String imagePath = "";
	public Bitmap bitmap;

	private HGAlertDlg dlg;
	private HGTagPickerView tagPickerView;
	private HGTipsDlg tipsDlg;
	private int tagsCount = 0;
	private List<String> base = Arrays.asList("时尚流", "小清新", "复古风", "甜美风", "中性风", "作死", "这就是我", "清晨的宁静", "下午茶", "后会无期",
			"no zuo no die", "随心所欲");
	private String content = "";
	private View vview;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				tagsCount--;
				tagViews.remove(vview);
				tagsContainer.removeView(vview);
				break;

			default:
				break;
			}
		};
	};
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("添加标签");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("添加标签");
	}
	public void setPageStart(String name) {
		if (name != null) {
			MobclickAgent.onPageStart(name);
		}

	}

	public void setPageEnd(String name) {
		if (name != null) {
			MobclickAgent.onPageEnd(name);
		}
	}

	public static void open(Activity activity, String imagePath) {
		Intent intent = new Intent(activity, ActivityAddTags.class);
		intent.putExtra(KImagePath, imagePath);
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_add_tags);
		MyApplication.addActivity(this);
		MyApplication.addFlagActivity(this);
		if (getIntent() != null) {
			Intent intent = getIntent();
			imagePath = intent.getStringExtra(KImagePath);
			System.out.println("zuo===" + imagePath);
		}
		getViews();
		initViews();
		setListeners();
	}

	protected void getViews() {
		headLeft = (ImageView) findViewById(R.id.tv_head_left);
		headTitle = (TextView) findViewById(R.id.tv_head_title);
		headRight = (TextView) findViewById(R.id.tv_head_right);
		headRight.setVisibility(View.VISIBLE);
		image = (ImageView) findViewById(R.id.image);
		tagsContainer = (FrameLayout) findViewById(R.id.tagsContainer);
	}

	protected void initViews() {
		headTitle.setText("添加标签");
		headRight.setText("完成");
		// bitmap = BitmapUtil.loadBitmap(imagePath);
		// image.setImageBitmap(bitmap);
		File imageFile = new File(imagePath);
		Picasso.with(this).load(imageFile).placeholder(R.drawable.tianc).error(R.drawable.tianc).resize(180, 180).centerCrop()
				.into(image);
	}

	protected void setListeners() {
		headLeft.setOnClickListener(this);
		headRight.setOnClickListener(this);
		image.setOnClickListener(this);
		image.setOnTouchListener(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		this.width = image.getMeasuredWidth();
		this.height = image.getMeasuredHeight();
	}

	@Override
	protected void onDestroy() {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_head_left:
			this.finish();
			break;
		case R.id.tv_head_right:
			goToEditFootprints();
			break;
		case R.id.image: {
			if (tagsCount >= 20) {
				if (!HGTipsDlg.hasDlg(this)) {
					tipsDlg = HGTipsDlg.showDlg("最多添加20个标签~", this);
				}
				return;
			}
			if (tagPickerView == null) {
				tagPickerView = HGTagPickerView.showDlg(base, this, this);
			}
		}
			break;
		}
	}

	private void goToEditFootprints() {
		List<TagInfoModel> tagInfos = new ArrayList<TagInfoModel>();
		for (TagView tagView : tagViews) {
			TagInfoModel infoModel = new TagInfoModel();
			infoModel.tag_name = tagView.getData().bname;
			infoModel.x = (tagView.getData().leftMargin * 1.0f) / (width * 1.0f);
			infoModel.y = (tagView.getData().topMargin * 1.0f) / (height * 1.0f);
			tagInfos.add(infoModel);
		}
		MyApplication.flags.add(new Flags(imagePath, tagInfos));
		// todo
		// 在应用程序中需要将这些tag的信息上传到服务器，此处为了展示方便将其存储在本地
		// LocalStatic.addTagInfos(tagInfos);
		// LocalStatic.path = imagePath;
		Intent intent = new Intent(this, Activity_Send_Flags.class);
		startActivity(intent);
		this.finish();
	}

	private void addTag(float px, float py) {
		tagsCount++;
		TagInfo tagInfo = new TagInfo();
		tagInfo.bid = 2L;
		tagInfo.bname = content;
		tagInfo.direct = TagInfo.Direction.Left;
		tagInfo.pic_x = 50;
		tagInfo.pic_y = 50;
		tagInfo.type = TagInfo.Type.CustomPoint;
		tagInfo.leftMargin = (int) px;
		tagInfo.topMargin = (int) py;
		TagView tagView = new TagViewLeft(this, null);
		tagView.setData(tagInfo);
		tagView.setTagViewListener(this);
		tagView.setTagViewMoveListener(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = tagInfo.leftMargin;
		params.topMargin = tagInfo.topMargin;
		tagViews.add(tagView);
		tagsContainer.addView(tagView, params);
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		int action = motionEvent.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			positionX = motionEvent.getX();
			positionY = motionEvent.getY();
		}
		return false;
	}

	private View destView;

	@Override
	public void onTagViewClicked(View view, TagInfo info) {
		destView = view;
		if (null == dlg) {
			dlg = HGAlertDlg.showDlg("确定删除该标签?", null, this, this);
			// tagsContainer.removeView(destView);
		}
	}

	@Override
	public void onBackPressed() {
		if (tipsDlg != null) {
			tipsDlg.onBackPressed(this);
			tipsDlg = null;
			return;
		}
		if (dlg != null) {
			dlg.onBackPressed(this);
			dlg = null;
			return;
		}
		if (tagPickerView != null) {
			tagPickerView.onBackPressed(this);
			tagPickerView = null;
			return;
		}
		super.onBackPressed();
	}

	private void removeTag(View view) {
		vview = view;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				handler.sendEmptyMessage(1);
			}
		}).start();

	}

	@Override
	public void onAlertDlgClicked(boolean isConfirm) {
		if (isConfirm) {
			removeTag(destView);
		}
		dlg = null;
	}

	@Override
	public void onTagPickerViewClicked(String content, boolean isConfirm) {
		if (isConfirm) {
			this.content = content;
			addTag(positionX, positionY);
		}
		tagPickerView = null;
	}

	@Override
	public void onTagViewMove(View view, TagInfo info) {
		removeTag(view);
		addTag(info.leftMargin, info.topMargin);
	}
}
