package comandos;

import java.io.IOException;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteDeNPC;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;
/**
 * 
 * Inicio de sesion
 *
 */
public class InicioSesion extends ComandosServer {

	@Override
	public void ejecutar() {
		Paquete paqueteSv = new Paquete(null, 0);
		paqueteSv.setComando(Comando.INICIOSESION);
		// Recibo el paquete usuario
		getEscuchaCliente().setPaqueteUsuario((PaqueteUsuario) (getGson().fromJson(getCadenaLeida(), PaqueteUsuario.class)));
		// Si se puede loguear el usuario le envio un mensaje de exito y el paquete personaje con los datos
		try {
			if (Servidor.getConector().loguearUsuario(getEscuchaCliente().getPaqueteUsuario())) {

				PaquetePersonaje paquetePersonaje = new PaquetePersonaje();
				paquetePersonaje=Servidor.getConector().getPersonaje(
						getEscuchaCliente().getPaqueteUsuario());
				paquetePersonaje.setComando(Comando.INICIOSESION);
				paquetePersonaje.setMensaje(Paquete.getMsjExito());
				getEscuchaCliente().setIdPersonaje(paquetePersonaje.getId());

				getEscuchaCliente().getSalida().writeObject(getGson().toJson(paquetePersonaje));
				
				//Manejo de NPC
				PaqueteDeNPC paqueteNPC = (PaqueteDeNPC) new PaqueteDeNPC(Servidor.getPersonajesNPC()).clone();
				paqueteNPC.setComando(Comando.ACTUALIZARNPC);				
				getEscuchaCliente().getSalida().writeObject(getGson().toJson(paqueteNPC));

			} else {
				paqueteSv.setMensaje(Paquete.getMsjFracaso());
				getEscuchaCliente().getSalida().writeObject(getGson().toJson(paqueteSv));
			}
		} catch (IOException e) {
			Servidor.getLog().append("Falló al intentar iniciar sesión \n");
		}

	}

}
