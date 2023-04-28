package Helpers;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private Socket socket; //protected socket for connecting to Customer
    private Helpers.Kitchen kitchen;//allows use of kitchen
    public static int teaWaiting, coffeeWaiting, teaBrewing, coffeeBrewing, teaTray, coffeeTray; //each drinks for each area


    public ClientHandler(Socket socket, Kitchen kitchen) {
        this.kitchen = kitchen;
        this.socket = socket;
    }

    public void serverUpdate(){
        System.out.println("\n --------------------- ");
        System.out.println("SERVER UPDATE:"); //This is a server update that will keep records of useful info for the Barista
        getCustomerList(); //gets customers in cafe
        ordersInArea(); //orders in each area funtion
        listOfOrders(); //get list of orders
        System.out.println(" -------------------- \n ");
    }

    public void ordersInArea(){ //prints each drink in each area
        System.out.println(teaWaiting + " teas and " + coffeeWaiting + " coffee in waiting area"); //prints drinks in waiting area
        System.out.println(teaBrewing + " teas and " + coffeeBrewing + " coffee in brewing area"); //prints drinks in brewing area
        System.out.println(teaTray + " teas and " + coffeeTray + " coffee in brewing area"); //prints drinks in brewing area
    }

    public void getCustomerList(){ // we can get the amount of customers + a list of all the names
        List<String> customersInCafe = kitchen.customersList(); //gets the customers in the cafe

        System.out.println("In the cafe there are " + customersInCafe.size() + " customer's."); //prints number of customers

        System.out.println("Here are the names of each customer: ");
        if (customersInCafe.size() > 0){ //makes sure we have a number of customer greater than 0
            for (String customerName : customersInCafe) {  //for all customer names in cafe
                System.out.println("Customer: " + customerName); //print the customer name
            }
        }
    }


    public void listOfOrders(){ //prints the list of orders to complete
        int numOfOrder = kitchen.numOfOrders; //uses the variable numOfOrders from kitchen
        System.out.println("There are currently " + numOfOrder + " orders waiting to be made");
    }

    @Override
    public void run() {
        String customerName = null; //sets the customerName to null to start
        try (
             Scanner scanner = new Scanner(socket.getInputStream()); //declares scanner
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) { //print writer to send back to client
                try {
                    customerName = scanner.nextLine().toLowerCase(); //first scanner is for customer name
                    System.out.println("New customer " + customerName); // prints the customer name to the server
                    kitchen.CustomerID(); //called new ID for new customer

                    kitchen.CustomerEntered(customerName, Kitchen.customersID ); //add new customer to the customers tree
                    writer.println(Kitchen.customersID); //sends customerID to customer client
                    while (true) {

                        serverUpdate(); //prints server update

                        String choice = scanner.nextLine(); //scans in choice
                        String[] subStrings = choice.split(" "); //splits them into an array
                        switch (subStrings[0].toLowerCase()) { //uses first substring in array to pick which case

                            case "waiting_area": //set up ordering the drinks
                                String coffee = subStrings[1].toLowerCase(); //second string will be order (coffee or tea)
                                int coffeeAmount = Integer.parseInt(subStrings[2]);// third string will be order amount
                                String tea = subStrings[3].toLowerCase();
                                int teaAmount = Integer.parseInt(subStrings[4]);
                                teaWaiting += teaAmount; coffeeWaiting += coffeeAmount; //
                                System.out.println("Order received for " + customerName + " ( " + coffeeAmount + " " + coffee.toLowerCase() + " and " + teaAmount + " " + tea.toLowerCase()+ " )"); //confirms to the customer they get what they ordered
                                boolean i = kitchen.WaitingArea(customerName, coffee, coffeeAmount, tea, teaAmount); //this calls the waiting area and brewing function. Makes the coffees and teas

                                if (i == true){ //if true is called then pass
                                    writer.println(1); //send back to client
                                } else if( i == false){
                                    writer.println(2);
                                } else { //if true is not called error
                                    throw new Exception("ERROR order did not go through");
                                }
                                break;// break out of case


                            case "brewing_area"://get status of customers order
                                String name = subStrings[1].toLowerCase(); //second string will be customers name
                                int id = Integer.parseInt(subStrings[2]);
                                int status = kitchen.status(name, id);//gets the status of the order to see if coffees / teas are prepared
                                if (status == 1){ //if enough coffees then send message
                                    teaBrewing -= 2; teaTray += 2;
                                    coffeeBrewing -= 2; coffeeTray += 2;
                                    writer.println(1);
                                } else if (status == 2){ //if enough teas then send message
                                    writer.println(2);
                                } else if (status == 4) {
                                    writer.println(3);
                                 } else { //else error not enough coffees or teas
                                    writer.println(0);
                                }
                                break;


                            case "tray_area":
                                String customer = subStrings[1].toLowerCase(); //second string will be customers name
                                int customerID = Integer.parseInt(subStrings[2]);
                                int orderReady = kitchen.TrayArea(customer , customerID); //gives customer there coffees / teas
                                if (orderReady == 1){ // give coffees then send message
                                    System.out.println("\nOrder delivered to " + customerName );
                                    writer.println(1);
                                }  else if (orderReady == 2){
                                    writer.println(2);
                                } else if (orderReady == 4) {
                                    writer.println(3);
                                } else { writer.println(0);
                                }
                                break;



                            case "exit": //exit
                                kitchen.numOfOrders--;
                                kitchen.removeCustomers(customerName);

                            default:
                                throw new Exception("\nUnknown command: " + subStrings[0] + " please use a command from the list.");
                            }
                        }
                } catch (Exception e) {
                    writer.println("Error: " + e.getMessage()); //sends error to customer
                    socket.close();
                }
           } catch (Exception e) {
            e.getMessage();
        }
        finally {
            System.out.println("Customer " + customerName + " has left the cafe.");
            kitchen.removeCustomers(customerName);
        }
    }
}
