package main;
import java.awt.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;
import java.util.ArrayList;


import TetrisBlock.*;
public class PlayManager {
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;


    TetrisBlock currentTetrisBlock;
    final int TETRISBLOCK_START_X;
    final int TETRISBLOCK_START_Y;
    TetrisBlock nextTetrisBlock;
    final int NEXTTETRISBLOCK_X;
    final int NEXTTETRISBLOCK_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    int level = 1;
    int lines;
    int score;
    public static int dropInterval = 60;
    boolean gameOver;

    public PlayManager() {
        left_x = (GamePanel.WIDTH / 2) - (WIDTH / 2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        TETRISBLOCK_START_X = left_x + (WIDTH / 2) - Block.SIZE;
        TETRISBLOCK_START_Y = top_y + Block.SIZE;

        NEXTTETRISBLOCK_X = right_x + 175;
        NEXTTETRISBLOCK_Y = top_y + 500;

        currentTetrisBlock = pickTetrisBlock();
        currentTetrisBlock.setXY(TETRISBLOCK_START_X, TETRISBLOCK_START_Y);

        nextTetrisBlock = pickTetrisBlock();
        nextTetrisBlock.setXY(NEXTTETRISBLOCK_X, NEXTTETRISBLOCK_Y);


    }

    private TetrisBlock pickTetrisBlock() {
        TetrisBlock tetrisBlock = null;
        int i = new Random().nextInt(7);
        switch (i) {
            case 0:
                tetrisBlock = new TetrisBlock_L1();
                break;
            case 1:
                tetrisBlock = new TetrisBlock_L2();
                break;
            case 2:
                tetrisBlock = new TetrisBlock_Square();
                break;
            case 3:
                tetrisBlock = new TetrisBlock_Bar();
                break;
            case 4:
                tetrisBlock = new TetrisBlock_T();
                break;
            case 5:
                tetrisBlock = new TetrisBlock_Z1();
                break;
            case 6:
                tetrisBlock = new TetrisBlock_Z2();
                break;
        }
        return tetrisBlock;
    }

    public void update() {
        if (currentTetrisBlock.active == false) {
            staticBlocks.add(currentTetrisBlock.b[0]);
            staticBlocks.add(currentTetrisBlock.b[1]);
            staticBlocks.add(currentTetrisBlock.b[2]);
            staticBlocks.add(currentTetrisBlock.b[3]);
            currentTetrisBlock.deactivating = false;

            if (currentTetrisBlock.b[0].x == TETRISBLOCK_START_X && currentTetrisBlock.b[0].y == TETRISBLOCK_START_Y) {
                gameOver = true;
            }
            currentTetrisBlock.deactivating = false;

            currentTetrisBlock = nextTetrisBlock;
            currentTetrisBlock.setXY(TETRISBLOCK_START_X, TETRISBLOCK_START_Y);
            nextTetrisBlock = pickTetrisBlock();
            nextTetrisBlock.setXY(NEXTTETRISBLOCK_X, NEXTTETRISBLOCK_Y);

            checkDelete();
        } else {
            currentTetrisBlock.update();
        }
    }

    private void checkDelete() {
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while (x < right_x && y < bottom_y) {
            for (int i = 0; i < staticBlocks.size(); i++) {
                if (staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
                    blockCount++;
                }
            }
            x += Block.SIZE;

            if (x == right_x) {
                if (blockCount == 12) {
                    for (int i = staticBlocks.size() - 1; i > -1; i--) {
                        if (staticBlocks.get(i).y == y) {
                            staticBlocks.remove(i);
                        }
                    }
                    lineCount++;
                    lines++;


                    if(lines % 10 == 0 && dropInterval > 1) {
                        level++;
                        if (dropInterval > 10) {
                            dropInterval -= 10;
                        }
                        else {
                            dropInterval -= 1;
                        }
                    }


                    for (int i = 0; i < staticBlocks.size(); i++) {
                        if (staticBlocks.get(i).y < y) {
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }

                }
                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }
        if (lineCount > 0) {
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }
    }


    public void draw(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arrial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x + 60, y + 60);
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("POZIOM: " + level, x, y); y+=70;
        g2.drawString("PIETRA: " + lines, x, y); y+= 70;
        g2.drawString("WYNIK: " + score, x, y);
        if (currentTetrisBlock != null) {
            currentTetrisBlock.draw(g2);
        }
        nextTetrisBlock.draw(g2);
        for (int i = 0; i < staticBlocks.size(); i++) {
            staticBlocks.get(i).draw(g2);
        }
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if (gameOver) {
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        } else if (Klawiatura.pausePressed) {
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
        }
    }
}