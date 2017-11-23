package comandos;

import java.io.IOException;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;
/**
 * 
 * @author Javadabadu
 *
 */
public class ActualizarPersonajeLvl extends ComandosServer {

	@Override
	public void ejecutar() {
		getEscuchaCliente().setPaquetePersonaje((PaquetePersonaje) 
				getGson().fromJson(getCadenaLeida(), PaquetePersonaje.class));
		Servidor.getConector().actualizarPersonajeSubioNivel(getEscuchaCliente().getPaquetePersonaje());
		Servidor.getPersonajesConectados().remove(getEscuchaCliente().getPaquetePersonaje().getId());
		Servidor.getPersonajesConectados().put(getEscuchaCliente().getPaquetePersonaje().getId()
				, getEscuchaCliente().getPaquetePersonaje());
		getEscuchaCliente().getPaquetePersonaje().ponerBonus();
		for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
			try {
				conectado.getSalida().writeObject(getGson().toJson(getEscuchaCliente().getPaquetePersonaje()));
			} catch (IOException e) {
				Servidor.getLog().append("Falló al intentar enviar paquetePersonaje a:"
						+ conectado.getPaquetePersonaje().getId() + "\n");
			}
		}
	}
}
