package comandos;

import mensajeria.Comando;
import servidor.EscuchaCliente;

public abstract class ComandosServer extends Comando {
	private EscuchaCliente escuchaCliente;
/**
 * 
 * @param escuchaCliente
 */
	public void setEscuchaCliente(final EscuchaCliente escuchaCliente) {
		this.escuchaCliente = escuchaCliente;
	}
	public EscuchaCliente getEscuchaCliente() {
		return escuchaCliente;
	}
	
}
