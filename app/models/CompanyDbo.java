package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class CompanyDbo {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Integer id;

	@OneToMany(mappedBy="company")
	private List<UserDbo> users = new ArrayList<UserDbo>();

	private String name;

	private String description;

	@Column(nullable=false)
	private String softwareType;
	
	private String beginDayOfWeek;

	private String getEmailYesOrNo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UserDbo> getUsers() {
		return users;
	}

	public void setUsers(List<UserDbo> users) {
		this.users = users;
	}

	public void addUser(UserDbo userDbo) {
		this.users.add(userDbo);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setSoftwareType(SoftwareType type) {
		this.softwareType = type.getDatabaseCode();
	}
	
	public SoftwareType getSoftwareType() {
		return SoftwareType.translate(softwareType);
	}

	public String getBeginDayOfWeek() {
		return beginDayOfWeek;
	}

	public void setBeginDayOfWeek(String beginDayOfWeek) {
		this.beginDayOfWeek = beginDayOfWeek;
	}

	public String getGetEmailYesOrNo() {
		return getEmailYesOrNo;
	}

	public void setGetEmailYesOrNo(String getEmailYesOrNo) {
		this.getEmailYesOrNo = getEmailYesOrNo;
	}

}
