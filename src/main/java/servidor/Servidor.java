package servidor;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import mensajeria.PaqueteMensaje;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaqueteNpc;
import mensajeria.PaquetePersonaje;
/**
 * 
 * @author Javadabadu
 *
 */
public class Servidor extends Thread {

	private static ArrayList<EscuchaCliente> clientesConectados = new ArrayList<>();
	private static Map<Integer, PaqueteMovimiento> ubicacionPersonajes = new HashMap<>();
	private static Map<Integer, PaquetePersonaje> personajesConectados = new HashMap<>();
	private static Map<Integer, PaqueteNpc> personajesNPC = new HashMap<>();

	private static Thread server;
	private static ServerSocket serverSocket;
	private static Conector conexionDB;
	private final int PUERTO = 55050;
	private static final int NUMERO25 = 25;

	private final static int ANCHO = 700;
	private final static int ALTO = 640;
	private final static int ALTO_LOG = 520;
	private final static int ANCHO_LOG = ANCHO - NUMERO25;
	private static JTextArea log;
	private static AtencionConexiones atencionConexiones;
	private static AtencionMovimientos atencionMovimientos;
	private static final int NUMERO10 = 10;
	private static final int NUMERO13 = 13;
	private static final int NUMERO16 = 16;
	private static final int NUMERO30 = 30;
	private static final int NUMERO40 = 40;
	private static final int NUMERO70 = 70;
	private static final int NUMERO100 = 100;
	private static final int NUMERO200 = 200;
	private static final int NUMERO220 = 220;
	private static final int NUMERO360 = 360;
/**
 * Metodo main! Habia main?
 * @param args
 */
	public static void main(String[] args) {
		cargarInterfaz();
	}
/**
 * Metodo cargarInterfaz
 */
	private static void cargarInterfaz() {
		JFrame ventana = new JFrame("Servidor WOME");
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setSize(ANCHO, ALTO);
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.setLayout(null);
		ventana.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/servidor/server.png"));
		JLabel titulo = new JLabel("Log del servidor...");
		titulo.setFont(new Font("Courier New", Font.BOLD, NUMERO16));
		titulo.setBounds(NUMERO10, 0, NUMERO200, NUMERO30);
		ventana.add(titulo);

		log = new JTextArea();
		log.setEditable(false);
		log.setFont(new Font("Times New Roman", Font.PLAIN, NUMERO13));
		JScrollPane scroll = new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(NUMERO10, NUMERO40, ANCHO_LOG, ALTO_LOG);
		ventana.add(scroll);

		final JButton botonIniciar = new JButton();
		final JButton botonDetener = new JButton();
		botonIniciar.setText("Iniciar");
		botonIniciar.setBounds(NUMERO220, ALTO - NUMERO70, NUMERO100, NUMERO30);
		botonIniciar.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				server = new Thread(new Servidor());
				server.start();
				botonIniciar.setEnabled(false);
				botonDetener.setEnabled(true);
			}
		});

		ventana.add(botonIniciar);

		botonDetener.setText("Detener");
		botonDetener.setBounds(NUMERO360, ALTO - NUMERO70, NUMERO100, NUMERO30);
		botonDetener.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					server.stop();
					atencionConexiones.stop();
					atencionMovimientos.stop();
					for (EscuchaCliente cliente : clientesConectados) {
						cliente.getSalida().close();
						cliente.getEntrada().close();
						cliente.getSocket().close();
					}
					serverSocket.close();
					log.append("El servidor se ha detenido." + System.lineSeparator());
				} catch (IOException e1) {
					log.append("Fallo al intentar detener el servidor." + System.lineSeparator());
				}
				if (conexionDB != null){
					conexionDB.close();
				}
				botonDetener.setEnabled(false);
				botonIniciar.setEnabled(true);
			}
		});
		botonDetener.setEnabled(false);
		ventana.add(botonDetener);

		ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		ventana.addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent evt) {
				if (serverSocket != null) {
					try {
						server.stop();
						atencionConexiones.stop();
						atencionMovimientos.stop();
						for (EscuchaCliente cliente : clientesConectados) {
							cliente.getSalida().close();
							cliente.getEntrada().close();
							cliente.getSocket().close();
						}
						serverSocket.close();
						
						
						log.append("El servidor se ha detenido." + System.lineSeparator());
					} catch (IOException e) {
						log.append("Fallo al intentar detener el servidor."
										+ System.lineSeparator());
						System.exit(1);
					}
				}
				if (conexionDB != null){
					conexionDB.close();
				}
				System.exit(0);
			}
		});

		ventana.setVisible(true);
	}
