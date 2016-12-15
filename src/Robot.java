//Fichier : Robot.java
//Auteurs : Gutierrez Cyrian - Magnin Gauthier - Mounier Baptiste
//Contexte : TSCR - Projet robotique - M1 SCA

import lejos.robotics.Color;

/** Repr�sentation du Robot */
public class Robot {

	/** Tableau bidimensionnel repr�sentant la carte (grille) */
	private int[][] map;
	/** Position de d�part en abscisses */
	private int posX;
	/** Position de d�part en ordonn�es */
	private int posY;
	/** Direction d'avancement */
	private int direction;
	
	/** D�placement d�croissant en Y */
	public final static int VERS_LE_HAUT = 1;
	/** D�placement croissant en X */
	public final static int VERS_LA_DROITE = 2;
	/** D�placement croissant en Y */
	public final static int VERS_LE_BAS = 3;
	/** D�placement d�croissant en X */
	public final static int VERS_LA_GAUCHE = 4;
	
	/** Constructeur principal du robot
	 * @param nbLigne Nombre de lignes
	 * @param nbColonne Nombre de colonnes
	 * @param x Position de d�part en abscisses
	 * @param y Position de d�part en ordonn�es */
	public Robot(int nbLigne, int nbColonne, int x, int y, int dir){
		posX = x;
		posY = y;
		direction = dir;
		map = new int[nbLigne][nbColonne];
		
		for(int l=0; l<nbLigne; l++){
			for(int c=0; c<nbColonne; c++){
				map[l][c] = Color.NONE;
			}
		}
	}
	
	/** Met � jour la carte sur la position du robot
	 * @param color Couleur detect�e et � mettre dans la carte */
	public void updateMap(int color){
		
		map[posY][posX] = color;
	}
	
	/** Met � jour la position du robot en fonction de sa direction */
	public void updatePos(){
		switch(direction){
			case VERS_LA_GAUCHE:
				posX -= 1;
				break;
				
			case VERS_LA_DROITE:
				posX += 1;
				break;
				
			case VERS_LE_HAUT:
				posY -= 1;
				break;
				
			case VERS_LE_BAS:
				posY += 1;
				break;
		}
	}
	
	/** Test si la case sur laquelle se trouve le robot a d�j� �t� explor�e
	 * @return Vrai si la case poss�de une couleur */
	public boolean isAlreadyExplored(){
		return (map[posX][posY] != Color.NONE);
	}
}