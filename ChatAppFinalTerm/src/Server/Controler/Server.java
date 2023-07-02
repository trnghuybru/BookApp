/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.Controler;

import java.net.ServerSocket;
import Server.View.ServerView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import Server.Model.Account;
import DAOs.AccountDB;

/**
 *
 * @author GIGABYTE
 */
public class Server {

    public static volatile ServerThreadBus serverThreadBus;
    public static Socket socketOfServer;
    private static BufferedWriter os;

    public static void main(String[] args) {
        ServerSocket listener = null;
        serverThreadBus = new ServerThreadBus();
        System.out.println("Server is waiting to accept user....");
        int clientNumber = 0;

        try {
            listener = new ServerSocket(7777);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(8));
        try {
            while (true) {
                socketOfServer = listener.accept();
                ServerThread serverThread = new ServerThread(socketOfServer, clientNumber++);
                serverThreadBus.add(serverThread);
                System.out.println("So thread dang chay la: " + serverThreadBus.getLength());
                executor.execute(serverThread);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                listener.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


}
