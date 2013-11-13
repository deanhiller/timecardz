package controllers;

import java.util.List;

import org.h2.engine.User;

import models.TimeCardDbo;
import models.UserDbo;
import play.db.jpa.JPA;
import play.mvc.Controller;

public class NewApp extends Controller {

	public static void clockInOut() {
		render();
	}

	public static void contractor() {
		render();
	}
	public static void companyAlias() {
		render();
	}

	public static void timeCardDetail(Integer id) {
		UserDbo user = JPA.em().find(UserDbo.class, id);
		List<TimeCardDbo> timeCardDbo = user.getTimecards();
		render(timeCardDbo, user);
	}
	
}
