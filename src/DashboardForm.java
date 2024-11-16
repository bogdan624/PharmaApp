import com.mysql.cj.log.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardForm extends JFrame {
    private JPanel DashboardPanel;
    private JLabel lbAdmin;
    private JButton btnRegister;
    private JButton btnLogin;

    public DashboardForm(){
        setTitle("Dashboard");
        setContentPane(DashboardPanel);
        setMinimumSize(new Dimension(500,429));
        setSize(1200,700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        lbAdmin.setText("Pagina principala");
        setLocationRelativeTo(null);
        setVisible(true);

        boolean hasRegistredUsers = connectToDatabse();

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                RegisterForm registerForm = new RegisterForm(DashboardForm.this);
                Client user = registerForm.client;

                if(user != null){
                    JOptionPane.showMessageDialog(DashboardForm.this,
                            "New user: " + user.nume,
                            "Successful Registration",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginForm loginForm = new LoginForm(DashboardForm.this);
                Client user = loginForm.user;

                if(user != null){
                    JOptionPane.showMessageDialog(DashboardForm.this,
                            "Bun venit: " + user.nume,
                            "Successful Login",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private boolean connectToDatabse() {
        boolean hasRegistredUser = false;

        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/farmacie";
        final String USERNAME = "root";
        final String PASSWORD = "1234";

        try{
            Connection conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM client");

            if(resultSet.next()){
                int numUsers = resultSet.getInt(1);
                if(numUsers > 0){
                    hasRegistredUser = true;
                }
            }
            statement.close();
            conn.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return hasRegistredUser;
    }

    public static void main(String[] args) {
        DashboardForm myForm = new DashboardForm();
    }
}
