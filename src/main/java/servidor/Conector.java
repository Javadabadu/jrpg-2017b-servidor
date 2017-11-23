package servidor;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import dominio.Inventario;
import dominio.Item;
import dominio.Mochila;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;

public class Conector {
	private static final int CANTITEMS = 9;
	private static final int MAXITEMS = 29;
	Connection connect;
	private Session session;

	public void connect() {
		try {

			Servidor.log.append("Estableciendo conexión con la base de datos..." + System.lineSeparator());
			session = HibernateConector.getInstance().obtenerSession();
			Servidor.log.append("Conexión con la base de datos establecida con éxito." + System.lineSeparator());

		} catch (HibernateException ex) {
			Servidor.log.append("Fallo al intentar establecer la conexión con la base de datos. " + ex.getMessage()
					+ System.lineSeparator());
			ex.printStackTrace();
		}
	}

	public void close() {

		try {
			if (session.isConnected()) {
				session.close();
			}
		} catch (HibernateException he) {
			Servidor.log.append("Error al intentar cerrar la conexión con la base de datos." + System.lineSeparator());
			Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, he);
		}
	}

	public boolean registrarUsuario(PaqueteUsuario user) {
		boolean retorno = true;
		Transaction tx = null;

		try {
			if (!session.isConnected())
				connect();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<PaqueteUsuario> query = builder.createQuery(PaqueteUsuario.class);
			Root<PaqueteUsuario> root = query.from(PaqueteUsuario.class);
			query.select(root).where(builder.equal(root.get("username"), user.getUsername()));
			
			List<PaqueteUsuario> consulta = session.createQuery(query).getResultList();
			if (consulta.size() == 0) {
				tx = session.beginTransaction();
				session.save(user);
				tx.commit();
			} else {
				Servidor.log.append(
						"El usuario " + user.getUsername() + " ya se encuentra en uso." + System.lineSeparator());
				retorno = false;
			}

		} catch (HibernateException he) {
			Servidor.log
					.append("Error al intentar registrar el usuario " + user.getUsername() + System.lineSeparator());
			System.err.println(he.getMessage());
			retorno = false;
			if (tx != null)
				tx.rollback();
		} finally {
			session.clear();
		}

		return retorno;
	}

	public boolean registrarPersonaje(PaquetePersonaje paquetePersonaje, PaqueteUsuario paqueteUsuario) {
		boolean retorno = true;
		Transaction tx = null;
		Mochila mochila;
		Inventario inventario;
		
		try {
			if (!session.isConnected())
				connect();

			tx = session.beginTransaction();

			int idPersonajeNuevo = generarIDPersonaje();

			paqueteUsuario.setIdPj(idPersonajeNuevo);
			mochila = new Mochila(idPersonajeNuevo);
			inventario = new Inventario(idPersonajeNuevo);
			paquetePersonaje.setIdInventario(idPersonajeNuevo);
			paquetePersonaje.setIdMochila(idPersonajeNuevo);
			session.save(paquetePersonaje);
			session.save(mochila);
			session.save(inventario);
		    session.update(paqueteUsuario);
			tx.commit();
			

		} catch (HibernateException he) {
			Servidor.log.append(
					"Error al intentar crear el personaje " + paquetePersonaje.getNombre() + System.lineSeparator());
			he.getStackTrace();
			retorno = false;
			if (tx != null)
				tx.rollback();

		} finally {
			session.clear();
		}

		return retorno;
	}

	/**
	 * Busca el maximo id y para obtener el siguiente.
	 * 
	 * @return el nuevo id
	 */
	public int generarIDPersonaje() {
		Integer id = 0;
		if (!session.isConnected())
			connect();
		String queryHQL = "SELECT MAX(pp.id) FROM PaquetePersonaje as pp";

		id = (int) session.createQuery(queryHQL).getSingleResult();
		return ++id;
	}

	/**
	 * Metodo que sirve para registrar inventario y mochila. 
	 * @param idInventarioMochila
	 * @return
	 */
	public boolean registrarInventarioMochila(final int idInventarioMochila) {
		boolean retorno = true;
		Transaction tx = session.beginTransaction();
		try {
			Mochila mochila = new Mochila(idInventarioMochila);
			Inventario inventario = new Inventario(idInventarioMochila);
			if (!session.isConnected())
				connect();

			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<PaquetePersonaje> query = builder.createQuery(PaquetePersonaje.class);
			Root<PaquetePersonaje> root = query.from(PaquetePersonaje.class);
			query.select(root).where(builder.equal(root.get("id"), idInventarioMochila));
			List<PaquetePersonaje> consulta = session.createQuery(query).getResultList();

			if (consulta.size() == 0) {
				Servidor.log
						.append("Error: No se encontro el personaje " + idInventarioMochila + System.lineSeparator());
				retorno = false;
			} else {
				// Obtengo el personaje y lo actualizo
				PaquetePersonaje personaje = consulta.get(0);
				session.save(mochila);
				session.save(inventario);
				personaje.setIdMochila(idInventarioMochila);
				personaje.setIdInventario(idInventarioMochila);
				session.update(personaje);
				Servidor.log
						.append("Se ha registrado el inventario de " + idInventarioMochila + System.lineSeparator());

			}

		} catch (HibernateException e) {
			Servidor.log.append("Error al registrar el inventario de " + idInventarioMochila + System.lineSeparator());
			if (tx != null)
				tx.rollback();
			retorno = false;
		}

		return retorno;

	}
