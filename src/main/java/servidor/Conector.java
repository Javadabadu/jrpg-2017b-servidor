package servidor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
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
	Connection connect;
	private Session session;

	public void connect() {
		try {

			Servidor.log.append("Estableciendo conexión con la base de datos..." + System.lineSeparator());
			// connect = DriverManager.getConnection("jdbc:sqlite:" + url);
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

			if(session.isDirty())
				session.clear();
			// Primero commiteo al personaje
			tx = session.beginTransaction();
			session.save(paquetePersonaje);

			paqueteUsuario.setIdPj(paquetePersonaje.getId());
			mochila = new Mochila(paquetePersonaje.getId());
			inventario = new Inventario(paquetePersonaje.getId());
			paquetePersonaje.setIdInventario(paquetePersonaje.getId());
			paquetePersonaje.setIdMochila(paquetePersonaje.getId());
			session.update(paquetePersonaje);
			session.update(paqueteUsuario);
			session.save(mochila);
			session.save(inventario);
			tx.commit();
		} catch (HibernateException he) {
			Servidor.log.append(
					"Error al intentar crear el personaje " + paquetePersonaje.getNombre() + System.lineSeparator());
			he.getStackTrace();
			retorno = false;
			if (tx != null)
				tx.rollback();
		}

		return retorno;
	}

	public boolean registrarInventarioMochila(int idInventarioMochila) {
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

		// PreparedStatement stAsignarPersonaje = connect
		// .prepareStatement("UPDATE personaje SET idInventario=?, idMochila=? WHERE
		// idPersonaje=?");
		// stAsignarPersonaje.setInt(1, idInventarioMochila);
		// stAsignarPersonaje.setInt(2, idInventarioMochila);
		// stAsignarPersonaje.setInt(3, idInventarioMochila);
		// stAsignarPersonaje.execute();
		//
		//
		// Preparo la consulta para el registro el inventario en la base de
		// datos

		// PreparedStatement stRegistrarInventario = connect.prepareStatement(
		// "INSERT INTO
		// inventario(idInventario,manos1,manos2,pie,cabeza,pecho,accesorio) VALUES
		// (?,-1,-1,-1,-1,-1,-1)");
		// stRegistrarInventario.setInt(1, idInventarioMochila);
		// Preparo la consulta para el registro la mochila en la base de
		// datos
		// PreparedStatement stRegistrarMochila = connect.prepareStatement(
		// "INSERT INTO
		// mochila(idMochila,item1,item2,item3,item4,item5,item6,item7,item8,item9,item10,item11,item12,item13,item14,item15,item16,item17,item18,item19,item20)
		// VALUES(?,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1)");
		// stRegistrarMochila.setInt(1, idInventarioMochila);
		//
		// Registro inventario y mochila
		// stRegistrarInventario.execute();
		// stRegistrarMochila.execute();
		//

		// Le asigno el inventario y la mochila al personaje
	}

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
	// public boolean loguearUsuario(PaqueteUsuario user) {
	// ResultSet result = null;
	// try {
	// // Busco usuario y contraseña
	// PreparedStatement st = connect
	// .prepareStatement("SELECT * FROM registro WHERE usuario = ? AND password = ?
	// ");
	// st.setString(1, user.getUsername());
	// st.setString(2, user.getPassword());
	// result = st.executeQuery();
	//
	// // Si existe inicio sesion
	// if (result.next()) {
	// Servidor.log
	// .append("El usuario " + user.getUsername() + " ha iniciado sesión." +
	// System.lineSeparator());
	// return true;
	// }
	//
	// // Si no existe informo y devuelvo false
	// Servidor.log.append("El usuario " + user.getUsername()
	// + " ha realizado un intento fallido de inicio de sesión." +
	// System.lineSeparator());
	// return false;
	//
	// } catch (SQLException e) {
	// Servidor.log
	// .append("El usuario " + user.getUsername() + " fallo al iniciar sesión." +
	// System.lineSeparator());
	// return false;
	// }
	//
	// }

	public PaquetePersonaje getPersonaje(PaqueteUsuario user) throws IOException {

//		ResultSet result = null;
//		ResultSet resultadoItemsID = null;
//		ResultSet resultadoDatoItem = null;
		PaquetePersonaje personaje = new PaquetePersonaje();
		int i = 2;
		int j = 0;
		if (!session.isConnected())
			connect();

		try {
			// Selecciono el personaje de ese usuario
			// PreparedStatement st = connect.prepareStatement("SELECT * FROM registro WHERE
			// usuario = ?");
			// st.setString(1, user.getUsername());
			// result = st.executeQuery();
			CriteriaBuilder builderUsuario = session.getCriteriaBuilder();
			CriteriaQuery<PaqueteUsuario> query = builderUsuario.createQuery(PaqueteUsuario.class);
			Root<PaqueteUsuario> root = query.from(PaqueteUsuario.class);
			query.select(root).where(builderUsuario.equal(root.get("idPersonaje"), user.getIdPj()));
			// Obtengo el id
			// int idPersonaje = result.getInt("idPersonaje");
			List<PaqueteUsuario> consultaUser = session.createQuery(query).getResultList();
			if (consultaUser.size() != 0) {
				int idPersonaje = consultaUser.get(0).getIdPj();

				CriteriaBuilder builderPer = session.getCriteriaBuilder();
				CriteriaQuery<PaquetePersonaje> queryPer = builderPer.createQuery(PaquetePersonaje.class);
				Root<PaquetePersonaje> rootPer = queryPer.from(PaquetePersonaje.class);
				queryPer.select(rootPer).where(builderPer.equal(rootPer.get("id"), idPersonaje));
				List<PaquetePersonaje> consultaPer = session.createQuery(queryPer).getResultList();
				if(consultaPer.size() != 0)
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
	                    queryItem.select(rootItem).where(builderItem.equal(rootItem.get("idItem"), consultaMochi.obtenerItem(i)));
	                     Item item = session.createQuery(queryItem).getSingleResult();

	                    if (item != null) {
	                        personaje.anadirItem(item.getIdItem(), item.getNombre(), item.getWearLocation(),
	                                item.getBonusSalud(), item.getBonusEnergia(), item.getBonusFuerza(),
	                                item.getBonusDestreza(), item.getBonusInteligencia(), item.getFoto().toString(),
	                                item.getFotoEquipado());
	                    }
	             
	                }
	                i++;
	                j++;
	            }
			} else {
				Servidor.log
				.append("Fallo al intentar recuperar el personaje " + user.getUsername() + System.lineSeparator());
				
			}

			} catch (HibernateException ex) {
				Servidor.log
						.append("Fallo al intentar recuperar el personaje " + user.getUsername() + System.lineSeparator());
				Servidor.log.append(ex.getMessage() + System.lineSeparator());
			}

			return personaje;
		}
			// Selecciono los datos del personaje
			// PreparedStatement stSeleccionarPersonaje = connect
			// .prepareStatement("SELECT * FROM personaje WHERE idPersonaje = ?");
			// stSeleccionarPersonaje.setInt(1, idPersonaje);
			// result = stSeleccionarPersonaje.executeQuery();
			// Traigo los id de los items correspondientes a mi personaje
			// PreparedStatement stDameItemsID = connect.prepareStatement("SELECT * FROM
			// mochila WHERE idMochila = ?");
			// stDameItemsID.setInt(1, idPersonaje);
			// resultadoItemsID = stDameItemsID.executeQuery();
			// // Traigo los datos del item
			// PreparedStatement stDatosItem = connect.prepareStatement("SELECT * FROM item
			// WHERE idItem = ?");

			// // Obtengo los atributos del personaje
			// PaquetePersonaje personaje = new PaquetePersonaje();
			// personaje.setId(idPersonaje);
			// personaje.setRaza(result.getString("raza"));
			// personaje.setCasta(result.getString("casta"));
			// personaje.setFuerza(result.getInt("fuerza"));
			// personaje.setInteligencia(result.getInt("inteligencia"));
			// personaje.setDestreza(result.getInt("destreza"));
			// personaje.setEnergiaTope(result.getInt("energiaTope"));
			// personaje.setSaludTope(result.getInt("saludTope"));
			// personaje.setNombre(result.getString("nombre"));
			// personaje.setExperiencia(result.getInt("experiencia"));
			// personaje.setNivel(result.getInt("nivel"));
