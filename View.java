package Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class View extends JFrame {
    private Controller controller;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public View() {
        setTitle("Electronic Voting System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 400));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createTitledPanel(createMenuPanel(), "Main Menu"), "Menu");
        mainPanel.add(createTitledPanel(createVoterRegistrationPanel(), "Voter Registration"), "Voter");
        mainPanel.add(createTitledPanel(createCandidateRegistrationPanel(), "Candidate Registration"), "Candidate");
        mainPanel.add(createTitledPanel(createVotePanel(), "Cast Vote"), "Vote");
        mainPanel.add(createTitledPanel(createResultsPanel(), "Election Results"), "Results");
        mainPanel.add(createTitledPanel(createAdminLoginPanel(), "Admin Login"), "AdminLogin");
        mainPanel.add(createTitledPanel(createAdminMenuPanel(), "Admin Dashboard"), "AdminMenu");
        mainPanel.add(createTitledPanel(createVotersPanel(), "Voter Information"), "Voters");
        mainPanel.add(createTitledPanel(createCandidatesPanel(), "Candidate Information"), "Candidates");
        mainPanel.add(createTitledPanel(createVoteHistoryPanel(), "Vote History"), "VoteHistory");
        mainPanel.add(createTitledPanel(createCandidatesViewPanel(), "View Candidates"), "CandidatesView");

        add(mainPanel);
        cardLayout.show(mainPanel, "Menu");
        setVisible(true);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private JPanel createTitledPanel(JPanel contentPanel, String title) {
        JPanel titledPanel = new JPanel(new BorderLayout());
        titledPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        titledPanel.add(headerPanel, BorderLayout.NORTH);
        titledPanel.add(contentPanel, BorderLayout.CENTER);
        return titledPanel;
    }

    private JButton createStyledButton(String text, boolean isExit) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(isExit ? new Color(220, 53, 69) : PRIMARY_COLOR);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(isExit ? new Color(220, 53, 69).brighter() : PRIMARY_COLOR.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(isExit ? new Color(220, 53, 69) : PRIMARY_COLOR);
            }
        });
        return button;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        String[] actions = {"Register Voter", "Cast Vote", "View Candidates",
                            "View Results", "Admin Login", "Exit"};
        for (String action : actions) {
            boolean isExit = action.equals("Exit");
            JButton button = createStyledButton(action, isExit);
            button.addActionListener(e -> {
                System.out.println("Button clicked: " + action); // Debug log
                if (controller == null) {
                    showMessage("Controller not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                switch (action) {
                    case "Register Voter":
                        cardLayout.show(mainPanel, "Voter");
                        break;
                    case "Cast Vote":
                        cardLayout.show(mainPanel, "Vote");
                        break;
                    case "View Candidates":
                        controller.refreshCandidatesView();
                        break;
                    case "View Results":
                        controller.refreshResults();
                        break;
                    case "Admin Login":
                        cardLayout.show(mainPanel, "AdminLogin");
                        break;
                    case "Exit":
                        System.exit(0);
                        break;
                }
            });
            panel.add(button, gbc);
        }
        return panel;
    }

    private JPanel createVoterRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField = new JTextField(20);
        JTextField idField = new JTextField(20);
        JButton submitBtn = createStyledButton("Register", false);
        JButton backBtn = createStyledButton("Back", false);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(LABEL_FONT);
        JLabel idLabel = new JLabel("ID Number (8 digits):");
        idLabel.setFont(LABEL_FONT);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(nameLabel, gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(idLabel, gbc);
        gbc.gridx = 1; panel.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, gbc);

        submitBtn.addActionListener(e -> {
            System.out.println("Voter Register button clicked"); // Debug log
            if (controller != null) {
                controller.registerVoter(nameField.getText(), idField.getText());
            } else {
                showMessage("Controller not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backBtn.addActionListener(e -> {
            System.out.println("Voter Back button clicked"); // Debug log
            nameField.setText("");
            idField.setText("");
            cardLayout.show(mainPanel, "Menu");
        });
        return panel;
    }

    private JPanel createCandidateRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField = new JTextField(20);
        JTextField idField = new JTextField(20);
        JTextField positionField = new JTextField(20);
        JButton submitBtn = createStyledButton("Register", false);
        JButton backBtn = createStyledButton("Back", false);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(LABEL_FONT);
        JLabel idLabel = new JLabel("ID Number (8 digits):");
        idLabel.setFont(LABEL_FONT);
        JLabel positionLabel = new JLabel("Position:");
        positionLabel.setFont(LABEL_FONT);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(nameLabel, gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(idLabel, gbc);
        gbc.gridx = 1; panel.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(positionLabel, gbc);
        gbc.gridx = 1; panel.add(positionField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, gbc);

        submitBtn.addActionListener(e -> {
            System.out.println("Candidate Register button clicked"); // Debug log
            if (controller != null) {
                controller.registerCandidate(nameField.getText(), idField.getText(), positionField.getText());
            } else {
                showMessage("Controller not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backBtn.addActionListener(e -> {
            System.out.println("Candidate Back button clicked"); // Debug log
            nameField.setText("");
            idField.setText("");
            positionField.setText("");
            cardLayout.show(mainPanel, "AdminMenu");
        });
        return panel;
    }

    private JPanel createVotePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField voterIdField = new JTextField(20);
        JTextField candidateIdField = new JTextField(20);
        JButton submitBtn = createStyledButton("Cast Vote", false);
        JButton backBtn = createStyledButton("Back", false);

        JLabel voterIdLabel = new JLabel("Voter ID:");
        voterIdLabel.setFont(LABEL_FONT);
        JLabel candidateIdLabel = new JLabel("Candidate ID:");
        candidateIdLabel.setFont(LABEL_FONT);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(voterIdLabel, gbc);
        gbc.gridx = 1; panel.add(voterIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(candidateIdLabel, gbc);
        gbc.gridx = 1; panel.add(candidateIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, gbc);

        submitBtn.addActionListener(e -> {
            System.out.println("Cast Vote button clicked"); // Debug log
            if (controller != null) {
                controller.castVote(voterIdField.getText(), candidateIdField.getText());
            } else {
                showMessage("Controller not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backBtn.addActionListener(e -> {
            System.out.println("Vote Back button clicked"); // Debug log
            voterIdField.setText("");
            candidateIdField.setText("");
            cardLayout.show(mainPanel, "Menu");
        });
        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextArea resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultsArea.setBackground(SECONDARY_COLOR);
        resultsArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        JButton refreshBtn = createStyledButton("Refresh", false);
        JButton backBtn = createStyledButton("Back", false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> {
            System.out.println("Results Refresh button clicked"); // Debug log
            if (controller != null) {
                controller.refreshResults();
            } else {
                showMessage("Controller not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backBtn.addActionListener(e -> {
            System.out.println("Results Back button clicked"); // Debug log
            cardLayout.show(mainPanel, "Menu");
        });
        return panel;
    }

    private JPanel createAdminLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginBtn = createStyledButton("Login", false);
        JButton backBtn = createStyledButton("Back", false);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(LABEL_FONT);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(LABEL_FONT);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(usernameLabel, gbc);
        gbc.gridx = 1; panel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(passwordLabel, gbc);
        gbc.gridx = 1; panel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginBtn);
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, gbc);

        loginBtn.addActionListener(e -> {
            System.out.println("Admin Login button clicked"); // Debug log
            if (controller != null) {
                controller.adminLogin(usernameField.getText(), new String(passwordField.getPassword()));
            } else {
                showMessage("Controller not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backBtn.addActionListener(e -> {
            System.out.println("Admin Login Back button clicked"); // Debug log
            usernameField.setText("");
            passwordField.setText("");
            cardLayout.show(mainPanel, "Menu");
        });
        return panel;
    }

    private JPanel createAdminMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        String[] actions = {"Register Candidate", "View Voter Information", "View Candidate Information",
                            "View Vote History", "View Election Results", "Back to Main Menu"};
        for (String action : actions) {
            boolean isBack = action.equals("Back to Main Menu");
            JButton button = createStyledButton(action, isBack);
            button.addActionListener(e -> {
                System.out.println("Admin button clicked: " + action); // Debug log
                if (controller == null) {
                    showMessage("Controller not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                switch (action) {
                    case "Register Candidate":
                        cardLayout.show(mainPanel, "Candidate");
                        break;
                    case "View Voter Information":
                        controller.refreshVoters();
                        break;
                    case "View Candidate Information":
                        controller.refreshCandidates();
                        break;
                    case "View Vote History":
                        controller.refreshVoteHistory();
                        break;
                    case "View Election Results":
                        controller.refreshResults();
                        break;
                    case "Back to Main Menu":
                        cardLayout.show(mainPanel, "Menu");
                        break;
                }
            });
            panel.add(button, gbc);
        }
        return panel;
    }

    private JPanel createVotersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID Number", "Name", "Person ID", "Has Voted"}, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setGridColor(new Color(200, 200, 200));
        table.setBackground(SECONDARY_COLOR);

        JScrollPane scrollPane = new JScrollPane(table);
        JButton refreshBtn = createStyledButton("Refresh", false);
        JButton backBtn = createStyledButton("Back", false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> {
            System.out.println("Voters Refresh button clicked"); // Debug log
            if (controller != null) {
                controller.refreshVoters();
            } else {
                showMessage("Controller not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backBtn.addActionListener(e -> {
            System.out.println("Voters Back button clicked"); // Debug log
            cardLayout.show(mainPanel, "AdminMenu");
        });
        return panel;
    }

    private JPanel createCandidatesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID Number", "Name", "Person ID", "Position", "Vote Count"}, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setGridColor(new Color(200, 200, 200));
        table.setBackground(SECONDARY_COLOR);

        JScrollPane scrollPane = new JScrollPane(table);
        JButton refreshBtn = createStyledButton("Refresh", false);
        JButton backBtn = createStyledButton("Back", false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> {
            System.out.println("Candidates Refresh button clicked"); // Debug log
            if (controller != null) {
                controller.refreshCandidates();
            } else {
                showMessage("Controller not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backBtn.addActionListener(e -> {
            System.out.println("Candidates Back button clicked"); // Debug log
            cardLayout.show(mainPanel, "AdminMenu");
        });
        return panel;
    }

    private JPanel createVoteHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Voter ID", "Voter Name", "Candidate ID", "Candidate Name", "Position", "Vote Time"}, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setGridColor(new Color(200, 200, 200));
        table.setBackground(SECONDARY_COLOR);

        JScrollPane scrollPane = new JScrollPane(table);
        JButton refreshBtn = createStyledButton("Refresh", false);
        JButton backBtn = createStyledButton("Back", false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> {
            System.out.println("Vote History Refresh button clicked"); // Debug log
            if (controller != null) {
                controller.refreshVoteHistory();
            } else {
                showMessage("Controller not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backBtn.addActionListener(e -> {
            System.out.println("Vote History Back button clicked"); // Debug log
            cardLayout.show(mainPanel, "AdminMenu");
        });
        return panel;
    }

    private JPanel createCandidatesViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID Number", "Name", "Person ID", "Position"}, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setGridColor(new Color(200, 200, 200));
        table.setBackground(SECONDARY_COLOR);
        table.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(table);
        JButton refreshBtn = createStyledButton("Refresh", false);
        JButton backBtn = createStyledButton("Back", false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> {
            System.out.println("Candidates View Refresh button clicked"); // Debug log
            if (controller != null) {
                controller.refreshCandidatesView();
            } else {
                showMessage("Controller not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backBtn.addActionListener(e -> {
            System.out.println("Candidates View Back button clicked"); // Debug log
            cardLayout.show(mainPanel, "Menu");
        });
        return panel;
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void showAdminMenu() {
        cardLayout.show(mainPanel, "AdminMenu");
    }

    public void updateResults(Map<String, List<Map<String, Object>>> results) {
        System.out.println("Updating Results panel"); // Debug log
        JPanel titledPanel = (JPanel) mainPanel.getComponent(4); // Results panel
        JPanel contentPanel = (JPanel) titledPanel.getComponent(1); // Content panel
        Component centerComponent = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (!(centerComponent instanceof JScrollPane)) {
            System.err.println("Error: Expected JScrollPane in Results panel, found " + centerComponent.getClass());
            return;
        }
        JScrollPane scrollPane = (JScrollPane) centerComponent;
        JTextArea resultsArea = (JTextArea) scrollPane.getViewport().getView();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Map<String, Object>>> entry : results.entrySet()) {
            sb.append("Results for ").append(entry.getKey()).append(":\n");
            sb.append("----------------------------------------\n");
            for (Map<String, Object> candidate : entry.getValue()) {
                sb.append(String.format("%-30s: %d votes\n", candidate.get("name"), candidate.get("votes")));
            }
            sb.append("\n");
        }
        resultsArea.setText(sb.toString());
        cardLayout.show(mainPanel, "Results");
    }

    public void updateVoters(List<Model.Voter> voters) {
        System.out.println("Updating Voters panel"); // Debug log
        JPanel titledPanel = (JPanel) mainPanel.getComponent(7); // Voters panel
        JPanel contentPanel = (JPanel) titledPanel.getComponent(1); // Content panel
        Component centerComponent = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (!(centerComponent instanceof JScrollPane)) {
            System.err.println("Error: Expected JScrollPane in Voters panel, found " + centerComponent.getClass());
            return;
        }
        JScrollPane scrollPane = (JScrollPane) centerComponent;
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Model.Voter voter : voters) {
            model.addRow(new Object[]{voter.getIdNumber(), voter.getName(), voter.getPersonId(), voter.hasVoted() ? "Yes" : "No"});
        }
        cardLayout.show(mainPanel, "Voters");
    }

    public void updateCandidates(List<Model.Candidate> candidates) {
        System.out.println("Updating Candidates panel"); // Debug log
        JPanel titledPanel = (JPanel) mainPanel.getComponent(8); // Candidates panel
        JPanel contentPanel = (JPanel) titledPanel.getComponent(1); // Content panel
        Component centerComponent = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (!(centerComponent instanceof JScrollPane)) {
            System.err.println("Error: Expected JScrollPane in Candidates panel, found " + centerComponent.getClass());
            return;
        }
        JScrollPane scrollPane = (JScrollPane) centerComponent;
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Model.Candidate candidate : candidates) {
            model.addRow(new Object[]{candidate.getIdNumber(), candidate.getName(), candidate.getPersonId(),
                                     candidate.getPosition(), candidate.getVoteCount()});
        }
        cardLayout.show(mainPanel, "Candidates");
    }

    public void updateVoteHistory(List<Map<String, String>> history) {
        System.out.println("Updating Vote History panel"); // Debug log
        JPanel titledPanel = (JPanel) mainPanel.getComponent(9); // VoteHistory panel
        JPanel contentPanel = (JPanel) titledPanel.getComponent(1); // Content panel
        Component centerComponent = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (!(centerComponent instanceof JScrollPane)) {
            System.err.println("Error: Expected JScrollPane in VoteHistory panel, found " + centerComponent.getClass());
            return;
        }
        JScrollPane scrollPane = (JScrollPane) centerComponent;
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Map<String, String> vote : history) {
            model.addRow(new Object[]{vote.get("voter_id"), vote.get("voter_name"), vote.get("candidate_id"),
                                     vote.get("candidate_name"), vote.get("position"), vote.get("vote_time")});
        }
        cardLayout.show(mainPanel, "VoteHistory");
    }

    public void updateCandidatesView(List<Model.Candidate> candidates) {
        System.out.println("Updating Candidates View panel"); // Debug log
        JPanel titledPanel = (JPanel) mainPanel.getComponent(10); // CandidatesView panel
        JPanel contentPanel = (JPanel) titledPanel.getComponent(1); // Content panel
        Component centerComponent = ((BorderLayout) contentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (!(centerComponent instanceof JScrollPane)) {
            System.err.println("Error: Expected JScrollPane in CandidatesView panel, found " + centerComponent.getClass());
            return;
        }
        JScrollPane scrollPane = (JScrollPane) centerComponent;
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Model.Candidate candidate : candidates) {
            model.addRow(new Object[]{candidate.getIdNumber(), candidate.getName(), candidate.getPersonId(), candidate.getPosition()});
        }
        cardLayout.show(mainPanel, "CandidatesView");
    }
}