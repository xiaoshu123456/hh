package demo;

import java.util.ArrayList;
import java.util.List;

import utils.ApiUtils;
import org.apache.commons.lang.StringUtils;
/**
 * 本demo代码是用来产生接口中所需签名值siganature的。
 * 请根据签名值所需要包含的参数来设置参数表。
 */
public class GenerateSignatureDemo {
	public static void main(String[] args) {
		// 本例的签名值需要包含：appId、appsecrect、templateId、orderId、version、timestamp。
		// 请设置自己的appId
		String appId = "1S18adf0cd57d";
		// 请设置自己的appSecrect
		String appsecrect = "4a43737889693c640c1942e4f8a14f3e3b7f547f";
		// 请设置自己的templateId
		String templateId = "125871491963343";
		// 请设置自己的orderId
		String orderId = "test-v32-1";

		String version = "03";
		String timestamp = String.valueOf(System.currentTimeMillis());

		/** 生成签名 */
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
		// appsecrect会在ApiUtils.getSignature方法中被加入
		String signature = ApiUtils.getSignature(lists, appsecrect);
	}

}
