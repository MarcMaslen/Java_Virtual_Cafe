package Helpers;

import java.util.*;

public class Kitchen extends Thread {
    private final Map<String, Order> customers = new TreeMap<>(); //Customer Map Tree
    int numOfOrders = 0; //stores the number of active orders
    static int coffees = 0; // holds current amount of coffee brewed
    static int teas = 0; //holds current amount of teas brewed
    static float orderedCoffees; //gets amount of ordered coffee to send to thread
    static float orderedTeas; //gets amount of ordered coffee to send to thread
    static int customersID = 0; //unique identifier
    boolean idling; //idling is true unless order is placed

    public void CustomerID(){ //sets the customer unique ID
        customersID++; //each new customer it increments
    }


    public void CustomerEntered(String customerName, int customersID) { //This is to store customers Names
        Order order = new Order(customerName, customersID ); //stores customer name and ID
        customers.put(customerName, order); //stores customer name in TreeMap
        idling = true;
    }


    public void removeCustomers(String customerName) { //Allows us to remove customers when they leave the cafe
        customers.remove(customerName); //removes customer from treemap
    }


    public List<String> customersList() { //returns list of current customers
        List<String> result = new ArrayList<>(); //sets up an ArrayList

        for (Order newCustomer : customers.values()) { //for all new customers
            result.add(newCustomer.getCustomerName()); //add result to ArrayList
        }
        return result; //returns list of customers
    }


    public boolean WaitingArea(String CustomerName, String coffee, int coffeeAmount, String tea, int teaAmount) throws Exception { //this creates the orders
        synchronized (this) { //synchronized

            if (!coffee.equalsIgnoreCase("coffee") || !tea.equalsIgnoreCase("tea")) { //checks to make sure only coffee and tea was ordered
                return false;
            }

            if (customers.get(CustomerName).getCoffee() == null && customers.get(CustomerName).getTea() == null) { //checks to see if they placed an order or not as I don't want to add more orders if they already have one
                numOfOrders++; //increments number of orders
            }

            if (customers.get(CustomerName).getCoffee() != null && customers.get(CustomerName).getTea() != null) { //checks to see if the customer has an order, if not add drinks to order rather than make new one
                customers.get(CustomerName).addToOrder(coffeeAmount, teaAmount); //adds drinks to order
                System.out.println("\n Order Updated: " + coffee + " " + customers.get(CustomerName).getCoffeeOrderAmount() + " and " + tea + " " + customers.get(CustomerName).getTeaOrderAmount()); //displays new order
            }

            idling = false; //order placed so no longer idling
            customers.get(CustomerName).setOrder(coffee, coffeeAmount, tea, teaAmount); //with a specific customer name set order

            if (teaAmount <= teas && coffeeAmount <= coffees){ //if customer makes an order, and we already have the drinks made due to a previous customer leaving, use them instead.
                System.out.println(coffeeAmount + " coffee and " + teaAmount + " tea are already made and ready for " + CustomerName); //prints that message
                return true;
            }

            float coffeesToFullfil = (float) coffeeAmount / 2; //as two drinks can be made at one time, divide number of drinks by 2.
            Math.ceil(coffeesToFullfil); // rounds up drinks in case of odd amount, if customer orders 3 we can make 4 and have one for next customer.

            float teasToFullfil = (float) teaAmount / 2;
            Math.ceil(teasToFullfil);

            orderedCoffees = coffeesToFullfil; //sets the amount of ordered coffees needed for the thread loops
            orderedTeas = teasToFullfil;//sets the amount of ordered tea needed for the thread loops

            BrewingCoffee brewingCoffee = new BrewingCoffee(); //defines the coffee thread
            BrewingTea brewingtea = new BrewingTea();//defines the tea thread

            brewingCoffee.start(); //starts thread
            brewingtea.start(); //starts thread

            return true;
        }
    }

    public int status(String customerName, int id) throws Exception {

        if (customers.get(customerName).getCustomerID() != id){ //checks to see if customer name and id match
            throw new Exception ("customer name and order number do not match");
        }

        if (idling == true) { //checks to see if they placed an order or not
            System.out.println("No order found for " + customerName); //if idling and ask for status display message
            return 4;
        }

        String coffee = customers.get(customerName).getCoffee(); //gets customer order against the name.
        String tea = customers.get(customerName).getCoffee(); //gets customer order against the name.
        int coffeeOrderAmount = customers.get(customerName).getCoffeeOrderAmount(); //gets customer order amount against the name
        int teaOrderAmount = customers.get(customerName).getTeaOrderAmount(); //gets customer order amount against the name

        if (teaOrderAmount <= teas && coffeeOrderAmount <= coffees){ //if customer ordered coffee, and we have less than or equal to the amount brewed
            System.out.println("\n Order status for " + customerName + " :");
            System.out.println(" - " + coffeeOrderAmount + " " + coffee + " " + teaOrderAmount + " " + tea + " in tray"); //print message
            return 1;
        } else if (!(teaOrderAmount <= teas) && !(coffeeOrderAmount <= coffees)) { //if number of ordered drinks arnt prepared drink not ready
            System.out.println("\nOrder not ready yet, please wait");
            return 2;
        }
        return 0;
    }


    public int TrayArea(String customerName, int id) throws Exception { //get status of order

        if (customers.get(customerName).getCustomerID() != id) { //checks id and name are correct
            throw new Exception("customer name and order number do not match our records");
        }

        if (idling == true) { //checks to see if they placed an order or not
            System.out.println("No order found for " + customerName);
            return 4;
        }

        int coffeeOrderAmount = customers.get(customerName).getCoffeeOrderAmount(); //gets customer order amount against the name
        int teaOrderAmount = customers.get(customerName).getTeaOrderAmount(); //gets customer order amount against the name

        if (coffeeOrderAmount <= coffees && teaOrderAmount <= teas) {//if customer ordered coffee, and we have less than or equal to the amount brewed
            numOfOrders--; //order complete so get rid of it.
            teas -= teaOrderAmount; //subtracts amount from tea
            coffees -= coffeeOrderAmount; //subtracts amount from coffee
            System.out.println("\n" + customerName + " Has taken there order, there is now " + coffees + " coffees prepared and " + teas + " teas prepared"); //displays how many coffees left
            return 1;

        } else if (!(teaOrderAmount <= teas) && !(coffeeOrderAmount <= coffees)) { //if drinks ordered dont match made drinks made
            System.out.println("\n order not ready yet!");
            return 2;
        } else {
            throw new Exception("Error customer name wrong!"); //in case customer name entered wrong
        }

    }

}
