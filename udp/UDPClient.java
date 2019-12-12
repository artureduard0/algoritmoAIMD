package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class UDPClient extends Thread {
	private DatagramSocket clientSocket;
	private InetAddress ipDestino;
	private final int portaDestino = 4003;
	private int n;
	private final int limiteDeN = 5;
	private final String msg = "aaaaaaaaaaaaaaaa";
	private int ACK;
	private final int timeout = 15;
	private ArrayList<Integer> acksConf;
	private int tamJanela;
	private boolean perda;

	public UDPClient(String ipDestino) {
		this.perda = false;
		this.tamJanela = 1;
		this.acksConf = new ArrayList<>();
		this.n = 0;
		this.ACK = 0;
		try {
			this.ipDestino = InetAddress.getByName(ipDestino);
			clientSocket = new DatagramSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void enviar(String msg) throws IOException {
		byte[] sendData = new byte[1024];
		sendData = msg.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipDestino, portaDestino);
		clientSocket.send(sendPacket);
		System.out.println("Mensagem enviada: " + msg);
		this.verificarConfirmacao(msg);
	}

	private void verificarConfirmacao(String msg) throws IOException {
		byte[] receiveData = new byte[1024];
		try {
			clientSocket.setSoTimeout(timeout);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			clientSocket.receive(receivePacket);
		} catch (SocketTimeoutException e2) {
			// SE ESTOURAR O TEMPO, REENVIA
			String[] a = msg.split(";");
			System.out.println(" > Reenviando pacote PERDIDO: " + a[0]);
			this.perda = true;
			this.enviar(msg);
		}

		byte[] converter = new byte[receivePacket.getLength()];
		System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), converter, 0, receivePacket.getLength());
		String data = new String(converter);

		try {
			int ACKRec = Integer.parseInt(data);
			// testa se é o pacote esperado
			if (!acksConf.contains(ACKRec) && msg.startsWith(data)) {
				System.out.println("Confirmacao recebida: " + data);
				acksConf.add(ACKRec);
				ACK++;
			} else {
				// se não for o pacote esperado, continua esperando
				this.verificarConfirmacao(msg);
			}
		} catch (NumberFormatException e1) {
			// ignora possível pacote em branco recebido
			// e1.printStackTrace();
		}
	}

	public void run() {
		for (int i = 0; i < tamJanela; i++) {
			if (n - 1 == limiteDeN)
				break;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int qtdSeg = tamJanela;
			System.out.println("-----------------------------------------------------");
			System.out.println("@ Número de segmentos enviados: " + qtdSeg + " | n: " + n);
			int cont = 0;
			while (cont < qtdSeg) {
				try {
					if (this.perda)
						cont = qtdSeg;
					this.enviar(ACK + ";" + msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
				cont++;
			}
			if (this.perda) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Tamanho da janela: Reduzindo pela metade");
				this.perda = false;
				if (n != 0)
					this.n = n - 1;
				if(qtdSeg / 2 > 0)
					this.tamJanela = qtdSeg / 2;
				this.run();
				try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException exit) {
					break;
				}
			} else {
				if (n < limiteDeN) {
					System.out.println("# Tamanho da janela: Incrementando n");
					this.tamJanela *= 2;
					n++;
				} else {
					System.out.println("# Todos os pacotes foram enviados.");
					break;
				}
			}
		}
	}
}
