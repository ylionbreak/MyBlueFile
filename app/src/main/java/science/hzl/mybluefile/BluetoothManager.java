package science.hzl.mybluefile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by YLion on 2015/5/6.
 */
public class BluetoothManager {
	BluetoothSocket transferSocket;
	StringBuilder incoming=new StringBuilder();
	Boolean listening=false;
	String opp;

	BluetoothManager(){

	}

	void connectDevice(BluetoothDevice device){
		try{
			UUID uuid =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			BluetoothSocket bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
			bluetoothSocket.connect();

			transferSocket=bluetoothSocket;

			Log.e("x", String.valueOf(bluetoothSocket.isConnected()));
		}catch (IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public UUID startServerSocket  (BluetoothAdapter bluetoothAdapter){
		UUID uuid =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		String name = "bluetoothServer";

		try{
			final BluetoothServerSocket btserver =
					bluetoothAdapter.listenUsingRfcommWithServiceRecord(name,uuid);

			Thread acceptTread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BluetoothSocket serverSocket =btserver.accept();
						//start to listen
						transferSocket = serverSocket;

						listenForMessages(serverSocket,incoming);
						//getBlueToothFile(serverSocket);
					}catch (IOException e){
						e.printStackTrace();
					}
				}
			});
			acceptTread.start();
		}catch (IOException e){
			e.printStackTrace();
		}
		return uuid;
	}

	void getBlueToothFile(BluetoothSocket socket){
		try {
			InputStream inputStream = socket.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/12345.jpg");
			FileOutputStream fos = new FileOutputStream(file);
//			byte[] buffer = new byte[1024];
//			int len=0;
//			//如果len不等于-1，说明没有读到文件末尾，循环将输入流中的内容写入输出流
//			while((len=inputStream.read(buffer))!=-1){
//				//将buffer中的内容写入输出流
//				fos.write(buffer, 0, len);
//			}


			byte[] buffer = new byte[1024];
			inputStream.read(buffer);
			fos.write(buffer);

			//flush把缓冲区中的数据强行输出
			fos.flush();
			//关闭流
			fos.close();
			inputStream.close();
			Log.e("x","sendOut");
		}catch (IOException e){
			e.printStackTrace();
		}

	}

	void SendBlueToothFile(BluetoothSocket socket){
		OutputStream outputStream;
		try {
			//创建输出流
			outputStream = socket.getOutputStream();
			//获取文件
			File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/12345.jpg");


			InputStream filesIn = new FileInputStream(f);
			//定义一个buffer
			byte[] buffer = new byte[(int)f.length()];
			//读取数据长度
//			int len=0;
//			//如果len不等于-1，说明没有读到文件末尾，循环将输入流中的内容写入输出流
//			while((len=filesIn.read(buffer))!=-1){
			filesIn.read(buffer);
//				filesIn.read(buffer,0,len);
			//将buffer中的内容写入输出流
			outputStream.write(buffer);
//			}

			//flush把缓冲区中的数据强行输出
//			outputStream.flush();
			//关闭流
			outputStream.close();
			filesIn.close();

		}catch (IOException e){
			e.printStackTrace();
		}
	}

	private void sendMessage(BluetoothSocket socket,String message){
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();

			byte[] bytes = (message+" ").getBytes();
			bytes[bytes.length-1]=0;

			outputStream.write(bytes);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	private void listenForMessages(BluetoothSocket socket,StringBuilder incoming){
		listening = true;

		int bufferSize =1024;
		byte[] buffer = new byte[bufferSize];
		try {
			InputStream inputStream = socket.getInputStream();
			int bytesRead = -1;

			while (listening){
				bytesRead = inputStream.read(buffer);

				String result = "";
				if(bytesRead != -1){

					while(bytesRead == bufferSize && buffer[bufferSize-1] != 0){
						result = result + new String(buffer,0,bytesRead);
						bytesRead = inputStream.read(buffer);
					}
					result = result + new String(buffer,0,bytesRead);

					incoming.append(result);
				}
				opp=result;
				Log.e("x","xxxx");

			}
			//socket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}


}
