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
/**
 * 
 * @author Javadabadu
 *
 */
public class Conector {
<<<<<<< HEAD
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
=======

	private String url = "primeraBase.bd";
	private Connection connect;
	
	private static final int NUMERO1 = 1;
	private static final int NUMERO2 = 2;
	private static final int NUMERO3 = 3;
	private static final int NUMERO4 = 4;
	private static final int NUMERO5 = 5;
	private static final int NUMERO6 = 6;
	private static final int NUMERO7 = 7;
	private static final int NUMERO8 = 8;
	private static final int NUMERO9 = 9;
	private static final int NUMERO10 = 10;
	private static final int NUMERO11 = 11;
	private static final int NUMERO12 = 12;
	private static final int NUMERO13 = 13;
	private static final int NUMERO20 = 20;
	private static final int NUMERO21 = 21;
	private static final int NUMERO29 = 29;
	
	

	public void connect() {
		try {
			Servidor.getLog().append("Estableciendo conexión con la base de datos..."
						+ System.lineSeparator());
			connect = DriverManager.getConnection("jdbc:sqlite:" + url);
			Servidor.getLog().append("Conexión con la base de datos establecida con éxito."
						+ System.lineSeparator());
		} catch (SQLException ex) {
			Servidor.getLog().append("Fallo al intentar establecer la conexión con la base de datos. "
						+ ex.getMessage()
>>>>>>> master
					+ System.lineSeparator());
			ex.printStackTrace();
		}
	}

	public void close() {

		try {
<<<<<<< HEAD
			if (session.isConnected()) {
				session.close();
			}
		} catch (HibernateException he) {
			Servidor.log.append("Error al intentar cerrar la conexión con la base de datos." + System.lineSeparator());
			Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, he);
=======
			connect.close();
		} catch (SQLException ex) {
			Servidor.getLog().append("Error al intentar cerrar la "
					+ "conexión con la base de datos." + System.lineSeparator());
			Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
>>>>>>> master
		}
	}

	public boolean registrarUsuario(PaqueteUsuario user) {
<<<<<<< HEAD
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
		
=======
		ResultSet result = null;
		boolean retorno = false;
		try {
			PreparedStatement st1 = connect.prepareStatement("SELECT"
					+ " * FROM registro WHERE usuario= ? ");
			st1.setString(1, user.getUsername());
			result = st1.executeQuery();

			if (!result.next()) {

				PreparedStatement st = connect.prepareStatement("INSERT INTO registro "
								+ "(usuario, password, idPersonaje) VALUES (?,?,?)");
				st.setString(1, user.getUsername());
				st.setString(2, user.getPassword());
				st.setInt(3, user.getIdPj());
				st.execute();
				Servidor.getLog().append("El usuario " + user.getUsername()
						+ " se ha registrado." + System.lineSeparator());
				retorno = true;
			} else {
				Servidor.getLog().append("El usuario " + user.getUsername()
						+ " ya se encuentra en uso." + System.lineSeparator());
			}
		} catch (SQLException ex) {
			Servidor.getLog().append("Eror al intentar registrar el usuario "
						+ user.getUsername() + System.lineSeparator());
			System.err.println(ex.getMessage());
			
		}
		return retorno;
	}
