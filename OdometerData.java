package ca.mcgill.ecse211.lab3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * OdometerData class from lab 2
 * 
 */

public class OdometerData {


  private volatile double x; 
  private volatile double y; 
  private volatile double theta; 


  private volatile static int numberOfIntances = 0; 


  private static final int MAX_INSTANCES = 1; 


  private static Lock lock = new ReentrantLock(true);

  private volatile boolean isReseting = false; 
  
 
  private Condition doneReseting = lock.newCondition();
  
  


  private static OdometerData odoData = null;

 
  protected OdometerData() {
    this.x = 0;
    this.y = 0;
    this.theta = 0;
  }

  
  public synchronized static OdometerData getOdometerData() throws OdometerExceptions {
    if (odoData != null) { 
      return odoData;
    } else if (numberOfIntances < MAX_INSTANCES) { 
 
      odoData = new OdometerData();
      numberOfIntances += 1;
      return odoData;
    } else {
      throw new OdometerExceptions("Only one intance of the Odometer can be created.");
    }

  }

  
  public double[] getXYT() {
    double[] position = new double[3];
    lock.lock();
    try {
      while (isReseting) { 
        doneReseting.await(); 
      }

      position[0] = x;
      position[1] = y;
      position[2] = theta;

    } catch (InterruptedException e) {
     
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

    return position;

  }

  
  public void update(double dx, double dy, double dtheta) {
    lock.lock();
    isReseting = true;
    try {
      x += dx;
      y += dy;
      theta = (theta + (360 + dtheta) % 360) % 360;
      isReseting = false; 
      doneReseting.signalAll();
    } finally {
      lock.unlock();
    }

  }

 
  public void setXYT(double x, double y, double theta) {
    lock.lock();
    isReseting = true;
    try {
      this.x = x;
      this.y = y;
      this.theta = theta;
      isReseting = false;
      doneReseting.signalAll(); 
    } finally {
      lock.unlock();
    }
  }

  
  public void setX(double x) {
    lock.lock();
    isReseting = true;
    try {
      this.x = x;
      isReseting = false; 
      doneReseting.signalAll(); 
    } finally {
      lock.unlock();
    }
  }

 
  public void setY(double y) {
    lock.lock();
    isReseting = true;
    try {
      this.y = y;
      isReseting = false;
      doneReseting.signalAll(); 
    } finally {
      lock.unlock();
    }
  }

 
  public void setTheta(double theta) {
    lock.lock();
    isReseting = true;
    try {
      this.theta = theta;
      isReseting = false;
      doneReseting.signalAll();
    } finally {
      lock.unlock();
    }
  }


 

  public double getTheta() {
    return theta;
  }

 
  public double getX() {
    return x;
  }

 
  public double getY() {
    return y;
  }




}
