package com.femto.ugershop.activity;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import com.alipay.sdk.app.PayTask;
import com.femto.ugershop.R;
import com.femto.ugershop.alipay.PayResult;
import com.femto.ugershop.alipay.SignUtils;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.weixinpay.Constants;
import com.femto.ugershop.weixinpay.MD5;
import com.femto.ugershop.weixinpay.Util;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_SelectPay extends BaseActivity {
	private RelativeLayout rl_back_sp, rl_zhifubao, rl_weixin;
	private TextView tv_surepay;
	private ImageView im_seleweixin, im_selezhifubao;
	private int flag = 0;// 1为支付宝，2为微信支付
	/* ----------------weixinpay---------------------- */
	Map<String, String> resultunifiedorder;
	StringBuffer sb;
	PayReq req;
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	/* ----------------weixinpay---------------------- */
	/* ----------------alipay---------------------- */

	// 商户PID
	public static final String PARTNER = "2088021003617332";
	// 商户收款账号
	public static final String SELLER = "yoogeee@qq.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALmVgBVjfREjy52EaOBg/kg9nsmwZgwfFRVQoNSlfQOz5lJ3ZTs1DJw+x+zYDk34PqiccQ7sBm/eVuX+t+LdYhJH5O/i0Fme4OkSGtfuQIhSkRroB30YepBoQb7zYQzPrJjifrTznuZqZJcEUAEXa8e16WNOY8g/vHZ9UP6dfDiZAgMBAAECgYAB64AFuJ/Em6AxovsKxzYaPTj1UTPdED1BmTHwwT2H2kaZt85+TFAzFZsv4hRTziunxuaMhviSE7TGcFoqqKupDDqcppe3vG9z7LtoKkjUxDrCKNhKTnnRu5uLk96YT4CKI0RUFSBqX5GgfZwfXL6m7GgXpBdRfnOukecfaeAy4QJBAO1hk1oiVJabUXu2jee0JRtI0xXgcbtrR/RZuPm4Of2+jCK7lXsobW021dC011SH+b5Y9+fpGjyxdr1GKaepVm0CQQDII+D81etMiu5cfyLfqK/Xjl0df7gBozdM6RYN+9LIjAPCm1hQQEoswP0lyBsV88nKoEKNYqvVSgv9U5Xnnz9dAkAYrWUhIKKEiZ796mt0I/EblgWDp7KLFksDpvQo2bMFovJ0heWzSO+fQ+0UZkhgXEZlOGCFNmFo39YFNS8NY/g9AkBDEMrOzrxHbPLM6gl8myE5gUownWDCo7ffPOgYDX+nlkuYRcgFNFXmw1DQ/UoeRBAlpyhGCn98PJDh9cVw1Us9AkEArm6Lh/S7aZBoXuUklYfKvb8aUH5omhdSp64W/fyhIgTymupOG+/cM17dUNAnOUz+Px6Ei+yPSmMFqNRd751Zjw==";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTUgwbDdGYONH30QvGe0L+SwtmCxgZodkJv8fd etSSc0vTotAalueMQyv00PEFh1BV/eLi6j3t71Nv0QL1jxzZSEbdzQYR1DTfXW1OLine4GdQ4YIh W1cWWUwIFWLWHNrNI9SxGEXO1AiHpzN0LVJbznokAgqs/HE6AZze5wtD5QIDAQAB";
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	/* ----------------alipay---------------------- */
	/* ----------------alipay---------------------- */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(Activity_SelectPay.this, "支付成功", Toast.LENGTH_SHORT).show();
					finish();
					MyApplication.exitpayact();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(Activity_SelectPay.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(Activity_SelectPay.this, "支付失败", Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(Activity_SelectPay.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};
	private String orderCode;
	private String price;
	private String productName;

	/* ----------------alipay---------------------- */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("支付");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("支付");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_sp:
			finish();
			break;
		case R.id.rl_zhifubao:
			flag = 1;
			im_selezhifubao.setImageResource(R.drawable.btn_select_green_small2);
			im_seleweixin.setImageResource(R.drawable.btn_select_green_small);
			break;
		case R.id.rl_weixin:
			flag = 2;
			im_selezhifubao.setImageResource(R.drawable.btn_select_green_small);
			im_seleweixin.setImageResource(R.drawable.btn_select_green_small2);
			break;
		case R.id.tv_surepay:
			if (flag == 1) {
				aliPay();
			} else if (flag == 2) {
				weixinPay();
			} else {
				Toast.makeText(Activity_SelectPay.this, "请选择支付方式", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		MyApplication.addpaycct(this);
		sb = new StringBuffer();
		req = new PayReq();
		// TODO Auto-generated method stub
		rl_back_sp = (RelativeLayout) findViewById(R.id.rl_back_sp);
		// rl_zhifubao, rl_weixin
		rl_zhifubao = (RelativeLayout) findViewById(R.id.rl_zhifubao);
		rl_weixin = (RelativeLayout) findViewById(R.id.rl_weixin);
		tv_surepay = (TextView) findViewById(R.id.tv_surepay);
		// im_seleweixin, im_selezhifubao
		im_seleweixin = (ImageView) findViewById(R.id.im_seleweixin);
		im_selezhifubao = (ImageView) findViewById(R.id.im_selezhifubao);
	}

	@Override
	public void initUtils() {

	}

	@Override
	public void Control() {
		rl_back_sp.setOnClickListener(this);
		rl_weixin.setOnClickListener(this);
		rl_zhifubao.setOnClickListener(this);
		tv_surepay.setOnClickListener(this);

	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_selectpay);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		// TODO Auto-generated method stub
		orderCode = getIntent().getStringExtra("orderCode");
		price = getIntent().getStringExtra("price");
		productName = getIntent().getStringExtra("productname");
		System.out.println("zuo手法哦的=" + productName + "  " + price);
		if (!(Double.parseDouble(price) > 0)) {
			showToast("价格不能为0！", 0);
			finish();
			return;
		}
	}

	/* ----------------alipay---------------------- */
	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void aliPay() {
		// 订单
		String orderInfo = getOrderInfo("" + productName, "" + productName, "" + price);
		System.out.println("zuo==" + productName + "   " + productName);
		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
		System.out.println("zuo=ali=" + payInfo);
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(Activity_SelectPay.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(Activity_SelectPay.this);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + AppFinalUrl.usersoonPay + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		// SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
		// Locale.getDefault());
		// Date date = new Date();
		// String key = format.format(date);
		//
		// Random r = new Random();
		// key = key + r.nextInt();
		// key = key.substring(0, 15);

		return orderCode;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	/* ----------------alipay---------------------- */
	/* ----------------weixinpay---------------------- */
	private void weixinPay() {
		// 生成预支付订单
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute();
	}

	/**
	 * 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion", packageSign);
		return packageSign;
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion", appSign);
		return appSign;
	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("orion", sb.toString());
		return sb.toString();
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(Activity_SelectPay.this, "提示", "正在获取预支付订单...");
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
			// show.setText(sb.toString());
			resultunifiedorder = result;
			genPayReq();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {

			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();
			byte[] buf = Util.httpPost(url, entity);
			String content = new String(buf);
			Map<String, String> xml = decodeXml(content);
			return xml;
		}
	}

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			System.out.println("zuo==fail=-" + e.getMessage());
		}
		return null;

	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String genOutTradNo() {
		Random random = new Random();
		// return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
		// .getBytes());
		return orderCode;
	}

	//
	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();
			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
			packageParams.add(new BasicNameValuePair("body", "weixin"));
			packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", AppFinalUrl.usersoonPayWeiXin));
			packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
			// packageParams.add(new BasicNameValuePair("total_fee", "" + ((int)
			// Double.parseDouble(price)) * 100));
			packageParams.add(new BasicNameValuePair("total_fee", "" + ((int) Double.parseDouble(price)) * 100));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));
			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);

			return xmlstring;

		} catch (Exception e) {
			// Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			System.out.println("zuo==fail=-" + e.getMessage());
			return null;
		}

	}

	private void genPayReq() {

		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n" + req.sign + "\n\n");

		// show.setText(sb.toString());
		System.out.println("zuo===genPayReq()=" + sb.toString());
		sendPayReq();
		Log.e("orion", signParams.toString());
		System.out.println("zuo==signParams.toString()=-" + signParams.toString());

	}

	private void sendPayReq() {

		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}

	/* ----------------weixinpay---------------------- */

}
