package pis.hue2;

import java.net.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import java.awt.Color;

public class Client extends JFrame {
	private JButton btnKoppeln;
	private JButton btnEntkoppeln;
	private JButton btnSchicken;
	private JLabel lblBenutzer;
	private JTextArea textArea;
	private JTextField tf;
	private JTextField eingabeName;
	private JScrollBar scrollBar;
	private DefaultListModel listModel;
	private  JList list;

	String nutzername;
	ArrayList<String> benutzerListe = new ArrayList();
	Boolean verbindung = false;

	Socket sock;
	BufferedReader reader;
	PrintWriter writer;

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Client().setVisible(true);
			}
		});
	}

	public void Lauscher() {
		Thread einleser = new Thread(new Einleser());
		einleser.start();
	}

	public void hinzufuegen(String daten) {
		try{
			if(benutzerListe.size()>3){
				throw new Exception("e2");
			}
			
		}catch(Exception e2){
			textArea.append("refused: too_many_users \n");
			eingabeName.setEditable(true);
			
		}
		try{
			
			for(int i = 0; i<benutzerListe.size(); i++){
				if(benutzerListe.get(i).equals(eingabeName.getText())){
					
					throw new Exception("e3");
					
					
				}
			}
		}catch(Exception e3){
			textArea.append("refused: name_in_use \n");
			eingabeName.setEditable(true);
			
				}
		try{ 
			
			if(eingabeName.getText().length()>30){
			throw new Exception("e4");
		}
		
		else for(int i = 0; i<eingabeName.getText().length(); i++){
			if(eingabeName.getText().charAt(i)== ':'){
				throw new Exception("e4");
			}
		}
		
		}catch(Exception e4){
			textArea.append("refused: invalid_name \n");
			eingabeName.setEditable(true);
			
		}
		
		benutzerListe.add(daten);
	}

	public void loeschen(String daten) {
		textArea.append(daten + " logged out.\n");
	}

	public void sch_benutzer() {
		String[] list = new String[(benutzerListe.size())];
		benutzerListe.toArray(list);
		for (String token : list) {

		}
	}

	public void trennenSenden() {
		String gehen = (nutzername + ": :Entkoppelt");
		try {
			writer.println(gehen);
			writer.flush();
		} catch (Exception e) {
			textArea.append("Disconnection unable.\n");
		}
	}

	public void trennen() {
		try {
			textArea.append("Disconnected.\n");
			sock.close();
		} catch (Exception ex) {
			textArea.append("Disconnection unable. \n");
		}
		verbindung = false;
		eingabeName.setEditable(true);

	}

	public class Einleser implements Runnable {
		@Override
		public void run() {
			String[] daten;
			String stream;
			String fertig = "Fertig";
			String verbinden = "Koppeln";
			String trennen = "Entkoppeln";
			String chat = "Schreiben";

			try {
				while ((stream = reader.readLine()) != null) {
					daten = stream.split(":");

					if (daten[2].equals(chat)) {
						textArea.append(daten[0] + ": " + daten[1] + "\n");
						textArea.setCaretPosition(textArea.getDocument().getLength());
					} else if (daten[2].equals(verbinden)) {
						textArea.removeAll();
						hinzufuegen(daten[0]);
					} else if (daten[2].equals(trennen)) {
						loeschen(daten[0]);
					} else if (daten[2].equals(fertig)) {

						sch_benutzer();
						benutzerListe.clear();
					}
				}
			} catch (Exception e) {
			}
		}
	}

	public Client() {
		JScrollPane jScrollPane1 = new JScrollPane();
		lblBenutzer = new JLabel();
		lblBenutzer.setFont(new Font("Arial", Font.PLAIN, 17));
		eingabeName = new JTextField();
		eingabeName.setFont(new Font("Arial", Font.PLAIN, 15));
		btnKoppeln = new JButton();
		btnKoppeln.setFont(new Font("Arial", Font.PLAIN, 15));
		btnKoppeln.setBackground(new Color(255, 255, 255));
		btnKoppeln.setForeground(new Color(51, 153, 0));
		btnEntkoppeln = new JButton();
		btnEntkoppeln.setFont(new Font("Arial", Font.PLAIN, 15));
		btnEntkoppeln.setBackground(new Color(255, 255, 255));
		btnEntkoppeln.setForeground(new Color(255, 0, 0));
		tf = new JTextField();
		tf.setFont(new Font("Arial", Font.PLAIN, 15));
		btnSchicken = new JButton();
		btnSchicken.setFont(new Font("Arial", Font.PLAIN, 15));
		btnSchicken.setForeground(new Color(255, 255, 255));
		btnSchicken.setBackground(new Color(51, 153, 0));

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Chat");
		setName("client");

		lblBenutzer.setText("Geben Sie hier einen Benutzernamen ein:");

		btnKoppeln.setText("verbinden");
		btnKoppeln.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				verbindenActionPerformed(e);
			}
		});

		btnEntkoppeln.setText("trennen");
		btnEntkoppeln.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trennenActionPerformed(e);
			}
		});

		btnSchicken.setText(">");
		btnSchicken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendenActionPerformed(e);
			}
		});
		textArea = new JTextArea();
		textArea.setFont(new Font("Arial", Font.PLAIN, 15));
		textArea.setColumns(20);
		textArea.setRows(5);
		jScrollPane1.setViewportView(textArea);
		textArea.setEditable(false);
		
		
		
		

		
		 list = new JList(benutzerListe.toArray());
		  
		
		  
		  list.setBackground(new Color(255, 255, 204));
		list.setFont(new Font("Arial", Font.PLAIN, 15));

		scrollBar = new JScrollBar();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup().addGap(12)
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(lblBenutzer)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(eingabeName, GroupLayout.PREFERRED_SIZE, 390, GroupLayout.PREFERRED_SIZE)
								.addGap(40)
								.addComponent(btnKoppeln, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 390, GroupLayout.PREFERRED_SIZE)
								.addGap(7)
								.addComponent(scrollBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addGap(12)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(btnEntkoppeln, GroupLayout.PREFERRED_SIZE, 142,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(list, GroupLayout.PREFERRED_SIZE, 142,
												GroupLayout.PREFERRED_SIZE)))
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(tf, GroupLayout.PREFERRED_SIZE, 399, GroupLayout.PREFERRED_SIZE)
								.addGap(18).addComponent(btnSchicken, GroupLayout.PREFERRED_SIZE, 151,
										GroupLayout.PREFERRED_SIZE)))
				.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup().addGap(13).addComponent(lblBenutzer).addGap(7)
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup().addGap(1).addComponent(eingabeName,
								GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnKoppeln))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrollBar, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup().addComponent(btnEntkoppeln).addGap(15)
								.addComponent(list, GroupLayout.PREFERRED_SIZE, 270, GroupLayout.PREFERRED_SIZE)))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(
						groupLayout.createParallelGroup(Alignment.LEADING).addComponent(tf, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(btnSchicken))
				.addContainerGap()));
		getContentPane().setLayout(groupLayout);

		pack();
	}

	private void verbindenActionPerformed(ActionEvent e) {
		
	
		if (verbindung == false) {
			nutzername = eingabeName.getText();
			eingabeName.setEditable(false);

			try {
				sock = new Socket("127.0.0.1", 2222);

				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				writer = new PrintWriter(sock.getOutputStream());
				writer.println(nutzername + ":gekoppelt.:Koppeln");
				writer.flush();
				verbindung = true;
		
			
			} catch (Exception e1) {
				textArea.append("Unable to connect! \n");
				eingabeName.setEditable(true);
			}

			Lauscher();

		} else if (verbindung == true) {
			textArea.append("You are logged in. \n");
		}
	}

	private void trennenActionPerformed(ActionEvent e) {
		trennenSenden();
		trennen();
	}

	private void sendenActionPerformed(ActionEvent e) {
		String leer = "";
		if ((tf.getText()).equals(leer)) {
			tf.setText("");
			tf.requestFocus();
		} else {
			try {
				writer.println(nutzername + ":" + tf.getText() + ":Schreiben");
				writer.flush();
			} catch (Exception e1) {
				textArea.append("Message unable to send. \n");
			}
			tf.setText("");
			tf.requestFocus();
		}

		tf.setText("");
		tf.requestFocus();
	}

}
