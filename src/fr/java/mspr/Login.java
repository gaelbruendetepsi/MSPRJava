package fr.java.mspr;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends JFrame {
	public void Open() {
		setTitle("Login");
		setResizable(false);
		setSize(500,150);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		JPanel form = new JPanel();
		form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

		JLabel login = new JLabel("Prenom.Nom : ");
		JTextField logint = new JTextField(20);
		Utils.setID(logint, "loginTxt");
		JLabel pass = new JLabel("Mot de passe : ");
		JPasswordField passt = new JPasswordField(20);
		Utils.setID(passt, "passTxt");
		JButton confirm = new JButton("Connexion");
		confirm.addActionListener(new Listeners());
		Utils.setID(confirm, "login");
		login.setAlignmentX(Component.CENTER_ALIGNMENT);
		logint.setAlignmentX(Component.CENTER_ALIGNMENT);
		pass.setAlignmentX(Component.CENTER_ALIGNMENT);
		passt.setAlignmentX(Component.CENTER_ALIGNMENT);
		confirm.setAlignmentX(Component.CENTER_ALIGNMENT);
		form.add(login);
		form.add(logint);
		form.add(pass);
		form.add(passt);
		form.add(confirm);

		add(form, BorderLayout.CENTER);

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				Close();
				new Accueil().Open();
			}
		});

		setLocationRelativeTo(null);
		setVisible(true);


		logint.setText("adrien.ketterer");
		passt.setText("oui");
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
			case "login": 
				String login = ((JTextField)Utils.getCompByID((JComponent) comp.getParent(), "loginTxt")).getText();
				String pass = ((JPasswordField)Utils.getCompByID((JComponent) comp.getParent(), "passTxt")).getText();
				if(login.equalsIgnoreCase("") || !login.contains(".") || login.split("\\.").length != 2 || pass.equalsIgnoreCase("")) {
					JOptionPane.showMessageDialog(comp, "Identifiants incorrects", "Erreur d'identifiants", JOptionPane.ERROR_MESSAGE);	
					return;
				}
				String prenom = login.split("\\.")[0];
				String nom = login.split("\\.")[1];

				ArrayList<ArrayList<String>> resultat = Utils.MysqlRequest("SELECT * from AGENT WHERE PRENOM = '"+prenom+"' AND nom ='"+nom+"' AND password = '"+pass+"'");
				if(resultat.isEmpty()) {
					//erreur, rien trouvé
					JOptionPane.showMessageDialog(comp, "Identifiants incorrects", "Erreur d'identifiants", JOptionPane.ERROR_MESSAGE);	
				}
				else {
					//agent(s) trouvé(s), on prend le premier
					//changer visiteur actuel
					Utils.connectedVisitor = new Classe_Agent(Integer.parseInt(resultat.get(0).get(0)),resultat.get(0).get(1),resultat.get(0).get(2),(char)
							resultat.get(0).get(4).charAt(0),resultat.get(0).get(3),resultat.get(0).get(5),resultat.get(0).get(6), resultat.get(0).get(8).equalsIgnoreCase("true"));
					//ouvrir profil
					Close();
					new Profil().Open();
				}

				break;


			}
		}

	}	
}
