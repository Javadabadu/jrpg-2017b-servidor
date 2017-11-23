package comandos;

import mensajeria.PaqueteMovimiento;
import servidor.Servidor;
/**
 * 
 * @author Javadabadu
 *
 */
public class Movimiento extends ComandosServer {

	@Override
	public void ejecutar() {
		getEscuchaCliente().setPaqueteMovimiento((PaqueteMovimiento)
				(getGson().fromJson((String) getCadenaLeida(), PaqueteMovimiento.class)));
		Servidor.getUbicacionPersonajes().get(getEscuchaCliente().getPaqueteMovimiento().getIdPersonaje())
											.setPosX(getEscuchaCliente().getPaqueteMovimiento().getPosX());
		Servidor.getUbicacionPersonajes().get(getEscuchaCliente().getPaqueteMovimiento().getIdPersonaje())
											.setPosY(getEscuchaCliente().getPaqueteMovimiento().getPosY());
		Servidor.getUbicacionPersonajes().get(getEscuchaCliente().getPaqueteMovimiento().getIdPersonaje())
											.setDireccion(getEscuchaCliente().getPaqueteMovimiento().getDireccion());
		Servidor.getUbicacionPersonajes().get(getEscuchaCliente().getPaqueteMovimiento().getIdPersonaje())
											.setFrame(getEscuchaCliente().getPaqueteMovimiento().getFrame());
		synchronized (Servidor.getAtencionMovimientos()) {
			Servidor.getAtencionMovimientos().notify();
		}
	}
}
