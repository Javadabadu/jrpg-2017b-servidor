package servidor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Grupo javadabaduu
 */
public class HibernateConector {

	private static HibernateConector instance;
	private Session session;
	private SessionFactory factory;

	/*
	 * Uso del patron *Singleton* para la obtencion de una sola session.
	 */
	/*
	 * Constructor privado
	 */
	private HibernateConector() {

		Configuration cfg = new Configuration();
		cfg.configure("hibernate.cfg.xml");

		factory = cfg.buildSessionFactory();
		session = factory.openSession();

	}

	public static HibernateConector getInstance() {
		if (instance == null)
			instance = new HibernateConector();
		return instance;
	}

	public Session obtenerSession() {
		return session;
	}

}