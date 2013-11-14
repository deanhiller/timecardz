package models;

import java.util.HashMap;
import java.util.Map;

public enum SoftwareType {
	CLOCK_IN("clockin"), CONTRACTOR("contractor"), MANAGER_APPROVES("approval");
	
	private static Map<String, SoftwareType> types = new HashMap<String, SoftwareType>();
	static {
		for(SoftwareType type : SoftwareType.values())
			types.put(type.getDatabaseCode(), type);
	}
	
	private String databaseCode;
	
	private SoftwareType(String type) {
		this.databaseCode = type;
	}
	
	public String getDatabaseCode() {
		return databaseCode;
	}

	public static SoftwareType translate(String type) {
		return types.get(type);
	}
}
