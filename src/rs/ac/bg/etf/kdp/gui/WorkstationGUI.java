package rs.ac.bg.etf.kdp.gui;


import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;


import rs.ac.bg.etf.kdp.RemoteCommunication;

public class WorkstationGUI extends JFrame {
	boolean isMouseOver = false;
	int count = 0;
	String ip;
	JProgressBar[] jpb;
	JButton[] jb;
	JLabel[] jlb;
	int size;
	int port;
	RemoteCommunication l;
	public WorkstationGUI(RemoteCommunication linda,String ip,int size,int port) {
		this.ip=ip;
		this.port= port;
		this.size = size;
		this.setTitle("Workstation " + ip+" "+port);
		this.setBounds(100, 100, 407, size*70);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//this.getContentPane().setLayout(null);
		jpb = new JProgressBar[size];
		jlb = new JLabel[size];
		jb = new JButton[size];
		l=linda;
		Panel p = new Panel(new GridLayout(size,1));
		for(int i=0;i<size;i++) {
			Panel pp = new Panel();
			jpb[i]=new JProgressBar();
			jlb[i]=new JLabel("No Job");
			//jb[i]=new JButton("Stop");
			pp.add(jpb[i]);
			pp.add(jlb[i]);
			//pp.add(jb[i]);
			p.add(pp);
		}
		this.add(p);
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(10, 45, 371, 22);
		//this.getContentPane().add(progressBar);

		JButton btnMousOver = new JButton("Stop");
		btnMousOver.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				isMouseOver = true;
				btnMousOver.setEnabled(false);
				Thread go = new Thread() {
					public void run() {
						while (isMouseOver && count < 101) {
							count = count + 2;
							progressBar.setValue(count);
							// do some stuffs here
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				};

			}

			public void mouseExited(MouseEvent evt) {
				isMouseOver = false;
				btnMousOver.setEnabled(true);
			}

			public void mousePressed(MouseEvent evt) {
				JOptionPane.showMessageDialog(null, "Mouse CLicked");
			}

			public void mouseReleased(MouseEvent evt) {
				JOptionPane.showMessageDialog(null, "Mouse released");
			}
		});
		btnMousOver.setBounds(142, 11, 108, 23);
		this.setVisible(true);
	}
	public JProgressBar getProgreesBar(int i) {
		return jpb[i];
	}
	public JLabel getJLabel(int i) {
		return jlb[i];
	}
}