//
//			while (j <= 9) {
//				if (resultadoItemsID.getInt(i) != -1) {
//					stDatosItem.setInt(1, resultadoItemsID.getInt(i));
//					resultadoDatoItem = stDatosItem.executeQuery();
//					personaje.anadirItem(resultadoDatoItem.getInt("idItem"), resultadoDatoItem.getString("nombre"),
//							resultadoDatoItem.getInt("wereable"), resultadoDatoItem.getInt("bonusSalud"),
//							resultadoDatoItem.getInt("bonusEnergia"), resultadoDatoItem.getInt("bonusFuerza"),
//							resultadoDatoItem.getInt("bonusDestreza"), resultadoDatoItem.getInt("bonusInteligencia"),
//							resultadoDatoItem.getString("foto"), resultadoDatoItem.getString("fotoEquipado"));
//				}
//				i++;
//				j++;
//			}

			// Devuelvo el paquete personaje con sus datos
	

	public PaqueteUsuario getUsuario(String usuario) {
		PaqueteUsuario user = new PaqueteUsuario();
		CriteriaBuilder registroBuilder ;
		CriteriaQuery<PaqueteUsuario> userQuery;
		Root<PaqueteUsuario> userRoot;
		
		try {
		if(!session.isConnected())
			connect();
		
		registroBuilder = session.getCriteriaBuilder();
		userQuery = registroBuilder.createQuery(PaqueteUsuario.class);
		userRoot = userQuery.from(PaqueteUsuario.class);
		
		userQuery.select(userRoot).where(registroBuilder.equal(userRoot.get("username"), usuario));
		user = session.createQuery(userQuery).getSingleResult();
		}catch(HibernateException he) {
			Servidor.log.append("Fallo al intentar recuperar el usuario " + usuario + System.lineSeparator());
			Servidor.log.append(he.getMessage() + System.lineSeparator());
		}
		return user;
	}
