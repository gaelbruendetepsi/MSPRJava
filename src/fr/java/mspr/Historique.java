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
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatter;

import com.mysql.jdbc.Util;

import fr.java.mspr.Accueil.Listeners;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Historique extends JFrame {

	private boolean agent = false;
	
	public void Open() {
		setTitle("Historique");
		setSize(720, 480);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		if (Utils.connectedVisitor.isAgent())
			if (((Classe_Agent) Utils.connectedVisitor).isAdmin()) {	}
			else {
				agent = true;
			}
		else {
			Close();
			new Profil().Open();
		}

		JTable tableau = LoadHistorique();
		add(new JScrollPane(tableau), BorderLayout.CENTER);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				Close();
				new Profil().Open();
			}
		});
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private JTable LoadHistorique() {
		ArrayList<ArrayList<String>> v_resultat = Utils.MysqlRequest("SELECT * from VISITE" + (agent ? " WHERE ID_TABLE_VISITE = 0" : ""));
		ArrayList<ArrayList<String>> e_resultat = Utils.MysqlRequest("SELECT * from EMPRUNT_HIST" + (agent ? " WHERE ID_AGENT = "+Utils.connectedVisitor.getId() : ""));
		String[][] data = {{}};
		String  title[] = {"Date", "id utilisateur" , "Type d'historique", "Emprunt : Equipement", "Emprunt action", "Emprunt quantité : Ancienne => Nouvelle"};
		DefaultTableModel model = new DefaultTableModel(data, title);
		for (ArrayList<String> visite : v_resultat) 
			model.addRow(new Object[]{visite.get(1), visite.get(2) ,"Visite"});		

		for (ArrayList<String> emprunt : e_resultat) 
			model.addRow(new Object[]{emprunt.get(5), emprunt.get(1), "Emprunt", Classe_Equipement.getById(Integer.parseInt(emprunt.get(2))).getNom(), (Integer.parseInt(emprunt.get(3))<Integer.parseInt(emprunt.get(4)) ? "ajoute" : "retire") , emprunt.get(3) +" -> "+ emprunt.get(4) });	

model.fireTableDataChanged();

		return new JTable(model);
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

			}
		}
	}

}
