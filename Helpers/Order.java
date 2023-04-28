package Helpers;

public class Order {
    private final String customerName; //Customer
    private final int customerID;
    private String coffee; //order coffee
    private String tea; //order tea
    private int coffeeOrderAmount; //order amount of drinks
    private int teaOrderAmount; //order amount of drinks


    public Order(String customerName, int customerID) { //gets order Name
        this.customerName = customerName;
        this.customerID = customerID;
    }

    public String getCustomerName() {return customerName;} //get the customers name
    public int getCustomerID() {return customerID;}// customerID
    public int getCoffeeOrderAmount() {return coffeeOrderAmount;} //gets order amount
    public int getTeaOrderAmount() {return teaOrderAmount;} //gets tea amount
    public String getCoffee() {return coffee;} //gets order
    public String getTea() {return tea;} //get tea
    public void setOrder(String newCoffee, int newCoffeeAmount, String newTea, int newTeaAmount){coffee = newCoffee; coffeeOrderAmount = newCoffeeAmount; tea = newTea; teaOrderAmount = newTeaAmount;} //sets the order for the customer

    public void addToOrder(int moreCoffeeAmount, int moreTeaAmount){coffeeOrderAmount += moreCoffeeAmount; teaOrderAmount += moreTeaAmount;} //if customer orders more after original order, add to it.
}
