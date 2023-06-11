import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

public class ChecksumGUI extends JFrame {
    private JLabel fileLabel;
    private JTextArea checksumTextArea;
    private JComboBox<String> algorithmComboBox;
    private Map<String, MessageDigest> algorithms;

    public ChecksumGUI() {
        setTitle("Checksum Calculator");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        fileLabel = new JLabel("No file selected");
        mainPanel.add(fileLabel, BorderLayout.NORTH);

        JPanel algorithmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        algorithmComboBox = new JComboBox<>();
        algorithmComboBox.addItem("MD5");
        algorithmComboBox.addItem("SHA-256");
        algorithmComboBox.addItem("CRC32");
        algorithmPanel.add(algorithmComboBox);
        mainPanel.add(algorithmPanel, BorderLayout.CENTER);

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseFile();
            }
        });
        mainPanel.add(browseButton, BorderLayout.SOUTH);

        add(mainPanel);

        algorithms = new HashMap<>();
        try {
            algorithms.put("MD5", MessageDigest.getInstance("MD5"));
            algorithms.put("SHA-256", MessageDigest.getInstance("SHA-256"));
        } catch (NoSuchAlgorithmException e) {
            showMessage("Error initializing algorithms");
        }
    }

    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            fileLabel.setText(file.getAbsolutePath());

            try {
                FileInputStream inputStream = new FileInputStream(file);
                calculateChecksum(inputStream);
                inputStream.close();
            } catch (IOException ex) {
                showMessage("Error reading file");
            }
        }
    }

    private void calculateChecksum(FileInputStream inputStream) throws IOException {
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();

        if (selectedAlgorithm.equals("CRC32")) {
            calculateCRC32Checksum(inputStream);
        } else {
            MessageDigest md = algorithms.get(selectedAlgorithm);

            if (md != null) {
                calculateMessageDigestChecksum(md, inputStream);
            } else {
                showMessage("Invalid algorithm selected");
            }
        }
    }

    private void calculateCRC32Checksum(FileInputStream inputStream) throws IOException {
        CRC32 crc32 = new CRC32();

        byte[] dataBytes = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(dataBytes)) != -1) {
            crc32.update(dataBytes, 0, bytesRead);
        }

        long crc32Checksum = crc32.getValue();
        displayChecksum("CRC32 Checksum", String.valueOf(crc32Checksum));
    }

    private void calculateMessageDigestChecksum(MessageDigest md, FileInputStream inputStream) throws IOException {
        byte[] dataBytes = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, bytesRead);
        }

        byte[] checksumBytes = md.digest();
        String checksum = bytesToHex(checksumBytes);
        displayChecksum(algorithmComboBox.getSelectedItem() + " Checksum", checksum);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    private void displayChecksum(String algorithm, String checksum) {
        checksumTextArea.setText("");
        checksumTextArea.append(algorithm + ": " + checksum);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException |
                    IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            ChecksumGUI checksumGUI = new ChecksumGUI();
            checksumGUI.setVisible(true);
        });
    }
}
