package ad.uade.tateti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_PLAYER_1_NAME = "ad.uade.tateti.PLAYER_1_NAME";
    public static final String EXTRA_PLAYER_1_OPTION = "ad.uade.tateti.PLAYER_1_OPTION";
    public static final String OPTION_CROSSES = "✕";
    public static final String OPTION_CIRCLES = "◯";
    private String player1Option = OPTION_CROSSES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setPlayer1Option(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.rbCrosses:
                if (checked) {
                    player1Option = OPTION_CROSSES;
                    break;
                }
            case R.id.rbCircles:
                if (checked) {
                    player1Option = OPTION_CIRCLES;
                    break;
                }
        }
    }

    public void startGame(View view) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        EditText etPlayer1Name = (EditText) findViewById(R.id.etPlayer1Name);

        String player1Name = etPlayer1Name.getText().toString();

        intent.putExtra(EXTRA_PLAYER_1_NAME, player1Name.isEmpty() ? getString(R.string.no_name) : player1Name);
        intent.putExtra(EXTRA_PLAYER_1_OPTION, player1Option);
        startActivity(intent);
    }
}