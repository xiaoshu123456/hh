package api;

public class UndefinedServiceResult {
	// 请求成功
	public static final String SUCCESS = "0";

	// 服务器出错
	public static final String FAIL = "-1";

	/**
	 * 执行结果码。
	 */
	String resultCode = "";

	/**
	 * 详细的消息内容。
	 */
	String resultMsg = "";

	/**
	 * 要返回的对象
	 */
	Object obj;

	public UndefinedServiceResult(String resultCode, String resultMsg, Object obj) {
		this.resultCode = resultCode;
		this.resultMsg = resultMsg;
		this.obj = obj;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Boolean getIsSuccess() {
		return SUCCESS.equals(getResultCode());
	}
}
