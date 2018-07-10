package com.femto.ugershop.fragment;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_ConsultCen;
import com.femto.ugershop.activity.Activity_List;
import com.femto.ugershop.activity.Activity_Login;
import com.femto.ugershop.activity.Activity_Message;
import com.femto.ugershop.activity.Activity_MyCustom;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Fragment_Message extends BaseFragment implements OnClickListener {
	private View view;
	private RelativeLayout rl_myup, rl_activity_c, rl_messageme, rl_sercice_c;
	private TextView tv_unredcount_message_c, tv_unred_myup;
	private int allCount, aMeCount, discussCount, topCount1, uploadCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_message, container, false);
		initView(view);
		initCont();
		return view;
	}

	private void initView(View v) {
		// TODO Auto-generated method stub
		rl_myup = (RelativeLayout) v.findViewById(R.id.rl_myup);
		rl_activity_c = (RelativeLayout) v.findViewById(R.id.rl_activity_c);
		rl_messageme = (RelativeLayout) v.findViewById(R.id.rl_messageme);
		tv_unredcount_message_c = (TextView) v.findViewById(R.id.tv_unredcount_message_c1);
		tv_unred_myup = (TextView) v.findViewById(R.id.tv_unred_myup);
		rl_sercice_c = (RelativeLayout) v.findViewById(R.id.rl_sercice_c);
		rl_sercice_c.setOnClickListener(this);
	}

	private void initCont() {
		// TODO Auto-generated method stub
		rl_myup.setOnClickListener(this);
		rl_activity_c.setOnClickListener(this);
		rl_messageme.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_myup:
			if (MyApplication.islogin) {
				Intent intent_ = new Intent(getActivity(), Activity_MyCustom.class);
				startActivity(intent_);
			} else {
				Intent intent_ = new Intent(getActivity(), Activity_Login.class);
				startActivity(intent_);
			}
			break;
		case R.id.rl_activity_c:
			Intent intent_activity = new Intent(getActivity(), Activity_List.class);
			intent_activity.putExtra("type", 2);
			startActivity(intent_activity);
			break;
		case R.id.rl_messageme:
			Intent intent_message = new Intent(getActivity(), Activity_Message.class);
			intent_message.putExtra("allCount", allCount);
			intent_message.putExtra("aMeCount", aMeCount);
			intent_message.putExtra("discussCount", discussCount);
			intent_message.putExtra("topCount", topCount1);
			startActivity(intent_message);
			break;
		case R.id.rl_sercice_c:
			Intent intent_consult = new Intent(getActivity(), Activity_ConsultCen.class);
			startActivity(intent_consult);
			break;
		default:
			break;
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			getUnread();
		}

	}

	private void getUnread() {
		RequestParams params = new RequestParams();
		params.put("userId", MyApplication.userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyMessageCount, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				// allCount, aMeCount, discussCount, topCount1
				System.out.println("zuo-response-" + response.toString());
				allCount = 0;
				aMeCount = 0;
				discussCount = 0;
				topCount1 = 0;
				allCount = response.optInt("allCount");
				aMeCount = response.optInt("aMeCount");
				discussCount = response.optInt("discussCount");
				uploadCount = response.optInt("uploadCount");
				topCount1 = response.optInt("topCount");
				if (allCount != 0) {
					tv_unredcount_message_c.setVisibility(View.VISIBLE);
					if (allCount > 99) {
						tv_unredcount_message_c.setText("99+");
					} else {
						tv_unredcount_message_c.setText("" + allCount);
					}

				} else {
					tv_unredcount_message_c.setVisibility(View.GONE);
				}
				if (uploadCount > 0) {
					tv_unred_myup.setVisibility(View.VISIBLE);
					if (uploadCount > 99) {
						tv_unred_myup.setText("99+");
					} else {
						tv_unred_myup.setText("" + uploadCount);
					}

				} else {
					tv_unred_myup.setVisibility(View.GONE);
				}

			}
		});
	}

}
