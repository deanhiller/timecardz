package alipay;

public class PaymentGatewayConfig {

	private static PaymentGatewayConfig instance;
	
	private String merchantId;
	private String merchantKey;
	private String returnUrl;
	private String notifyUrl;
	

	public synchronized static PaymentGatewayConfig instance(){
		if(instance == null){
			instance = new PaymentGatewayConfig();
		}
		return instance;
	}
	
	private PaymentGatewayConfig(){
		//TODO: please populate with real credentials and server urls.
		setMerchantId("2088002007018916");
		setMerchantKey("secret");
		setReturnUrl(null);
		setNotifyUrl(null);
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantKey() {
		return merchantKey;
	}

	public void setMerchantKey(String merchantKey) {
		this.merchantKey = merchantKey;
	}
}
