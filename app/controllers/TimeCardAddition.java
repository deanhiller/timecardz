package controllers;

import models.CompanyDbo;
import models.DayCardDbo;
import models.StatusEnum;
import models.TimeCardDbo;
import models.UserDbo;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.auth.Secure;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class TimeCardAddition extends Controller {
	private static final Logger log = LoggerFactory.getLogger(TimeCardAddition.class);

	public static void postTimeAddition(float[] noofhours, String[] details)
			throws Throwable {
		Integer id = null;
		UserDbo user = Utility.fetchUser();
		CompanyDbo company = user.getCompany();
		UserDbo manager = user.getManager();
		LocalDate beginOfWeek = Utility.calculateBeginningOfTheWeek();
		if (manager.getBeginDayOfWeek()!=null && manager.getBeginDayOfWeek().equalsIgnoreCase("Saturady")) {
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
			if (noofhours[i] > 12) {
				validation.addError("noofhours[i]",
						"hours should be less than 12");
			} else {
				dayC.setNumberOfHours(noofhours[i]);
				totalhours = totalhours + noofhours[i];
				dayC.setDetail(details[i]);
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
		timeCardDbo.setApproved(false);
		timeCardDbo.setStatus(StatusEnum.SUBMIT);
		user.addTimecards(timeCardDbo);
		JPA.em().persist(timeCardDbo);
		JPA.em().persist(user);
		JPA.em().flush();
		if (manager.isGetEmailYesOrNo() !=null && manager.isGetEmailYesOrNo().equalsIgnoreCase("yes")) {
			Utility.sendEmailForApproval(manager.getEmail(), company.getName(),
					user.getEmail());
		}
		OtherStuff.home(id);
	}

	public static void addEditTimeCardRender(Integer timeCardId) {
		UserDbo user = Utility.fetchUser();
		UserDbo manager = user.getManager();
		StatusEnum status = null;
		TimeCardDbo timeCard = null;
		DayCardDbo dayC = null;
		boolean readOnly = false;
		LocalDate beginOfWeek = Utility.calculateBeginningOfTheWeek();
		if(manager.getBeginDayOfWeek()!=null && manager.getBeginDayOfWeek().equalsIgnoreCase("Saturady")){
			beginOfWeek=beginOfWeek.minusDays(2);
		}
		if(manager.getBeginDayOfWeek()!=null && manager.getBeginDayOfWeek().equalsIgnoreCase("Sunday")){
			beginOfWeek=beginOfWeek.minusDays(1);
		}
		if (timeCardId == null) {
			timeCard = new TimeCardDbo();
			timeCard.setBeginOfWeek(Utility.calculateBeginningOfTheWeek());
			for (int i = 0; i < 7; i++) {
				dayC = new DayCardDbo();
				timeCard.getDaycards().add(dayC);
				dayC.setDate(beginOfWeek.plusDays(i));
			}
		} else {
			timeCard = JPA.em().find(TimeCardDbo.class, timeCardId);
			status = timeCard.getStatus();
			if (status == StatusEnum.APPROVED)
				readOnly = true;
			else
				readOnly = false;
		}
		render(readOnly, timeCard, beginOfWeek);
	}

}
