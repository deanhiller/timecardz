package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Controller;
import play.mvc.Router;

public class Payment extends Controller {

	private static final Logger log = LoggerFactory.getLogger(Payment.class);
	private static Random r = new Random(System.currentTimeMillis());
	private static int counter = 0;
	
	public static void payment() {
		render();
	}

	public static void purchase() {
		String txId = generateUniqueId()+"";
		String callbackUrl = createCallbackUrl(txId);
		String finishUrl = createFinishUrl(txId);

		//I am not sure this works as I am not sure if we need to escape stuff in the callback urls
		//ESPECIALL if the callback urls have paramaters like ?param1=val1&param2=val2
		//We can completely avoid paramaters by changing a few things though so let me know if you need
		//callbackUrl to have paramaters!!! and finishUrl too
		String url = "http://alipay.com/api/asdfd?url1="+callbackUrl+"&url2="+finishUrl;
		redirect(url);
	}

	private static synchronized String generateUniqueId() {
		//random makes it harder to crack and only lives for around 60 minutes before being destroyed....
		long id = r.nextLong();
		long abs = Math.abs(id);
		return counter+"-"+abs;
	}

	private static String createCallbackUrl(String txId) {
		Map<String, Object> urlParamsMap = new HashMap<String, Object>();
		urlParamsMap.put("txId", txId);
		String baseUrl = request.getBase();
		//This creates the 
		String url = baseUrl+Router.reverse("Payment.callback", urlParamsMap).url;
		return url;
	}

	private static String createFinishUrl(String txId) {
		Map<String, Object> urlParamsMap = new HashMap<String, Object>();
		urlParamsMap.put("txId", txId);
		String baseUrl = request.getBase();
		//This creates the 
		String url = baseUrl+Router.reverse("Payment.finished", urlParamsMap).url;
		return url;
	}
	
	public static void callback(String txId) {
		markAsFinished(txId);
		render();
	}
	
	public static void finished(String txId) {
		markAsFinished(txId);
		render();
	}

	private static void markAsFinished(String txId) {
		log.info("transaction id="+txId+" is complete and paid");
	}
}
