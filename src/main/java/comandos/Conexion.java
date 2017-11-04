package comandos;

import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import servidor.Servidor;
/**
 * 
 * Clase conexion
 *
 */
public class Conexion extends ComandosServer {

	@Override
	public void ejecutar() {
		getEscuchaCliente().setPaquetePersonaje((PaquetePersonaje) 
				(getGson().fromJson(getCadenaLeida(), PaquetePersonaje.class)).clone());
		Servidor.getPersonajesConectados().put(getEscuchaCliente().getPaquetePersonaje().getId(),
				(PaquetePersonaje) getEscuchaCliente().getPaquetePersonaje().clone());
		Servidor.getUbicacionPersonajes().put(getEscuchaCliente().getPaquetePersonaje().getId()
				,(PaqueteMovimiento) new PaqueteMovimiento(
				getEscuchaCliente().getPaquetePersonaje().getId()).clone());
		synchronized (Servidor.getAtencionConexiones()) {
			Servidor.getAtencionConexiones().notify();
		}
	}
}
