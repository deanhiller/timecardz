package alipay.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.libs.WS.HttpResponse;
import play.libs.ws.WSUrlFetch;

public class HttpUtil {
	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	private static final String ENC = "UTF-8";

	/**
	 * decode url query to map.
	 * @param urlEncodedString
	 * @return
	 */
	public static Map<String, String> urlDecode(String urlEncodedString) {
		try {
			String[] nvps = urlEncodedString.split("&");
			Map<String, String> pairs = new HashMap<String, String>();
			for (String nvp : nvps) {
				String[] pair = nvp.split("=");
				String name = URLDecoder.decode(pair[0], ENC);
				String value = null;
				if (pair.length > 1) {
					value = URLDecoder.decode(pair[1], ENC);
				}
				pairs.put(name, value);
			}
			return pairs;
		} catch (UnsupportedEncodingException e) {
			logger.error("unsupported encoding", e);
			throw new AssertionError();
		}
	}

	/**
	 * Encode map to url query string.
	 * @param params
	 * @return
	 */
	public static String urlEncode(Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		if (params != null) {
			for (Map.Entry<String, String> param : params.entrySet()) {
				if (sb.length() > 0) {
					sb.append('&');
				}
				try {
					sb.append(URLEncoder.encode(param.getKey(), ENC)).append(
							'=');

					String value = param.getValue();
					if (null == value) {
						value = "";
					}

					sb.append(URLEncoder.encode(value, ENC));
				} catch (UnsupportedEncodingException e) {
					logger.error("unsupported encoding", e);
					throw new AssertionError();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Simple GET to server.
	 * @param urlvalue
	 * @return
	 */
    public static String doGet(String urlvalue) {
    		WSUrlFetch wsu = new WSUrlFetch();
    		HttpResponse response = wsu.newRequest(urlvalue,"utf-8").get();
    		String body = response.getString();
    		return body;
    }
    
	/**
	 * Simple POST to server.
	 * @param urlvalue
	 * @return
	 */
    public static String doPost(String urlvalue) {
    		WSUrlFetch wsu = new WSUrlFetch();
    		HttpResponse response = wsu.newRequest(urlvalue,"utf-8").post();
    		String body = response.getString();
    		return body;
    }
}
