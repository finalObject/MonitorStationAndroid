package com.example.start;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private String uuid = "00001101-0000-1000-8000-00805F9B34FB";
	private String deviceName = "HC-06";
	private int realTem = 0;
	private int realHum = 0;
	private int realIrr = 0;
	private int realSmo = 0;
	private Boolean state = false;
	private BluetoothSocket bts = null;

	private DataOutputStream dos = null;
	private Handler handler = null;
	private String mess = "";

	private Button btnConnect = null;
	private Button btnSendCmd = null;
	private Button btnSendMsg = null;

	private TextView textDevice = null;
	private TextView textRealTem = null;
	private TextView textRealHum = null;
	private TextView textRealIrr = null;
	private TextView textRealSmo = null;
	private TextView textCmdTime = null;
	private TextView textRevTime = null;
	private TextView textSetTem = null;
	private TextView textSetHum = null;
	private TextView textSetIrr = null;
	private TextView textSetSmo = null;
	private TextView textChat = null;

	private SeekBar seekSetTem = null;
	private SeekBar seekSetHum = null;
	private SeekBar seekSetIrr = null;
	private SeekBar seekSetSmo = null;

	private EditText editContent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		initViews();
	}

	private class SeekBarSetTemListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			textSetTem.setText("�趨�¶�\n" + arg1);
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

	}

	private class SeekBarSetHumListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			textSetHum.setText("�趨ʪ��\n" + arg1);
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

	}

	private class SeekBarSetIrrListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			textSetIrr.setText("�趨����\n" + arg1);
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

	}

	private class SeekBarSetSmoListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			textSetSmo.setText("����Ũ��\n" + arg1);
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

	}

	private class BtnConnectListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// ����Ѿ���ԣ�ֱ�ӷ���
			if (state) {
				Toast.makeText(getApplicationContext(), "�豸�Ѿ�������",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// �����ĳ�ʼ��
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (adapter != null) {
				textChat.setText(textChat.getText() + "�豸ӵ������\n");
				if (!adapter.isEnabled()) {
					textChat.setText(textChat.getText() + "�豸����δ����\n");
					Intent intent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivity(intent);
					textChat.setText(textChat.getText() + "�豸�������ɳ�����\n");
				} else {
					textChat.setText(textChat.getText() + "�豸����������\n");
				}
				// ������Ե������豸
				// Log.i("tag","222");
				Set<BluetoothDevice> devices = adapter.getBondedDevices();
				if (devices.size() > 0) {
					Boolean flag = false;
					// Log.i("tag","1");
					for (Iterator iterator = devices.iterator(); iterator
							.hasNext();) {
						// Log.i("tag","2");
						BluetoothDevice device = (BluetoothDevice) iterator
								.next();
						textChat.setText(textChat.getText() + "�����豸"
								+ device.getName() + "\n");
						if (device.getName().equals(deviceName)) {
							try {
								bts = device
										.createRfcommSocketToServiceRecord(UUID
												.fromString(uuid));
								bts.connect();
								textChat.setText(textChat.getText()
										+ "�͵�Ƭ�����ӳɹ�\n");
								state = true;
								textDevice.setText(deviceName);
								textDevice.setTextColor(0xff1fded9);

								// ������������handler
								handler = new Handler() {
									public void handleMessage(Message m) {
										if (m.what == 1) {
											textChat.setText(textChat.getText()
													+ "��˵��" + mess + "\n");
										char[] chars = mess.toCharArray();
										if(chars.length>=14){
											if(chars[0]=='#'&&chars[13]=='!'){
												realTem=(chars[1]-'0')*100+(chars[2]-'0')*10+(chars[3]-'0');
												realHum=(chars[4]-'0')*100+(chars[5]-'0')*10+(chars[6]-'0');
												realIrr=(chars[7]-'0')*100+(chars[8]-'0')*10+(chars[9]-'0');
												realSmo=(chars[10]-'0')*100+(chars[11]-'0')*10+(chars[12]-'0');
												textRealTem.setText("ʵ���¶�\n"+realTem);
												textRealHum.setText("ʵ��ʪ��\n"+realHum);
												textRealIrr.setText("ʵ�ʹ���\n"+realIrr);
												textRealSmo.setText("����Ũ��\n"+realSmo);
												Calendar calendar = Calendar.getInstance();
												textRevTime.setText("������ݽ���ʱ��\n"
														+ (calendar.get(Calendar.MONTH) + 1) + "."
														+ calendar.get(Calendar.DATE) + " "
														+ calendar.get(Calendar.HOUR) + ":"
														+ calendar.get(Calendar.MINUTE) + ":"
														+ calendar.get(Calendar.SECOND));
											}
										}
										}
										super.handleMessage(m);
									}

								};
								new Thread(new ReadHandlerClientThread())
										.start();

								// Log.i("tag", "start");
							} catch (IOException e) {
								// TODO Auto-generated catch block

								textChat.setText(textChat.getText()
										+ "�͵�Ƭ������ʧ�ܣ�����һ�µ�Ƭ������������\n");
							}
							// Log.i("tag","true");
							flag = true;
							break;
						}
					}
					if (flag == false) {
						textChat.setText(textChat.getText() + "����豸��û��"
								+ deviceName + "\n");
					}

				} else {
					textChat.setText(textChat.getText() + "û������豸����ȥϵͳ�����������\n");
				}

			} else {
				textChat.setText(textChat.getText() + "��ֻ�ֻ�����û������\n");
			}
		}

	}

	private class BtnSendCmdListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (state == false) {
				Toast.makeText(getApplicationContext(), "��û�������豸��",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String tmpSmg = "#"
					+ String.format("%03d%03d%03d%03d",
							seekSetTem.getProgress(), seekSetHum.getProgress(),
							seekSetIrr.getProgress(), seekSetSmo.getProgress())
					+ "!";
			try {
				dos = new DataOutputStream(bts.getOutputStream());
				dos.writeUTF(tmpSmg);
				textChat.setText(textChat.getText() + "��˵��" + tmpSmg + "\n");
				Calendar calendar = Calendar.getInstance();
				textCmdTime.setText("���ָ���ʱ��\n"
						+ (calendar.get(Calendar.MONTH) + 1) + "."
						+ calendar.get(Calendar.DATE) + " "
						+ calendar.get(Calendar.HOUR) + ":"
						+ calendar.get(Calendar.MINUTE) + ":"
						+ calendar.get(Calendar.SECOND));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				textChat.setText(textChat.getText() + "����������ȡ�����ˣ���Ҳ��֪����ô��\n");
			}
		}

	}

	private class BtnSendMsgListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (state == false) {
				Toast.makeText(getApplicationContext(), "��û�������豸��",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String tmpSmg = editContent.getText().toString();
			editContent.setText("");
			try {
				dos = new DataOutputStream(bts.getOutputStream());
				dos.writeUTF(tmpSmg);
				textChat.setText(textChat.getText() + "��˵��" + tmpSmg + "\n");
				Calendar calendar = Calendar.getInstance();
				textCmdTime.setText("���ָ���ʱ��\n"
						+ (calendar.get(Calendar.MONTH) + 1) + "."
						+ calendar.get(Calendar.DATE) + " "
						+ calendar.get(Calendar.HOUR) + ":"
						+ calendar.get(Calendar.MINUTE) + ":"
						+ calendar.get(Calendar.SECOND));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				textChat.setText(textChat.getText() + "����������ȡ�����ˣ���Ҳ��֪����ô��\n");
			}
		}

	}

	// ���߳�����handlerȥ��������
	// ���������治Ҫ����ת���ַ�������˵�س������а�balabala
	// ���ڷ������ݱ�����"#099099099099!",����̾�Ž�β���ó���ס
	private class ReadHandlerClientThread extends Thread {
		// private Handler handler = null;
		public ReadHandlerClientThread() {
			super();

		}

		@Override
		public void run() {
			DataInputStream dis = null;

			try {
				while (true) {
					// ��ȡ������������
					// Log.i("tag", "before get");
					dis = new DataInputStream(bts.getInputStream());
					String tmpString = "";
					Boolean flag = false;
					// Log.i("tag", "before read");

					// String receive = dis.readUTF();

					byte tmpb[] = new byte[10];
					dis.read(tmpb);
					if (tmpb[0] != 35) {
						mess = "�յ�������������\n" + new String(tmpb, "GBK");
						Message m = new Message();
						m.what = 1;
						handler.sendMessage(m);
						continue;
					}
					while (flag == false) {
						tmpString += new String(tmpb, "GBK");
						for (int j = 0; j < tmpb.length; j++) {
							if (tmpb[j] == 33) {
								flag = true;
							}
						}
						if (flag == false) {
							tmpb = new byte[10];
							dis.read(tmpb);
						}
					}
					//��Щ����������ȥ�ַ����ж����ַ�
					char[] tmpChars = tmpString.toCharArray();
					char[] tmpMess= new char[14];
					tmpMess[0]='#';
					tmpMess[13]='!';
					int index=1;
					for(int i =0;i<tmpChars.length;i++){
						if((tmpChars[i]>='0')&&(tmpChars[i]<='9')){
							if(index==13){
								break;
							}
							tmpMess[index]=tmpChars[i];
							index++;
						}
					}
					tmpString = new String(tmpMess);
					// Log.i("tag", "before print");
					// ��ȫ�ֱ���mess��ֵ

					// mess=receive;
					
					mess = tmpString;

					// ֪ͨhandlerȥ����仯
					Message m = new Message();
					m.what = 1;
					handler.sendMessage(m);
					// Log.i("tag", mess);
					// Log.i("tag", "Over");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (dis != null) {
						dis.close();
					}
					if (bts != null) {
						bts = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void initViews() {
		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(new BtnConnectListener());
		btnSendCmd = (Button) findViewById(R.id.btnSendCmd);
		btnSendCmd.setOnClickListener(new BtnSendCmdListener());
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
		btnSendMsg.setOnClickListener(new BtnSendMsgListener());

		textDevice = (TextView) findViewById(R.id.textDevice);
		textRealTem = (TextView) findViewById(R.id.textRealTem);
		textRealHum = (TextView) findViewById(R.id.textRealHum);
		textRealIrr = (TextView) findViewById(R.id.textRealIrr);
		textRealSmo = (TextView) findViewById(R.id.textRealSmo);
		textCmdTime = (TextView) findViewById(R.id.textCmdTime);
		textRevTime = (TextView) findViewById(R.id.textRevTime);
		textSetTem = (TextView) findViewById(R.id.textSetTem);
		textSetHum = (TextView) findViewById(R.id.textSetHum);
		textSetIrr = (TextView) findViewById(R.id.textSetIrr);
		textSetSmo = (TextView) findViewById(R.id.textSetSmo);
		textChat = (TextView) findViewById(R.id.textChat);

		seekSetTem = (SeekBar) findViewById(R.id.seekSetTem);
		seekSetTem.setOnSeekBarChangeListener(new SeekBarSetTemListener());
		seekSetHum = (SeekBar) findViewById(R.id.seekSetHum);
		seekSetHum.setOnSeekBarChangeListener(new SeekBarSetHumListener());
		;
		seekSetIrr = (SeekBar) findViewById(R.id.seekSetIrr);
		seekSetIrr.setOnSeekBarChangeListener(new SeekBarSetIrrListener());
		seekSetSmo = (SeekBar) findViewById(R.id.seekSetSmo);
		seekSetSmo.setOnSeekBarChangeListener(new SeekBarSetSmoListener());

		editContent = (EditText) findViewById(R.id.editContent);
	}
}
