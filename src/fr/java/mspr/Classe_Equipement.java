package fr.java.mspr;

import java.util.ArrayList;

public class Classe_Equipement {
	private int id, stock;
	private String nom;
	public static ArrayList<Classe_Equipement> equipements_dispos = new ArrayList<Classe_Equipement>();
	
	
	public Classe_Equipement(int id, String nom, int stock) {
		super();
		this.id = id;
		this.stock = stock;
		this.nom = nom;
		equipements_dispos.add(this);
	}
	
	public static Classe_Equipement getById(int id) {
		for(Classe_Equipement e : equipements_dispos)
			if(e.getId()==id)
				return e;
		return null;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getstock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	

}
