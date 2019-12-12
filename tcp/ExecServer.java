package tcp;

public class ExecServer {
	public static void main(String[] args)  {
		TCPServer server = new TCPServer();
		try {
			server.start();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}
