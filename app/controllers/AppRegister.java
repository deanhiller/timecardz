package controllers;

import java.text.DateFormat;
import java.text.ParseException;
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
		String totalWorkHrs=null;
		UserDbo user = UserDbo.findByEmailId(JPA.em(), useremail1);
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		LocalDate date = LocalDate.now();
		Calendar cal = Calendar.getInstance();
		String time = dateFormat.format(cal.getTime());
		TimeCardDbo timeCardDbo = TimeCardDbo.findByDate(JPA.em(), date);
		timeCardDbo.setClockOutTime(time);
		timeCardDbo.getClockInTime();
		long diffTime = 0;
		try {
			 Date clockOut = dateFormat.parse(time);
			 Date clockIn = dateFormat.parse(timeCardDbo.getClockInTime());
		     diffTime = clockOut.getTime() - clockIn.getTime();
		     long timeInSeconds = diffTime / 1000;
		     long hours, minutes, seconds;
		     hours = timeInSeconds / 3600;
		     timeInSeconds = timeInSeconds - (hours * 3600);
		     minutes = timeInSeconds / 60;
		     timeInSeconds = timeInSeconds - (minutes * 60);
		     seconds = timeInSeconds;
		     totalWorkHrs = (hours<10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds) + " h";
		    } catch (ParseException e) {
		  e.printStackTrace();
		}
		timeCardDbo.setTotalWrkTime(totalWorkHrs);  	
		JPA.em().persist(timeCardDbo);
		JPA.em().persist(timeCardDbo);
		JPA.em().flush();
		render(date, time, timeCardDbo);
	}
	
}
