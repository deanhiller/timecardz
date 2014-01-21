package app.alipay;

import junit.framework.TestCase;

import org.junit.Test;

import alipay.SendGoodsResult;

public class SendGoodsResultTest {
	
	@Test
	public void testConstruction(){
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?> " +
				"<alipay> " +
				"<is_success>T</is_success> " +
				"<request> " +
				"<param name=\"partner\">2088002007018916</param> " +
				"<param name=\"logistics_name\">天天</param> " +
				"<param name=\"create_transport_type\">EMS</param> " +
				"<param name=\"trade_no\">2008040902681748</param> " +
				"<param name=\"agent\">2088002007018916</param> " +
				"<param name=\"notify_url\">http://10.2.5.100/api/apireceive/returnSuccess.php</param> " +
				"<param name=\"invoice_no\">3455333</param> " +
				"<param name=\"service\">send_goods_confirm_by_platform</param> " +
				"<param name=\"_input_charset\">utf-8</param> " +
				"<param name=\"transport_type\">EMS</param> " +
				"<param name=\"return_url\">http://10.2.5.100/api/returnResultList.php</param> " +
				"</request> " +
				"<response> " +
				"<tradeBase> " +
				"<buyer_account>20880020073014230156</buyer_account> " +
				"<buyer_actions>[REFUND,CONFIRM_GOODS]</buyer_actions> " +
				"<buyer_login_email>maaimin0577@yahoo.com.cn</buyer_login_email> " +
				"<buyer_type>PRIVATE_ACCOUNT</buyer_type> " +
				"<buyer_user_id>2088002007301423</buyer_user_id> " +
				"<channel>interface/digital</channel> " +
				"<create_time>2008-04-09 16:10:25</create_time> " +
				"<currency>156</currency> " +
				"<gathering_type>1</gathering_type> " +
				"<last_modified_time>2008-04-10 14:35:25</last_modified_time> " +
				"<operator_role>B</operator_role> " +
				"<out_trade_no>12345566654585</out_trade_no> " +
				"<partner_id>2088002007018916</partner_id> " +
				"<seller_account>20880020073014100156</seller_account> " +
				"<seller_actions>[EXTEND_TIMEOUT]</seller_actions> " +
				"<seller_login_email>song_xianqun@yahoo.com.cn</seller_login_email> " +
				"<seller_type>PRIVATE_ACCOUNT</seller_type> " +
				"<seller_user_id>2088002007301410</seller_user_id> " +
				"<service_fee>0.00</service_fee> " +
				"<service_fee_ratio>0.0</service_fee_ratio> " +
				"<stop_timeout>F</stop_timeout> " +
				"<total_fee>2.00</total_fee> " +
				"<trade_from>INST_PARTNER</trade_from> " +
				"<trade_no>2008040902681748</trade_no> " +
				"<trade_status>WAIT_BUYER_CONFIRM_GOODS</trade_status> " +
				"<trade_type>S</trade_type> " +
				"</tradeBase> " +
				"</response> " +
				"<sign>eb07c7407bafa62ec7c0804751a21c1e</sign> " +
				"<sign_type>MD5</sign_type> " +
				"</alipay>" ;
		
		SendGoodsResult result = new SendGoodsResult(xml);
		
		TestCase.assertTrue(result.isTranSuccessful());
		
		System.out.println(result.getParams());
		TestCase.assertTrue(result.isCompleted());
		TestCase.assertNull(result.getErrorMessage());
		
	}

}
