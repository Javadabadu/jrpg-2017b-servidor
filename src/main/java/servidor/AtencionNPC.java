package servidor;


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
		
		PaqueteNpc lucas = new PaqueteNpc(0, "Lucaneitor", PaqueteNpc.LUCANEITOR, 2,1, -1, 1000, Estado.getEstadoJuego(),1,1 );
		Servidor.getPersonajesNPC().put(0, lucas);
		PaqueteNpc lucas1 = new PaqueteNpc(1, "Lucaneitor1", PaqueteNpc.LUCANEITOR, 2,1, 2000, 1200, Estado.getEstadoJuego(),1,1 );
		Servidor.getPersonajesNPC().put(1, lucas1);
		PaqueteNpc lucas2 = new PaqueteNpc(2, "Lucaneitor2", PaqueteNpc.LUCANEITOR, 2,1, 400, 700, Estado.getEstadoJuego(),1,1 );
		Servidor.getPersonajesNPC().put(2, lucas2);
		PaqueteNpc lucas3 = new PaqueteNpc(3, "Lucaneitor3", PaqueteNpc.LUCANEITOR, 2,1, 60, 1500, Estado.getEstadoJuego(),1,1 );
		Servidor.getPersonajesNPC().put(3, lucas3);
		PaqueteNpc lucas4 = new PaqueteNpc(4, "LucaneitorPruebas", PaqueteNpc.LUCANEITOR, 2,1, 0, 80, Estado.getEstadoJuego(),1,1 );
		Servidor.getPersonajesNPC().put(4, lucas4);
		
		
		PaqueteNpc leo = new PaqueteNpc(5, "Leo-nidas", PaqueteNpc.LEONIDAS, 2, 1, -2000, 1130, Estado.getEstadoJuego(),1,1);
		Servidor.getPersonajesNPC().put(5, leo);
		PaqueteNpc leo1 = new PaqueteNpc(6, "Leo-nidas1", PaqueteNpc.LEONIDAS, 2, 1, -1000, 800, Estado.getEstadoJuego(),1,1);
		Servidor.getPersonajesNPC().put(6, leo1);
		PaqueteNpc leo2 = new PaqueteNpc(7, "Leo-nidas2", PaqueteNpc.LEONIDAS, 2, 1, -20, 1800, Estado.getEstadoJuego(),1,1);
		Servidor.getPersonajesNPC().put(7, leo2);
		PaqueteNpc leo3 = new PaqueteNpc(8, "Leo-nidas3", PaqueteNpc.LEONIDAS, 2, 1, 1500, 630, Estado.getEstadoJuego(),1,1);
		Servidor.getPersonajesNPC().put(8, leo3);
		PaqueteNpc leo4 = new PaqueteNpc(9, "Leo-nidas4", PaqueteNpc.LEONIDAS, 2, 1, 2000, 600, Estado.getEstadoJuego(),1,1);
		Servidor.getPersonajesNPC().put(9, leo4);
		
		
		Servidor.log.append("Se crearon los NPC. " + System.lineSeparator());
		
	}
	

}

