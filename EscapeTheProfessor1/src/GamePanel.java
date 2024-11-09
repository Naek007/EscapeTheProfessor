 import javax.swing.*;
 import javax.swing.Timer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements KeyListener {
	private int playerX = 490, playerY = 170;
    private BufferedImage studentBack, studentLeft,professorImage, studentRight, doorImage, switchImage, womanSignImage, background, flashlightImage, wallImageMap2, skyImage, moonImage, deskImage, computerImage, maleSignImage,chestImage;
    private BufferedImage currentImage;
    private Rectangle doorPosition, maleSignPosition, secondDoorPosition, switchPosition, womanSignPosition,thirdDoorPosition,chestPosition;
    private boolean hasFlashlight = false;
    private boolean flashlightOn = false;
    private boolean inventoryOpen = false;
    private boolean inDialogue = true;
    private int dialogueIndex = 0;
    private List<String> dialogues;
    private String objective = "";
    private boolean isLightOn = false;
    private boolean isPlayerVisible = true;
    private int professorX, professorY;
    private Timer professorTimer;
    private int previousMap = 1; // กำหนดค่าเริ่มต้นเป็นแมพ 1
    private EscapeTheProfessor parentFrame; // อ้างอิงถึง JFrame หลัก
    private boolean isProfessorActive = false; // ตัวแปรเพื่อเก็บสถานะการทำงานของ professor

    
    private Rectangle fourthDoorPosition;
    private int currentMap = 1;

    private List<Rectangle> wallPositions;
    private List<Rectangle> skyPositions;
    private Rectangle moonPosition;
    private List<Rectangle> deskPositions;
    private Rectangle flashlightPosition;
    private List<String> inventoryItems;

    public GamePanel(EscapeTheProfessor parentFrame) {
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();
        this.parentFrame = parentFrame;
        dialogues = new ArrayList<>();
        dialogues.add("Student: Huhhh It's almost the project due date, so why isn't my work finished yet? :((");
        dialogues.add("Student: I think I'm going to take a break for a bit. Better go to the Restroom.");
        dialogues.add("Student: It was very dark outside the room. We need a flashlight.");
        dialogues.add("Student: Okay, let’s get the flashlight and go outside the room to the Restroom.");

        addKeyListener(this);
        setLayout(null);

        inventoryItems = new ArrayList<>();
        wallPositions = new ArrayList<>();
        skyPositions = new ArrayList<>();
        deskPositions = new ArrayList<>();

        try {
        	chestImage = resizeImage(ImageIO.read(new File("src/assets/Chest.png")), 45, 45); // โหลดภาพ Chest
        	professorImage = resizeImage(ImageIO.read(new File("src/assets/Professor.png")), 48, 48);
            studentBack = resizeImage(ImageIO.read(new File("src/assets/Student Back side.png")), 48, 48);
            studentLeft = resizeImage(ImageIO.read(new File("src/assets/Student Left side.png")), 48, 48);
            studentRight = resizeImage(ImageIO.read(new File("src/assets/Student right side.png")), 48, 48);
            doorImage = resizeImage(ImageIO.read(new File("src/assets/Door.png")), 50, 50);
            switchImage = resizeImage(ImageIO.read(new File("src/assets/switch.png")), 50, 50);
            womanSignImage = resizeImage(ImageIO.read(new File("src/assets/womansign.png")), 50, 50);
            background = ImageIO.read(new File("src/assets/floor.png"));
            flashlightImage = resizeImage(ImageIO.read(new File("src/assets/Flashlight.png")), 48, 48);
            wallImageMap2 = resizeImage(ImageIO.read(new File("src/assets/wallmap2.png")), 45, 45);
            skyImage = resizeImage(ImageIO.read(new File("src/assets/sky.png")), 50, 50);
            moonImage = resizeImage(ImageIO.read(new File("src/assets/moon.png")), 50, 50);
            deskImage = resizeImage(ImageIO.read(new File("src/assets/desk.png")), 50, 50);
            computerImage = resizeImage(ImageIO.read(new File("src/assets/computer.png")), 40, 40);
            maleSignImage = resizeImage(ImageIO.read(new File("src/assets/malesign.png")), 50, 50);
            currentImage = studentBack;
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        setupWallsAndSky();
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        Action toggleInventoryAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inventoryOpen = !inventoryOpen;
                repaint();
            }
        };

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0), "toggleInventory");
        getActionMap().put("toggleInventory", toggleInventoryAction);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image tmp = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
    private void initializeProfessor() {
        professorX = doorPosition.x;
        professorY = doorPosition.y;
        isProfessorActive = true;
        startProfessorTimer();
    }

    private void startProfessorTimer() {
        professorTimer = new Timer(5000, e -> {
            moveProfessor();
            repaint();
        });
        professorTimer.start();
    }
    private boolean checkProfessorCollision(int x, int y) {
        Rectangle professorRect = new Rectangle(x, y, 48, 48);
        for (Rectangle wall : wallPositions) {
            if (professorRect.intersects(wall)) {
                return true; // ชนกับกำแพง
            }
        }
        return false; // ไม่ชนกับกำแพง
    }

    private void moveProfessor() {
        if (isProfessorActive) {
            int nextX = professorX;
            int nextY = professorY;
            
            if (professorX > playerX) nextX -= 2;  // ลด X เพื่อให้วิ่งไปทางซ้าย
            else if (professorX < playerX) nextX += 2; // เพิ่ม X เพื่อให้วิ่งไปทางขวา
            if (professorY > playerY) nextY -= 2; // ลด Y เพื่อให้วิ่งขึ้น
            else if (professorY < playerY) nextY += 2; // เพิ่ม Y เพื่อให้วิ่งลง
            
            // ตรวจสอบการชนกำแพง ถ้าไม่ชนให้ย้ายตำแหน่งได้
            if (!checkProfessorCollision(nextX, professorY)) {
                professorX = nextX;
            }
            if (!checkProfessorCollision(professorX, nextY)) {
                professorY = nextY;
            }

            // ตรวจสอบว่าถูกจับได้หรือไม่
            if (new Rectangle(professorX, professorY, 48, 48).intersects(new Rectangle(playerX, playerY, 48, 48))) {
                parentFrame.showGameOverScreen(); // แสดงหน้าจอ Game Over เมื่อถูกจับได้
                professorTimer.stop(); // หยุด Timer
            }
        }
    }
    private void stopProfessorTimer() {
        if (professorTimer != null) {
            professorTimer.stop();
            professorTimer = null;
            isProfessorActive = false;
        }
    }
    private void setupWallsAndSky() {
        wallPositions.clear();
        skyPositions.clear();
        deskPositions.clear();

        if (currentMap == 1) {
            setupMap1Walls();
        } else if (currentMap == 2) {
            setupMap2Walls();
        } else if (currentMap == 3) {
            setupMap3MazeWalls(); // เรียกฟังก์ชันสร้างเขาวงกตใน map3
        } else if (currentMap == 4) {
            setupMap4Walls();
        }
    }

    private void setupMap1Walls() {
        int wallStartX = 50;
        int wallStartY = 50;

        for (int i = 0; i < 10; i++) {
            wallPositions.add(new Rectangle(wallStartX + i * 50, wallStartY, 50, 50));
        }

        wallPositions.add(new Rectangle(0, 0, 50, 50));
        wallPositions.add(new Rectangle(0, 50, 50, 50));
        wallPositions.add(new Rectangle(450, 50, 50, 50));
        wallPositions.add(new Rectangle(500, 50, 50, 50));
        wallPositions.add(new Rectangle(550, 50, 50, 50));
        wallPositions.add(new Rectangle(600, 50, 50, 50));
        wallPositions.add(new Rectangle(650, 50, 50, 50));
        wallPositions.add(new Rectangle(700, 50, 50, 50));
        wallPositions.add(new Rectangle(750, 50, 50, 50));

        skyPositions.add(new Rectangle(50, 0, 50, 50));
        skyPositions.add(new Rectangle(100, 0, 50, 50));
        skyPositions.add(new Rectangle(150, 0, 50, 50));
        skyPositions.add(new Rectangle(200, 0, 50, 50));
        skyPositions.add(new Rectangle(250, 0, 50, 50));
        skyPositions.add(new Rectangle(300, 0, 50, 50));
        skyPositions.add(new Rectangle(350, 0, 50, 50));
        skyPositions.add(new Rectangle(400, 0, 50, 50));
        skyPositions.add(new Rectangle(450, 0, 50, 50));
        skyPositions.add(new Rectangle(500, 0, 50, 50));
        skyPositions.add(new Rectangle(550, 0, 50, 50));
        skyPositions.add(new Rectangle(600, 0, 50, 50));
        skyPositions.add(new Rectangle(650, 0, 50, 50));
        skyPositions.add(new Rectangle(700, 0, 50, 50));
        skyPositions.add(new Rectangle(750, 0, 50, 50));

        moonPosition = new Rectangle(350, 0, 50, 50);

        deskPositions.add(new Rectangle(50, 100, 50, 50));
        deskPositions.add(new Rectangle(50, 150, 50, 50));
        deskPositions.add(new Rectangle(250, 150, 50, 50));
        deskPositions.add(new Rectangle(300, 150, 50, 50));
        deskPositions.add(new Rectangle(350, 150, 50, 50));
        deskPositions.add(new Rectangle(400, 150, 50, 50));
        deskPositions.add(new Rectangle(450, 150, 50, 50));
        deskPositions.add(new Rectangle(500, 150, 50, 50));
        deskPositions.add(new Rectangle(550, 150, 50, 50));
        deskPositions.add(new Rectangle(600, 150, 50, 50));
        deskPositions.add(new Rectangle(650, 150, 50, 50));
        deskPositions.add(new Rectangle(700, 150, 50, 50));
        deskPositions.add(new Rectangle(250, 400, 50, 50));
        deskPositions.add(new Rectangle(300, 400, 50, 50));
        deskPositions.add(new Rectangle(350, 400, 50, 50));
        deskPositions.add(new Rectangle(400, 400, 50, 50));
        deskPositions.add(new Rectangle(450, 400, 50, 50));
        deskPositions.add(new Rectangle(500, 400, 50, 50));
        deskPositions.add(new Rectangle(550, 400, 50, 50));
        deskPositions.add(new Rectangle(600, 400, 50, 50));
        deskPositions.add(new Rectangle(650, 400, 50, 50));
        deskPositions.add(new Rectangle(700, 400, 50, 50));

        flashlightPosition = new Rectangle(50, 100, 50, 50);
        doorPosition = new Rectangle(50, 550, 50, 50);
    }

    private void setupMap2Walls() {
        // ตำแหน่งประตูหลักใน Map 2
        doorPosition = new Rectangle(50, 550, 50, 50);

        // ประตูเพิ่มเติม
        secondDoorPosition = new Rectangle(600, 50, 50, 50);
        
        // ประตูที่สาม
        thirdDoorPosition = new Rectangle(700, 50, 50, 50);

        professorX = 750; // ตำแหน่งเริ่มต้นของ Professor ทางด้านขวาสุดของ Map 2
        professorY = 300; // กำหนดตำแหน่ง Y ของ Professor
        isProfessorActive = true; // กำหนดให้ Professor เริ่มทำงานเมื่อเข้าแมพ 2


        // (กำหนดตำแหน่งของผนังและองค์ประกอบอื่นๆ ที่เหลือ)
    


        wallPositions.add(new Rectangle(0, 0, 50, 50));
        wallPositions.add(new Rectangle(50, 0, 50, 50));
        wallPositions.add(new Rectangle(100, 0, 50, 50));
        wallPositions.add(new Rectangle(150, 0, 50, 50));
        wallPositions.add(new Rectangle(200, 0, 50, 50));
        wallPositions.add(new Rectangle(250, 0, 50, 50));
        wallPositions.add(new Rectangle(300, 0, 50, 50));
        wallPositions.add(new Rectangle(350, 0, 50, 50));
        wallPositions.add(new Rectangle(400, 0, 50, 50));
        wallPositions.add(new Rectangle(450, 0, 50, 50));
        wallPositions.add(new Rectangle(500, 0, 50, 50));
        wallPositions.add(new Rectangle(550, 0, 50, 50));
        wallPositions.add(new Rectangle(600, 0, 50, 50));
        wallPositions.add(new Rectangle(650, 0, 50, 50));
        wallPositions.add(new Rectangle(700, 0, 50, 50));
        wallPositions.add(new Rectangle(750, 0, 50, 50));

        wallPositions.add(new Rectangle(0, 50, 50, 50));
        wallPositions.add(new Rectangle(50, 50, 50, 50));
        wallPositions.add(new Rectangle(100, 50, 50, 50));
        wallPositions.add(new Rectangle(150, 50, 50, 50));
        wallPositions.add(new Rectangle(200, 50, 50, 50));
        wallPositions.add(new Rectangle(250, 50, 50, 50));
        wallPositions.add(new Rectangle(300, 50, 50, 50));
        wallPositions.add(new Rectangle(350, 50, 50, 50));
        wallPositions.add(new Rectangle(400, 50, 50, 50));
        wallPositions.add(new Rectangle(450, 50, 50, 50));
        wallPositions.add(new Rectangle(500, 50, 50, 50));

        maleSignPosition = new Rectangle(550, 50, 50, 50);
        switchPosition = new Rectangle(650, 50, 50, 50);
        womanSignPosition = new Rectangle(750, 50, 50, 50);
    }


    private void setupMap4Walls() {
        setupMap1Walls(); // Use Map 1's wall setup
        flashlightPosition = null; // No flashlight in Map 4
        moonPosition = new Rectangle(350, 0, 50, 50); // Set moon position from Map 1
    }
    private void setupMap3MazeWalls() {
        wallPositions.clear();
        
        // กำแพงรอบนอกของเขาวงกต
        for (int x = 0; x <= 750; x += 50) {
            wallPositions.add(new Rectangle(x, 0, 50, 50)); // กำแพงบน
            wallPositions.add(new Rectangle(x, 550, 50, 50)); // กำแพงล่าง
        }
        for (int y = 0; y <= 550; y += 50) {
           
            wallPositions.add(new Rectangle(750, y, 50, 50)); // กำแพงขวา
        }

        // สร้างเขาวงกตภายใน (ลายตามแบบในภาพ)
        // เส้นที่ 1 (บน)
        fourthDoorPosition = new Rectangle(700, 50, 50, 50); // เพิ่มตำแหน่งประตูใหม่
        wallPositions.add(new Rectangle(100, 50, 50, 50));
        wallPositions.add(new Rectangle(150, 50, 50, 50));
        wallPositions.add(new Rectangle(250, 50, 50, 50));
        wallPositions.add(new Rectangle(350, 50, 50, 50));
        wallPositions.add(new Rectangle(450, 50, 50, 50));
        wallPositions.add(new Rectangle(600, 50, 50, 50));
        

        // เส้นที่ 2
        wallPositions.add(new Rectangle(100, 100, 50, 50));
        wallPositions.add(new Rectangle(200, 100, 50, 50));
        wallPositions.add(new Rectangle(300, 100, 50, 50));
        wallPositions.add(new Rectangle(400, 100, 50, 50));
        wallPositions.add(new Rectangle(500, 100, 50, 50));
        wallPositions.add(new Rectangle(600, 100, 50, 50));
        
        // เส้นที่ 3
        wallPositions.add(new Rectangle(50, 150, 50, 50));
        wallPositions.add(new Rectangle(200, 150, 50, 50));
        wallPositions.add(new Rectangle(350, 150, 50, 50));
        wallPositions.add(new Rectangle(550, 150, 50, 50));
        wallPositions.add(new Rectangle(650, 150, 50, 50));
        
        // เส้นที่ 4
        wallPositions.add(new Rectangle(100, 200, 50, 50));
        wallPositions.add(new Rectangle(150, 200, 50, 50));
        wallPositions.add(new Rectangle(300, 200, 50, 50));
        wallPositions.add(new Rectangle(450, 200, 50, 50));
        wallPositions.add(new Rectangle(500, 200, 50, 50));
        wallPositions.add(new Rectangle(600, 200, 50, 50));
        
        // เส้นที่ 5
        wallPositions.add(new Rectangle(200, 250, 50, 50));
        
        
        
        // เส้นที่ 6
        
        wallPositions.add(new Rectangle(300, 300, 50, 50));
        wallPositions.add(new Rectangle(400, 300, 50, 50));
        
        wallPositions.add(new Rectangle(650, 300, 50, 50));
        
        // เส้นที่ 7
        wallPositions.add(new Rectangle(100, 350, 50, 50));
      ;
        
        // เส้นที่ 9
        wallPositions.add(new Rectangle(100, 450, 50, 50));
        wallPositions.add(new Rectangle(200, 450, 50, 50));
        wallPositions.add(new Rectangle(350, 450, 50, 50));
        wallPositions.add(new Rectangle(550, 450, 50, 50));
        wallPositions.add(new Rectangle(650, 450, 50, 50));
        
        // เส้นที่ 10 (ล่าง)
        wallPositions.add(new Rectangle(50, 500, 50, 50));
        wallPositions.add(new Rectangle(250, 500, 50, 50));
        wallPositions.add(new Rectangle(300, 500, 50, 50));
        wallPositions.add(new Rectangle(450, 500, 50, 50));
        wallPositions.add(new Rectangle(600, 500, 50, 50));
    }
    private void setupMap() {
        if (currentMap == 2) {
            doorPosition = new Rectangle(50, 550, 50, 50);
            switchPosition = new Rectangle(650, 50, 50, 50);
        }
    }
    private void switchMapNext() {
        previousMap = currentMap; // บันทึกแมพก่อนหน้า
        if (currentMap == 1) {
            currentMap = 2;
        } else if (currentMap == 2) {
            currentMap = 3;
            
            // เรียกใช้ฟังก์ชัน initializeProfessorWithDelay เมื่อเข้าสู่แมพ 3
            initializeProfessorWithDelay();
            
        } else if (currentMap == 3) {
            currentMap = 4;
        } else if (currentMap == 2 && isProfessorActive) {
            professorX = playerX - 50;  
            professorY = playerY;  
        }
        resetDialogue();
        setupWallsAndSky();
    }
 // ฟังก์ชันสำหรับหน่วงเวลา 2 วินาทีก่อนการแสดงตัวของ Professor
    private void initializeProfessorWithDelay() {
        // ใช้ Timer เพื่อหน่วงเวลา 2000 มิลลิวินาที (2 วินาที)
        new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                professorX = 90;
                professorY = 50;
                isProfessorActive = true;
                startProfessorTimer();
                ((Timer)e.getSource()).stop(); // หยุด Timer หลังจากทำงานเสร็จสิ้น
            }
        }).start();
    }

    private void switchMapPrevious() {
        previousMap = currentMap; // บันทึกแมพก่อนหน้า
        if (currentMap == 4) {
            currentMap = 3;
        } else if (currentMap == 3) {
            currentMap = 2;
        } else if (currentMap == 2) {
            currentMap = 1;
            
        }else if (currentMap == 3 && isProfessorActive) {
            professorX = playerX + 50;  
            professorY = playerY;    }
        resetDialogue();
        setupWallsAndSky();
    }


    private void resetDialogue() {
        dialogueIndex = 0;
        dialogues.clear();
        inDialogue = true;

        // เพิ่มการตรวจสอบ: แสดงบทพูดเฉพาะเมื่อเข้ามาจากแมพ 1 เท่านั้น
        if (currentMap == 2) {
            if (previousMap == 1) {
                dialogues.add("Student: OMG It's so dark and very scary. Damn it, I have to go to the Toilet.");
                dialogues.add("Student: Go to Restroom");
            } else {
                // ถ้าเข้ามาจากแมพอื่นไม่ต้องแสดงบทพูด
                inDialogue = false;
            }
        } else if (currentMap == 4) {
            dialogues.add("Student: It's eerily quiet here... Let's keep moving.");
        }
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        double scaleX = getWidth() / 800.0;
        double scaleY = getHeight() / 600.0;
        g2d.scale(scaleX, scaleY);

        if (inventoryOpen) {
            drawInventory(g2d);
        } else {
            for (int x = 0; x < 800; x += background.getWidth()) {
                for (int y = 0; y < 600; y += background.getHeight()) {
                    g2d.drawImage(background, x, y, this);
                }
            }

            for (Rectangle wall : wallPositions) {
                g2d.drawImage(wallImageMap2, wall.x, wall.y, wall.width, wall.height, null);
            }

            for (Rectangle sky : skyPositions) {
                g2d.drawImage(skyImage, sky.x, sky.y, sky.width, sky.height, null);
            }

            if (inDialogue && dialogueIndex < dialogues.size()) {
                drawDialogueBox(g2d, dialogues.get(dialogueIndex));
            }

            if (!inDialogue && !objective.isEmpty()) {
                drawObjectiveBox(g2d);
            }

            if (currentMap == 1 && moonPosition != null) {
                g2d.drawImage(moonImage, moonPosition.x, moonPosition.y, moonPosition.width, moonPosition.height, null);
            }

            for (Rectangle desk : deskPositions) {
                g2d.drawImage(deskImage, desk.x, desk.y, desk.width, desk.height, null);
            }

            // Draw computers in Map 1 and Map 4
            if (currentMap == 1 || currentMap == 4) {
                for (int i = 250; i <= 700; i += 50) {
                    g2d.drawImage(computerImage, i, 150, 40, 40, null);
                    g2d.drawImage(computerImage, i, 400, 40, 40, null);
                }
            }
            if (currentMap == 1 && flashlightPosition != null && 
            	    playerX >= flashlightPosition.x && playerX < flashlightPosition.x + flashlightPosition.width &&
            	    playerY >= flashlightPosition.y && playerY < flashlightPosition.y + flashlightPosition.height) {
            	    
            	    hasFlashlight = true;
            	    flashlightPosition = null; // ลบตำแหน่งไฟฉายหลังจากเก็บแล้ว
            	    inventoryItems.add("Flashlight"); // เพิ่มไฟฉายเข้าไปใน inventory
            	}


            // Draw doors in Map 1, 2, and 4
            if ((currentMap == 1 || currentMap == 4 || currentMap == 2) && doorPosition != null) {
                g2d.drawImage(doorImage, doorPosition.x, doorPosition.y, doorPosition.width, doorPosition.height, null);
            }
            

            // Draw additional door in Map 2
            if (currentMap == 2 && secondDoorPosition != null) {
                g2d.drawImage(doorImage, secondDoorPosition.x, secondDoorPosition.y, secondDoorPosition.width, secondDoorPosition.height, null);
            }
            if (currentMap == 2) {
                if (isLightOn) {
                    g2d.setColor(new Color(255, 255, 255, 150)); // สีขาวโปร่งใส
                } else {
                    g2d.setColor(new Color(0, 0, 0, 220)); // สีดำโปร่งใส (มืด)
                }
                g2d.fillRect(0, 0, 800, 600);
            }

            if (isPlayerVisible) {
                g2d.drawImage(currentImage, playerX, playerY, null);
            }
            if (isProfessorActive) {
                g2d.drawImage(professorImage, professorX, professorY, null);
            }



            if (currentMap == 1 && flashlightPosition != null && !hasFlashlight) {
                g2d.drawImage(flashlightImage, flashlightPosition.x, flashlightPosition.y, null);
            }

            if (currentMap == 2 && maleSignPosition != null) {
                g2d.drawImage(maleSignImage, maleSignPosition.x, maleSignPosition.y, maleSignPosition.width, maleSignPosition.height, null);
            }
            if (currentMap == 2 && switchPosition != null) {
                g2d.drawImage(switchImage, switchPosition.x, switchPosition.y, switchPosition.width, switchPosition.height, null);
            }

            if (currentMap == 2 && womanSignPosition != null) {
                g2d.drawImage(womanSignImage, womanSignPosition.x, womanSignPosition.y, womanSignPosition.width, womanSignPosition.height, null);
            }
            if (currentMap == 2 && thirdDoorPosition != null) {
                g2d.drawImage(doorImage, thirdDoorPosition.x, thirdDoorPosition.y, thirdDoorPosition.width, thirdDoorPosition.height, null);
            }
            if (currentMap == 3 && fourthDoorPosition != null) {
                g2d.drawImage(doorImage, fourthDoorPosition.x, fourthDoorPosition.y, fourthDoorPosition.width, fourthDoorPosition.height, null);
            }  // ตรวจสอบการชนกับ fourthDoorPosition
            if (currentMap == 3 && fourthDoorPosition != null &&
                    playerX >= fourthDoorPosition.x && playerX < fourthDoorPosition.x + fourthDoorPosition.width &&
                    playerY >= fourthDoorPosition.y && playerY < fourthDoorPosition.y + fourthDoorPosition.height) {
                    // เรียกหน้าจอ "You Win" โดยใช้ parentFrame
                    parentFrame.showWinScreen();
                    return;
            }
            
            


            g2d.drawImage(currentImage, playerX, playerY, null);

            if ((currentMap == 2 || currentMap == 3) && hasFlashlight && flashlightOn) {
                int radius = 200;
                RadialGradientPaint gradient = new RadialGradientPaint(playerX + 32, playerY + 32, radius,
                        new float[]{0f, 1f}, new Color[]{new Color(255, 255, 224, 180), new Color(0, 0, 0, 220)});
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, 800, 600);
            } else if ((currentMap == 2 || currentMap == 3) && !flashlightOn) {
                g2d.setColor(new Color(0, 0, 0, 220));
                g2d.fillRect(0, 0, 800, 600);
            }
        }

        g2d.scale(1 / scaleX, 1 / scaleY);
    }


    private void drawInventory(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(100, 100, 600, 400);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        g.drawString("Inventory", 350, 140);

        g.setFont(new Font("Monospaced", Font.PLAIN, 18));
        int yPosition = 180;
        for (String item : inventoryItems) {
            if (item.equals("Flashlight")) {
                g.drawImage(flashlightImage, 120, yPosition - 20, 24, 24, null);
                g.drawString("Flashlight", 150, yPosition);
            } else {
                g.drawString(item, 150, yPosition);
            }
            yPosition += 30;
        }
    }

    private void drawDialogueBox(Graphics2D g2d, String text) {
        g2d.setColor(new Color(50, 50, 50, 220));
        g2d.fillRect(0, 480, 800, 100);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 18));

        int lineHeight = g2d.getFontMetrics().getHeight();
        int maxWidth = 700;
        int x = 50;
        int y = 510;

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String testLine = line + word + " ";
            int lineWidth = g2d.getFontMetrics().stringWidth(testLine);

            if (lineWidth > maxWidth) {
                g2d.drawString(line.toString(), x, y);
                line = new StringBuilder(word + " ");
                y += lineHeight;
            } else {
                line.append(word).append(" ");
            }
        }

        if (line.length() > 0) {
            g2d.drawString(line.toString(), x, y);
        }
    }

    private void drawObjectiveBox(Graphics2D g2d) {
        g2d.setColor(new Color(50, 50, 50, 220));
        g2d.fillRect(600, 20, 180, 50);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("Objective:", 610, 45);
        g2d.drawString(objective, 610, 70);
    }

    private boolean checkCollision(int x, int y) {
        Rectangle playerRect = new Rectangle(x, y, 48, 48);
        for (Rectangle wall : wallPositions) {
            if (playerRect.intersects(wall)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        int tempX = playerX, tempY = playerY;
        
    
        
        if (inDialogue) {
            if (key == KeyEvent.VK_SPACE) {
                if (dialogueIndex < dialogues.size() - 1) {
                    dialogueIndex++;
                } else {
                    inDialogue = false;
                }
                repaint();
            }
            return;
        }int moveSpeed = 20;

        switch (key) {
            case KeyEvent.VK_W:
                tempY -= moveSpeed;
                currentImage = studentBack;
                if ((currentMap == 1 || currentMap == 4) && tempY < 0) tempY = 0;
                if (currentMap == 2 && tempY < 0) tempY = 0;
                break;
                

            case KeyEvent.VK_A:
                tempX -= moveSpeed;
                currentImage = studentLeft;
                if (tempX < 0) {
                    if (currentMap == 3) {
                        currentMap = 2;
                        playerX = 750;
                        setupWallsAndSky();
                        repaint();
                        return;
                    } else if (currentMap == 2 || currentMap == 4 || currentMap == 1) {
                        tempX = 0;
                        
                    }
                }
                break;

            case KeyEvent.VK_S:
                tempY += moveSpeed;
                currentImage = studentBack;
                if ((currentMap == 1 || currentMap == 4) && tempY > 550) tempY = 550;
                if (currentMap == 2 && tempY > 550) tempY = 550;
                break;

            case KeyEvent.VK_D:
                tempX += moveSpeed;
                currentImage = studentRight;
                if (tempX > 750) {
                    if (currentMap == 2) {
                        currentMap = 3;
                        playerX = 10;
                        setupWallsAndSky();
                        return;
                    } else if (currentMap == 4 || currentMap == 1) {
                        tempX = 750;
                    }
                }
                break;

            case KeyEvent.VK_F:
                if (hasFlashlight) flashlightOn = !flashlightOn;
                break;
        }

        if (!checkCollision(tempX, tempY)) {
            playerX = tempX;
            playerY = tempY;
        }

        if (currentMap == 1 && flashlightPosition != null && !hasFlashlight && !inDialogue && 
        	    playerX >= flashlightPosition.x && playerX < flashlightPosition.x + flashlightPosition.width &&
        	    playerY >= flashlightPosition.y && playerY < flashlightPosition.y + flashlightPosition.height) {
        	    
        	    hasFlashlight = true;  // ตั้งค่าให้เก็บไฟฉายแล้ว
        	    flashlightPosition = null;  // ลบตำแหน่งไฟฉายหลังจากเก็บแล้ว
        	    inventoryItems.add("Flashlight");  // เพิ่มไฟฉายเข้า inventory
        	    repaint();  // วาดหน้าจอใหม่เพื่ออัปเดตสถานะของไฟฉาย
        	}

        if (key == KeyEvent.VK_E && currentMap == 2 && switchPosition != null) {
            Rectangle playerRect = new Rectangle(playerX, playerY, 64, 64);
            if (playerRect.intersects(switchPosition)) {
                isLightOn = !isLightOn;
                initializeProfessor();
               

                repaint(); // วาดหน้าจอใหม่
            }
        }
        if (isProfessorActive && professorTimer == null) {
            professorTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (professorX > playerX) professorX -= 5; // ลดตำแหน่ง X เพื่อให้วิ่งไปทางซ้าย
                    else if (professorX < playerX) professorX += 5; // เพิ่ม X เพื่อให้วิ่งไปทางขวา
                    if (professorY > playerY) professorY -= 5; // ลด Y เพื่อให้วิ่งขึ้น
                    else if (professorY < playerY) professorY += 5; // เพิ่ม Y เพื่อให้วิ่งลง
                    
                    // ตรวจสอบว่าถูกจับได้หรือยัง
                    if (new Rectangle(professorX, professorY, 48, 48).intersects(new Rectangle(playerX, playerY, 48, 48))) {
                        parentFrame.showGameOverScreen(); // แสดงหน้าจอ Game Over เมื่อ Professor จับได้
                        professorTimer.stop(); // หยุด Timer
                    }
                    repaint(); // วาดหน้าจอใหม่ให้ Professor เคลื่อนที่
                }
            });
            professorTimer.start(); // เริ่ม Timer
        }


        // ตรวจสอบการกดปุ่ม G ในแมพที่ 2 และการชนกับตำแหน่งประตูที่สอง
        if (key == KeyEvent.VK_G && currentMap == 2 && secondDoorPosition != null) {
            Rectangle playerRect1 = new Rectangle(playerX, playerY, 64, 64);
            if (playerRect1.intersects(secondDoorPosition)) {
                isPlayerVisible = false; // ซ่อนตัวละคร
                repaint(); // วาดหน้าจอใหม่ให้ตัวละครหายไป

                // ใช้ Timer เพื่อให้ตัวละครกลับมาอีกครั้งหลังจาก 2 วินาที
                new javax.swing.Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        isPlayerVisible = true; // แสดงตัวละครอีกครั้ง
                        repaint(); // วาดหน้าจอใหม่ให้ตัวละครกลับมา
                        ((javax.swing.Timer) evt.getSource()).stop(); // หยุด Timer หลังจากทำงานเสร็จสิ้น
                    }
                }).start();
            }
        }

     // การตรวจสอบการเปลี่ยนแผนที่และตำแหน่งของ player ในแต่ละกรณี
        if (currentMap == 1 && doorPosition != null &&
            playerX >= doorPosition.x && playerX < doorPosition.x + doorPosition.width &&
            playerY >= doorPosition.y && playerY < doorPosition.y + doorPosition.height) {

            currentMap = 2;
            playerX = doorPosition.x;
            playerY = doorPosition.y;
            setupWallsAndSky();

        } else if (currentMap == 2 && doorPosition != null &&
                   playerX >= doorPosition.x && playerX < doorPosition.x + doorPosition.width &&
                   playerY >= doorPosition.y && playerY < doorPosition.y + doorPosition.height) {

            currentMap = 4;
            playerX = doorPosition.x;
            playerY = doorPosition.y;
            
            setupWallsAndSky();

        } else if (currentMap == 4 && doorPosition != null &&
                   playerX >= doorPosition.x && playerX < doorPosition.x + doorPosition.width &&
                   playerY >= doorPosition.y && playerY < doorPosition.y + doorPosition.height) {

            currentMap = 2;
            playerX = doorPosition.x;
            playerY = doorPosition.y;
            setupWallsAndSky();
        }

        // เรียกใช้ repaint() นอก if-else เพื่อให้แน่ใจว่าทำงานได้ในทุกเงื่อนไข
        repaint();

    }
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
    
}