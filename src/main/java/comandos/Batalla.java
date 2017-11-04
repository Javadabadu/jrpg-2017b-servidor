package comandos;

import java.io.IOException;

import estados.Estado;
import mensajeria.Comando;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteDeNPC;
import servidor.EscuchaCliente;
import servidor.Servidor;
/**
 *Clase batalla
 *
 */
public class Batalla extends ComandosServer {

	@Override
	public void ejecutar() {
		// Le reenvio al id del personaje batallado que quieren pelear
		getEscuchaCliente().setPaqueteBatalla((PaqueteBatalla) getGson().fromJson(getCadenaLeida(), PaqueteBatalla.class));

		Servidor.getLog().append(getEscuchaCliente().getPaqueteBatalla().getId() + " quiere batallar con "
				+ getEscuchaCliente().getPaqueteBatalla().getIdEnemigo() + System.lineSeparator());
		try {

			// seteo estado de batalla
			Servidor.getPersonajesConectados().get(getEscuchaCliente().getPaqueteBatalla().getId())
					.setEstado(Estado.getEstadoBatalla());
	
			if (getEscuchaCliente().getPaqueteBatalla().getTipoBatalla() == PaqueteBatalla.BATALLAPERSONAJE) {
				Servidor.getPersonajesConectados().get(getEscuchaCliente().getPaqueteBatalla().getIdEnemigo())
				.setEstado(Estado.getEstadoBatalla());
			}else{
				Servidor.getPersonajesNPC().get(getEscuchaCliente().getPaqueteBatalla().getIdEnemigo())
				.setEstado(Estado.getEstadoBatalla());
			}
			getEscuchaCliente().getPaqueteBatalla().setMiTurno(true);
			getEscuchaCliente().getSalida().writeObject(getGson().toJson(getEscuchaCliente().getPaqueteBatalla()));
			
			for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
				if (conectado.getIdPersonaje() == getEscuchaCliente().getPaqueteBatalla().getIdEnemigo()) {
					int aux = getEscuchaCliente().getPaqueteBatalla().getId();
					getEscuchaCliente().getPaqueteBatalla().setId(
							getEscuchaCliente().getPaqueteBatalla().getIdEnemigo());
					getEscuchaCliente().getPaqueteBatalla().setIdEnemigo(aux);
					getEscuchaCliente().getPaqueteBatalla().setMiTurno(false);
					conectado.getSalida().writeObject(
							getGson().toJson(getEscuchaCliente().getPaqueteBatalla()));
					break;
				}
			}
		} catch (IOException e) {
			Servidor.getLog().append("Falló al intentar enviar Batalla \n");
		}

		synchronized (Servidor.getAtencionConexiones()) {
			Servidor.getAtencionConexiones().notify();
		}

	}

}
