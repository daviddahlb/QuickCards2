package com.david.quickcards;

import android.animation.Animator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    int currentCardDisplayedIndex = 0;
    int EDIT_CARD_REQUEST_CODE = 200;
    int ADD_CARD_REQUEST_CODE = 100;
    Flashcard cardToEdit;

    //for accessing a random index, but not current index
    public int getRandomNumber(int minNumber, int maxNumber, int currentIndex) {

        //if min and max = 0, just return 0
        if (maxNumber == 0){
            return 0;
        }

        int to_return;
        //get a random int, but do not return currentIndex
        do {
            Random rand = new Random();
            to_return = rand.nextInt((maxNumber - minNumber) + 1) + minNumber;
        }while(to_return == currentIndex);

        return to_return;
    }

    //iterates through allFlashcards, returns index of the card
    public int getIndexOfCard(String s){

        allFlashcards = flashcardDatabase.getAllCards();
        int int_to_return = -1;
        for (int i = 0; i < allFlashcards.size(); i++)
        {
            if (s.equals(allFlashcards.get(i).getQuestion())){
                int_to_return = i;
            }
        }
        return int_to_return;
    }

    //based on size of database, shows empty state or not
    void setEmptyState(int list_size) {
        //if the size is 0, then displays empty_state and hides others
        if (list_size == 0) {
            //if database empty, hide everything and show empty_state image
                //set question, answer editTexts invisible
            findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
            findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);

                //set Delete, next, and edit Button invisible
            findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.next_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.edit_button).setVisibility(View.INVISIBLE);


            //set empty_state to visible
            findViewById(R.id.empty_state).setVisibility(View.VISIBLE);

        } else if (list_size > 0) {

           // currentCardDisplayedIndex = getRandomNumber(0, list_size - 1, currentCardDisplayedIndex);
            //if at least one in database, show question, hide answer, show all buttons
            //set question, delete, edit, and next buttons visible
            findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
            findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
            findViewById(R.id.next_button).setVisibility(View.VISIBLE);
            findViewById(R.id.edit_button).setVisibility(View.VISIBLE);

            //set answer to INVISIBLE
            findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);

            //set empty_state to INVISIBLE
            findViewById(R.id.empty_state).setVisibility(View.INVISIBLE);
        }
        //size is not 0 or greater?!
        else {
            Log.d("out of bounds", "somehow size of database is not greater than zero");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CARD_REQUEST_CODE && resultCode == RESULT_OK) {

            //gets returned strings from Activity
            String question = data.getExtras().getString("question");
            String answer = data.getExtras().getString("answer");

            //creates and inserts new Flashcard with question and answer strings
            Flashcard fc = new Flashcard(question, answer);
            flashcardDatabase.insertCard(fc);
            allFlashcards = flashcardDatabase.getAllCards();

            ((TextView) findViewById(R.id.flashcard_question)).setText(question);
            ((TextView) findViewById(R.id.flashcard_answer)).setText(answer);

            //reset current view state
            setEmptyState(allFlashcards.size());
        }
        else if (requestCode == EDIT_CARD_REQUEST_CODE && resultCode == RESULT_OK){
            String question = data.getExtras().getString("question");
            String answer = data.getExtras().getString("answer");

            //edit cardToEdit with returned strings
            cardToEdit.setQuestion(question);
            cardToEdit.setAnswer(answer);
            flashcardDatabase.updateCard(cardToEdit);
            ((TextView) findViewById(R.id.flashcard_question)).setText(cardToEdit.getQuestion());
            ((TextView) findViewById(R.id.flashcard_answer)).setText(cardToEdit.getAnswer());
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

                View answerSideView = findViewById(R.id.flashcard_answer);

                // get the center for the clipping circle
                int cx = answerSideView.getWidth() / 2;
                int cy = answerSideView.getHeight() / 2;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(answerSideView, cx, cy, 0f, finalRadius);

                // hide the question and show the answer to prepare for playing the animation!
                //questionSideView.setVisibility(View.INVISIBLE);
                answerSideView.setVisibility(View.VISIBLE);

                anim.setDuration(750);
                anim.start();
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

        //on click of PLUS image, switches activity to AddCardActivity
        //and prepares to receive the resulting data (q and a)
        findViewById(R.id.ic_plus).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        //on click of EDIT button, opens edit activity
        findViewById(R.id.edit_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //get the current question string in the TextView
                tv = findViewById(R.id.flashcard_question);
                String find_me_string = tv.getText().toString();

                cardToEdit = allFlashcards.get(getIndexOfCard(find_me_string));

                Intent myIntent = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(myIntent, 200);
            }
        });

        //on click of NEXT button, cycles to next question
        // and loops to beginning once end is reached
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //give random number to display index
                currentCardDisplayedIndex = getRandomNumber(0, allFlashcards.size() - 1, currentCardDisplayedIndex);

                //load animation resources
                final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.left_out);
                final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_in);

                //set up animation listeners for one animation to end and then the 2nd one begins
                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // this method is called when the animation first starts
                        //findViewById(R.id.flashcard_question).startAnimation(leftOutAnim);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // this method is called when the animation is finished playing
                        findViewById(R.id.flashcard_question).startAnimation(rightInAnim);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }
                });

                // display the random question/answer with data from the database
                ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());

                //show question, hide answer
                findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);

                findViewById(R.id.flashcard_question).startAnimation(leftOutAnim);


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
