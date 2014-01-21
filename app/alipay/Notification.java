package alipay;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alipay.util.HttpUtil;
import alipay.util.StringUtil;

public class Notification {

	private static final Logger logger = LoggerFactory.getLogger(Notification.class);
	Map<String, String> params;


	//private static final String P_IS_SUCCESS ="is_success";
	private static final String P_OUT_TRADE_NO ="out_trade_no";
	private static final String P_TRADE_NO ="trade_no";
	private static final String P_TRADE_STATUS ="trade_status";
	private static final String P_NOTIFY_ID ="notify_id";
	private static final String P_TOTAL_FEE ="total_fee";
	
	private String ipAddr;

	public Notification(String query) throws IOException{
		logger.debug("Receive notification:" + query);
		params = HttpUtil.urlDecode(query);
	}
	
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public String getOrderId(){
		return params.get(P_OUT_TRADE_NO);
	}
	public String getNotificationId(){
		return params.get(P_NOTIFY_ID);
	}
	public String getAlipayOrderId(){
		return params.get(P_TRADE_NO);
	}
	public String getAmount(){
		return params.get(P_TOTAL_FEE);
	}
	public boolean isSuccessful(){
		return StringUtil.equals(params.get(P_TRADE_STATUS), "TRADE_FINISHED") ||
				StringUtil.equals(params.get(P_TRADE_STATUS), "TRADE_SUCCESS");
	}
	public boolean isWaitingForGoods(){
		return StringUtil.equals(params.get(P_TRADE_STATUS), "WAIT_SELLER_SEND_GOODS");
	}
}
