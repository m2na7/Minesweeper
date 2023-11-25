package com.example.firstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ToggleButton tButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 테이블 레이아웃 생성
        TableLayout table;
        table = findViewById(R.id.tableLayout);

        // 9X9 지뢰찾기 버튼 생성
        BlockButton[][] buttons = new BlockButton[9][9];
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1.0f);

        for (int i = 0; i < 9; i++) {
            TableRow row = new TableRow(this);
            table.addView(row);

            for (int j = 0; j < 9; j++) {
                buttons[i][j] = new BlockButton(this, i, j);
                buttons[i][j].setLayoutParams(layoutParams);
                row.addView(buttons[i][j]);

                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleButtonClick((BlockButton) view);
                    }
                });
            }
        }

        tButton = findViewById(R.id.toggleButton);

        // 10개의 지뢰 랜덤 배치
        for (int i = 0; i < 10; i++) {
            Random rand = new Random();
            int pos[] = new int[2];
            pos[0] = rand.nextInt(9);
            pos[1] = rand.nextInt(9);

            initializeMineButton(buttons[pos[0]][pos[1]]);
        }
    }

    // 지뢰 버튼 초기화 및 클릭 리스너 추가
    public void initializeMineButton(BlockButton b) {
        b.setMine(true);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleButtonClick((BlockButton) view);
            }
        });
    }

    // 토글버튼 상태에 따른 동작변화
    private void handleButtonClick(BlockButton button) {
        if (tButton.isChecked()) {
            button.toggleFlag();
        } else {
            button.breakBlock();
        }
    }
}
