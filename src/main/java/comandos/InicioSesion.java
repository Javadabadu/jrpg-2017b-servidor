package comandos;

import java.io.IOException;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteDeNPC;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;

public class InicioSesion extends ComandosServer {

	@Override
	public void ejecutar() {
		Paquete paqueteSv = new Paquete(null, 0);
		paqueteSv.setComando(Comando.INICIOSESION);
		
		// Recibo el paquete usuario
		escuchaCliente.setPaqueteUsuario((PaqueteUsuario) (gson.fromJson(cadenaLeida, PaqueteUsuario.class)));
		
		// Si se puede loguear el usuario le envio un mensaje de exito y el paquete personaje con los datos
		try {
			if (Servidor.getConector().loguearUsuario(escuchaCliente.getPaqueteUsuario())) {

				PaquetePersonaje paquetePersonaje = new PaquetePersonaje();
				paquetePersonaje = Servidor.getConector().getPersonaje(escuchaCliente.getPaqueteUsuario());
				paquetePersonaje.setComando(Comando.INICIOSESION);
				paquetePersonaje.setMensaje(Paquete.msjExito);
				escuchaCliente.setIdPersonaje(paquetePersonaje.getId());

				escuchaCliente.getSalida().writeObject(gson.toJson(paquetePersonaje));
				
				//Manejo de NPC
				PaqueteDeNPC paqueteNPC = (PaqueteDeNPC) new PaqueteDeNPC(Servidor.getPersonajesNPC()).clone();
				paqueteNPC.setComando(Comando.ACTUALIZARNPC);				
				escuchaCliente.getSalida().writeObject(gson.toJson(paqueteNPC));

			} else {
				paqueteSv.setMensaje(Paquete.msjFracaso);
				escuchaCliente.getSalida().writeObject(gson.toJson(paqueteSv));
			}
		} catch (IOException e) {
			Servidor.log.append("Falló al intentar iniciar sesión \n");
		}

	}

}
