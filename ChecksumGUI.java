import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.MessageDigest;
import javax.swing.*;

public class ChecksumGUI extends JFrame {
    private JLabel checksumLabel;
    private JRadioButton md5RadioButton;
    private JRadioButton sha1RadioButton;
    private JRadioButton sha256RadioButton;

    public ChecksumGUI() {
        setTitle("Checksum Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Create GUI components
        checksumLabel = new JLabel("Checksum: ");

        md5RadioButton = new JRadioButton("MD5");
        sha1RadioButton = new JRadioButton("SHA-1");
        sha256RadioButton = new JRadioButton("SHA-256");

        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateChecksum();
            }
        });

        JPanel algorithmPanel = new JPanel();
        algorithmPanel.add(md5RadioButton);
        algorithmPanel.add(sha1RadioButton);
        algorithmPanel.add(sha256RadioButton);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(md5RadioButton);
        buttonGroup.add(sha1RadioButton);
        buttonGroup.add(sha256RadioButton);

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(algorithmPanel);
        panel.add(checksumLabel);
        panel.add(calculateButton);

        add(panel);
    }

    private void calculateChecksum() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            String algorithm;
            if (md5RadioButton.isSelected()) {
                algorithm = "MD5";
            } else if (sha1RadioButton.isSelected()) {
                algorithm = "SHA-1";
            } else {
                algorithm = "SHA-256";
            }

            String checksum = calculateChecksum(file, algorithm);
            checksumLabel.setText("Checksum: " + checksum);
        }
    }

    private String calculateChecksum(File file, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            FileInputStream fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, bytesRead);
            }

            byte[] mdBytes = md.digest();
            StringBuilder sb = new StringBuilder();

            for (byte mdByte : mdBytes) {
                sb.append(Integer.toString((mdByte & 0xff) + 0x100, 16).substring(1));
            }

            fis.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChecksumGUI gui = new ChecksumGUI();
            gui.setVisible(true);
        });
    }
}
