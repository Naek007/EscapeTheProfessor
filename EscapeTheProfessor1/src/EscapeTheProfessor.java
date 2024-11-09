import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EscapeTheProfessor extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public EscapeTheProfessor() {
        setTitle("Escape the Professor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // สร้างหน้าแรก (Start Screen)
        JPanel startScreen = createStartScreen();
        mainPanel.add(startScreen, "Start");
        
        // สร้างหน้าเนื้อเรื่อง (Story Screen)
        JPanel storyScreen = createStoryScreen();
        mainPanel.add(storyScreen, "Story");

        // สร้างหน้า Chapter I (Chapter Screen)
        JPanel chapterScreen = createChapterScreen();
        mainPanel.add(chapterScreen, "Chapter");

        // แก้ไขการสร้าง GamePanel โดยส่งอ้างอิง parentFrame (this) ให้กับ GamePanel
        GamePanel gamePanel = new GamePanel(this);
        mainPanel.add(gamePanel, "Game");

        add(mainPanel);
        cardLayout.show(mainPanel, "Start"); // เริ่มต้นที่หน้าจอ Start
    }


    public void showWinScreen() {
        JPanel winScreen = createWinScreen();
        mainPanel.add(winScreen, "Win");
        cardLayout.show(mainPanel, "Win");
    }

    // สร้างหน้าจอ Win Screen
    private JPanel createWinScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        JLabel winLabel = new JLabel("You Win", SwingConstants.CENTER);
        winLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        winLabel.setForeground(Color.YELLOW);

        panel.add(winLabel, BorderLayout.CENTER);
        return panel;
    }
    public void showGameOverScreen() {
        JPanel gameOverScreen = new JPanel(new BorderLayout());
        gameOverScreen.setBackground(Color.BLACK);

        JLabel gameOverLabel = new JLabel("Game Over", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        gameOverLabel.setForeground(Color.RED);
        gameOverScreen.add(gameOverLabel, BorderLayout.CENTER);

        mainPanel.add(gameOverScreen, "GameOver");
        cardLayout.show(mainPanel, "GameOver");
    }


    private JPanel createStartScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("Escape The Professor", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        titleLabel.setForeground(Color.RED);

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Monospaced", Font.BOLD, 24));
        startButton.setBackground(Color.WHITE);
        startButton.setForeground(Color.BLACK);
        startButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Story");
            }
        });

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStoryScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        JLabel storyLabel = new JLabel(
            "<html><div style='text-align: center; color: red;'>The player takes on the role of a student working on a project late into the night at the CS faculty and encounters a ghost professor who is chasing the player. Players must find a way out of the building to win the game by using various items in the game to stay safe from the Professor ghost according to the map. There will be a cabinet to sneak in. The Professor ghost will appear in spots. If caught by a ghost Professor, the game will end in a loss.</div></html>",
            SwingConstants.CENTER
        );
        storyLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));

        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Monospaced", Font.BOLD, 18));
        nextButton.setBackground(Color.WHITE);
        nextButton.setForeground(Color.BLACK);
        nextButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // เมื่อกดปุ่ม Next ให้ไปที่หน้าจอ Chapter
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Chapter");
            }
        });

        panel.add(storyLabel, BorderLayout.CENTER);
        panel.add(nextButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createChapterScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        // เพิ่มข้อความ Chapter I
        JLabel chapterLabel = new JLabel("Chapter I", SwingConstants.CENTER);
        chapterLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        chapterLabel.setForeground(Color.RED);
        panel.add(chapterLabel, BorderLayout.CENTER);

        // ปุ่ม Next สำหรับไปยังหน้าเกมหลัก
        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Monospaced", Font.BOLD, 18));
        nextButton.setBackground(Color.WHITE);
        nextButton.setForeground(Color.BLACK);
        nextButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // เมื่อกดปุ่ม Next ให้ไปที่หน้าจอเกม
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Game");
                mainPanel.getComponent(3).requestFocusInWindow(); // ให้ GamePanel รับการโฟกัส
            }
        });

        // จัดปุ่มไว้ที่มุมล่างขวา
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EscapeTheProfessor game = new EscapeTheProfessor();
            game.setVisible(true);
        });
    }
}
