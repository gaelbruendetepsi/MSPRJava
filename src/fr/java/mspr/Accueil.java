package fr.java.mspr;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Accueil extends JFrame {

	public void Open() {
		setTitle("Accueil");
		setResizable(false);
		setSize(500,150);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel text = new JLabel("Code CNI : ");
		JTextField cni = new JTextField(20);
		Utils.setID(cni, "CNICODE");
		JButton confirm = new JButton("Confirmer");
		confirm.addActionListener(new Listeners());
		Utils.setID(confirm, "confirm");
		bottom.add(text);
		bottom.add(cni);
		bottom.add(confirm);
		add(bottom, BorderLayout.PAGE_END);


		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton login = new JButton("S'identifier");
		login.addActionListener(new Listeners());
		Utils.setID(login, "login");
		right.add(login);
		add(right, BorderLayout.PAGE_START);

		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void Close() {
		setVisible(false);
		dispose(); 	
	}
	class Listeners implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent comp = (JComponent) e.getSource();
			switch(Utils.getID(comp)) {
			case "confirm":
				String CNI = (((JTextField)Utils.getCompByID((JComponent) comp.getParent(), "CNICODE")).getText());
				if(CNI.equalsIgnoreCase("")) break;
				ArrayList<ArrayList<String>> resultat = Utils.MysqlRequest("SELECT * from VISITEUR WHERE CNI = '"+CNI+"'");
				if(resultat.isEmpty()) {
					//erreur, rien trouvé
					Close();
					new RegisterNewUtilisateur().Open(CNI);
				}
				else {
					//visiteur(s) trouvé(s), on prend le premier
					java.util.Date dt = new java.util.Date();
					java.text.SimpleDateFormat sdf =  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String currentTime = sdf.format(dt);
					//changer visiteur actuel
					Utils.connectedVisitor = new Classe_Visiteur(Integer.parseInt(resultat.get(0).get(0)),resultat.get(0).get(1),resultat.get(0).get(2),resultat.get(0).get(4),(char)resultat.get(0).get(3).charAt(0),resultat.get(0).get(5),resultat.get(0).get(6),resultat.get(0).get(7));
					//enregistrer visite
					Utils.MysqlRequest("INSERT INTO VISITE VALUES (NULL, '"+currentTime+"' , "+Utils.connectedVisitor.getId()+")");
					//ouvrir profil
					Close();
					new Profil().Open();
				}
				break;
			case "login": 
				Close();
				new Login().Open();
				break;
			}
		}

	}	
}
