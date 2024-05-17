package net.docn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class FileHashChecker extends JFrame {
    private JTextField filePathField;  // 文件路径输入框
    private JTextArea hashTextArea;    // 计算出的哈希值显示区域
    private JTextField inputHashField; // 用户输入的哈希值输入框
    private JComboBox<String> hashTypeComboBox; // 哈希类型选择框
    private JLabel resultLabel;        // 比较结果标签

    public FileHashChecker() {
        setTitle("文件哈希校验工具");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        // 文件路径标签和输入框
        JLabel filePathLabel = new JLabel("文件路径:");
        filePathField = new JTextField();

        // 哈希类型标签和选择框
        JLabel hashTypeLabel = new JLabel("哈希类型:");
        String[] hashTypes = {"MD5", "SHA-1", "SHA-256", "SHA-512"};
        hashTypeComboBox = new JComboBox<>(hashTypes);

        // 浏览按钮
        JButton browseButton = new JButton("浏览");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        // 计算哈希按钮
        JButton calculateButton = new JButton("计算哈希");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText().trim();
                // 去除路径头尾的双引号
                if (filePath.startsWith("\"") && filePath.endsWith("\"")) {
                    filePath = filePath.substring(1, filePath.length() - 1);
                }
                String hashType = (String) hashTypeComboBox.getSelectedItem();
                if (filePath.isEmpty() || hashType == null) {
                    JOptionPane.showMessageDialog(null, "请选择文件和哈希类型。");
                    return;
                }
                try {
                    String hash = calculateFileHash(filePath, hashType);
                    hashTextArea.setText(hash);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "计算哈希时出错: " + ex.getMessage());
                }
            }
        });

        // 计算出的哈希值标签和显示区域
        JLabel hashLabel = new JLabel("计算出的哈希值:");
        hashTextArea = new JTextArea();
        hashTextArea.setEditable(false);

        // 输入的哈希值标签和输入框
        JLabel inputHashLabel = new JLabel("输入的哈希值:");
        inputHashField = new JTextField();

        // 比较哈希按钮
        JButton compareButton = new JButton("比较哈希");
        compareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String calculatedHash = hashTextArea.getText().trim();
                String inputHash = inputHashField.getText().trim();
                if (calculatedHash.equalsIgnoreCase(inputHash)) {
                    resultLabel.setText("哈希值匹配！");
                } else {
                    resultLabel.setText("哈希值不匹配。");
                }
            }
        });

        resultLabel = new JLabel("");

        // 将所有组件添加到面板
        panel.add(filePathLabel);
        panel.add(filePathField);
        panel.add(hashTypeLabel);
        panel.add(hashTypeComboBox);
        panel.add(browseButton);
        panel.add(calculateButton);
        panel.add(hashLabel);
        panel.add(new JScrollPane(hashTextArea));
        panel.add(inputHashLabel);
        panel.add(inputHashField);
        panel.add(compareButton);
        panel.add(resultLabel);

        add(panel);
    }

    /**
     * 计算文件的哈希值
     * @param filePath 文件路径
     * @param hashType 哈希类型
     * @return 哈希值
     * @throws Exception 计算哈希时可能抛出的异常
     */
    private String calculateFileHash(String filePath, String hashType) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(hashType);
        FileInputStream fis = new FileInputStream(new File(filePath));
        byte[] byteArray = new byte[1024];
        int bytesCount;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();

        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FileHashChecker().setVisible(true);
            }
        });
    }
}
