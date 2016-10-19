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
			textSetTem.setText("设定温度\n" + arg1);
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
			textSetHum.setText("设定湿度\n" + arg1);
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
			textSetIrr.setText("设定光照\n" + arg1);
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
			textSetSmo.setText("烟雾浓度\n" + arg1);
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
			// 如果已经配对，直接返回
			if (state) {
				Toast.makeText(getApplicationContext(), "设备已经连接了",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// 蓝牙的初始化
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (adapter != null) {
				textChat.setText(textChat.getText() + "设备拥有蓝牙\n");
				if (!adapter.isEnabled()) {
					textChat.setText(textChat.getText() + "设备蓝牙未启动\n");
					Intent intent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivity(intent);
					textChat.setText(textChat.getText() + "设备蓝牙已由程序开启\n");
				} else {
					textChat.setText(textChat.getText() + "设备蓝牙已启动\n");
				}
				// 遍历配对的蓝牙设备
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
						textChat.setText(textChat.getText() + "发现设备"
								+ device.getName() + "\n");
						if (device.getName().equals(deviceName)) {
							try {
								bts = device
										.createRfcommSocketToServiceRecord(UUID
												.fromString(uuid));
								bts.connect();
								textChat.setText(textChat.getText()
										+ "和单片机连接成功\n");
								state = true;
								textDevice.setText(deviceName);
								textDevice.setTextColor(0xff1fded9);

								// 处理接收事务的handler
								handler = new Handler() {
									public void handleMessage(Message m) {
										if (m.what == 1) {
											textChat.setText(textChat.getText()
													+ "她说：" + mess + "\n");
										char[] chars = mess.toCharArray();
										if(chars.length>=14){
											if(chars[0]=='#'&&chars[13]=='!'){
												realTem=(chars[1]-'0')*100+(chars[2]-'0')*10+(chars[3]-'0');
												realHum=(chars[4]-'0')*100+(chars[5]-'0')*10+(chars[6]-'0');
												realIrr=(chars[7]-'0')*100+(chars[8]-'0')*10+(chars[9]-'0');
												realSmo=(chars[10]-'0')*100+(chars[11]-'0')*10+(chars[12]-'0');
												textRealTem.setText("实际温度\n"+realTem);
												textRealHum.setText("实际湿度\n"+realHum);
												textRealIrr.setText("实际光照\n"+realIrr);
												textRealSmo.setText("烟雾浓度\n"+realSmo);
												Calendar calendar = Calendar.getInstance();
												textRevTime.setText("最近数据接收时间\n"
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
										+ "和单片机连接失败，重启一下单片机和这个程序吧\n");
							}
							// Log.i("tag","true");
							flag = true;
							break;
						}
					}
					if (flag == false) {
						textChat.setText(textChat.getText() + "配对设备里没有"
								+ deviceName + "\n");
					}

				} else {
					textChat.setText(textChat.getText() + "没有配对设备，请去系统蓝牙自行配对\n");
				}

			} else {
				textChat.setText(textChat.getText() + "这只手机好像没有蓝牙\n");
			}
		}

	}

	private class BtnSendCmdListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (state == false) {
				Toast.makeText(getApplicationContext(), "还没有连接设备呢",
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
				textChat.setText(textChat.getText() + "我说：" + tmpSmg + "\n");
				Calendar calendar = Calendar.getInstance();
				textCmdTime.setText("最近指令发送时间\n"
						+ (calendar.get(Calendar.MONTH) + 1) + "."
						+ calendar.get(Calendar.DATE) + " "
						+ calendar.get(Calendar.HOUR) + ":"
						+ calendar.get(Calendar.MINUTE) + ":"
						+ calendar.get(Calendar.SECOND));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				textChat.setText(textChat.getText() + "额数据流获取出错了，我也不知道怎么了\n");
			}
		}

	}

	private class BtnSendMsgListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (state == false) {
				Toast.makeText(getApplicationContext(), "还没有连接设备呢",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String tmpSmg = editContent.getText().toString();
			editContent.setText("");
			try {
				dos = new DataOutputStream(bts.getOutputStream());
				dos.writeUTF(tmpSmg);
				textChat.setText(textChat.getText() + "我说：" + tmpSmg + "\n");
				Calendar calendar = Calendar.getInstance();
				textCmdTime.setText("最近指令发送时间\n"
						+ (calendar.get(Calendar.MONTH) + 1) + "."
						+ calendar.get(Calendar.DATE) + " "
						+ calendar.get(Calendar.HOUR) + ":"
						+ calendar.get(Calendar.MINUTE) + ":"
						+ calendar.get(Calendar.SECOND));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				textChat.setText(textChat.getText() + "额数据流获取出错了，我也不知道怎么了\n");
			}
		}

	}

	// 多线程提醒handler去处理事务
	// 输入流里面不要包含转义字符，比如说回车啊换行啊balabala
	// 串口发送数据必须是"#099099099099!",不已叹号结尾会让程序卡住
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
					// 读取服务器端数据
					// Log.i("tag", "before get");
					dis = new DataInputStream(bts.getInputStream());
					String tmpString = "";
					Boolean flag = false;
					// Log.i("tag", "before read");

					// String receive = dis.readUTF();

					byte tmpb[] = new byte[10];
					dis.read(tmpb);
					if (tmpb[0] != 35) {
						mess = "收到的数据有问题\n" + new String(tmpb, "GBK");
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
					//这些步骤用来除去字符串中多余字符
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
					// 吧全局变量mess赋值

					// mess=receive;
					
					mess = tmpString;

					// 通知handler去处理变化
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
