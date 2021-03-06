//Fichier : Robot.java
//Auteurs : Gutierrez Cyrian - Magnin Gauthier - Mounier Baptiste
//Contexte : TSCR - Projet robotique - M1 SCA

package robot;

import java.util.ArrayList;
import java.util.HashMap;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.robotics.Color;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;

/** Repr�sentation du Robot */
public class Robot {

	/** Tableau bidimensionnel repr�sentant la carte (grille) */
	private int[][] map = {{Color.RED, Color.BLUE, Color.GREEN, Color.GREEN, Color.WHITE},
		       {Color.GREEN, Color.BLUE, Color.GREEN, Color.GREEN, Color.GREEN},
		       {Color.GREEN, Color.BLUE, Color.BLUE, Color.GREEN, Color.BROWN},
		       {Color.GREEN, Color.GREEN, Color.BLUE, Color.GREEN, Color.GREEN},
		       {Color.GREEN, Color.BROWN, Color.BROWN, Color.BROWN, Color.GREEN},
		       {Color.GREEN, Color.GREEN, Color.GREEN, Color.RED, Color.BLUE},
		       {Color.WHITE, Color.GREEN, Color.GREEN, Color.GREEN, Color.BLUE}};
	/* Carte, uniquement une fois objectif exploration r�alis�e (au-dessus), en-dessous sinon */
	/*private int[][] map;*/
	/** Position de d�part en abscisses */
	private int posX;
	/** Position de d�part en ordonn�es */
	private int posY;
	/** Direction d'avancement */
	private int direction;

	/** D�placement d�croissant en Y */
	public final static int VERS_LE_HAUT = 0;
	/** D�placement croissant en X */
	public final static int VERS_LA_DROITE = 1;
	/** D�placement croissant en Y */
	public final static int VERS_LE_BAS = 2;
	/** D�placement d�croissant en X */
	public final static int VERS_LA_GAUCHE = 3;
	
	/** Changement d'orientation par rotation droite */
	public final static int ROTATION_DROITE = 1;
	/** Changement d'orientation par rotation gauche */
	public final static int ROTATION_GAUCHE = -1;
	/** Changement d'orientation par demi-tour */
	public final static int DEMI_TOUR = 2;
	
	/** Correspondance des couleurs avec l'environnement */
	private static HashMap<Integer,String> environnement;
	/** Constante de l'environnement : Prairie */
	private final static String PRAIRIE = "Prairie";
	/** Constante de l'environnement : Oc�an */
	private final static String OCEAN = "Oc�an";
	/** Constante de l'environnement : Montagne */
	private final static String MONTAGNE = "Montagne";
	/** Constante de l'environnement : Ville */
	private final static String VILLE = "Ville";
	/** Constante de l'environnement : D�part */
	private final static String DEPART = "D�part";
	
	/** Vrai si le robot doit activer le comportement AboutTurn */
	private boolean demandeDemiTour = false;
	/** Vrai si le robot doit activer le comportement TurnRight */
	private boolean demandeQuartDeTourDroit = false;
	/** Vrai si le robot doit activer le comportement TurnLeft */
	private boolean demandeQuartDeTourGauche = false;
	
	/** Comportement d'avancement */
	private Behavior bDriveForward;
	/** Pilot permettant d'effectuer des trajectoires */
	private DifferentialPilot pilot;
	
	/** Etat de la r�ception d'une demande d'aide */
	private boolean demandeAideRecue = false;
	/** Position en abscisses du robot � secourir */
	private int xTarget;
	/** Position en ordonn�es du robot � secourir */
	private int yTarget;
	
	/** Mutateur de l'�tat de la r�ception d'une demande d'aide */
	public void setDemandeAideRecue(boolean demandeAideRecue){
		this.demandeAideRecue = demandeAideRecue;
	}
	
	/** Accesseur de l'�tat de la r�ception d'une demande d'aide
	 * @return L'�tat de la r�ception d'une demande d'aide, vrai si une demande est re�ue */
	public boolean isDemandeAideRecue(){
		return demandeAideRecue;
	}
	
	/** Met � jours la position du robot � secourir
	 * @param x La position en abscisse du robot � secourir
	 * @param y La position en ordonn�es du robot � secourir */
	public void updateTargetPosition(int x, int y){
		xTarget = x;
		yTarget = y;
	}
	
	/** Accesseur de la position en abscisse du robot � secourir
	 * @return La position en abscisse du robot � secourir */
	public int getXTarget(){
		return xTarget;
	}

	/** Accesseur de la position en ordonn�es du robot � secourir
	 * @return La position en ordonn�es du robot � secourir */
	public int getYTarget(){
		return yTarget;
	}

