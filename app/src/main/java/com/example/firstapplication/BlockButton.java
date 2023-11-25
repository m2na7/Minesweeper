package com.example.firstapplication;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.ColorInt;

public class BlockButton extends androidx.appcompat.widget.AppCompatButton {
    boolean mine = false;
    boolean flag = false;
    int neighborMines = 0;
    static int flags = 0;

    public BlockButton(Context context, int x, int y) {
        super(context);
    }

    public void toggleFlag() {
        if (!mine) {
            flag = !flag;
            if (flag) {
                flags--;
                setText("F");
            } else {
                flags++;
                setText("");
            }
        }
    }

    public boolean breakBlock() {
        if (mine) {
            setBackgroundColor(Color.BLUE);
            return true;
        } else {
            return false;
        }
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