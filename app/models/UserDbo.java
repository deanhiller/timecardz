package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Query;

import org.hibernate.annotations.Index;

import play.db.jpa.Model;

@Entity
@NamedQueries({
	@NamedQuery(name = "findAllField", query = "select u from UserDbo as u"),
	@NamedQuery(name = "findByEmailId", query = "select u from UserDbo as u where u.email=:email") })
public class UserDbo {

	@Id
	@GeneratedValue
	private Integer id;

	@Index(name="entityIndexColumn")
	@Column(unique = true)
	private String email;

	private String password;

	private String firstName;

	private String lastName;

	@OneToOne(mappedBy = "user")
	public Token token;

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	private String phone;

	private boolean isAdmin;

	private String role;;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	private boolean isNewPasswordChange;

	private String endOfWeek;

	public boolean isNewPasswordChange() {
		return isNewPasswordChange;
	}

	public void setNewPasswordChange(boolean isNewPasswordChange) {
		this.isNewPasswordChange = isNewPasswordChange;
	}

	private String beginDayOfWeek;

	private String getEmailYesOrNo;

	public String getEndOfWeek() {
		return endOfWeek;
	}

	public void setEndOfWeek(String endOfWeek) {
		this.endOfWeek = endOfWeek;
	}

	public String getBeginDayOfWeek() {
		return beginDayOfWeek;
	}

	public void setBeginDayOfWeek(String beginDayOfWeek) {
		this.beginDayOfWeek = beginDayOfWeek;
	}

	public String isGetEmailYesOrNo() {
		return getEmailYesOrNo;
	}

	public void setGetEmailYesOrNo(String getEmailYesOrNo) {
		this.getEmailYesOrNo = getEmailYesOrNo;
	}

	@ManyToOne
	private CompanyDbo company;

	@ManyToOne
	private UserDbo manager;

	public List<TimeCardDbo> getTimecards() {
		return timecards;
	}

	public void setTimecards(List<TimeCardDbo> timecards) {
		this.timecards = timecards;
	}

	@OneToMany(mappedBy = "manager")
	private List<UserDbo> employees = new ArrayList<UserDbo>();

	@OneToMany
	private List<TimeCardDbo> timecards = new ArrayList<TimeCardDbo>();

	public Integer getId() {
		return id;
	}

	public List<UserDbo> getEmployees() {
		return employees;
	}

	public void setEmployees(List<UserDbo> employees) {
		this.employees = employees;
	}

	public void addEmployee(UserDbo employee) {
		this.employees.add(employee);
	}

	public void deleteEmployee(UserDbo employee) {
		this.employees.remove(employee);
	}
	public void deleteTimeCard(TimeCardDbo timecards) {
		this.timecards.remove(timecards);
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CompanyDbo getCompany() {
		return company;
	}

	public void setCompany(CompanyDbo company) {
		this.company = company;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public UserDbo getManager() {
		return manager;
	}

	public void setManager(UserDbo manager) {
		this.manager = manager;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public void addTimecards(TimeCardDbo timecard) {
		this.timecards.add(timecard);
	}
	
	public static List<UserDbo> findAllField(EntityManager mgr) {
		Query query = mgr.createNamedQuery("findAll");
		return query.getResultList();
	}

	public static UserDbo findByEmailId(EntityManager mgr, String email) {
		Query query = mgr.createNamedQuery("findByEmailId");
		query.setParameter("email", email);
		try {
			return (UserDbo) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