//	ResultSet result = null;
//	PreparedStatement st;
//
//	try {
//		st = connect.prepareStatement("SELECT * FROM registro WHERE usuario = ?");
//		st.setString(1, usuario);
//		result = st.executeQuery();
//
//		String password = result.getString("password");
//		int idPersonaje = result.getInt("idPersonaje");
//
//		PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
//		paqueteUsuario.setUsername(usuario);
//		paqueteUsuario.setPassword(password);
//		paqueteUsuario.setIdPj(idPersonaje);
//
//		return paqueteUsuario;
//	} catch (SQLException e) {
//		Servidor.log.append("Fallo al intentar recuperar el usuario " + usuario + System.lineSeparator());
//		Servidor.log.append(e.getMessage() + System.lineSeparator());
//	}
//
//	return new PaqueteUsuario();

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
	int i = 2 , j = 1;
		try {
			if(!session.isConnected())
				connect();
			tx = session.beginTransaction();
			session.update(paquetePersonaje);
			tx.commit();
			
			mochiBuilder = session.getCriteriaBuilder();
			mochiQuery = mochiBuilder.createQuery(Mochila.class);
			mochiRoot = mochiQuery.from(Mochila.class);
			mochiQuery.select(mochiRoot).where(mochiBuilder.equal(mochiRoot.get("idMochila"), paquetePersonaje.getId() ));
			mochila = session.createQuery(mochiQuery).getSingleResult();
			
			if(mochila != null) {
				itemBuilder = session.getCriteriaBuilder();
				itemQuery = itemBuilder.createQuery(Item.class);
				itemRoot = itemQuery.from(Item.class);
				
				while (j <= CANTITEMS) {
					if (mochila.obtenerItem(i) != -1) {
						itemQuery.select(itemRoot).where(itemBuilder.equal(itemRoot.get("idItem"), mochila.obtenerItem(i)));
						item = session.createQuery(itemQuery).getSingleResult();
						
						paquetePersonaje.anadirItem(item.getIdItem(),
								item.getNombre(), item.getWearLocation(),
								item.getBonusSalud(), item.getBonusEnergia(),
								item.getBonusFuerza(), item.getBonusDestreza(),
								item.getBonusInteligencia(), item.getFoto().toString(),
								item.getFotoEquipado());
					}
					i++;
					j++;
				}
				Servidor.log.append("El personaje " + paquetePersonaje.getNombre() + " se ha actualizado con éxito."
						+ System.lineSeparator());
			}else {
				Servidor.log.append("No se encontro la mochila del personaje " + paquetePersonaje.getNombre() + System.lineSeparator());
			}
			
			
		} catch (HibernateException | IOException e ) {
			Servidor.log.append("Fallo al intentar actualizar el personaje " + paquetePersonaje.getNombre()
					+ System.lineSeparator());
			
			if(tx != null)
				tx.rollback();
		}

	}

