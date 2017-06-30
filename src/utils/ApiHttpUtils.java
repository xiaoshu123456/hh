package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import api.UndefinedServiceResult;
import demo.Constants;

public class ApiHttpUtils {
	private static final Logger log = Logger.getLogger(ApiHttpUtils.class);

	public static UndefinedServiceResult analyseSignStatusJson(String signStatusJson, String appId, String orderId,
			String version, String expectAccount) {
		try {
			JSONObject ssWrapObject = JSONObject.parseObject(signStatusJson);
			if (ssWrapObject == null) {
				return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "接口返回JSON格式不正确", false);
			}
			Object appIdObj = ssWrapObject.get("appId");
			Object orderIdObj = ssWrapObject.get("orderId");
			if (appIdObj == null || orderIdObj == null) {
				return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "接口返回结果中没有订单或者appId信息", false);
			}
			// 验证订单信息
			if (!appId.equals(appIdObj.toString()) || !orderId.equals(orderIdObj.toString())) {
				return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "接口返回结果不是期待的订单", false);
			}

			Object signStatus = ssWrapObject.get("signStatus");
			if (signStatus == null) {
				return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "接口返回结果中没有signStatus", null);
			}
			Integer signStatus_i = (Integer)signStatus;
			// 取值范围：0=草稿、1=编辑完成（待签署）、2=作废、3=拒签、4=已成功、5=已过期
			switch (signStatus_i) {
			case 1:
				System.out.println("合同状态为：待签署");
				break;
			case 2:
				System.out.println("合同状态为：已作废");
				break;
			case 4:
				System.out.println("合同状态为：所有人已成功完成签署");
				break;
			case 5:
				System.out.println("合同状态为：已过期");
				break;
			default:
				return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "接口返回合同状态不正确", null);
			}

			// 解析signatories中的签署人签署状态
			Object signatoriesObj = ssWrapObject.get("signatories");
			if (signatoriesObj == null) {
				return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "接口返回结果中没有signatories", null);
			}
			List<Object> sigObjlist = JSONObject.parseArray(signatoriesObj.toString());
			if (sigObjlist == null || sigObjlist.isEmpty()) {
				return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "接口返回结果signatories中没有每个签署人的状态", null);
			}
			if (version.equals("03")) {
				boolean foundAccount = false;
				for (Object sigObj : sigObjlist) {
					JSONObject sigJsonObj = JSONObject.parseObject(sigObj.toString());
					if (sigJsonObj == null) {
						return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "接口返回结果不正确", null);
					} else {
						Object account = sigJsonObj.get("account");
						if (account == null) {
							return new UndefinedServiceResult(UndefinedServiceResult.FAIL,
									"接口返回结果signatories中没有签署人的account", null);
						}
						Object status = sigJsonObj.get("status");
						if (status == null) {
							return new UndefinedServiceResult(UndefinedServiceResult.FAIL,
									"接口返回结果signatories中没有签署人"+account+"的status", null);
						}
						Integer sigerStatus_i = (Integer)status;
						String sigerStatus = null;
						// 取值范围：0=待签署：还没轮到本人签署、1=签署中：该本人签署、2=已签署、3=拒签。
						switch (sigerStatus_i) {
						case 0:
							sigerStatus= "待签署：还没轮到本人签署";
							break;
						case 1:
							sigerStatus= "签署中：该本人签署";
							break;
						case 2:
							sigerStatus= "已签署";
							break;
						case 3:
							sigerStatus= "拒签";
							break;
						default:
							return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "接口返回签署人的签署状态不正确", null);
						}
						System.out.println("****签署人" + account + "签署订单" + Constants.MY_ORDER_ID + "状态为：" + sigerStatus);

						if(StringUtils.equals(expectAccount, account.toString()) ){
							foundAccount = true;
							if(sigerStatus_i == 2){
								return new UndefinedServiceResult(UndefinedServiceResult.SUCCESS, "签署成功", null);
							}else{
								return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "尚未签署成功", null);
							}
						}
					}
				}
				if(!foundAccount){
					return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "返回值中未找到预期签署人", null);
				}else{
					return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "尚未签署成功", null);
				}
				
			} else {
				System.out.println("03之前的version已经不支持了。");
				return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "03之前的version已经不支持了", null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "analyseSignStatusJson出现异常", null);
		}
		
	}

