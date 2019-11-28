import java.io.BufferedReader;
import java.io.IOException;
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
    private final String userName = "root";

    /**
     * The password for the MySQL account (or empty for anonymous)
     */
    private final String password = "Lebronjames23!";

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

            if (promptLogin(conn, br)) System.out.println("Nice Login");
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
            conn.close();


        } catch (SQLException | IOException e) {
            System.out.println("ERROR: Invalid Table");
            e.printStackTrace();
            return;
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

        return investor_passwords.contains(password) && investor_ids.contains(userName);
    }

    /**
     * Connect to the DB and do some stuff
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        JavaMySql app = new JavaMySql();
        app.run();
    }
}
