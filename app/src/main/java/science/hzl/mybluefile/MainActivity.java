package science.hzl.mybluefile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {
	BluetoothSocket bluetoothSocket;
	BluetoothManager bluetoothManager=new BluetoothManager();
	BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
	EditText beConnectedText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button beConnected = (Button)findViewById(R.id.be_connect_button);
		Button connect = (Button)findViewById(R.id.connect);
		beConnectedText = (EditText)findViewById(R.id.connect_text);

		connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//BluetoothDevice device = bluetoothAdapter.getRemoteDevice(beConnectedText.getText().toString());
				BluetoothDevice device = bluetoothAdapter.getRemoteDevice("22:22:CC:06:08:C9");
				bluetoothManager.connectDevice(device);

			}
		});
		beConnected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bluetoothManager.startServerSocket(bluetoothAdapter);
			}
		});


	}


}
