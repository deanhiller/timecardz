package controllers;

import models.CompanyDbo;
import models.UserDbo;
import controllers.auth.Secure;
import play.db.jpa.JPA;
import play.mvc.Controller;

public class AppRegister extends Controller{
	public static void postRegister(String company, String email,
		String password, String verifyPassword,String selectRadio) throws Throwable {
		validation.required(email);
		validation.required(company);
		if (company == null) {
			validation.addError("company", "company must be supplied");
		}
		if (password == null) {
			validation.addError("password", "Password must be supplied");
		}
		if (!password.equals(verifyPassword)) {
			validation.addError("verifyPassword", "Passwords did not match");
		}
		if (!email.contains("@"))
			validation.addError("email", "This is not a valid email");
		if (Register.emailAlreadyExists(email)) {
			validation.addError("user.email", "This email is already in use");
		}
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			Application.register();
		}
		CompanyDbo companyDbo = new CompanyDbo();
		companyDbo.setName(company);
		UserDbo user = new UserDbo();
		user.setEmail(email);
		user.setPassword(password);
		user.setManager(user);
		user.setAdmin(true);
		user.setBeginDayOfWeek("Monday");
		user.setGetEmailYesOrNo("Yes");
		companyDbo.addUser(user);
		user.setCompany(companyDbo);
		JPA.em().persist(companyDbo);
		JPA.em().persist(user);
		JPA.em().flush();
		Secure.addUserToSession(user.getEmail());
		NewApp.contractor();
	}
	

}
