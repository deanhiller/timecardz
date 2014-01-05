package controllers;

import java.util.HashMap;
import java.util.Map;

import play.mvc.Controller;
import play.mvc.Router;

public class Payment extends Controller {

	public static void payment() {
		//Setup the confirmation url that alipay can direct back to after we redirect to them
		String url = createConfirmUrl();

		//Here, get some kind of payment token from payment vendor
		String token = fetchToken(url);

		render(token);
	}

	private static String createConfirmUrl() {
		Map<String, Object> map = new HashMap<String, Object>();
		String baseUrl = request.getBase();
		//This creates the 
		String url = baseUrl+Router.reverse("Payment.confirm", map).url;
		return url;
	}

	private static String fetchToken(String url) {
		return "ASDFSDFFSFDSFDWEREWR";
	}
	
	public static void confirm() {
		
		render();
	}

	public static void postConfirmPayment() {
		
		//Here we need to call back to alipay.com and tell them to complete the transaction and we will save XXX to our database
		
		finished();
	}
	
	public static void finished() {
		render();
	}
}
