package fr.java.mspr;

import java.util.ArrayList;

public class Classe_Emprunt {
private int id, nb;
private Classe_Equipement equipement;
private Classe_Agent agent;

public static ArrayList<Classe_Emprunt> emprunts_actuels = new ArrayList<Classe_Emprunt>();


public Classe_Emprunt(int id, int nb, Classe_Equipement equipement, Classe_Agent agent) {
	super();
	this.id = id;
	this.nb = nb;
	this.equipement = equipement;
	this.agent = agent;
	this.emprunts_actuels.add(this);
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getNb() {
	return nb;
}
public void setNb(int nb) {
	this.nb = nb;
}
public Classe_Equipement getEquipement() {
	return equipement;
}
public void setEquipement(Classe_Equipement equipement) {
	this.equipement = equipement;
}
public Classe_Agent getAgent() {
	return agent;
}
public void setAgent(Classe_Agent agent) {
	this.agent = agent;
}



}
