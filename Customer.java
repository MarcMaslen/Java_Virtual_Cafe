
/*-----------------------------|
*       Helpers.Client - Customer      |
*-----------------------------*/

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

 /*---------------------------------------
            Customer - Client
  ---------------------------------------*/

public class Customer implements AutoCloseable {

    final int port = 8888; //this outlines the port number for the client
    private Scanner customerReader; // scanner for user input
    private PrintWriter writer;


    //sets up the socket and reader / writer to the server
    public Customer(String customerName) throws Exception{
        Socket socket = new Socket("localhost", port); // connects to server
        customerReader = new Scanner(socket.getInputStream()); //getting input stream
        writer = new PrintWriter(socket.getOutputStream(), true); // print line function with auto flush
        writer.println(customerName); //reads customer name to barista
    }

    public int getCoffeeOrTea(String coffee, int coffeeAmount, String tea, int teaAmount){ //gets the orders
        writer.println("waiting_area " + coffee + " " + coffeeAmount + " " + tea + " " + teaAmount); //sends message with the order and amount
        String line = customerReader.nextLine(); //reads next line from server
        return Integer.parseInt(line);
    }

    public int getStatus(String customerName, int id){ //get the status of the order
        writer.println("brewing_area " + customerName + " " + id); //sends to brewing area and confirms customerName, order and amount
        String status = customerReader.nextLine(); //reads next line from server
        return Integer.parseInt(status); //returns int sent from server
    }

    public int getTray( String customerName ,int id){//Get the order amount from the customer
        writer.println("tray_area " + customerName + " " + id); //goes to tray area and confirms name
        String tray = customerReader.nextLine(); //reads next line from server
        return Integer.parseInt(tray);
    }


    public void exit(){ //Exits the cafe
        writer.println("exit"); //this just sends the message back to server to remove customer from list.
        System.exit(1);//exits client
    }

    public int orderNumber(){ //gets order number
        String id = customerReader.nextLine();// gets customer id from server
        return Integer.parseInt(id);
    }


    @Override
    public void close() throws Exception { //closes down the customer reader and writer
        customerReader.close();
        writer.close();
    }



    /*---------------------------------------
            Main method - Client Program
     ---------------------------------------*/

    public static void main(String[] args) {
        System.out.print("Hey, please enter your name: "); //asks for name of customer
        Runtime.getRuntime().addShutdownHook(new Thread()); //Handles SIGTERM signals (closes application with ctrl + c)
        try{
            Scanner in = new Scanner(System.in); //scanner to prepare for server messages
            String customer = in.nextLine(); //takes in customer name
            try (Customer client = new Customer(customer)) {
                System.out.println("--------------------------------------------------------");
                System.out.println("||   Welcome the the Virtual Cafe - " + customer + ", customer number - " + client.orderNumber() + "  ||"); //welcome message

                System.out.println(" "); //breaks text up
                //gives the customer some options to choose from
                System.out.println("What can we do for you today, pick one of the following options\n" +
                        "Order a coffee and tea - Example 'order 2 coffee and 2 tea' (Make sure to write it in this order and include the 'and' between the drinks. if you don't want a drink write it in but put 0 amount) \n" +
                        "Check the status of your order - by asking 'status name id'. Example 'status marc 1' \n" +
                        "Get your Tray - type 'tray name'. example 'tray marc'  " +
                        "leave the shop by typing 'exit' ");
                System.out.println("--------------------------------------------------------\n");
                while(true) {
                    System.out.print("Enter Choice: ");
                    String choice = in.nextLine().toUpperCase(); //takes in text from customer
                    String[] subStrings = choice.split(" "); //splits them into an array
                    switch(subStrings[0]) { //using first string of array it chooses the switch case

                        case "ORDER": //allows customer to order
                            try {
                                int Amount1 = Integer.parseInt(subStrings[1]); //reads second string of array
                                String drink1 = subStrings[2]; // reads third string of array
                                int Amount2 = Integer.parseInt(subStrings[4]);
                                String drink2 = subStrings[5];

                                if (drink1.equalsIgnoreCase("coffee") && drink2.equalsIgnoreCase("tea")) { //checks that they only order coffee or tea
                                    System.out.println("\nOrder received for " + customer +  " ( " + Amount1 + " " + drink1.toLowerCase() + " and " + Amount2 + " " + drink2.toLowerCase() +" )\n" + //confirms order
                                            "Just making that now for you, this might take a couple minutes. You can check your progress with the Barista"); //lets customer know it might make a couple of minutes
                                    int i = client.getCoffeeOrTea(drink1, Amount1, drink2, Amount2); //calls order function so Barista can make drinks and returns int

                                    if (i == 1) { //if order returns 1 then it is complete
                                        System.out.println("Order has been submitted, Feel free to check the status of your drink to check when its done\n");
                                    } else if ( i == 2) {
                                        System.out.println("That isn't a valid order, please try again\n");
                                    }
                                } else {
                                    System.out.println("It appears the order was entered incorrectly, please use the format 'order 2 coffee and 2 tea'\n");
                                }


                            } catch (Exception e){ //in case what they entered is incorrect
                                System.out.println("Please specify what drink and the amount needed, for example 'order 2 coffee and 2 tea'.\n");
                            }
                            break;// breaks out of case


                        case "STATUS": //Check's the status of the order
                            String Name = subStrings[1].toLowerCase(); // gets the second string of thr array
                            int id = Integer.parseInt(subStrings[2]);
                            System.out.println("\nI will check your status now for you.");
                            int status = client.getStatus(Name, id); //get status of order and returns int
                            if (status == 1){ //if order is ready
                                System.out.println("\nOrder status for " + Name + ": Your drinks are ready for collection at the tray area (e.g. tray marc 1)");
                            } else if (status == 2){ //if order is not ready yet
                                System.out.println("\nOrder status for " + Name + ": Your order is not ready yet");
                            } else if (status == 3){ //if no order has been placed
                                System.out.println("\nNo order found for " + Name);
                            }
                            break; //breaks out of case


                        case "TRAY": //allows customer to get drinks
                            String customerName = subStrings[1].toLowerCase(); //confirms name so they get the right drink
                            int customerid = Integer.parseInt(subStrings[2]); //confirms ID
                            int i = client.getTray(customerName ,customerid); //calls getTray
                            if (i == 1){ //if order is ready and gets delivered
                                System.out.println("\nOrder delivered to " + customerName );
                                System.out.println("Enjoy your drinks and have a nice day!");
                                client.exit(); //exits client
                            } else if (i == 2){ //if not ready yet
                                System.out.println("\norder not ready yet!");
                            } else if (i == 3 ) { //if no order has been placed
                                System.out.println("\nNo order has been found for " + customerName + "\n");
                            } else { //if input incorrect
                                System.out.println("You entered that in incorrect, make sure you type it correctly.");
                            }
                            break;

                        case "EXIT": //Exits The cafe for the user.
                            System.out.println("Oh you need to head off? Not a problem, have a nice day!");
                            client.exit(); //exits client

                        default:
                            System.out.println("\nSorry you entered an incorrect command, can you please choose one of the follow options as it is shown.");
                            break;
                        }
                    }

                }

             } catch (Exception exception) {
            System.out.println(exception.getMessage()); //Catching any exceptions made while it runs
        }
    }
}
