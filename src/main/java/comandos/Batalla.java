package comandos;

import java.io.IOException;

import estados.Estado;
import mensajeria.Comando;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteDeNPC;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class Batalla extends ComandosServer {

	@Override
	public void ejecutar() {
		// Le reenvio al id del personaje batallado que quieren pelear
		escuchaCliente.setPaqueteBatalla((PaqueteBatalla) getGson().fromJson(getCadenaLeida(), PaqueteBatalla.class));

		Servidor.log.append(escuchaCliente.getPaqueteBatalla().getId() + " quiere batallar con "
				+ escuchaCliente.getPaqueteBatalla().getIdEnemigo() + System.lineSeparator());
		try {

			// seteo estado de batalla
			Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteBatalla().getId())
					.setEstado(Estado.getEstadoBatalla());
	
			if (escuchaCliente.getPaqueteBatalla().getTipoBatalla() == PaqueteBatalla.BATALLAPERSONAJE) {
				Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteBatalla().getIdEnemigo())
				.setEstado(Estado.getEstadoBatalla());
			}else{
				Servidor.getPersonajesNPC().get(escuchaCliente.getPaqueteBatalla().getIdEnemigo())
				.setEstado(Estado.getEstadoBatalla());
			}
			escuchaCliente.getPaqueteBatalla().setMiTurno(true);
			escuchaCliente.getSalida().writeObject(getGson().toJson(escuchaCliente.getPaqueteBatalla()));
			
			for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
				if (escuchaCliente.getPaqueteBatalla().getTipoBatalla() == PaqueteBatalla.BATALLAPERSONAJE) {
					if (conectado.getIdPersonaje() == escuchaCliente.getPaqueteBatalla().getIdEnemigo()) {
						int aux = escuchaCliente.getPaqueteBatalla().getId();
						escuchaCliente.getPaqueteBatalla().setId(escuchaCliente.getPaqueteBatalla().getIdEnemigo());
						escuchaCliente.getPaqueteBatalla().setIdEnemigo(aux);
						escuchaCliente.getPaqueteBatalla().setMiTurno(false);
						conectado.getSalida().writeObject(getGson().toJson(escuchaCliente.getPaqueteBatalla()));
						break;
					}
				} else {
					PaqueteDeNPC paqueteNPC = (PaqueteDeNPC) new PaqueteDeNPC(Servidor.getPersonajesNPC()).clone();
					paqueteNPC.setComando(Comando.ACTUALIZARNPC);
					conectado.getSalida().writeObject(getGson().toJson(paqueteNPC));
				}
			}
		} catch (IOException e) {
			Servidor.log.append("Falló al intentar enviar Batalla \n");
		}

		synchronized (Servidor.atencionConexiones) {
			Servidor.atencionConexiones.notify();
		}

	}

}
