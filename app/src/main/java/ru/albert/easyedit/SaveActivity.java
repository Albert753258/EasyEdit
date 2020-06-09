package ru.albert.easyedit;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SaveActivity extends AppCompatActivity {
    ListView listView;
    String path;
    Button dirButton;
    AppCompatActivity activity;
    EditText fileName;
    Dialog dialog;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savefile);
        if(MainActivity.isOpened){
            path = MainActivity.path;
            saveFile();
            finish();
        }
        else {
            dirButton = findViewById(R.id.chooseDir);
            listView = findViewById(R.id.listView);
            final File f = Environment.getExternalStorageDirectory();
            path = f.getPath();
            File[] list = f.listFiles();
            ArrayList<String> arrayList = new ArrayList();
            arrayList.add("..");
            for(File file: list){
                if(file.isDirectory()){
                    arrayList.add(file.getAbsolutePath());
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(adapter);
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog);
            activity = this;
            dirButton.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) { dialog.setTitle("Заголовок диалога");
                    dialog.show();
                    saveButton = dialog.findViewById(R.id.saveButton);
                    fileName = dialog.findViewById(R.id.fileName);
                    saveButton.setOnClickListener(new Button.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            path += "/" + fileName.getText();
                            saveFile();
                            finish();
                        }
                    });
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                    //Toast.makeText(getApplicationContext(), ((TextView) itemClicked).getText(), Toast.LENGTH_SHORT).show();
                    String pathName = String.valueOf(((TextView)itemClicked).getText());
                    if(pathName.equals("..")){
                        String[] arr = path.split("/");
                        pathName = "/";
                        for(int i = 1; i < arr.length - 1; i++){
                            pathName += arr[i] + "/";
                        }
                        int i = 0;
                    }
                    File f = new File(pathName);
                    path = pathName;
                    System.out.println(f.getPath());
                    File[] list = f.listFiles();
                    ArrayList<String> arrayList = new ArrayList();
                    arrayList.add("..");
                    for(File file: list){
                        if(file.isDirectory()){
                            arrayList.add(file.getAbsolutePath());
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, arrayList);
                    listView.setAdapter(adapter);
                }
            });
        }
    }
    public void saveFile(){
        String disk[] = path.split("/");
        try {
            if(!(disk[1].equals("storage") && (disk[2].equals("emulated")) && (disk[3].equals("0")) || disk[1].equals("sdcard"))){
                try{
                    Process process = Runtime.getRuntime().exec(new String[]{"su"});
                    DataOutputStream os = new DataOutputStream(process.getOutputStream());
                    String mountString = "mount -o rw,remount /" + disk[1] + "\n";
                    os.writeBytes(mountString);
                    String cmd[] = String.valueOf(MainActivity.text.getText()).split("\n");
                    os.writeBytes("echo " + cmd[0] + ">" + path + "\n");
                    for(int i = 1; i < cmd.length; i++){
                        os.writeBytes("echo " + cmd[i] + " >> " + path + "\n");
                    }
                    os.flush();
                }
                catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(SaveActivity.this, "No root! Choose another directory", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                //Process process = Runtime.getRuntime().exec(new String[]{"touch " + path});
                File file = new File(path);
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                writer.write(String.valueOf(MainActivity.text.getText()));
                writer.close();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}