package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import models.CompanyDbo;
import models.TimeCardDbo;
import models.UserDbo;
import controllers.auth.Secure;
import play.db.jpa.JPA;
import play.mvc.Controller;
import sun.net.www.content.image.jpeg;

public class AppRegister extends Controller{
	private static final Logger log = LoggerFactory.getLogger(UserAddition.class);
	

	public static void postClockIn(String useremail) {
		UserDbo user = UserDbo.findByEmailId(JPA.em(), useremail);
		if (user != null) {
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			LocalDate date = LocalDate.now();
			Calendar cal = Calendar.getInstance();
			String time = dateFormat.format(cal.getTime());
			TimeCardDbo timeCardDbo = new TimeCardDbo();
			timeCardDbo.setDate(date);
			timeCardDbo.setClockInTime(time);
			JPA.em().persist(timeCardDbo);
			user.addTimecards(timeCardDbo);
			JPA.em().persist(user);
			JPA.em().flush();
			render(date, time, timeCardDbo);
		}
		render();

	}

	public static void postClockOut(String useremail1) {
		UserDbo user = UserDbo.findByEmailId(JPA.em(), useremail1);
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		LocalDate date = LocalDate.now();
		Calendar cal = Calendar.getInstance();
		String time = dateFormat.format(cal.getTime());
		TimeCardDbo timeCardDbo = TimeCardDbo.findByDate(JPA.em(), date);
		timeCardDbo.setClockOutTime(time);
		timeCardDbo.getClockInTime();
		JPA.em().persist(timeCardDbo);
		JPA.em().persist(timeCardDbo);
		JPA.em().flush();
		render(date, time, timeCardDbo);
	}
}
