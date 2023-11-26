package com.example.firstapplication;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.ColorInt;

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

    public boolean breakBlock() {
        if (!flag && !breakState) {
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
                setText(String.valueOf(neighborMines));
                breakState = true;
                return false;
            }
        }
        return false;
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