/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.Controler;

import DAOs.AccountDB;
import Server.Model.Account;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author GIGABYTE
 */
class ServerThread implements Runnable {

    private Socket socketOfServer;
    private int clientNumber;
    private BufferedReader is;
    private BufferedWriter os;
    private boolean isClosed;

    private BufferedReader getIs() {
        return is;
    }

    private BufferedWriter getOs() {
        return os;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public ServerThread(Socket socketOfServer, int clientNumber) {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        System.out.println("Server thread number " + clientNumber + " Started");
        isClosed = false;
    }

    @Override
    public void run() {
        try {
            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));

            Account authenticatedAccount = authenticateUser(is,os);
            if (authenticatedAccount != null) {
                System.out.println("Khởi động thành công ID là: " + clientNumber);
                write("login,success");
                write("get-id" + "," + this.clientNumber);
                Server.serverThreadBus.sendOnlineList();
                Server.serverThreadBus.multiCastSend("global-message" + "," + "----Client " + this.clientNumber + " đã đăng nhập--");
                String message;
                while (!isClosed) {
                    message = is.readLine();
                    if (message == null) {
                        break;
                    }
                    String[] messageSplit = message.split(",");
                    if (messageSplit[0].equals("send-to-global")) {
                        Server.serverThreadBus.boardCast(this.getClientNumber(), "global-message" + "," + "Client " + messageSplit[2] + ": " + messageSplit[1]);
                    }
                    if (messageSplit[0].equals("send-to-person")) {
                        Server.serverThreadBus.sendMessageToPerson(Integer.parseInt(messageSplit[3]), "Client " + messageSplit[2] + " to you: " + messageSplit[1]);
                    }
                }
            } else {
                write("login,fail");
                isClosed = true;
                Server.serverThreadBus.remove(clientNumber);
                System.err.println(this.clientNumber + " đã thoát");
            }

        } catch (IOException ex) {
            isClosed = true;
            Server.serverThreadBus.remove(clientNumber);
            System.err.println(this.clientNumber + " đã thoát");
            Server.serverThreadBus.multiCastSend("global-message" + "," + "---Client " + this.clientNumber + " đã thoát----");
            ex.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
                if (socketOfServer != null && !socketOfServer.isClosed()) {
                    socketOfServer.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void write(String message) throws IOException {
        os.write(message);
        os.newLine();
        os.flush();
    }

    // Phương thức xác thực người dùng
    private static Account authenticateUser(BufferedReader reader, BufferedWriter writer) {
        try {
           

            // Đọc tên đăng nhập từ người dùng
            String accountString = reader.readLine().trim();

            String[] accountSet = accountString.split(",");

            Account account = new Account(accountSet[0], accountSet[1]);

            Account accountX = AccountDB.getInstance().selectById(account.getPhone());
            if (accountX == null) {
                return null;
            } else {
                if (account.getPassword().equals(accountX.getPassword())) {
                    return accountX;
                } else {
                    return null;
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Thông tin đăng nhập không chính xác
        return null;
    }

    private static class User {

        private String username;
        private String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
