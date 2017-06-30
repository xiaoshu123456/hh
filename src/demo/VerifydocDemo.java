package demo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import api.UndefinedServiceResult;
import utils.ApiHttpUtils;

/**
 * 本demo代码是一个简单的示例，以便开发人员更快的熟悉的API验签接口verifydoc的使用。 仅供参考，请开发人员根据业务需要及编程风格进行修改调整。
 */
public class VerifydocDemo {

	public static void main(String[] args) {
		// 验签接口的url
		String api_verify_url = Constants.API_SERVER_URI + "/verifydoc.shtml";
		try {
			// 时间戳
			String timeStamp = String.valueOf(System.currentTimeMillis());
			/**
			 * 使用appSecret为参数列表生成签权签名
			 * 注：生成签名里的timestamp一定要和API接口中传入的timestamp参数的值是相同的。
			 */
			String signature = ApiHttpUtils.getMySignature(Constants.MY_APP_ID, Constants.MY_TEMPLATE_ID,
					Constants.MY_ORDER_ID, timeStamp, Constants.MY_API_VERSION, Constants.MY_APP_SECRET);
			Map<String, String> map = new HashMap<String, String>();
			// 设置appId
			map.put("appId", Constants.MY_APP_ID);
			// 设置API接口的版本
			map.put("version", Constants.MY_API_VERSION);
			// 设置时间戳
			map.put("timestamp", timeStamp);
			// 设置签权签名
			map.put("signature", signature);

			// 要验签的pdf文件
			File file = new File("C:/Users/Default/Downloads/test-v33-3/downloaded.pdf");

			String returnJson = ApiHttpUtils.sendPostVerifyDoc(api_verify_url, map, file);
			System.out.println("verifydoc接口返回：returnJson=" + returnJson);
			UndefinedServiceResult callRes;

			callRes = ApiHttpUtils.getServiceResultFromAPIResultJson(returnJson);
			if (!callRes.getIsSuccess()) {
				System.out.println("调用verifydoc接口失败，原因为：" + callRes.getResultMsg());
			} else {
				System.out.println("验签结果为：" + callRes.getObj());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
