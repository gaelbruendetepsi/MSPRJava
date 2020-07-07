package fr.java.mspr;

public class Classe_Visiteur extends Classe_Utilisateur {

	private String CNI;


	public Classe_Visiteur(int id, String nom, String prenom, String date_naissance, char genre, String telephone, String lien_photo,
			String cNI) {
		super(id,nom,prenom,genre,date_naissance,telephone,lien_photo);
		CNI = cNI;
		this.isAgent = false;
	}

	public String getCNI() {
		return CNI;
	}
	public void setCNI(String cNI) {
		CNI = cNI;
	}


}
