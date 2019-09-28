package ca.mcgill.ecse211.lab3;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import ca.mcgill.ecse211.lab3.Odometer;



public class Navigation extends Thread {
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;
  private EV3UltrasonicSensor ultraSensor;
  private EV3MediumRegulatedMotor sensorMotor;



  private double track;
  private double radius;
  private double Theta;			
  private double deltaTheta;	
  private double distance;	
  private boolean isNavigating;
  private double deltaX;	
  private double deltaY;	
  private boolean isObstacle;	
  

	
  private static final int FORWARD_SPEED = 100; 
  private static final int ROTATE_SPEED = 100;
	


  
  private Odometer odometer;  

 
  public Navigation (EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, 
      EV3UltrasonicSensor ultraSensor, EV3MediumRegulatedMotor sensorMotor) {
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
    this.ultraSensor = ultraSensor;
    this.sensorMotor = sensorMotor;
    try {
      this.odometer = Odometer.getOdometer();
    } catch (OdometerExceptions e) {
      e.printStackTrace();
    }

  }   

  
  public void travelTo (double x, double y) {

    isNavigating = true;  

    
    deltaX = x - odometer.getX();
    deltaY = y - odometer.getY();
    distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

   
    if (deltaY == 0) 
    {
      if (deltaX >= 0) {
        Theta = 90;
      }
      else {
        Theta = -90;
      }
    }
    else 
    {
     
      Theta = Math.atan2(deltaX, deltaY) * 180 / Math.PI; 
    }
    
    deltaTheta = Theta - odometer.getTheta();	
    if (deltaY < 0) 
      Theta = Theta + 180;
      

    if (Math.abs(deltaTheta) > 180) 
    { 
    	if(deltaTheta>0)
    		Theta = Theta - 360;
    	else
    		Theta = Theta + 360;
    }
   
    
    turnTo(Theta);

  
 
  public void turnTo (double Theta) {

    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);

 
    if (deltaTheta > 0 && deltaTheta <= 180) {     
      leftMotor.rotate(convertAngle(radius, track, deltaTheta), true);
      rightMotor.rotate(-convertAngle(radius, track, deltaTheta), false);
    }
 
    else if (deltaTheta < 0 && deltaTheta >= -180){    
      deltaTheta = Math.abs(deltaTheta);
      leftMotor.rotate(-convertAngle(radius, track, deltaTheta), true);
      rightMotor.rotate(convertAngle(radius, track, deltaTheta), false);      
    } 
  }

  
  
  public boolean isNavigating() {
    return isNavigating; 
  }

  
  private static int convertDistance(double radius, double distance) {
    return (int) ((180.0 * distance) / (Math.PI * radius));
  }

 
  private static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }


}