//	/**
//	 * 检查AccountSignStatus中指定account的签署人是否已经完成签署
//	 * 
//	 * @param signStatus
//	 * @param account
//	 * @return
//	 */
//	public static boolean isSigerSignedSuccess(AccountSignStatus signStatus, String account) {
//		if (signStatus == null || account == null) {
//			System.out.println("输入参数为空");
//			return false;
//		}
//		if (signStatus.getSignStatus() == 4) {
//			// 合同的所有签署人都已完成签署
//			return true;
//		}
//		List<AccountReceiverInfo> signatories = signStatus.getSignatories();
//		for (AccountReceiverInfo accountReceiverInfo : signatories) {
//			if (account.equals(accountReceiverInfo.getAccount())) {
//				return (accountReceiverInfo.getStatus() == 2);
//			}
//		}
//		return false;
//	}



	/**
	 * 把HttpResponse实体中的数据流写到文件中
	 * 
	 * @param entity
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static UndefinedServiceResult saveFile(HttpEntity entity, String filePath) throws IOException {

		if (entity == null || filePath == null) {
			System.out.println("输入参数为空");
			return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "输入参数为空", null);
		}
		InputStream inStream = null;
		FileOutputStream fileout = null;
		try {
			inStream = entity.getContent();
			System.out.println("file stream size:{}" + inStream.available());
			File file = new File(filePath);
			file.getParentFile().mkdirs();

			fileout = new FileOutputStream(file);
			/**
			 * 根据实际运行效果 设置缓冲区大小
			 */
			byte[] buffer = new byte[10485760];
			int ch = 0;
			while ((ch = inStream.read(buffer)) != -1) {
				fileout.write(buffer, 0, ch);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("saveFile error: " + e);
			return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "saveFile error: " + e, null);
		} finally {
			inStream.close();
			fileout.flush();
			fileout.close();
		}
		return new UndefinedServiceResult(UndefinedServiceResult.SUCCESS, "saveFile success", null);
	}

	/**
	 * 发送客户端的post请求，并将远程返回的JSON字符串返回。
	 * 该函数适用于startsign/silentsign/signstatus/view等等接口。
	 * 
	 * @param url
	 *            API接口的URL
	 * @param parasMap
	 *            参数的map：key为参数名称，value为对象的JSON字符串
	 * @return
	 */
	public static String sendPost(String url, Map<String, String> parasMap) {
		if (url == null) {
			System.out.println("输入参数为空");
			return null;
		}

		if (parasMap == null || parasMap.isEmpty()) {
			System.out.println("输入参数为空");
			return null;
		}

		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(url);

			// 设置post请求的实体部分
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			for (String key : parasMap.keySet()) {
				StringBody stringBody = new StringBody(parasMap.get(key), Charset.forName("UTF-8"));
				HttpEntity reqEntity = builder.addPart(key, stringBody).build();
				httppost.setEntity(reqEntity);
			}
			HttpResponse myResponse = httpClient.execute(httppost);
			if (myResponse.getStatusLine().getStatusCode() != 200) {
				System.out.println("code:{}" + myResponse.getStatusLine().getStatusCode());
			}
			HttpEntity entity = myResponse.getEntity();
			String returnJson = EntityUtils.toString(entity, CharEncoding.UTF_8);

			// System.out.println("调用url=" + url + ", response: --> {}" +
			// returnJson);
			return returnJson;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送客户端的post请求，并将远程返回的HttpResponse返回。
	 * 该函数仅适用于调用下载合同接口download，该接口将以文件流的形式返回给客户端，客户端需要从HttpResponse的实体中获得文件流。
	 * 
	 * @param url
	 *            API接口的URL
	 * @param parasMap
	 *            参数的map：key为参数名称，value为对象的JSON字符串
	 * @return
	 */
	public static HttpResponse sendPost_returnResponse(String url, Map<String, String> parasMap) {
		if (url == null) {
			System.out.println("输入参数为空");
			return null;
		}

		if (parasMap == null || parasMap.isEmpty()) {
			System.out.println("输入参数为空");
			return null;
		}
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(url);

			// 设置post请求的实体部分
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			for (String key : parasMap.keySet()) {
				StringBody stringBody = new StringBody(parasMap.get(key), Charset.forName("UTF-8"));
				HttpEntity reqEntity = builder.addPart(key, stringBody).build();
				httppost.setEntity(reqEntity);
			}
			// System.out.println("calling url=" + url);

			HttpResponse myResponse = httpClient.execute(httppost);
			return myResponse;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static UndefinedServiceResult getServiceResultFromAPIResultJson(String returnJson) throws Exception {
		if (returnJson == null) {
			return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "getServiceResultFromAPIResultJson输入参数为空",
					null);
		}
		try {
			JSONObject rObject = JSONObject.parseObject(returnJson);

			if (rObject != null) {
				Object resultCode = rObject.get("resultCode");
				Object resultMsg = rObject.get("resultMsg");
				Object obj = rObject.get("obj");

				if (resultCode == null || resultMsg == null) {
					System.out.println("API接口返回结果中没有包括有效的resultCode或者resultMsg");
					return new UndefinedServiceResult(UndefinedServiceResult.FAIL,
							"API接口返回结果中没有包括有效的resultCode或者resultMsg", null);
				} else if (resultCode != null && resultCode.toString().equals("0")) {
					// System.out.println("API接口返回请求成功");
					return new UndefinedServiceResult(resultCode.toString(), resultMsg.toString(), obj);
				} else {
					System.out.println("API接口返回请求失败,原因：" + resultMsg.toString());
					return new UndefinedServiceResult(resultCode.toString(), resultMsg.toString(), obj);
				}
			} else {
				return new UndefinedServiceResult(UndefinedServiceResult.FAIL, "API接口返回结果不是一个合法的json字符串", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("API接口返回结果不是一个合法的json字符串,解析出现异常，详情：" + e.toString());
			Exception ex = new Exception("API接口返回结果不是一个合法的json字符串,解析出现异常");
			throw ex;
		}
	}

	/**
	 * 解析API接口：signstauts/view/download等返回的JSON字符串,内容包括：resultCode：响应码；
	 * ResultMsg： 提示信息；obj：不同的返回对象Object
	 * 如果其中的resultCode为0，表示本次API调用成功且obj为期待中的结果对象，否则ResultMsg为错误原因描述且obj为null。
	 * 
	 * @param returnJson
	 *            API接口返回的JSON字符串
	 * @return 若调用成功返回其中的对象Object，否则返回null
	 */
	public static Object getResultObjFromAPIReturnJson(String returnJson) {
		if (returnJson == null) {
			System.out.println("输入参数为空");
			return null;
		}
		try {
			JSONObject rObject = JSONObject.parseObject(returnJson);

			if (rObject != null) {
				Object resultCode = rObject.get("resultCode");
				Object resultMsg = rObject.get("resultMsg");

				if (resultCode == null || resultMsg == null) {
					System.out.println("API接口返回结果中没有包括有效的resultCode或者resultMsg");
					return null;
				} else if (resultCode != null && resultCode.toString().equals("0")) {
					// System.out.println("API接口返回请求成功");
					Object obj = rObject.get("obj");
					if (obj == null) {
						System.out.println("API接口返回结果中没有包括有效的obj");
						return null;
					} else {
						//System.out.println("API接口返回结果中的obj：" + obj);
						return obj;
					}
				} else {
					System.out.println(
							"API接口返回结果错误：resultCode=" + resultCode.toString() + ",resultMsg=" + resultMsg.toString());
				}
			} else {
				System.out.println("API接口返回结果不是一个合法的json字符串");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("API接口返回结果不是一个合法的json字符串,解析出现异常，详情：" + e.toString());
		}
		return null;
	}

	/**
	 * 根据传入的参数计算安全签名
	 * 
	 * @param appId
	 * @param templateId
	 * @param orderId
	 * @param timestamp
	 * @param version
	 * @return
	 */
	public static String getMySignature(String appId, String templateId, String orderId, String timestamp,
			String version, String appSecret) {

		List<String> lists = new ArrayList<String>();
		if (!StringUtils.isBlank(appId)) {
			lists.add("appId=" + appId);
		}
		if (!StringUtils.isBlank(orderId)) {
			lists.add("orderId=" + orderId);
		}
		if (!StringUtils.isBlank(templateId)) {
			lists.add("templateId=" + templateId);
		}
		if (!StringUtils.isBlank(timestamp)) {
			lists.add("timestamp=" + timestamp);
		}
		if (!StringUtils.isBlank(version)) {
			lists.add("version=" + version);
		}
		
		String signature = ApiUtils.getSignature(lists, appSecret);
		return signature;
	}


	public static String sendPostVerifyDoc(String url, Map<String, String> parasMap, File file) {
		if (url == null) {
			System.out.println("输入参数为空");
			return null;
		}

		if (parasMap == null || parasMap.isEmpty()) {
			System.out.println("输入参数为空");
			return null;
		}

		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(url);

			// 设置post请求的实体部分
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			for (String key : parasMap.keySet()) {
				StringBody stringBody = new StringBody(parasMap.get(key), Charset.forName("UTF-8"));
				
				HttpEntity reqEntity = builder.addPart(key, stringBody).build();
				
				httppost.setEntity(reqEntity);
			}
			HttpEntity reqEntity = builder.addPart("file", new FileBody(file)).build();
			httppost.setEntity(reqEntity);
			
			HttpResponse myResponse = httpClient.execute(httppost);
			if (myResponse.getStatusLine().getStatusCode() != 200) {
				System.out.println("response code = " + myResponse.getStatusLine().getStatusCode());
			}
			HttpEntity entity = myResponse.getEntity();
			String returnJson = EntityUtils.toString(entity, CharEncoding.UTF_8);

			return returnJson;
		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
