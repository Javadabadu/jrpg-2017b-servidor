package comandos;

import mensajeria.PaquetePersonaje;
import servidor.Servidor;
/**
 * 
 * Clase MostrarMapas
 *
 */
public class MostrarMapas extends ComandosServer {

	@Override
	public void ejecutar() {
		getEscuchaCliente().setPaquetePersonaje((PaquetePersonaje)
				getGson().fromJson(getCadenaLeida(), PaquetePersonaje.class));
		Servidor.getLog().append(getEscuchaCliente().getSocket()
				.getInetAddress().getHostAddress()
				+" ha elegido el mapa "
				+getEscuchaCliente().getPaquetePersonaje().getMapa()
				+System.lineSeparator());	
	}

}