/**
 * Logueo de usuario. Retorna si es posible o no el acceso.
 * @param user
 * @return V O F
 */
	public boolean loguearUsuario(PaqueteUsuario user) {

		boolean retorno = true;
		try {
			if (!session.isConnected())
				connect();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<PaqueteUsuario> query = builder.createQuery(PaqueteUsuario.class);
			Root<PaqueteUsuario> root = query.from(PaqueteUsuario.class);
			// Creacion de predicado para la consulta de usuario y password
			Predicate predicado = builder.conjunction();
			predicado = builder.equal(root.get("username"), user.getUsername());
			predicado = builder.and(predicado, builder.equal(root.get("password"), user.getPassword()));
			query.select(root).where(predicado);
			List<PaqueteUsuario> consulta = session.createQuery(query).getResultList();

			if (consulta.size() == 0) {
				Servidor.log.append("El usuario " + user.getUsername()
						+ " ha realizado un intento fallido de inicio de sesión." + System.lineSeparator());
				retorno = false;
			} else {
				Servidor.log
						.append("El usuario " + user.getUsername() + " ha iniciado sesión." + System.lineSeparator());
			}
		} catch (HibernateException he) {
			Servidor.log
					.append("El usuario " + user.getUsername() + " fallo al iniciar sesión." + System.lineSeparator());
			retorno = false;
		}

		return retorno;
	}


	public PaquetePersonaje getPersonaje(PaqueteUsuario user) throws IOException {

		PaquetePersonaje personaje = new PaquetePersonaje();
		int i = 2;
		int j = 0;
		if (!session.isConnected())
			connect();

		try {
			
			CriteriaBuilder builderUsuario = session.getCriteriaBuilder();
			CriteriaQuery<PaqueteUsuario> query = builderUsuario.createQuery(PaqueteUsuario.class);
			Root<PaqueteUsuario> root = query.from(PaqueteUsuario.class);
			query.select(root).where(builderUsuario.equal(root.get("username"), user.getUsername()));
			// Obtengo el id
			List<PaqueteUsuario> consultaUser = session.createQuery(query).getResultList();
			if (consultaUser.size() != 0) {
				int idPersonaje = consultaUser.get(0).getIdPj();

				CriteriaBuilder builderPer = session.getCriteriaBuilder();
				CriteriaQuery<PaquetePersonaje> queryPer = builderPer.createQuery(PaquetePersonaje.class);
				Root<PaquetePersonaje> rootPer = queryPer.from(PaquetePersonaje.class);
				queryPer.select(rootPer).where(builderPer.equal(rootPer.get("id"), idPersonaje));
				List<PaquetePersonaje> consultaPer = session.createQuery(queryPer).getResultList();
				if (consultaPer.size() != 0)
					personaje = consultaPer.get(0);

				CriteriaBuilder builderMochi = session.getCriteriaBuilder();
				CriteriaQuery<Mochila> queryMochi = builderMochi.createQuery(Mochila.class);
				Root<Mochila> rootMochi = queryMochi.from(Mochila.class);
				queryMochi.select(rootMochi).where(builderMochi.equal(rootMochi.get("idMochila"), idPersonaje));
				Mochila consultaMochi = session.createQuery(queryMochi).getSingleResult();

				// Items
				CriteriaBuilder builderItem = session.getCriteriaBuilder();
				CriteriaQuery<Item> queryItem = builderItem.createQuery(Item.class);
				Root<Item> rootItem = queryItem.from(Item.class);

				while (j <= CANTITEMS) {
					if (consultaMochi.obtenerItem(i) != -1) {
						queryItem.select(rootItem)
								.where(builderItem.equal(rootItem.get("idItem"), consultaMochi.obtenerItem(i)));
						Item item = session.createQuery(queryItem).getSingleResult();

						if (item != null) {
							personaje.anadirItem(item.getIdItem(), item.getNombre(), item.getWearLocation(),
									item.getBonusSalud(), item.getBonusEnergia(), item.getBonusFuerza(),
									item.getBonusDestreza(), item.getBonusInteligencia(), item.obtenerFoto(),
									item.getFotoEquipado());
						}

					}
					i++;
					j++;
				}
			} else {
				Servidor.log.append(
						"Fallo al intentar recuperar el personaje " + user.getUsername() + System.lineSeparator());

			}

		} catch (HibernateException ex) {
			Servidor.log
					.append("Fallo al intentar recuperar el personaje " + user.getUsername() + System.lineSeparator());
			Servidor.log.append(ex.getMessage() + System.lineSeparator());
		}

		return personaje;
	}

	// Devuelvo el paquete personaje con sus datos

	public PaqueteUsuario getUsuario(String usuario) {
		PaqueteUsuario user = new PaqueteUsuario();
		CriteriaBuilder registroBuilder;
		CriteriaQuery<PaqueteUsuario> userQuery;
		Root<PaqueteUsuario> userRoot;

		try {
			if (!session.isConnected())
				connect();

			registroBuilder = session.getCriteriaBuilder();
			userQuery = registroBuilder.createQuery(PaqueteUsuario.class);
			userRoot = userQuery.from(PaqueteUsuario.class);

			userQuery.select(userRoot).where(registroBuilder.equal(userRoot.get("username"), usuario));
			user = session.createQuery(userQuery).getSingleResult();
		} catch (HibernateException he) {
			Servidor.log.append("Fallo al intentar recuperar el usuario " + usuario + System.lineSeparator());
			Servidor.log.append(he.getMessage() + System.lineSeparator());
		}
		return user;
	}

