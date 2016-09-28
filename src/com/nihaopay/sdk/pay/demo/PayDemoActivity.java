package com.nihaopay.sdk.pay.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.nihaopay.sdk.pay.NihaopayResult;
import com.nihaopay.sdk.pay.NihaopayTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

public class PayDemoActivity extends FragmentActivity {

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				NihaopayResult payResult = new NihaopayResult((String) msg.obj);

				String resultStatus = payResult.getStatus();

				// 判断resultStatus 为“success”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "success")) {
					Toast.makeText(PayDemoActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
				} else {
					// 判断resultStatus 为非“success”则代表可能支付失败
					// “pending”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "pending")) {
						Toast.makeText(PayDemoActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(PayDemoActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(PayDemoActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(View v) {
		if (TextUtils.isEmpty(Config.TOKEN)) {
			new AlertDialog.Builder(this)
					.setTitle("警告")
					.setMessage("需要配置TOKEN")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									//
									finish();
								}
							}).show();
			return;
		}
		
		final String payInfo = getPayInfo("http://www.baiu.com", "1", "USD");
		
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				
//				final String payInfo = getPayInfo("http://www.baiu.com", "1", "USD", "708325cc65f15d8d5d28297b47eb8c0dd47889edd145d51de881fdfb5c5c0e1c");
				
				System.out.println("send info : "+ payInfo);
				// 构造PayTask 对象
				NihaopayTask alipay = new NihaopayTask(PayDemoActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, Config.TOKEN);

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

//	/**
//	 * check whether the device has authentication alipay account.
//	 * 查询终端设备是否存在支付宝认证账户
//	 * 
//	 */
//	public void check(View v) {
//		Runnable checkRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				// 构造PayTask 对象
//				PayTask payTask = new PayTask(PayDemoActivity.this);
//				// 调用查询接口，获取查询结果
//				boolean isExist = payTask.checkAccountIfExist();
//
//				Message msg = new Message();
//				msg.what = SDK_CHECK_FLAG;
//				msg.obj = isExist;
//				mHandler.sendMessage(msg);
//			}
//		};
//
//		Thread checkThread = new Thread(checkRunnable);
//		checkThread.start();
//
//	}

//	/**
//	 * get the sdk version. 获取SDK版本号
//	 * 
//	 */
//	public void getSDKVersion() {
//		PayTask payTask = new PayTask(this);
//		String version = payTask.getVersion();
//		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
//	}
	
	public String getPayInfo(String ipn_url,  String amount, String currency) {
		
		String orderInfo = "amount="  + amount ;

		orderInfo += "&currency=" +  currency ;

		// 商户网站唯一订单号
		orderInfo += "&reference=" +  getOutTradeNo() ;

		// 后台通知url
		orderInfo += "&ipn_url=" +  ipn_url ;
		
		String note = "it is test";
		orderInfo += "&note=" +  note ;
		String description = "it is a book";
		orderInfo += "&description=" +  description ;
		
		
		
		return orderInfo;
	}



	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public static String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

}
