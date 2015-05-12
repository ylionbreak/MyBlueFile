package science.hzl.mybluefile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

	BluetoothManager bluetoothManager=new BluetoothManager();
	BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
	TextView progress;
	EditText beConnectedText;
	Handler myHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progress =(TextView)findViewById(R.id.progress);
		Button beConnected = (Button)findViewById(R.id.be_connect_button);
		Button connect = (Button)findViewById(R.id.connect);
		Button send = (Button)findViewById(R.id.send);
		beConnectedText = (EditText)findViewById(R.id.connect_text);

		connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//BluetoothDevice device = bluetoothAdapter.getRemoteDevice(beConnectedText.getText().toString());
				//BluetoothDevice device = bluetoothAdapter.getRemoteDevice("22:22:CC:06:08:C9");
				BluetoothDevice device = bluetoothAdapter.getRemoteDevice("18:DC:56:D3:26:D1");
				bluetoothManager.connectDevice(device);

			}
		});
		beConnected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sharedPreferences = getSharedPreferences("3", ActionBarActivity.MODE_PRIVATE);
				bluetoothManager.startServerSocket(bluetoothAdapter, sharedPreferences);
			}
		});
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				myHandler = new Handler() {
					public void handleMessage(Message msg) {

						progress.setText(String.valueOf(msg.what)+"%");
						super.handleMessage(msg);

					}
				};

				Thread acceptTread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							bluetoothManager.SendBlueToothFile(0,myHandler);
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				});
				acceptTread.start();
			}
		});


	}

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		try {
//			bluetoothManager.getTransferSocket().close();
//		}catch (IOException e){
//			e.printStackTrace();
//		}
//
//	}
}




//Toast toastTell;
//			toastTell=Toast.makeText(App.getContext(), "success", Toast.LENGTH_SHORT);
//toastTell.setGravity(Gravity.TOP, 0, 600);
//		toastTell.show();