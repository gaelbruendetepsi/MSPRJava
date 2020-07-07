package fr.java.mspr;

public class Classe_Agent extends Classe_Utilisateur {

	private boolean isAdmin;

	public Classe_Agent(int id, String nom, String prenom, char genre, String date_naissance, String telephone, String lien_photo, boolean isAdmin) {
		super(id,nom,prenom,genre,date_naissance,telephone,lien_photo);
		this.isAgent = true;
		this.isAdmin = isAdmin;
	}

	public Classe_Emprunt getEmpruntByEquipementId(int id) {
		if(this.isAgent) {
			for(Classe_Emprunt emprunt : Classe_Emprunt.emprunts_actuels)
				if(emprunt.getEquipement().getId() == id && emprunt.getAgent().getId() == this.getId())
					return emprunt;
			return null;
		}
		else return null;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}


}
