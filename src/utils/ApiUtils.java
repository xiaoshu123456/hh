package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.imageio.stream.FileImageInputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import com.cloopen.rest.sdk.utils.encoder.BASE64Encoder;

public class ApiUtils {
	public static String getSignature(Map<String, String> parasMap, String appSecret) {
		/** 生成签名 */
		List<String> lists = new ArrayList<String>();
		for(String key:parasMap.keySet()){
			lists.add(key+"=" + parasMap.get(key));
		}
		String signature = getSignature(lists, appSecret);
		System.out.println("signature = " + signature);
		return signature;
	}

	public static String getSignature(List<String> lists, String appSecret) {
		//排序
		Collections.sort(lists);
		String p = "";
		for (String s : lists) {
			if (p.equals("")) {
				p = p + s;
			} else {
				p = p + "&" + s;
			}
		}
		/*自己实现产生签名方法时请注意，appsecret全为小写。*/
		p = p + "&appsecret=" + appSecret;
		System.out.println(" p = " + p);
		//加密
		String signature = DigestUtils.sha1Hex(p).toUpperCase();
		System.out.println(" signature = " + signature);
		//返回加密 签名
		return signature;
	}

	/**
	 * 获取指定图片的Base64编码数据
	 * 
	 * @param rootdir
	 * @param imgPath
	 * @return 图片的Base64编码数据
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readImage(String imgPath) throws FileNotFoundException, IOException {
		String str = "";
		File f = new File(imgPath);
		FileImageInputStream input = null;
		byte[] b = null;
		if (f.exists()) {
			input = new FileImageInputStream(f);
			b = new byte[(int) f.length()];
			input.read(b);
			input.close();
			BASE64Encoder encoder = new BASE64Encoder();
			str = encoder.encode(b);
		}
		return "data:image/png;base64," + str;
	}
}
