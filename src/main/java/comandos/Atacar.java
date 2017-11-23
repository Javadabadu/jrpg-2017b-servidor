package comandos;

import java.io.IOException;

import mensajeria.PaqueteAtacar;
import servidor.EscuchaCliente;
import servidor.Servidor;
/**
 * Clase Atacar
 *
 */
public class Atacar extends ComandosServer {

	@Override
	public void ejecutar() {
		getEscuchaCliente().setPaqueteAtacar((PaqueteAtacar) getGson().fromJson(getCadenaLeida(), PaqueteAtacar.class));
		for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if (conectado.getIdPersonaje() == getEscuchaCliente().getPaqueteAtacar().getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(
							getGson().toJson(getEscuchaCliente().getPaqueteAtacar()));
				} catch (IOException e) {
					Servidor.getLog().append("Falló al intentar enviar ataque a:"
							+conectado.getPaquetePersonaje().getId() + "\n");
				}
			}
		}

	}

}
