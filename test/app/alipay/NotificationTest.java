package app.alipay;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import alipay.Notification;
import alipay.PaymentGatewayConfig;
import alipay.signature.MD5Signature;
import alipay.util.HttpUtil;
import alipay.util.SignatureUtil;

public class NotificationTest {

	@Test
	public void testAsyncNotification() throws IOException {
		String query= "trade_no=2008102203208746&out_tra"
				+ "de_no=3618810634349901&discount=-5&payment_type=1&subject=iphone%E"
				+ "6%89%8B%E6%9C%BA&body=Hello&price=10.00&quantity=1&total_fee=10.0"
				+ "0&trade_status=TRADE_FINISHED&refund_status=REFUND_SUCCESS&selle"
				+ "r_email=chao.chenc1%40alipay.com&seller_id=2088002007018916&buyer_id="
				+ "2088002007013600&buyer_email=13758698870&gmt_create=2008-10-22+20"
				+ "%3A49%3A31&is_total_fee_adjust=N&gmt_payment=2008-10-22+20%3A49%3"
				+ "A50&gmt_close=2008-10-22+20%3A49%3A46&gmt_refund=2008-10-29+19%3"
				+ "A38%3A25&use_coupon=N&notify_time=2009-08-12+11%3A08%3A32&notify_"
				+ "type=trade_status_sync&notify_id=70fec0c2730b27528665af4517c27b95&sign"
				+ "_type=MD5";

		PaymentGatewayConfig config = PaymentGatewayConfig.instance();
		Map<String, String> params = HttpUtil.urlDecode(query);
		String sign = SignatureUtil.sign(params, new MD5Signature(config.getMerchantKey()));
		params.put("sign", sign);
		
		String request = HttpUtil.urlEncode(params);
		Notification n = new Notification(request);
		
		TestCase.assertEquals("3618810634349901", n.getOrderId());
		TestCase.assertEquals("2008102203208746", n.getAlipayOrderId());
		TestCase.assertEquals("10.00", n.getAmount());
		TestCase.assertEquals("70fec0c2730b27528665af4517c27b95", n.getNotificationId());
	}
	
	@Test
	public void testSyncNotification() throws IOException {
		String msg = "is_success=T&sign_type=MD5&body=Hello&buyer_email=xinjie_xj%4"
				+ "0163.com&buyer_id=2088101000082594&exterface=create_direct_pay_by_use"
				+ "r&out_trade_no=6402757654153618&payment_type=1&seller_email=chao.che"
				+ "nc1%40alipay.com&seller_id=2088002007018916&subject=%E5%A4%96%E9"
				+ "%83%A8FP&total_fee=10.00&trade_no=2008102303210710&trade_status=TR"
				+ "ADE_FINISHED&notify_id=RqPnCoPT3K9%252Fvwbh3I%252BODmZS9o4qC"
				+ "hHwPWbaS7UMBJpUnBJlzg42y9A8gQlzU6m3fOhG&notify_time=2008-10-23+"
				+ "13%3A17%3A39&notify_type=trade_status_sync&extra_common_param=%E4"
				+ "%BD%A0%E5%A5%BD%EF%BC%8C%E8%BF%99%E6%98%AF%E6%B5%"
				+ "8B%E8%AF%95%E5%95%86%E6%88%B7%E7%9A%84%E5%B9%BF%E5%"
				+ "91%8A%E3%80%82";
		
		PaymentGatewayConfig config = PaymentGatewayConfig.instance();
		Map<String, String> params = HttpUtil.urlDecode(msg);
		String sign = SignatureUtil.sign(params, new MD5Signature(config.getMerchantKey()));
		params.put("sign", sign);
		
		String request = HttpUtil.urlEncode(params);
		Notification n = new Notification(request);
		
		TestCase.assertEquals("6402757654153618", n.getOrderId());
		TestCase.assertEquals("2008102303210710", n.getAlipayOrderId());
		TestCase.assertEquals("10.00", n.getAmount());
		
		String id = URLDecoder.decode("RqPnCoPT3K9%252Fvwbh3I%252BODmZS9o4qChHwPWbaS7UMBJpUnBJlzg42y9A8gQlzU6m3fOhG", "UTF-8");
		TestCase.assertEquals(id, n.getNotificationId());
	}
}
