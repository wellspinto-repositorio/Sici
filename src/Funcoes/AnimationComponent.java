/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

/*
* Slide Animation Component Class
* Created by: Romar J. Ybanez a.k.a mensahero
*
* To create an instance of this class:
*               AnimationComponent dog = new AnimationComponent("dogWalking", 3)
*        note:
*               your images must be .gif format and the name should be:
*                          dogWalking1.gif, dogWalking2.gif, etc.. number should start with 1
*               when you use it remove the "1.gif" only use "dogWalking".. like the above example..
*
* To load a non animated image like background images:
*                AnimationComponent bg = new AnimationComponent("bg.gif")
*
*
* to Start the animation:
*               dog.start(0,2,2) //by setting the forceStop to 0 it animation will not stop on its own.
*                                //slide 0 is equivalent to "dogWalking1.gif" : take note of the number..
*                                //Slide 1 is placed on the endingSlide.. so the animation will start at 0 and end at 1
*                                //if you set the forceStop to 2 it will stop at slide 2.
* to stop the animation:
*               dog.stop()       // this will stop the animation.
*
* others:
*               dog.isRunning    //will return true if the animation is running.. vice versa..
*                                //for synchronized animation..
*
* NOTE:  If you know how to add JButtons, JLabels.. then adding this component is actually the same..
*
* You may develop or alter this code in any way.. if you indeed use it..
* you may or may not give me credit.. its your call.. lmao..
* you can even claim this as your own if that will make you happy.. lol..
*
* for further inquiries:
* email: hinahangaan@yahoo.com
* yahooMessenger: hinahangaan
* D.I.C account: mensahero   << you can pm me here.. its better..
*/




import java.net.URL;
import javax.swing.*;

public class AnimationComponent extends JComponent
            implements Runnable
    {

        public static int imageLoadCurrentCount = 0;  //For progress bar purposes.

        JLabel imageLabel;                            //Label for holding images
        int currentSlide;                             //Current Slide number
        int endSlide;                                 //End Slide number
        int forceStopSlide;                           //Force Animation to stop at a certain Slide
        Boolean isRunning;                            //To check if an instance is running.
        ImageIcon[] img;                              //Array of ImageIcons
        Thread t;                                     //Thread that handles animation

        public AnimationComponent(){}                 //No Parameter Constractor/ for Progress Bar purposes

        public AnimationComponent(String imgDir){     //Single Parameter for Non-animated Images/ i.e background.jpg

               imageLabel = new JLabel();
               img = new ImageIcon[1];
               img[0] = createImageIcon(imgDir);

               setLayout(null);
               imageLabel.setBounds(0,0,img[currentSlide].getIconWidth(),img[currentSlide].getIconHeight());
               this.add( imageLabel );
               System.out.println(imgDir);
        }


        public AnimationComponent(String imgDir, int numSlides){     //Double Param Constructor for Animated Slides..

                imageLabel = new JLabel();
                isRunning = false;
                forceStopSlide = 0;
                currentSlide = 0;
                endSlide = 0;
                img = new ImageIcon[numSlides];

                for(int i = 1; i <= numSlides ; i++){
                      System.out.println(imgDir + i + ".gif");
                      img[i - 1] = createImageIcon(imgDir + i + ".gif");
                      imageLoadCurrentCount++;
                  }


                setLayout(null);
                imageLabel.setBounds(0,0,img[currentSlide].getIconWidth(),img[currentSlide].getIconHeight());
                this.add( imageLabel );

        }


      public void run(){

          try {
            while(isRunning){
              animation();
              forceStop(currentSlide);
              Thread.sleep(150);
            }
          } catch (InterruptedException e) {}

      }

      public void start(int cs, int es, int fs ) {             // currentSlide, endingSlide, forceStopSlide
                    currentSlide = cs; endSlide = es; forceStopSlide = fs;
                    if ( !isRunning )
                    {
                        isRunning = true;
                        t = new Thread(this);
                        t.start(  );
                    }
                }

       public void stop(  ) {

               t.interrupt(  );
               isRunning = false;
         }

        public void forceStop(int slide) {

            if(forceStopSlide != 0){
                if(slide == forceStopSlide){
                   stop();
                }
            }

        }

       synchronized private void animation(){
             currentSlide = (currentSlide < endSlide) ? currentSlide + 1: 0;
             imageLabel.setIcon(img[currentSlide]);
        }

       protected ImageIcon createImageIcon(String path) {

            ImageIcon tempImg;
            URL imgURL = getClass().getResource(path);

            tempImg = (imgURL != null) ? new ImageIcon(imgURL): null;
            return tempImg;
        }


        public int getImageLoadedCount(){
           return  imageLoadCurrentCount;
        }

    }
