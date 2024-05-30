import javax.swing.*; // 그래픽 사용자 인터페이스(GUI) 컴포넌트를 위한 라이브러리입니다.
import java.awt.*; // GUI 디자인과 이벤트 처리를 위한 클래스들을 포함하고 있습니다.
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MembershipForm01 {
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("회원 가입");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // JFrame: 창을 나타내는 컴포넌트입니다. 제목이 "회원 가입"인 창을 생성합니다.
        // setSize(300, 200): 창의 크기를 가로 300, 세로 200으로 설정합니다.
        // setDefaultCloseOperation(...): 창의 '닫기' 버튼을 클릭하면 프로그램이 종료되도록 설정합니다.
        
        JPanel panel = new JPanel(new GridLayout(4, 2));
        // JPanel: 컴포넌트들을 담을 수 있는 컨테이너입니다.
        // new GridLayout(4, 2): 4행 2열의 그리드 레이아웃을 생성합니다.
        
        JLabel nameLabel = new JLabel("이름: ");
        JTextField nameField = new JTextField();
        // JLabel: 텍스트 라벨을 표시합니다.
        // JTextField: 사용자가 텍스트를 입력할 수 있는 필드를 생성합니다.
        
        JLabel idLabel = new JLabel("아이디: ");
        JTextField idField = new JTextField();
        
        JLabel passwordLabel = new JLabel("비밀번호: ");
        JPasswordField passwordField = new JPasswordField();
        // JPasswordField: 비밀번호를 입력 받을 때 사용되는 필드입니다. 입력 내용이 *로 가려져 보입니다.
        
        JButton loginButton = new JButton("로그인");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginWindow(); // 로그인 창을 보여줍니다.
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
        // JButton: 클릭 가능한 버튼을 생성합니다.
        // addActionListener(...): 버튼 클릭 시 수행할 동작을 정의합니다. 사용자가 입력한 정보를 가져와 파일에 저장하고 "가입 완료!"라는 메시지를 표시합니다.
  
        
        
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(idLabel);
        panel.add(idField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);       // 로그인 버튼 추가
        panel.add(registerButton);
        
        frame.add(panel);
        frame.setVisible(true);
        // 마지막으로 frame.setVisible(true);를 통해 프레임(창)을 화면에 표시합니다.
    }
    
    private static void showLoginWindow() {
        JFrame loginFrame = new JFrame("로그인");
        loginFrame.setSize(380, 250);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(null);
        
        JLabel idLabel = new JLabel("아이디:");
        idLabel.setBounds(10, 20, 80, 25);
        loginFrame.add(idLabel);
        
        JTextField loginIdField = new JTextField(20);
        loginIdField.setBounds(100, 20, 160, 25);
        loginFrame.add(loginIdField);
        
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setBounds(10, 50, 80, 25);
        loginFrame.add(passwordLabel);
        
        JPasswordField loginPasswordField = new JPasswordField(20);
        loginPasswordField.setBounds(100, 50, 160, 25);
        loginFrame.add(loginPasswordField);
        
        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(270, 20, 80, 55);
        loginFrame.add(loginButton);
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = loginIdField.getText();
                String password = new String(loginPasswordField.getPassword());
                
                if (validateLogin(id, password)) {
                    JOptionPane.showMessageDialog(loginFrame, "로그인 성공!");
                    loginFrame.dispose();  // 로그인 성공 시 로그인 창 닫기
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "로그인 실패! 아이디 또는 비밀번호를 확인해주세요.");
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
        // BufferedWriter: 텍스트 파일에 문자를 쓰기 위한 클래스입니다.
        // FileWriter("members.txt", true): members.txt 파일에 쓰기를 위한 객체를 생성합니다. true 옵션은 파일 끝에 추가(append)하는 모드를 나타냅니다.
        // bw.write(...): 파일에 문자열을 씁니다.
        // bw.newLine(): 새 줄을 추가합니다.
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
            return false;  // 필드가 비어 있는지 확인
        }
     
        if (!id.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            return false;  // ID는 알파벳으로 시작하고, 알파벳 및 숫자만 포함
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
        // 회원의 정보(이름, 아이디, 비밀번호)를 저장하는 클래스입니다.
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