package alipay;

import java.math.BigDecimal;

import models.StatusEnum;

public class PurchaseOrder {
	
	private String subject;
	private String description;
	private BigDecimal amount;
	private String id;
	private OrderStatus status;
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("{").append("id=").append(id)
		.append("subject=").append(subject)
		.append("desc=").append(description)
		.append("amount=").append(amount ==null ? "": amount.toPlainString())
		.append("status=").append(status).append("}");
		return sb.toString();
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}

}
