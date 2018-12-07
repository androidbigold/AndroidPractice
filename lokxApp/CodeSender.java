import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;
import java.net.Socket;
import java.util.ArrayList;


public class CodeSender {
	final static String rootPath = "./ReceiveFile/";
	final static int port = 30000;

	public static void main(String[] args) {
 			Thread sendThread = new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("send start...");
					while(true){
						Scanner in = new Scanner(System.in);
						String s = in.nextLine();

						String result = new CodeSender().SendCode(s, port);
						System.out.println(result);
					}
				}
			});
			sendThread.start();

	}

	public String SendCode(String code, int port) {
		try {
			String ipFile = rootPath + "../ip.txt";
			String ipAddress = "127.0.0.1";
			try{
				BufferedReader in = new BufferedReader(new FileReader(ipFile));
				ipAddress = in.readLine();
				System.out.println("客户端当前IP: " + ipAddress);
				in.close();
			}
			catch (IOException e){
				return e.getMessage();
			}

			Socket name = new Socket(ipAddress, port);
			OutputStream outputName = name.getOutputStream();
			OutputStreamWriter outputWriter = new OutputStreamWriter(outputName);
			BufferedWriter bwName = new BufferedWriter(outputWriter);
			bwName.write(code);
			bwName.close();
			outputWriter.close();
			outputName.close();
			name.close();
 
			return code + " 发送完成";
		} catch (Exception e) {
			return "发送错误:\n" + e.getMessage();
		}
	}

}
