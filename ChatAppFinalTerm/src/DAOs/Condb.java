/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

/**
 *
 * @author GIGABYTE
 */
import java.sql.*;

public class Condb {

    static String url = "jdbc:mysql://localhost:3306/bookapp";
    static String user = "root";
    static String password = "";

    public static Connection getConnection() {
        Connection c = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            c = DriverManager.getConnection(url, user, password);
            if (c != null) {
//   System.out.print("Connected to database successfully");    
                return c;
            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return c;
    }

    public static void closeDBConnect(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
