package comandos;

import java.io.IOException;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;
/**
 * 
 * Clase para registrar a un usuario
 *
 */
public class Registro extends ComandosServer {

	@Override
	public void ejecutar() {
		Paquete paqueteSv = new Paquete(null, 0);
		paqueteSv.setComando(Comando.REGISTRO);
		getEscuchaCliente().setPaqueteUsuario((PaqueteUsuario)
			(getGson().fromJson(getCadenaLeida(),PaqueteUsuario.class)).clone());

		// Si el usuario se pudo registrar le envio un msj de exito
		try {
			if (Servidor.getConector().registrarUsuario(getEscuchaCliente().getPaqueteUsuario())) {
				paqueteSv.setMensaje(Paquete.getMsjExito());
				getEscuchaCliente().getSalida().writeObject(getGson().toJson(paqueteSv));

				// Si el usuario no se pudo registrar le envio un msj de fracaso
			} else {
				paqueteSv.setMensaje(Paquete.getMsjFracaso());
				getEscuchaCliente().getSalida().writeObject(getGson().toJson(paqueteSv));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Servidor.getLog().append("Falló al intentar enviar registro\n");
		}

	}

}
