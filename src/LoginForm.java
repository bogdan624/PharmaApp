import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog{
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnOK;
    private JButton btnCancel;
    private JPanel loginPanel;
    public LoginForm(JFrame parent){
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(500,429));
        setSize(1200,700);
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());

                user = getAutehticatedUser(email,password);

                if(user != null){
                    dispose();
                }
                else {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Email or Password Invalid",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();

            }
        });
        setVisible(true);
    }
    public Client user;
    private Client getAutehticatedUser(String email, String password){
        Client user = null;

        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/farmacie";
        final String USERNAME = "root";
        final String PASSWORD = "1234";
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM client WHERE email_client=? AND parola_client=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                user = new Client();
                user.nume = resultSet.getString("nume_client");
                user.email = resultSet.getString("email_client");
                user.telefon = resultSet.getString("telefon_client");
                user.parola = resultSet.getString("parola_client");
            }

            stmt.close();
            conn.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return user;
    }

    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(null);
        Client user = loginForm.user;
        if(user != null){
            System.out.println("Autentificare realizata cu succes pt utilizatorul: " + user.nume);
            System.out.println("Email: " + user.email);
            System.out.println("Telefon: " + user.telefon);
        }
        else {
            System.out.println("Autentificare inchisa");
        }
    }
}
