package demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import utils.ApiHttpUtils;
import utils.JsonUtils;

/**
 * 本demo代码是一个简单的示例，以便开发人员更快的熟悉的API接口download的使用。 该接口的功能是：下载指定订单编号对应的合同。
 * 合同内容以流的形式返回。 仅供参考，请开发人员根据业务需要及编程风格进行修改调整。
 */
public class DownloadDemo {

	public static void main(String[] args) {

		/** 下载合同接口地址 */
		String API_DOWN_SIGN_URI = Constants.API_SERVER_URI + "/download.shtml";

		try {
			// 时间戳
			String timeStamp = String.valueOf(System.currentTimeMillis());
			/**
			 * 使用appSecret为参数列表生成签名
			 * 注：生成签名里的timestamp一定要和API接口中传入的timestamp参数的值是相同的。
			 */
			String signature = ApiHttpUtils.getMySignature(Constants.MY_APP_ID, Constants.MY_TEMPLATE_ID,
					Constants.MY_ORDER_ID, timeStamp, Constants.MY_API_VERSION, Constants.MY_APP_SECRET);
			Map<String, String> map = new HashMap<String, String>();
			// 设置appId
			map.put("appId", Constants.MY_APP_ID);
			map.put("templateId", Constants.MY_TEMPLATE_ID);
			map.put("orderId", Constants.MY_ORDER_ID);
			map.put("version", Constants.MY_API_VERSION);
			map.put("timestamp", timeStamp);
			map.put("signature", signature);
			String orderInfoJson = JsonUtils.toJsonString(map);
			System.out.println("download接口中输入的第一个参数：" + orderInfoJson);

			Map<String, String> parasMap = new HashMap<String, String>();
			// 接口的第一个参数:orderInfoJson
			parasMap.put("orderInfoJson", orderInfoJson);
			// 调用接口的二个参数：账号必须是该订单对应合同的收件人之一，否则没有权限下载合同
			parasMap.put("account", Constants.ENTERPRISE_ACCOUNT);
			HttpResponse myResponse = ApiHttpUtils.sendPost_returnResponse(API_DOWN_SIGN_URI, parasMap);
			System.out.println("response code = " + myResponse.getStatusLine().getStatusCode());

			HttpEntity resposeEntity = myResponse.getEntity();

			// 备注：请根据业务需求给每个下载的文档赋予不同的文件名，并存储至适当的位置。
			String filename = "C:/Users/Default/Downloads/" + Constants.MY_ORDER_ID + "/downloaded.pdf";
			if (!ApiHttpUtils.saveFile(resposeEntity, filename).getIsSuccess()) {
				System.out.println("合同下载失败");
			}
			System.out.println("成功将合同下载至：" + filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
