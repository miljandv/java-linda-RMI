package rs.ac.bg.etf.kdp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import rs.ac.bg.etf.kdp.LocalLinda;
import rs.ac.bg.etf.kdp.RemoteCommunication;
import rs.ac.bg.etf.kdp.ToupleSpace;



@SuppressWarnings("serial")
public class ServerGUI extends JFrame {
	JButton jb;
	JTextArea jta;
	JTextArea jtajobs;
	JLabel jl;
	JTextField jtf;
	JLabel jworkerstcnt = new JLabel("WorkStations: 0");
	JLabel jworkercnt = new JLabel("Workers: 0");
	public ServerGUI() {
		super("Server");
		jb = new JButton("Redo job");
		jta = new JTextArea();
		jtajobs = new JTextArea();
		jl = new JLabel("Active jobs: 0");	
		jtf = new JTextField(); jtf.setColumns(4);
		jl.setFont(new Font("Serif", Font.BOLD, 19));
		jworkerstcnt.setFont(new Font("Serif", Font.BOLD, 19));
		jworkercnt.setFont(new Font("Serif", Font.BOLD, 19));
		jta.setEditable(false);
		JScrollPane sp = new JScrollPane(jta);
		JScrollPane spjobs = new JScrollPane(jtajobs);
		jtajobs.setEditable(false);
		jtajobs.setRows(20);
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
								ArrayList<String[]> x = ((LocalLinda)ToupleSpace.getLinda()).tupleSpace;
								 BufferedWriter outputWriter = null;
								  try {
									outputWriter = new BufferedWriter(new FileWriter("C:\\Users\\milja\\OneDrive\\Desktop\\test.txt"));
									  for (int i = 0; i < x.get(0).length; i++) {
										    outputWriter.write(x.get(0)[i]+"");
										    if(i+1<x.get(0).length)outputWriter.newLine();
										  }
										  outputWriter.flush();  
										  outputWriter.close();  
								  } catch (IOException e) {
									e.printStackTrace();
								}
								jb.setEnabled(true);
							}

						});

					};
				};
				t.start();
			}
		});

		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setBounds(300, 100, 1100, 600);
		Panel left= new Panel();
		left.setLayout(new BorderLayout());
		Panel p = new Panel();
		p.setLayout(new FlowLayout());
		Panel ppleft = new Panel(new GridLayout(3,1));
		ppleft.add(jl);
		ppleft.add(jworkerstcnt);
		ppleft.add(jworkercnt);
		left.add(ppleft,BorderLayout.NORTH);
		p.add(jtf);
		left.setBackground(new Color(209, 224, 239));
		p.add(jb);
		p.setBackground(new Color(240, 240, 240));
		left.add(p,BorderLayout.CENTER);
		//left.add(jworkerstcnt);
		//left.add(jworkercnt);
		Panel pc = new Panel();
		pc.setLayout(new BorderLayout());
		pc.add(sp,BorderLayout.CENTER);
		jta.setBackground(new Color(225, 230, 246));
		jtajobs.setRows(13);
		pc.add(spjobs,BorderLayout.SOUTH);
		jtajobs.setBackground(new Color(244, 246, 252));
		this.add(pc,BorderLayout.CENTER);
		this.add(left,BorderLayout.EAST);
	}

	public JTextArea getJta() {
		return jta;
	}
	public JLabel GetJworkerstcnt() {
		return jworkerstcnt;	
	}
	public JLabel GetJworkercnt() {
		return jworkercnt;	
	}

}