/**
 * metodo run
 */
	public void run() {
		try {
			
			conexionDB = new Conector();
			conexionDB.connect();
			log.append("Iniciando el servidor..." + System.lineSeparator());
			serverSocket = new ServerSocket(PUERTO);
			log.append("Servidor esperando conexiones..." + System.lineSeparator());
			String ipRemota;
			atencionConexiones = new AtencionConexiones();
			atencionMovimientos = new AtencionMovimientos();
			atencionConexiones.start();
			atencionMovimientos.start();
			
			AtencionNPC.cargarNPC();

			while (true) {
				Socket cliente = serverSocket.accept();
				ipRemota = cliente.getInetAddress().getHostAddress();
				log.append(ipRemota + " se ha conectado" + System.lineSeparator());

				ObjectOutputStream salida = new ObjectOutputStream(cliente.getOutputStream());
				ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());

				EscuchaCliente atencion = new EscuchaCliente(ipRemota, cliente, entrada, salida);
				atencion.start();
				clientesConectados.add(atencion);
			}
		} catch (Exception e) {
			log.append("Fallo la conexión." + System.lineSeparator());
		}
	}
/**
 * mensaje a usuario
 */
	public static boolean mensajeAUsuario(final PaqueteMensaje pqm) {
		boolean result = false;
		boolean noEncontro = true;
		Iterator<Integer> it = personajesConectados.keySet().iterator();
		
		while(it.hasNext() && noEncontro){
		    if (personajesConectados.get(it.next()).getNombre().equals(pqm.getUserReceptor())){
		      result = true;
		      noEncontro = false;
		    }
		}
		// Si existe inicio sesion
		if (result)
			Servidor.log.append(pqm.getUserEmisor() + " envió mensaje a "
							+ pqm.getUserReceptor() + System.lineSeparator());
		 else 
			// Si no existe informo y devuelvo false
			Servidor.log.append("El mensaje para " + pqm.getUserReceptor()
							+ " no se envió, ya que se encuentra desconectado." + System.lineSeparator());
			
		return result;
	}
/**
 * mensajes a all
 * @param contador
 * @return
 */
	public static boolean mensajeAAll(final int contador) {
		boolean result = true;
		if (personajesConectados.size() != contador + 1) {
			result = false;
			Servidor.log.append("Uno o más de todos los usuarios se ha desconectado,"
						+ " se ha mandado el mensaje a los demas." + System.lineSeparator());
		}else {
			
			Servidor.log.append("Se ha enviado un mensaje a todos los usuarios"
			+ System.lineSeparator());
				
		}
		return result;
		}
/**
 * devuelve clientes conectados
 * @return
 */
	public static ArrayList<EscuchaCliente> getClientesConectados() {
		return clientesConectados;
	}
/**
 * devuelve map de ubicacion de personajes
 * @return
 */
	public static Map<Integer, PaqueteMovimiento> getUbicacionPersonajes() {
		return ubicacionPersonajes;
	}
/**
 * devuelve map de personajes conectados
 * @return
 */
	public static Map<Integer, PaquetePersonaje> getPersonajesConectados() {
		return personajesConectados;
	}
/**
 * devuelve conector
 * @return
 */
	public static Conector getConector() {
		return conexionDB;
	}
/**
 * devuelve log
 * @return
 */
	public static JTextArea getLog() {
		return log;
	}
/**
 * settea log
 * @param log
 */
	public static void setLog(JTextArea log) {
		Servidor.log = log;
	}
/**
 * devuelve atencion conexiones
 * @return
 */
	public static AtencionConexiones getAtencionConexiones() {
		return atencionConexiones;
	}
/**
 * settea atencion conexiones
 * @param atencionConexiones
 */
	public static void setAtencionConexiones(AtencionConexiones atencionConexiones) {
		Servidor.atencionConexiones = atencionConexiones;
	}
/**
 * devuelve atencion movimientos
 * @return
 */
	public static AtencionMovimientos getAtencionMovimientos() {
		return atencionMovimientos;
	}
/**
 * settea atencion movimientos
 * @param atencionMovimientos
 */
	public static void setAtencionMovimientos(AtencionMovimientos atencionMovimientos) {
		Servidor.atencionMovimientos = atencionMovimientos;
	}
public static Map<Integer, PaqueteNpc> getPersonajesNPC() {
	return personajesNPC;
}
public static void setPersonajesNPC(Map<Integer, PaqueteNpc> personajesNPC) {
	Servidor.personajesNPC = personajesNPC;
}
}