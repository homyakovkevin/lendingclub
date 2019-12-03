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
import java.util.BitSet;
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
            "==================== Portfolio management ============================ \n" +
            "5 --> Show portfolio\n" +
            "6 --> Create record in portfolio\n" +
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
          defaultByCr(br, conn);
          break;
        case ("3"):
          averageSizeByCg(br, conn);
          break;
        case ("4"):
          averageInterestByCg(br, conn);
          break;
        case ("5"):
          showRecords(br, conn, false);
        case ("6"):
          addLoanToAccount(conn, br);
          break;
        case ("7"):
          updateInvestorWallet(conn, br);
          break;
        case ("8"):
          deleteRecord(br, conn);
        case ("9"):
            showMonthlyProfit(br, conn);
        case ("end"):
            return;
        default:
          System.out.println("Please, enter only numbers from 1 to 9!\n");
          mainMenuProcessor(br, conn);
          break;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void averageInterestByCg(BufferedReader br, Connection conn) {
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
        System.out.println("============================================" +
            "1 --> Retry \n" +
            "0 --> Back to Main Menu \n");
        String option = br.readLine();
        if (option.equals("1")) {
          defaultByHo(br, conn);
        } else if (option.equals("0")) {
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

  private void showRecords(BufferedReader br, Connection conn, boolean readOnly) {
    try {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("select * from investor_loan");
      System.out.println("===================== YOUR PORTFOLIO ================================");
      System.out.println("loan_id" + "\t" + "investor_id" + "\t" + "loan_amount" + "\t" + "int_rate"
          + "\t" + "term" + "\t" + "purpose" + "\t" + "issue_date" + "\t" + "loan_status"
          + "\t" + "member_id" + "\t" + "grade" + "\t" + "home_id");
      while (rs.next()) {
        System.out.println(rs.getString("loan_id") + "\t" + rs.getString("investor_id")
            + "\t" + rs.getString("loan_amount") + "\t" + rs.getString("int_rate")
            + "\t" + rs.getString("term") + "\t" + rs.getString("purpose")
            + "\t" + rs.getString("issue_date") + "\t" + rs.getString("loan_status")
            + "\t" + rs.getString("member_id") + "\t" + rs.getString("grade")
            + "\t" + rs.getString("home_id"));
      }
      System.out.println("===================================================================");
      System.out.println("0 --> Return to main menu");
      if (!readOnly) {
        String s = br.readLine();
        if (s.equals("0")) mainMenuProcessor(br, conn);
        else {
          System.out.println("Wrong option selected, redirecting to Main Menu...");
          mainMenuProcessor(br, conn);
        }
      }
    } catch (SQLException | IOException se) {
      se.printStackTrace();
    }
  }

  private void deleteRecord(BufferedReader br, Connection conn) {
    showRecords(br, conn, true);
    System.out.println("Provide loan_id of record you want to delete");
    try {
      String s = br.readLine();
      int id = Integer.parseInt(s);
      String sql = "delete from investor_loan where loan_id=?";
      PreparedStatement preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
      System.out.println("Record deleted successfully");
      System.out.println("0 --> Return to main menu");
      System.out.println("1 --> Delete one more record");
      String s2 = br.readLine();
      if (s2.equals("0")) mainMenuProcessor(br, conn);
      else if (s2.equals("1")) deleteRecord(br, conn);
      else {
        System.out.println("Wrong option selected, redirecting to Main Menu...");
        mainMenuProcessor(br, conn);
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }

  private void showMonthlyProfit(BufferedReader br, Connection conn) {
    try {
      CallableStatement stmt = conn.prepareCall("{? = call monthly_profit(?)}");
      stmt.registerOutParameter(1, Types.FLOAT);
      stmt.setString(2, this.clientUsername);
      stmt.execute();
      System.out.println("============================================");
      System.out.println("Monthly Profit: $" + (stmt.getFloat(1)));
      System.out.println("============================================");
      System.out.println("Returning to main menu");
      mainMenuProcessor(br, conn);
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }

  private void averageSizeByCg(BufferedReader br, Connection conn) {
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
        System.out.println("============================================" +
            "1 --> Retry \n" +
            "0 --> Back to Main Menu \n");
        String option = br.readLine();
        if (option.equals("1")) {
          defaultByHo(br, conn);
        } else if (option.equals("0")) {
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
        System.out.println("============================================" +
            "1 --> Retry \n" +
            "0 --> Back to Main Menu \n");
        String option = br.readLine();
        if (option.equals("1")) {
          defaultByHo(br, conn);
        } else if (option.equals("0")) {
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
        System.out.println("============================================" +
            "1 --> Retry \n" +
            "0 --> Back to Main Menu \n");
        String option = br.readLine();
        if (option.equals("1")) {
          defaultByHo(br, conn);
        } else if (option.equals("0")) {
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
        String query = " insert into investor_account (investor_id, passcode, first_name, last_name)"
            + " values (?, ?, ?, ?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, userName);
        preparedStmt.setString(2, passCode);
        preparedStmt.setString(3, firstName);
        preparedStmt.setString(4, lastName);
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

  public void addLoanToAccount(Connection conn, BufferedReader br) throws IOException {

    System.out.println("Are you sure you want to add a loan to your wallet");
    System.out.println("Enter 0, if you want to return to main menu. Enter 1 if you want to proceed");
    String userChoice = br.readLine();

    if (userChoice.equals("1")) {
      int ssn;
      int borrowerIncome;
      String borrowerState;
      float borrowerCreditScore;


      try {
        System.out.println("Please, enter the requested information to add the loan to your investor wallet");

        // Get data for borrower table
        System.out.println("Please, enter the borrower's Social Security Number");
        ssn = Integer.parseInt(br.readLine());

        System.out.println("Please, enter borrower's income");
        borrowerIncome = Integer.parseInt(br.readLine());

        System.out.println("Please, enter the the borrower's state int the two-letter format");
        borrowerState = br.readLine();

        System.out.println("Please, enter borrower's credit rating");
        borrowerCreditScore = Float.parseFloat(br.readLine());

        // the mysql insert statement
        String queryBorrower = " insert into investor_borrower (member_id, annual_income, address, credit_score)"
            + " values (?, ?, ?, ?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmtBorrower = conn.prepareStatement(queryBorrower);
        preparedStmtBorrower.setInt(1, ssn);
        preparedStmtBorrower.setInt(2, borrowerIncome);
        preparedStmtBorrower.setString(3, borrowerState);
        preparedStmtBorrower.setFloat(4, borrowerCreditScore);
        // execute the preparedstatement
        preparedStmtBorrower.execute();


        // Get data for loan table

        int loanAmount;
        double intRate;
        String loanTerm;
        String loanPurpose;
        String loanIssueDate;
        int loanStatus = 4;
        int loanGrade;
        int home_id;


        System.out.println("Please, enter the loan amount");
        loanAmount = Integer.parseInt(br.readLine());

        System.out.println("Please, enter the interest rate of the loan");
        intRate = Double.parseDouble(br.readLine());

        System.out.println("Please, enter the loan term");
        System.out.println("Enter the terms in terms of months, for example: 36 months");
        loanTerm = br.readLine();

        System.out.println("Please, enter the purpose of the loan");
        System.out.println("These are the most popular loan purposes: \n" +
            "debt_consolidation\n" +
            "credit_card\n" +
            "home_improvement\n" +
            "other\n" +
            "major purchase\n" +
            "medical\n" +
            "car\n" +
            "small business\n" +
            "moving\n" +
            "vacation\n" +
            "house\n" +
            "renewable energy\n");
        loanPurpose = br.readLine();

        System.out.println("Please, enter the date the loan was issued in the following format, 15-Mar");
        loanIssueDate = br.readLine();

        System.out.println("Please, enter the grade of the loan");
        System.out.println("Choose a value associated with the grade: \n" +
            "0 --> G\n" +
            "1 --> C\n" +
            "2 --> A\n" +
            "3 --> B\n" +
            "4 --> E\n" +
            "5 --> D\n" +
            "5 --> F\n");
        loanGrade = Integer.parseInt(br.readLine());

        System.out.println("Please, enter the home ownership type of the borrower");
        System.out.println("Please choose Home Ownership type: \n" +
            "0 --> Owns\n" +
            "1 --> Rent\n" +
            "2 --> Mortgage\n");
        home_id = Integer.parseInt(br.readLine());


        // the mysql insert statement
        String queryLoan = " insert into investor_loan (investor_id, loan_amount, int_rate, term, purpose, issue_date, loan_status, "
            + "member_id, grade, home_id)"
            + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmtLoan = conn.prepareStatement(queryLoan);
        preparedStmtLoan.setString(1, clientUsername);
        preparedStmtLoan.setInt(2, loanAmount);

        preparedStmtLoan.setDouble(3, intRate);
        preparedStmtLoan.setString(4, loanTerm);
        preparedStmtLoan.setString(5, loanPurpose);
        preparedStmtLoan.setString(6, loanIssueDate);
        preparedStmtLoan.setInt(7, loanStatus);
        preparedStmtLoan.setInt(8, ssn);
        preparedStmtLoan.setInt(9, loanGrade);
        preparedStmtLoan.setInt(10, home_id);

        // execute the preparedstatement
        preparedStmtLoan.execute();
          System.out.println("Loan was added to your wallet");
          System.out.println("============================================\n" +
              "2 --> Add more \n" +
              "1 --> Show Portfolio \n" +
              "0 --> Back to Main Menu \n");
          String option = br.readLine();
          if (option.equals("2")) {
              addLoanToAccount(conn, br);
          } else if (option.equals("0")) {
              mainMenuProcessor(br, conn);
          }
          else if (option.equals("1")) {
              this.showRecords(br, conn, false);
          }


      } catch (NumberFormatException | SQLException e) {
        e.printStackTrace();
      }
    } else if (userChoice.equals("0")) {
      this.mainMenuProcessor(br, conn);
    }
  }

  public void updateInvestorWallet(Connection conn, BufferedReader br) throws SQLException, IOException {


    double intRate = 0;
    int loanAmount = 0;
    String term = "";
    String purpose = "";
    int grade = 0;

    String queryLoan = "select distinct loan_id from investor_loan where investor_id = ?";

    PreparedStatement preparedStmtLoan = conn.prepareStatement(queryLoan);
    preparedStmtLoan.setString(1, clientUsername);
    preparedStmtLoan.execute();
    ResultSet rsLoanIds = preparedStmtLoan.executeQuery();


    System.out.println("Here is the list of the loans in your portfolio");
    this.showRecords(br, conn, true);
    System.out.println("Please, enter the loan ids you want to update");

    int loanID = Integer.parseInt(br.readLine());


    // the mysql insert statement
    String getUpdates = "select int_rate, loan_amount, term, purpose, grade from investor_loan where investor_id = ? and loan_id = ?";

    // create the mysql insert preparedstatement
    PreparedStatement preparedgetUPdates = conn.prepareStatement(getUpdates);
    preparedgetUPdates.setString(1, clientUsername);
    preparedgetUPdates.setInt(2, loanID);
    preparedgetUPdates.execute();
    ResultSet rsUpdateFields = preparedgetUPdates.executeQuery();


    while (rsUpdateFields.next()) {
      intRate = rsUpdateFields.getDouble("int_rate");
      loanAmount = rsUpdateFields.getInt("loan_amount");
      term = rsUpdateFields.getString("term");
      purpose = rsUpdateFields.getString("purpose");
      grade = rsUpdateFields.getInt("grade");
    }

    try {
      System.out.println("Please choose the column you want to update: \n" +
          "0 --> int_rate\n" +
          "1 --> loan_amount\n" +
          "2 --> term\n" +
          "3 --> purpose\n" +
          "4 --> grade\n" +
          "5 --> annual_income\n" +
          "====================================================== \n" +
          "99 --> Return to Main menu\n");

      String updateColumn = br.readLine();

      CallableStatement stmt;
      switch (updateColumn) {
        case ("0"):
          System.out.println("Enter a new value for the int_rate");
          double updatedIntRate = Double.parseDouble(br.readLine());
          stmt = conn.prepareCall("update investor_loan set int_rate = ?, loan_amount = ?, term = ?, purpose = ?, grade = ? where investor_id = ? and loan_id = ?");
          stmt.setDouble(1, updatedIntRate);
          stmt.setInt(2, loanAmount);
          stmt.setString(3, term);
          stmt.setString(4, purpose);
          stmt.setInt(5, grade);
          stmt.setString(6, clientUsername);
          stmt.setInt(7, loanID);
          stmt.execute();
          break;
        case ("1"):
          System.out.println("Enter a new value for the loan amount");
          int updatedLoanAmount = Integer.parseInt(br.readLine());
          stmt = conn.prepareCall("update investor_loan set int_rate = ?, loan_amount = ?, term = ?, purpose = ?, grade = ? where investor_id = ? and loan_id = ?");
          stmt.setDouble(1, intRate);
          stmt.setInt(2, updatedLoanAmount);
          stmt.setString(3, term);
          stmt.setString(4, purpose);
          stmt.setInt(5, grade);
          stmt.setString(6, clientUsername);
          stmt.setInt(7, loanID);
          stmt.execute();
          break;
        case ("2"):
          System.out.println("Enter a new value for the term");
          String updatedterm = br.readLine();
          stmt = conn.prepareCall("update investor_loan set int_rate = ?, loan_amount = ?, term = ?, purpose = ?, grade = ? where investor_id = ? and loan_id = ?");
          stmt.setDouble(1, intRate);
          stmt.setInt(2, loanAmount);
          stmt.setString(3, updatedterm);
          stmt.setString(4, purpose);
          stmt.setInt(5, grade);
          stmt.setString(6, clientUsername);
          stmt.setInt(7, loanID);
          stmt.execute();
          break;
        case ("3"):
          System.out.println("Enter a new value for the purpose");
          String updatedPurpose = br.readLine();
          stmt = conn.prepareCall("update investor_loan set int_rate = ?, loan_amount = ?, term = ?, purpose = ?, grade = ? where investor_id = ? and loan_id = ?");
          stmt.setDouble(1, intRate);
          stmt.setInt(2, loanAmount);
          stmt.setString(3, term);
          stmt.setString(4, updatedPurpose);
          stmt.setInt(5, grade);
          stmt.setString(6, clientUsername);
          stmt.setInt(7, loanID);
          stmt.execute();
          break;
        case ("4"):
          System.out.println("Enter a new value for the borrower's grade");
          int updatedGrade = Integer.parseInt(br.readLine());
          stmt = conn.prepareCall("update investor_loan set int_rate = ?, loan_amount = ?, term = ?, purpose = ?, grade = ? where investor_id = ? and loan_id = ?");
          stmt.setDouble(1, intRate);
          stmt.setInt(2, loanAmount);
          stmt.setString(3, term);
          stmt.setString(4, purpose);
          stmt.setInt(5, updatedGrade);
          stmt.setString(6, clientUsername);
          stmt.setInt(7, loanID);
          stmt.execute();
          break;
        case ("99"):
          mainMenuProcessor(br, conn);
          break;
        default:
          System.out.println("Please, enter only numbers from 0 to 5!\n");
          updateInvestorWallet(conn, br);
          break;
      }

        System.out.println("Loan was updated in your wallet");
        System.out.println("============================================\n" +
            "1 --> Retry \n" +
            "0 --> Back to Main Menu \n");
        String option = br.readLine();
        if (option.equals("1")) {
            updateInvestorWallet(conn, br);
        } else if (option.equals("0")) {
            mainMenuProcessor(br, conn);
        }
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
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
