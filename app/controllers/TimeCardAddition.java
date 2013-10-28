package controllers;

import java.util.Date;
import java.util.List;

import models.CompanyDbo;
import models.DayCardDbo;
import models.SecureToken;
import models.StatusEnum;
import models.TimeCardDbo;
import models.Token;
import models.UserDbo;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.auth.Secure;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class TimeCardAddition extends Controller {
	private static final Logger log = LoggerFactory.getLogger(TimeCardAddition.class);

	public static void postTimeAddition(String date,float[] noofhours, String Description)
			throws Throwable {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		LocalDate beginOfWeek = formatter.parseLocalDate(date);
		Integer id = null;
		UserDbo user = Utility.fetchUser();
		CompanyDbo company = user.getCompany();
		UserDbo manager = user.getManager();
		DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEEEEEE");
		if (manager.getBeginDayOfWeek()!=null && manager.getBeginDayOfWeek().equalsIgnoreCase("Saturday")) {
			beginOfWeek = beginOfWeek.minusDays(2);
		}
		if (manager.getBeginDayOfWeek()!=null && manager.getBeginDayOfWeek().equalsIgnoreCase("Sunday")) {
			beginOfWeek = beginOfWeek.minusDays(1);
		}
		TimeCardDbo timeCardDbo = new TimeCardDbo();
		timeCardDbo.setBeginOfWeek(beginOfWeek);
		float totalhours = 0;
		for (int i = 0; i < 7; i++) {
			DayCardDbo dayC = new DayCardDbo();
			dayC.setDate(beginOfWeek.plusDays(i));
			dayC.setDay(fmt.print(beginOfWeek.plusDays(i)));
			if (noofhours[i] > 12) {
				validation.addError("noofhours[i]",
						"hours should be less than 12");
			} else {
				dayC.setNumberOfHours(noofhours[i]);
				totalhours = totalhours + noofhours[i];
				timeCardDbo.addDayCard(dayC);
				JPA.em().persist(dayC);
			}
		}
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			// addTime();
		}
		JPA.em().flush();

		timeCardDbo.setNumberOfHours(totalhours);
		timeCardDbo.setDetail(Description);
		timeCardDbo.setApproved(false);
		timeCardDbo.setStatus(StatusEnum.SUBMIT);
		user.addTimecards(timeCardDbo);
		JPA.em().persist(timeCardDbo);
		JPA.em().persist(user);
		String key = Utility.generateKey();
		SecureToken secureToken = new SecureToken();
		secureToken.setSecureToken(key);
		secureToken.setValue(timeCardDbo.getId());
		JPA.em().persist(secureToken);
		JPA.em().flush();
		if (manager.isGetEmailYesOrNo() !=null && manager.isGetEmailYesOrNo().equalsIgnoreCase("yes")) {
			Utility.sendEmailForApproval(manager.getEmail(), company.getName(),	user.getEmail(),key);
		}
		OtherStuff.home(id);
	}

	public static void addEditTimeCardRender(String date) {
	DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		LocalDate beginOfWeek = formatter.parseLocalDate(date);
		UserDbo user = Utility.fetchUser();
		UserDbo manager = user.getManager();
		TimeCardDbo timeCard = null;
		DayCardDbo dayC = null;
		DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEEEEEE");
		if(manager.getBeginDayOfWeek()!=null && manager.getBeginDayOfWeek().equalsIgnoreCase("Saturday")){
			beginOfWeek=beginOfWeek.minusDays(2);
		}
		if(manager.getBeginDayOfWeek()!=null && manager.getBeginDayOfWeek().equalsIgnoreCase("Sunday")){
			beginOfWeek=beginOfWeek.minusDays(1);
		}
			timeCard = new TimeCardDbo();
			timeCard.setBeginOfWeek(beginOfWeek);
			for (int i = 0; i < 7; i++) {
				dayC = new DayCardDbo();
				timeCard.getDaycards().add(dayC);
				dayC.setDate(beginOfWeek.plusDays(i));
				dayC.setDay(fmt.print(beginOfWeek.plusDays(i)));
			}
		render(timeCard, beginOfWeek);
	}
	public static void timeCardApproval(String key){
		SecureToken token =JPA.em().find(SecureToken.class, key);
		TimeCardDbo timeCardDbo= JPA.em().find(TimeCardDbo.class, token.getValue());
		List<DayCardDbo> dayCardDbo=timeCardDbo.getDaycards();
		render(timeCardDbo,dayCardDbo);
	}

}