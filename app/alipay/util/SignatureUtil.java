package alipay.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import alipay.signature.MD5Signature;

public class SignatureUtil {

		public static String getSignData(Map<String, String> params) {
			StringBuffer content = new StringBuffer();

			List<String> keys = new ArrayList<String>(params.keySet());
			Collections.sort(keys);

			for (int i = 0; i < keys.size(); i++) {
				String key = (String) keys.get(i);
				if ("sign".equals(key) || "sign_type".equals(key)) {
					continue;
				}
				String value = (String) params.get(key);
				if (value != null) {
					content.append((i == 0 ? "" : "&") + key + "=" + value);
				} else {
					content.append((i == 0 ? "" : "&") + key + "=");
				}

			}

			return content.toString();
		}

		public static String sign(Map<String, String> params, MD5Signature signature)
				throws IOException {
			String signData = getSignData(params);
			return signature.sign(signData);
		}
		
		public static boolean checkSign(String signData, String sign,
				MD5Signature signature) throws IOException {
			return signature.verify(signData, sign);
		}

		public static boolean checkSign(Map<String, String> params, String sign,
				MD5Signature signature) throws IOException {
			String signData = getSignData(params);
			return signature.verify(signData, sign);
		}
		
		public static boolean checkSign(Map<String, String> params,
				MD5Signature signature) throws IOException {
			String sign = params.get("sign");
			String signData = getSignData(params);
			return signature.verify(signData, sign);
		}
	}
