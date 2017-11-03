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
		escuchaCliente.setPaquetePersonaje((PaquetePersonaje)
				gson.fromJson(cadenaLeida, PaquetePersonaje.class));
		Servidor.log.append(escuchaCliente.getSocket()
				.getInetAddress().getHostAddress()
				+" ha elegido el mapa "
				+escuchaCliente.getPaquetePersonaje().getMapa()
				+System.lineSeparator());	
	}

}