/**
 * registrarPersonaje
 * @param paquetePersonaje
 * @param paqueteUsuario
 * @return
 */
	public boolean registrarPersonaje(final PaquetePersonaje paquetePersonaje,final PaqueteUsuario paqueteUsuario) {
		boolean retorno = false;
>>>>>>> master
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
			

<<<<<<< HEAD
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

=======
			// Registro al personaje en la base de datos
			PreparedStatement stRegistrarPersonaje = connect.prepareStatement(
					"INSERT INTO personaje (idInventario, idMochila,"
					+ "casta,raza,fuerza,destreza,inteligencia,saludTope,"
					+ "energiaTope,nombre,experiencia,nivel,idAlianza) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
					PreparedStatement.RETURN_GENERATED_KEYS);
			stRegistrarPersonaje.setInt(1, -1);
			stRegistrarPersonaje.setInt(2, -1);
			stRegistrarPersonaje.setString(NUMERO3, paquetePersonaje.getCasta());
			stRegistrarPersonaje.setString(NUMERO4, paquetePersonaje.getRaza());
			stRegistrarPersonaje.setInt(NUMERO5, paquetePersonaje.getFuerza());
			stRegistrarPersonaje.setInt(NUMERO6, paquetePersonaje.getDestreza());
			stRegistrarPersonaje.setInt(NUMERO7, paquetePersonaje.getInteligencia());
			stRegistrarPersonaje.setInt(NUMERO8, paquetePersonaje.getSaludTope());
			stRegistrarPersonaje.setInt(NUMERO9, paquetePersonaje.getEnergiaTope());
			stRegistrarPersonaje.setString(NUMERO10, paquetePersonaje.getNombre());
			stRegistrarPersonaje.setInt(NUMERO11, 0);
			stRegistrarPersonaje.setInt(NUMERO12, 1);
			stRegistrarPersonaje.setInt(NUMERO13, -1);
			stRegistrarPersonaje.execute();

			// Recupero la última key generada
			ResultSet rs = stRegistrarPersonaje.getGeneratedKeys();
			if (rs != null && rs.next()) {

				// Obtengo el id
				int idPersonaje = rs.getInt(1);

				// Le asigno el id al paquete personaje que voy a devolver
				paquetePersonaje.setId(idPersonaje);

				// Le asigno el personaje al usuario
				PreparedStatement stAsignarPersonaje = connect.prepareStatement(
							"UPDATE registro SET idPersonaje=? WHERE usuario=? AND password=?");
				stAsignarPersonaje.setInt(NUMERO1, idPersonaje);
				stAsignarPersonaje.setString(NUMERO2, paqueteUsuario.getUsername());
				stAsignarPersonaje.setString(NUMERO3, paqueteUsuario.getPassword());
				stAsignarPersonaje.execute();

				// Por ultimo registro el inventario y la mochila
				if (this.registrarInventarioMochila(idPersonaje)) {
					Servidor.getLog().append("El usuario " + paqueteUsuario.getUsername()
							+ " ha creado el personaje "
							+ paquetePersonaje.getId() + System.lineSeparator());
					retorno = true;
				} else {
					Servidor.getLog().append("Error al registrar la mochila y el inventario del usuario "
							+ paqueteUsuario.getUsername() + " con el personaje" + paquetePersonaje.getId() + System.lineSeparator());
				
				}
			}
			

		} catch (SQLException e) {
			Servidor.getLog().append(
					"Error al intentar crear el personaje "
			+ paquetePersonaje.getNombre() + System.lineSeparator());
		}
		return retorno;
	}
/**
 * registrarInventarioMochila
 * @param idInventarioMochila
 * @return
 */
	public boolean registrarInventarioMochila(final int idInventarioMochila) {
		boolean retorno = false; 
		try {
			// Preparo la consulta para el registro el inventario en la base de
			// datos
			PreparedStatement stRegistrarInventario = connect.prepareStatement(
					"INSERT INTO inventario(idInventario,manos1,manos2"
					+ ",pie,cabeza,pecho,accesorio) VALUES"
					+ " (?,-1,-1,-1,-1,-1,-1)");
			stRegistrarInventario.setInt(1, idInventarioMochila);

			// Preparo la consulta para el registro la mochila en la base de
			// datos
			PreparedStatement stRegistrarMochila = connect.prepareStatement(
					"INSERT INTO mochila("
					+ "idMochila,item1,item2,item3,item4,item5,item6,item7,item8,"
					+ "item9,item10,item11,item12,item13,item14,item15,item16,item17,"
					+ "item18,item19,item20) VALUES(?,-1,-1,-1,-1,-1,-1,"
					+ "-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1)");
			stRegistrarMochila.setInt(1, idInventarioMochila);

			// Registro inventario y mochila
			stRegistrarInventario.execute();
			stRegistrarMochila.execute();

			// Le asigno el inventario y la mochila al personaje
			PreparedStatement stAsignarPersonaje = connect
					.prepareStatement("UPDATE personaje SET idInventario=?,"
							+ " idMochila=? WHERE idPersonaje=?");
			stAsignarPersonaje.setInt(1, idInventarioMochila);
			stAsignarPersonaje.setInt(2, idInventarioMochila);
			stAsignarPersonaje.setInt(3, idInventarioMochila);
			stAsignarPersonaje.execute();

			Servidor.getLog().append("Se ha registrado el inventario de "
					+ idInventarioMochila + System.lineSeparator());
			retorno = true;

		} catch (SQLException e) {
			Servidor.getLog().append("Error al registrar el inventario de "
					+ idInventarioMochila + System.lineSeparator());
		
		}
		return retorno;
	}