/**
 * Metodo que sirve para actualizar el Personaje En la BBDD
 * @param paquetePersonaje
 */
	public void actualizarPersonaje(PaquetePersonaje paquetePersonaje) {

		CriteriaBuilder mochiBuilder;
		CriteriaQuery<Mochila> mochiQuery;
		CriteriaBuilder itemBuilder;
		CriteriaQuery<Item> itemQuery;
		Root<Mochila> mochiRoot;
		Root<Item> itemRoot;
		Transaction tx = null;
		Mochila mochila = new Mochila();
		Item item = new Item();
		int i = 2, j = 1;
		try {
			if (!session.isConnected())
				connect();
			tx = session.beginTransaction();
			session.update(paquetePersonaje);
			tx.commit();

			mochiBuilder = session.getCriteriaBuilder();
			mochiQuery = mochiBuilder.createQuery(Mochila.class);
			mochiRoot = mochiQuery.from(Mochila.class);
			mochiQuery.select(mochiRoot)
					.where(mochiBuilder.equal(mochiRoot.get("idMochila"), paquetePersonaje.getId()));
			mochila = session.createQuery(mochiQuery).getSingleResult();

			if (mochila != null) {
				itemBuilder = session.getCriteriaBuilder();
				itemQuery = itemBuilder.createQuery(Item.class);
				itemRoot = itemQuery.from(Item.class);

				while (j <= CANTITEMS) {
					if (mochila.obtenerItem(i) != -1) {
						itemQuery.select(itemRoot)
								.where(itemBuilder.equal(itemRoot.get("idItem"), mochila.obtenerItem(i)));
						item = session.createQuery(itemQuery).getSingleResult();

						paquetePersonaje.anadirItem(item.getIdItem(), item.getNombre(), item.getWearLocation(),
								item.getBonusSalud(), item.getBonusEnergia(), item.getBonusFuerza(),
								item.getBonusDestreza(), item.getBonusInteligencia(), item.obtenerFoto(),
								item.getFotoEquipado());
					}
					i++;
					j++;
				}
				Servidor.log.append("El personaje " + paquetePersonaje.getNombre() + " se ha actualizado con éxito."
						+ System.lineSeparator());
			} else {
				Servidor.log.append("No se encontro la mochila del personaje " + paquetePersonaje.getNombre()
						+ System.lineSeparator());
			}

		} catch (HibernateException he) {
			Servidor.log.append("Fallo al intentar actualizar el personaje " + paquetePersonaje.getNombre()
					+ System.lineSeparator());

			if (tx != null)
				tx.rollback();
		}

	}

