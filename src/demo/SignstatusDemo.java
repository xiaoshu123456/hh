package demo;

import java.util.HashMap;
import java.util.Map;

import api.UndefinedServiceResult;
import utils.ApiHttpUtils;
import utils.JsonUtils;

public class SignstatusDemo {
	/**
	 * 本函数为示例的对接代码，以便开发人员更快的熟悉API接口signstatus的使用。
	 * 
	 * 仅供参考，请开发人员根据业务需要进行修改调整。
	 * @param args
	 */
	public static void main(String[] args) {
		// 查询合同签署状态接口的地址
		String api_signstatus_url = Constants.API_SERVER_URI + "/signstatus.shtml";
		try {
			String account = Constants.ENTERPRISE_ACCOUNT;
			boolean sigerSignSuc_0 = false;

			/** 调用查询签署状态接口signstatus来检查第1个签署人是否签署成功 */
			String appId = Constants.MY_APP_ID;
			String templateId = Constants.MY_TEMPLATE_ID;
			String orderId = Constants.MY_ORDER_ID;
			String version = Constants.MY_API_VERSION;
			String appSecret = Constants.MY_APP_SECRET;
			String timestamp = String.valueOf(System.currentTimeMillis());

			/**
			 * 使用appSecret为参数列表生成签权签名
			 * 注：生成签名里的timestamp一定要和API接口中传入的timestamp参数的值是相同的。
			 */
			String signature = ApiHttpUtils.getMySignature(appId, templateId, orderId, timestamp, version, appSecret);
			// 设置orderInfo参数，该参数为json字符串
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

			Map<String, String> parasMap = new HashMap<String, String>();
			parasMap.put("orderInfoJson", orderInfoJson);

			String returnJson = ApiHttpUtils.sendPost(api_signstatus_url, parasMap);
			System.out.println("signstatus接口返回：returnJson=" + returnJson);
			Object wrapObj = ApiHttpUtils.getResultObjFromAPIReturnJson(returnJson);
			if (wrapObj == null) {
				System.out.println("signstatus接口返回结果中没有obj");
				return;
			}
			// 返回的resultCode是成功,解析里面的签署状态。
			String signStatusJson = wrapObj.toString();
			UndefinedServiceResult getStatusRes = ApiHttpUtils.analyseSignStatusJson(signStatusJson, appId, orderId,
					version, account);
			if (getStatusRes.getIsSuccess()) {
				System.out.println("****签署人" + account + "签署订单" + Constants.MY_ORDER_ID + "成功");

			} else {
				System.out.println("****签署人" + account + "签署订单" + Constants.MY_ORDER_ID + "失败！原因：" + getStatusRes.getResultMsg());
				return;
			}

		} catch (Exception e) {
			System.out.println("出现异常，详情：" + e);
		}
	}

}
