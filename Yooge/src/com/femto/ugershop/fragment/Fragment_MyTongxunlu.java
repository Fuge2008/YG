package com.femto.ugershop.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.hx.adapter.MyConAdaptr;
import com.femto.hx.adapter.SortAdapter;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.easemob.applib.domain.ContactsList;
import com.femto.ugershop.view.CharacterParser;
import com.femto.ugershop.view.CustomProgressDialog;
import com.femto.ugershop.view.MySlidebar;
import com.femto.ugershop.view.MySlidebar.OnTouchingLetterChangedListener;
import com.femto.ugershop.view.PinyinComparator;
import com.femto.ugershop.view.SortModel;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class Fragment_MyTongxunlu extends BaseFragment implements OnItemClickListener, OnItemLongClickListener {
	private ListView lv_mycontacts;
	private List<ContactsList> contans;
	private int myId;
	private DisplayImageOptions options;
	private MySlidebar sibar;
	private TextView dialog;
	private SortAdapter stadapter;
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private PinyinComparator pinyinComparator;
	private CustomProgressDialog pd = null;
	private int userId;
	private int flag = 0;

	public void showProgressDialog(String title) {
		// TODO Auto-generated method stub
		if (pd == null) {
			pd = CustomProgressDialog.createDialog(getActivity());
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

	public Fragment_MyTongxunlu(int userId, int flag) {
		super();
		this.userId = userId;
		this.flag = flag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = View.inflate(getActivity(), R.layout.fragment_mycontacts, null);
		initParams();
		initView(v);
		return v;
	}

	private void initView(View v) {
		contans = new ArrayList<ContactsList>();
		lv_mycontacts = (ListView) v.findViewById(R.id.lv_mycontacts);
		sibar = (MySlidebar) v.findViewById(R.id.sidrbar);
		dialog = (TextView) v.findViewById(R.id.dialog);
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		sibar.setTextView(dialog);

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
		lv_mycontacts.setOnItemClickListener(this);
		lv_mycontacts.setOnItemLongClickListener(this);
		if (flag == 2) {
			getData();
		}

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden && contans.size() == 0) {
			getData();
		}
		if (!hidden) {
			MobclickAgent.onResume(getActivity());
			setPageStart("通讯录");
		} else {
			MobclickAgent.onPause(getActivity());
			setPageEnd("通讯录");
		}
	}

	private void getData() {
		showProgressDialog("正在加载...");
		RequestParams params = new RequestParams();
		params.put("userId", userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyAction, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo==" + response.toString());
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
						c.setInfo(j.optString("info"));
						contans.add(c);
					}
					SourceDateList = filledData(contans);
					Collections.sort(SourceDateList, pinyinComparator);
					stadapter = new SortAdapter(getActivity(), SourceDateList);
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
		SharedPreferences sp = getActivity().getSharedPreferences("Login", getActivity().MODE_PRIVATE);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("name", SourceDateList.get(position).getUserName())
				.putExtra("userId", "" + SourceDateList.get(position).getUserId()));
	}

	private List<SortModel> filledData(List<ContactsList> date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date.get(i).getUserName());
			sortModel.setImgUrl(date.get(i).getImgUrl());
			sortModel.setUserName(date.get(i).getUserName());
			sortModel.setUserId(date.get(i).getUserId());
			sortModel.setInfo(date.get(i).getInfo());
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

	private void showDeleDialog(final int id, final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示").setMessage("确定删除此联系人?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				cancelPeople(id, position);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		showDeleDialog(SourceDateList.get(position).getUserId(), position);
		return true;
	}

	private void cancelPeople(int befid, final int position) {
		RequestParams params = new RequestParams();
		params.put("friend.user.id", myId);
		params.put("friend.beuser.id", befid);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercancleFocus, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						SourceDateList.remove(position);
						stadapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
