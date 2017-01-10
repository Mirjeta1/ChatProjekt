package pis.hue2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;

public class Server extends JFrame {
	private JButton btnBeenden;
	private JButton btnStarten;
	private JScrollPane scrollen;
	private JLabel lblName;
	private JTextArea textArea;
	
	ArrayList clients;
	ArrayList<String> users;
	
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Server().setVisible(true);
			}
		});
	}

	
	
		public Server() {
		scrollen = new JScrollPane();
		btnStarten = new JButton();
		btnStarten.setFont(new Font("Arial", Font.PLAIN, 15));
		btnBeenden = new JButton();
		btnBeenden.setFont(new Font("Arial", Font.PLAIN, 15));
		lblName = new JLabel();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Chat");
		setName("server");

		btnStarten.setText("Server starten");
		btnStarten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startenActionPerformed(e);
			}
		});

		btnBeenden.setText("Server beenden");
		btnBeenden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				beendenActionPerformed(e);
			}
		});
		textArea = new JTextArea();
		textArea.setFont(new Font("Arial", Font.PLAIN, 15));
		scrollen.setViewportView(textArea);
		textArea.setEditable(false);
		
				textArea.setColumns(20);
				textArea.setRows(5);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(lblName)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(25)
					.addComponent(btnStarten, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(35)
					.addComponent(scrollen, GroupLayout.PREFERRED_SIZE, 469, GroupLayout.PREFERRED_SIZE))
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(358, Short.MAX_VALUE)
					.addComponent(btnBeenden, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblName)
					.addGap(11)
					.addComponent(btnStarten, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addGap(12)
					.addComponent(scrollen, GroupLayout.PREFERRED_SIZE, 340, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnBeenden)
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);

		pack();
	}

	private void beendenActionPerformed(ActionEvent e) {
		try {
			Thread.sleep(5000); 
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		mitteilen("Server shutdown! \n:say");
		textArea.append("Shutdown! \n");

		textArea.setText("");
	}

	private void startenActionPerformed(ActionEvent e) {
		Thread starten = new Thread(new Start());
		starten.start();

		textArea.append("Server started! \n");
	}

	public void hinzufuegen(String daten) {
		String msg;
		String hnzufgn = ": :Koppeln";//connect
		String fertig = "Server: :Fertig";//done
		String name = daten;
		textArea.append("Before " + name + " added. \n");
		users.add(name);
		textArea.append("After " + name + " added. \n");
		String[] list = new String[(users.size())];
		users.toArray(list);

		for (String token : list) {
			msg = (token + hnzufgn);
			mitteilen(msg);
		}
		mitteilen(fertig);
	}

	public void loeschen(String daten) {
		String msg;
		String hnzufgn = ": :Koppeln";
		String fertig = "Server: :Fertig";
		String name = daten;
		users.remove(name);
		String[] list = new String[(users.size())];
		users.toArray(list);

		for (String token : list) {
			msg = (token + hnzufgn);
			mitteilen(msg);
		}
		mitteilen(fertig);
	}

	public void mitteilen(String msg) {
		Iterator it = clients.iterator();

		while (it.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(msg);
				textArea.append("Sending: " + msg + "\n");
				writer.flush();
				textArea.setCaretPosition(textArea.getDocument().getLength());

			} catch (Exception e) {
				textArea.append("Error! \n");
			}
		}
	}
	public class Start implements Runnable {
		@Override
		public void run() {
			clients = new ArrayList();
			users = new ArrayList();

			try {
				ServerSocket ss = new ServerSocket(2222);

				while (true) {
					Socket clientSock = ss.accept();
					PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
					clients.add(writer);

					Thread listener = new Thread(new ClientHandler(clientSock, writer));
					listener.start();
					textArea.append("Connection enabled. \n");
					
				}
			} catch (Exception ex) {
				textArea.append("Error! \n");
			}
		
		}
	
	}
	public class ClientHandler implements Runnable {
		BufferedReader reader;
		Socket client;
		PrintWriter writer;

		public ClientHandler(Socket socket, PrintWriter user) {
			writer = user;
			try {
				client = socket;
				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			} catch (Exception ex) {
				textArea.append("Error! \n");
			}

		}

		@Override
		public void run() {
			String msg;
			String verbinden = "Koppeln";
			String trennen = "Entkoppeln";
			String s_chat = "Schreiben";
			String[] daten;

			try {
				while ((msg = reader.readLine()) != null) {
					textArea.append("Empfang: " + msg + "\n");
					daten = msg.split(":");

					for (String token:daten) {
						textArea.append(token + "\n");
					}

					if (daten[2].equals(verbinden)) {
						mitteilen((daten[0] + ":" + daten[1] + ":" + s_chat));
						hinzufuegen(daten[0]);
					} else if (daten[2].equals(trennen)) {
						mitteilen((daten[0] + ":entkoppelt." + ":" + s_chat));
						loeschen(daten[0]);
					} else if (daten[2].equals(s_chat)) {
						mitteilen(msg);
					} else {
						textArea.append("Irregularrities. \n");
					}
				}
			} catch (Exception e) {
				textArea.append("Connection lost. \n");
				clients.remove(writer);
				e.printStackTrace();
				
			}
		}
	}
	}
