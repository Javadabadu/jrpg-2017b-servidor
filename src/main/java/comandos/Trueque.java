package comandos;

import java.io.IOException;

import mensajeria.PaqueteComerciar;
import servidor.EscuchaCliente;
import servidor.Servidor;
/**
 * 
 * @author Javadabadu
 *
 */
public class Trueque extends ComandosServer {

	@Override
	public void ejecutar() {
		PaqueteComerciar paqueteComerciar;
		paqueteComerciar = (PaqueteComerciar) getGson().fromJson(getCadenaLeida(), PaqueteComerciar.class);
		//BUSCO EN LAS ESCUCHAS AL QUE SE LO TENGO QUE MANDAR
		for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if (conectado.getPaquetePersonaje().getId() == paqueteComerciar.getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(getGson().toJson(paqueteComerciar));
				} catch (IOException e) {
					Servidor.getLog().append("Falló al intentar enviar trueque a:"
							+ conectado.getPaquetePersonaje().getId() + "\n");
				}
		}
			else if (conectado.getPaquetePersonaje().getId() == paqueteComerciar.getId()) {
				try {
					conectado.getSalida().writeObject(getGson().toJson(paqueteComerciar));
				} catch (IOException e) {
					Servidor.getLog().append("Falló al intentar enviar trueque a:"
				+ conectado.getPaquetePersonaje().getId() + "\n");
				}
			}
		}
	}
}
