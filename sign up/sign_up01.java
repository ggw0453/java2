import javax.swing.*;

import java.awt.*; 

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class sign_up01 {
    public static void main(String[] args) {
        JFrame frame = new JFrame("회원 가입");
        frame.setSize(600,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(4,2));

        JLabel nameLabel = new JLabel("이름: ");
        JTextField nameField = new JTextField();

        JLabel idLabel = new JLabel("아이디: ");
        JTextField idField = new JTextField();

        JLabel passwordLabel = new JLabel("비밀번호: ");
        JPasswordField passwordField = new JPasswordField();
        
        JButton loginButton = new JButton("로그인");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginWindow();
            }
        });

        JButton registerButton = new JButton("가입하기");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String id = idField.getText();
                String password = new String(passwordField.getPassword());
                
                if (!isValidInput(name, id, password)) {
                    JOptionPane.showMessageDialog(frame, "입력이 유효하지 않습니다!");
                    return;
                }
                
                if (isIdDuplicated(id)) {
                    JOptionPane.showMessageDialog(frame, "이미 존재하는 아이디입니다!");
                    return;
                }
    
                String hashedPassword = hashPassword(password);
                Member member = new Member(name, id, hashedPassword);
                saveMemberToFile(member);
                JOptionPane.showMessageDialog(frame, "가입 완료!");
            }
        });


        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(idLabel);
        panel.add(idField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void showLoginWindow(){
        JFrame loginFrame = new JFrame("로그인");
        loginFrame.setSize(600,400);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(null);

        JLabel idLabel = new JLabel("아이디:");
        idLabel.setBounds(150,110,80,25);
        loginFrame.add(idLabel);

        JTextField loginIdField = new JTextField(20);
        loginIdField.setBounds(210, 100, 160, 45);
        loginFrame.add(loginIdField);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setBounds(150, 160, 80, 25);
        loginFrame.add(passwordLabel);

        JPasswordField loginPasswordField = new JPasswordField(20);
        loginPasswordField.setBounds(210, 150, 160, 45);
        loginFrame.add(loginPasswordField);

        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(390, 110, 80, 55);
        loginFrame.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = loginIdField.getText();
                String password = new String(loginPasswordField.getPassword());
                
                if (validateLogin(id, password)) {
                    if (id.equals("ggw0453")&&password.equals("kimoon2121")) {
                        JOptionPane.showMessageDialog(loginFrame, "관리자님 어서오십시오");
                    }
                    JOptionPane.showMessageDialog(loginFrame, "로그인되었습니다");
                    loginFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "다시 입력해 주세요");
                }
            }
        });

        loginFrame.setVisible(true);
    }

    private static boolean validateLogin(String id, String password) {
        String hashedPassword = hashPassword(password);
        try (BufferedReader br = new BufferedReader(new FileReader("members.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("아이디: " + id + ",") && line.contains("비밀번호: " + hashedPassword)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void saveMemberToFile(Member member) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("members.txt", true))) {
            bw.write(member.toString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isIdDuplicated(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader("members.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("아이디: " + id + ",")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isValidInput(String name, String id, String password) {
        if (name.isEmpty() || id.isEmpty() || password.isEmpty()) {
            return false;
        }
     
        if (!id.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            return false;
        }
        return true;
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class Member {
        private String name;
        private String id;
        private String password;
        
        public Member(String name, String id, String password) {
            this.name = name;
            this.id = id;
            this.password = password;
        }
        
        @Override
        public String toString() {
            return "이름: " + name + ", 아이디: " + id + ", 비밀번호: " + password;
        }
    }
}