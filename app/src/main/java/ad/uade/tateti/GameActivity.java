package ad.uade.tateti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameActivity extends AppCompatActivity {
    private boolean turnPlayer1;
    private String player1Name;
    private String player1Option;
    private String player2Option;
    private String turn;
    private String[][] tiles;
    private Set<String> tilesAvailable;
    private int machineDelay = 1000;
    private String winner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        player1Name = intent.getStringExtra(MainActivity.EXTRA_PLAYER_1_NAME);
        player1Option = intent.getStringExtra(MainActivity.EXTRA_PLAYER_1_OPTION);
        player2Option = player1Option.equals("✕") ? "◯" : "✕";

        TextView tvPlayer1Name = findViewById(R.id.tvPlayer1Name);
        TextView tvPlayer1PlaysWith = findViewById(R.id.tvPlayer1PlaysWith);
        tvPlayer1Name.setText(player1Name);
        tvPlayer1PlaysWith.setText(getString(R.string.plays_with) + " " +
                (player1Option.equals("✕") ? getString(R.string.crosses) : getString(R.string.circles)));

        restartGame(null);
    }

    private void changeTurn() {
        if (!checkGameState())
            return;

        turnPlayer1 = !turnPlayer1;
        turn = turnPlayer1 ? player1Option : player2Option;
        TextView tvTurnOfName = findViewById(R.id.tvTurnOfName);
        tvTurnOfName.setText(turnPlayer1 ? player1Name : getString(R.string.player_2_name));

        if (!turnPlayer1)
            machineMove();
    }

    public void clickTile(View view) {
        TextView tvMessage = findViewById(R.id.tvMessage);
        if (!tvMessage.getText().toString().isEmpty())
            return;

        String pos = view.getTag().toString();
        tiles[pos.charAt(0) - '0'][pos.charAt(1) - '0'] = turn;
        TextView tile = findViewById(getResources().getIdentifier("tile" + pos, "id", getPackageName()));
        if (tile.getText().equals("") && turnPlayer1) {
            tile.setText(turn);
            tilesAvailable.remove(pos);
            changeTurn();
        }
    }

    public void restartGame(View view) {
        setMessage(null);
        tiles = new String[3][3];
        tilesAvailable = new HashSet<String>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tiles[i][j] = "";
                setTileOption(i + "" + j, "");
                tilesAvailable.add(i + "" + j);
            }
        }
        turn = player1Option;
        turnPlayer1 = false;
        winner = "";
        changeTurn();
    }

    private void machineMove() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String tile = findTileTwoOfThree(turn);

                if (tile == null)
                    tile = findTileTwoOfThree(null);

                if (tile == null)
                    tile = findRandomTile();

                tiles[tile.charAt(0) - '0'][tile.charAt(1) - '0'] = turn;
                setTileOption(tile, turn);
                tilesAvailable.remove(tile);
                changeTurn();
            }
        }, machineDelay);
    }

    private String findTileTwoOfThree(String option) {
        String tile = null;
        for (int i = 0; i < 3 && tile == null; i++) {
            tile = checkTwoOccupied(i,0,i,1,i,2, option);
        }

        for (int j = 0; j < 3 && tile == null; j++) {
            tile = checkTwoOccupied(0,j,1,j,2,j, option);
        }

        if (tile == null)
            tile = checkTwoOccupied(0,0,1,1,2,2, option);

        if (tile == null)
            tile = checkTwoOccupied(0,2,1,1,2,0, option);

        return tile;
    }

    private String findRandomTile() {
        int size = tilesAvailable.size();
        int item = new Random().nextInt(size);
        int i = 0;
        for(String tileAvailable : tilesAvailable)
        {
            if (i == item) {
                return tileAvailable;
            }
            i++;
        }
        return null;
    }

    private String checkTwoOccupied(int row1, int col1, int row2, int col2, int row3, int col3, String option) {
        if (tiles[row1][col1].isEmpty() && !tiles[row2][col2].isEmpty() && !tiles[row3][col3].isEmpty() && tiles[row2][col2] == tiles[row3][col3] && (option == null || tiles[row2][col2] == option))
            return row1 + "" + col1;
        else if (!tiles[row1][col1].isEmpty() && tiles[row2][col2].isEmpty() && !tiles[row3][col3].isEmpty() && tiles[row1][col1] == tiles[row3][col3] && (option == null || tiles[row1][col1] == option))
            return row2 + "" + col2;
        else if (!tiles[row1][col1].isEmpty() && !tiles[row2][col2].isEmpty() && tiles[row3][col3].isEmpty() && tiles[row1][col1] == tiles[row2][col2] && (option == null || tiles[row1][col1] == option))
            return row3 + "" + col3;
        return null;
    }
    
    private boolean checkGameState() {
        for (int i = 0; i < 3; i++) {
            if (!tiles[i][0].equals("") && tiles[i][0] == tiles[i][1] && tiles[i][0] == tiles[i][2]) {
                winner = player1Option == tiles[i][0] ? player1Name : getString(R.string.player_2_name);
                setMessage(winner);
                return false;
            }
        }

        for (int j = 0; j < 3; j++) {
            if (!tiles[0][j].equals("") && tiles[0][j] == tiles[1][j] && tiles[0][j] == tiles[2][j]) {
                winner = player1Option == tiles[0][j] ? player1Name : getString(R.string.player_2_name);
                setMessage(winner);
                return false;
            }
        }

        if (!tiles[1][1].equals("") && ((tiles[1][1] == tiles[0][0] && tiles[1][1] == tiles[2][2]) || (tiles[1][1] == tiles[0][2] && tiles[1][1] == tiles[2][0]))) {
            winner = player1Option == tiles[1][1] ? player1Name : getString(R.string.player_2_name);
            setMessage(winner);
            return false;
        }

        if (tilesAvailable.isEmpty()) {
            setMessage("");
            return false;
        }

        return true;
    }

    private void setTileOption(String pos, String text) {
        ((Button) findViewById(getResources().getIdentifier("tile" + pos, "id", getPackageName()))).setText(text);
    }

    private void setMessage(String winner) {
        TextView tvMessage = findViewById(R.id.tvMessage);

        if (winner == null) {
            tvMessage.setText("");
            tvMessage.setBackgroundColor(Color.TRANSPARENT);
        } else if (winner.isEmpty()) {
            tvMessage.setText(getString(R.string.result_draw));
            tvMessage.setBackgroundColor(Color.YELLOW);
        } else if (winner.equals(player1Name)) {
            tvMessage.setText(getString(R.string.result_won));
            tvMessage.setBackgroundColor(Color.GREEN);
        } else if (winner.equals(getString(R.string.player_2_name))) {
            tvMessage.setText(getString(R.string.result_lose));
            tvMessage.setBackgroundColor(Color.RED);
        }
    }
}