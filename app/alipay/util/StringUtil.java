package alipay.util;

public class StringUtil {

	public static boolean isEmpty(String s1) {
		return (s1 == null || s1.length()==0);
	}

	public static boolean equals(String s1, String s2) {
		if(s1 == null ){
			return s2 == null;
		}else{
			return s1.equals(s2);
		}
	}
	
}
