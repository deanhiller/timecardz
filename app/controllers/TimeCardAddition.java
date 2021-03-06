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
import play.mvc.Scope.Session;
import play.mvc.With;

@With(Secure.class)
public class TimeCardAddition extends Controller {
	private static final Logger log = LoggerFactory.getLogger(TimeCardAddition.class);

	public static void postTimeAddition(Integer timeCardId,Integer[] dayCardsid, String date, float[] noofhours, String detail)
			throws Throwable {
		Integer id = null;
		if (timeCardId == null) {
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
			LocalDate beginOfWeek = formatter.parseLocalDate(date);
			UserDbo user = Utility.fetchUser();
			CompanyDbo company = user.getCompany();
			DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEEEEEE");
			if (company.getBeginDayOfWeek() != null	&& company.getBeginDayOfWeek().equalsIgnoreCase("Saturday")) {
				beginOfWeek = beginOfWeek.minusDays(2);
			}
			if (company.getBeginDayOfWeek() != null	&& company.getBeginDayOfWeek().equalsIgnoreCase("Sunday")) {
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
			}
			JPA.em().flush();

			timeCardDbo.setNumberOfHours(totalhours);
			timeCardDbo.setDetail(detail);
			timeCardDbo.setApproved(false);
			timeCardDbo.setStatus(StatusEnum.UNSUBMITED);
			user.addTimecards(timeCardDbo);
			JPA.em().persist(timeCardDbo);
			Integer newTimeCardId=timeCardDbo.getId();
			session.put("newTimeCardId", newTimeCardId);
			JPA.em().persist(user);
			JPA.em().flush();
		    OtherStuff.home(id);
		} else {
			TimeCardDbo timeCard = JPA.em().find(TimeCardDbo.class, timeCardId);
			int sum = 0;
			for (int i = 0; i < 7; i++) {
				DayCardDbo dayC = JPA.em().find(DayCardDbo.class, dayCardsid[i]);
				if (noofhours[i] > 12) {
					validation.addError("noofhours[i]",
							"hours should be less than 12");
				} else {
					dayC.setNumberOfHours(noofhours[i]);
					JPA.em().persist(dayC);
					sum += noofhours[i];
				}
				if (validation.hasErrors()) {
					params.flash();
					validation.keep();
					OtherStuff.home(id);
				}

			}
			timeCard.setNumberOfHours(sum);
			timeCard.setStatus(StatusEnum.UNSUBMITED);
			timeCard.setDetail(detail);
			JPA.em().persist(timeCard);
			JPA.em().flush();
			OtherStuff.home(id);

		}
	}

	public static void postMail(Integer id, String submitToManager) {
		Integer newId = id;
		TimeCardDbo timeCard = null;
		if (submitToManager.equalsIgnoreCase("yes")) {
			if (id == null) {
				String newTimeCardId = session.get("newTimeCardId");
				newId = Integer.parseInt(newTimeCardId);
				session.remove(newTimeCardId);
				timeCard = JPA.em().find(TimeCardDbo.class, newId);
			} else {
				timeCard = JPA.em().find(TimeCardDbo.class, id);
			}
			timeCard.setStatus(StatusEnum.SUBMIT);
			UserDbo user = Utility.fetchUser();
			CompanyDbo company = user.getCompany();
			UserDbo manager = user.getManager();
			String key = Utility.generateKey();
			SecureToken secureToken = new SecureToken();
			secureToken.setSecureToken(key);
			secureToken.setValue(newId);
			JPA.em().persist(secureToken);
			JPA.em().flush();
			Utility.sendEmailForApproval(manager.getEmail(), company.getName(),	user.getEmail(), key);
		}
		OtherStuff.home(id);
	}

	public static void addEditTimeCardRender(String date,Integer id) {
		LocalDate beginOfWeek=null;
		if (id != null) {
			TimeCardDbo timeCard = JPA.em().find(TimeCardDbo.class, id);
			beginOfWeek = timeCard.getBeginOfWeek();
			render(timeCard, beginOfWeek);
		} else {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		beginOfWeek = formatter.parseLocalDate(date).dayOfWeek().withMinimumValue();
		UserDbo user = Utility.fetchUser();
		CompanyDbo manager = user.getCompany();
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
	}

	public static void deleteTimeCardRender(Integer timeCardId) {
		Integer id = timeCardId;
		render(id);
	}

	public static void postDeleteTimeCard(Integer timeCardId) {
		Integer id = null;
		String email = Session.current().get("username");
		UserDbo user = UserDbo.findByEmailId(JPA.em(), email);
		TimeCardDbo timeCard = JPA.em().find(TimeCardDbo.class, timeCardId);
		user.deleteTimeCard(timeCard);
		JPA.em().persist(user);
		JPA.em().flush();
		OtherStuff.home(id);
	}
	public static void timeCardApproval(String key){
		SecureToken token =JPA.em().find(SecureToken.class, key);
		TimeCardDbo timeCardDbo= JPA.em().find(TimeCardDbo.class, token.getValue());
		List<DayCardDbo> dayCardDbo=timeCardDbo.getDaycards();
		render(timeCardDbo,dayCardDbo);
	}

}