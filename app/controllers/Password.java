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

	
	public static void sendEmailForPasswordReset(String emailId,String key) {
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
			email.setMsg(" Hi,\n  Please Click  " + signupUrl+ "resetPassword/" + key + " "+ " for reseting the password of "+ " email-id  "+""+emailId+". \n Best Regards");
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

	public static void alreadyChanged() {
		render();
	}

}
