package servidor;
import java.io.IOException;

import estados.Estado;
import mensajeria.PaqueteNpc;

public class AtencionNPC {

	public static void ejecutar(){
		cargarNPC();
	}
	
	public static void cargarNPC(){
		
//		PaqueteNpc personajeNPC = new PaqueteNpc(0, "Lucaneitor", PaqueteNpc.LUCANEITOR, 2,1, 20, 70, Estado.estadoJuego,1,1);	
//		Servidor.getPersonajesNPC().put(0, personajeNPC);
//		PaqueteNpc personajeNPC2 = new PaqueteNpc(1, "Leo-nidas", PaqueteNpc.LEONIDAS, 2, 1, 50, 100, Estado.estadoJuego,1,1);
//		Servidor.getPersonajesNPC().put(1, personajeNPC2);
		
		PaqueteNpc lucas = new PaqueteNpc(0, "Lucaneitor", PaqueteNpc.LUCANEITOR, 2,1, 100, 450, Estado.estadoJuego,1,1 );
		Servidor.getPersonajesNPC().put(0, lucas);
		PaqueteNpc lucas1 = new PaqueteNpc(1, "Lucaneitor1", PaqueteNpc.LUCANEITOR, 2,1, 80, 100, Estado.estadoJuego,1,1 );
		Servidor.getPersonajesNPC().put(1, lucas1);
		PaqueteNpc lucas2 = new PaqueteNpc(2, "Lucaneitor2", PaqueteNpc.LUCANEITOR, 2,1, 20, 70, Estado.estadoJuego,1,1 );
		Servidor.getPersonajesNPC().put(2, lucas2);
		
		PaqueteNpc leo = new PaqueteNpc(3, "Leo-nidas", PaqueteNpc.LEONIDAS, 2, 1, 100, 150, Estado.estadoJuego,1,1);
		Servidor.getPersonajesNPC().put(3, leo);
		PaqueteNpc leo1 = new PaqueteNpc(4, "Leo-nidas", PaqueteNpc.LEONIDAS, 2, 1, 150, 230, Estado.estadoJuego,1,1);
		Servidor.getPersonajesNPC().put(4, leo1);
		PaqueteNpc leo2 = new PaqueteNpc(5, "Leo-nidas", PaqueteNpc.LEONIDAS, 2, 1, 200, 200, Estado.estadoJuego,1,1);
		Servidor.getPersonajesNPC().put(5, leo2);
		Servidor.log.append("Se crearon los NPC. " + System.lineSeparator());
	}
}