	/** Constructeur principal du robot
	 * @param nbLigne Nombre de lignes
	 * @param nbColonne Nombre de colonnes
	 * @param x Position de d�part en abscisses
	 * @param y Position de d�part en ordonn�es */
	public Robot(int nbLigne, int nbColonne, int x, int y, int dir){
		posX = x;
		posY = y;
		direction = dir;
		
		/* Carte si exploration � faire */
		/*map = new int[nbLigne][nbColonne];
		for(int l=0; l<nbLigne; l++){
			for(int c=0; c<nbColonne; c++){
				map[l][c] = Color.NONE;
			}
		}*/

		environnement = new HashMap<Integer,String>();
		environnement.put(Color.GREEN, PRAIRIE);
		environnement.put(Color.BLUE, OCEAN);
		environnement.put(Color.BROWN, MONTAGNE);
		environnement.put(Color.RED, VILLE);
		environnement.put(Color.WHITE, DEPART);
		environnement.put(Color.NONE, "-");
		
		pilot = new DifferentialPilot(5.6, 12.0, Motor.B, Motor.C);
			//Diam�tre des roues, espace entre les roues, moteur gauche, moteur droit
		pilot.setTravelSpeed(5.);
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
	
	/** Met � jour la position du robot en fonction de sa direction (en sens inverse) */
	public void updatePosInverse(){
		switch(direction){
		case VERS_LA_GAUCHE:
			posX += 1;
			break;

		case VERS_LA_DROITE:
			posX -= 1;
			break;

		case VERS_LE_HAUT:
			posY += 1;
			break;

		case VERS_LE_BAS:
			posY -= 1;
			break;
	}
	}
	
	/** Mettre � jour la direction du robot en fonction d'une rotation
	 * @param rotation Rotation effectu�e entra�nant la modification de la direction : ROTATION_DROITE | ROTATION_GAUCHE */
	public void updateDirection(int rotation){
		direction = (direction + rotation) % 4;
		if(direction < 0) direction = 3;		//Car modulo conserve les valeurs n�gatives
	}
	/** Test si la case sur laquelle se trouve le robot a d�j� �t� explor�e
	 * @return Vrai si la case poss�de une couleur */
	public boolean isAlreadyExplored(){
		return (map[posY][posX] != Color.NONE);
	}

	/** Test si le robot est en dehors de la carte (physique sur la derni�re case)
	 * @return Vrai si le robot est sorti */
	public boolean isOutOfMap(){
		return (posY < 0 || posX < 0 || posY > map.length-1 || posX > map[0].length-1);
	}

	/** Affiche la carte sur l'afficheur LCD */
	public void displayMap(){
		LCD.clear();
		String toDisplay;

		for(int l=0; l<map.length; l++){
			toDisplay = "";
			for(int c=0; c<map[l].length; c++){
				toDisplay += (environnement.get(map[l][c])).charAt(0) + " | ";
			}
			toDisplay = toDisplay.substring(0, toDisplay.length()-2);
			LCD.drawString(toDisplay, 0, l);
		}
		LCD.refresh();
	}

	/** Acc�de � la largeur de l'environnement
	 * @return Largeur de la carte */
	public int getMapLength() {
		return map[0].length;
	}

	/** Acc�de � la hauteur de l'environnement
	 * @return Hauteur de la carte */
	public int getMapHeight() {
		return map.length;
	}
	
	public void explorer(){
		/* On suppose que le robot d�marre � sa position de d�part (0, 6) */
		
		int mapHeight = this.getMapHeight();
		
		for(int c=0; c<this.getMapLength(); c++){
			//Parcours de la colonne puis d�calage sur la suivante
			if(c%2 == 0){ //Vers le haut
				this.voyager(c+1, 0);
				if(c != this.getMapLength()-1){
					//Quart-de-tour vers la prochaine colonne sauf si derni�re colonne
					quartDeTourDroit();
				}
			} else { //Vers le bas
				this.voyager(c+1, mapHeight-1);
				quartDeTourGauche();
			}
		}
		
		//Pause puis voyage vers une ville une fois exploration termin�e
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.letsGoToTheCity();
	}
	
	/** Fait se d�placer le robot de sa position actuelle � la position demand�e
	 * @param xDest Abscisse de la position de destination */
	public void voyager(int xDest, int yDest){
		//System.out.println(xDest + " " + yDest + " " + posX + " " + posY + "\n" + direction);
		
		//Recherche de la position de la destination par rapport � la position actuelle
		int xDir = -1;
		int yDir = -1;
		
		if(xDest - posX < 0){
			xDir = VERS_LA_GAUCHE;
		} else if(xDest - posX > 0){
			xDir = VERS_LA_DROITE;
		}
		
		if(yDest - posY < 0){
			yDir = VERS_LE_HAUT;
		} else if(yDest - posY > 0){
			yDir = VERS_LE_BAS;
		}
		
		//Recherche du c�t� vers lequel faire le quart-de-tour entre les 2 d�placements
		boolean quartDroit = false;
		boolean quartGauche = false;
		if(direction == VERS_LA_GAUCHE || direction == VERS_LA_DROITE){
			if((xDir == VERS_LA_DROITE && yDir == VERS_LE_HAUT) || (xDir == VERS_LA_GAUCHE && yDir == VERS_LE_BAS)){
				quartGauche = true;
			} else {
				quartDroit = true;
			}
		} else { //VERS_LA_HAUT || VERS_LA_BAS
			if((xDir == VERS_LA_DROITE && yDir == VERS_LE_HAUT) || (xDir == VERS_LA_GAUCHE && yDir == VERS_LE_BAS)){
				quartDroit = true;
			} else {
				quartGauche = true;
			}
		}
		
		//D�placement en 2 temps
		for(int i=0; i<2; i++){
			if(direction == VERS_LE_HAUT || direction == VERS_LE_BAS){
				if(posY != yDest){
					if(yDir != direction){
						demiTour();
					}
					
					//Avancer jusqu'� posY = yDest
					bDriveForward.action();
					while(posY != yDest){
						//TODO trouver un autre moyen de savoir quand arr�ter le robot
					}
					bDriveForward.suppress();
					avancer(4.);
				}
			} else { //VERS_LA_DROITE || VERS_LA_GAUCHE
				if(posX != xDest){
					if(xDir != direction){
						demiTour();
					}
					
					//Avancer jusqu'� posX = xDest
					bDriveForward.action();
					while(posX != xDest){
						//TODO trouver un autre moyen de savoir quand arr�ter le robot
					}
					bDriveForward.suppress();
					avancer(4.);
				}
			}
			
			//Quart-de-tour entre les deux d�placements
			if(i == 0){
				if(quartDroit){
					quartDeTourDroit();
				} else if(quartGauche) {
					quartDeTourGauche();
				}
			}
		}
	}
	
	/** Emm�ne le robot � la ville la plus proche */
	public void letsGoToTheCity(){
		//Recherche des villes
		ArrayList<Integer[]> villes = new ArrayList<Integer[]>();
		for(int l=0; l<map.length; l++){
			for(int c=0; c<map[0].length; c++){
				if(environnement.get(map[l][c]) == VILLE){
					villes.add(new Integer[]{l,c});
				}
			}
		}
		
		//Cherche la ville la plus proche
		int distanceMin = Integer.MAX_VALUE;
		int indexVilleMin = -1;
		for(int i=0; i<villes.size(); i++){
			int distance = Math.abs(villes.get(i)[0] - posY) + Math.abs(villes.get(i)[1] - posX);
			if(distance < distanceMin){
				indexVilleMin = i;
				distanceMin = distance;
			}
		}
		
		//D�placement vers la ville la plus proche
		voyager(villes.get(indexVilleMin)[1], villes.get(indexVilleMin)[0]);
	}
	
	/** Fait avancer le robot du nombre de cm donn� param�tre
	 * @param cm Longueur sur laquelle faire avancer le robot */
	public void avancer(double cm){
		pilot.travel(cm);
	}
	
	/** Faire faire au robot un demi-tour */
	public void demiTour(){
		//Faire demi-tour
		demandeDemiTour = true;
		//Attend fin du demi-tour
		try {
			Thread.sleep(14000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** Accesseur de demandeDemiTour
	 * @return Vrai si la robot doit faire demi-tour */
	public boolean isRequestingForAboutTurn() {
		return demandeDemiTour;
	}
	
	/** Arr�te la demande de demi-tour */
	public void stopDemandeDemiTour(){
		demandeDemiTour = false;
	}
	
	/** Faire faire au robot un quart-de-tour � droite */
	public void quartDeTourDroit(){
		//Faire quart-tour
		demandeQuartDeTourDroit = true;
		//Attend fin du quart-de-tour
		try {
			Thread.sleep(7000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** Accesseur de demandeQuartDeTourDroit
	 * @return Vrai si la robot doit faire un quart-de-tour � droite */
	public boolean isRequestingForTurnRight() {
		return demandeQuartDeTourDroit;
	}
	
	/** Arr�te la demande de quart-de-tour � droite */
	public void stopDemandeQuartDeTourDroit(){
		demandeQuartDeTourDroit = false;
	}
	
	/** Faire faire au robot un quart-de-tour � gauche */
	public void quartDeTourGauche(){
		//Faire quart-tour
		demandeQuartDeTourGauche = true;
		//Attend fin du quart-de-tour
		try {
			Thread.sleep(7000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** Accesseur de demandeQuartDeTourDroit
	 * @return Vrai si la robot doit faire un quart-de-tour � droite */
	public boolean isRequestingForTurnLeft() {
		return demandeQuartDeTourGauche;
	}
	
	/** Arr�te la demande de quart-de-tour � droite */
	public void stopDemandeQuartDeTourGauche(){
		demandeQuartDeTourGauche = false;
	}

	/** Ajoute un comportement d'avancement au robot */
	public void addDriveForwardBehavior(Behavior bDriveForward) {
		this.bDriveForward = bDriveForward;
	}
	
	/** Accesseur de la position sur x (abscisse) du robot
	 * @return Abscisse de la position du robot */
	public int getPosX(){
		return this.posX;
	}

	/** Accesseur de la position sur y (ordonn�e) du robot
	 *  @return Ordonn�e de la position du robot */
	public int getPosY(){
		return this.posY;
	}
	
}
