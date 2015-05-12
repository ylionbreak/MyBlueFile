package science.hzl.mybluefile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

	BluetoothManager bluetoothManager=new BluetoothManager();
	BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
	static TextView progress;
	EditText beConnectedText;
	EditText fileText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progress =(TextView)findViewById(R.id.progress);
		Button connect = (Button)findViewById(R.id.connect);
		Button send = (Button)findViewById(R.id.send);
		beConnectedText = (EditText)findViewById(R.id.connect_text);
		fileText = (EditText)findViewById(R.id.fileName);
		SharedPreferences sharedPreferences = getSharedPreferences("file", ActionBarActivity.MODE_PRIVATE);
		bluetoothManager.startServerSocket(bluetoothAdapter, sharedPreferences);

		connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BluetoothDevice device = bluetoothAdapter.getRemoteDevice(beConnectedText.getText().toString());
				//BluetoothDevice device = bluetoothAdapter.getRemoteDevice("22:22:CC:06:08:C9");
				//BluetoothDevice device = bluetoothAdapter.getRemoteDevice("10:FA:CE:84:4A:B7");
				//BluetoothDevice device = bluetoothAdapter.getRemoteDevice("18:DC:56:D3:26:D1");
				bluetoothManager.connectDevice(device);

			}
		});
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Thread acceptTread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							bluetoothManager.SendBlueToothFile(0,fileText.getText().toString());
							//bluetoothManager.SendBlueToothFile(0,"123.jpg");
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				});
				acceptTread.start();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			bluetoothManager.getTransferSocket().close();
		}catch (IOException e){
			e.printStackTrace();
		}

	}

}