/**
 * Metodo que sirve para actualizar inventario En la BBDD
 * @param paquetePersonaje
 */
	public void actualizarInventario(PaquetePersonaje paquetePersonaje) {
		Transaction tx = null;
		Mochila mochila = new Mochila(paquetePersonaje.getId());
		int i = 0;
		try {
			while (i < paquetePersonaje.getCantItems()) {
				mochila.establecerItem(i + 1, paquetePersonaje.getItemID(i));
				i++;
			}

			tx = session.beginTransaction();
			session.update(mochila);
			tx.commit();

		} catch (HibernateException he) {
			if (tx != null)
				tx.rollback();
		}
	}

/**
 * Metodo que sirve para actualizar inventario En la BBDD.	
 * @param idPersonaje
 */
	public void actualizarInventario(int idPersonaje) {
		int i = 0;
		PaquetePersonaje paquetePersonaje = Servidor.getPersonajesConectados().get(idPersonaje);
		Transaction tx = null;
		Mochila mochila = new Mochila(paquetePersonaje.getId());
		try {
			while (i < paquetePersonaje.getCantItems()) {
				mochila.establecerItem(i + 1, paquetePersonaje.getItemID(i));
				i++;
			}

			if (paquetePersonaje.getCantItems() < CANTITEMS) {
				int itemGanado = new Random().nextInt(MAXITEMS);
				itemGanado += 1;
				mochila.establecerItem(paquetePersonaje.getCantItems() + 1, itemGanado);
			}

			tx = session.beginTransaction();
			session.update(mochila);
			tx.commit();
		} catch (HibernateException he) {
			if (tx != null)
				tx.rollback();
		}
	}

/**
 * Actualiza el nivel del personaje en la BBDD.
 * @param paquetePersonaje
 */
	public void actualizarPersonajeSubioNivel(PaquetePersonaje paquetePersonaje) {
		Transaction tx = null;
		try {
			if (!session.isConnected())
				connect();

			tx = session.beginTransaction();
			session.update(paquetePersonaje);
			tx.commit();
			Servidor.log.append("El personaje " + paquetePersonaje.getNombre() + " se ha actualizado con éxito."
					+ System.lineSeparator());
			
		} catch (HibernateException e) {
			Servidor.log.append("Fallo al intentar actualizar el personaje " + paquetePersonaje.getNombre()
					+ System.lineSeparator());
		} finally {
			if (tx != null)
				tx.rollback();
		}
	}
}
