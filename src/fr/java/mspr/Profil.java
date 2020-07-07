package fr.java.mspr;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

import com.mysql.jdbc.Util;

import fr.java.mspr.Accueil.Listeners;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Profil extends JFrame {

	public void Open() {
		setTitle("Profil");
		setSize(720, 480);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


		if (Utils.connectedVisitor.isAgent()) {
			JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
			if (((Classe_Agent) Utils.connectedVisitor).isAdmin()) {
				JButton createagent = new JButton("Créer un nouvel agent");
				createagent.addActionListener(new Listeners());
				Utils.setID(createagent, "createAgent");
				top.add(createagent);
			}
			JButton historique = new JButton("Voir l'historique");
			Utils.setID(historique, "historique");
			historique.addActionListener(new Listeners());
			top.add(historique);
			add(top, BorderLayout.PAGE_START);
		}
		
		{
			JPanel top2 = new JPanel();
			top2.setLayout(new BoxLayout(top2, BoxLayout.Y_AXIS));

			// affichage photo 
			BufferedImage myPicture;
			try {
				myPicture = ImageIO.read(new File(Utils.connectedVisitor.getLien_photo()));
				Image dimg = myPicture.getScaledInstance(198, 148, Image.SCALE_SMOOTH);
				JLabel photo = new JLabel(new ImageIcon(dimg));
				photo.setAlignmentX(Component.CENTER_ALIGNMENT);
				top2.add(photo);
			} catch (IOException e) {
				e.printStackTrace();
			}


			//affichage status utilisateur
			JLabel status = new JLabel();
			status.setAlignmentX(Component.CENTER_ALIGNMENT);
			if (Utils.connectedVisitor.isAgent())
				if (((Classe_Agent) Utils.connectedVisitor).isAdmin())
					status.setText("Administrateur");
				else
					status.setText("Agent");
			else
				status.setText("Visiteur");
			top2.add(status);


			JLabel nom = new JLabel(Utils.connectedVisitor.getNom().toUpperCase());
			nom.setAlignmentX(Component.CENTER_ALIGNMENT);
			top2.add(nom);
			JLabel prenom = new JLabel(Utils.connectedVisitor.getPrenom().substring(0, 1).toUpperCase()
					+ Utils.connectedVisitor.getPrenom().substring(1));
			prenom.setAlignmentX(Component.CENTER_ALIGNMENT);
			top2.add(prenom);
			JLabel datenaiss = new JLabel("Né" + (Utils.connectedVisitor.getGenre().startsWith("F") ? "e" : "")
					+ " le : " + Utils.connectedVisitor.getDate_naissance());
			datenaiss.setAlignmentX(Component.CENTER_ALIGNMENT);
			top2.add(datenaiss);
			JLabel tel = new JLabel("Tél : " + Utils.connectedVisitor.getTelephone());
			tel.setAlignmentX(Component.CENTER_ALIGNMENT);
			top2.add(tel);
			JLabel genre = new JLabel(String.valueOf(Utils.connectedVisitor.getGenre()));
			genre.setAlignmentX(Component.CENTER_ALIGNMENT);
			top2.add(genre);
			top2.setPreferredSize(new Dimension(250, 50));
			top2.setMaximumSize(top2.getPreferredSize());
			add(top2, BorderLayout.EAST);
		}

		if (Utils.connectedVisitor.isAgent()) {

			LoadEqEm();

			Classe_Agent agent = (Classe_Agent) Utils.connectedVisitor;

			JPanel bottom = new JPanel();
			bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
			for (Classe_Equipement equipement : Classe_Equipement.equipements_dispos) {

				JLabel label = new JLabel(equipement.getNom());
				SpinnerModel value;
				if (agent.getEmpruntByEquipementId(equipement.getId()) != null)
					value = new SpinnerNumberModel(agent.getEmpruntByEquipementId(equipement.getId()).getNb(), 0,
							(agent.getEmpruntByEquipementId(equipement.getId()).getNb() + equipement.getstock()), 1);
				else
					value = new SpinnerNumberModel(0, 0, equipement.getstock(), 1);
				JSpinner spinner = new JSpinner(value);
				spinner.setPreferredSize(new Dimension(50, 20));
				spinner.setMaximumSize(spinner.getPreferredSize());
				bottom.add(label);
				bottom.add(spinner);
				label.setAlignmentX(Component.CENTER_ALIGNMENT);
				spinner.setAlignmentX(Component.CENTER_ALIGNMENT);
				JFormattedTextField field = (JFormattedTextField) spinner.getEditor().getComponent(0);
				DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
				formatter.setCommitsOnValidEdit(true);
				Utils.setID(spinner, String.valueOf(spinner.getValue()));
				spinner.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						int ancienne_quantite = Integer.parseInt(Utils.getID(spinner));
						int nouvelle_quantite = Integer.parseInt(((JSpinner) e.getSource()).getValue().toString());

						//historisation emprunts
						java.util.Date dt = new java.util.Date();
						java.text.SimpleDateFormat sdf =  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String currentTime = sdf.format(dt);
						Utils.MysqlRequest("INSERT INTO EMPRUNT_HIST VALUES (NULL, "+Utils.connectedVisitor.getId()+", "+equipement.getId()+", "+ancienne_quantite+", "+nouvelle_quantite+", '"+currentTime+"')");

						//agent remet en stock et ne npossède plus cet equipement, on supprime donc l'emprunt.
						if (nouvelle_quantite <= 0)
							Utils.MysqlRequest("DELETE FROM EMPRUNT WHERE ID_AGENT=" + Utils.connectedVisitor.getId() + " AND ID_EQUIPEMENT = " + equipement.getId());
						else {
							// tentative d'update d'un emprunt potentiel
							ArrayList<ArrayList<String>> resultat = Utils
									.MysqlRequest("UPDATE EMPRUNT SET NB_EQUIPEMENT = " + nouvelle_quantite
											+ " WHERE ID_AGENT=" + Utils.connectedVisitor.getId()
											+ " AND ID_EQUIPEMENT = " + equipement.getId());

							// pas d'emprunt avec cet equipement, on le créé
							if (resultat == null)
								Utils.MysqlRequest("INSERT INTO EMPRUNT VALUES (NULL, " + nouvelle_quantite + "," + equipement.getId() + "," + Utils.connectedVisitor.getId() + ")");
						}
						// on met a jour le stock, pareil dans la variable
						// on reduit, donc on ajoute du stock
						if (ancienne_quantite > nouvelle_quantite) {
							Utils.MysqlRequest("UPDATE EQUIPEMENT SET STOCK = " + (equipement.getstock() + 1)
									+ " WHERE ID_TABLE_EQUIPEMENT = " + equipement.getId());
							equipement.setStock(equipement.getstock() + 1);
						}
						// on rajoute, donc on enleve du stock
						else {
							Utils.MysqlRequest("UPDATE EQUIPEMENT SET STOCK = " + (equipement.getstock() - 1)
									+ " WHERE ID_TABLE_EQUIPEMENT = " + equipement.getId());
							equipement.setStock(equipement.getstock() - 1);
						}
						Utils.setID(spinner, ((JSpinner) e.getSource()).getValue().toString());
					}
				});
			}

			bottom.setPreferredSize(new Dimension(250, Classe_Equipement.equipements_dispos.size() * 40));
			bottom.setMaximumSize(bottom.getPreferredSize());
			add(bottom, BorderLayout.PAGE_END);
		}

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				Close();
				new Accueil().Open();
			}
		});


		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void LoadEqEm() {
		Classe_Equipement.equipements_dispos.clear();
		Classe_Emprunt.emprunts_actuels.clear();

		ArrayList<ArrayList<String>> resultat = Utils.MysqlRequest("SELECT * from EQUIPEMENT");
		for (ArrayList<String> equipement : resultat) {
			new Classe_Equipement(Integer.parseInt(equipement.get(0)), equipement.get(1),
					Integer.parseInt(equipement.get(2)));

		}

		resultat = Utils.MysqlRequest("SELECT * from EMPRUNT WHERE ID_AGENT='" + Utils.connectedVisitor.getId() + "'");
		for (ArrayList<String> emprunt : resultat)
			new Classe_Emprunt(Integer.parseInt(emprunt.get(0)), Integer.parseInt(emprunt.get(1)),
					Classe_Equipement.getById(Integer.parseInt(emprunt.get(2))), (Classe_Agent) Utils.connectedVisitor);

	}

	public void Close() {
		setVisible(false);
		dispose();
	}

	class Listeners implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent comp = (JComponent) e.getSource();
			switch (Utils.getID(comp)) {
			case "createAgent":
				Close();
				new RegisterNewUtilisateur().OpenAdmin();
				break;
			case "historique":
				Close();
				new Historique().Open();
				break;
			}
		}
	}

}