/**
 * loguearUsuario
 * @param user
 * @return
 */
	public boolean loguearUsuario(final PaqueteUsuario user) {
		ResultSet result = null;
		boolean retorno =false;
		try {
			// Busco usuario y contraseña
			PreparedStatement st = connect
					.prepareStatement("SELECT * FROM registro WHERE usuario = ? AND password = ? ");
			st.setString(1, user.getUsername());
			st.setString(2, user.getPassword());
			result = st.executeQuery();

			// Si existe inicio sesion
			if (result.next()) {
				Servidor.getLog().append("El usuario " + user.getUsername()
				+ " ha iniciado sesión." + System.lineSeparator());
				retorno = true;
			}

			// Si no existe informo y devuelvo false
			Servidor.getLog().append("El usuario " + user.getUsername()
			+ " ha realizado un intento fallido de inicio de sesión." + System.lineSeparator());
			

		} catch (SQLException e) {
			Servidor.getLog().append("El usuario " + user.getUsername()
			+ " fallo al iniciar sesión." + System.lineSeparator());
			
		}
		return retorno;
>>>>>>> master
	}
/**
 * Logueo de usuario. Retorna si es posible o no el acceso.
 * @param user
 * @return V O F
 */
	public boolean loguearUsuario(PaqueteUsuario user) {

		boolean retorno = true;
		try {
<<<<<<< HEAD
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
=======
			int i = 2;
			int j = 1;
			PreparedStatement stActualizarPersonaje = connect
					.prepareStatement("UPDATE personaje SET fuerza=?, destreza=?, inteligencia=?,"
							+ " saludTope=?, energiaTope=?, experiencia=?, nivel=? "
							+ "  WHERE idPersonaje=?");
			stActualizarPersonaje.setInt(NUMERO1, paquetePersonaje.getFuerza());
			stActualizarPersonaje.setInt(NUMERO2, paquetePersonaje.getDestreza());
			stActualizarPersonaje.setInt(NUMERO3, paquetePersonaje.getInteligencia());
			stActualizarPersonaje.setInt(NUMERO4, paquetePersonaje.getSaludTope());
			stActualizarPersonaje.setInt(NUMERO5, paquetePersonaje.getEnergiaTope());
			stActualizarPersonaje.setInt(NUMERO6, paquetePersonaje.getExperiencia());
			stActualizarPersonaje.setInt(NUMERO7, paquetePersonaje.getNivel());
			stActualizarPersonaje.setInt(NUMERO8, paquetePersonaje.getId());
			stActualizarPersonaje.executeUpdate();
			PreparedStatement stDameItemsID = connect.prepareStatement(
					"SELECT * FROM mochila WHERE idMochila = ?");
			stDameItemsID.setInt(1, paquetePersonaje.getId());
			ResultSet resultadoItemsID = stDameItemsID.executeQuery();
			PreparedStatement stDatosItem = connect.prepareStatement(
								"SELECT * FROM item WHERE idItem = ?");
			ResultSet resultadoDatoItem = null;
			paquetePersonaje.eliminarItems();
			while (j <= NUMERO9) {
				if (resultadoItemsID.getInt(i) != -1) {
					stDatosItem.setInt(1, resultadoItemsID.getInt(i));
					resultadoDatoItem = stDatosItem.executeQuery();
					paquetePersonaje.anadirItem(resultadoDatoItem.getInt("idItem"),
							resultadoDatoItem.getString("nombre"),
							resultadoDatoItem.getInt("wereable"),
							resultadoDatoItem.getInt("bonusSalud"),
							resultadoDatoItem.getInt("bonusEnergia"),
							resultadoDatoItem.getInt("bonusFuerza"),
							resultadoDatoItem.getInt("bonusDestreza"),
							resultadoDatoItem.getInt("bonusInteligencia"),
							resultadoDatoItem.getString("foto"),
							resultadoDatoItem.getString("fotoEquipado"));
				}
				i++;
				j++;
			}
			Servidor.getLog().append("El personaje " + paquetePersonaje.getNombre()
						+ " se ha actualizado con éxito."  + System.lineSeparator());;
		} catch (SQLException e) {
			Servidor.getLog().append("Fallo al intentar actualizar el personaje "
						+ paquetePersonaje.getNombre()  + System.lineSeparator());
		}
	}
