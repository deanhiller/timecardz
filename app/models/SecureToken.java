package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SecureToken {

	@Id
	private String secureToken;

	private Integer value;

	public String getSecureToken() {
		return secureToken;
	}

	public void setSecureToken(String secureToken) {
		this.secureToken = secureToken;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
