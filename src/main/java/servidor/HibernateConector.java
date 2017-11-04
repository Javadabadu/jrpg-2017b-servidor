package servidor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Grupo javadabaduu
 */
public class HibernateConector {

	private static Session session;
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

	public static Session obtenerSession() {

		if (session == null)
			new HibernateConector();

		return session;
	}

}