package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.google.gson.Gson;

import comandos.ComandosServer;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteAtacar;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteDeMovimientos;
import mensajeria.PaqueteDePersonajes;
import mensajeria.PaqueteFinalizarBatalla;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaqueteNpc;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
/**
 * 
 * @author Javadabadu
 *
 */
public class EscuchaCliente extends Thread {
	private final Socket socket;
	private final ObjectInputStream entrada;
	private final ObjectOutputStream salida;
	private int idPersonaje;
	private final Gson gson = new Gson();
	private PaquetePersonaje paquetePersonaje;
	private PaqueteMovimiento paqueteMovimiento;
	private PaqueteBatalla paqueteBatalla;
	private PaqueteAtacar paqueteAtacar;
	private PaqueteFinalizarBatalla paqueteFinalizarBatalla;
	private PaqueteUsuario paqueteUsuario;
	private PaqueteDeMovimientos paqueteDeMovimiento;
	private PaqueteDePersonajes paqueteDePersonajes;
/**
 * Escucha cliente
 * @param ip
 * @param socket
 * @param entrada
 * @param salida
 * @throws IOException
 */
	public EscuchaCliente(final String ip,final Socket socket,
							final ObjectInputStream entrada,final ObjectOutputStream salida) throws IOException {
		this.socket = socket;
		this.entrada = entrada;
		this.salida = salida;
		paquetePersonaje = new PaquetePersonaje();
	}
/**
 * Metodo run
 */
	public void run() {
		try {
			ComandosServer comand;
			Paquete paquete;
			Paquete paqueteSv = new Paquete(null, 0);
			paqueteUsuario = new PaqueteUsuario();

			String cadenaLeida = (String) entrada.readObject();
		
			while (!((paquete = gson.fromJson(cadenaLeida, Paquete.class)).getComando()
					== Comando.DESCONECTAR)) {
								

				comand = (ComandosServer) paquete.getObjeto(Comando.NOMBREPAQUETE);
				comand.setCadena(cadenaLeida);
				comand.setEscuchaCliente(this);
				comand.ejecutar();
				cadenaLeida = (String) entrada.readObject();
			}

			entrada.close();
			salida.close();
			socket.close();

			Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
			Servidor.getUbicacionPersonajes().remove(paquetePersonaje.getId());
			Servidor.getClientesConectados().remove(this);

			for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
				paqueteDePersonajes = new PaqueteDePersonajes(Servidor.getPersonajesConectados());
				paqueteDePersonajes.setComando(Comando.CONEXION);
				conectado.salida.writeObject(gson.toJson(
									paqueteDePersonajes, PaqueteDePersonajes.class));
			}
			Servidor.getLog().append(paquete.getIp() + " se ha desconectado."
						+ System.lineSeparator());
		} catch (IOException | ClassNotFoundException e) {
			Servidor.getLog().append("Error de conexion: "
						+ e.getMessage() + System.lineSeparator());
		}
	}
/**
 * devuelve socket
 * @return
 */
	public Socket getSocket() {
		return socket;
	}
/**
 * devuelve entrada
 * @return
 */
	public ObjectInputStream getEntrada() {
		return entrada;
	}
/**
 * devuelve salida
 * @return
 */
	public ObjectOutputStream getSalida() {
		return salida;
	}
/**
 * devuelve paquete personaje
 * @return
 */
	public PaquetePersonaje getPaquetePersonaje() {
		return paquetePersonaje;
	}
/**
 * devuelve id personaje
 * @return
 */
	public int getIdPersonaje() {
		return idPersonaje;
	}
/**
 * devuelve paquete movimiento
 * @return
 */
	public PaqueteMovimiento getPaqueteMovimiento() {
		return paqueteMovimiento;
	}
/**
 * settea paquete movimiento
 * @param paqueteMovimiento
 */
	public void setPaqueteMovimiento(final PaqueteMovimiento paqueteMovimiento) {
		this.paqueteMovimiento = paqueteMovimiento;
	}
/**
 * devuelve paquete batalla
 * @return
 */
	public PaqueteBatalla getPaqueteBatalla() {
		return paqueteBatalla;
	}
/**
 * settea paquete batalla
 * @param paqueteBatalla
 */
	public void setPaqueteBatalla(final PaqueteBatalla paqueteBatalla) {
		this.paqueteBatalla = paqueteBatalla;
	}
/**
 * devuelve paquete atacar
 * @return
 */
	public PaqueteAtacar getPaqueteAtacar() {
		return paqueteAtacar;
	}
/**
 * settea paquete atacar
 * @param paqueteAtacar
 */
	public void setPaqueteAtacar(final PaqueteAtacar paqueteAtacar) {
		this.paqueteAtacar = paqueteAtacar;
	}
/**
 * devuelve paquete finalizar batalla
 * @return
 */
	public PaqueteFinalizarBatalla getPaqueteFinalizarBatalla() {
		return paqueteFinalizarBatalla;
	}
/**
 * settea paquete finalizar batalla
 * @param paqueteFinalizarBatalla
 */
	public void setPaqueteFinalizarBatalla(PaqueteFinalizarBatalla paqueteFinalizarBatalla) {
		this.paqueteFinalizarBatalla = paqueteFinalizarBatalla;
	}
/**
 * devuelve paquete de movimiento
 * @return
 */
	public PaqueteDeMovimientos getPaqueteDeMovimiento() {
		return paqueteDeMovimiento;
	}
/**
 * settea paquete de movimiento
 * @param paqueteDeMovimiento
 */
	public void setPaqueteDeMovimiento(final PaqueteDeMovimientos paqueteDeMovimiento) {
		this.paqueteDeMovimiento = paqueteDeMovimiento;
	}
/**
 * devuelve paquete de personajes
 * @return
 */
	public PaqueteDePersonajes getPaqueteDePersonajes() {
		return paqueteDePersonajes;
	}
/**
 * settea paquete de personajes
 * @param paqueteDePersonajes
 */
	public void setPaqueteDePersonajes(final PaqueteDePersonajes paqueteDePersonajes) {
		this.paqueteDePersonajes = paqueteDePersonajes;
	}
/**
 * devuelve idPersonaje
 * @param idPersonaje
 */
	public void setIdPersonaje(int idPersonaje) {
		this.idPersonaje = idPersonaje;
	}
/**
 * settea paquete personaje
 * @param paquetePersonaje
 */
	public void setPaquetePersonaje(final PaquetePersonaje paquetePersonaje) {
		this.paquetePersonaje = paquetePersonaje;
	}
/**
 * devuelve paquete usuario
 * @return
 */
	public PaqueteUsuario getPaqueteUsuario() {
		return paqueteUsuario;
	}
/**
 * settea Paquete usuario
 * @param paqueteUsuario
 */
	public void setPaqueteUsuario(final PaqueteUsuario paqueteUsuario) {
		this.paqueteUsuario = paqueteUsuario;
	}
	
//	public PaqueteNpc getPaqueteNPC() {
//		return paqueteNPC;
//	}
//
//	public void setPaqueteNPC(PaqueteNpc paqueteNPC) {
//		this.paqueteNPC = paqueteNPC;
//	}
}

