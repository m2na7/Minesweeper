package com.example.firstapplication;

import android.content.Context;
import android.graphics.Color;

public class BlockButton extends androidx.appcompat.widget.AppCompatButton {
    private int x, y; // 버튼 좌표
    boolean mine; // 지뢰여부
    boolean flag; // 깃발여부
    int neighborMines; // 블록 주변의 지뢰 갯수
    static int flags; // 깃발이 꽂힌 블록 수
    private boolean breakState; // 블록 오픈 여부

    public BlockButton(Context context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
        mine = false;
        flag = false;
        neighborMines = 0;
        flags = 0;
        breakState = false;
        setTextSize(16);
        setTextColor(Color.BLACK);
    }

    // 깃발 토글
    public void toggleFlag() {
        if (!breakState) {
            flag = !flag;
            if (flag) {
                flags++;
                setText("🚩");
            } else {
                flags--;
                setText("");
            }
        }
    }

    // 블록 오픈
    public boolean breakBlock(BlockButton[][] buttons) {
        if (!flag && !breakState) {
            setClickable(false);
            if (mine) {
                setText("💣");
                return true;
            } else if (neighborMines == 0) {
                setBackgroundColor(Color.WHITE);
                breakState = true;
                return false;
            } else {
                setBackgroundColor(Color.WHITE);
                setCountColor(neighborMines);
                setText(String.valueOf(neighborMines));
                breakState = true;
                return false;
            }
        }
        return false;
    }

    // 블록 주변의 지뢰 갯수에 따라 텍스트 색상 변경
    public void setCountColor(int count) {
        this.neighborMines = count;

        switch (count) {
            case 1:
                setTextColor(Color.BLUE);
                break;
            case 2:
                setTextColor(Color.parseColor("#4B7A47"));
                break;
            case 3:
                setTextColor(Color.RED);
                break;
            case 4:
                setTextColor(Color.DKGRAY);
                break;
            default:
                setTextColor(Color.BLACK);
                break;
        }
    }

    public int getBlockX() {
        return x;
    }

    public int getBlockY() {
        return y;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isFlag() {
        return flag;
    }

    public int getNeighborMines() {
        return neighborMines;
    }

    public void setNeighborMines(int neighborMines) {
        this.neighborMines = neighborMines;
    }
}