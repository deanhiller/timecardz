package controllers;

import models.Token;
import models.UserDbo;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.db.jpa.JPA;
import play.libs.Mail;
import play.mvc.Controller;
import play.mvc.With;
import controllers.auth.Secure;
import controllers.auth.Secure.Security;

public class Password extends Controller {
	private static final Logger log = LoggerFactory.getLogger(Password.class);
	static boolean reset = false;

	public static void changePass(String email) {
		validation.required(email);
		if(email==null){
			validation.addError("email", "email must be supplied");
		}
		String key = Utility.generateKey();
		Token token = new Token();
		long timestamp = System.currentTimeMillis();
		token.setTime(timestamp);
		token.setToken(key);
		token.setEmail(email);
		JPA.em().persist(token);
		JPA.em().flush();
		boolean existing=Register.emailAlreadyExists(email);
		if (existing) {
			sendEmailForPassowdReset(email, key);
		} else{
			validation.addError("email", "email does not exists");
		}
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			changePassword();
		}
 }
	public static void sendEmailForPassowdReset(String emailId,String key) {
		String mode = Play.configuration.getProperty("application.mode");
		String port = Play.configuration.getProperty("http.port");
		String signupUrl = "null";
		if ("dev".equals(mode)) {
			signupUrl = Play.configuration.getProperty("dev.signupUrl");
			signupUrl = signupUrl + ":" + port + "/";
		} else {
			signupUrl = Play.configuration.getProperty("prod.signupUrl");
		}
		UserDbo user = UserDbo.findByEmailId(JPA.em(), emailId);
		SimpleEmail email = new SimpleEmail();
		try {
			user.setNewPasswordChange(false);
			JPA.em().persist(user);
			JPA.em().flush();
			email.setFrom("no-reply@tbd.com");
			email.addTo(emailId);
			email.setSubject("Link for Password Reset");
			email.setMsg(" Hi,\n  Please go to " + signupUrl+ "resetPassword/" + key + " "+ "and reset the password. \n Best Regards");
			Mail.send(email);
		    flash.success("We sent you an email to change your password");
		} catch (EmailException e) {
			log.error("ERROR in sending mail to " + emailId);
			e.printStackTrace();
		}
		Application.login();
	}
	public static void changePassword() {
		render();
	}

	public static void resetPassword(String key) {
		boolean nullKey = false;
		if (key != null) {
			Token token = JPA.em().find(Token.class, key);
			if (token != null) {
				UserDbo otherUser = UserDbo.findByEmailId(JPA.em(),
						token.getEmail());
				String emailId = otherUser.getEmail();
				if (otherUser.isNewPasswordChange()) {
					alreadyChanged();
				}
				render(emailId, reset);
			} else {
				nullKey = true;
				render(nullKey);
			}
		} else {
			nullKey = true;
			render(nullKey);
		}
	}

	public static void change(String emailId, String password,
			String verifyPassword) {
		validation.required(emailId);
		Boolean existing = Register.emailAlreadyExists(emailId);
		if (!existing) {
			validation.addError("email", "This email is not registered with us");
		}
		if (password == null) {
			validation.addError("password", "Password must be supplied");
		}
		if (!password.equals(verifyPassword)) {
			validation.addError("verifyPassword", "Passwords did not match");
		}
		UserDbo otherUser = UserDbo.findByEmailId(JPA.em(), emailId);
		otherUser.setPassword(password);
		otherUser.setNewPasswordChange(true);
		JPA.em().persist(otherUser);
		JPA.em().flush();
		render();

	}

	public static void alreadyChanged() {
		render();
	}

}
