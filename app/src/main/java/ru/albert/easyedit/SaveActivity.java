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
    Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savefile);
        location = findViewById(R.id.editText2);
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String disk[] = String.valueOf(location.getText()).split("/");
                try {
                    if((!(disk[1].equals("storage")) && !(disk[1].equals("emulated")) && !(disk[1].equals("0"))) || !(disk[1].equals("sdcard"))){
                        try{
                            Process process = Runtime.getRuntime().exec(new String[]{"su"});
                            DataOutputStream os = new DataOutputStream(process.getOutputStream());
                            os.writeBytes("mount -o rw,remount system /system\n");
                            String cmd = "echo " + MainActivity.text.getText() + " > " + location.getText() + '\n';
                            os.writeBytes(cmd);
                            os.writeBytes("exit\n");
                            os.flush();
                        }
                        catch (IOException e){
                            Toast.makeText(SaveActivity.this, "No root! Choose another directory", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(location.getText())));
                        writer.write(String.valueOf(MainActivity.text.getText()));
                        writer.close();
                    }
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}