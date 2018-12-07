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
import java.util.regex.*;
import org.json.JSONObject;



public class StringReciever { 
	private static ServerSocket server;
	final static String rootPath = "./ReceiveFile/";
	final static String MessagePattern = "(.*--and--)(.*)*";
	final static int port = 10083;

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
						String result = new StringReciever().ReceiveCode();
						System.out.println(result);
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

			if(Pattern.matches(MessagePattern, code)){
				String[] messages = code.split("--and--");
				String position = messages[0];
				String[] records = messages[1].split(",");

				File positionFile = new File(rootPath + "position.json");
				if(!positionFile.exists())
					positionFile.createNewFile();

				String latitude = position.split(",")[0].split(":")[1];
				String longitude = position.split(",")[1].split(":")[1];
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("latitude", latitude);
				jsonObject.put("longitude", longitude);

				BufferedWriter pbw = new BufferedWriter(new FileWriter(positionFile));
				pbw.write(jsonObject.toString());
				pbw.flush();
				pbw.close();

				File recordsFile = new File(rootPath + "records.txt");
				if(!recordsFile.exists())
					recordsFile.createNewFile();
				BufferedWriter rbw = new BufferedWriter(new FileWriter(recordsFile));
				for(String r: records)
					rbw.write(r + "\r\n");
				rbw.flush();
				rbw.close();

				return "位置与通话记录接受完成";
			}

			return code;
		} catch (Exception e) {
			return "接收错误:\n" + e.getMessage();
		}
	}
}
