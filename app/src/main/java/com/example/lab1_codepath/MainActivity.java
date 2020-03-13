package com.example.lab1_codepath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // To get an instance of the database
    FlashcardDatabase flashcardDatabase;
    // List that holds the flashcard objects
    List<Flashcard> allFlashcards;
    // Index of the card to be displayed, starts at 0 (first card)
    int currentCardDisplayedIndex = 0;

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

        }

        findViewById(R.id.flashcard_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
                findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
            }
        });

        findViewById(R.id.relativeLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
            }
        });

        // Navigate to the activity that will allow users to add a flash card
        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAddCard = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(toAddCard, 100);
            }
        });

        // Next button that changes flashcards
        findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // advance our pointer index so we can show the next card
                    currentCardDisplayedIndex++;

                    // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
                    if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                        currentCardDisplayedIndex = 0;
                    }

                    // If there are cards, set the question and answer TextViews with data from the database
                    if(allFlashcards != null && allFlashcards.size() > 0) {
                        ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                        ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
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

            ((TextView) findViewById(R.id.flashcard_question)).setText(question);
            ((TextView) findViewById(R.id.flashcard_answer)).setText(answer);

            // We now need to save data into the database
            // 'question' and 'answer' are the data extracted previously
            flashcardDatabase.insertCard(new Flashcard(question, answer));
            // Update list of flashcards
            allFlashcards = flashcardDatabase.getAllCards();
        }
    }
}