/**
 * getter de personaje
 * @param user
 * @return
 * @throws IOException
 */
	public PaquetePersonaje getPersonaje(final PaqueteUsuario user) throws IOException {
		ResultSet result = null;
		ResultSet resultadoItemsID = null;
		ResultSet resultadoDatoItem = null;
>>>>>>> master
		int i = 2;
		int j = 0;
		if (!session.isConnected())
			connect();

<<<<<<< HEAD
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
=======
			// Obtengo el id
			int idPersonaje = result.getInt("idPersonaje");

			// Selecciono los datos del personaje
			PreparedStatement stSeleccionarPersonaje = connect
					.prepareStatement("SELECT * FROM personaje WHERE idPersonaje = ?");
			stSeleccionarPersonaje.setInt(1, idPersonaje);
			result = stSeleccionarPersonaje.executeQuery();
			// Traigo los id de los items correspondientes a mi personaje
			PreparedStatement stDameItemsID = connect.prepareStatement("SELECT * FROM mochila WHERE idMochila = ?");
			stDameItemsID.setInt(1, idPersonaje);
			resultadoItemsID = stDameItemsID.executeQuery();
			// Traigo los datos del item
			PreparedStatement stDatosItem = connect.prepareStatement("SELECT * FROM item WHERE idItem = ?");
			
			// Obtengo los atributos del personaje
			PaquetePersonaje personaje = new PaquetePersonaje();
			personaje.setId(idPersonaje);
			personaje.setRaza(result.getString("raza"));
			personaje.setCasta(result.getString("casta"));
			personaje.setFuerza(result.getInt("fuerza"));
			personaje.setInteligencia(result.getInt("inteligencia"));
			personaje.setDestreza(result.getInt("destreza"));
			personaje.setEnergiaTope(result.getInt("energiaTope"));
			personaje.setSaludTope(result.getInt("saludTope"));
			personaje.setNombre(result.getString("nombre"));
			personaje.setExperiencia(result.getInt("experiencia"));
			personaje.setNivel(result.getInt("nivel"));
			while (j <= NUMERO9) {
				if (resultadoItemsID.getInt(i) != -1) {
					stDatosItem.setInt(1, resultadoItemsID.getInt(i));
					resultadoDatoItem = stDatosItem.executeQuery();
					personaje.anadirItem(resultadoDatoItem.getInt("idItem"),
							resultadoDatoItem.getString("nombre"),
							resultadoDatoItem.getInt("wereable"),
							resultadoDatoItem.getInt("bonusSalud"),
							resultadoDatoItem.getInt("bonusEnergia"),
							resultadoDatoItem.getInt("bonusFuerza"),
							resultadoDatoItem.getInt("bonusDestreza"),
							resultadoDatoItem.getInt("bonusInteligencia"),
							resultadoDatoItem.getString("foto"),
							resultadoDatoItem.getString("fotoEquipado"));
				}
				i++;
				j++;
			}
			
			// Devuelvo el paquete personaje con sus datos
			return personaje;
		} catch (SQLException ex) {
			Servidor.getLog().append("Fallo al intentar recuperar el personaje "
					+ user.getUsername() + System.lineSeparator());
			Servidor.getLog().append(ex.getMessage() + System.lineSeparator());
		}
		return new PaquetePersonaje();
	}
	/**
	 * getter de usuario
	 * @param usuario
	 * @return
	 */
	public PaqueteUsuario getUsuario(final String usuario) {
		ResultSet result = null;
		PreparedStatement st;
		try {
			st = connect.prepareStatement("SELECT * FROM registro WHERE usuario = ?");
			st.setString(1, usuario);
			result = st.executeQuery();
			String password = result.getString("password");
			int idPersonaje = result.getInt("idPersonaje");
			PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
			paqueteUsuario.setUsername(usuario);
			paqueteUsuario.setPassword(password);
			paqueteUsuario.setIdPj(idPersonaje);
			return paqueteUsuario;
		} catch (SQLException e) {
			Servidor.getLog().append("Fallo al intentar recuperar el usuario " + usuario + System.lineSeparator());
			Servidor.getLog().append(e.getMessage() + System.lineSeparator());
		}
		return new PaqueteUsuario();
	}
