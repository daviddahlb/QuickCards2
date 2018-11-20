package com.david.quickcards;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView tv_question, tv_answer;
    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    int currentCardDisplayedIndex = 0;

    //for accessing a random index
    public int getRandomNumber(int minNumber, int maxNumber, int currentIndex) {

        if (maxNumber == 0){
            return 0;
        }

        int to_return = -1;
        //get a random int, but do not return currentIndex
        do {
            Log.d("test", "generating random..." + to_return + " ");
            Random rand = new Random();
            to_return = rand.nextInt((maxNumber - minNumber) + 1) + minNumber;
        }while(to_return == currentIndex);

        return to_return;
    }


    //based on size of database, shows empty state or not
    void setEmptyState(int list_size) {
        //if the size is 0, then displays empty_state and hides others
        if (list_size == 0) {
            //if database empty, hide everything
            //set question, answer editTexts invisible
            //set Delete button and next Button invisible
            findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
            findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
            findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.next_button).setVisibility(View.INVISIBLE);

            //set empty_state to visible
            findViewById(R.id.empty_state).setVisibility(View.VISIBLE);

        } else if (list_size > 0) {

            currentCardDisplayedIndex = getRandomNumber(0, list_size - 1, currentCardDisplayedIndex);
            //if at least one in database, show question, hide answer, show all buttons
            //set question, delete_button, and next_button visible
            findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
            findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
            findViewById(R.id.next_button).setVisibility(View.VISIBLE);

            //set answer to INVISIBLE
            findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);


            //set empty_state to visible
            findViewById(R.id.empty_state).setVisibility(View.INVISIBLE);
        }
        //size is not 0 or greater?!
        else {
            Log.d("out of bounds", "somehow size of database is not greater than zero");
        }

        //if size is > 0, then invisible empty_state and vis necessary buttons
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) { // this 100 needs to match the 100 we used when we called startActivityForResult!
            String question = data.getExtras().getString("question"); // 'string1' needs to match the key we used when we put the string in the Intent
            String answer = data.getExtras().getString("answer");
            tv_question = findViewById(R.id.flashcard_question);
            tv_answer = findViewById(R.id.flashcard_answer);
            tv_question.setText(question);
            tv_answer.setText(answer);

            flashcardDatabase.insertCard(new Flashcard(question, answer));
            allFlashcards = flashcardDatabase.getAllCards();
            setEmptyState(allFlashcards.size());

        }
    }

    //////////// ON CREATE //////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashcardDatabase = new FlashcardDatabase(this);
        allFlashcards = flashcardDatabase.getAllCards();
        setEmptyState(allFlashcards.size());

        if (allFlashcards != null && allFlashcards.size() > 0) {
            ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(0).getQuestion());
            ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(0).getAnswer());
        }

        //on click of question, hide question and show answer
        findViewById(R.id.flashcard_question).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
            }
        });

        //on click of answer, show question and hide answer
        findViewById(R.id.flashcard_answer).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
            }
        });

        //on click of PLUS image, switches activity to addQuestionActivity
        //and prepares to receive the resulting data (q and a)
        findViewById(R.id.ic_plus).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, addQuestionActivity.class);
                MainActivity.this.startActivityForResult(intent, 100);
            }
        });

        //on click of NEXT button, cycles to next question
        // and loops to beginning once end is reached
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //give random number to display index
                currentCardDisplayedIndex = getRandomNumber(0, allFlashcards.size() - 1, currentCardDisplayedIndex);

                // display the random question/answer with data from the database
                ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());

                //show question, hide answer
                findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
            }
        });

        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //gets string of the current displayed card question and calls deleteCard
                flashcardDatabase.deleteCard(((TextView) findViewById(R.id.flashcard_question)).getText().toString());

                //update List of cards
                allFlashcards = flashcardDatabase.getAllCards();
                setEmptyState(allFlashcards.size());

                //reset current index to previous card index or 0
                //if there are at least one card still in the database, decrement index
                if (allFlashcards.size() > 0){

                    //if not empty, display random card
                    currentCardDisplayedIndex = getRandomNumber(0, allFlashcards.size() - 1, currentCardDisplayedIndex);
                    ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                    ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());

                    //hide answer, show question
                    findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                    findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
                }
             }
        });
    }
}
