package comandos;

import java.io.IOException;

import estados.Estado;
import mensajeria.Comando;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteDeNPC;
import mensajeria.PaqueteFinalizarBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;
/**
 * 
 * @author Javadabadu
 *
 */
public class FinalizarBatalla extends ComandosServer {

	@Override
	public void ejecutar() {
		PaqueteFinalizarBatalla paqueteFinalizarBatalla = (PaqueteFinalizarBatalla)
									getGson().fromJson(getCadenaLeida(), PaqueteFinalizarBatalla.class);
		getEscuchaCliente().setPaqueteFinalizarBatalla(paqueteFinalizarBatalla);
		Servidor.getConector().actualizarInventario(paqueteFinalizarBatalla.getGanadorBatalla());
		Servidor.getPersonajesConectados().get(getEscuchaCliente().getPaqueteFinalizarBatalla()
												.getId()).setEstado(Estado.getEstadoJuego());
		Servidor.getPersonajesConectados().get(getEscuchaCliente().getPaqueteFinalizarBatalla()
												.getIdEnemigo()).setEstado(Estado.getEstadoJuego());
		for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if (conectado.getIdPersonaje() == getEscuchaCliente().getPaqueteFinalizarBatalla().getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(
									getGson().toJson(getEscuchaCliente().getPaqueteFinalizarBatalla()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Servidor.getLog().append("Falló al intentar enviar finalizarBatalla a:"
					+ conectado.getPaquetePersonaje().getId() + "\n");
				}
			}
		}
		synchronized (Servidor.getAtencionConexiones()) {
			Servidor.getAtencionConexiones().notify();
		}
	}
}

