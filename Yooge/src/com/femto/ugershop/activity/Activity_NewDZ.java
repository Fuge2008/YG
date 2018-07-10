package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.view.ScrollViewWithListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_NewDZ extends BaseActivity implements OnItemClickListener {
	private RelativeLayout rl_back_newdz;
	private GridView gv_habit, gv_special;
	private ScrollViewWithListView lv_kyw;
	private MySpecialAdapter sadapter;
	private MyHabitAdapter hadapter;
	private MyKYEadapter kadapter;
	private List<Spe> spe;
	private List<String> hab;
	private List<String> kyw;
	private int fkyw = -1, fspe = -1, fhab = -1;
	private EditText ed_xiongwei, ed_yaowei, ed_beiwei, ed_kuchang, ed_xiuchang, ed_shengao, ed_tizhong, ed_jiankuan;
	private int shengao, xiuchang, sex, jingkuan, fuWei, xiongwei, beichang, shouBiWei, yaowei, datuichang, tuiwei, tizhong,
			userId, xiaotuichang, tuichang;
	private TextView tv_save_newmode, tv_guidemode;
	private String videoUrl = "http://7xou4r.com2.z0.glb.qiniucdn.com/量尺寸稿%2B%2B%2B无水印.mp4";

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_newdz:
			finish();
			break;
		case R.id.tv_save_newmode:
			String sepcial = "";
			for (int i = 0; i < spe.size(); i++) {
				if (spe.get(i).isChec()) {
					sepcial = sepcial + spe.get(i).getName() + ",";
				}
			}
			upModeCard(ed_xiongwei.getText().toString().trim(), ed_yaowei.getText().toString().trim(), ed_beiwei.getText()
					.toString().trim(), ed_kuchang.getText().toString().trim(), ed_xiuchang.getText().toString().trim(),
					ed_shengao.getText().toString().trim(), ed_tizhong.getText().toString().trim(), ed_jiankuan.getText()
							.toString().trim(), hab.get(fhab), kyw.get(fkyw), sepcial);
			break;
		case R.id.tv_guidemode:

			Intent intent_video = new Intent(this, Activity_Video.class);
			intent_video.putExtra("videourl", videoUrl);
			startActivity(intent_video);

			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_newdz = (RelativeLayout) findViewById(R.id.rl_back_newdz);
		lv_kyw = (ScrollViewWithListView) findViewById(R.id.lv_kyw);
		tv_save_newmode = (TextView) findViewById(R.id.tv_save_newmode);
		tv_guidemode = (TextView) findViewById(R.id.tv_guidemode);
		gv_habit = (GridView) findViewById(R.id.gv_habit);
		gv_special = (GridView) findViewById(R.id.gv_special);
		// ed_xiongwei, ed_yaowei, ed_beiwei, ed_kuchang, ed_xiuchang,
		// ed_shengao, ed_tizhong, ed_jiankuan
		ed_xiongwei = (EditText) findViewById(R.id.ed_xiongwei);
		ed_yaowei = (EditText) findViewById(R.id.ed_yaowei);
		ed_beiwei = (EditText) findViewById(R.id.ed_beiwei);
		ed_kuchang = (EditText) findViewById(R.id.ed_kuchang);
		ed_xiuchang = (EditText) findViewById(R.id.ed_xiuchang);
		ed_shengao = (EditText) findViewById(R.id.ed_shengao);
		ed_tizhong = (EditText) findViewById(R.id.ed_tizhong);
		ed_jiankuan = (EditText) findViewById(R.id.ed_jiankuan);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	class Spe {
		String name;
		boolean chec;

		public Spe(String name, boolean chec) {
			super();
			this.name = name;
			this.chec = chec;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isChec() {
			return chec;
		}

		public void setChec(boolean chec) {
			this.chec = chec;
		}

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		spe = new ArrayList<Spe>();
		spe.add(new Spe("肩偏宽", false));
		spe.add(new Spe("臀偏肥", false));
		spe.add(new Spe("手臂偏肥", false));
		spe.add(new Spe("手臂偏长", false));
		spe.add(new Spe("腰围偏大", false));
		spe.add(new Spe("臀围偏大", false));
		spe.add(new Spe("胸围大", false));
		spe.add(new Spe("大腿偏粗", false));
		spe.add(new Spe("小腿偏粗", false));
		spe.add(new Spe("有肚腩", false));

		hab = new ArrayList<String>();
		hab.add("显瘦");
		hab.add("合身");
		hab.add("宽松");
		kyw = new ArrayList<String>();
		kyw.add("偏上");
		kyw.add("偏中");
		kyw.add("偏下");
		rl_back_newdz.setOnClickListener(this);
		tv_save_newmode.setOnClickListener(this);
		tv_guidemode.setOnClickListener(this);
		lv_kyw.setOnItemClickListener(this);
		gv_habit.setOnItemClickListener(this);
		gv_special.setOnItemClickListener(this);
		sadapter = new MySpecialAdapter();
		kadapter = new MyKYEadapter();
		hadapter = new MyHabitAdapter();
		lv_kyw.setAdapter(kadapter);
		gv_habit.setAdapter(hadapter);
		gv_special.setAdapter(sadapter);
		getData();
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_newdz);
	}

	private void getData() {
		RequestParams params = new RequestParams();
		params.put("userId", MyApplication.userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyMedolCard, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				// shengao, xiuchang, sex, jingkuan, fuWei, xiongwei,
				// beichang,
				// shouBiWei, yaowei, datuichang, tuiwei, tizhong,
				// userId,
				// xiaotuichang;
				System.out.println("zuo===" + response.toString());
				shengao = response.optInt("shengao");
				xiuchang = response.optInt("xiuchang");
				sex = response.optInt("sex");
				String sexs = response.optString("sex");
				jingkuan = response.optInt("jingkuan");
				shouBiWei = response.optInt("tuiwei");
				fuWei = response.optInt("fuWei");
				tuichang = response.optInt("tuichang");
				xiongwei = response.optInt("xiongwei");
				yaowei = response.optInt("yaowei");
				beichang = response.optInt("beichang");
				datuichang = response.optInt("datuichang");
				tuiwei = response.optInt("tuiwei");
				tizhong = response.optInt("tizhong");
				xiaotuichang = response.optInt("xiaotuichang");
				String dressHabit = response.optString("dressHabit");
				String pantsLocation = response.optString("pantsLocation");
				String bodyIntroduce = response.optString("bodyIntroduce");
				ed_xiongwei.setText("" + xiongwei);
				ed_yaowei.setText("" + yaowei);
				ed_jiankuan.setText("" + jingkuan);
				ed_tizhong.setText("" + tizhong);
				ed_xiuchang.setText("" + xiuchang);
				ed_beiwei.setText("" + shouBiWei);
				ed_kuchang.setText("" + tuichang);
				ed_shengao.setText("" + shengao);
				setThres(dressHabit, pantsLocation, bodyIntroduce);
				// ed_xiongwei, ed_yaowei, ed_beiwei, ed_kuchang, ed_xiuchang,
				// ed_shengao, ed_tizhong, ed_jiankuan

			}

		});

	}

	private void upModeCard(String xiongwei, String yaowei, String beiwei, String kuchang, String xiuchang, String shengao,
			String tizhong, String jiankuan, String dressHabit, String pantsLocation, String bodyIntroduce) {

		RequestParams params = new RequestParams();
		params.put("user.id", MyApplication.userId);
		params.put("user.high", shengao);
		params.put("user.weight", tizhong);
		params.put("user.waist", yaowei);
		params.put("user.bust", xiongwei);
		params.put("user.jianKuan", jiankuan);
		params.put("user.legLength", kuchang);
		params.put("user.hip", beiwei);
		params.put("user.xiuLength", xiuchang);
		params.put("user.dressHabit", dressHabit);
		params.put("user.pantsLocation", pantsLocation);
		params.put("user.bodyIntroduce", bodyIntroduce);
		showProgressDialog("保存中...");
		System.out.println("zuoparams=" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userupdateMyMedolCard, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Intent intent = new Intent();
						setResult(102, intent);
						finish();
					} else {
						showToast("保存失败,请检查参数", 0);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	private void setThres(String dressHabit, String pantsLocation, String bodyIntroduce) {
		// TODO Auto-generated method stub
		for (int i = 0; i < hab.size(); i++) {
			if (dressHabit.equals(hab.get(i))) {
				fhab = i;
			}
		}
		for (int i = 0; i < kyw.size(); i++) {
			if (pantsLocation.equals(kyw.get(i))) {
				fkyw = i;
			}
		}
		for (int i = 0; i < spe.size(); i++) {
			if (bodyIntroduce.contains(spe.get(i).getName())) {
				spe.get(i).setChec(true);
			} else {

			}
		}

		sadapter.notifyDataSetChanged();
		hadapter.notifyDataSetChanged();
		kadapter.notifyDataSetChanged();
	}

	class MySpecialAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return spe == null ? 0 : spe.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return spe.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_NewDZ.this, R.layout.item_spe, null);
			TextView tv_spe = (TextView) v.findViewById(R.id.tv_spe);
			tv_spe.setText("" + spe.get(position).getName());
			if (spe.get(position).isChec()) {
				tv_spe.setBackgroundResource(R.drawable.round_bg_sele);
				tv_spe.setTextColor(getResources().getColor(R.color.black));
			} else {
				tv_spe.setBackgroundResource(R.drawable.round_bg);
				tv_spe.setTextColor(getResources().getColor(R.color.sblack));
			}
			return v;
		}
	}

	class MyHabitAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return hab == null ? 0 : hab.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return hab.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_NewDZ.this, R.layout.item_hab, null);
			TextView tv_hab = (TextView) v.findViewById(R.id.tv_hab);
			tv_hab.setText(hab.get(position));
			CheckBox cb_hab = (CheckBox) v.findViewById(R.id.cb_hab);
			if (fhab == position) {
				cb_hab.setChecked(true);
			} else {
				cb_hab.setChecked(false);
			}
			return v;
		}
	}

	class MyKYEadapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return kyw == null ? 0 : kyw.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return kyw.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_NewDZ.this, R.layout.item_kyw, null);
			TextView tv_hab = (TextView) v.findViewById(R.id.tv_kyw);
			tv_hab.setText(kyw.get(position));
			CheckBox cb_kyw = (CheckBox) v.findViewById(R.id.cb_kyw);
			if (fkyw == position) {
				cb_kyw.setChecked(true);
			} else {
				cb_kyw.setChecked(false);
			}
			return v;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (parent == lv_kyw) {
			if (fkyw == position) {
				fkyw = -1;
			} else {
				fkyw = position;
			}

			kadapter.notifyDataSetChanged();
		} else if (parent == gv_habit) {
			if (fhab == position) {
				fhab = -1;
			} else {
				fhab = position;
			}
			hadapter.notifyDataSetChanged();
		} else if (parent == gv_special) {
			if (spe.get(position).isChec()) {
				spe.get(position).setChec(false);
			} else {
				spe.get(position).setChec(true);
			}
			sadapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("我的模特卡");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("我的模特卡");
	}
}
