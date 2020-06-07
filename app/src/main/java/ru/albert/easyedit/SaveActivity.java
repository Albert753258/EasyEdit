package ru.albert.easyedit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class SaveActivity extends AppCompatActivity {
    EditText location;
    AppCompatActivity activity;
    Button saveButton;
    String locationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savefile);
        location = findViewById(R.id.editText2);
        saveButton = findViewById(R.id.saveButton);
        activity = this;
        if(MainActivity.isOpened){
            locationText = MainActivity.path;
            String disk[] = locationText.split("/");
            try {
                if(!(disk[1].equals("storage") && (disk[2].equals("emulated")) && (disk[3].equals("0")) || disk[1].equals("sdcard"))){
                    try{
                        Process process = Runtime.getRuntime().exec(new String[]{"su"});
                        DataOutputStream os = new DataOutputStream(process.getOutputStream());
                        String mountString = "mount -o rw,remount /" + disk[1] + "\n";
                        os.writeBytes(mountString);
                        String cmd[] = String.valueOf(MainActivity.text.getText()).split("\n");
                        os.writeBytes("echo " + cmd[0] + ">" + locationText + "\n");
                        for(int i = 1; i < cmd.length; i++){
                            os.writeBytes("echo " + cmd[i] + " >> " + locationText + "\n");
                        }
                        os.flush();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                        Toast.makeText(SaveActivity.this, "Нет доступа к файлу, выбирете другую папку", Toast.LENGTH_SHORT).show();
                        clickListener();
                    }
                }
                else {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(locationText));
                    writer.write(String.valueOf(MainActivity.text.getText()));
                    writer.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            activity.finish();
        }
        else {
            clickListener();
        }
    }
    public void clickListener(){
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                locationText = String.valueOf(location.getText());
                String disk[] = locationText.split("/");
                try {
                    if(!(disk[1].equals("storage") && (disk[2].equals("emulated")) && (disk[3].equals("0")) || disk[1].equals("sdcard"))){
                        try{
                            Process process = Runtime.getRuntime().exec(new String[]{"su"});
                            DataOutputStream os = new DataOutputStream(process.getOutputStream());
                            String mountString = "mount -o rw,remount /" + disk[1] + "\n";
                            os.writeBytes(mountString);
                            String cmd[] = String.valueOf(MainActivity.text.getText()).split("\n");
                            os.writeBytes("echo " + cmd[0] + ">" + locationText + "\n");
                            for(int i = 1; i < cmd.length; i++){
                                os.writeBytes("echo " + cmd[i] + " >> " + locationText + "\n");
                            }
                            os.flush();
                        }
                        catch (IOException e){
                            e.printStackTrace();
                            Toast.makeText(SaveActivity.this, "No root! Choose another directory", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(locationText));
                        writer.write(String.valueOf(MainActivity.text.getText()));
                        writer.close();
                    }
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                activity.finish();
            }
        });
    }
}