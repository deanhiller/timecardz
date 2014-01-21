package controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import models.StatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Controller;
import play.mvc.Router;
import alipay.Notification;
import alipay.PaymentGateway;
import alipay.PaymentGatewayConfig;
import alipay.PurchaseOrder;

public class Payment extends Controller {

	private static final Logger log = LoggerFactory.getLogger(Payment.class);
	private static Random r = new Random(System.currentTimeMillis());
	private static int counter = 0;
	
	private static Map<String, PurchaseOrder> orderMap= new HashMap<String, PurchaseOrder>();
	
	public static void payment() {
		//TODO: when customer comes in this method. a purchase record should be persisted and status is : submited.
		// which means it is pending to pay.
		// put the purchase record ID back to payment.html
		render();
	}

	public static void purchase() {
		
		//TODO: have purchase record ID from request. kind of "request.getParameter("orderId");
		String txId = generateUniqueId()+"";// use random id for DEV.
		
		//TODO: with the purchase record ID and customer ID from session, validate if the purchase belong to
		//current customer. Bar the transaction if they are not matched.
		
		//TODO: fetch out order info, including amount to charge.
		// I emulated a purchase record here for DEV
		PurchaseOrder order = new PurchaseOrder();
		order.setAmount(new BigDecimal("0.01"));
		order.setId(txId);
		order.setSubject("Sample Purchaser Order");
		order.setDescription("Sample Purchaser Order with amount 0.01.");
		order.setStatus(StatusEnum.SUBMIT);
		orderMap.put(txId, order); // for testing.

		PaymentGatewayConfig config = PaymentGatewayConfig.instance();
		config.setNotifyUrl(createFinishUrl());
		config.setReturnUrl(createCallbackUrl());
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

		processMessage(message);
		
		//no matter what, response to alipay.
		renderText("success");
	}
	
	public static void finished(String txId) {
		String message = request.querystring;
		log.info("Receive query:" + message);
		processMessage(message);
		render();
	}

	private static void processMessage(String payload) {
		try {
			Notification notification = new Notification(payload);
			
			//TODO fetch order back from DB. In DEV here, get from map.
			PurchaseOrder order = orderMap.get(notification.getOrderId());
			
			if(order!=null && (order.getStatus() == StatusEnum.SUBMIT || order.getStatus() == StatusEnum.SENT_GOODS)){
				// if the order has not been processed. process it.
				PaymentGateway gateway = new PaymentGateway(PaymentGatewayConfig.instance());
				gateway.processNotification(notification, order);
				if(order.getStatus() == StatusEnum.APPROVED){
					//TODO charged is completed. Do what ever necessary
					//such as extend membership.
					log.info("Purchase completed!");
				}else if (order.getStatus() == StatusEnum.SENT_GOODS){
					//TODO ask customer to confirm goods received.
					log.info("Please confirm receiving goods.");
				}else{
					//TODO purchase declined, do what ever needed.
					log.info("Failed to charge order.");
				}
			}else{
				log.warn("Skipping processing order:" + order);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
