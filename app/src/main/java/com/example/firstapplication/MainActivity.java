package com.example.firstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView mineCountTextView;
    private ToggleButton tButton;
    BlockButton[][] buttons = new BlockButton[9][9];
    int numMines = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mineCountTextView = findViewById(R.id.textView3);

        // 테이블 레이아웃 생성
        TableLayout table;
        table = findViewById(R.id.tableLayout);

        // 토글 버튼 생성
        tButton = findViewById(R.id.toggleButton);

        // 9X9 지뢰찾기 레이아웃 생성
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

        // 지뢰 생성
        generateMines(buttons);

        // 주변 지뢰 갯수 계산
        calculateNeighborMines(buttons);
    }

    // 지뢰 랜덤 배치
    private void generateMines(BlockButton[][] buttons) {
        Random rand = new Random();

        for (int i = 0; i < numMines; i++) {
            int x, y;
            do {
                x = rand.nextInt(9);
                y = rand.nextInt(9);
            } while (buttons[x][y].isMine()); // 이미 지뢰가 있는 위치인지 확인

            initializeMineButton(buttons[x][y]);
        }
    }

    // 남은 지뢰 갯수를 계산
    private void updateMineCount(BlockButton button) {
        int remainingMines = numMines - button.flags; // 전체 지뢰 갯수 - 깃발의 갯수
        mineCountTextView.setText(String.format("%d", remainingMines));

        if (button.flags > numMines) { // 깃발의 갯수 > 전체 지뢰 갯수 -> Toast 메시지 출력
            Toast.makeText(this, "깃발의 갯수가 지뢰의 갯수보다 더 많습니다", Toast.LENGTH_SHORT).show();
        }
    }

    // 버튼 주변의 지뢰 갯수를 계산
    private void calculateNeighborMines(BlockButton[][] buttons) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // 각 버튼이 지뢰가 아니면 countSurroundingMines 호출
                if (!buttons[i][j].isMine()) {
                    int count = countMines(buttons, i, j);
                    buttons[i][j].setNeighborMines(count);
                }
            }
        }
    }

    // 각각의 버튼에 대한 주변 지뢰의 갯수를 카운트
    private int countMines(BlockButton[][] buttons, int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int ni = x + i;
                int nj = y + j;
                if (ni >= 0 && ni < 9 && nj >= 0 && nj < 9 && buttons[ni][nj].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    // 지뢰 버튼 초기화 및 클릭 리스너 추가
    private void initializeMineButton(BlockButton button) {
        button.setMine(true);
        updateMineCount(button); // 초기 전체 지뢰 수

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleButtonClick((BlockButton) view);
            }
        });
    }

    // 토글버튼 상태에 따른 동작변화
    private void handleButtonClick(BlockButton button) {
        if (tButton.isChecked()) {  // 깃발 꽂기 모드
            button.toggleFlag();
            updateMineCount(button);
        } else { // 블록 열기 모드
            breakBlock(button);
        }
    }

    // 블록 열기
    private void breakBlock(BlockButton button) {
        if (button.breakBlock(buttons)) {
            if(button.isMine()) { // 지뢰 클릭시 게임 종료
                disableButtons();
                Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show();
            }
        } else if (button.getNeighborMines() == 0 && !button.flag) {
            openBlocks(buttons, button.getBlockX(), button.getBlockY());
        }

        if(checkGameWin()) {
            disableButtons();
            Toast.makeText(this, "Game Win", Toast.LENGTH_SHORT).show();
        }
    }

    // 주변 지뢰수가 0 -> 주변의 모든 블록 열기
    private void openBlocks(BlockButton[][] buttons, int x, int y) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int ni = x + i;
                int nj = y + j;

                if (ni >= 0 && ni < 9 && nj >= 0 && nj < 9 &&
                        !buttons[ni][nj].isMine() && !buttons[ni][nj].isFlag() && buttons[ni][nj].isClickable()) {
                    buttons[ni][nj].breakBlock(buttons);

                    // 재귀적으로 열도록 처리
                    if (buttons[ni][nj].getNeighborMines() == 0) {
                        openBlocks(buttons, ni, nj);
                    }
                }
            }
        }
    }

    // 게임 종료시 모든 버튼을 사용불가 상태로 변경
    private void disableButtons() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    // 게임 승리 조건에 충족하는지 확인
    private boolean checkGameWin() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!buttons[i][j].isMine() && buttons[i][j].isClickable()) {
                    return false;
                }
            }
        }
        return true;  // 모든 승리 조건에 만족하면 true
    }
}
