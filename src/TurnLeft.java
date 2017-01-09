//Fichier : TurnLeft.java
//Auteurs : Gutierrez Cyrian - Magnin Gauthier - Mounier Baptiste
//Contexte : TSCR - Projet robotique - M1 SCA

import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;

/** Comportement de quart-de-tour vers la gauche */
public class TurnLeft implements Behavior {
	/** Robot */
	private Robot robot;

	/** Constructeur principal du comportement
	 * @param robot Robot */
	public TurnLeft(Robot robot){
		this.robot = robot;
	}

	@Override
	public boolean takeControl() {
		return (Button.LEFT.isDown());
	}

	@Override
	public void action() {
		Motor.B.stop(true);
		Motor.C.stop(true);
		
		DifferentialPilot pilot = new DifferentialPilot(5.6, 12.0, Motor.B, Motor.C);
			//Diam�tre des roues, espace entre les roues, moteur gauche, moteur droit
		
		pilot.setTravelSpeed(5.); //En cm
		pilot.setRotateSpeed(30.); //Degr�s/sec
		pilot.travel(2.); //Avance de 2cm (pour bien rester call� au centre de la case apr�s rotation)
		pilot.rotate(80.); //En degr� dans le sens inverse des aiguilles d'une montre
		//Demander rotation de 80� effectue une rotation de 90�
		pilot.travel(-13.); //Avance de 13cm = 1,5 case + 1 limite entre deux cases
		
		//Modification de l'orientation
		robot.updateDirection(Robot.ROTATION_GAUCHE);
	}

	@Override
	public void suppress() {
		//Rien � faire en cas d'arr�t du comportement
	}

}