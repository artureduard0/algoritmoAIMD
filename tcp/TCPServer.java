package tcp;

import java.io.*;
import java.net.*;

public class TCPServer {
	private ServerSocket serverSocket;
	
	public TCPServer() {
		try {
			serverSocket = new ServerSocket(5050);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() throws IOException {
		System.out.println("Servidor TCP iniciado. Aguardando conexao.");
		Socket socketConexao = serverSocket.accept();
		System.out.println("Conectado!");
		while(true) {
			ObjectInputStream receber = new ObjectInputStream(socketConexao.getInputStream());
			ObjectOutputStream enviar = new ObjectOutputStream(socketConexao.getOutputStream());
			String data = null;
			try {
				data = (String) receber.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(data == null)
				continue;
			String[] arrayData = data.split(";");
			String ACK = arrayData[0];
			String msg = arrayData[1];
			System.out.println("Mensagem recebida: " + msg);
			enviar.flush();
			enviar.writeObject(ACK);
			System.out.println("Confirmacao enviada: " + ACK);
		}
	}
}
