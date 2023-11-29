package com.example.firstapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final int NUM_MINES = 3; // 전체 지뢰 갯수 설정
    private TableLayout table; // 지뢰찾기 테이블 레이아웃
    BlockButton[][] buttons = new BlockButton[9][9]; // 지뢰찾기 블록버튼
    private ToggleButton tButton; // 깃발or블록오픈 모드변경 토글버튼
    private Button replayButton; // 재도전 버튼
    private TextView mineCountTextView; // 지뢰갯수 텍스트
    private Chronometer timer; // 타이머
    private TextView timerTextView; // 시간표시 텍스트
    private Handler handler = new Handler(); // 시간측정을 위한 핸들러
    boolean isGameOver = false; // 게임종료 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mineCountTextView = findViewById(R.id.countTextView);
        timerTextView = findViewById(R.id.timerTextView);
        tButton = findViewById(R.id.toggleButton);
        replayButton = findViewById(R.id.replayButton);

        startGame();
    }

    // 게임 시작
    private void startGame() {
        initializeTimer(); // 타이머 초기화
        createLayout(); // 테이블, 버튼 레이아웃 생성
        generateMines(buttons); // 지뢰 생성
        calculateNeighborMines(buttons); // 주변 지뢰 갯수 계산
    }

    // 타이머 초기화
    private void initializeTimer() {
        timer = new Chronometer(this);
        timer.setBase(SystemClock.elapsedRealtime());

        // 게임 시작 후 시간측정
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGameOver) { // 게임이 종료되었는지 확인
                    updateTimer();
                    handler.postDelayed(this, 100);  // 0.1초마다 반복
                }
            }
        }, 100);
    }

    // 시간 측정 및 제한
    private void updateTimer() {
        long currentTime = SystemClock.elapsedRealtime() - timer.getBase();
        timerTextView.setText(formatElapsedTime(currentTime));

        // 999초 이상 경과하면 게임 패배
        if (currentTime >= 999000) {
            isGameOver = true;
            disableBlocks();
            Toast.makeText(this, "Game Over 시간초과", Toast.LENGTH_SHORT).show();
        }
    }

    // 시간을 초단위로 변환
    private String formatElapsedTime(long milliseconds) {
        double seconds = milliseconds / 1000.0;  // 밀리초를 초로 변환
        return String.format("%.1f", seconds);  // 소수점 한 자리까지만 출력
    }

    // 초기 레이아웃 설정
    private void createLayout() {
        table = findViewById(R.id.tableLayout); // 테이블 레이아웃 생성

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1.0f);

        // 9X9 지뢰찾기 버튼 생성
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

        // 재도전 버튼 생성
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replayGame();
            }
        });
    }

    // 게임 재도전
    private void replayGame() {
        isGameOver = false;
        mineCountTextView.setText(String.valueOf(NUM_MINES));
        replayButton.setEnabled(true);
        tButton.setEnabled(true);

        // 기존 클릭 리스너 해제
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                buttons[i][j].setOnClickListener(null);
            }
        }

        table.removeAllViews(); // 기존 테이블 제거
        startGame();
    }

    // 지뢰 랜덤 배치
    private void generateMines(BlockButton[][] buttons) {
        Random rand = new Random();

        for (int i = 0; i < NUM_MINES; i++) {
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
        int remainingMines = NUM_MINES - button.flags; // 전체 지뢰 갯수 - 깃발의 갯수
        mineCountTextView.setText(String.format("%d", remainingMines));

        if (button.flags > NUM_MINES) { // 깃발의 갯수 > 전체 지뢰 갯수 -> Toast 메시지 출력
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
            if (button.isMine()) { // 지뢰 클릭시 게임 종료
                buttons[button.getBlockX()][button.getBlockY()].setBackgroundColor(Color.RED); // 클릭한 지뢰 배경색 변경
                endGame();
            }
        } else if (button.getNeighborMines() == 0 && !button.flag) {
            openBlocks(buttons, button.getBlockX(), button.getBlockY());
        }

        // 승리 조건 충족시 게임 종료
        if (checkWin()) {
            endGame();
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
    private void disableBlocks() {
        tButton.setEnabled(false);
        replayButton.setEnabled(false);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    // 게임 패배시 모든 블록을 오픈
    private void openAllBlocks() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                buttons[i][j].breakBlock(buttons);
            }
        }
    }

    // 게임 승리 조건에 충족하는지 확인
    private boolean checkWin() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!buttons[i][j].isMine() && buttons[i][j].isClickable()) {
                    return false;  // 아직 클릭되지 않은 버튼이 있으면 false 반환
                }
            }
        }
        return true;  // 모든 조건에 만족하면 true 반환
    }

    // 게임종료
    private void endGame() {
        if (!isGameOver) { // 이미 게임이 종료되었는지 확인 (Toast 메시지 중복 출력 방지)
            isGameOver = true;
            disableBlocks();

            if (checkWin()) {
                showWinDialog();
            } else {
                openAllBlocks();
                showLoseDialog();
            }
        }
    }

    // 승리시 출력 Dialog
    private void showWinDialog() {
        String endTime = timerTextView.getText().toString();

        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        TextView titleTextView = customLayout.findViewById(R.id.dialogTitle);
        TextView messageTextView = customLayout.findViewById(R.id.dialogMessage);

        titleTextView.setText("🎊Congratulations🎊");
        messageTextView.setText("기록 : " + endTime + "초\n" + "게임을 재시작하시겠습니까?");

        new AlertDialog.Builder(this)
                .setView(customLayout)
                .setCancelable(false)
                .setPositiveButton("예", (dialog, id) -> replayGame())
                .setNegativeButton("아니오", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    // 패배시 출력 Dialog
    private void showLoseDialog() {
        View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        TextView titleTextView = customLayout.findViewById(R.id.dialogTitle);
        TextView messageTextView = customLayout.findViewById(R.id.dialogMessage);

        titleTextView.setText("😓Game Over😓");
        messageTextView.setText("게임을 재시작하시겠습니까?");

        new AlertDialog.Builder(this)
                .setView(customLayout)
                .setCancelable(false)
                .setPositiveButton("예", (dialog, id) -> replayGame())
                .setNegativeButton("아니오", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

}