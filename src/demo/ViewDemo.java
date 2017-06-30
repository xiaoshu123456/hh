package demo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import api.UndefinedServiceResult;
import utils.ApiHttpUtils;
import utils.JsonUtils;

public class ViewDemo {

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		/** 查看合同API接口view的URL */
		String api_view_url = Constants.API_SERVER_URI + "/view.shtml";

		try {
			String timestamp = String.valueOf(System.currentTimeMillis());

			/**
			 * 使用appSecret为参数列表生成签权签名
			 * 注：生成签名里的timestamp一定要和API接口中传入的timestamp参数的值是相同的。
			 */
			String signature = ApiHttpUtils.getMySignature(Constants.MY_APP_ID, Constants.MY_TEMPLATE_ID,
					Constants.MY_ORDER_ID, timestamp, Constants.MY_API_VERSION, Constants.MY_APP_SECRET);

			Map<String, String> map = new HashMap<String, String>();
			// 设置appId
			map.put("appId", Constants.MY_APP_ID);
			// 设置要使用的模板，该模板
			map.put("templateId", Constants.MY_TEMPLATE_ID);
			// 设置本次要签约的业务编号
			map.put("orderId", Constants.MY_ORDER_ID);
			// 设置API接口的版本
			map.put("version", Constants.MY_API_VERSION);
			// 设置时间戳
			map.put("timestamp", timestamp);
			// 设置签权签名
			map.put("signature", signature);
			String orderInfoJson = JsonUtils.toJsonString(map);
			// 备注：view接口将返回一个1号签合同查看页面的有效链接URL。开发人员需要在自己的程序中使用该URL，将系统页面跳转至1号签合同查看页面
			try {
				// 调用查看合同接口
				Map<String, String> parasMap = new HashMap<String, String>();
				parasMap.put("orderInfoJson", orderInfoJson);
				parasMap.put("account", Constants.ENTERPRISE_ACCOUNT);
				System.out
						.println("view接口输入：orderInfoJson=" + orderInfoJson + ", account=" + Constants.ENTERPRISE_ACCOUNT);
				String returnJson = ApiHttpUtils.sendPost(api_view_url, parasMap);
				System.out.println("view接口返回：returnJson=" + returnJson);
				UndefinedServiceResult viewRes = ApiHttpUtils.getServiceResultFromAPIResultJson(returnJson);

				String viewSignURL = (String) viewRes.getObj();
				System.out.println("****签署人" + Constants.ENTERPRISE_ACCOUNT + "查看该合同的页面有效链接URL为：" + viewSignURL);
				System.out.println("****将该URL粘贴到浏览器地址栏可查看合同,而对接系统需要将页面redirect至该URL");

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("API接口view出现异常，详情：" + e);
			}

		} catch (Exception e) {
			System.out.println("出现异常，详情：" + e);
		}
	}



}
