package com.femto.ugershop.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;

@SuppressLint("ValidFragment")
public class Fragment_Production extends BaseFragment implements OnClickListener {
	private View view;
	private Fragment_SalseProd fsalse;
	private Fragment_LastProd flast;
	private Fragment_CustomProd fcustom;
	private FragmentTransaction transaction;
	private RadioButton rb_custpro, rb_last, rb_salse;
	private int userId;
	private List<ProductList> productList;
	private boolean initFragment = false;

	public Fragment_Production(int userId) {
		super();
		this.userId = userId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_producation, container, false);
		productList = new ArrayList<ProductList>();
		initView(view);
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden && !initFragment) {
			initFragment = true;

			initFragment();
		}
	}

	private void getData(int type) {
		RequestParams params = new RequestParams();
		params.put("type", type);
		params.put("userId", userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetProductByUserIdAndType, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("productList");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String title = j.optString("title");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String url = j.optString("url");
						int shareCount = j.optInt("shareCount");
						int topCount = j.optInt("topCount");
						int discussCount = j.optInt("discussCount");
						int productId = j.optInt("productId");
						productList.add(new ProductList(title, userName, createDate, url, shareCount, topCount, discussCount,
								productId));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	private void initView(View v) {
		rb_custpro = (RadioButton) v.findViewById(R.id.rb_custpro);
		rb_last = (RadioButton) v.findViewById(R.id.rb_last);
		rb_salse = (RadioButton) v.findViewById(R.id.rb_salse);
		rb_salse.setOnClickListener(this);
		rb_last.setOnClickListener(this);
		rb_custpro.setOnClickListener(this);
	}

	private void initFragment() {
		transaction = getFragmentManager().beginTransaction();
		fsalse = new Fragment_SalseProd(userId);
		flast = new Fragment_LastProd(userId);
		fcustom = new Fragment_CustomProd(userId);
		transaction.add(R.id.fl_conprod, fsalse);
		transaction.add(R.id.fl_conprod, flast);
		transaction.add(R.id.fl_conprod, fcustom);
		fragmentShowOrHide(fsalse, flast, fcustom, true);
	}

	private void fragmentShowOrHide(Fragment showFragment, Fragment hideFragment1, Fragment hideFragment2, boolean isInit) {
		if (!isInit) {
			transaction = getFragmentManager().beginTransaction();
		}
		transaction.show(showFragment);
		transaction.hide(hideFragment1);
		transaction.hide(hideFragment2);
		transaction.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_custpro:
			fragmentShowOrHide(fcustom, fsalse, flast, false);
			break;
		case R.id.rb_last:
			fragmentShowOrHide(flast, fsalse, fcustom, false);
			break;
		case R.id.rb_salse:
			fragmentShowOrHide(fsalse, flast, fcustom, false);
			break;

		default:
			break;
		}

	}

	class ProductList {
		String title, userName, createDate, url;
		int shareCount, topCount, discussCount, productId;

		public ProductList(String title, String userName, String createDate, String url, int shareCount, int topCount,
				int discussCount, int productId) {
			super();
			this.title = title;
			this.userName = userName;
			this.createDate = createDate;
			this.url = url;
			this.shareCount = shareCount;
			this.topCount = topCount;
			this.discussCount = discussCount;
			this.productId = productId;
		}
	}
}
