package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.UndefinedServiceResult;
import utils.ApiHttpUtils;
import utils.ApiUtils;

/**
 * 本demo代码是一个简单的示例，以便开发人员更快的熟悉的API接口getsignedorders的使用。
 * 该接口的功能是：查询对接平台的签署成功的合同中属于指定account的合同列表。
 * 列表内容主要包括：
 * "completeTime":"2017-03-01 15:26:09:009", //合同所有签署人签署完成的时间 
 * "orderId":"order_id_example_59",//合同对应订单号 
 * "templateId": "56341484722985",//合同使用的模板编号
 * "templateName": "电子商务商品购销合同",//模板名称 
 * "title": "电子商务商品购销合同"//合同的标题
 *  
 * 列表内容按照completeTime从新到旧排序。
 * 
 * 注意：在后续版本中，本接口返回列表有可能会包括更多的内容，客户端解析时请注意版本兼容。
 *
 * 仅供参考，请开发人员根据业务需要及编程风格进行修改调整。
 */
public class GetsignedordersDemo {

	public static void main(String[] args) {
		// 验签接口的url
		String api_getsignedorders_url = Constants.API_SERVER_URI + "/getsignedorders.shtml";

		try {
			String timeStamp = String.valueOf(System.currentTimeMillis());

			/**
			 * 使用appSecret为参数列表生成签权签名
			 * 注：生成签名里的timestamp一定要和API接口中传入的timestamp参数的值是相同的。
			 */
			List<String> lists = new ArrayList<String>();
			lists.add("appId=" + Constants.MY_APP_ID);
			lists.add("account=" + Constants.ENTERPRISE_ACCOUNT);
			lists.add("timestamp=" + timeStamp);
			lists.add("version=" + Constants.MY_API_VERSION);
			String signature = ApiUtils.getSignature(lists, Constants.MY_APP_SECRET);
			System.out.println("signature = " + signature);

			// 调用查看合同接口
			Map<String, String> parasMap = new HashMap<String, String>();
			// 设置appId
			parasMap.put("appId", Constants.MY_APP_ID);
			// 设置要查询的签署人账号
			parasMap.put("account", Constants.ENTERPRISE_ACCOUNT);
			// 设置version
			parasMap.put("version", Constants.MY_API_VERSION);
			// 设置时间戳
			parasMap.put("timestamp", timeStamp);
			// 设置鉴权签名
			parasMap.put("signature", signature);

			String returnJson = ApiHttpUtils.sendPost(api_getsignedorders_url, parasMap);
			System.out.println("getsignedorders接口返回：returnJson=" + returnJson);
			UndefinedServiceResult callRes = ApiHttpUtils.getServiceResultFromAPIResultJson(returnJson);

			if (!callRes.getIsSuccess()) {
				System.out.println("调用getsignedorders接口失败，原因为：" + callRes.getResultMsg());
			} else {
				System.out.println("已经签署成功的合同列表为：" + callRes.getObj());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 注意：在后续版本中，本接口返回列表有可能会包括更多的内容，客户端解析时请注意版本兼容。
	}
}
