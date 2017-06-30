package specialDemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import api.UndefinedServiceResult;
import demo.Constants;
import utils.ApiHttpUtils;
import utils.JsonUtils;

/**
 * 本demo代码是对“发起合同之后，为第一个签署人自动调用后台签署”的情况的简单示例。
 * 仅供参考，请开发人员根据业务需要及编程风格进行修改调整，并增加错误处理逻辑 。
 * 本demo使用的模板是两个签署人：
 * 第一个签署人有四个签署控件，按次序分别是：签名(img)、印章(signet)、文本(text)、和签署日期(date)
 * 第二个签署人有一个签署控件：签名(img)
 * 
 */
public class StartSignWithAutoSilentSignDemo {

	/**
	 * 本函数为示例对接代码，以便开发人员更快的熟悉API接口的使用。仅供参考，请开发人员根据业务需要进行摘取和修改。
	 * 
	 * @param args
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static void main(String[] args) throws IOException {

		String api_startsign_url = Constants.API_SERVER_URI + "/startsign.shtml";

		try {

			// ****发起签署，第一个签署人自动签
			
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
				// 设置当前签署人的编号，对于startsign接口，该值总是为0
				map.put("currentSigerIndex", 0);

				// ******设置第1个签署人的信息
				List<JSONObject> signatories = new ArrayList<JSONObject>();
				JSONObject party0 = new JSONObject();
				// 设置签署人在模板中的编号
				party0.put("sigerIndex", 0);
				// 设置是否在startsign中完成该签署人的签署，即自动签署。
				party0.put("isAutoSign", 1);
				// 设置该签署人的账号。
				party0.put("account", Constants.ENTERPRISE_ACCOUNT);
				// 若account为邮箱，需要设置签署人的手机号码。备注：不需要签字的审阅人可以不设手机号码。
				party0.put("phone", "13712345678");
				// 设置该签署人的确认签署手机验证码，如对接平台有给用户发送验证码，则设置；没有发送则不必设置
				party0.put("smsVerifCode", "1234");

				// ****设置第一个签署人的后台签署使用的CA证书信息*****//
				// 设置使用哪个发证机构的CA证书，默认为0：CFCA
				party0.put("authority", 0);
				// 备注：默认使用平台证书，如果使用其他证书，请修改代码，并提供必要的信息
				// 设置使用的CA证书类型为：企业临时证书
				party0.put("caType", 2);
				// 设置申请或使用的CA证书中的公司名称
				party0.put("name", "张三之科技有限公司");
				// 设置申请或使用的CA证书中的证件类型："8"为企业营业执照
				party0.put("idCardType", "8");
				// 设置申请或使用的CA证书中的证件号码，请改为正确的真实号码
				party0.put("idCardNo", "888888888");

				// 设置写到合同原因里的该签署人的一些补充信息，如果设备信息等等
				party0.put("addition", "设备信息：ipone");
				// 设置该合同的标题
				party0.put("title", "测试发起时自动为第一个人调用后台签署");

				/** 下面设置自动签署人的所有控件信息 */
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
				// signData0_0.put("value",
				// ApiUtils.readImage("C:/working_dir/test/panyan2.png"));
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
				// signData0_1.put("value",
				// ApiUtils.readImage("C:/working_dir/test/panyan3.png"));
				// } catch (IOException e) {
				// e.printStackTrace();
				// return;
				// }
				// 印章控件value的填值方式3：signonclick:公司名称
				signData0_1.put("value", "signonclick:宇宙科技有限公司");

				// 文本类型
				JSONObject signData0_2 = new JSONObject();
				signData0_2.put("id", 2);
				signData0_2.put("type", "text");
				signData0_2.put("value", "文本控件想要传的内容");

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

				// ******设置第2个签署人的信息，因为不需要自动签署，不需要提供CA证书和控件信息*****//
				JSONObject party1 = new JSONObject();
				party1.put("sigerIndex", 1);
				// 设置第二个签署人的account
				party1.put("account", Constants.USER_ACCOUNT);
				// 若account为邮箱，需要设置签署人的手机号码。备注：不需要签字的审阅人可以不设手机号码。
				party1.put("phone", "13717656980");

				// 将这两个签署人的信息加入signatories链表中
				signatories.add(party0);
				signatories.add(party1);
				map.put("signatories", signatories);

				/** 至此该签署人信息和控件信息填写完毕 */

				/** 调用发起签署接口创建合同，并为设置为自动签署的一方完成签署 */
				Map<String, String> parasMap = new HashMap<String, String>();
				String signCommonJson = JsonUtils.toJsonString(map);
				parasMap.put("signCommonJson", signCommonJson);
				System.out.println("startsign接口输入：" + signCommonJson);
				String returnJson = ApiHttpUtils.sendPost(api_startsign_url, parasMap);
				System.out.println("startsign接口返回：returnJson=" + returnJson);
				UndefinedServiceResult startRes = ApiHttpUtils.getServiceResultFromAPIResultJson(returnJson);
				if (startRes.getIsSuccess()) {
					System.out.println("startsign接口返回成功！");
				} else {
					System.out.println("startsign接口返回失败！");
				}

				// 检查该签署人是否签署成功
				// 直接从startsign的返回值中解析签署状态
				String signStatusJson = startRes.getObj().toString();
				UndefinedServiceResult getStatusRes = ApiHttpUtils.analyseSignStatusJson(signStatusJson,
						Constants.MY_APP_ID, Constants.MY_ORDER_ID, Constants.MY_API_VERSION,
						Constants.ENTERPRISE_ACCOUNT);

				if (!getStatusRes.getIsSuccess()) {
					System.out.println("签署人" + Constants.ENTERPRISE_ACCOUNT + "签署订单" + Constants.MY_ORDER_ID + "的合同失败！");
					return;
				} else {
					System.out.println("签署人" + Constants.ENTERPRISE_ACCOUNT + "签署订单" + Constants.MY_ORDER_ID + "的合同成功！");
				}

		} catch (Exception e) {
			System.out.println("出现异常，详情：" + e);
		}
	}
}
