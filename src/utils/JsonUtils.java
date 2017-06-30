package utils;



import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * JSON工具类
 * 
 */

public class JsonUtils {
	/** 日期配置 */
	private static final SerializeConfig dateConfig;
	/** 空值配置 */
	private static final SerializerFeature[] nullFeatures = { SerializerFeature.WriteMapNullValue, // Map输出空置字段
			SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
			SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
			SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
			SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null
	};

	static {
		dateConfig = new SerializeConfig();
		ObjectSerializer dataFormat = new ObjectSerializer() {
			public void write(JSONSerializer jsonserializer, Object obj, Object obj1, Type type) throws IOException {
				if (obj == null) {
					jsonserializer.getWriter().writeNull();
					return;
				} else {
					Date date = (Date) obj;
					/** 默认的时间格式化器，格式为yyyy-MM-dd HH:mm:ss: */
					SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");
					jsonserializer.write(defaultFormat.format(date));
					return;
				}
			}
		};
		dateConfig.put(java.util.Date.class, dataFormat);
		dateConfig.put(java.sql.Date.class, dataFormat);
	}

	/**
	 * 将对象转换成Json字符串
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static String toJsonString(Object object) {
		return JSON.toJSONString(object, dateConfig, SerializerFeature.WriteMapNullValue);
	}

	/**
	 * 将对象转换成Json字符串，对null对象进行格式化<br/>
	 * Map输出空置字段(null)<br/>
	 * list字段如果为null，输出为[]，而不是null<br/>
	 * 数值字段如果为null，输出为0，而不是null<br/>
	 * Boolean字段如果为null，输出为false，而不是null<br/>
	 * 字符类型字段如果为null，输出为""，而不是null<br/>
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static String toJsonStringFormatNull(Object object) throws Exception {
		return JSON.toJSONString(object, dateConfig, nullFeatures);
	}

	/**
	 * 将字符串转换成指定的对象
	 * 
	 * @param <T>
	 * @param jsonStr
	 * @param t
	 * @return
	 */
	public static <T> T parse2Object(String jsonStr, Class<T> t) {

		return JSON.parseObject(jsonStr, t);
	}

	/**
	 * 根据传入的结果标志和结果消息生成标准的结果JSON字符串。
	 * 
	 * @param resultCode
	 *            结果标志，可以使用ResultConstrant中的常量值。
	 * @param resultMsg
	 *            结果消息。
	 * @return
	 */
	public static String createStandardResultJsonString(int resultCode, String resultMsg) {
		JSONObject result = new JSONObject();
		result.put("resultCode", resultCode);
		result.put("resultMsg", resultMsg);
		return result.toJSONString();
	}

	/**
	 * 根据传入的消息生成标准的成功消息结果JSON字符串。
	 * 
	 * @param resultMsg
	 *            成功的消息。
	 * @return
	 */
	public static String createStandardSuccessResultJsonString(String resultMsg) {
		return createStandardResultJsonString(0, resultMsg);
	}

	/**
	 * 根据传入的消息生成标准的失败消息结果JSON字符串。
	 * 
	 * @param resultMsg
	 *            失败的详细消息。
	 * @return
	 */
	public static String createStandardFaildResultJsonString(String resultMsg) {
		return createStandardResultJsonString(2, resultMsg);
	}

	/**
	 * 根据传入的消息生成标准的异常消息结果JSON字符串。
	 * 
	 * @param resultMsg
	 *            异常的详细消息。
	 * @return
	 */
	public static String createStandardExceptionResultJsonString(String resultMsg) {
		return createStandardResultJsonString(3, resultMsg);
	}

	/**
	 * 创建标准的回话超时的返回结果JSON字符串。
	 * @return
	 */
	public static String createSessionTimeoutResultJsonString() {
		return createStandardResultJsonString(5, "您的会话已超时。");
	}
}