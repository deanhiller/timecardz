package controllers;

import models.CompanyDbo;
import models.Token;
import models.UserDbo;
import play.db.jpa.JPA;
import play.mvc.Controller;
import controllers.auth.Secure;

public class Register extends Controller {
	// This is for company admin
	public static void postRegister(String company, String email,
			String password, String verifyPassword) throws Throwable {
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
		if (emailAlreadyExists(email)) {
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
		OtherStuff.setupWizard();
	}
	
	public static void addedUserRegister(String token) {
		Token tkn = JPA.em().find(Token.class, token);
		UserDbo user=tkn.getUser();
		String email=user.getEmail();
		long sendmailtime = tkn.getTime();
		long logintime = System.currentTimeMillis();
		long duration = (7 * 24 * 60 * 60);
		long interval = logintime - sendmailtime;
		validation.required(interval);
		if (interval > duration) {
			validation.addError("interval","Plese request the admin to send the message again");

		} else {
			render(email);
		}
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			Application.index();
		}
	}
	
	public static void postUserRegister(String email, String password,
			String verifyPassword, String firstName, String lastName,
			String phone) {
		Integer id=null;
		validation.required(email);
		if (password == null) {
			validation.addError("password", "Password must be supplied");
		}
		if (!password.equals(verifyPassword)) {
			validation.addError("verifyPassword", "Passwords did not match");
		}
		if (!email.contains("@"))
			validation.addError("email", "This is not a valid email");
		Boolean existing = Register.emailAlreadyExists(email);
		if (!existing ) {
			validation.addError("email", "This email is not registered with us");
		}

		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			//postUserRegister();
		}
		UserDbo user=UserDbo.findByEmailId(JPA.em(), email);
		user.setEmail(email);
		user.setPassword(password);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPhone(phone);
		user.setAdmin(false);
		JPA.em().persist(user);
		JPA.em().flush();
		Secure.addUserToSession(user.getEmail());
		OtherStuff.home(id);
	}
	public static boolean emailAlreadyExists(String  email) {
		UserDbo otherUser = UserDbo.findByEmailId(JPA.em(), email);
		if(otherUser == null)
			return false;
		return true;
	}

	
}
