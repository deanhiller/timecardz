package app.alipay;

import java.io.IOException;
import java.math.BigDecimal;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import alipay.PaymentGateway;
import alipay.PaymentGatewayConfig;
import alipay.PurchaseOrder;
import alipay.SendGoodsResult;

public class PaymentGatewayTest {
	private PaymentGateway gateway;
	@Before
	public void buildGateway(){
		gateway = new PaymentGateway(PaymentGatewayConfig.instance());
	}
	
	@Test
	public void testURLGeneration() throws IOException{
		PurchaseOrder order = new PurchaseOrder();
		order.setAmount(new BigDecimal("0.01"));
		order.setId(""+System.currentTimeMillis());
		order.setSubject("Sample Purchaser Order");
		order.setDescription("Sample Purchaser Order with amount 0.01.");
		
		String url = gateway.getServiceUrl(order);
		
		//System.out.println(url);
		TestCase.assertTrue(url.startsWith("https://mapi.alipay.com/gateway.do"));
	}
	
	@Test
	public void testNotificationVerificationWithInvalidOrderShouldFail(){
		String nId="inValidNotificationId";
		TestCase.assertFalse(gateway.verifyNotification(nId));
	}
	
	@Test
	public void testSendGoodsWithInvalidOrderShouldFail() throws IOException{
		String nId="inValidNotificationId";
		SendGoodsResult result = gateway.sendGoods(nId);
		TestCase.assertFalse(result.isTranSuccessful());
		TestCase.assertNotNull(result.getErrorMessage());
		
		TestCase.assertFalse(result.isCompleted());
	}

}
