import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class App extends JFrame {
    private JPanel tableListPanel;
    private JButton[] tableButtons;
    private String[] tables = {"customers", "movies", "media", "rental_history", "actors", "star_billings"};

    public App() {
        setTitle("Media Rentals");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel containerPanel = new JPanel(new GridBagLayout());
        tableListPanel = new JPanel();
        tableListPanel.setLayout(new GridLayout(tables.length + 1, 1, 10, 10));
        
        JLabel titleLabel = new JLabel("Select a Table:", SwingConstants.CENTER);
        tableListPanel.add(titleLabel);

        tableButtons = new JButton[tables.length];
        for (int i = 0; i < tables.length; i++) {
            tableButtons[i] = new JButton(tables[i]);
            tableButtons[i].setPreferredSize(new Dimension(150, 30));
            tableButtons[i].addActionListener(new TableButtonListener());
            tableListPanel.add(tableButtons[i]);
        }
        
        containerPanel.add(tableListPanel);
        add(containerPanel, BorderLayout.CENTER);
    }

    private class TableButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();
            String tableName = clickedButton.getText();
            new TableEditor(tableName);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}

class TableEditor extends JFrame {
    private String tableName;
    private JTable table;
    private JButton insertButton, updateButton, deleteButton;
    private Connection connection;
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "user_name"; //your username
    private static final String PASSWORD = "password"; //your password

    public TableEditor(String tableName) {
        this.tableName = tableName;
        setTitle("Editing: " + tableName);
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        table = new JTable();
        loadData();
        
        JPanel buttonPanel = new JPanel();
        insertButton = new JButton("Insert");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        insertButton.addActionListener(e -> showInsertForm());
        updateButton.addActionListener(e -> showUpdateForm());
        deleteButton.addActionListener(e -> showDeleteForm());
        
        buttonPanel.add(insertButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }

    private void loadData() {
        
        try {
            connection =DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            DefaultTableModel model = new DefaultTableModel();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                model.addRow(row);
            }

            table.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showInsertForm() {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField[] fields = new JTextField[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            panel.add(new JLabel(table.getColumnName(i)));
            fields[i] = new JTextField();
            panel.add(fields[i]);
        }
        int result = JOptionPane.showConfirmDialog(this, panel, "Insert Record", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                StringBuilder values = new StringBuilder();
                for (JTextField field : fields) {
                    values.append("'").append(field.getText()).append("',");
                }
                values.setLength(values.length() - 1); // Remove last comma
                Statement statement = connection.createStatement();
                statement.executeUpdate("INSERT INTO " + tableName + " VALUES(" + values + ")");
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showUpdateForm() {
        String condition = JOptionPane.showInputDialog(this, "Enter condition for update (e.g., id=1):");
        if (condition != null) {
            JPanel panel = new JPanel(new GridLayout(0, 2));
            JTextField[] fields = new JTextField[table.getColumnCount()];
            for (int i = 0; i < table.getColumnCount(); i++) {
                panel.add(new JLabel(table.getColumnName(i)));
                fields[i] = new JTextField();
                panel.add(fields[i]);
            }
            int result = JOptionPane.showConfirmDialog(this, panel, "Update Record", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    StringBuilder setClause = new StringBuilder();
                    for (int i = 0; i < fields.length; i++) {
                        if (!fields[i].getText().isEmpty()) {
                            setClause.append(table.getColumnName(i)).append("='").append(fields[i].getText()).append("',");
                        }
                    }
                    setClause.setLength(setClause.length() - 1); // Remove last comma
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE " + tableName + " SET " + setClause + " WHERE " + condition);
                    loadData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showDeleteForm() {
        String condition = JOptionPane.showInputDialog(this, "Enter condition for deletion (e.g., id=1):");
        if (condition != null) {
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate("DELETE FROM " + tableName + " WHERE " + condition);
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

