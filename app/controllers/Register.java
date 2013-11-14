package controllers;

import models.CompanyDbo;
import models.EntityDbo;
import models.Role;
import models.SoftwareType;
import models.Token;
import models.UserDbo;
import play.db.jpa.JPA;
import play.mvc.Controller;
import controllers.auth.Secure;

public class Register extends Controller {

	public static void postNewAppRegister(UserDbo user,String type,String company) throws Throwable {
			validation.required(user.getEmail());
			validation.required(company);
			if (company == null) {
				validation.addError("company", "company must be supplied");
			}
			if (user.getPassword() == null) {
				validation.addError("user.password", "Password must be supplied");
			}
			if (!(user.getEmail().contains("@")))
				validation.addError("user.email", "This is not a valid email");
			if (Register.emailAlreadyExists(user.getEmail())) {
				validation.addError("user.email", "This email is already in use");
			}
			if (validation.hasErrors()) {
				params.flash(); // add http parameters to the flash scope
				validation.keep(); // keep the errors for the next request
				Register.register();
			}
			
			SoftwareType softwareType = SoftwareType.translate(type);
			
			CompanyDbo companyDbo = new CompanyDbo();
			companyDbo.setName(company);
			companyDbo.setSoftwareType(softwareType);
			user.setManager(user);
			user.setRole(Role.ADMIN);
			user.setCompany(companyDbo);			
			companyDbo.addUser(user);
			
			JPA.em().persist(companyDbo);
			JPA.em().persist(user);
			JPA.em().flush();
			
			if(user.getEmail().endsWith("@deanstest.com")) {
				Secure.addUserToSession(user.getEmail());
				NewApp.clockInOut();
			}
			
			Application.overloaded();
		}
	
	public static void addedUserRegister(String token) {
		Token tkn = JPA.em().find(Token.class, token);
		UserDbo user = tkn.getUser();
		String email = user.getEmail();
		long sendmailtime = tkn.getTime();
		long logintime = System.currentTimeMillis();
		long duration = (7 * 24 * 60 * 60);
		long interval = logintime - sendmailtime;
		validation.required(interval);
		if (interval > duration) {
			validation.addError("interval",
					"Plese request the admin to send the message again");

		} else {
			render(user);
		}
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			Application.index();
		}
	}

	public static void postUserRegister(UserDbo user) {
		Integer id = null;
		validation.required(user.getEmail());
		if (user.getPassword() == null) {
			validation.addError("password", "Password must be supplied");
		}
		if (!user.getEmail().contains("@"))
			validation.addError("email", "This is not a valid email");
		Boolean existing = Register.emailAlreadyExists(user.getEmail());
		if (!existing) {
			validation
					.addError("email", "This email is not registered with us");
		}

		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			// postUserRegister();
		}
		/*
		 * // don't think these are needed user=UserDbo.findByEmailId(JPA.em(),
		 * user.getEmail()); user.setEmail(user.getEmail());
		 * user.setPassword(user.getPassword()); user.setFirstName();
		 * user.setLastName(lastName); user.setPhone(phone);
  		 * user.setAdmin(false);
  		 */
		
		JPA.em().flush();
		Secure.addUserToSession(user.getEmail());
		OtherStuff.home(id);
		//NewApp.companyAlias();
	}

	public static boolean emailAlreadyExists(String email) {
		UserDbo otherUser = UserDbo.findByEmailId(JPA.em(), email);
		if (otherUser == null)
			return false;
		return true;
	}

	public static void register() {
		render();
	}

}
