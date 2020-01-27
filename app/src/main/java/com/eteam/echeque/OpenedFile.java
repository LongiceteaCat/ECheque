package com.eteam.echeque;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.security.Permission;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenedFile extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_OK = 1;
    private static final int PERMISSION_REQUEST = 0;
    private static final int SELECT_PICTURE = 1;
    private ImageView imageView;
    private TextView textDate;
    private TextView textTotal;
    private Bitmap image;
    private String resultText;
    private Date date;
    private String pattern="\\d{2}\\. \\d{2}\\. \\d{4}";
    private String strFormat = new String("dd. mm .yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_file);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageView = findViewById(R.id.FullScr);
        textDate = findViewById(R.id.date_text);
        textTotal = findViewById(R.id.total_text);
        image = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        imageView.setImageBitmap(image);
        textDate.setMovementMethod(new ScrollingMovementMethod());
        textTotal.setMovementMethod(new ScrollingMovementMethod());
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }
        if(image !=null) {
            runTextRecognition();
        }
    }
    @Override
    public void onRequestPermissionsResult (int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults){
        switch (requestCode){
            case PERMISSION_REQUEST:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permissian granted!",Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(this,"Permissian not granted!",Toast.LENGTH_SHORT).show();
                    finish();
                }
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
            for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                String blockText = block.getText();
                Float blockConfidence = block.getConfidence();
                List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                Point[] blockCornerPoints = block.getCornerPoints();
                Rect blockFrame = block.getBoundingBox();
                for (FirebaseVisionText.Line line : block.getLines()) {
                    String lineText = line.getText();
                    Float lineConfidence = line.getConfidence();
                    List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                    Point[] lineCornerPoints = line.getCornerPoints();
                    Rect lineFrame = line.getBoundingBox();
                    for (FirebaseVisionText.Element element : line.getElements()) {
                        String elementText = element.getText();
                        Float elementConfidence = element.getConfidence();
                        List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                        Point[] elementCornerPoints = element.getCornerPoints();
                        Rect elementFrame = element.getBoundingBox();
                    }
                }
            }


            textTotal.setText(resultText);
            String regex = "\\d{2}\\. \\d{2}\\. \\d{4}";
            Matcher m = Pattern.compile(regex).matcher(resultText);
            if (m.find()) {
                textDate.setText(m.group(0));
                Toast.makeText(this,m.group(0), Toast.LENGTH_SHORT).show();
//                System.out.print(m.group(1));
                //System.out.print(m.group(2));
                Toast.makeText(this,"Done!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this,"No text found!", Toast.LENGTH_SHORT).show();
            }
        }

    }

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
                textDate.setText("");
                runTextRecognition();
                return true;
            case R.id.action_takeapic:
                Toast.makeText(this,"Take a picture",Toast.LENGTH_SHORT).show();
               // dispatchTakePictureIntent();
                return true;
            case R.id.action_import:
                ImportImage();
                //pickImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    private void ImportImage() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,RESULT_LOAD_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 1 && resultCode == 0) {
                return;
            }
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                image = BitmapFactory.decodeFile(picturePath);
                imageView.setImageBitmap(image);

    }


}
