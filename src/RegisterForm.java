import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class RegisterForm extends JDialog {
    private JTextField tfNume;
    private JTextField tfEmail;
    private JTextField tfTelefon;
    private JPasswordField pfParola;
    private JPasswordField pfConfirmareParola;
    private JButton btnRegister;
    private JButton btnAnuleaza;
    private JPanel registerPanel;

    public RegisterForm(JFrame parent){
        super(parent);
        setTitle("Creati un cont nou");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(500,429));
        setSize(1200,700);
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        btnAnuleaza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String nume = tfNume.getText();
        String email = tfEmail.getText();
        String telefon = tfTelefon.getText();
        String parola = String.valueOf(pfParola.getPassword());
        String confirmareParola = String.valueOf(pfConfirmareParola.getPassword());

        if(nume.isEmpty() || email.isEmpty() || telefon.isEmpty() || parola.isEmpty()){
            JOptionPane.showMessageDialog(this,
                    "Va rog sa introduceti valori in toate campurile",
                    "Incercati din nou",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!parola.equals(confirmareParola)){
            JOptionPane.showMessageDialog(this,
                    "Confirmarea parolei nu este buna",
                    "Incercati din nou",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        client = addClientToDatabase(nume,email,telefon,parola);
        if (client != null){
            dispose();
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "Nu s-a putut inregistra un client nou",
                    "Incercati din nou",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    public Client client;
    private Client addClientToDatabase(String nume, String email,String telefon, String parola){
        Client client = null;
        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/farmacie";
        final String USERNAME = "root";
        final String PASSWORD = "1234";
        try{
            Connection conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO client(nume_client, telefon_client,email_client,parola_client)" + "VALUES(?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,nume);
            preparedStatement.setString(2,telefon);
            preparedStatement.setString(3,email);
            preparedStatement.setString(4,parola);

            int addedRows = preparedStatement.executeUpdate();
            if(addedRows > 0){
                client = new Client();
                client.nume = nume;
                client.telefon = telefon;
                client.email = email;
                client.parola = parola;
            }

            stmt.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return client;
    }

    public static void main(String[] args) {
        RegisterForm myForm = new RegisterForm(null);
        Client client = myForm.client;
        if(client != null){
            System.out.println("Registrare realizata cu succes pentru utilizatorul: "+ client.nume);
        }
        else {
            System.out.println("Registrare anulata");
        }
    }
}
