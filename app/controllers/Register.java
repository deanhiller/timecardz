package controllers;

import models.CompanyDbo;
import models.Role;
import models.SoftwareType;
import models.Token;
import models.UserDbo;

import org.hibernate.FlushMode;
import org.hibernate.Session;

import play.data.validation.Valid;
import play.db.jpa.JPA;
import play.mvc.Controller;
import controllers.auth.Secure;
import controllers.test.Processor;
import controllers.test.SomeClass;

public class Register extends Controller {

	public static void register() {
		long interval = 60;
		int windowSize = 100;
		Processor p = new SomeClass(interval, windowSize, 0, 0, null, response);
		await(5000);
		
		render();
	}

}
