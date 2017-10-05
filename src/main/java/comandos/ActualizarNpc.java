package comandos;
import mensajeria.PaqueteNpc;

public class ActualizarNpc extends ComandosEscucha{

	@Override
	public void ejecutar() {

		PaqueteNpc pnpc = (PaqueteNpc) gson.fromJson(cadenaLeida, PaqueteNpc.class);
		
		juego.getNpcs().put(pnpc.getId(), pnpc);
		
		 if (juego.getNpc().getId() == pnpc.getId()) {
			 juego.getEstadoJuego().actualizarNpc();
			 juego.getCliente().actualizarNpc(juego.getNpcs().get(pnpc.getId()));
		  
		 }
		
	}

	
}
