package com.femto.ugershop.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_Search;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class Fragment_Store extends BaseFragment implements OnClickListener {
	private View view;
	private RelativeLayout rl_designer, rl_newgoods, rl_menu, rl_sreach;
	private ImageView im_right, im_left;
	private Fragment_NewGoods fnew;
	private Fragment_Designer fdesi;
	private FragmentTransaction transaction;
	private PopupWindow ppw_one, ppw_two;
	private View customViewone, customViewtwo;
	private ListView lv_ppwtow;
	private MyPopuLVAdapter plvadapter;
	private AlertDialog serachdialog;
	private List<ProductJson> productJson;
	private int sex = 0, ctype = 0;
	private List<CothTypy> coths;
	private boolean islogin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_store, container, false);
		productJson = new ArrayList<ProductJson>();
		coths = new ArrayList<CothTypy>();
		initView(view);

		MobclickAgent.onPageStart("商城");
		return view;
	}

	private void initView(View v) {
		plvadapter = new MyPopuLVAdapter();
		rl_newgoods = (RelativeLayout) v.findViewById(R.id.rl_newgoods);
		rl_designer = (RelativeLayout) v.findViewById(R.id.rl_designer);
		rl_menu = (RelativeLayout) v.findViewById(R.id.rl_menu);
		rl_menu.setOnClickListener(this);
		rl_designer.setOnClickListener(this);
		rl_newgoods.setOnClickListener(this);
		im_left = (ImageView) v.findViewById(R.id.im_left);
		im_right = (ImageView) v.findViewById(R.id.im_right);
		rl_sreach = (RelativeLayout) v.findViewById(R.id.rl_sreach);
		rl_sreach.setOnClickListener(this);
		
		getData();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_newgoods:
			rl_sreach.setVisibility(View.VISIBLE);
			rl_menu.setVisibility(View.VISIBLE);
			fragmentShowOrHide(fnew, fdesi, false);
			im_left.setBackgroundResource(R.drawable.frame_select_left);
			im_right.setBackgroundColor(Color.TRANSPARENT);
			break;
		case R.id.rl_designer:
			// rl_sreach.setVisibility(View.INVISIBLE);
			// rl_menu.setVisibility(View.INVISIBLE);
			// fragmentShowOrHide(fdesi, fnew, false);
			// im_right.setBackgroundResource(R.drawable.frame_select_righ);
			// im_left.setBackgroundColor(Color.TRANSPARENT);
			break;
		case R.id.rl_menu:
			if (ppw_one != null && ppw_one.isShowing()) {
				ppw_one.dismiss();

			} else {
				initPpwone();

				ppw_one.showAsDropDown(v, 0, 0);

			}
			break;
		case R.id.rl_sreach:
			Intent intent = new Intent(getActivity(), Activity_Search.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	private void showSearchDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View v = View.inflate(getActivity(), R.layout.dialog_search, null);
		builder.setView(v);
		serachdialog = builder.create();
		serachdialog.show();
	}

	private void initFragment() {
		transaction = getFragmentManager().beginTransaction();
		fnew = new Fragment_NewGoods();
		fdesi = new Fragment_Designer();
		transaction.add(R.id.fl_fragmentcontain_store, fnew);
		transaction.add(R.id.fl_fragmentcontain_store, fdesi);
		fragmentShowOrHide(fnew, fdesi, true);
	}

	private void fragmentShowOrHide(Fragment showFragment, Fragment hideFragment1, boolean isInit) {
		if (!isInit) {
			transaction = getFragmentManager().beginTransaction();
		}
		transaction.show(showFragment);
		transaction.hide(hideFragment1);
		transaction.commit();
	}

	public void initPpwone() {
		customViewone = View.inflate(getActivity(), R.layout.popu_newgoodsone, null);
		lv_ppwtow = (ListView) customViewone.findViewById(R.id.lv_poputwo);

		lv_ppwtow.setAdapter(plvadapter);
		ppw_one = new PopupWindow(customViewone, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		ppw_one.setFocusable(true);
		ppw_one.setBackgroundDrawable(new BitmapDrawable());
		lv_ppwtow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("clothId", coths.get(position).cotherId);
				intent.setAction("com.search.cloth");
				getActivity().sendBroadcast(intent);
				ppw_one.dismiss();
			}
		});
		RelativeLayout rl_woman = (RelativeLayout) customViewone.findViewById(R.id.rl_woman);
		RelativeLayout rl_man = (RelativeLayout) customViewone.findViewById(R.id.rl_man);
		RelativeLayout rl_up = (RelativeLayout) customViewone.findViewById(R.id.rl_up);
		RelativeLayout rl_down = (RelativeLayout) customViewone.findViewById(R.id.rl_down);
		final ImageView im_women = (ImageView) customViewone.findViewById(R.id.im_women);
		final ImageView im_man = (ImageView) customViewone.findViewById(R.id.im_man);
		final ImageView im_up = (ImageView) customViewone.findViewById(R.id.im_up);
		final ImageView im_down = (ImageView) customViewone.findViewById(R.id.im_down);
		rl_woman.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sex = 1;
				im_women.setVisibility(View.VISIBLE);
				im_man.setVisibility(View.INVISIBLE);
				coths = productJson.get(sex).smallProductJy.get(ctype).cothTypy;
				plvadapter.notifyDataSetChanged();
			}
		});
		rl_man.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sex = 0;
				im_man.setVisibility(View.VISIBLE);
				im_women.setVisibility(View.INVISIBLE);
				coths = productJson.get(sex).smallProductJy.get(ctype).cothTypy;
				plvadapter.notifyDataSetChanged();
			}
		});
		rl_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ctype = 0;
				im_up.setVisibility(View.VISIBLE);
				im_down.setVisibility(View.INVISIBLE);
				coths = productJson.get(sex).smallProductJy.get(ctype).cothTypy;
				plvadapter.notifyDataSetChanged();
			}
		});
		rl_down.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ctype = 1;
				im_down.setVisibility(View.VISIBLE);
				im_up.setVisibility(View.INVISIBLE);
				coths = productJson.get(sex).smallProductJy.get(ctype).cothTypy;
				plvadapter.notifyDataSetChanged();
			}
		});

	};

	// public void initPpwtwo() {
	// customViewtwo = View.inflate(getActivity(), R.layout.popu_newgoodstwo,
	// null);
	// lv_ppwtow = (ListView) customViewtwo.findViewById(R.id.lv_poputwo);
	// plvadapter = new MyPopuLVAdapter();
	// lv_ppwtow.setAdapter(plvadapter);
	// ppw_two = new PopupWindow(customViewtwo, LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT, false);
	// ppw_two.setFocusable(true);
	// ppw_two.setBackgroundDrawable(new BitmapDrawable());
	// };

	class MyPopuLVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return coths == null ? 0 : coths.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return coths.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			v = View.inflate(getActivity(), R.layout.item_lv_popu, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_sort_name);
			tv_sort_name.setText("" + coths.get(position).cotherName);
			return v;
		}
	}

	public void getData() {
		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetShowSort, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("productJson");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						int sexId = j.optInt("sexId");
						String sexName = j.optString("sexName");
						List<SmallProductJy> smallProductJy = new ArrayList<SmallProductJy>();
						JSONArray optJSONArray = j.optJSONArray("smallProductJy");
						for (int k = 0; k < optJSONArray.length(); k++) {
							JSONObject jj = optJSONArray.getJSONObject(k);
							int sortId = jj.optInt("sortId");
							String sortName = jj.optString("sortName");
							List<CothTypy> cothTypy = new ArrayList<CothTypy>();
							JSONArray optJSONArray2 = jj.optJSONArray("cothTypy");
							for (int l = 0; l < optJSONArray2.length(); l++) {
								JSONObject jjj = optJSONArray2.getJSONObject(l);
								int cotherId = jjj.optInt("cotherId");
								String cotherName = jjj.optString("cotherName");
								cothTypy.add(new CothTypy(cotherId, cotherName));
							}

							smallProductJy.add(new SmallProductJy(sortId, sortName, cothTypy));
						}
						productJson.add(new ProductJson(sexId, sexName, smallProductJy));
					}
					coths = productJson.get(1).smallProductJy.get(0).cothTypy;
					plvadapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class ProductJson {
		int sexId;
		String sexName;
		List<SmallProductJy> smallProductJy;

		public ProductJson(int sexId, String sexName, List<SmallProductJy> smallProductJy) {
			super();
			this.sexId = sexId;
			this.sexName = sexName;
			this.smallProductJy = smallProductJy;
		}

	}

	class SmallProductJy {
		int sortId;
		String sortName;
		List<CothTypy> cothTypy;

		public SmallProductJy(int sortId, String sortName, List<CothTypy> cothTypy) {
			super();
			this.sortId = sortId;
			this.sortName = sortName;
			this.cothTypy = cothTypy;
		}
	}

	class CothTypy {
		int cotherId;
		String cotherName;

		public CothTypy(int cotherId, String cotherName) {
			super();
			this.cotherId = cotherId;
			this.cotherName = cotherName;
		}

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		System.out.println("zuo==商城==" + hidden);
		if (!hidden) {
			if (!islogin) {
				islogin = true;
				initFragment();
			}
			MobclickAgent.onPageStart("商城");
		} else {
			MobclickAgent.onPageEnd("商城");
		}
	}
}