//	int i = 2;
//	int j = 1;
//	PreparedStatement stActualizarPersonaje = connect.prepareStatement(
//			"UPDATE personaje SET fuerza=?, destreza=?, inteligencia=?, saludTope=?, energiaTope=?, experiencia=?, nivel=? "
//					+ "  WHERE idPersonaje=?");
//
//	stActualizarPersonaje.setInt(1, paquetePersonaje.getFuerza());
//	stActualizarPersonaje.setInt(2, paquetePersonaje.getDestreza());
//	stActualizarPersonaje.setInt(3, paquetePersonaje.getInteligencia());
//	stActualizarPersonaje.setInt(4, paquetePersonaje.getSaludTope());
//	stActualizarPersonaje.setInt(5, paquetePersonaje.getEnergiaTope());
//	stActualizarPersonaje.setInt(6, paquetePersonaje.getExperiencia());
//	stActualizarPersonaje.setInt(7, paquetePersonaje.getNivel());
//	stActualizarPersonaje.setInt(8, paquetePersonaje.getId());
//	stActualizarPersonaje.executeUpdate();
//
//	PreparedStatement stDameItemsID = connect.prepareStatement("SELECT * FROM mochila WHERE idMochila = ?");
//	stDameItemsID.setInt(1, paquetePersonaje.getId());
//	ResultSet resultadoItemsID = stDameItemsID.executeQuery();
//	PreparedStatement stDatosItem = connect.prepareStatement("SELECT * FROM item WHERE idItem = ?");
//	ResultSet resultadoDatoItem = null;
//	paquetePersonaje.eliminarItems();
//
//	while (j <= 9) {
//		if (resultadoItemsID.getInt(i) != -1) {
//			stDatosItem.setInt(1, resultadoItemsID.getInt(i));
//			resultadoDatoItem = stDatosItem.executeQuery();
//
//			paquetePersonaje.anadirItem(resultadoDatoItem.getInt("idItem"),
//					resultadoDatoItem.getString("nombre"), resultadoDatoItem.getInt("wereable"),
//					resultadoDatoItem.getInt("bonusSalud"), resultadoDatoItem.getInt("bonusEnergia"),
//					resultadoDatoItem.getInt("bonusFuerza"), resultadoDatoItem.getInt("bonusDestreza"),
//					resultadoDatoItem.getInt("bonusInteligencia"), resultadoDatoItem.getString("foto"),
//					resultadoDatoItem.getString("fotoEquipado"));
//		}
//		i++;
//		j++;
//	}
//	Servidor.log.append("El personaje " + paquetePersonaje.getNombre() + " se ha actualizado con éxito."
//			+ System.lineSeparator());
//	;
//} catch (SQLException e) {
//	Servidor.log.append("Fallo al intentar actualizar el personaje " + paquetePersonaje.getNombre()
//			+ System.lineSeparator());
//}
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
			if(tx != null)
				tx.rollback();
		}
	}
	
//	int i = 0;
//	PreparedStatement stActualizarMochila;
//	try {
//		stActualizarMochila = connect.prepareStatement(
//				"UPDATE mochila SET item1=? ,item2=? ,item3=? ,item4=? ,item5=? ,item6=? ,item7=? ,item8=? ,item9=? "
//						+ ",item10=? ,item11=? ,item12=? ,item13=? ,item14=? ,item15=? ,item16=? ,item17=? ,item18=? ,item19=? ,item20=? WHERE idMochila=?");
//		while (i < paquetePersonaje.getCantItems()) {
//			stActualizarMochila.setInt(i + 1, paquetePersonaje.getItemID(i));
//			i++;
//		}
//		for (int j = paquetePersonaje.getCantItems(); j < 20; j++) {
//			stActualizarMochila.setInt(j + 1, -1);
//		}
//		stActualizarMochila.setInt(21, paquetePersonaje.getId());
//		stActualizarMochila.executeUpdate();
//
//	} catch (SQLException e) {
//	}

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
			
			if (paquetePersonaje.getCantItems() < 9) {
				int itemGanado = new Random().nextInt(29);
				itemGanado += 1;
				mochila.establecerItem(paquetePersonaje.getCantItems() + 1, itemGanado);
			}

			tx = session.beginTransaction();
			session.update(mochila);
			tx.commit();
		} catch (HibernateException he) {
			if(tx != null)
				tx.rollback();
		}
	}

