import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/farmacie",
                    "root",
                    "1234"
            );
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM client");

            while (resultSet.next()) {
                System.out.println(resultSet.getString("nume_client"));
                System.out.println(resultSet.getString("parola_client"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}