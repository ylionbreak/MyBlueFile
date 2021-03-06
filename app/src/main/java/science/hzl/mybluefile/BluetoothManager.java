package science.hzl.mybluefile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by YLion on 2015/5/6.
 */
public class BluetoothManager {

	BluetoothSocket transferSocket;
	Boolean listening=false;
	String returnMessage="";

	static Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what==0){
				MainActivity.progress.setText("");
			}else if(msg.what==2000000000){
				MainActivity.progress.setText("发送成功");

			}else if(msg.what==1999999999){
				MainActivity.progress.setText("连接成功");

			}else if(msg.what==1999999998){
				MainActivity.progress.setText("接受完成！");

			}else{
				MainActivity.progress.setText(String.valueOf(msg.what)+"%");
				if(msg.what==100){
					MainActivity.progress.setText("接收成功，在sd卡根目录");
				}
			}

			super.handleMessage(msg);

		}
	};
	void connectDevice(BluetoothDevice device){
		try{
			UUID uuid =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			BluetoothSocket bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
			bluetoothSocket.connect();
			transferSocket=bluetoothSocket;
			Log.e("x", String.valueOf(bluetoothSocket.isConnected()));
			if(bluetoothSocket.isConnected()){
				Message message = new Message();
				message.what=1999999999;
				handler.sendMessage(message);
			}
		}catch (IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public UUID startServerSocket  (BluetoothAdapter bluetoothAdapter, final SharedPreferences sharedPreferences){
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
						//开始监听
						transferSocket = serverSocket;
						getBlueToothFile(serverSocket, sharedPreferences);
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

	void getBlueToothFile(BluetoothSocket socket, SharedPreferences sharedPreferences){
		try {

			//获取文件名字
			String fileName;
			listenForMessages(transferSocket);
			fileName=returnMessage;
			sendMessage("ok");
			//接受文件长度
			listenForMessages(transferSocket);
			Log.e("rtime","1");
			//查看该文件被传到哪里
			String thisFilePosi =returnMessage;
			int fileLength=Integer.parseInt(thisFilePosi);
			int thisFileTimes =sharedPreferences.getInt(thisFilePosi,0);
			Log.e("rtime","2");
			//告诉发到哪里
			sendMessage(String.valueOf(thisFileTimes));
			//开始接收
			InputStream inputStream = socket.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+fileName);
			FileOutputStream fos = new FileOutputStream(file,true);
			Log.e("rtime","3");
			//创建缓冲区
			byte[] buffer =new byte[102400];
			int thisRead=0;
			Log.e("rtime","4");
			SharedPreferences.Editor editor = sharedPreferences.edit();

			//开始接受
			while(fileLength-thisRead-thisFileTimes>=0){
				buffer[thisRead%102400]=(byte)inputStream.read();
				thisRead++;
				if(thisRead-thisFileTimes==0){
					Message message = new Message();
					message.what=0;
				}
				if(thisRead%102400==0){
					fos.write(buffer);
					editor.putInt(thisFilePosi, thisFileTimes+thisRead);
					editor.apply();
					Message message = new Message();
					message.what=(thisRead+thisFileTimes)*100/(fileLength);
					handler.sendMessage(message);
					Log.e("readTime",String.valueOf(thisRead));
				}else if(fileLength-thisRead-thisFileTimes==0){
					fos.write(buffer,0,thisRead%102400);
					editor.putInt(thisFilePosi, 0);
					editor.apply();
					Message message = new Message();
					message.what=1999999998;
					handler.sendMessage(message);
				}

			}
			Log.e("rtime","5");
			//flush把缓冲区中的数据强行输出
			fos.flush();
			//关闭流
			fos.close();
			inputStream.close();

		}catch (IOException e){
			e.printStackTrace();
		}

	}

	int SendBlueToothFile(int time,String filename){
		OutputStream outputStream;
		try {
			listening=true;
			//handler

			Log.e("wtime","1");
			//创建输出流
			outputStream = transferSocket.getOutputStream();
			//获取文件
			//File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+filename);
			File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bluefile/"+filename);
			Log.e("file",Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+filename );
			//发送文件名字
			sendMessage(filename);
			listenForMessages(transferSocket);
			//发送长度
			sendMessage(String.valueOf(f.length()));
			listenForMessages(transferSocket);
			Log.e("wtime","2");
			//接收到了消息开始分开发送文件
			InputStream filesIn = new FileInputStream(f);
			byte[] buffer = new byte[(int)f.length()];
			filesIn.read(buffer);
			Log.e("wtime","3");
			Log.e("x",returnMessage);
			if(returnMessage.equalsIgnoreCase("0")) {
				Log.e("wtime","4");
				outputStream.write(buffer);
			}else{
				outputStream.write(buffer,Integer.parseInt(returnMessage),(int)f.length()-1-Integer.valueOf(returnMessage));
			}
			Message message = new Message();
			message.what=2000000000;
			handler.sendMessage(message);
			Log.e("wtime", "5");

			outputStream.flush();
			//outputStream.close();
			//filesIn.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		return time;
	}

	private void sendMessage(String message){
		OutputStream outputStream;
		try {
			outputStream = transferSocket.getOutputStream();
			byte[] bytes = (message).getBytes();
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
			int bytesRead ;

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
				listening=false;
				returnMessage=result;
			}

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
