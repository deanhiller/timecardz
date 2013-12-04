package controllers;

import java.util.List;

import models.CompanyDbo;
import models.DayCardDbo;
import models.SecureToken;
import models.StatusEnum;
import models.TimeCardDbo;
import models.UserDbo;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;
import controllers.auth.Secure;

@With(Secure.class)
public class OtherStuff extends Controller {

	static final Logger log = LoggerFactory.getLogger(OtherStuff.class);

	public static void setupWizard() {
		render();
	}

	public static void postAdminSetup(String beginDayOfWeek, String endOfWeek,
			String emailSend) {
		UserDbo admin = Utility.fetchUser();
		CompanyDbo company = admin.getCompany();
		company.setBeginDayOfWeek(beginDayOfWeek);
		company.setGetEmailYesOrNo(emailSend);
		JPA.em().persist(admin);
		JPA.em().flush();
		UserAddition.listUsers();

	}

	public static void home(Integer id) {
		UserDbo employee = Utility.fetchUser();
		String email=employee.getEmail();
		List<TimeCardDbo> timeCards = employee.getTimecards();
		render(timeCards,email);
	}

	public static void success() {
		render();
	}

	public static void cancel() {
		render();
	}

	public static void postCardsAction(Integer timeCardId, int status) {

		TimeCardDbo ref = JPA.em().find(TimeCardDbo.class, timeCardId);
		if (ref != null) {
			if (status == 1) {
				ref.setStatus(StatusEnum.APPROVED);
				ref.setApproved(true);
			} else {
				ref.setStatus(StatusEnum.CANCELLED);
				ref.setApproved(false);
			}

		}
		JPA.em().persist(ref);
		JPA.em().flush();
		OtherStuff.home(timeCardId);
	}

}