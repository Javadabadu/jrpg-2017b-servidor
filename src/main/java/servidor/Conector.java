package servidor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
/**
 * 
 * @author Javadabadu
 *
 */
public class Conector {

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
					+ System.lineSeparator());
		}
	}

	public void close() {
		try {
			connect.close();
		} catch (SQLException ex) {
			Servidor.getLog().append("Error al intentar cerrar la "
					+ "conexión con la base de datos." + System.lineSeparator());
			Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public boolean registrarUsuario(PaqueteUsuario user) {
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
		try {

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
	}

	public void actualizarPersonaje(PaquetePersonaje paquetePersonaje) {
		try {
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
		int i = 2;
		int j = 0;
		try {
			// Selecciono el personaje de ese usuario
			PreparedStatement st = connect.prepareStatement("SELECT * FROM registro WHERE usuario = ?");
			st.setString(1, user.getUsername());
			result = st.executeQuery();

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
		int i = 0;
		PreparedStatement stActualizarMochila;
		try {
			stActualizarMochila = connect.prepareStatement(
							"UPDATE mochila SET item1=? ,item2=? ,item3=? ,item4=? ,item5=? ,item6=? ,item7=? ,item8=? ,item9=? "
							+ ",item10=? ,item11=? ,item12=? ,item13=? ,item14=? ,item15=? ,item16=? ,item17=? ,item18=? ,item19=?"
							+ " ,item20=? WHERE idMochila=?");
			while (i < paquetePersonaje.getCantItems()) {
				stActualizarMochila.setInt(i + 1, paquetePersonaje.getItemID(i));
				i++;
			}
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
		int i = 0;
		PaquetePersonaje paquetePersonaje = Servidor.getPersonajesConectados().get(idPersonaje);
		PreparedStatement stActualizarMochila;
		try {
			stActualizarMochila = connect.prepareStatement(
							"UPDATE mochila SET item1=? ,item2=? ,item3=? ,"
							+ "item4=? ,item5=? ,item6=? ,item7=? ,item8=? ,item9=? "
							+ ",item10=? ,item11=? ,item12=? ,item13=? ,item14=? ,item15=? ,item16=? ,item17=? ,item18=?"
							+ " ,item19=? ,item20=? WHERE idMochila=?");
			while (i < paquetePersonaje.getCantItems()) {
				stActualizarMochila.setInt(i + 1, paquetePersonaje.getItemID(i));
				i++;
			}
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
		}
	}

	public Connection getConnect() {
		return connect;
	}

	public void setConnect(Connection connect) {
		this.connect = connect;
	}
	
	
}
