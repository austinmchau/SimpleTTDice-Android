package com.austinmchau.simplettdice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "amcMsg";

    //
    //Mark: UI Elements
    //

    Button rollButton;
    Button rollButtonLeft;
    Button rollButtonRight;

    TextView outputLabel;

    Spinner typeOfDiceSpinner;

    TableViewFragment tableViewFragment;

    private void addListenerOnButtons() {
        rollButton = (Button) findViewById(R.id.rollButton);
        rollButtonLeft = (Button) findViewById(R.id.rollButtonLeft);
        rollButtonRight = (Button) findViewById(R.id.rollButtonRight);

        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer results = diceRoller.roll(currentNumberOfDice, currentTypeOfDice);
                outputLabel.setText(results.toString());
                tableViewFragment.addHistory(getHistoryOutput(), getHistoryTimeStamp());
            }
        });

        rollButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentNumberOfDice(-1);
            }
        });

        rollButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentNumberOfDice(+1);
            }
        });
    }

    //
    //Mark: Data Storage
    //

    private int currentNumberOfDice = 1;
    private void setCurrentNumberOfDice(int incrementBy) {
        currentNumberOfDice += incrementBy;
        if (currentNumberOfDice < 1) {
            currentNumberOfDice = 1;
        }
        if (currentNumberOfDice != 1) {
            rollButton.setText("Roll ×"+currentNumberOfDice);
        } else {
            rollButton.setText("Roll");
        }
    }

    private DiceType currentTypeOfDice;

    private ACSTTDiceRoller.DiceRoller diceRoller = new ACSTTDiceRoller.DiceRoller();

    //
    //Mark: Activity Life Cycle
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputLabel = (TextView) findViewById(R.id.outputLabel);
        typeOfDiceSpinner = (Spinner) findViewById(R.id.typeOfDiceSpinner);
        //set the default according to value
        typeOfDiceSpinner.setSelection(2); //2 = D6

        currentTypeOfDice = DiceType.values()[typeOfDiceSpinner.getSelectedItemPosition()];

        //Spinner Setup
        typeOfDiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentTypeOfDice = DiceType.values()[parent.getSelectedItemPosition()];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addListenerOnButtons();

        tableViewFragment = (TableViewFragment) getSupportFragmentManager().findFragmentById(R.id.tableViewFragment);
    }

    //
    //Mark: Option Menu
    //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_option_reset_rolls:
                outputLabel.setText(R.string.output_label_default);
                diceRoller.reset();
                tableViewFragment.resetHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("outputLabel", outputLabel.getText().toString());
        outState.putInt("currentNumberOfDice", currentNumberOfDice);
        outState.putSerializable("currentTypeOfDice", currentTypeOfDice);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        outputLabel.setText(savedInstanceState.getString("outputLabel"));
        currentNumberOfDice = savedInstanceState.getInt("currentNumberOfDice");
        currentTypeOfDice = (DiceType) savedInstanceState.getSerializable("currentTypeOfDice");
        if (currentNumberOfDice != 1) {
            rollButton.setText("Roll ×"+currentNumberOfDice);
        } else {
            rollButton.setText("Roll");
        }
    }

    //
    //Mark: Private Convenience
    //

    private String getHistoryOutput() {
        ACSTTDiceRoller.DiceSet roll = diceRoller.diceRollHistory().get(diceRoller.diceRollHistory().size() - 1);

        String diceType = roll.numberOfDiceOnHand() + currentTypeOfDice.toString().toLowerCase(); //localize?

        String rollNumber = "";
        if (roll.diceResults().size() <= 6) {
            ArrayList<String> rolls = new ArrayList<>();
            for (Integer results: roll.diceResults()) {
                rolls.add(results.toString());
            }
            rollNumber += TextUtils.join(", ", rolls);
        } else {
            rollNumber = "…";
        }

        return diceType + ": [" + rollNumber + "] = " + roll.diceResultsTotal();

    }

    private String getHistoryTimeStamp() {
        ACSTTDiceRoller.DiceSet roll = diceRoller.diceRollHistory().get(diceRoller.diceRollHistory().size() - 1);
        return DateFormat.getTimeInstance().format(roll.rolledTimeStamp());
        //return "0";
    }

    private String diceTypeToString(DiceType diceType) {
        int id = getResources().getIdentifier(currentTypeOfDice.toString().toLowerCase(), "string", "com.austinmchau.simplettdice");
        return getResources().getString(id);
    }

}
