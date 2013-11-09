package controllers;

import java.util.List;

import models.CompanyDbo;
import models.Token;
import models.UserDbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.auth.Secure;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class UserAddition extends Controller {
	private static final Logger log = LoggerFactory.getLogger(UserAddition.class);

	public static void ajaxAddEdit(Integer id) {
		UserDbo admin = Utility.fetchUser();
		CompanyDbo company = admin.getCompany();
		log.info("Adding users by Admin = " + admin.getEmail()
				+ " and Company = " + company.getName());
		List<UserDbo> users = company.getUsers();
		render(admin, company, users);

	}

	public static void postUserAddition(String useremail, String manager,String role)
			throws Throwable {
		validation.required(useremail);
		if (!useremail.contains("@"))
			validation.addError("useremail", "This is not a valid email");
		Boolean existing = Register.emailAlreadyExists(useremail);
		if (existing) {
			validation.addError("useremail", "This email already exists");
		}
		if(validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep();
			flash.error("Your form has errors");
			flash.put("showPopup", "true");
			listUsers();
		}
		UserDbo admin = Utility.fetchUser();
		CompanyDbo company = admin.getCompany();
		UserDbo user = new UserDbo();
		user.setEmail(useremail);
		user.setRole(role);
		user.setCompany(company);
		if (manager == null) {
			// If there is no manager, add the current user as Manager
			user.setManager(admin);
		} else {
			UserDbo adminDbo = UserDbo.findByEmailId(JPA.em(), manager);
			user.setManager(adminDbo);
		}
		JPA.em().persist(user);
		company.addUser(user);
		JPA.em().persist(company);
		JPA.em().flush();
		String key = Utility.generateKey();
		Token token = new Token();
		long timestamp = System.currentTimeMillis();
		token.setTime(timestamp);
		token.setToken(key);
		token.setUser(user);
		JPA.em().persist(token);
		JPA.em().flush();
		Utility.sendEmail(useremail, company.getName(), key);
		UserAddition.listUsers();
	}

	public static void postAddition(String name, String address, String phone,
			String detail) throws Throwable {
		Integer id = null;
		validation.required(name);
		UserDbo user = Utility.fetchUser();

		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			// addCompany();
		}
		CompanyDbo company = new CompanyDbo();
		company.setName(name);
		company.setAddress(address);
		company.setPhoneNumber(phone);
		company.setDescription(detail);
		company.addUser(user);
		user.setCompany(company);
		JPA.em().persist(company);
		JPA.em().persist(user);
		JPA.em().flush();
		OtherStuff.home(id);
	}

	public static void listUsers() {
		UserDbo admin = Utility.fetchUser();
		CompanyDbo company = admin.getCompany();
		OtherStuff.log.info("Adding users by Admin = " + admin.getEmail()
				+ " and Company = " + company.getName());
		List<UserDbo> users = company.getUsers();
		render(admin, company, users);
	}
}
