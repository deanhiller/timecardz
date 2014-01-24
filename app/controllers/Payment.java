package controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Controller;
import play.mvc.Router;
import alipay.Notification;
import alipay.OrderStatus;
import alipay.PaymentGateway;
import alipay.PaymentGatewayConfig;
import alipay.PurchaseOrder;
import alipay.util.StringUtil;

public class Payment extends Controller {

	private static final Logger log = LoggerFactory.getLogger(Payment.class);
	private static Random r = new Random(System.currentTimeMillis());
	private static int counter = 0;
	
	private static Map<String, PurchaseOrder> orderMap = new HashMap<String, PurchaseOrder>();
	
	public static void payment() {
		//TODO: when customer comes in this method. a purchase record should be persisted and status is : submitted.
		// which means it is pending to pay.
		// put the purchase record ID back to payment.html
		String txnId = generateUniqueId()+"";// use random id for DEV.
		
		PurchaseOrder order = new PurchaseOrder();
		order.setAmount(new BigDecimal("0.01"));
		order.setId(txnId);
		order.setSubject("Sample Order - 账单样例");
		order.setDescription("Sample Purchaser Order with amount 0.01.");
		order.setStatus(OrderStatus.SUBMITTED);
		orderMap.put(txnId, order); // for testing.
		
		render(txnId);
	}

	public static void purchase(String txnId) {
		
		//TODO: have purchase record ID from request. kind of "request.getParameter("orderId");
		String orderId = txnId;
		
		//TODO: with the purchase record ID and customer ID from session, validate if the purchase belong to
		//current customer. Bar the transaction if they are not matched.
		
		//TODO: fetch out order info, including amount to charge.
		// I emulated a purchase record here for DEV
		PurchaseOrder order = orderMap.get(orderId);

		PaymentGatewayConfig config = PaymentGatewayConfig.instance();
		//if notify url or return url not set, compute from request.
		if(StringUtil.isEmpty(config.getNotifyUrl()))
			config.setNotifyUrl(createCallbackUrl());
		if(StringUtil.isEmpty(config.getReturnUrl()))
			config.setReturnUrl(createFinishUrl());
		PaymentGateway gateway = new PaymentGateway(PaymentGatewayConfig.instance());

		try {
			String serviceUrl = gateway.getServiceUrl(order);
			redirect(serviceUrl);
		} catch (IOException e) {
			log.error("There is an error on constructing service url." + e);
			e.printStackTrace();
		}
	}

	private static synchronized String generateUniqueId() {
		//random makes it harder to crack and only lives for around 60 minutes before being destroyed....
		long id = r.nextLong();
		long abs = Math.abs(id);
		return counter+"-"+abs;
	}

	private static String createCallbackUrl() {
		String baseUrl = request.getBase();
		//This creates the 
		String url = baseUrl+Router.reverse("Payment.callback").url;
		return url;
	}

	private static String createFinishUrl() {
		String baseUrl = request.getBase();
		//This creates the 
		String url = baseUrl+Router.reverse("Payment.finished").url;
		return url;
	}
	
	public static void callback() {
		String message = params.get("body");
		log.info("Receive notification:" + message);

		try{
			processMessage(message);
		}
		catch (Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
		}
		
		//no matter what, response to alipay. so the message is not resent again and again.
		renderText("success");
	}
	
	public static void finished(String txId) {
		String message = request.querystring;
		log.info("Receive query:" + message);
		PurchaseOrder order = processMessage(message);
		if(order != null && order.getStatus() == OrderStatus.SENT_GOODS){
			//TODO ask customer to confirm goods on Alipay.
		}else{
			//TODO show purchase error message to customer. "There is an error during purchase, please contact admin."
		}
		render();
	}

	private static PurchaseOrder processMessage(String payload) {
		try {
			Notification notification = new Notification(payload);
			notification.setIpAddr(request.remoteAddress);
			
			//TODO fetch order back from DB. In DEV here, get from map.
			PurchaseOrder order = orderMap.get(notification.getOrderId());
			
			if(order!=null && (order.getStatus() == OrderStatus.SUBMITTED || order.getStatus() == OrderStatus.SENT_GOODS)){
				// if the order has not been charged. process it.
				OrderStatus oriStatus = order.getStatus();
				PaymentGateway gateway = new PaymentGateway(PaymentGatewayConfig.instance());
				gateway.processNotification(notification, order);
				if(order.getStatus() == OrderStatus.CHARGED){
					//TODO charged is completed. Do what ever necessary
					//such as to extend membership.
					log.info("Purchase completed!");
				}else if (order.getStatus() == OrderStatus.SENT_GOODS){
					if( oriStatus != OrderStatus.SENT_GOODS){
						//TODO ask customer to confirm goods received.
						//such as sent out emails.
						//first tiem reach the status.
						log.info("Goods has been sent.");
					}
				}else if (order.getStatus() == OrderStatus.CLOSED){
					//The order is closed automatically after 8 days if customer does not pay.
					log.info("Order is closed.");
				}else{
					//We care about charged order and goods sent order, leave the others.
				}
			}else{
				log.warn("Skipping processing order:" + order);
			}
			return order;
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
