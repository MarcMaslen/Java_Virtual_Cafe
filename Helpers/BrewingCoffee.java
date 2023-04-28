package Helpers;

import static Helpers.Kitchen.coffees;

public class BrewingCoffee extends Thread{

    public void run() { //make a new thread to make the coffee
        for (int i = 0; i < Kitchen.orderedCoffees; i++) { //loops through coffee amount
            System.out.println("Brewing 2 coffees now..."); //displays message to server to show brewing in progress
            try {
                Thread.sleep(45000); //waits 45 seconds for each 2 coffees
                ClientHandler.coffeeWaiting -= 2;
                ClientHandler.coffeeBrewing += 2;
                coffees += 2; //adds two coffees ready to serve
                System.out.println("\nCurrently have: " + coffees + " coffees"); //displays amount of coffees
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
