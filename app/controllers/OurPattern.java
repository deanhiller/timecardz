package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import models.DayCardDbo;
import models.EmailToUserDbo;
import models.StatusEnum;
import models.TimeCardDbo;
import models.Token;

import models.UserDbo;

import models.CompanyDbo;

import controllers.auth.Check;
import controllers.auth.Secure;
import controllers.auth.Secure.Security;
import play.Play;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.libs.Crypto;
import play.libs.Time;
import play.mvc.Controller;
import play.mvc.With;
import play.mvc.Scope.Session;

public class OurPattern extends Controller {

	private static final Logger log = LoggerFactory.getLogger(OurPattern.class);
	
	//NOTE: Some of the patterns I use in here are horrible.....like looping over a List instead of just using
	//a HashMap.....bleck, but for now, we are just demonstrating the ajax pattern we want to copy 1000 times.
	//This is what we list in the web page..
	private static List<UserDbo> ourUsers = new ArrayList<UserDbo>();
	
	public static void listUsers() {
		List<UserDbo> users = ourUsers;
		String val = flash.get("showPopup");
		boolean showPopup = false;
		if("true".equals(val))
			showPopup = true;
		render(users, showPopup);
	}

	public static void ajaxUser(Integer id) {
		if(id == null) { //this is a new user(ie. user add
			//use user?.name, user?.email, etc. etc. since user==null
			render();
		}
		UserDbo user = callDatabaseGetUser(id);
		render(user);
	}

	public static void postUser(UserDbo user,Integer id) {
		
		if(user.getEmail().equals("")){
			validation.addError("user.email", "Please enter an email id");
		flash.error("Your form has errors");
		}
		if(emailExistsAlready(user.getEmail())) {
			//this puts a message under the actual field and causes the label to turn red and the input border turns
			//red, etc. etc. (at least when done correctly)
			validation.addError("user.email", "This email is already in use");
			//this puts a message at the top of the form!!!!
			flash.error("Your form has errors");
		}
		
		//we always allow errors to queue up to show all errors at once for the user in case they have a few errors on
		//their form
		if(validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep();
			//flash.put("showPopup", "true");
			listUsers();
		}
		if(id==null){
		JPA.em().persist(user);
		JPA.em().flush();}
		postUserToDatabase(user,id);
		listUsers();
	}
	
	private static boolean emailExistsAlready(String email) {
		for(UserDbo user : ourUsers) {
			if(email.equals(user.getEmail()))
				return true;
		}
		return false;
	}

	private static void postUserToDatabase(UserDbo user, Integer id) {
		if (id == null) {
			ourUsers.add(user);
		} else {
			for (UserDbo  ourUser: ourUsers) {
				if (ourUser.getId() == id) {
					ourUser.setId(id);
					ourUser.setEmail(user.getEmail());
				}
			}
			listUsers();
		}
	}

	private static UserDbo callDatabaseGetUser(int id) {
		// normally we would call into database for the purposes of this patter,
		// it does not matter
		for (UserDbo user : ourUsers) {
			if (user.getId() == id) {
				return user;
			}
		}
		return null;
	}
	
	
}
