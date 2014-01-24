package alipay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alipay.signature.MD5Signature;
import alipay.util.HttpUtil;
import alipay.util.SignatureUtil;
import alipay.util.StringUtil;

public class PaymentGateway {
	
	public PaymentGateway(PaymentGatewayConfig config){
		super();
		this.config = config;
	}
	
	public void processNotification(Notification notification, PurchaseOrder order) throws IOException{
		MD5Signature sign = new MD5Signature(config.getMerchantKey());
		if( ! SignatureUtil.checkSign(notification.getParams(), sign)){
			throw new IllegalArgumentException("Signature check faield.");
		}
		if(! validateIpAddr(notification.getIpAddr())){
			throw new IllegalArgumentException("IP addr check faield. Incoming IP:"+notification.getIpAddr());
		}
		//check from alipay see if the notification real.
		if(! verifyNotification(notification.getNotificationId())){
			throw new IllegalArgumentException("Verify notification against Alipay failed.");
		}
		
		if(notification.isSuccessful()){
			order.setStatus(OrderStatus.CHARGED);
			logger.debug("The order is charged.");
		}else if(notification.isWaitingForGoods() && order.getStatus() == OrderStatus.SUBMITTED){
			//send goods
			SendGoodsResult result = sendGoods(notification.getAlipayOrderId());
			if(result.isTranSuccessful()){
				if( ! SignatureUtil.checkSign(result.getParams(), sign)){
					throw new IllegalArgumentException("Send Goods response Signature check faield.");
				}
				if(result.isCompleted()){
					//send goods ok, update po to sent_goods.
					order.setStatus(OrderStatus.SENT_GOODS);
					logger.debug("Goods has been sent, wait customer to confirm.");
				}else{
					logger.error("Fail to send goods");
				}
			}else{
				logger.error("Fail to call send-goods API. " + result.getErrorMessage());
			}
		}else if(notification.isClosed()){
			order.setStatus(OrderStatus.CLOSED);
			logger.debug("Order is closed.");
		}else{
			logger.info("Skip purchase notification." + notification);
		}
	}
	
	public SendGoodsResult sendGoods(String alipayOrderId) throws IOException {
		Map<String, String> request = new HashMap<String, String>();
		request.put(P_SERVICE, SEND_GOODS_CONFIRM_BY_PLATFORM);
		request.put(P_PARTNER, config.getMerchantId());
		request.put(P_INPUT_CHARSET, "UTF-8");
		request.put(P_SIGN_TYPE, "MD5");
		
		request.put(P_TRADE_NO, alipayOrderId);
		request.put(P_LOGISTICS_NAME, "DIRECT");
		request.put(P_TRANSPORT_TYPE, "DIRECT");
		
		String authSign = SignatureUtil.sign(request, new MD5Signature(config.getMerchantKey()));
		request.put(P_SIGN, authSign);
		
		String url = ALIPAY_HOST + "?" + HttpUtil.urlEncode(request);
		logger.debug("Send goods request:"+url);
		String result = HttpUtil.doGet(url);
		logger.debug("Send goods result:"+result);
		return new SendGoodsResult(result);
	}

	private boolean validateIpAddr(String ipAddr) {
		if(StringUtil.equals(ipAddr, "121.0.26.1")
				|| StringUtil.equals(ipAddr, "121.0.26.2")
				//local host for testing.
				|| StringUtil.equals(ipAddr, "127.0.0.1")
				|| StringUtil.equals(ipAddr, "0:0:0:0:0:0:0:1")
				){
			return true;
		}
		return false;
	}

	public String getServiceUrl(PurchaseOrder order) throws IOException{
		Map<String, String> request = getPurchaserRequest(order);
		StringBuffer sb = new StringBuffer();
		sb.append(ALIPAY_HOST).append("?").append(HttpUtil.urlEncode(request));
		String url = sb.toString();
		logger.debug("Generated URL:" + url);
		order.setStatus(OrderStatus.SUBMITTED);
		return url;
	}
	
