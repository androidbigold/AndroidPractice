import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;



public class FileReciever { 
	private static ServerSocket server;
	final static String rootPath = "./ReceiveFile/";
	final static String key = "1234567812345678";
 
	public static void main(String[] args) {
		// 服务器端用于监听Socket的线程
		Thread listener = new Thread(new Runnable() {
			@Override
			public void run() {
				int port = 10081;
				try {
					server = new ServerSocket(port);
				} catch (Exception e) {
					System.out.println(e.toString());
				}

				if (server != null) {
					System.out.println("port: " + port + "\n");
					while (true) {
						String result = new FileReciever().ReceiveFile();
						System.out.println(result);
					}
				}
			}
		});
		listener.start();
	}
 
	// 文件接收方法
	public String ReceiveFile() {

		try {
			// 接收文件名
			Socket name = server.accept();
			InputStream nameStream = name.getInputStream();
			InputStreamReader streamReader = new InputStreamReader(nameStream);
			BufferedReader br = new BufferedReader(streamReader);
			String fileName = br.readLine();
			br.close();
			streamReader.close();
			nameStream.close();
			name.close();
 
			// 接收文件数据
			Socket data = server.accept();
			InputStream dataStream = data.getInputStream();

			String[] fileNameList = fileName.split("/");
			String fName = fileNameList[fileNameList.length - 1];
			String path = fileName.substring(0, fileName.indexOf(fName));

			File dir = new File(rootPath + path); // 创建文件的存储路径
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String savePath = rootPath + fileName + ".encrypt"; // 定义完整的存储路径
			FileOutputStream file = new FileOutputStream(savePath, false);
			byte[] buffer = new byte[1024];
			int size = -1;
			while ((size = dataStream.read(buffer)) != -1) {
				file.write(buffer, 0, size);
			}
			file.close();
			dataStream.close();
			data.close();

			if(!Crypto.decrypt(savePath, rootPath + fileName, key))
				return fileName + " 解密失败";
			File temp = new File(savePath);
			temp.delete();

			return fileName + " 接收完成";
		} catch (Exception e) {
			return "接收错误:\n" + e.getMessage();
		}
	}
}
