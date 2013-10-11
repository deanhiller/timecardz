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

import models.EntityDbo;

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
	
	public static void listUsers() {
		List<EntityDbo> users = EntityDbo.findAll(JPA.em());
		String val = flash.get("showPopup");
		String valForDelete = flash.get("showPopupForDelete");
		boolean showPopup = false;
		boolean showPopupForDelete = false;
		if("true".equals(val))
			showPopup = true;
		if("true".equals(valForDelete))
			showPopup = true;
		render(users, showPopup,showPopupForDelete);
	}

	public static void ajaxAddEdit(Integer id) {
		if(id == null) { //this is a new user(ie. user add
			//use user?.name, user?.email, etc. etc. since user==null
			render();
		}
		EntityDbo user = JPA.em().find(EntityDbo.class, id);
		render(user);
	}

	public static void ajaxDelete(Integer id) {
		EntityDbo user = JPA.em().find(EntityDbo.class, id);
		render(user);
	}

	public static void postDelete(Integer userid) {
		EntityDbo user = JPA.em().find(EntityDbo.class, userid);
		JPA.em().remove(user);
		JPA.em().flush();
		listUsers();
	}

	public static void postUser(EntityDbo user) {

		if (user.getFirstName().equals(""))
			validation.addError("user.firstName", "Please enter firstName");

		if (user.getLastName().equals(""))
			validation.addError("user.lastName", "Please enter lastName");
		
		if(user.getEmail().equals(""))
			validation.addError("user.email", "Please enter an email id");

		if (emailExistsAlready(user)) {
			//this puts a message under the actual field and causes the label to turn red and the input border turns
			//red, etc. etc. (at least when done correctly)
			validation.addError("user.email", "This email is already in use");
		}

		//we always allow errors to queue up to show all errors at once for the user in case they have a few errors on
		//their form
		if(validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep();
			flash.error("Your form has errors");
			flash.put("showPopup", "true");
			listUsers();
		}
		
		user = JPA.em().merge(user);
		JPA.em().flush();
		
		listUsers();
	}
	
	private static boolean emailExistsAlready(EntityDbo user) {
		EntityDbo otherUser = EntityDbo.findByEmail(JPA.em(), user.getEmail());
		if(otherUser == null || otherUser.getId() == user.getId())
			return false;
		return true;
	}

}
