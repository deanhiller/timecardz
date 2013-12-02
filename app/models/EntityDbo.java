package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.annotations.Index;

@Entity
@NamedQueries({
		@NamedQuery(name = "findAll", query = "select u from EntityDbo as u"),
		@NamedQuery(name = "findByEmail", query = "select u from EntityDbo as u where u.email=:email") })
public class EntityDbo {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Integer id;

	@Index(name="entityIndex")
	@Column(unique = true)
	private String email;

	private String firstName;

	private String lastName;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public static List<EntityDbo> findAll(EntityManager mgr) {
		Query query = mgr.createNamedQuery("findAll");
		return query.getResultList();
	}
	
	public static EntityDbo findByEmail(EntityManager mgr, String email) {
		Query query = mgr.createNamedQuery("findByEmail");
		query.setParameter("email", email);
		try {
			return (EntityDbo) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
