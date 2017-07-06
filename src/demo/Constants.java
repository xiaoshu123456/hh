package demo;

public class Constants {
	/** api server的地址 */
	// 备注：当正式上线时，需要更新为正式环境下的URL
	public static String API_SERVER_URI = "http://open.enjoysign.com";
	// 对接平台在一号签系统中的企业账号，请对接测试前到http://open.enjoysign.com/申请自己的企业账号。
	// 在本demo中，使用该账号作为第一个签署人
	public static final String ENTERPRISE_ACCOUNT = "wanghl816@sina.cn";
	// 第二个签署人的account,可为邮箱或者手机号码。
	public static final String USER_ACCOUNT = "api_example@enjoysign.com";

	// 一号签系统给对接平台分配的企业APPID，请对接测试前申请自己的企业APPID。
	public static final String MY_APP_ID = "1S14981167881478a0c90861c";
	// 一号签系统给对接平台分配的企业APP的秘钥
	public static final String MY_APP_SECRET = "d3863715f3a17733019a6b2aab23f40d7ce8abd3";
	
	// 本次签署使用的模板ID，本模板为两方签署。请对接测试前创建自己的模板。
	public static final String MY_TEMPLATE_ID = "27514981219131893";
	// 本demo使用的API的版本。03之后版本的startsign和silentsign的返回值中包括签署状态，不必再调用signstatus来查询签署状态。
	public static final String MY_API_VERSION = "03";

	// 双方均签约成功后，请修改订单编号,否则会报重复签署的错误     由平台生成的业务号，同一平台号下，所有业务号的编号必须唯一。
	//1号签将有唯一的合同与之对应。
	public static final String MY_ORDER_ID = "test1";

}
