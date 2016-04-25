package network;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

/*
 问题：如何设置自己的端口号
 */

public class User {

	static JFrame frame = new JFrame();
	static JLabel label = null;
	static JPanel panelcenter = null;
	static JTextArea textshow;
	static JTextArea textinput;
	static JPanel panelbottom;
	static TextField tf1;
	static TextField tf2;
	static Button btn;
	
	public static void main(String[] args) {
		setUI();
		Send();
		Get();
	}
	
	public static void setUI(){
		//设置整个框架结构
				frame.setLayout(new BorderLayout());
				frame.setResizable(false);//设置窗体不可改变大小
				frame.setSize(700, 500);
				frame.setLocation(300,200);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);//设置窗口可见
				
				//设置顶部监听标签
				label = new JLabel("未在任何端口进行监听");
				label.setHorizontalAlignment( JLabel.RIGHT);//设置内容在标签中右对齐
				frame.add(label, BorderLayout.NORTH);
				
				//设置窗口中间的文字显示区和输入区
				panelcenter = new JPanel(new BorderLayout());
				textshow = new JTextArea();
				textinput = new JTextArea(5,20);
				panelcenter.add(new JScrollPane(textshow), BorderLayout.CENTER);
				panelcenter.add(new JScrollPane(textinput), BorderLayout.SOUTH);
				frame.add(panelcenter, BorderLayout.CENTER);
				
				//设置窗口底端
				panelbottom = new JPanel();
				panelbottom.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
				tf1 = new TextField(10);
				tf2 = new TextField(10);
				btn = new Button("send");
				panelbottom.add(tf1);
				panelbottom.add(tf2);
				panelbottom.add(btn);
				frame.add(panelbottom, BorderLayout.SOUTH);
	}
	
	public static void Send(){
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//InetAddress goalip = InetAddress.getLocalHost();//此处先设置发送到本机，可修改为tf1中内容
					String goalip = tf1.getText();
					String port = tf2.getText();
					
					byte[] buffer = new byte[2048];
					buffer = textinput.getText().getBytes();
					int len = buffer.length;
					
					DatagramPacket dp = new DatagramPacket(buffer, len, InetAddress.getByName(goalip), Integer.parseInt(port));
					DatagramSocket ds = new DatagramSocket();
					ds.send(dp);
					
					
					//更新显示区
					textshow.append("我对" + tf1.getText() + " : " + port + "说：\n" + textinput.getText() + "\n");
					//将显示区的滚动条放到最下面
					textshow.setCaretPosition(textshow.getText().length());
					
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (SocketException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	public static void Get(){

		String port = JOptionPane.showInputDialog("请输入监听端口");
		label.setText("在端口" + port + "监听");
		//开启socket监听
		DatagramSocket ds;
		try {
			ds = new DatagramSocket(Integer.parseInt(port));
			new Thread(){
				DatagramPacket dp;
				public void run(){
					
					byte[] buf = new byte[2048];
					int len = buf.length;
					dp = new DatagramPacket(buf, len);
					
					while(!ds.isClosed())
					{
						try {
							ds.receive(dp);
							
							//更新显示区
							textshow.append(dp.getAddress().getHostAddress() + " : " + dp.getPort()
									+ "对我说：\n" + new String(dp.getData(), 0, dp.getLength()) + "\n");
							//将显示区滚动条放到最下面
							textshow.setCaretPosition(textshow.getText().length());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			}.start();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		
	}
}
