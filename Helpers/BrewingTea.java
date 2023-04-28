package Helpers;

import static Helpers.Kitchen.teas;

public class BrewingTea extends Thread{

    public void run() { //make a new thread to make the teas
        for (int i = 0; i < Kitchen.orderedTeas; i++) { //loops through tea amount
            System.out.println("Brewing 2 teas now..."); //displays message to server that coffees are being brewed
            try {
                Thread.sleep(30000); //takes 30 seconds per 2 teas
                ClientHandler.teaWaiting -= 2;
                ClientHandler.teaBrewing += 2;
                teas += 2; //add two teas ready to be served
                System.out.println("\nCurrently have: " + teas + " teas"); //displays current number of teas available
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
