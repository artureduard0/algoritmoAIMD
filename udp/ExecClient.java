package udp;

public class ExecClient {
	public static void main(String args[]) {
		UDPClient clente = new UDPClient("192.168.100.29");
		clente.start();
	}
}