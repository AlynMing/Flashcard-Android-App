package com.example.lab1_codepath;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // To get an instance of the database
    FlashcardDatabase flashcardDatabase;
    // List that holds the flashcard objects
    List<Flashcard> allFlashcards;
    // Index of the card to be displayed, starts at 0 (first card)
    int currentCardDisplayedIndex = 0;
    int camera_distance = 25000;

    boolean isBlankString(String string) {
        return string == null || string.trim().isEmpty();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        // Access the cards
        allFlashcards = flashcardDatabase.getAllCards();

        // Check if the list of flashcards is empty
        // If not, display a saved flashcard
        if (allFlashcards != null && allFlashcards.size() > 0) { // If there are flashcards
            ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(0).getQuestion());
            ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(0).getAnswer());
        }
        else{
            ((TextView) findViewById(R.id.flashcard_question)).setText("Add a card!");
            ((TextView) findViewById(R.id.flashcard_answer)).setText("Add a card!");
        }
//        if (allFlashcards.size() > 0) {  // Only reveal answers if there are cards in the database
//            findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
//            findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
//        }

        findViewById(R.id.flashcard_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allFlashcards.size() > 0) {  // Only reveal answers if there are cards in the database
                    //findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
                    //findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);

                    // Add a reveal animation for when the answer is revealed
                    final View answerSideView = findViewById(R.id.flashcard_answer);
                    final View questionSideView = findViewById(R.id.flashcard_question);

                    findViewById(R.id.flashcard_question).setCameraDistance(camera_distance);
                    findViewById(R.id.flashcard_answer).setCameraDistance(camera_distance);
                    questionSideView.animate()
                            .rotationY(90)
                            .setDuration(200)
                            .withEndAction(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            questionSideView.setVisibility(View.INVISIBLE);
                                            questionSideView.setRotationY(0);
                                            findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
                                            // second quarter turn
                                            findViewById(R.id.flashcard_answer).setRotationY(-90);
                                            findViewById(R.id.flashcard_answer).animate()
                                                    .rotationY(0)
                                                    .setDuration(200)
                                                    .start();
                                        }
                                    }
                            ).start();

//                    // get the center for the clipping circle
////                    int cx = answerSideView.getWidth() / 2;
////                    int cy = answerSideView.getHeight() / 2;
////
////                    // get the final radius for the clipping circle
////                    float finalRadius = (float) Math.hypot(cx, cy);
////
////                    // create the animator for this view (the start radius is zero)
////                    Animator anim = ViewAnimationUtils.createCircularReveal(answerSideView, cx, cy, 0f, finalRadius);
////
////                    // hide the question and show the answer to prepare for playing the animation!
////                    questionSideView.setVisibility(View.INVISIBLE);
////                    answerSideView.setVisibility(View.VISIBLE);
////
////                    anim.setDuration(1000);
////                    anim.start();

                }
            }
        });

        findViewById(R.id.relativeLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allFlashcards.size() > 0 && findViewById(R.id.flashcard_answer).getVisibility() == View.VISIBLE){  // Only reveal answers if there are cards in the database
                    //findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                    //findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);

                    final View answerSideView = findViewById(R.id.flashcard_answer);
                    final View questionSideView = findViewById(R.id.flashcard_question);

                    findViewById(R.id.flashcard_question).setCameraDistance(camera_distance);
                    findViewById(R.id.flashcard_answer).setCameraDistance(camera_distance);
                    answerSideView.animate()
                            .rotationY(90)
                            .setDuration(200)
                            .withEndAction(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            answerSideView.setVisibility(View.INVISIBLE);
                                            findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                                            answerSideView.setRotationY(0);
                                            // second quarter turn
                                            findViewById(R.id.flashcard_question).setRotationY(-90);
                                            findViewById(R.id.flashcard_question).animate()
                                                    .rotationY(0)
                                                    .setDuration(200)
                                                    .start();
                                        }
                                    }
                            ).start();
                }
            }
        });

        // Navigate to the activity that will allow users to add a flash card
        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAddCard = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(toAddCard, 100);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        // Next button that changes flashcards
        findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.left_out);
                final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_in);

                // advance our pointer index so we can show the next card
                currentCardDisplayedIndex++;

                // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
                if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                    currentCardDisplayedIndex = 0;
                }

                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // this method is called when the animation first starts

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // this method is called when the animation is finished playing
                        // Check that there are flashcards in the database. If there are, play swiping animation on transition.
                        if(allFlashcards.size() > 1) {
                            ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                            ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());

                            // Check if the question is already visible
                            if (findViewById(R.id.flashcard_question).getVisibility() == View.VISIBLE){
                                findViewById(R.id.flashcard_question).startAnimation(rightInAnim);
                            }
                            else{
                                findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                                findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
                                findViewById(R.id.flashcard_question).startAnimation(rightInAnim);
                            }
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }
                });

                // If there are cards in the database, play swiping animation
                if(allFlashcards.size() > 1) {
                    if (findViewById(R.id.flashcard_question).getVisibility() == View.VISIBLE){
                        findViewById(R.id.flashcard_question).startAnimation(leftOutAnim);
                    }
                    else{
                        findViewById(R.id.flashcard_answer).startAnimation(leftOutAnim);
                    }
                }

            }
        });

        // Button to delete flashcards
        findViewById(R.id.deleteTrashButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the current card
                flashcardDatabase.deleteCard(((TextView) findViewById(R.id.flashcard_question)).getText().toString());
                // Update the current card index
                currentCardDisplayedIndex--;
                // Make sure that the index does not go below 0
                if (currentCardDisplayedIndex < 0){
                    currentCardDisplayedIndex = 0;
                }
                // Update cards after deletion
                allFlashcards = flashcardDatabase.getAllCards();

                // If there are no cards left in the database, display
                // an 'empty' state by updating the question TextView to display "Add a card!"
                if (allFlashcards.size() == 0) {
                    ((TextView) findViewById(R.id.flashcard_question)).setText("Add a card!");
                    ((TextView) findViewById(R.id.flashcard_answer)).setText("Add a card!");
                }
                // Else, display the previous card
                else{
                    // set the question and answer TextViews with data from the database
                    ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                    ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 & data != null){ // this 100 needs to match the 100 we used when we called startActivityForResult!
            String question = data.getExtras().getString("question"); // 'string1' needs to match the key we used when we put the string in the Intent
            String answer = data.getExtras().getString("answer");
            // If the user gives inputs for question and answer:
            if (!isBlankString(question) && !isBlankString(answer)) {
                ((TextView) findViewById(R.id.flashcard_question)).setText(question);
                ((TextView) findViewById(R.id.flashcard_answer)).setText(answer);

                // We now need to save data into the database
                // 'question' and 'answer' are the data extracted previously
                flashcardDatabase.insertCard(new Flashcard(question, answer));
                // Update currentCardDisplayedIndex
                currentCardDisplayedIndex = allFlashcards.size();
                // Update list of flashcards
                allFlashcards = flashcardDatabase.getAllCards();

                // Snackbar message to show that card has been added
                Snackbar.make(findViewById(R.id.flashcard_question),
                        "Card successfully created.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
            else{
                // Snackbar message to show that card has not been added
                Snackbar.make(findViewById(R.id.flashcard_question),
                        "Error: Please enter a question and an answer.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        }
    }
}
