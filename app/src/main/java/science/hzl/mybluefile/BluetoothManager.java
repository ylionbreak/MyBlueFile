package science.hzl.mybluefile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by YLion on 2015/5/6.
 */
public class BluetoothManager {
	private static final String SEND = "SEND:";
	//private static final String
	BluetoothSocket transferSocket;
	StringBuilder incoming=new StringBuilder();
	Boolean listening=false;
	String returnMessage="";
	String filename="12345.jpg";

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
						listenForMessages(transferSocket);
						//listenForMessages(serverSocket,incoming);
						getBlueToothFile(serverSocket);
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
			//接受文件长度
			listenForMessages(transferSocket);
			int file
			InputStream inputStream = socket.getInputStream();

			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/12345.jpg");
			FileOutputStream fos = new FileOutputStream(file,true);
			byte[] buffer = new byte[102400];
			int len;

			inputStream.read(buffer);
			fos.write(buffer);

			//flush把缓冲区中的数据强行输出
			fos.flush();
<<<<<<< HEAD
=======
			
			
			//关闭流
>>>>>>> origin/master
			fos.close();
			inputStream.close();

		}catch (IOException e){
			e.printStackTrace();
		}

	}

	int SendBlueToothFile(int time,Handler handler){
		OutputStream outputStream;
		try {
			listening=true;
			//handler
			Message message = new Message();
			//创建输出流
			outputStream = transferSocket.getOutputStream();
			//获取文件
			File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/12345.jpg");
			Log.e("file",Environment.getExternalStorageDirectory().getAbsolutePath() );
			//发送长度
			sendMessage("SEND"+String.valueOf(f.length()));
			listenForMessages(transferSocket);
			//等待消息
			while(returnMessage.equalsIgnoreCase(""));
			//接收到了消息开始分开发送文件
			listening=false;
			InputStream filesIn = new FileInputStream(f);
			byte[] buffer = new byte[(int)f.length()];
			filesIn.read(buffer);
			if(returnMessage.equalsIgnoreCase("FIRST")) {
				outputStream.write(buffer);
			}else{
				outputStream.write(buffer,Integer.valueOf(returnMessage),(int)f.length()-Integer.valueOf(returnMessage));
			}
			outputStream.flush();
			outputStream.close();
			filesIn.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		return time;
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

	private void sendMessage(String message){
		OutputStream outputStream;
		try {
			outputStream = transferSocket.getOutputStream();
			byte[] bytes = (message+" ").getBytes();
			bytes[bytes.length-1]=0;
			outputStream.write(bytes);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	private void listenForMessages(BluetoothSocket socket){
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
				}
				returnMessage=result;


			}
			//socket.close();
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
				returnMessage=result;


			}
			//socket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public BluetoothSocket getTransferSocket() {
		return transferSocket;
	}
}


//发送文件名与长度
//接受有无传输过与长度
//NO无 从0开始
//YES有 从接受位置开始
