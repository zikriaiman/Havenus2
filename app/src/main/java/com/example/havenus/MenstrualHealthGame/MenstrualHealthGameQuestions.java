package com.example.havenus.MenstrualHealthGame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.havenus.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenstrualHealthGameQuestions extends AppCompatActivity {
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;

    private ImageView questionImageView;
    private TextView questionTextView;
    private TextView questionNumberTextView;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private Button submitButton;
    private int[] headerImageResources = {R.drawable.q1, R.drawable.q2, R.drawable.q3, R.drawable.q4, R.drawable.q5, R.drawable.q6, R.drawable.q7, R.drawable.q8, R.drawable.q9, R.drawable.q10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menstrual_health_game_questions);

        // Initialize your views
        questionImageView = findViewById(R.id.header_picture);
        questionTextView = findViewById(R.id.question);
        questionNumberTextView = findViewById(R.id.questionNumber);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        submitButton = findViewById(R.id.submitButton);

        // Initialize questionList with your questions
        questionList = getQuestions();

        // Display the first question
        displayQuestion();
    }

    private List<Question> getQuestions() {
        // Add your questions here
        List<Question> questions = new ArrayList<>();

        List<String> options1 = Arrays.asList("" +
                        "First day of a new calendar month",
                "First day of your period",
                "First day after your period has finished",
                "Whenever; you can decide when your cycle starts as long as you keep track");
        questions.add(new Question("What counts as the first day of a new menstrual cycle?", options1, 1));

        List<String> options2 = Arrays.asList("" +
                        "2 days",
                "4 days",
                "6 days",
                "8 days");
        questions.add(new Question("Which of the following would be considered an abnormal length of time for a period to last?", options2, 3));

        List<String> options3 = Arrays.asList("" +
                        "The absence of period",
                "Having 2 periods in 1 month",
                "An irregular cycle",
                "Starting your period at 8 years old or younger");
        questions.add(new Question("What does the word “amenorrhea” mean?", options3, 0));

        List<String> options4 = Arrays.asList("" +
                        "26 days",
                "28 days",
                "30 days",
                "32 days");
        questions.add(new Question("How long is the average menstrual cycle?", options4, 1));

        List<String> options5 = Arrays.asList("" +
                        "Low body weight (10% or more under normal weight",
                "Polycystic ovary syndrome (PCOS)",
                "Stress",
                "All of the above");
        questions.add(new Question("Which of the following is a cause of amenorrhea in women or people who were previously menstruating (when periods stop for 3 months or more)?", options5, 3));

        List<String> options6 = Arrays.asList("" +
                        "Bacteriol vaginosis",
                "Polycystic ovary syndrome (PCOS)",
                "Thrush",
                "None of the above");
        questions.add(new Question("Irregular periods can be a key symptom of what gynecological condition?", options6, 1));

        List<String> options7 = Arrays.asList("" +
                        "Light pink",
                "Bright red",
                "Brown",
                "All of the above");
        questions.add(new Question("What color is normal for period blood?", options7, 3));

        List<String> options8 = Arrays.asList("" +
                        "Food poisoning",
                "Normal variation in periods",
                "Thrush",
                "Being extra fertile");
        questions.add(new Question("What is one of the most common causes of a light period (a period that is shorter in duration than is usual for the individual)?", options8, 1));

        List<String> options9 = Arrays.asList("" +
                        "To regulate body temperature",
                "To prepare the body for pregnancy",
                "To cleanse the reproductive organs",
                "To control appetite");
        questions.add(new Question("What is the purpose of menstrual cycle?", options9, 1));


        List<String> options10 = Arrays.asList("" +
                        "Regular exercise",
                "High-stress levels",
                "Adequate sleep",
                "All of the above");
        questions.add(new Question("Which lifestyle factor can impact menstrual health?", options10, 3));

        return questions;
    }

    private void displayQuestion() {
        // Uncheck all radio buttons
        radioButton1.setChecked(false);
        radioButton2.setChecked(false);
        radioButton3.setChecked(false);
        radioButton4.setChecked(false);

        // Update UI to display current question
        Question currentQuestion = questionList.get(currentQuestionIndex);

        questionTextView.setText(currentQuestion.getQuestionText());

        // Reset text color to default for all radio buttons
        radioButton1.setTextColor(Color.BLACK);
        radioButton2.setTextColor(Color.BLACK);
        radioButton3.setTextColor(Color.BLACK);
        radioButton4.setTextColor(Color.BLACK);

        // Set question number
        questionNumberTextView.setText((currentQuestionIndex + 1) + "/" + questionList.size());

        // Set header picture based on the question number
        questionImageView.setImageResource(headerImageResources[currentQuestionIndex]);

        // Create a list of RadioButtons
        List<RadioButton> radioButtons = Arrays.asList(radioButton1, radioButton2, radioButton3, radioButton4);

        // Iterate through the options and set text for each RadioButton
        for (int i = 0; i < currentQuestion.getOptions().size(); i++) {
            // Enable all radio buttons
            radioButtons.get(i).setEnabled(true);
            radioButtons.get(i).setText(currentQuestion.getOptions().get(i));
        }

        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswerAndProceed();
            }
        });
    }

    private void checkAnswerAndProceed() {
        int selectedRadioButtonId = getCheckedRadioButtonId();

        if (selectedRadioButtonId != -1) {
            int selectedAnswerIndex = getRadioButtonIndex(selectedRadioButtonId);

            // Check if the selected answer is correct
            boolean isCorrect = (selectedAnswerIndex == questionList.get(currentQuestionIndex).getCorrectAnswer());

            // Highlight both wrong and correct answers
            highlightCorrectAnswer(isCorrect, selectedAnswerIndex);

            // Move to the next question or finish the quiz after a delay
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (currentQuestionIndex < questionList.size() - 1) {
                        currentQuestionIndex++;
                        displayQuestion();
                    } else {
                        showResult();
                    }
                }
            }, 1000); // Delay for 1000 milliseconds (1 second)
        } else {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
        }
    }

    private void highlightCorrectAnswer(boolean isCorrect, int selectedAnswerIndex) {
        List<RadioButton> radioButtons = Arrays.asList(radioButton1, radioButton2, radioButton3, radioButton4);
        int correctAnswerIndex = questionList.get(currentQuestionIndex).getCorrectAnswer();

        for (int i = 0; i < radioButtons.size(); i++) {
            if (i == correctAnswerIndex) {
                radioButtons.get(i).setTextColor(Color.GREEN);
                if (isCorrect) {
                    // Increment score if the answer is correct
                    score++;
                }
            } else if (i == selectedAnswerIndex) {
                radioButtons.get(i).setTextColor(Color.RED);
            } else {
                radioButtons.get(i).setEnabled(false);
            }
        }
    }

    private int getCheckedRadioButtonId() {
        if (radioButton1.isChecked()) {
            return radioButton1.getId();
        } else if (radioButton2.isChecked()) {
            return radioButton2.getId();
        } else if (radioButton3.isChecked()) {
            return radioButton3.getId();
        } else if (radioButton4.isChecked()) {
            return radioButton4.getId();
        }

        return -1;
    }

    private int getRadioButtonIndex(int radioButtonId) {
        List<RadioButton> radioButtons = Arrays.asList(radioButton1, radioButton2, radioButton3, radioButton4);

        for (int i = 0; i < radioButtons.size(); i++) {
            if (radioButtons.get(i).getId() == radioButtonId) {
                return i;
            }
        }

        return -1;
    }


    private void showResult() {
        // Navigate to ResultActivity
        Intent intent = new Intent(MenstrualHealthGameQuestions.this, MenstrualHealthGameResult.class);
        // Pass the score to the ResultActivity
        intent.putExtra("SCORE_EXTRA", score);
        startActivity(intent);

        // Finish the current activity if needed
        finish();
    }

}
// Question.java
class Question {
    private String questionText;
    private List<String> options;
    private int correctAnswer;

    public Question(String questionText, List<String> options, int correctAnswer) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}
