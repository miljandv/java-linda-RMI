package rs.ac.bg.etf.kdp.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import rs.ac.bg.etf.kdp.CWLocalLinda;
import rs.ac.bg.etf.kdp.LocalLinda;
import rs.ac.bg.etf.kdp.RemoteCommunication;
import rs.ac.bg.etf.kdp.ToupleSpace;
import rs.ac.bg.etf.kdp.elems.Client;

@SuppressWarnings("serial")
public class ClientGUI extends JFrame {
	String ip;
	JButton jb;
	final JTextArea jta = new JTextArea(
			"C:\\Users\\milja\\OneDrive\\Desktop\\TestLinda1.jar rs.ac.bg.etf.kdp.Integral");
	final JTextArea rjta = new JTextArea();
	RemoteCommunication l;
	int port;
	Client myClient;

	public JTextArea getrjta() {
		return rjta;
	}

	public ClientGUI(RemoteCommunication linda, String ip, int myport, Client myclient) {
		super("Get");
		l = linda;
		myClient = myclient;
		this.port = myport;
		this.setTitle("Client " + ip + " " + port);
		this.setBounds(100, 100, 507, 590);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		this.getContentPane().setBackground(new Color(225, 230, 246));

		jta.setBounds(10, 15, 471, 35);
		rjta.setBounds(10, 100, 471, 435);
		this.getContentPane().add(jta);

		// jta = new JTextArea();
		jb = new JButton("Execute task");

		Border border = BorderFactory.createLineBorder(Color.black);
		jta.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder()));
		rjta.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder()));

		jb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jb.setEnabled(false);

				Thread t = new Thread() {
					@Override
					public void run() {

						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								System.out.println("Client sending a job!");
								String text = jta.getText();
								String[] textarr = text.split(" ");
								if (textarr.length < 2)
									return;
								File ft = new File(textarr[0]);
								String classname = textarr[1];
								FileInputStream fis;
								try {
									fis = new FileInputStream(ft);
									byte[] test = new byte[(int) ft.length()];
									fis.read(test, 0, (int) ft.length());
									int waiter = 0;
									/*try {
										if(l==null) {
											CWLocalLinda lcw = new CWLocalLinda(myClient.getServerIp(),
													myClient.getServerPort());
											
										}
										l.sendBinary(textarr[0], test, ip, myport, classname);
									} catch (Exception e) {
									}*/
									System.out.println("wow");
									loop:while (waiter != 3) {
										try {
											l.isup();
											System.out.println("wow");
											l.sendBinary(textarr[0], test, ip, myport, classname);
											break loop;
										} catch (Exception e) {
											waiter++;
											rjta.append("Failed to reach the server, try number "+waiter+"...\n\n");
											CWLocalLinda lcw = null;
											try {
												lcw = new CWLocalLinda(myClient.getServerIp(),
														myClient.getServerPort());
											} catch (Exception e1) {
											}
											//System.err.println("wow");
											RemoteCommunication rc = (lcw==null)?null:lcw.stub;
											myClient.setServer(rc);
											if(waiter==3) {
												rjta.append("Failed to reach the server "+myClient.getServerIp()+" "+
														myClient.getServerPort()+", try again later\n\n");
											}
										}
									}
								} catch (FileNotFoundException e) {
								//	System.out.println("wow");
								} catch (IOException e) {
									// e.printStackTrace();
								}
								jb.setEnabled(true);
							}

						});

					};
				};
				t.start();
			}
		});
		jb.setBounds(192, 59, 108, 30);
		this.setVisible(true);
		this.add(jta);
		this.add(jb);
		this.add(rjta);
	}

}