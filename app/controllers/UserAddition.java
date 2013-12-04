package controllers;

import java.util.List;

import models.CompanyDbo;
import models.Role;
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
		admin.setRole(Role.ADMIN);
		CompanyDbo company = admin.getCompany();
		log.info("Adding users by Admin = " + admin.getEmail()
				+ " and Company = " + company.getName());
		List<UserDbo> users = company.getUsers();
		render(admin, company, users);

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
		if (admin != null) {
			boolean checkAdmin = admin.isAdmin();
			if (checkAdmin) {
				CompanyDbo company = admin.getCompany();
				OtherStuff.log.info("Adding users by Admin = "
						+ admin.getEmail() + " and Company = "
						+ company.getName());
				List<UserDbo> users = company.getUsers();
				render(admin, company, users);
			}
			flash.success("Oops! You are not manager.Sign-in as manager for accessing Time Card and adding the users");
			Application.login();
		}
		flash.success("Oops! No User is Signed-In.Sign-in for accessing Time Card and adding the users");
		Application.login();
	}
}
