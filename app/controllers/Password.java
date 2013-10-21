package controllers;

import models.Token;
import models.UserDbo;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.db.jpa.JPA;
import play.libs.Mail;
import play.mvc.Controller;
import play.mvc.With;
import controllers.auth.Secure;

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
		if(existing)
			{
			Utility.sendEmailForPassowdReset(email,key);
			}
		else{
			validation.addError("email", "email does not exists");
		}
			

	}

	public static void changePassword() {
		render();
	}

	public static void resetPassword(String key) {
		Token token=JPA.em().find(Token.class, key);
		UserDbo otherUser = UserDbo.findByEmailId(JPA.em(), token.getEmail());
        String emailId=otherUser.getEmail();
		if (otherUser.isNewPasswordChange()) {
			alreadyChanged();
		}
		render(emailId, reset);
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
