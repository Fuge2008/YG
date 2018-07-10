package com.femto.ugershop.activity;

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
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月12日 下午2:55:20 类说明
 */
public class Activity_FilterCloth extends SwipeBackActivity implements OnItemClickListener {
	private RelativeLayout rl_back_filter, rl_man_filter, rl_woman_filter;
	private List<CothTypy> coths;
	private List<ProductJson> productJson;
	// private ImageView im_left1d_filter, im_right1d_filter;
	private TextView tv_woman, tv_man;
	private MyGVAdapter gvadapter;
	private GridView gv_cloth;
	private int sex = 2, ctype = 0;
	private TextView tv_upcloth, tv_downcloth, tv_all_cloth;
	private String clothType = "上装";
	private ListView lv_upordown;
	private List<SmallProductJy> data;
	private MyLvAdapter lvadapter;
	private CothTypy ct;
	private SmallProductJy sp;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_filter:
			finish();
			break;
		case R.id.rl_man_filter:
			// im_left1d_filter.setBackgroundResource(R.drawable.frame_select_righ);
			// im_right1d_filter.setBackgroundColor(Color.TRANSPARENT);

			tv_man.setBackgroundResource(R.drawable.sele_clothtype);
			tv_man.setTextColor(Color.parseColor("#FFFFFF"));

			tv_woman.setBackgroundResource(R.drawable.sele_cloths_type_no2);
			tv_woman.setTextColor(Color.parseColor("#595959"));

			sex = 1;
			for (int i = 0; i < productJson.size(); i++) {
				if (productJson.get(i).sexId == sex) {
					ctype = 0;
					coths = productJson.get(i).smallProductJy.get(ctype).cothTypy;
					data = productJson.get(i).smallProductJy;
					lvadapter.notifyDataSetChanged();
					gvadapter.notifyDataSetChanged();
				}
			}

			// coths = productJson.get(sex).smallProductJy.get(ctype).cothTypy;
			// gvadapter.notifyDataSetChanged();
			break;
		case R.id.rl_woman_filter:
			// im_right1d_filter.setBackgroundResource(R.drawable.frame_select_left);
			// im_left1d_filter.setBackgroundColor(Color.TRANSPARENT);

			tv_woman.setBackgroundResource(R.drawable.sele_clothtype);
			tv_woman.setTextColor(Color.parseColor("#FFFFFF"));

			tv_man.setBackgroundResource(R.drawable.sele_cloths_type_no2);
			tv_man.setTextColor(Color.parseColor("#595959"));

			sex = 2;
			// for (int i = 0; i < productJson.size(); i++) {
			// if (productJson.get(i).sexId == sex) {
			// for (int j = 0; j < productJson.get(i).smallProductJy.size();
			// j++) {
			// if
			// (productJson.get(i).smallProductJy.get(j).sortName.equals(clothType))
			// {
			// coths = productJson.get(i).smallProductJy.get(j).cothTypy;
			// gvadapter.notifyDataSetChanged();
			// }
			// }
			// }
			// }

