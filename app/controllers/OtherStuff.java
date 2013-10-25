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

	private static final Logger log = LoggerFactory.getLogger(OtherStuff.class);

	public static void setupWizard() {
		render();
	}

	public static void adminSetup(String beginDayOfWeek, String endOfWeek,
			String emailSend) {
		UserDbo admin = Utility.fetchUser();
		admin.setBeginDayOfWeek(beginDayOfWeek);
		admin.setEndOfWeek(endOfWeek);
		admin.setGetEmailYesOrNo(emailSend);
		JPA.em().persist(admin);
		JPA.em().flush();
		company();

	}

	public static void company() {
		UserDbo admin = Utility.fetchUser();
		CompanyDbo company = admin.getCompany();
		log.info("Adding users by Admin = " + admin.getEmail()
				+ " and Company = " + company.getName());
		List<UserDbo> users = company.getUsers();
		render(admin, company, users);
	}

	public static void home(Integer id) {
		UserDbo employee = Utility.fetchUser();
		UserDbo manager=employee.getManager();
		List<UserDbo> employees = employee.getEmployees();
		List<TimeCardDbo> timeCards = employee.getTimecards();
		LocalDate beginOfWeek = Utility.calculateBeginningOfTheWeek();
		if(manager.getBeginDayOfWeek()!=null && manager.getBeginDayOfWeek().equalsIgnoreCase("Saturday")){
		    beginOfWeek=beginOfWeek.minusDays(2);
		}
		if(manager.getBeginDayOfWeek()!=null && manager.getBeginDayOfWeek().equalsIgnoreCase("Sunday")){
			beginOfWeek=beginOfWeek.minusDays(1);
		}
		DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEEEEEE");
		if (id == null) {
			String email = employee.getEmail();
			String currentWeek = fmt.print(beginOfWeek);
			DayCardDbo[] dayCards = new DayCardDbo[7];
			int[] noofhours = new int[7];
			String[] details = new String[7];
			for (int i = 0; i < 7; i++) {
				noofhours[i] = 0;
				details[i] = "";
				dayCards[i] = new DayCardDbo();
				dayCards[i].setDate(beginOfWeek.plusDays(i));
				dayCards[i].setDay(fmt.print(beginOfWeek.plusDays(i)));
			}
			render(timeCards, beginOfWeek, email, currentWeek, employee,
					dayCards, noofhours, details);

		} else {
			TimeCardDbo timeCard = JPA.em().find(TimeCardDbo.class, id);
			StatusEnum status = timeCard.getStatus();
			boolean readOnly;
			if (status == StatusEnum.APPROVED)
				readOnly = true;
			else
				readOnly = false;
			List<DayCardDbo> dayCardDbo = timeCard.getDaycards();
			float[] noofhours = new float[7];
			String[] details = new String[7];
			int i = 0;
			for (DayCardDbo dayCard : dayCardDbo) {
				noofhours[i] = dayCard.getNumberOfHours();
				details[i] = dayCard.getDetail();
				i++;
			}
			render(timeCard, timeCards, dayCardDbo, noofhours, details,
					beginOfWeek, readOnly, status);
		}
		render();
	}

	public static void success() {
		render();
	}

	public static void cancel() {
		render();
	}

	public static void cardsAction(Integer timeCardId, int status) {

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
