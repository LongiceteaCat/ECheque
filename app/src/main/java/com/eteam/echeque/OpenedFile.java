package com.eteam.echeque;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.text.SimpleDateFormat;
import java.util.List;

public class OpenedFile extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;
    private Bitmap image;
    private String resultText;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_file);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageView = findViewById(R.id.FullScr);
        //textView = findViewById(R.id.scanned);
        image = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        imageView.setImageBitmap(image);
       // textView.setMovementMethod(new ScrollingMovementMethod());
        //textView.setText("Scanned text here");
        if(image !=null) {
            runTextRecognition();
        }
    }
    private void runTextRecognition(){
        FirebaseVisionImage check = FirebaseVisionImage.fromBitmap(image);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(check).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);

            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
        String resultText = firebaseVisionText.getText();
        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        if (blocks.size()==0) {
            Toast.makeText(this,"No text found!", Toast.LENGTH_SHORT).show();
           // textView.setText("No text found");
            return;
        }else {
            for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()) {
                String blockText = block.getText();
                Float blockConfidence = block.getConfidence();
                List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                Point[] blockCornerPoints = block.getCornerPoints();
                Rect blockFrame = block.getBoundingBox();
                for (FirebaseVisionText.Line line: block.getLines()) {
                    String lineText = line.getText();
                    Float lineConfidence = line.getConfidence();
                    List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                    Point[] lineCornerPoints = line.getCornerPoints();
                    Rect lineFrame = line.getBoundingBox();
                    for (FirebaseVisionText.Element element: line.getElements()) {
                        String elementText = element.getText();
                        Float elementConfidence = element.getConfidence();
                        List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                        Point[] elementCornerPoints = element.getCornerPoints();
                        Rect elementFrame = element.getBoundingBox();
                    }
                }
            }
            //SimpleDateFormat
            //textView.setText(resultText);
        }

           /* for(FirebaseVisionText.TextBlock ablock : firebaseVisionText.getTextBlocks() )
            {
                String text = ablock.getText();
                textView.setText(text);
            }
        }*/

    }
   /* private void processTextRecognitionResult(FirebaseVisionText texts){
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size()==0) {
            Toast.makeText(this,"No text found!", Toast.LENGTH_SHORT).show();
            return;
        }
        resultText = "";
        for (int i = 0;i<blocks.size();i++){
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for(int j = 0;j<lines.size();j++){
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for(int k = 0;k<elements.size();k++){
                   resultText
                }
            }
        }
    }*/
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.menu_scan, menu);
       return true;
   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                Toast.makeText(this, "Scan", Toast.LENGTH_SHORT).show();
                textView.setText("");
                runTextRecognition();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
