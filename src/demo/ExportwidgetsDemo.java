package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.UndefinedServiceResult;
import utils.ApiHttpUtils;
import utils.ApiUtils;
/**
 * 本demo代码是一个简单的示例，以便开发人员更快的熟悉的API接口exportwidgets的使用。
 * 该接口的功能是：导出指定模板的签署控件列表，以便开发人员比照进行编码。 列表内容例如：
 * {
 *	 "0": [   //签署人索引:第一个签署人
 *    {
 *     "id": 0,  //签署控件的编号
 *      "type": "img"   //签署控件的类型
 *    },
 *    {
 *      "id": 1,
 *      "type": "signet"
 *    },
 *    {
 *      "id": 2,
 *      "type": "text"
 *      "prompt": "身份证号码"
 *    },
 *    {
 *      "id": 3,
 *      "type": "date"
 *    }
 *  ],
 *  "1": [
 *    {
 *      "id": 0,
 *      "type": "img"
 *    }
 *  ]
* }
 *
 * 仅供参考，请开发人员根据业务需要及编程风格进行修改调整。
 */
public class ExportwidgetsDemo {

	public static void main(String[] args) {
		// API接口exportwigets的URL常量。
		String api_exportwidgets_url = Constants.API_SERVER_URI + "/exportwidgets.shtml";

		try {
			String timeStamp = String.valueOf(System.currentTimeMillis());

			/**
			 * 使用appSecret为参数列表生成签权签名
			 * 注：生成签名里的timestamp一定要和API接口中传入的timestamp参数的值是相同的。
			 */
			List<String> lists = new ArrayList<String>();
			lists.add("appId=" + Constants.MY_APP_ID);
			lists.add("templateId=" + Constants.MY_TEMPLATE_ID);
			lists.add("timestamp=" + timeStamp);
			lists.add("version=" + Constants.MY_API_VERSION);
			String signature = ApiUtils.getSignature(lists, Constants.MY_APP_SECRET);
			System.out.println("signature = " + signature);

			// 调用查看合同接口
			Map<String, String> parasMap = new HashMap<String, String>();
			// 设置appId
			parasMap.put("appId", Constants.MY_APP_ID);
			// 设置模板编号
			parasMap.put("templateId", Constants.MY_TEMPLATE_ID);
			// 设置version
			parasMap.put("version", Constants.MY_API_VERSION);
			// 设置时间戳
			parasMap.put("timestamp", timeStamp);
			// 设置鉴权签名
			parasMap.put("signature", signature);

			String returnJson = ApiHttpUtils.sendPost(api_exportwidgets_url, parasMap);
			System.out.println("exportwidgets接口返回：returnJson=" + returnJson);
			UndefinedServiceResult callRes;

			callRes = ApiHttpUtils.getServiceResultFromAPIResultJson(returnJson);

			if (!callRes.getIsSuccess()) {
				System.out.println("调用exportwidgets接口失败，原因为：" + callRes.getResultMsg());
			} else {
				System.out.println("模板列表为：" + callRes.getObj());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