			for (int i = 0; i < productJson.size(); i++) {
				if (productJson.get(i).sexId == sex) {
					ctype = 0;
					coths = productJson.get(i).smallProductJy.get(ctype).cothTypy;
					data = productJson.get(i).smallProductJy;
					lvadapter.notifyDataSetChanged();
					gvadapter.notifyDataSetChanged();
				}
			}
			break;
		case R.id.tv_all_cloth:
			tv_all_cloth.setBackgroundColor(getResources().getColor(R.color.tab_main_color));
			Intent intent = new Intent();
			intent.putExtra("clothId", 0);
			intent.setAction("com.search.cloth");
			sendBroadcast(intent);
			finish();
			break;
		// case R.id.tv_upcloth:
		// ctype = 0;
		// clothType = "上装";
		// for (int i = 0; i < productJson.size(); i++) {
		// if (productJson.get(i).sexId == sex) {
		// for (int j = 0; j < productJson.get(i).smallProductJy.size(); j++) {
		// if
		// (productJson.get(i).smallProductJy.get(j).sortName.equals(clothType))
		// {
		// coths = productJson.get(i).smallProductJy.get(j).cothTypy;
		// gvadapter.notifyDataSetChanged();
		// }
		// }
		// }
		// }
		// tv_upcloth.setBackgroundColor(getResources().getColor(R.color.sgray));
		// tv_downcloth.setBackgroundColor(getResources().getColor(R.color.white));
		// break;
		// case R.id.tv_downcloth:
		// ctype = 1;
		// clothType = "下装";
		// for (int i = 0; i < productJson.size(); i++) {
		// if (productJson.get(i).sexId == sex) {
		// for (int j = 0; j < productJson.get(i).smallProductJy.size(); j++) {
		// if
		// (productJson.get(i).smallProductJy.get(j).sortName.equals(clothType))
		// {
		// coths = productJson.get(i).smallProductJy.get(j).cothTypy;
		// gvadapter.notifyDataSetChanged();
		// }
		// }
		// }
		// }
		// tv_upcloth.setBackgroundColor(getResources().getColor(R.color.white));
		// tv_downcloth.setBackgroundColor(getResources().getColor(R.color.sgray));
		// break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		productJson = new ArrayList<ProductJson>();
		coths = new ArrayList<CothTypy>();
		data = new ArrayList<SmallProductJy>();
		ct = new CothTypy(0, "全部");
		sp = new SmallProductJy(1, "", null);
		rl_back_filter = (RelativeLayout) findViewById(R.id.rl_back_filter);
		rl_man_filter = (RelativeLayout) findViewById(R.id.rl_man_filter);
		rl_woman_filter = (RelativeLayout) findViewById(R.id.rl_woman_filter);
		// im_left1d_filter = (ImageView) findViewById(R.id.im_left1d_filter);
		// im_right1d_filter = (ImageView) findViewById(R.id.im_right1d_filter);
		tv_woman = (TextView) findViewById(R.id.tv_woman);
		tv_man = (TextView) findViewById(R.id.tv_man);
		tv_all_cloth = (TextView) findViewById(R.id.tv_all_cloth);
		// tv_upcloth = (TextView) findViewById(R.id.tv_upcloth);
		// tv_downcloth = (TextView) findViewById(R.id.tv_downcloth);
		lv_upordown = (ListView) findViewById(R.id.lv_upordown);
		gv_cloth = (GridView) findViewById(R.id.gv_cloth);
		gvadapter = new MyGVAdapter();
		lvadapter = new MyLvAdapter();
		lv_upordown.setAdapter(lvadapter);
		gv_cloth.setAdapter(gvadapter);
		getData();
		gv_cloth.setOnItemClickListener(this);
		lv_upordown.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				for (int i = 0; i < productJson.size(); i++) {
					if (productJson.get(i).sexId == sex) {
						ctype = position;
						coths = productJson.get(i).smallProductJy.get(ctype).cothTypy;
						gvadapter.notifyDataSetChanged();
						lvadapter.notifyDataSetChanged();

					}
				}

			}
		});
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_filter.setOnClickListener(this);
		rl_man_filter.setOnClickListener(this);
		rl_woman_filter.setOnClickListener(this);
		tv_all_cloth.setOnClickListener(this);

	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_filtercloth);
		MyApplication.addActivity(this);
	}

	public void getData() {
		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetShowSort, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo+==" + response.toString());
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
					for (int i = 0; i < productJson.size(); i++) {
						if (productJson.get(i).sexId == sex) {
							coths = productJson.get(i).smallProductJy.get(ctype).cothTypy;
							data = productJson.get(i).smallProductJy;
							lvadapter.notifyDataSetChanged();
							gvadapter.notifyDataSetChanged();

						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class MyGVAdapter extends BaseAdapter {

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
			v = View.inflate(Activity_FilterCloth.this, R.layout.item_lv_popu, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_sort_name);
			tv_sort_name.setText("" + coths.get(position).cotherName);
			tv_sort_name.setBackgroundColor(Color.parseColor("#E5E5E5"));
			return v;
		}
	}

	class MyLvAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data == null ? 0 : data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			v = View.inflate(Activity_FilterCloth.this, R.layout.item_lv_popu, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_sort_name);
			tv_sort_name.setText("" + data.get(position).sortName);
			if (ctype == position) {
				tv_sort_name.setBackgroundColor(getResources().getColor(R.color.tab_main_sele));
				tv_sort_name.setTextColor(Color.parseColor("#FFFFFF"));
			} else {
				tv_sort_name.setBackgroundColor(getResources().getColor(R.color.grey));
				tv_sort_name.setTextColor(Color.parseColor("#908D88"));
			}
			return v;
		}
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("clothId", coths.get(position).cotherId);
		intent.setAction("com.search.cloth");
		sendBroadcast(intent);
		finish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("选择分类");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("选择分类");
	}
}
