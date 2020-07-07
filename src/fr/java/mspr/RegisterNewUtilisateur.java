package fr.java.mspr;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;
import com.mysql.jdbc.Util;

import fr.java.mspr.Accueil.Listeners;

import javax.imageio.ImageIO;
import javax.swing.*;
public class RegisterNewUtilisateur extends JFrame {
	public Webcam webcam = null;
	public String photo_url = "";
	public JLabel savedPic = null;

	public boolean creatingAgent = false;

	class Champ {
		String label, defaultVal, idChamp;
		int size;
		public Champ(String label, String defaultVal, int size, String idChamp){
			this.label=label;
			this.defaultVal = defaultVal;
			this.size = size;
			this.idChamp = idChamp;
		}
	}

	public void OpenAdmin() {
		if(Utils.connectedVisitor.isAgent()) 
			if(((Classe_Agent)Utils.connectedVisitor).isAdmin()) {
				creatingAgent = true;
				Open("");
			}
			else
				Close();
		else
			Close();

	}

	public void Open(String newCNI) {
		setTitle("S'enregistrer");
		setSize(720,480);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		JPanel top2 = new JPanel();
		top2.setLayout(new BoxLayout(top2, BoxLayout.Y_AXIS));
		top2.setPreferredSize(new Dimension(150, 440));
		top2.setMaximumSize(top2.getPreferredSize());


		{
			JLabel lab = new JLabel("Photo (clique pour sauvegarder)");
			lab.setAlignmentX(Component.CENTER_ALIGNMENT);
			top2.add(lab);	
			webcam = Webcam.getDefault();
			if(webcam == null){
				JOptionPane.showMessageDialog(this, "Pas de webcam détectée", "Erreur webcam", JOptionPane.ERROR_MESSAGE);	
				Close();
				new Accueil().Open();
				return;
			}
				

			webcam.setViewSize(WebcamResolution.VGA.getSize());
			WebcamPanel wpanel = new WebcamPanel(webcam);

			wpanel.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						DestroyPicture(top2);


						//création d'une photo
						photo_url = "photos/"+System.currentTimeMillis()+".png";
						ImageIO.write(webcam.getImage(), "PNG", new File(photo_url));
						BufferedImage myPicture = ImageIO.read(new File(photo_url));
						Image dimg = myPicture.getScaledInstance(198, 148, Image.SCALE_SMOOTH);
						savedPic = new JLabel(new ImageIcon(dimg));
						wpanel.setVisible(false);
						savedPic.setAlignmentX(Component.CENTER_ALIGNMENT);
						top2.add(savedPic, 1);
						lab.setText("Photo (clique pour recommencer)");
						savedPic.addMouseListener(new MouseListener() {
							@Override
							public void mouseClicked(MouseEvent e) {
								DestroyPicture(top2);

								wpanel.setVisible(true);
								wpanel.setPreferredSize(new Dimension(198, 2000));
								lab.setText("Photo (clique pour sauvegarder)");
							}
							public void mouseReleased(MouseEvent e) {}public void mousePressed(MouseEvent e) {}	public void mouseExited(MouseEvent e) {}public void mouseEntered(MouseEvent e) {}	
						});

					} catch (IOException  e1) {
						e1.printStackTrace();
					}
				}
				public void mouseReleased(MouseEvent e) {}public void mousePressed(MouseEvent e) {}public void mouseExited(MouseEvent e) {}public void mouseEntered(MouseEvent e) {}	
			});
			wpanel.setPreferredSize(new Dimension(198, 2000));
			wpanel.setAlignmentX(Component.CENTER_ALIGNMENT);
			wpanel.setMaximumSize(wpanel.getPreferredSize());
			top2.add(wpanel);

		}

		//automatisation des champs
		ArrayList<Champ> champs = new ArrayList<Champ>();
		champs.add(new Champ("Prénom", "", 20, "prenom"));
		champs.add(new Champ("Nom", "", 20, "nom"));
		champs.add(new Champ("Date de naissance (yyyy-mm-dd)", "", 10, "date_naissance"));
		champs.add(new Champ("Téléphone", "", 10, "telephone"));
		champs.add(new Champ("Genre (H/F)", "", 1, "genre"));

		if(!creatingAgent)
			champs.add(new Champ("CNI", newCNI, 20, "CNI"));
		else 
			champs.add(new Champ("Mot de passe", "", 20, "password"));

		//création des champs
		for(Champ champ : champs)
		{
			JLabel lab = new JLabel(champ.label);
			JTextField labt = new JTextField(champ.size);
			Utils.setID(labt, champ.idChamp);
			labt.setText(champ.defaultVal);
			lab.setAlignmentX(Component.CENTER_ALIGNMENT);
			labt.setPreferredSize(new Dimension(350, 100));
			labt.setMaximumSize(top2.getPreferredSize());
			top2.add(lab);	
			top2.add(labt);		
		}
		JButton envoyer = new JButton("Envoyer");
		envoyer.addActionListener(new Listeners());
		Utils.setID(envoyer, "envoyer");
		envoyer.setAlignmentX(Component.CENTER_ALIGNMENT);
		top2.add(envoyer);	


		add(top2, BorderLayout.PAGE_END);

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				Close();
				new Accueil().Open();
			}
		});

		setLocationRelativeTo(null);
		setVisible(true);
		webcam.open();

	}

	public void Close() {
		setVisible(false);
		DestroyPicture(null);
		if(webcam != null)
			webcam.close();
		dispose(); 	

	}

	public void DestroyPicture(JPanel top2) {
		//suppression d'une potentielle photo ratée
		if(!photo_url.equalsIgnoreCase("")) {
			new File(photo_url).delete();						
			photo_url = "";
		}
		if(savedPic != null) {
			if(top2 != null)
				top2.remove(savedPic);
			savedPic = null;
		}
	}
	class Listeners implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent comp = (JComponent) e.getSource();
			switch(Utils.getID(comp)) {
			case "envoyer":
				String prenom = ((JTextField)Utils.getCompByID((JComponent) comp.getParent(), "prenom")).getText();
				String nom = ((JTextField)Utils.getCompByID((JComponent) comp.getParent(), "nom")).getText();
				String date_naissance = ((JTextField)Utils.getCompByID((JComponent) comp.getParent(), "date_naissance")).getText();
				String telephone = ((JTextField)Utils.getCompByID((JComponent) comp.getParent(), "telephone")).getText();
				String genre = ((JTextField)Utils.getCompByID((JComponent) comp.getParent(), "genre")).getText();
				String CNI="", PASS="";
				if(!creatingAgent)
					 CNI = ((JTextField)Utils.getCompByID((JComponent) comp.getParent(), "CNI")).getText();
				else
					PASS = ((JTextField)Utils.getCompByID((JComponent) comp.getParent(), "password")).getText();

				//vérifications des champs
				if(prenom.equalsIgnoreCase("") || nom.equalsIgnoreCase("") || date_naissance.equalsIgnoreCase("") ||
						telephone.equalsIgnoreCase("") || genre.equalsIgnoreCase("") || (CNI.equalsIgnoreCase("") && PASS.equalsIgnoreCase("")) || photo_url.equalsIgnoreCase("") ||
						telephone.length() != 10|| (!genre.equalsIgnoreCase("H") && !genre.equalsIgnoreCase("F"))  || !date_naissance.matches("\\d{4}-\\d{2}-\\d{2}")) {
					JOptionPane.showMessageDialog(comp, "Champs mal remplis !", "Erreur de formulaire", JOptionPane.ERROR_MESSAGE);	
					return;
				}
				//création du nouveau visiteur/agent si possible
				Utils.MysqlRequest("INSERT INTO "+(creatingAgent ? "AGENT" : "VISITEUR")+" VALUES (NULL, '"+nom+"', '"+prenom+"', '"+genre+"', '"+date_naissance+"', '"+telephone+"', '"+photo_url+"', "+(creatingAgent ? "'"+PASS+"', '0'" : "'"+CNI+"'")+")");		

				//empecher la destruction de la photo
				photo_url = "";
				int option = JOptionPane.showConfirmDialog(null, "Votre compte " + (creatingAgent ? "agent" : "visiteur") + " à bien été créé !", "Création réussie", JOptionPane.DEFAULT_OPTION);
				if (option == JOptionPane.OK_OPTION) {
					Close();
					new Accueil().Open();
				}

				break;
			}
		}
	}	

}
