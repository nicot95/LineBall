package mygames.lineball.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import mygames.lineball.R;

public class AboutUsActivity extends Activity{

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
      //  view = findViewById(R.id.content);
    }

    private void addBackButton() {
        Button backButton = new Button(this);
        backButton.setText("Back");
        backButton.setTextColor(Color.WHITE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(intent);
            }
        });

        int diameter = 60;
        RelativeLayout.LayoutParams backButtonParams =
                new RelativeLayout.LayoutParams(diameter, diameter);
        backButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        backButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);

    }

}