//	int i = 0;
//	PaquetePersonaje paquetePersonaje = Servidor.getPersonajesConectados().get(idPersonaje);
//	PreparedStatement stActualizarMochila;
//	try {
//		stActualizarMochila = connect.prepareStatement(
//				"UPDATE mochila SET item1=? ,item2=? ,item3=? ,item4=? ,item5=? ,item6=? ,item7=? ,item8=? ,item9=? "
//						+ ",item10=? ,item11=? ,item12=? ,item13=? ,item14=? ,item15=? ,item16=? ,item17=? ,item18=? ,item19=? ,item20=? WHERE idMochila=?");
//		while (i < paquetePersonaje.getCantItems()) {
//			stActualizarMochila.setInt(i + 1, paquetePersonaje.getItemID(i));
//			i++;
//		}
//		if (paquetePersonaje.getCantItems() < 9) {
//			int itemGanado = new Random().nextInt(29);
//			itemGanado += 1;
//			stActualizarMochila.setInt(paquetePersonaje.getCantItems() + 1, itemGanado);
//			for (int j = paquetePersonaje.getCantItems() + 2; j < 20; j++) {
//				stActualizarMochila.setInt(j, -1);
//			}
//		} else {
//			for (int j = paquetePersonaje.getCantItems() + 1; j < 20; j++) {
//				stActualizarMochila.setInt(j, -1);
//			}
//		}
//		stActualizarMochila.setInt(21, paquetePersonaje.getId());
//		stActualizarMochila.executeUpdate();
//
//	} catch (SQLException e) {
//		Servidor.log.append("Falló al intentar actualizar inventario de" + idPersonaje + "\n");
//	}
	public void actualizarPersonajeSubioNivel(PaquetePersonaje paquetePersonaje) {
		Transaction tx = null;
		try {
				if(!session.isConnected())
					connect();
				
				tx = session.beginTransaction();
				session.update(paquetePersonaje);
				tx.commit();
			Servidor.log.append("El personaje " + paquetePersonaje.getNombre() + " se ha actualizado con éxito."
					+ System.lineSeparator());
			;
		} catch (HibernateException e) {
			Servidor.log.append("Fallo al intentar actualizar el personaje " + paquetePersonaje.getNombre()
					+ System.lineSeparator());
		}finally {
			if(tx != null)
				tx.rollback();
		}
	}
}

//PreparedStatement stActualizarPersonaje = connect.prepareStatement(
//		"UPDATE personaje SET fuerza=?, destreza=?, inteligencia=?, saludTope=?, energiaTope=?, experiencia=?, nivel=? "
//				+ "  WHERE idPersonaje=?");
//
//stActualizarPersonaje.setInt(1, paquetePersonaje.getFuerza());
//stActualizarPersonaje.setInt(2, paquetePersonaje.getDestreza());
//stActualizarPersonaje.setInt(3, paquetePersonaje.getInteligencia());
//stActualizarPersonaje.setInt(4, paquetePersonaje.getSaludTope());
//stActualizarPersonaje.setInt(5, paquetePersonaje.getEnergiaTope());
//stActualizarPersonaje.setInt(6, paquetePersonaje.getExperiencia());
//stActualizarPersonaje.setInt(7, paquetePersonaje.getNivel());
//stActualizarPersonaje.setInt(8, paquetePersonaje.getId());
//
//stActualizarPersonaje.executeUpdate();
//
//Servidor.log.append("El personaje " + paquetePersonaje.getNombre() + " se ha actualizado con éxito."
//		+ System.lineSeparator());
//;