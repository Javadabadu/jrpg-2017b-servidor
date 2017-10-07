package servidor;
import java.io.IOException;

import estados.Estado;
import mensajeria.PaqueteNpc;

public class AtencionNPC {

	public static void ejecutar(){
		cargarNPC();
	}
	
	public static void cargarNPC(){
		PaqueteNpc personajeNPC = new PaqueteNpc(0, "Lucaneitor", "Lucaneitor", 2,1, 20, 70, Estado.estadoJuego,1,1);	
		Servidor.getPersonajesNPC().put(0, personajeNPC);
//		PaqueteNpc personajeNPC2 = new PaqueteNpc(1, "Leo-nidas", "Leo-nidas", 2, 1, 200, 300, Estado.estadoJuego,1,1);
//		Servidor.getPersonajesNPC().put(1, personajeNPC2);
		Servidor.log.append("Se creo el NPC. " + System.lineSeparator());
	}
}

