package com.austinmchau.simplettdice;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class ACSTTDiceRoller {

    static final String TAG = "amcMsg";

    public static class Dice {

        //
        //Mark: Constructor
        //

        Dice(DiceType diceType) {
            this.diceType = diceType;
        }
        
        //
        //Mark: Data Store
        //

        private DiceType diceType;

        //
        //Mark: Getter/Setter
        //

        public DiceType getDiceType () { return  diceType; }

        //
        //Mark: Class Interfacing
        //

        public int rollDice() {
            int result = 0;
            switch (diceType) {
                case D20:
                    result = randomizer(1, 20, 1);
                    break;
                case D10:
                    result = randomizer(1, 10, 1);
                    break;
                case D6:
                    result = randomizer(1, 6, 1);
                    break;
                case D12:
                    result = randomizer(1, 12, 1);
                    break;
                case D4:
                    result = randomizer(1, 4, 1);
                    break;
                case D8:
                    result = randomizer(1, 8, 1);
                    break;
                case D100:
                    result = randomizer(1, 10, 10);
                    break;
            }
            return result;
        }

        private int randomizer(int min, int max, int interval) {
            final Random random = new Random();
            int rand = random.nextInt(max - min + 1) + 1;
            return rand * interval;
        }
    }

    public static class DiceSet {

        //
        //Mark: Constructor
        //



        //
        //Mark: Vars
        //

        private ArrayList<Dice> currentDiceOnhand = new ArrayList<Dice>();
        private ArrayList<Integer> currentDiceResults = new ArrayList<Integer>();
        private DiceType currentDiceTypes;
        private Date timeStamp;

        //
        //Mark: Getters/Setters
        //

        public ArrayList<Dice> diceOnHand() { return currentDiceOnhand; }
        public ArrayList<Integer> diceResults() { return currentDiceResults; }
        public int diceResultsTotal() {
            int result = 0;
            for (Integer r : currentDiceResults) {
                result += r.intValue();
            }
            return result;
        }
        public int numberOfDiceOnHand() { return currentDiceOnhand.size(); }
        public DiceType diceTypes() { return currentDiceTypes; }
        public Date rolledTimeStamp() { return timeStamp; }

        //
        //Mark: Class Interfacing
        //

        public int rollNewSet(int amountOfDice, DiceType ofType) {
            reset();
            addDices(amountOfDice, ofType);
            currentDiceTypes = ofType;
            rollAllDices();
            return diceResultsTotal();
        }

        //
        //Mark: Private backend
        //

        private void addDices(int amount, DiceType ofType) {
            for (int i = 0; i < amount; i++) {
                currentDiceOnhand.add(new Dice(ofType));
            }
        }

        private ArrayList<Integer> rollAllDices() {
            currentDiceResults = new ArrayList<Integer>();
            for (Dice dice: currentDiceOnhand) {
                currentDiceResults.add(new Integer(dice.rollDice()));
            }
            timeStamp = new Date();
            return currentDiceResults;
        }

        private void reset() {
            currentDiceOnhand = new ArrayList<Dice>();
            currentDiceResults = new ArrayList<Integer>();
        }
    }

    public static class DiceRoller {
        //
        //Mark: Data Store
        //

        private ArrayList<DiceSet> diceRolls = new ArrayList<DiceSet>();

        //
        //Mark: Getters/Setters
        //

        public ArrayList<DiceSet> diceRollHistory() { return diceRolls; }
        public DiceSet currentRoll() { return diceRolls.get(diceRolls.size() - 1); }

        //
        //Mark: Class Interfacing
        //

        public int roll(int amountOfDice, DiceType ofType) {
            diceRolls.add(new DiceSet());
            return currentRoll().rollNewSet(amountOfDice, ofType);
        }

        public ArrayList<Integer> rollReturnList(int amountOfDice, DiceType ofType) {
            diceRolls.add(new DiceSet());
            currentRoll().rollNewSet(amountOfDice, ofType);
            return currentRoll().diceResults();
        }

        public void reset() {
            diceRolls.clear();
        }
    }
}
