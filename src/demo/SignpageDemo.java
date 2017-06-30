package demo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import api.UndefinedServiceResult;
import utils.ApiHttpUtils;
import utils.JsonUtils;

public class SignpageDemo {

	/**
	 * 本demo代码是一个简单的示例，以便开发人员更快的熟悉的API接口signpage的使用。
	 * 该接口将返回一个URL，将打开1号签的签署合同页面，页面中显示的内容为指定订单号对应的合同，终端用户可以在页面上进行签署操作。
	 * 对接平台调用signpage接口，并将自己的页面redirect到该接口返回的url。
	 * 仅供参考，请开发人员根据业务需要及编程风格进行修改并增加错误处理。
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// 页面签署接口的URL
		String api_signpage_url = Constants.API_SERVER_URI + "/signpage.shtml";
		try {

			/** 为第2个签署人调用页面签署。签署控件将显示在页面上，并有游标引领用户填入所有内容，所以在调用接口时不必提供任何控件信息。 */
			// 时间戳
			String timeStamp = String.valueOf(System.currentTimeMillis());
			/**
			 * 使用appSecret为参数列表生成签权签名
			 * 注：生成签名里的timestamp一定要和API接口中传入的timestamp参数的值是相同的。
			 */
			String signature = ApiHttpUtils.getMySignature(Constants.MY_APP_ID, Constants.MY_TEMPLATE_ID,
					Constants.MY_ORDER_ID, timeStamp, Constants.MY_API_VERSION, Constants.MY_APP_SECRET);
			Map<String, Object> map = new HashMap<String, Object>();
			// 设置appId
			map.put("appId", Constants.MY_APP_ID);
			// 设置要使用的模板，该模板
			map.put("templateId", Constants.MY_TEMPLATE_ID);
			// 设置本次要签约的业务编号
			map.put("orderId", Constants.MY_ORDER_ID);
			// 设置API接口的版本
			map.put("version", Constants.MY_API_VERSION);
			// 设置时间戳
			map.put("timestamp", timeStamp);
			// 设置签权签名
			map.put("signature", signature);

			/**
			 * 对于页面签署的签署人，在调用接口时如果设置并实现了通知接口redirectUrl和notifyUrl，
			 * 则签署成功后，会通过两种接口通知客户端签署状态。
			 */
			// 设置签署结果的页面跳转通知接口,不设置的话，则不会发送通知
			map.put("redirectUrl", "http://www.baidu.com");
			// 设置签署结果的后台通知接口，不设置的话，则不会发送通知
			// map.put("notifyUrl", "xxxxxxxxxx");

			JSONObject siger = new JSONObject();
			siger.put("account", Constants.USER_ACCOUNT);

			// ***设置本次签署要使用的CA证书信息
			// 使用个人临时证书
			siger.put("caType", 4);
			siger.put("name", "王二小");
			siger.put("idCardType", "0");
			siger.put("idCardNo", "211102195004230518");
			// 不需要1号签为签署人发送验证码
			siger.put("needSendSMS", 0);
			// 设置对接平台自己给签署人发送并验证过的手机验证码
			siger.put("smsVerifCode", "678900");
			// 设置该签署人补充信息
			siger.put("addition", "设备信息：ipone");
			map.put("siger", siger);

			/** 调用页面签署的接口signpage */
			// 备注：signpage接口将返回一个1号签合同查看页面的有效链接URL。
			Map<String, String> parasMap = new HashMap<String, String>();
			String pageSignCommonJson = JsonUtils.toJsonString(map);
			System.out.println("调用signpage接口输入参数为： " + pageSignCommonJson);
			parasMap.put("pageSignCommonJson", pageSignCommonJson);

			String returnJson = ApiHttpUtils.sendPost(api_signpage_url, parasMap);
			System.out.println("signpage接口返回：returnJson=" + returnJson);
			UndefinedServiceResult signpageRes = ApiHttpUtils.getServiceResultFromAPIResultJson(returnJson);

			if (!signpageRes.getIsSuccess()) {
				System.out.println("为签署人" + Constants.USER_ACCOUNT + "调用页面签署接口失败，原因为：" + signpageRes.getResultMsg());
			}
			String signpageURL = (String) signpageRes.getObj();
			System.out.println("****为签署人" + Constants.USER_ACCOUNT + "签署订单" + Constants.MY_ORDER_ID + "的合同的页面URL为："
					+ signpageURL);
			System.out.println("****将该URL粘贴到浏览器地址栏可以在页面签署合同,而对接系统需要将页面redirect至该URL。");

		} catch (Exception e) {
			System.out.println("出现异常，详情：" + e);
		}
	}

}
