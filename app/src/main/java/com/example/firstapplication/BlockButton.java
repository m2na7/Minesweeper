package com.example.firstapplication;

import android.content.Context;
import android.graphics.Color;

public class BlockButton extends androidx.appcompat.widget.AppCompatButton {
    private int x, y;
    boolean mine = false;
    boolean flag = false;
    int neighborMines = 0;
    static int flags = 0;
    private boolean breakState = false;

    public BlockButton(Context context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
        setTextSize(14);
        setTextColor(Color.BLACK);
    }

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

    public boolean breakBlock(BlockButton[][] buttons) {
        if (!flag && !breakState) {
            setClickable(false);
            if (mine) {
                setBackgroundColor(Color.WHITE);
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

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getNeighborMines() {
        return neighborMines;
    }

    public void setNeighborMines(int neighborMines) {
        this.neighborMines = neighborMines;
    }
}