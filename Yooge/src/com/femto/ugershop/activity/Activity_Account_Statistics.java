package com.femto.ugershop.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Account_Statistics extends SwipeBackActivity {
	private RelativeLayout rl_back_account;
	private LineChartView mLineChart, mLineChart1;
	Paint mLineGridPaint;
	String[] lineLabels = { "●", "06-01", "06-02", "06-03", "06-04", "06-05", "06-06", "06-07" };
	float[] lineValues = { 0f, 6f, 10f, 20f, 38f, 8f, 4f, 4f };
	String[] lineLabels1 = { "●", "06-01", "06-02", "06-03", "06-04", "06-05", "06-06", "06-07" };
	float[] lineValues1 = { 0f, 400f, 50f, 100f, 400f, 200f, 300f, 100f };
	private final static int LINE_MAX = 2000;
	private final static int LINE_MIN = 0;
	private final static int LINE_MAX1 = 20;
	private final static int LINE_MIN1 = 0;
	private int isShowPoint[] = { 0, 1, 2, 3, 4, 5, 6 };
	private List<SaleCountList> saleCountList;
	private List<Moneylist> moneylist;
	private int myId;
	private DisplayImageOptions options;
	private int count = 1;
	private TextView tv_lastweek, tv_nextweek;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_account:
			finish();
			break;
		case R.id.tv_lastweek:
			getData(++count);
			break;
		case R.id.tv_nextweek:
			if (count == 1) {
				Toast.makeText(this, "没有上一周", Toast.LENGTH_SHORT).show();
			} else {
				getData(--count);
			}

			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		MyApplication.addActivity(this);
		saleCountList = new ArrayList<SaleCountList>();
		moneylist = new ArrayList<Moneylist>();
		rl_back_account = (RelativeLayout) findViewById(R.id.rl_back_account);
		tv_lastweek = (TextView) findViewById(R.id.tv_lastweek);
		tv_nextweek = (TextView) findViewById(R.id.tv_nextweek);
		mLineChart = (LineChartView) findViewById(R.id.linechart);
		mLineChart1 = (LineChartView) findViewById(R.id.linechart1);
		getData(count);

	}

	private void getData(int count) {
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("count", count);
		params.put("token", MyApplication.token);
		showProgressDialog("查询中...");
		MyApplication.ahc.post(AppFinalUrl.usergetMoneyTable, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo" + response.toString());
				dismissProgressDialog();
				moneylist.clear();
				saleCountList.clear();
				try {
					JSONArray jsonArray = response.getJSONArray("saleCountList");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String day = j.optString("day");
						int saleCount = j.optInt("saleCount");
						saleCountList.add(new SaleCountList(day, saleCount));
					}
					JSONArray jsonArray1 = response.getJSONArray("moneylist");
					for (int i = 0; i < jsonArray1.length(); i++) {
						JSONObject j = jsonArray1.getJSONObject(i);
						String day = j.optString("day");
						int moneyGet = j.optInt("moneyGet");
						moneylist.add(new Moneylist(moneyGet, day));
					}
					String[] ld = new String[saleCountList.size()];
					float[] lc = new float[saleCountList.size()];
					int maxcount = 10;
					for (int i = 0; i < saleCountList.size(); i++) {
						ld[i] = saleCountList.get(i).day;
						lc[i] = saleCountList.get(i).saleCount;
						if (maxcount < saleCountList.get(i).saleCount) {
							maxcount = saleCountList.get(i).saleCount;
						}
					}
					String[] ld1 = new String[moneylist.size()];
					float[] lc1 = new float[moneylist.size()];
					int maxmoney = 2000;
					for (int i = 0; i < moneylist.size(); i++) {
						ld1[i] = moneylist.get(i).day;
						lc1[i] = moneylist.get(i).moneyGet;
						if (maxmoney < moneylist.get(i).moneyGet) {
							maxmoney = moneylist.get(i).moneyGet;
						}
					}
					updateLineChart(mLineChart, ld1, lc1, 0, maxmoney);
					updateLineChart1(mLineChart1, ld, lc, 0, maxcount);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class SaleCountList {
		String day;
		int saleCount;

		public SaleCountList(String day, int saleCount) {
			super();
			this.day = day;
			this.saleCount = saleCount;
		}
	}

	class Moneylist {
		int moneyGet;
		String day;

		public Moneylist(int moneyGet, String day) {
			super();
			this.moneyGet = moneyGet;
			this.day = day;
		}
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_account.setOnClickListener(this);
		tv_lastweek.setOnClickListener(this);
		tv_nextweek.setOnClickListener(this);
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
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
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_accountstatistics);
		initParams();
		mLineGridPaint = new Paint();
		mLineGridPaint.setColor(getResources().getColor(R.color.black_deep));// 设置背景线条的颜色
		mLineGridPaint.setPathEffect(new DashPathEffect(new float[] { 32, 32 }, 10));// 设置背景虚线的宽和间距的宽
		mLineGridPaint.setStyle(Paint.Style.STROKE);
		mLineGridPaint.setAntiAlias(true);// 设置背景线条是否颜色不渐变(true为不渐变，false为渐变)
		mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(0.5f));// 设置背景线条的宽度
	}

	private void updateLineChart(LineChartView mLineChart, String[] ll, float[] lc, int LINE_MIN, int LINE_MAX) {
		mLineChart.reset();
		LineSet dataSet = new LineSet();
		dataSet.addPoints(ll, lc);
		dataSet.setDotsStrokeColor(getResources().getColor(R.color.green));// 圆点外空心圆的周边线条颜色
		dataSet.setDotsStrokeThickness(Tools.fromDpToPx(2));// 圆点外空心圆的周边线条粗细
		dataSet.setDotsColor(getResources().getColor(R.color.white));// 圆点的颜色
		dataSet.setDots(true);// 是否要圆点
		dataSet.setDotsRadius(Tools.fromDpToPx(5));// 原点的半径
		dataSet.setLineColor(getResources().getColor(R.color.sblack));// 线条的颜色
		dataSet.setLineThickness(Tools.fromDpToPx(3));// 线条的粗细
		dataSet.setSmooth(false);// 是否弯曲弧度
		// dataSet.beginAt(1);// 在第二个开始画
		// dataSet.endAt(lineLabels.length - 1);// 在倒数第二点的截止
		// dataSet.setDashed(true);// 虚线。且会动
		// dataSet.setLineSmooth(true);//是否弯曲线条
		// dataSet.setLineDashed(true);// 虚线。且会动
		dataSet.setFill(getResources().getColor(R.color.sgray));
		dataSet.setFill(true);// 是否显示下面阴影
		dataSet.setIsShowPoint(isShowPoint);
		mLineChart.addData(dataSet);
		mLineChart.setBorderSpacing(Tools.fromDpToPx(10));// 线图与Y轴距离
		mLineChart.setFontSize(20);// 设置X轴与Y轴字体大小
		// mLineChart.setLabelColor(getResources().getColor(R.color.red_c03_98));//
		// 设置X轴与Y轴字体颜色
		mLineChart.setLabelsFormat(new DecimalFormat("####"));// 设置标签显示格式
		mLineChart.setXAxis(false);// 是否显示X轴直线
		mLineChart.setYAxis(false);// 是否显示Y轴直线
		mLineChart.setGrid(LineChartView.GridType.HORIZONTAL, mLineGridPaint);// 设置背景线条的走向
		mLineChart.setXLabels(XController.LabelPosition.OUTSIDE);// 设置X轴字体的位置
		mLineChart.setYLabels(YController.LabelPosition.OUTSIDE);// 设置Y轴字体的位置
		mLineChart.setAxisBorderValues(LINE_MIN, (LINE_MAX / 10) * 10 + 10, ((LINE_MAX / 10) * 10 + 10) / 10);// 设置Y轴的最小值与最大值，以及每个值与值之间的差距
		mLineChart.show();// getAnimation()
		// mLineChart.animateSet(0, new
		// DashAnimation());// 设置线条动态变化

	}

	private void updateLineChart1(LineChartView mLineChart, String[] ll, float[] lc, int LINE_MIN1, int LINE_MAX1) {
		mLineChart.reset();
		LineSet dataSet = new LineSet();
		dataSet.addPoints(ll, lc);
		dataSet.setDotsStrokeColor(getResources().getColor(R.color.green));// 圆点外空心圆的周边线条颜色
		dataSet.setDotsStrokeThickness(Tools.fromDpToPx(2));// 圆点外空心圆的周边线条粗细
		dataSet.setDotsColor(getResources().getColor(R.color.white));// 圆点的颜色
		dataSet.setDots(true);// 是否要圆点
		dataSet.setDotsRadius(Tools.fromDpToPx(5));// 原点的半径
		dataSet.setLineColor(getResources().getColor(R.color.sblack));// 线条的颜色
		dataSet.setLineThickness(Tools.fromDpToPx(3));// 线条的粗细
		dataSet.setSmooth(false);// 是否弯曲弧度
		// dataSet.beginAt(1);// 在第二个开始画
		// dataSet.endAt(lineLabels.length - 1);// 在倒数第二点的截止
		// dataSet.setDashed(true);// 虚线。且会动
		// dataSet.setLineSmooth(true);//是否弯曲线条
		// dataSet.setLineDashed(true);// 虚线。且会动
		dataSet.setFill(getResources().getColor(R.color.sgray));
		dataSet.setFill(true);// 是否显示下面阴影
		dataSet.setIsShowPoint(isShowPoint);
		mLineChart.addData(dataSet);
		mLineChart.setBorderSpacing(Tools.fromDpToPx(10));// 线图与Y轴距离
		mLineChart.setFontSize(20);// 设置X轴与Y轴字体大小
		// mLineChart.setLabelColor(getResources().getColor(R.color.red_c03_98));//
		// 设置X轴与Y轴字体颜色
		mLineChart.setLabelsFormat(new DecimalFormat("####"));// 设置标签显示格式
		mLineChart.setXAxis(false);// 是否显示X轴直线
		mLineChart.setYAxis(false);// 是否显示Y轴直线
		mLineChart.setGrid(LineChartView.GridType.HORIZONTAL, mLineGridPaint);// 设置背景线条的走向
		mLineChart.setXLabels(XController.LabelPosition.OUTSIDE);// 设置X轴字体的位置
		mLineChart.setYLabels(YController.LabelPosition.OUTSIDE);// 设置Y轴字体的位置
		mLineChart.setAxisBorderValues(LINE_MIN1, (LINE_MAX1 / 10) * 10 + 10, ((LINE_MAX1 / 10) * 10 + 10) / 10);// 设置Y轴的最小值与最大值，以及每个值与值之间的差距
		mLineChart.show();// getAnimation()
		// mLineChart.animateSet(0, new
		// DashAnimation());// 设置线条动态变化
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("账表统计");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("账表统计");
	}
}
