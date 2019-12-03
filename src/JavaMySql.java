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
    private String userName = "root";

    /**
     * The password for the MySQL account (or empty for anonymous)
     */
    private String password = "Vk13790000!";

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
    private final String dbName = "lendingclub";

    /**
     * The name of the table we are testing with
     */
    private final String tableName = "JDBC_TEST";
    private final boolean useSSL = false;

    private String clientUsername = "";

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
    public void run() {
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
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            requireLogIn(br, conn);
        } catch (IOException e) {
            System.out.println("ERROR: Invalid Table");
            e.printStackTrace();
            return;
        }

    }

    private void requireLogIn(BufferedReader br, Connection conn) throws IOException {
        System.out.println("Please choose one option: \n" +
                        "1 --> Log in \n" +
                        "2 --> Register \n");
        String command = br.readLine();
        try {
            switch (command) {
                case ("1"):
                    if (this.promptLogin(conn, br)) {
                        mainMenuProcessor(br, conn);
                    } else {
                        System.out.println("Entered credentials didn't match our records! \n");
                        requireLogIn(br, conn);
                    }
                    break;
                case ("2"):
                    this.promptRegister(conn, br);
                    mainMenuProcessor(br, conn);
                    break;
                default:
                    System.out.println("Please, enter only 1 or 2! \n");
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mainMenuProcessor(BufferedReader br, Connection conn) throws IOException {
        String menu =
                "==================== Historical data summary ========================== \n" +
                        "1 --> Default by Home Ownership type\n" +
                        "2 --> Default by borrower's credit grade\n" +
                        "3 --> Average size of loan by borrower's credit grade\n" +
                        "4 --> Average interest rate by borrower's credit grade\n" +
                        "==================== Portfolio management ========================== \n" +
                        "5 --> Show portfolio\n" +
                        "6 --> Update record in portfolio\n" +
                        "7 --> Update record in portfolio\n" +
                        "8 --> Delete record in portfolio\n" +
                        "9 --> Show average monthly profit\n" +
                "Type \"END\" to close the program\n" +
                "=======================================================================";
        System.out.println(menu);
        String command = br.readLine();
        try {
            switch (command.toLowerCase()) {
                case ("1"):
                    defaultByHo(br, conn);
                    break;
                case ("2"):
                    defaultByCr (br, conn);
                    break;
                case ("3"):
                    averageSizeByCg (br, conn);
                    break;
                case ("4"):
                    averageInterestByCg (br, conn);
                    break;
                case ("5"):
                    //TODO
                    // showPortfolio (br, conn)
                case ("6"):
                    //TODO
                    // updateRecord (br, conn)
                case ("7"):
                    //TODO
                    // homeOwenershipAverage (br, conn)
                case ("8"):
                    //TODO
                    // homeOwenershipAverage (br, conn)
                case ("9"):
                    //TODO
                    // homeOwenershipAverage (br, conn)
                case ("end"):
                    //TODO
                    br.close();
                    break;
                default:
                    System.out.println("Please, enter only numbers from 1 to 9!\n");
                    mainMenuProcessor(br,conn);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void averageInterestByCg (BufferedReader br, Connection conn){
        System.out.println("Please choose credit grade: \n" +
                "0 --> G\n" +
                "1 --> C\n" +
                "2 --> A\n" +
                "3 --> B\n" +
                "4 --> E\n" +
                "5 --> D\n" +
                "6 --> F\n" +
                "====================================================== \n" +
                "99 --> Return to Main menu\n");
        try {
            CallableStatement stmt = conn.prepareCall("{? = call average_int_rate_by_grade(?)}");
            String creditGrade = br.readLine();
            int creditGradeInt = Integer.parseInt(creditGrade);
            if (creditGradeInt == 99) {
                mainMenuProcessor(br, conn);
            } else {
                stmt.registerOutParameter(1, Types.DOUBLE);
                stmt.setInt(2, creditGradeInt);
                stmt.execute();
                System.out.println("Average interest rate: " + (stmt.getDouble(1)) + "%");
                System.out.println("============================================"+
                        "1 --> Retry \n" +
                        "0 --> Back to Main Menu \n");
                String option = br.readLine();
                if (option.equals("1")){
                    defaultByHo(br, conn);
                } else if (option.equals("0")){
                    mainMenuProcessor(br, conn);
                } else {
                    System.out.println("Wrong option selected, redirecting to Main Menu... \n");
                    mainMenuProcessor(br, conn);
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        } catch (NumberFormatException nf) {
            System.out.println("Please enter number associated with desired option\n");
            defaultByHo(br, conn);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void averageSizeByCg (BufferedReader br, Connection conn){
        System.out.println("Please choose credit grade: \n" +
                "0 --> G\n" +
                "1 --> C\n" +
                "2 --> A\n" +
                "3 --> B\n" +
                "4 --> E\n" +
                "5 --> D\n" +
                "6 --> F\n" +
                "====================================================== \n" +
                "99 --> Return to Main menu\n");
        try {
            CallableStatement stmt = conn.prepareCall("{? = call average_loan_size_by_grade(?)}");
            String creditGrade = br.readLine();
            int creditGradeInt = Integer.parseInt(creditGrade);
            if (creditGradeInt == 99) {
                mainMenuProcessor(br, conn);
            } else {
                stmt.registerOutParameter(1, Types.INTEGER);
                stmt.setInt(2, creditGradeInt);
                stmt.execute();
                System.out.println("Average size of the loan: $" + (stmt.getInt(1)));
                System.out.println("============================================"+
                        "1 --> Retry \n" +
                        "0 --> Back to Main Menu \n");
                String option = br.readLine();
                if (option.equals("1")){
                    defaultByHo(br, conn);
                } else if (option.equals("0")){
                    mainMenuProcessor(br, conn);
                } else {
                    System.out.println("Wrong option selected, redirecting to Main Menu... \n");
                    mainMenuProcessor(br, conn);
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        } catch (NumberFormatException nf) {
            System.out.println("Please enter number associated with desired option\n");
            defaultByHo(br, conn);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void defaultByHo(BufferedReader br, Connection conn) {
        System.out.println("Please choose Home Ownership type: \n" +
                "0 --> Owns\n" +
                "1 --> Rent\n" +
                "2 --> Mortgage\n" +
                "====================================================== \n" +
                "99 --> Return to Main menu\n");
        try {
            CallableStatement stmt = conn.prepareCall("{? = call default_by_homeownership(?)}");
            String hoIndex = br.readLine();
            int hoIndexInt = Integer.parseInt(hoIndex);
            if (hoIndexInt == 99) {
                mainMenuProcessor(br, conn);
            } else {
                stmt.registerOutParameter(1, Types.FLOAT);
                stmt.setInt(2, hoIndexInt);
                stmt.execute();
                System.out.println("Average default rate: " + (stmt.getFloat(1) * 100) + "%");
                System.out.println("============================================"+
                        "1 --> Retry \n" +
                "0 --> Back to Main Menu \n");
                String option = br.readLine();
                if (option.equals("1")){
                    defaultByHo(br, conn);
                } else if (option.equals("0")){
                    mainMenuProcessor(br, conn);
                } else {
                    System.out.println("Wrong option selected, redirecting to Main Menu... \n");
                    mainMenuProcessor(br, conn);
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        } catch (NumberFormatException nf) {
            System.out.println("Please enter number associated with desired option\n");
            defaultByHo(br, conn);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void defaultByCr(BufferedReader br, Connection conn) {
        System.out.println("Please choose credit grade: \n" +
                "0 --> G\n" +
                "1 --> C\n" +
                "2 --> A\n" +
                "3 --> B\n" +
                "4 --> E\n" +
                "5 --> D\n" +
                "6 --> F\n" +
                "====================================================== \n" +
                "99 --> Return to Main menu\n");
        try {
            CallableStatement stmt = conn.prepareCall("{? = call default_by_grade(?)}");
            String creditGrade = br.readLine();
            int creditGradeInt = Integer.parseInt(creditGrade);
            if (creditGradeInt == 99) {
                mainMenuProcessor(br, conn);
            } else {
                stmt.registerOutParameter(1, Types.FLOAT);
                stmt.setInt(2, creditGradeInt);
                stmt.execute();
                System.out.println("Average default rate: " + (stmt.getFloat(1) * 100) + "%");
                System.out.println("============================================"+
                        "1 --> Retry \n" +
                        "0 --> Back to Main Menu \n");
                String option = br.readLine();
                if (option.equals("1")){
                    defaultByHo(br, conn);
                } else if (option.equals("0")){
                    mainMenuProcessor(br, conn);
                } else {
                    System.out.println("Wrong option selected, redirecting to Main Menu... \n");
                    mainMenuProcessor(br, conn);
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        } catch (NumberFormatException nf) {
            System.out.println("Please enter number associated with desired option\n");
            defaultByHo(br, conn);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }


    public boolean promptLogin(Connection conn, BufferedReader br) throws IOException, SQLException {
        System.out.println("Type in your login for Lending Club");
        String userName = br.readLine();
        System.out.println("Type in your password for Lending Club");
        String password = br.readLine();

        Statement stmt = conn.createStatement();
        ResultSet rs_investor_id = stmt.executeQuery("select investor_id from investor_account");
        List<String> investor_ids = new ArrayList<>();

        while (rs_investor_id.next()) {
            investor_ids.add(rs_investor_id.getString("investor_id"));
        }

        ResultSet rs_password = stmt.executeQuery("select passcode from investor_account");
        List<String> investor_passwords = new ArrayList<>();

        while (rs_password.next()) {
            investor_passwords.add(rs_password.getString("passcode"));
        }

        this.clientUsername = userName;

        return investor_passwords.contains(password) && investor_ids.contains(userName);

    }


    public void promptRegister(Connection conn, BufferedReader br) {
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
                System.out.println("Please, enter  password");
                passCode = br.readLine();

                System.out.println("Please, enter  First Name");
                firstName = br.readLine();

                System.out.println("Please, enter  Last Name");
                lastName = br.readLine();


                // the mysql insert statement
                String query = " insert into investor_account (investor_id, passcode, first_name, last_name, inv_number)"
                        + " values (?, ?, ?, ?, ?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString(1, userName);
                preparedStmt.setString(2, passCode);
                preparedStmt.setString(3, firstName);
                preparedStmt.setString(4, lastName);
                preparedStmt.setInt(5, 0);
                // execute the preparedstatement
                preparedStmt.execute();
                this.clientUsername = userName;
            } else {
                System.out.println("Email is already used. Log in to an existing account, or use a different email");
                this.promptRegister(conn, br);
            }
        } catch (IOException | SQLException io) {
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
//        String userName = br.readLine();
//        String password = br.readLine();
        JavaMySql app = new JavaMySql("root", "Vk13790000!");
        app.run();
    }
}
