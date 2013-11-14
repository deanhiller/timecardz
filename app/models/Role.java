package models;

import java.util.HashMap;
import java.util.Map;

public enum Role {
	ADMIN("admin"), USER("user");
	
	private static Map<String, Role> types = new HashMap<String, Role>();
	static {
		for(Role type : Role.values())
			types.put(type.getDatabaseCode(), type);
	}
	
	private String databaseCode;
	
	private Role(String type) {
		this.databaseCode = type;
	}
	
	public String getDatabaseCode() {
		return databaseCode;
	}

	public static Role translate(String type) {
		return types.get(type);
	}
}
