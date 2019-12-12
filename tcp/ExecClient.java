package tcp;

public class ExecClient {
	public static void main(String[] args) {
		TCPClient client = new TCPClient("192.168.100.29");
		client.start();
	}
}