	public Map<String, String> getPurchaserRequest(PurchaseOrder order) throws IOException{
		Map<String, String> request = new HashMap<String, String>();
		request.put(P_SERVICE, CREATE_PARTNER_TRADE_BY_BUYER);
		request.put(P_PARTNER, config.getMerchantId());
		request.put(P_INPUT_CHARSET, "UTF-8");
		request.put(P_SIGN_TYPE, "MD5");
		request.put(P_NOTIFY_URL, config.getNotifyUrl());
		request.put(P_RETURN_URL, config.getReturnUrl());
		
		request.put(P_OUT_TRADE_NO, order.getId());
		request.put(P_SUBJECT, order.getSubject());
		request.put(P_PAYMENT_TYPE, "1");// 1 -- general purchase.
		request.put(P_LOGISTICS_TYPE, "DIRECT"); // hardcode to express, compare to POST, it has shorter payment period, which is 10d.
		request.put(P_LOGISTICS_FEE, "0.00"); //hardcode to 0.00 as there is no express fee.
		request.put(P_LOGISTICS_PAYMENT, "SELLER_PAY");
		request.put(P_SELLER_ID, config.getMerchantId());
		request.put(P_PRICE, order.getAmount().toPlainString());
		request.put(P_QUANTITY, "1");// hard code to 1.
		request.put(P_TOTAL_FEE, order.getAmount().toPlainString());
		request.put(P_BODY, order.getDescription());
		
		String authSign = SignatureUtil.sign(request, new MD5Signature(config.getMerchantKey()));
		request.put(P_SIGN, authSign);
		
		return request;
	}
	
    public boolean verifyNotification(String nId) {
		Map<String, String> request = new HashMap<String, String>();
		request.put(P_SERVICE, NOTIFY_VERIFY);
		request.put(P_PARTNER, config.getMerchantId());
		request.put(P_NOTIFY_ID, nId);
		
		String verifyUrl = ALIPAY_HOST +"?"+ HttpUtil.urlEncode(request);
		logger.debug("Verify url:"+verifyUrl);
    	String responseTxt = "true";
		if(nId != null) {
			responseTxt = HttpUtil.doGet(verifyUrl);
		}
		logger.debug("Verify NotificationId:"+nId +" Result:"+responseTxt);
        if (responseTxt.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

	private PaymentGatewayConfig config;
	private static final Logger logger = LoggerFactory.getLogger(PaymentGateway.class);
	private static final String ALIPAY_HOST = "https://mapi.alipay.com/gateway.do";
	//api param
	private static final String CREATE_PARTNER_TRADE_BY_BUYER = "create_partner_trade_by_buyer"; //alipayescow
	private static final String SEND_GOODS_CONFIRM_BY_PLATFORM = "send_goods_confirm_by_platform";
	
	private static final String NOTIFY_VERIFY = "notify_verify"; //verify notification.
	
	private static final String P_SERVICE = "service";
	private static final String P_PARTNER = "partner";
	private static final String P_INPUT_CHARSET = "_input_charset";
	private static final String P_SIGN_TYPE = "sign_type";
	private static final String P_SIGN = "sign";
	private static final String P_NOTIFY_URL = "notify_url";
	private static final String P_RETURN_URL = "return_url";
	//business param
	private static final String P_OUT_TRADE_NO = "out_trade_no";
	private static final String P_SUBJECT = "subject"; // 128 Chinese characters
	private static final String P_PAYMENT_TYPE = "payment_type"; //1 purchase.
	private static final String P_LOGISTICS_TYPE = "logistics_type"; //logistics_type
	private static final String P_LOGISTICS_FEE = "logistics_fee"; 
	private static final String P_LOGISTICS_PAYMENT = "logistics_payment";
	
	private static final String P_SELLER_ID = "seller_id";//merchant id.
	private static final String P_PRICE = "price";
	private static final String P_QUANTITY = "quantity";
	private static final String P_TOTAL_FEE = "total_fee";
	private static final String P_BODY = "body";
	
	private static final String P_NOTIFY_ID = "notify_id";
	private static final String P_TRADE_NO = "trade_no";
	private static final String P_LOGISTICS_NAME = "logistics_name";
	private static final String P_TRANSPORT_TYPE = "transport_type";
	
}
