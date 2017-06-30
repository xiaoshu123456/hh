package demo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import api.UndefinedServiceResult;
import utils.ApiHttpUtils;
import utils.JsonUtils;

/**
 * 本demo代码是一个简单的示例，以便开发人员更快的熟悉的API接口startsign的使用。
 * 该接口的功能是：根据模板为该业务号生成唯一的合同。
 * 仅供参考，请开发人员根据业务需要及编程风格进行修改并增加错误处理。
 * @author panyan
 *
 */
public class StartsignDemo {

	/**
	 * 本函数为示例的对接代码，以便开发人员更快的熟悉API接口startsign的使用。仅供参考，请开发人员根据业务需要进行摘取和修改。
	 * 
	 * @param args
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static void main(String[] args) throws IOException, GeneralSecurityException {

		String api_startsign_url = Constants.API_SERVER_URI +"/api/startsign.shtml";

		try {
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
			// 设置合同的标题，标题尽量设置得有意义，以便标识并方便检索
			map.put("title", "template-" + Constants.MY_TEMPLATE_ID + "-" + System.currentTimeMillis());
			map.put("signature", signature);

			// ******设置第1个签署人的信息
			List<JSONObject> signatories = new ArrayList<JSONObject>();
			JSONObject party0 = new JSONObject();
			// 设置签署人在模板中的编号
			party0.put("sigerIndex", 0);
			// 设置该签署人的账号。
			party0.put("account", Constants.ENTERPRISE_ACCOUNT);
			// 若account为邮箱，需要设置签署人的手机号码。备注：不需要签字的审阅人可以不设手机号码。
			party0.put("phone", "13021185244");

//			// ******设置第2个签署人的信息
//			JSONObject party1 = new JSONObject();
//			party1.put("sigerIndex", 1);
//			// 设置第二个签署人的account
//			party1.put("account", Constants.USER_ACCOUNT);
//			// 若account为邮箱，需要设置签署人的手机号码。备注：不需要签字的审阅人可以不设手机号码。
//			party1.put("phone", "13717656980");

			// 将这两个签署人的信息加入signatories链表中
			signatories.add(party0);
//			signatories.add(party1);
			map.put("signatories", signatories);

			/** 调用发起签署接口创建合同 */
			Map<String, String> parasMap = new HashMap<String, String>();
			String signCommonJson = JsonUtils.toJsonString(map);
			parasMap.put("signCommonJson", signCommonJson);
			System.out.println("startsign接口输入：" + signCommonJson);
			
			String returnJson = ApiHttpUtils.sendPost(api_startsign_url, parasMap);
			System.out.println("startsign接口返回：returnJson=" + returnJson);
			System.out.println("-----------------------------------------");
			
			UndefinedServiceResult startRes = ApiHttpUtils.getServiceResultFromAPIResultJson(returnJson);
			System.out.println("*****************************"+startRes+"*************************************");
			if (startRes.getIsSuccess()) {
				System.out.println("startsign接口返回成功！");
			} else {
				System.out.println("startsign接口返回失败！失败原因为：" + startRes.getResultMsg());
			}
		} catch (Exception e) {
			System.out.println("出现异常，详情：" + e);
		}
	}

}
