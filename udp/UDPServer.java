package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer extends Thread {
	private final int port = 4003;
	private DatagramSocket serverSocket;

	public UDPServer() {
		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("Servidor UDP iniciado.");
		while (true) {
			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];

			// RECEBER PACOTE
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			try {
				serverSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}

			byte[] converter = new byte[receivePacket.getLength()];
			System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), converter, 0,
					receivePacket.getLength());
			String data = new String(converter);
			String[] arrayData = data.split(";");
			if(arrayData.length != 2)
				continue;
			String ACK = arrayData[0];
			String msg = arrayData[1];
			System.out.println("Mensagem recebida: " + msg);

			// ENVIAR PACOTE DE CONFIRMAÇÃO
			sendData = ACK.getBytes();
			InetAddress ipOrigem = receivePacket.getAddress();
			int port = receivePacket.getPort();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipOrigem, port);
			System.out.println("Confirmacao enviada: " + ACK);

			try {
				serverSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
}
