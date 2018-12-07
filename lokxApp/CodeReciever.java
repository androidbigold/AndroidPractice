import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;


public class CodeReciever { 
	private static ServerSocket server;
	final static int port = 10082;

	public static void main(String[] args) {
		// 服务器端用于监听Socket的线程
		Thread listener = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					server = new ServerSocket(port);
				} catch (Exception e) {
					System.out.println(e.toString());
				}

				if (server != null) {
					System.out.println("port: " + port + "\n");
					while (true) {
						String result = new CodeReciever().ReceiveCode();
						System.out.println(result);
						try{
							File ipFile = new File("./ip.txt");
							if(!ipFile.exists())
								ipFile.createNewFile();
							BufferedWriter bw = new BufferedWriter(new FileWriter(ipFile));
							bw.write(result);
							bw.flush();
							bw.close();
						}
						catch (IOException e){
							System.out.println(e.getMessage());
						}
					}
				}
			}
		});
		listener.start();
	}
 
	public String ReceiveCode() {
		try {
			Socket code_socket = server.accept();
			InputStream codeStream = code_socket.getInputStream();
			InputStreamReader streamReader = new InputStreamReader(codeStream);
			BufferedReader br = new BufferedReader(streamReader);
			String code = br.readLine();
			br.close();
			streamReader.close();
			codeStream.close();
			code_socket.close();

			return code;
		} catch (Exception e) {
			return "接收错误:\n" + e.getMessage();
		}
	}
}
