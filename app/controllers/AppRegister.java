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
	public static void postRegister(String company, String email,
		String password, String verifyPassword,String selectRadio) throws Throwable {
		validation.required(email);
		validation.required(company);
		if (company == null) {
			validation.addError("company", "company must be supplied");
		}
		if (password == null) {
			validation.addError("password", "Password must be supplied");
		}
		if (!password.equals(verifyPassword)) {
			validation.addError("verifyPassword", "Passwords did not match");
		}
		if (!email.contains("@"))
			validation.addError("email", "This is not a valid email");
		if (Register.emailAlreadyExists(email)) {
			validation.addError("user.email", "This email is already in use");
		}
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			Application.register();
		}
		CompanyDbo companyDbo = new CompanyDbo();
		companyDbo.setName(company);
		UserDbo user = new UserDbo();
		user.setEmail(email);
		user.setPassword(password);
		user.setManager(user);
		user.setAdmin(true);
		user.setBeginDayOfWeek("Monday");
		user.setGetEmailYesOrNo("Yes");
		companyDbo.addUser(user);
		user.setCompany(companyDbo);
		JPA.em().persist(companyDbo);
		JPA.em().persist(user);
		JPA.em().flush();
		Secure.addUserToSession(user.getEmail());
		if(selectRadio.equalsIgnoreCase("Simple clock-in / clock-out timecard software")){
			NewApp.clockInOut();
		}
		NewApp.contractor();
	}
	
	public static void addUser() {
		UserDbo admin = Utility.fetchUser();
		CompanyDbo company = admin.getCompany();
		log.info("Adding users by Admin = " + admin.getEmail()
				+ " and Company = " + company.getName());
		List<UserDbo> users = company.getUsers();
		render(admin, company, users);

	}

	public static void clockIn(String useremail) {
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

	public static void clockOut(String useremail1) {
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
