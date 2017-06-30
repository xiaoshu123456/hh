package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.UndefinedServiceResult;
import utils.ApiHttpUtils;
import utils.ApiUtils;

/**
 * 本demo代码是一个简单的示例，以便开发人员更快的熟悉的API接口exporttemplates的使用。
 * 该接口的功能是：导出appId所有的模板信息列表。列表中的每个模板信息如下例： 
 * { "createTime": "2017-01-06 11:52:49:049"//创建时间 
 * 	 "name": "测试",//模板名称
 *   "templateId": "50951483674769"//模板编号 
 *  } 
 * 注意：在后续版本中，本接口返回列表有可能会包括更多的内容，客户端解析时请注意版本兼容。
 * 仅供参考，请开发人员根据业务需要及编程风格进行修改调整。
 */
public class ExporttemplatesDemo {

	public static void main(String[] args) {
		// exporttemplates接口的url
		String API_EXPORT_TEMPLATES_URI = Constants.API_SERVER_URI + "/exporttemplates.shtml";
		try {
			String timeStamp = String.valueOf(System.currentTimeMillis());

			/**
			 * 使用appSecret为参数列表生成签权签名
			 * 注：生成签名里的timestamp一定要和API接口中传入的timestamp参数的值是相同的。
			 */
			List<String> lists = new ArrayList<String>();
			lists.add("appId=" + Constants.MY_APP_ID);
			lists.add("timestamp=" + timeStamp);
			lists.add("version=" + Constants.MY_API_VERSION);
			String signature = ApiUtils.getSignature(lists, Constants.MY_APP_SECRET);
			System.out.println("signature = " + signature);

			// 调用查看合同接口
			Map<String, String> parasMap = new HashMap<String, String>();
			// 设置appId
			parasMap.put("appId", Constants.MY_APP_ID);
			// 设置version
			parasMap.put("version", Constants.MY_API_VERSION);
			// 设置时间戳
			parasMap.put("timestamp", timeStamp);
			// 设置鉴权签名
			parasMap.put("signature", signature);
			// 设置模板的创建人，如果没有设置则返回属于该appId的所有模板
			//parasMap.put("creator", "");

			String returnJson = ApiHttpUtils.sendPost(API_EXPORT_TEMPLATES_URI, parasMap);
			System.out.println("exporttemplates接口返回：returnJson=" + returnJson);
			UndefinedServiceResult callRes;

			callRes = ApiHttpUtils.getServiceResultFromAPIResultJson(returnJson);

			if (!callRes.getIsSuccess()) {
				System.out.println("调用exporttemplates接口失败，原因为：" + callRes.getResultMsg());
			} else {
				System.out.println("模板列表为：" + callRes.getObj());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
