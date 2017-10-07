package comandos;

import java.io.IOException;

import estados.Estado;
import mensajeria.Comando;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteDeNPC;
import mensajeria.PaqueteFinalizarBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class FinalizarBatalla extends ComandosServer {

	@Override
	public void ejecutar() {
		
		PaqueteFinalizarBatalla paqueteFinalizarBatalla = (PaqueteFinalizarBatalla) gson.fromJson(cadenaLeida, PaqueteFinalizarBatalla.class);
		escuchaCliente.setPaqueteFinalizarBatalla(paqueteFinalizarBatalla);
		
		if (paqueteFinalizarBatalla.getTipoBatalla() == PaqueteBatalla.BATALLAPERSONAJE) {
			Servidor.getConector().actualizarInventario(paqueteFinalizarBatalla.getGanadorBatalla());
			Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteFinalizarBatalla().getIdEnemigo())
					.setEstado(Estado.estadoJuego);
		} else {
			// Gana el NPC: sigue en vivo (vuelve a estadoJuego)
			if (paqueteFinalizarBatalla.getGanadorBatalla() == paqueteFinalizarBatalla.getIdEnemigo())
				Servidor.getPersonajesNPC().get(paqueteFinalizarBatalla.getIdEnemigo()).setEstado(Estado.estadoJuego);
			else // Gana el Personaje: se borra el NPC
				Servidor.getPersonajesNPC().remove(paqueteFinalizarBatalla.getIdEnemigo());
		}
		Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteFinalizarBatalla().getId()).setEstado(Estado.estadoJuego);
		
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if (paqueteFinalizarBatalla.getTipoBatalla() == PaqueteBatalla.BATALLAPERSONAJE) {
				if (conectado.getIdPersonaje() == escuchaCliente.getPaqueteFinalizarBatalla().getIdEnemigo()) {
					try {
						conectado.getSalida().writeObject(gson.toJson(escuchaCliente.getPaqueteFinalizarBatalla()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Servidor.log.append("Falló al intentar enviar finalizarBatalla a:" + conectado.getPaquetePersonaje().getId() + "\n");
					}
				}
			} else {
				PaqueteDeNPC paqueteNPC = (PaqueteDeNPC) new PaqueteDeNPC(Servidor.getPersonajesNPC()).clone();
				paqueteNPC.setComando(Comando.ACTUALIZARNPC);
				try {
					conectado.getSalida().writeObject(gson.toJson(paqueteNPC));
				} catch (IOException e) {
					// TODO Auto-generated catch block

					Servidor.log.append("Falló al intentar enviar NPC actualizados." + conectado.getPaquetePersonaje().getId() + "\n");
				}
			}
		}
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}

	}

}
