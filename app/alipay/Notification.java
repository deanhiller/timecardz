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
	private static final String P_REFUND_STATUS ="refund_status";
	private static final String P_NOTIFY_ID ="notify_id";
	private static final String P_TOTAL_FEE ="total_fee";
	
	private String ipAddr;

	public Notification(String query) throws IOException{
		logger.debug("Receive notification:" + query);
		params = HttpUtil.urlDecode(query);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("[")
		.append("tradeStatus=").append(params.get(P_TRADE_STATUS))
		.append("&refundStatus=").append(params.get(P_REFUND_STATUS))
		.append("&orderId=").append(getOrderId())
		.append("&ipAddr=").append(getIpAddr())
		.append("&notificationId=").append(getNotificationId())
		.append("&alipayOrderId=").append(getAlipayOrderId())
		.append("]");
		return sb.toString();
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
	
	public boolean isClosed(){
		return StringUtil.equals(params.get(P_TRADE_STATUS), "TRADE_CLOSED");
	}
	
	/*
	 * Once customer has confirmed receiving goods, money is sent to merchant's account,
	 * customer no longer able to do refund.
	 * The only time window when customer can do a refund, is in between merchant has sent goods
	 * and customer no confirm receiving goods.
	 * 
	 * Since the case here enable timecard only when customer has confirm goods. Therefore
	 * we don't case refund notifications.
	 */
//	public boolean isRefundRequest(){
//		//Generally we don't need to care these message. Because there is no real money withdraw.
//		return StringUtil.equals(params.get(P_REFUND_STATUS), "WAIT_SELLER_AGREE") || // no goods is received.
//				StringUtil.equals(params.get(P_REFUND_STATUS), "WAIT_BUYER_RETURN_GOODS") || //goods received, buyer want return.
//				StringUtil.equals(params.get(P_REFUND_STATUS), "WAIT_SELLER_CONFIRM_GOODS"); //goods received, buyer has sent back.
//	}
//	public boolean isRefunded(){
//		//When this message is seen. then it is real refunded.
//		return StringUtil.equals(params.get(P_REFUND_STATUS), "REFUND_SUCCESS");
//	}
}
