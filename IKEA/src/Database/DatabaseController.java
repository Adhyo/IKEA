package Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Model.Admin;

public class DatabaseController {

    private static DatabaseManager db = new DatabaseManager();

    public static Admin getEmailAdmin(String email) {
        Admin user = new Admin();

        try {
            db.connect();
            String query1 = "SELECT * FROM admin WHERE Email ='" + email + "'";
            Statement stmt1 = db.con.createStatement();
            ResultSet rs1 = stmt1.executeQuery(query1);

            if (rs1.next()) {
                do {
                    user.getPassword();
                } while (rs1.next());

            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
        return user;
    }
}