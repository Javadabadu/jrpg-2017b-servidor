package comandos;

import java.io.IOException;

import mensajeria.Comando;
import mensajeria.PaqueteDeNPC;
import mensajeria.PaquetePersonaje;
import servidor.Servidor;
/**
 * 
 * Clase CrearPersonaje
 *
 */
public class CrearPersonaje extends ComandosServer {

	@Override
	public void ejecutar() {
		// Casteo el paquete personaje
		getEscuchaCliente().setPaquetePersonaje((PaquetePersonaje) 
				(getGson().fromJson(getCadenaLeida(),PaquetePersonaje.class)));
		// Guardo el personaje en ese usuario
		Servidor.getConector().registrarPersonaje(getEscuchaCliente().getPaquetePersonaje(),
				getEscuchaCliente().getPaqueteUsuario());
		try {
			PaquetePersonaje paquetePersonaje;
			paquetePersonaje = new PaquetePersonaje();
			paquetePersonaje = Servidor.getConector().getPersonaje(getEscuchaCliente().getPaqueteUsuario());
			getEscuchaCliente().setIdPersonaje(paquetePersonaje.getId());
			getEscuchaCliente().getSalida().writeObject(getGson().toJson
					(getEscuchaCliente().getPaquetePersonaje(),
							getEscuchaCliente().getPaquetePersonaje().getClass()));
			
			//Manejo de NPC
			PaqueteDeNPC paqueteNPC = (PaqueteDeNPC) new PaqueteDeNPC(Servidor.getPersonajesNPC()).clone();
			paqueteNPC.setComando(Comando.ACTUALIZARNPC);				
			getEscuchaCliente().getSalida().writeObject(getGson().toJson(paqueteNPC));

		} catch (IOException e1) {
			Servidor.getLog().append("Falló al intentar enviar personaje creado \n");
		}
	}

}
