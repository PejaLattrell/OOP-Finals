package Main;

import javax.swing.*;

public class VotingSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String url = "jdbc:mysql://localhost:3306/voting_system";
            String username = "root";
            String password = "Ee2ybgfi";
            Model model = new Model(url, username, password);
            View view = new View();
            Controller controller = new Controller(model, view);
            view.setController(controller);
        });
    }
}