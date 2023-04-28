import Helpers.ClientHandler;
import Helpers.Kitchen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Barista {
    private final static int port = 8888; //server port
    private static final Kitchen KITCHEN = new Kitchen(); //server kitchen

    public static int getPort() {
        return port;
    } //Get the port for the server and client connection

    private static void ServerRun() {

        // used for listening for incoming connection
        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(getPort()); //bind port to server
            System.out.println("Waiting to serve Customer..."); //message while waiting for connecting
            while (true) {
                socket = server.accept(); //accepts connecting
                new Thread(new ClientHandler(socket, KITCHEN)).start(); //starts new thread
            }
        } catch (IOException exception) { //Exception
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerRun();
    } //runs server
}
