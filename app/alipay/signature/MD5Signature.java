package alipay.signature;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;

import alipay.util.StringUtil;

public class MD5Signature {

	private String key;

	public MD5Signature(String key) {
		this.key = key;
	}

	public String sign(String content) throws IOException {
		String tosign = (content == null ? "" : content) + key;
		try {
			return DigestUtils.md5Hex(getContentBytes(tosign, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IOException("MD5 signature[content = " + content
					+ "; charset = utf-8" + "]ERROR!", e);
		}
	}

	private byte[] getContentBytes(String content, String charset)
			throws UnsupportedEncodingException {
		if (StringUtil.isEmpty(charset)) {
			return content.getBytes();
		}

		return content.getBytes(charset);
	}

	public boolean verify(String content, String sign) throws IOException {
		String tosign = (content == null ? "" : content) + key;

		try {
			String mySign = DigestUtils.md5Hex(getContentBytes(tosign, "utf-8"));
			return StringUtil.equals(mySign, sign) ? true : false;
		} catch (UnsupportedEncodingException e) {
			throw new IOException("MD5 signature[content = " + content
					+ "; charset =utf-8 " + "; signature = " + sign + "]ERROR!",
					e);
		}
	}
}
