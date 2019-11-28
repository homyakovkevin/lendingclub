/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 
db.mysql.url="jdbc:mysql://localhost:3306/db?characterEncoding=UTF-8&useSSL=false"
*/
//package javamysql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * @author kath
 */
public class JavaMySql {

    /**
     * The name of the MySQL account to use (or empty for anonymous)
     */
    private final String userName;

    /**
     * The password for the MySQL account (or empty for anonymous)
     */
    private final String password;

    /**
     * The name of the computer running MySQL
     */
    private final String serverName = "localhost";

    /**
     * The port of the MySQL server (default is 3306)
     */
    private final int portNumber = 3306;

    /**
     * The name of the database we are testing with (this default is installed with MySQL)
     */
    private final String dbName = "Lending Club";

    /**
     * The name of the table we are testing with
     */
    private final String tableName = "JDBC_TEST";
    private final boolean useSSL = false;

    public JavaMySql(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Get a new database connection
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);

        conn = DriverManager.getConnection("jdbc:mysql://"
                        + this.serverName + ":" + this.portNumber + "/" + this.dbName + "?characterEncoding=UTF-8&useSSL=false",
                connectionProps);

        return conn;
    }

    /**
     * Run a SQL command which does not return a recordset:
     * CREATE/INSERT/UPDATE/DELETE/DROP/etc.
     *
     * @throws SQLException If something goes wrong
     */
    public boolean executeUpdate(Connection conn, String command) throws SQLException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(command); // This will throw a SQLException if it fails
            return true;
        } finally {

            // This will run whether we throw an exception or not
            if (stmt != null) {
                stmt.close();
            }
        }
    }



    /**
     * Connect to MySQL and do some stuff.
     */
    public void run() throws IOException {
         // Connect to MySQL
        Connection conn = null;
        try {
            conn = this.getConnection();
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println("ERROR: Could not connect to the database");
            e.printStackTrace();
            return;
        }

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String command = br.readLine();

        System.out.println("Please, enter 1 to log in, or 2 to register an account");
        while (!command.equals("end")) {
            switch(command) {
                case ("1"):
                    this.promptLogin(conn, br);
                    break;
                case("2"):
                    this.promptRegister(conn, br);
                    break;
                default:
                    System.out.println("Please, enter only 1 or 2!");
                    break;
            }
        }
















//        // Connect to MySQL
//        Connection conn = null;
//        try {
//            conn = this.getConnection();
//            System.out.println("Connected to database");
//        } catch (SQLException e) {
//            System.out.println("ERROR: Could not connect to the database");
//            e.printStackTrace();
//            return;
//        }
//
//        try {
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("select character_name from lotr_character");
//            List<String> character_names = new ArrayList<>();
//
//            System.out.println("Choose your character from the list below");
//
//            while (rs.next()) {
//                System.out.println(rs.getString("character_name"));
//                character_names.add(rs.getString("character_name"));
//            }
//
//            String charName = "";
//
//            while (!character_names.contains(charName)) {
//                InputStreamReader isr = new InputStreamReader(System.in);
//                BufferedReader br = new BufferedReader(isr);
//                charName = br.readLine();
//                if (!character_names.contains(charName)) {
//                    System.out.println("Choose your character again, it does not match the one in the list");
//                }
//            }
//
//            String query = "{CALL track_character(?)}";
//            CallableStatement s = conn.prepareCall(query);
//            s.setString(1, charName);
//            ResultSet r = s.executeQuery();
//
//            while (r.next()) {
//                System.out.println(String.format("%s / %s / %s",
//                        r.getString("name_encountered"),
//                        r.getString("region_name"),
//                        r.getString("b.title")));
//            }
//            conn.close();
//
//
//        } catch (SQLException | IOException e) {
//            System.out.println("ERROR: Invalid Table");
//            e.printStackTrace();
//            return;
//        }

    }
    void promptRegister(Connection conn, BufferedReader br)  {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select investor_id from investor_account");
            List<String> investorIdList = new ArrayList<>();

            while (rs.next()) {
                investorIdList.add(rs.getString("investor_id"));
           }

            String userName;
            String passCode;
            String firstName;
            String lastName;

            System.out.println("Please, enter  username (email)");
            userName = br.readLine();
            if (!investorIdList.contains(userName)) {

                System.out.println("Please, enter  First Name");
                firstName = br.readLine();

                System.out.println("Please, enter  Last Name");
                lastName = br.readLine();


                System.out.println("Please, enter  password");
                passCode = br.readLine();

                // the mysql insert statement
                String query = " insert into users (investor_id, passcode, first_name, last_name, inv_number)"
                    + " values (?, ?, ?, ?, ?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString (1, userName);
                preparedStmt.setString (2, passCode);
                preparedStmt.setString (1, firstName);
                preparedStmt.setString (2, lastName);
                preparedStmt.setInt (2, 0);
                // execute the preparedstatement
                preparedStmt.execute();
            }
            else {
                System.out.println("Email is already used. Log in to an existing account, or use a different email");
                this.promptRegister(conn, br);
            }
        }
        catch (IOException | SQLException io) {
            io.printStackTrace();
        }
    }


        /**
     * Connect to the DB and do some stuff
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String userName = br.readLine();
        String password = br.readLine();

        JavaMySql app = new JavaMySql('root', 'Vk13790000!');
        app.run();
    }
}
        