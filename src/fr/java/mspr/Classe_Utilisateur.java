package fr.java.mspr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Classe_Utilisateur {
	protected int id;
	protected String nom, prenom, date_naissance, telephone, lien_photo;
	protected char genre;
	protected boolean isAgent;

	public Classe_Utilisateur() {

	}
	public Classe_Utilisateur(int id, String nom, String prenom, char genre, String date_naissance, String telephone, String lien_photo) {
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.date_naissance = date_naissance;
		this.telephone = telephone;
		this.lien_photo = lien_photo;
		this.genre = genre;
	}



	public boolean isAgent() {
		return isAgent;
	}
	public void setAgent(boolean isAgent) {
		this.isAgent = isAgent;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getDate_naissance() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		try {
			Date date = format.parse(date_naissance);
			String s = new SimpleDateFormat("dd MMMMM yyyy").format(date);
			return s;
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}
	public void setDate_naissance(String date_naissance) {
		this.date_naissance = date_naissance;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getLien_photo() {
		return lien_photo;
	}
	public void setLien_photo(String lien_photo) {
		this.lien_photo = lien_photo;
	}

	public String getGenre() {
		if(genre == 'H' || genre == 'h')
			return "Homme";
		else
			return "Femme";
	}
	public void setGenre(char genre) {
		this.genre = genre;
	}


}
