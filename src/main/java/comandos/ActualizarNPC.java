package comandos;

import java.io.IOException;

import estados.Estado;
import mensajeria.Comando;
import mensajeria.PaqueteDeNPC;
import mensajeria.PaqueteNpc;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ActualizarNPC extends ComandosServer {

	@Override
	public void ejecutar() {
		escuchaCliente.setPaqueteNPC((PaqueteNpc)getGson().fromJson(getCadenaLeida(), PaqueteNpc.class));
		
		//Actualizo la salud del NPC
		if (Servidor.getPersonajesNPC().get( escuchaCliente.getPaqueteNPC().getId()) != null) {
			int salud = escuchaCliente.getPaqueteNPC().getNpc().getSalud();		
			Servidor.getPersonajesNPC().get( escuchaCliente.getPaqueteNPC().getId()).getNpc().setSalud(salud);
		}		
				
		//Envio de NPC al cliente
		for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if (conectado.getPaquetePersonaje().getEstado() == Estado.getEstadoJuego()) {
				try {
					PaqueteDeNPC paqueteNPC = (PaqueteDeNPC) new PaqueteDeNPC(Servidor.getPersonajesNPC()).clone();
					paqueteNPC.setComando(Comando.ACTUALIZARNPC);
					synchronized (conectado) {
						conectado.getSalida().writeObject(getGson().toJson(paqueteNPC));
					}

				} catch (IOException e) {
					Servidor.log.append("Falló al intentar enviar ataque a:" + conectado.getPaquetePersonaje().getId() + "\n");
				}
			}
		}

	}

}