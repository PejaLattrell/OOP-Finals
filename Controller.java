package Main;

import javax.swing.*;

public class Controller {
    private final Model model;
    private final View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public void registerVoter(String name, String idNumber) {
        try {
            model.registerVoter(name, idNumber);
            view.showMessage("Voter registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (VotingException e) {
            view.showMessage(e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void registerCandidate(String name, String idNumber, String position) {
        try {
            model.registerCandidate(name, idNumber, position);
            view.showMessage("Candidate registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (VotingException e) {
            view.showMessage(e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void castVote(String voterId, String candidateId) {
        try {
            model.castVote(voterId, candidateId);
            view.showMessage("Vote cast successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (VotingException e) {
            view.showMessage(e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void adminLogin(String username, String password) {
        if (model.authenticateAdmin(username, password)) {
            view.showAdminMenu();
            view.showMessage("Admin login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            view.showMessage("Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshResults() {
        view.updateResults(model.getResults());
    }

    public void refreshVoters() {
        view.updateVoters(model.getAllVoters());
    }

    public void refreshCandidates() {
        view.updateCandidates(model.getAllCandidates());
    }

    public void refreshVoteHistory() {
        view.updateVoteHistory(model.getVoteHistory());
    }

    public void refreshCandidatesView() {
        view.updateCandidatesView(model.getAllCandidates());
    }
}