/**
 * actualizarInventario
 * @param paquetePersonaje
 */
	public void actualizarInventario(final PaquetePersonaje paquetePersonaje) {
>>>>>>> master
		int i = 0;
		try {
<<<<<<< HEAD
=======
			stActualizarMochila = connect.prepareStatement(
							"UPDATE mochila SET item1=? ,item2=? ,item3=? ,item4=? ,item5=? ,item6=? ,item7=? ,item8=? ,item9=? "
							+ ",item10=? ,item11=? ,item12=? ,item13=? ,item14=? ,item15=? ,item16=? ,item17=? ,item18=? ,item19=?"
							+ " ,item20=? WHERE idMochila=?");
>>>>>>> master
			while (i < paquetePersonaje.getCantItems()) {
				mochila.establecerItem(i + 1, paquetePersonaje.getItemID(i));
				i++;
			}
<<<<<<< HEAD

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
=======
			for (int j = paquetePersonaje.getCantItems(); j < NUMERO20; j++) {
				stActualizarMochila.setInt(j + 1, -1);
			}
			stActualizarMochila.setInt(NUMERO21, paquetePersonaje.getId());
			stActualizarMochila.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e);
		}
	}
	/**
	 * Actualizar inv
	 * @param idPersonaje
	 */
	public void actualizarInventario(final int idPersonaje) {
>>>>>>> master
		int i = 0;
		PaquetePersonaje paquetePersonaje = Servidor.getPersonajesConectados().get(idPersonaje);
		Transaction tx = null;
		Mochila mochila = new Mochila(paquetePersonaje.getId());
		try {
<<<<<<< HEAD
=======
			stActualizarMochila = connect.prepareStatement(
							"UPDATE mochila SET item1=? ,item2=? ,item3=? ,"
							+ "item4=? ,item5=? ,item6=? ,item7=? ,item8=? ,item9=? "
							+ ",item10=? ,item11=? ,item12=? ,item13=? ,item14=? ,item15=? ,item16=? ,item17=? ,item18=?"
							+ " ,item19=? ,item20=? WHERE idMochila=?");
>>>>>>> master
			while (i < paquetePersonaje.getCantItems()) {
				mochila.establecerItem(i + 1, paquetePersonaje.getItemID(i));
				i++;
			}
<<<<<<< HEAD

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
=======
			if (paquetePersonaje.getCantItems() < NUMERO9) {
				int itemGanado = new Random().nextInt(NUMERO29);
				itemGanado += 1;
				stActualizarMochila.setInt(paquetePersonaje.getCantItems() +1, itemGanado);
				for (int j = paquetePersonaje.getCantItems() + 2; j < NUMERO20; j ++) {
					stActualizarMochila.setInt(j, -1);
				}
			} else {
				for (int j = paquetePersonaje.getCantItems() + 1; j < NUMERO20; j ++) {
					stActualizarMochila.setInt(j, -1);
				}
			}
			stActualizarMochila.setInt(NUMERO21, paquetePersonaje.getId());
			stActualizarMochila.executeUpdate();

		} catch (SQLException e) {
			Servidor.getLog().append("Falló al intentar actualizar inventario de" + idPersonaje + "\n");
		}
	}
	/**
	 * ActualizarPersonajeSubioNivel
	 * @param paquetePersonaje
	 */
	public void actualizarPersonajeSubioNivel(final PaquetePersonaje paquetePersonaje) {
		try {
			PreparedStatement stActualizarPersonaje = connect
					.prepareStatement("UPDATE personaje SET fuerza=?, destreza=?, "
							+ "inteligencia=?, saludTope=?, energiaTope=?, experiencia=?, nivel=? "
							+ "  WHERE idPersonaje=?");
			stActualizarPersonaje.setInt(NUMERO1, paquetePersonaje.getFuerza());
			stActualizarPersonaje.setInt(NUMERO2, paquetePersonaje.getDestreza());
			stActualizarPersonaje.setInt(NUMERO3, paquetePersonaje.getInteligencia());
			stActualizarPersonaje.setInt(NUMERO4, paquetePersonaje.getSaludTope());
			stActualizarPersonaje.setInt(NUMERO5, paquetePersonaje.getEnergiaTope());
			stActualizarPersonaje.setInt(NUMERO6, paquetePersonaje.getExperiencia());
			stActualizarPersonaje.setInt(NUMERO7, paquetePersonaje.getNivel());
			stActualizarPersonaje.setInt(NUMERO8, paquetePersonaje.getId());
			stActualizarPersonaje.executeUpdate();
			Servidor.getLog().append("El personaje " + paquetePersonaje.getNombre()
				+ " se ha actualizado con éxito."  + System.lineSeparator());;
		} catch (SQLException e) {
			Servidor.getLog().append("Fallo al intentar actualizar el personaje "+ paquetePersonaje.getNombre()  + System.lineSeparator());
>>>>>>> master
		}
	}

	public Connection getConnect() {
		return connect;
	}

	public void setConnect(Connection connect) {
		this.connect = connect;
	}
	
	
}
