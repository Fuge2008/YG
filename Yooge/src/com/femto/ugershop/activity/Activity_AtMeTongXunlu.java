package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.hx.adapter.MySortAdapter;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.easemob.applib.domain.ContactsList;
import com.femto.ugershop.view.CharacterParser;
import com.femto.ugershop.view.CustomProgressDialog;
import com.femto.ugershop.view.MySlidebar;
import com.femto.ugershop.view.PinyinComparator;
import com.femto.ugershop.view.SortModel;
import com.femto.ugershop.view.MySlidebar.OnTouchingLetterChangedListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_AtMeTongXunlu extends SwipeBackActivity implements OnItemClickListener {
	private ListView lv_mycontacts;
	private List<ContactsList> contans;
	private int myId;
	private DisplayImageOptions options;
	private MySlidebar sibar;
	private TextView dialog, tv_sure_sele;
	private MySortAdapter stadapter;
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private PinyinComparator pinyinComparator;
	private CustomProgressDialog pd = null;
	private RelativeLayout rl_back_tong, rl_top_tongxunlu;
	private ArrayList<Integer> ids;
	private ArrayList<String> names;

	public void showProgressDialog(String title) {
		// TODO Auto-generated method stub
		if (pd == null) {
			pd = CustomProgressDialog.createDialog(this);
			pd.setMessage(title);
		}
		pd.setCanceledOnTouchOutside(true);
		pd.show();
	}

	/**
	 * 取消加载框
	 */
	public void dismissProgressDialog() {
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_tong:
			finish();
			break;
		case R.id.tv_sure_sele:
			suresele();
			break;

		default:
			break;
		}
	}

	private void suresele() {
		for (int i = 0; i < SourceDateList.size(); i++) {
			if (SourceDateList.get(i).isIssele()) {
				ids.add(SourceDateList.get(i).getUserId());
				names.add(SourceDateList.get(i).getName());
			}
		}
		Intent intent = new Intent();
		intent.putIntegerArrayListExtra("ids", ids);
		intent.putStringArrayListExtra("names", names);
		setResult(111, intent);
		finish();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		MyApplication.addActivity(this);
		contans = new ArrayList<ContactsList>();
		ids = new ArrayList<Integer>();
		names = new ArrayList<String>();
		lv_mycontacts = (ListView) findViewById(R.id.lv_mycontacts);
		lv_mycontacts.setOnItemClickListener(this);

		sibar = (MySlidebar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		rl_top_tongxunlu = (RelativeLayout) findViewById(R.id.rl_top_tongxunlu);
		rl_top_tongxunlu.setVisibility(View.VISIBLE);
		rl_back_tong = (RelativeLayout) findViewById(R.id.rl_back_tong);
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		sibar.setTextView(dialog);
		tv_sure_sele = (TextView) findViewById(R.id.tv_sure_sele);
		sibar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				int position = -1;
				if (contans.size() != 0) {
					position = stadapter.getPositionForSection(s.charAt(0));
				}

				if (position != -1) {
					if (contans.size() != 0) {
						lv_mycontacts.setSelection(position);
					}

				}
			}
		});

		getData();
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_tong.setOnClickListener(this);
		tv_sure_sele.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.fragment_mycontacts);
		initParams();
	}

	private void getData() {
		showProgressDialog("正在加载...");
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyAction, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("Firends");
					ContactsList c;
					for (int i = 0; i < jsonArray.length(); i++) {
						c = new ContactsList();
						JSONObject j = jsonArray.getJSONObject(i);
						c.setImgUrl(j.optString("imgUrl"));
						c.setName(j.optString("name"));
						c.setUserId(j.optInt("userId"));
						c.setUserName(j.optString("userName"));
						contans.add(c);
					}
					SourceDateList = filledData(contans);
					Collections.sort(SourceDateList, pinyinComparator);
					stadapter = new MySortAdapter(Activity_AtMeTongXunlu.this, SourceDateList);
					lv_mycontacts.setAdapter(stadapter);
					dismissProgressDialog();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", -1);

		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();

	}

	private List<SortModel> filledData(List<ContactsList> date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date.get(i).getUserName());
			sortModel.setImgUrl(date.get(i).getImgUrl());
			sortModel.setUserName(date.get(i).getUserName());
			sortModel.setUserId(date.get(i).getUserId());
			// ����ת����ƴ��
			String pinyin = characterParser.getSelling(date.get(i).getUserName());
			String sortString = "";
			if (pinyin != null && pinyin.length() > 0) {
				sortString = pinyin.substring(0, 1).toUpperCase();
			}

			// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * ���������е�ֵ��������ݲ�����ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}
		Collections.sort(filterDateList, pinyinComparator);
		stadapter.updateListView(filterDateList);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		if (SourceDateList.get(position).isIssele()) {
			SourceDateList.get(position).setIssele(false);
		} else {
			SourceDateList.get(position).setIssele(true);
		}
		stadapter.notifyDataSetChanged();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("我的通讯录");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("我的通讯录");
	}
}
