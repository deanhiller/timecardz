package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.hibernate.annotations.Index;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.db.jpa.Model;

import com.sun.org.apache.xpath.internal.operations.Mod;

@Entity
@NamedQueries({
	@NamedQuery(name = "findByDate", query = "select u from TimeCardDbo as u where u.date=:date") })
public class TimeCardDbo {

	private static DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd, yyyy");
	@Id
	@GeneratedValue
	private int id;
	private LocalDate beginOfWeek;

	@Index(name = "entityIndexColumn")
	@Column(unique = true)
	private LocalDate date;
	private String clockInTime;
	private String clockOutTime;

	private float numberOfHours;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getClockInTime() {
		return clockInTime;
	}

	public void setClockInTime(String clockInTime) {
		this.clockInTime = clockInTime;
	}

	public String getClockOutTime() {
		return clockOutTime;
	}

	public void setClockOutTime(String clockOutTime) {
		this.clockOutTime = clockOutTime;
	}

	private String detail;

	private boolean approved;

	@OneToMany
	private List<DayCardDbo> daycards = new ArrayList<DayCardDbo>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<DayCardDbo> getDaycards() {
		return daycards;
	}

	public void setDaycards(List<DayCardDbo> daycards) {
		this.daycards = daycards;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private String status;

	public void setId(LocalDate beginOfWeek) {
		this.beginOfWeek = beginOfWeek;
	}

	public String getRange() {
		LocalDate end = beginOfWeek.plusWeeks(1);
		end = end.minusDays(1);
		return fmt.print(beginOfWeek) + " to " + fmt.print(end);
	}

	public LocalDate getBeginOfWeek() {
		return beginOfWeek;
	}

	public void setBeginOfWeek(LocalDate beginOfWeek) {
		this.beginOfWeek = beginOfWeek;
	}

	public float getNumberOfHours() {
		return numberOfHours;
	}

	public void setNumberOfHours(float numberOfHours) {
		this.numberOfHours = numberOfHours;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public StatusEnum getStatus() {

		return StatusEnum.mapForConversion.get(status);
	}

	public void setStatus(StatusEnum status) {
		this.status = status.getDbValue();
	}

	public void addDayCard(DayCardDbo dayCard) {
		this.daycards.add(dayCard);
	}

	public static TimeCardDbo findByDate(EntityManager mgr, LocalDate date) {
		Query query = mgr.createNamedQuery("findByDate");
		query.setParameter("date", date);
		try {
			return (TimeCardDbo) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
