package Main;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

public class Model {
    private static class Person {
        protected String name;
        protected String idNumber;
        protected String personId;

        public Person(String name, String idNumber) {
            this.name = name;
            this.idNumber = idNumber;
            this.personId = UUID.randomUUID().toString();
        }

        public String getName() { return name; }
        public String getIdNumber() { return idNumber; }
        public String getPersonId() { return personId; }
    }

    public static class Voter extends Person {
        private boolean hasVoted;

        public Voter(String name, String idNumber) {
            super(name, idNumber);
            this.hasVoted = false;
        }

        public boolean hasVoted() { return hasVoted; }
        public void markVoted() { this.hasVoted = true; }
    }

    public static class Candidate extends Person {
        private String position;
        private int voteCount;

        public Candidate(String name, String idNumber, String position) {
            super(name, idNumber);
            this.position = position;
            this.voteCount = 0;
        }

        public String getPosition() { return position; }
        public int getVoteCount() { return voteCount; }
        public void addVote() { this.voteCount++; }
        public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    }

    private final String url;
    private final String username;
    private final String password;
    private final Map<String, String> adminCredentials;

    public Model(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.adminCredentials = new HashMap<>();
        adminCredentials.put("admin", "admin123");
    }

    public void registerVoter(String name, String idNumber) throws VotingException {
        if (name.trim().isEmpty()) {
            throw new VotingException("Name cannot be empty");
        }
        if (!Pattern.matches("\\d{8}", idNumber)) {
            throw new VotingException("Invalid ID number format (must be 8 digits)");
        }
        if (getVoter(idNumber) != null) {
            throw new VotingException("Voter already registered");
        }
        Voter voter = new Voter(name, idNumber);
        saveVoter(voter);
    }

    public void registerCandidate(String name, String idNumber, String position) throws VotingException {
        if (name.trim().isEmpty() || position.trim().isEmpty()) {
            throw new VotingException("Name and position cannot be empty");
        }
        if (!Pattern.matches("\\d{8}", idNumber)) {
            throw new VotingException("Invalid ID number format (must be 8 digits)");
        }
        if (getCandidate(idNumber) != null) {
            throw new VotingException("Candidate already registered");
        }
        Candidate candidate = new Candidate(name, idNumber, position);
        saveCandidate(candidate);
    }

    public void castVote(String voterId, String candidateId) throws VotingException {
        Voter voter = getVoter(voterId);
        Candidate candidate = getCandidate(candidateId);

        if (voter == null || candidate == null) {
            throw new VotingException("Invalid voter or candidate ID");
        }
        if (voter.hasVoted()) {
            throw new VotingException("Voter has already voted");
        }

        voter.markVoted();
        candidate.addVote();
        saveVote(voterId, candidateId);
        updateVoter(voter);
        updateCandidate(candidate);
    }

    public Map<String, List<Map<String, Object>>> getResults() {
        Map<String, List<Map<String, Object>>> results = new HashMap<>();
        for (Candidate candidate : getAllCandidates()) {
            results.computeIfAbsent(candidate.getPosition(), k -> new ArrayList<>());
            Map<String, Object> candidateData = new HashMap<>();
            candidateData.put("name", candidate.getName());
            candidateData.put("votes", candidate.getVoteCount());
            results.get(candidate.getPosition()).add(candidateData);
        }
        return results;
    }

    public boolean authenticateAdmin(String username, String password) {
        return adminCredentials.getOrDefault(username, "").equals(password);
    }

    public List<Map<String, String>> getVoteHistory() {
        List<Map<String, String>> history = new ArrayList<>();
        String sql = "SELECT v.voter_id, v.candidate_id, v.vote_time, " +
                     "vtr.name AS voter_name, c.name AS candidate_name, c.position " +
                     "FROM votes v " +
                     "JOIN voters vtr ON v.voter_id = vtr.id_number " +
                     "JOIN candidates c ON v.candidate_id = c.id_number " +
                     "ORDER BY v.vote_time DESC";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, String> vote = new HashMap<>();
                vote.put("voter_id", rs.getString("voter_id"));
                vote.put("voter_name", rs.getString("voter_name"));
                vote.put("candidate_id", rs.getString("candidate_id"));
                vote.put("candidate_name", rs.getString("candidate_name"));
                vote.put("position", rs.getString("position"));
                vote.put("vote_time", rs.getTimestamp("vote_time").toString());
                history.add(vote);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve vote history: " + e.getMessage());
        }
        return history;
    }

    public List<Voter> getAllVoters() {
        List<Voter> voters = new ArrayList<>();
        String sql = "SELECT * FROM voters";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Voter voter = new Voter(rs.getString("name"), rs.getString("id_number"));
                if (rs.getBoolean("has_voted")) {
                    voter.markVoted();
                }
                voters.add(voter);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve voters: " + e.getMessage());
        }
        return voters;
    }

    public List<Candidate> getAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String sql = "SELECT * FROM candidates";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Candidate candidate = new Candidate(
                        rs.getString("name"),
                        rs.getString("id_number"),
                        rs.getString("position"));
                candidate.setVoteCount(rs.getInt("vote_count"));
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve candidates: " + e.getMessage());
        }
        return candidates;
    }

    private void saveVoter(Voter voter) {
        String sql = "INSERT INTO voters (id_number, name, person_id, has_voted) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, voter.getIdNumber());
            pstmt.setString(2, voter.getName());
            pstmt.setString(3, voter.getPersonId());
            pstmt.setBoolean(4, voter.hasVoted());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save voter: " + e.getMessage());
        }
    }

    private void saveCandidate(Candidate candidate) {
        String sql = "INSERT INTO candidates (id_number, name, person_id, position, vote_count) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, candidate.getIdNumber());
            pstmt.setString(2, candidate.getName());
            pstmt.setString(3, candidate.getPersonId());
            pstmt.setString(4, candidate.getPosition());
            pstmt.setInt(5, candidate.getVoteCount());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save candidate: " + e.getMessage());
        }
    }

    private Voter getVoter(String idNumber) {
        String sql = "SELECT * FROM voters WHERE id_number = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Voter voter = new Voter(rs.getString("name"), rs.getString("id_number"));
                if (rs.getBoolean("has_voted")) {
                    voter.markVoted();
                }
                return voter;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve voter: " + e.getMessage());
        }
    }

    private Candidate getCandidate(String idNumber) {
        String sql = "SELECT * FROM candidates WHERE id_number = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Candidate candidate = new Candidate(
                        rs.getString("name"),
                        rs.getString("id_number"),
                        rs.getString("position"));
                candidate.setVoteCount(rs.getInt("vote_count"));
                return candidate;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve candidate: " + e.getMessage());
        }
    }

    private void updateVoter(Voter voter) {
        String sql = "UPDATE voters SET has_voted = ? WHERE id_number = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, voter.hasVoted());
            pstmt.setString(2, voter.getIdNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update voter: " + e.getMessage());
        }
    }

    private void updateCandidate(Candidate candidate) {
        String sql = "UPDATE candidates SET vote_count = ? WHERE id_number = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, candidate.getVoteCount());
            pstmt.setString(2, candidate.getIdNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update candidate: " + e.getMessage());
        }
    }

    private void saveVote(String voterId, String candidateId) {
        String sql = "INSERT INTO votes (voter_id, candidate_id) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, voterId);
            pstmt.setString(2, candidateId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save vote: " + e.getMessage());
        }
    }
}