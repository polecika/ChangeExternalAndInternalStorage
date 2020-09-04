package com.example.changeexternalandinternalstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText editLogin;
    private EditText editPassword;
    private Button btnLogin;
    private Button btnRegistration;
    private CheckBox checkStatus;
    private static ArrayList<String> countTracker;
    private static ArrayList<String> countTrackerExternal;
    private static int count = 0;
    private static int countExternal = 0;
    private static final String FILE_LOG_PAS = "file";
    private static final String FILE_NAME_ARRAY_LIST = "ArrayListFile";
    private static String checkInformationFileString;
    private static String checkInformationFileStringExternal;
    public static final String SAVE_MODE_CHECKBOX = "saved_mode";
    private SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editLogin = findViewById(R.id.editLogin);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistration = findViewById(R.id.btnRegistration);
        checkStatus = findViewById(R.id.checkStatus);
        countTracker = new ArrayList<>();
        countTrackerExternal = new ArrayList<>();
        checkInformationFileString = readArrayFileFromInternalStorage();
        checkInformationFileStringExternal = readArrayFileFromExternalStorage();
        loadStatus();


        if (checkInformationFileString != null) {
            String[] splitCheckInformationFileString = checkInformationFileString.split(";");
            for (int i = 0; i < splitCheckInformationFileString.length; i++) {
                countTracker.add(splitCheckInformationFileString[i]);
                count++;
            }
        }
        if (checkInformationFileStringExternal != null) {
            String[] splitCheckInformationFileString = checkInformationFileStringExternal.split(";");
            for (int i = 0; i < splitCheckInformationFileString.length; i++) {
                countTrackerExternal.add(splitCheckInformationFileString[i]);
                countExternal++;
            }
        }

        checkStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sPref = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean(SAVE_MODE_CHECKBOX, checkStatus.isChecked());
                ed.commit();
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(editLogin.getText().toString().equals("") && editPassword.getText().toString().equals(""))) {
                    String loginString = editLogin.getText().toString();
                    String passwordString = editPassword.getText().toString();
                    if (checkStatus.isChecked()) {
                        saveIntoExternalStorage(loginString, passwordString, countExternal);
                        countExternal++;
                    } else {
                        saveIntoInternalStorage(loginString, passwordString, count);
                        count++;
                    }
                    editLogin.setText("");
                    editPassword.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Не заполнена строка логина или пароля ", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((!countTracker.isEmpty() && !checkStatus.isChecked()) || (!countTrackerExternal.isEmpty() && checkStatus.isChecked()))
                {
                    if (!(editLogin.getText().toString().equals("") && editPassword.getText().toString().equals(""))) {
                        String loginString = editLogin.getText().toString();
                        String passwordString = editPassword.getText().toString();
                        String enterAccount = loginString + "/n" + passwordString;
                        if (checkStatus.isChecked()) {
                            for (int i = 0; i < countTrackerExternal.size(); i++) {
                                String checkFileLoginPassword = readFromExternalStorage(countTrackerExternal.get(i));
                                if (checkFileLoginPassword.equals(enterAccount)) {
                                    Toast.makeText(MainActivity.this, "Добро пожаловать " + loginString, Toast.LENGTH_SHORT).show();
                                    editLogin.setText("");
                                    editPassword.setText("");
                                    break;
                                }
                                if (i == (countTrackerExternal.size() - 1)) {
                                    Toast.makeText(MainActivity.this, "логина или пароля нет в базе", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } else {
                            for (int i = 0; i < countTracker.size(); i++) {
                                String checkFileLoginPassword = readFromInternalStorage(countTracker.get(i));
                                if (checkFileLoginPassword.equals(enterAccount)) {
                                    Toast.makeText(MainActivity.this, "Добро пожаловать " + loginString, Toast.LENGTH_SHORT).show();
                                    editLogin.setText("");
                                    editPassword.setText("");
                                    break;
                                }
                                if (i == (countTracker.size() - 1)) {
                                    Toast.makeText(MainActivity.this, "логина или пароля нет в базе", Toast.LENGTH_SHORT).show();
                                }

                            }

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Не заполнена строка логина или пароля или нет в базе зарегестрированных пользователей", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(MainActivity.this, "база логинов и паролей - пустая", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadStatus() {
        sPref = getPreferences(Context.MODE_PRIVATE);
        Boolean turnStatus = sPref.getBoolean(SAVE_MODE_CHECKBOX, false);
        checkStatus.setChecked(turnStatus);
    }

    private void saveIntoInternalStorage(String login, String password, int numberOfRegistration) {

        countTracker.add(FILE_LOG_PAS + numberOfRegistration);
        BufferedWriter arrayWriter = null;
        try {
            arrayWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILE_NAME_ARRAY_LIST, Context.MODE_APPEND)));
            arrayWriter.append(FILE_LOG_PAS + numberOfRegistration + ";");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (arrayWriter != null) {
                try {
                    arrayWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(openFileOutput(countTracker.get(numberOfRegistration), Context.MODE_APPEND)));
            writer.write(login + "/n" + password);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String readFromInternalStorage(String fileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(openFileInput(fileName)));
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String readArrayFileFromInternalStorage() {
        BufferedReader reader = null;
        String text;
        try {
            reader = new BufferedReader(new InputStreamReader(openFileInput(FILE_NAME_ARRAY_LIST)));
            text = reader.readLine();
            return text;
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //Сохранение и загрузка с внешней памяти
    private void saveIntoExternalStorage(String login, String password, int numberOfRegistration) {
        {
            countTrackerExternal.add(FILE_LOG_PAS + numberOfRegistration);
            FileWriter arrayWriterFlash = null;
            File file = new File(getApplicationContext().getExternalFilesDir(null), FILE_NAME_ARRAY_LIST);
            try {
                arrayWriterFlash = new FileWriter(file, true);
                arrayWriterFlash.append(FILE_LOG_PAS + numberOfRegistration + ";");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {
                    arrayWriterFlash.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        FileWriter writerFlash = null;
        File fileLog = new File(getApplicationContext().getExternalFilesDir(null), countTrackerExternal.get(numberOfRegistration));
        try {
            writerFlash = new FileWriter(fileLog, false);
            writerFlash.append(login + "/n" + password);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                writerFlash.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String readFromExternalStorage(String fileName) {
        String text = "";
        FileReader readerFlash = null;
        File fileFlash = new File(getApplicationContext().getExternalFilesDir(null), fileName);
        try {
            readerFlash = new FileReader(fileFlash);
            BufferedReader bufferReader = new BufferedReader(readerFlash);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                text += line;
            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                readerFlash.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return text;
    }

    private String readArrayFileFromExternalStorage() {
        String text = "";
        FileReader readerFlash = null;
        File fileFlash = new File(getApplicationContext().getExternalFilesDir(null), FILE_NAME_ARRAY_LIST);
        if(!fileFlash.exists()){
            return null;
        }
        try {
            readerFlash = new FileReader(fileFlash);
            BufferedReader bufferReader = new BufferedReader(readerFlash);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                text += line;
            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                readerFlash.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return text;
    }
}
