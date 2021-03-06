package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button capture, detect;
    private ImageView img;
    private TextView t1;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        capture=findViewById(R.id.captureimage);
        detect=findViewById(R.id.detect);
        img=findViewById(R.id.imageview1);
        t1=findViewById(R.id.textview1);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            dispatchTakePictureIntent();
            }
        });
    detect.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            t1.setText("");
            detecttextfromimage();

        }
    });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(imageBitmap);
        }
    }
    private void detecttextfromimage() {
        FirebaseVisionImage firebaseVisionImage= FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector= FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displaytextfromimage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Error", e.getMessage());
            }
        });
    }

    private void displaytextfromimage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blockList=firebaseVisionText.getBlocks();
        if(blockList.size()==0)
        {
            Toast.makeText(this, "No Text Detected ", Toast.LENGTH_SHORT).show();
        }
        else{
            for(FirebaseVisionText.Block block : firebaseVisionText.getBlocks())
            {
                String text= block.getText();
                t1.setText(text);
            }
        }
    }


}
