package com.eteam.echeque;


import android.os.Bundle;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class OpenedFile extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_file);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageView = findViewById(R.id.FullScr);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int resId = bundle.getInt("resId");
            imageView.setImageResource(resId);
        }
    }
}
