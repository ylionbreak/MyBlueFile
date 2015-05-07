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
			FileOutputStream fos = new FileOutputStream(file,true);
			byte[] buffer = new byte[1024];
			int len;
			//如果len不等于-1，说明没有读到文件末尾，循环将输入流中的内容写入输出流
			while((len=inputStream.read(buffer))!=-1){
				//将buffer中的内容写入输出流
				fos.write(buffer, 0, len);
			}
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

	int SendBlueToothFile(int time,Handler handler){
		OutputStream outputStream;
		try {
			Log.e("time", String.valueOf(time) );
			//创建输出流
			outputStream = transferSocket.getOutputStream();
			//获取文件
			File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/12345.jpg");
			Log.e("file",Environment.getExternalStorageDirectory().getAbsolutePath() );
			InputStream filesIn = new FileInputStream(f);
			//定义一个buffer
			byte[] buffer = new byte[1024];
			int len=1024;

			int fileLength=(int)(f.length()/1024);

			Message message = new Message();


			if(time==0){

				//如果len不等于-1，说明没有读到文件末尾，循环将输入流中的内容写入输出流
				do{
					Log.e("write" , "write" );
					len=filesIn.read(buffer,0,len);

					outputStream.write(buffer);
					time++;

					message.what = (time*100/fileLength);
					handler.sendMessage(message);
				}while(len!=-1);

			}else{

				for(int i=1;i<=time;i++){
					filesIn.read(buffer,0,1024);
				}
				do{
					len=filesIn.read(buffer,0,len);

					outputStream.write(buffer);
					time++;

					message.what = (time/fileLength)*100;
					handler.sendMessage(message);
				}while(len!=-1);


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

	public BluetoothSocket getTransferSocket() {
		return transferSocket;
	}
}
