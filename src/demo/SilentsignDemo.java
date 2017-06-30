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

public class SilentsignDemo {

	/**
	 * 本函数为示例的对接代码，以便开发人员更快的熟悉API接口silentsign的使用。
	 * silentsign接口的主要功能是：为指定签署人申请新的CA证书或者使用已有的CA证书，并完成合同的后台签署。
	 * 仅供参考，请开发人员根据业务需要进行修改调整并增加错误处理逻辑。
	 * @param args
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		String api_silentsign_url = Constants.API_SERVER_URI + "/api/silentsign.shtml";
		String account = Constants.ENTERPRISE_ACCOUNT;

		try {
			//时间戳
			String timeStamp = String.valueOf(System.currentTimeMillis());
			/** 使用appSecret为参数列表生成签权签名 
			 * 注：生成签名里的timestamp一定要和API接口中传入的timestamp参数的值是相同的。
			 * */
			String signature = ApiHttpUtils.getMySignature(Constants.MY_APP_ID, Constants.MY_TEMPLATE_ID, Constants.MY_ORDER_ID,
					timeStamp, Constants.MY_API_VERSION, Constants.MY_APP_SECRET);
			Map<String, Object> map = new HashMap<String, Object>();
			//设置appId
			map.put("appId", Constants.MY_APP_ID);
			// 设置要使用的模板，该模板
			map.put("templateId", Constants.MY_TEMPLATE_ID);
			// 设置本次要签约的业务编号
			map.put("orderId", Constants.MY_ORDER_ID);
			// 设置API接口的版本
			map.put("version", Constants.MY_API_VERSION);
			// 设置时间戳
			map.put("timestamp", timeStamp );
			// 设置签权签名
			map.put("signature", signature);
//			// 设置当前签署人编号
//			map.put("currentSigerIndex", 0);

			//******设置第1个签署人的信息
			List<JSONObject> signatories = new ArrayList<JSONObject>();
			JSONObject party0 = new JSONObject();
			// 设置签署人在模板中的编号
			party0.put("sigerIndex", 0);
			// 设置该签署人的账号。
			party0.put("account", Constants.ENTERPRISE_ACCOUNT);
			// 若account为邮箱，需要设置签署人的手机号码。备注：不需要签字的审阅人可以不设手机号码。
			party0.put("phone", "13021185244");
			// 设置该签署人的确认签署手机验证码，如对接平台有给用户发送验证码，则设置；没有发送则不必设置
//			party0.put("smsVerifCode", "1234");
			
			//****设置本次签署使用的CA证书信息
			// 设置使用哪个发证机构的CA证书，默认为0：CFCA
			party0.put("authority", 0);
			// 设置使用的CA证书类型为：企业临时证书
			party0.put("caType",2);
			// 设置申请或使用的CA证书中的公司名称
			party0.put("name", "金风科技有限公司");
			// 设置申请或使用的CA证书中的证件类型："8"为企业营业执照
			party0.put("idCardType", "8");
			// 设置申请或使用的CA证书中的证件号码，请改为正确的真实号码
			party0.put("idCardNo", "888888888");
			// 设置写到合同原因里的该签署人的一些补充信息，如果设备信息等等
			party0.put("addition", "设备信息：ipone");

			/** 下面设置所有控件信息 */
			/**
			 * 备注：如果不清楚模板中的控件信息，请参考文档中的“导出模板签署人签署控件列表接口“
			 * 以及本demo的main_exportwidgets()方法
			 */

			List<JSONObject> signDatalist0 = new ArrayList<JSONObject>();
			// 图片类型（用户签名）
			JSONObject signData0_0 = new JSONObject();
			signData0_0.put("id", 0);
			signData0_0.put("type", "img");
			// 签名控件value的填值方式1：填企业账号,则签署时将使用企业账号在1号签系统中预存的签名或者签章图片
			// signData0_0.put("value", SIGER_ACCOUNT_0);
			// 签名控件value的填值方式2：也可传入图片的BASE64数据
			// try {
			// signData0_0.put("value", ApiUtils.readImage("C:/working_dir/test/panyan2.png"));
			// } catch (IOException e) {
			// e.printStackTrace();
			// return;
			// }
			// 签名控件value的填值方式3：signonclick:姓名
			signData0_0.put("value", "signonclick:张三");

			// 图片类型(印章)
			JSONObject signData0_1 = new JSONObject();
			signData0_1.put("id", 1);
			signData0_1.put("type", "signet");
			// 印章控件value的填值方式1：填企业账号,则签署时将使用企业账号在1号签系统中预存的签名图片或者签章图片
			// signData0_1.put("value", SIGER_ACCOUNT_0);
			// 印章控件value的填值方式2：也可传入图片的BASE64数据
			// try {
			// signData0_1.put("value", ApiUtils.readImage("C:/working_dir/test/panyan3.png"));
			// } catch (IOException e) {
			// e.printStackTrace();
			// return;
			// }
			// 印章控件value的填值方式3：signonclick:公司名称
			signData0_1.put("value","signonclick:宇宙科技有限公司");

			// 文本类型
			JSONObject signData0_2 = new JSONObject();
			signData0_2.put("id", 2);
			signData0_2.put("type", "text");
			signData0_2.put("value","文本控件想要传的内容");

			// 日期类型(系统会签为本次接口调用发生的日期，而不是传入的值，所以可不传值)
			JSONObject signData0_3 = new JSONObject();
			signData0_3.put("id", 2);
			signData0_3.put("type", "date");

			// signDatas(要签署的数据数组)
			signDatalist0.add(signData0_0);
			signDatalist0.add(signData0_1);
			signDatalist0.add(signData0_2);
			signDatalist0.add(signData0_3);
			party0.put("signDatas", signDatalist0);

			// 将这签署人的信息加入signatories链表中
			signatories.add(party0);
			map.put("signatories", signatories);

			/** 至此该签署人信息和控件信息填写完毕 */
			
			Map<String, String> parasMap = new HashMap<String, String>();
			String signCommonJson = JsonUtils.toJsonString(map);
			System.out.println("silentsign接口输入" + signCommonJson);
			parasMap.put("signCommonJson", signCommonJson);

			String returnJson = ApiHttpUtils.sendPost(api_silentsign_url, parasMap);
			System.out.println("silentsign接口返回：returnJson=" + returnJson);
			UndefinedServiceResult silentRes = ApiHttpUtils.getServiceResultFromAPIResultJson(returnJson);

			if (!silentRes.getIsSuccess()) {
				System.out.println("为签署人" + account + "调用后台签署接口失败，原因为：" + silentRes.getResultMsg());
				return;
			} else {
				System.out.println("为签署人" + account + "调用后台签署接口成功。");
			}

			// 解析silentsign的返回值，检查该签署人是否签署成功
			String signStatusJson = silentRes.getObj().toString();
			UndefinedServiceResult getStatusRes = ApiHttpUtils.analyseSignStatusJson(signStatusJson,
					Constants.MY_APP_ID, Constants.MY_ORDER_ID, Constants.MY_API_VERSION, account);

			if (!getStatusRes.getIsSuccess()) {
				System.out.println("签署人" + account + "后台签署订单" + Constants.MY_ORDER_ID + "的合同失败！原因为：" + getStatusRes.getResultMsg());
				return;
			} else {
				System.out.println("签署人" + account + "后台签署订单" + Constants.MY_ORDER_ID + "的合同成功！");
			}

		} catch (Exception e) {
			System.out.println("为签署人" + account + "调用silentsign接口出现异常，详情：" + e);
		}
	}
}
