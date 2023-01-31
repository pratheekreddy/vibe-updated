package com.example.vibrationauthentication;

import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.SystemClock;
import android.os.VibrationEffect;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Vibrator;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.Button;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Hashtable;
import java.util.Dictionary;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public boolean loginAttempt;

    public String code;
    public String fakeCode;
    public int amplitude = 255;
    public int buffDuration = 50;
    public int timingDuration;
    public String choice;
    public long vibTimer;
    boolean has_code = false;
    Dictionary codeConversion = new Hashtable();
    List<String> alpha = new ArrayList<>();

    List<String> hexDict = new ArrayList<>();
    public String serverCode;

    private final OkHttpClient client = new OkHttpClient();

    public void run() throws Exception {
        Request request = new Request.Builder()
                .url("http://10.178.33.115")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    System.out.println(responseBody.string());

                }
            }
        });
    }

    public String generateCode(int codeLength){
        Random rand = new Random();
        String newCode = "";
        for (int i = 0; i < codeLength; i++) {
            newCode += alpha.get(rand.nextInt(alpha.size()));
        }
        return newCode;
    };

    public void setDict(){
        hexDict.add("0");  // 0
        hexDict.add("1");  // 1
        hexDict.add("2");  // 2
        hexDict.add("3");  // 3
        hexDict.add("4");  // 4
        hexDict.add("5");  // 5
        hexDict.add("6");  // 6
        hexDict.add("7");  // 7
        hexDict.add("8");  // 8
        hexDict.add("9");  // 9
        hexDict.add("A");  // A
        hexDict.add("B");  // B
        hexDict.add("C");  // C
        hexDict.add("D");  // D
        hexDict.add("E");  // E
        hexDict.add("F");  // F

        int start = 45;
//        Random rand = new Random();
        for (int i = 0; i < hexDict.size(); i++) {
            String timing = Integer.toString(start);
            codeConversion.put(hexDict.get(i), timing);
            start = start + 20;
        }

//        codeConversion.put("A", "45");
//        codeConversion.put("B", "65");
//        codeConversion.put("C", "85");
//        codeConversion.put("D", "105");
//        codeConversion.put("E", "125");
//        codeConversion.put("F", "145");
//        codeConversion.put("G", "165");
//        codeConversion.put("H", "185");
//        codeConversion.put("I", "205");
//        codeConversion.put("J", "225");
//        codeConversion.put("K", "245");
//        codeConversion.put("L", "265");
//        codeConversion.put("M", "285");
//        codeConversion.put("N", "305");
//        codeConversion.put("O", "325");
//        codeConversion.put("P", "345");
    }

/* current testing area */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginAttempt = false;
        setDict();
    }

    public void appSleep(int time){
        //try { Thread.sleep(time); }
        //catch(InterruptedException e) { Thread.currentThread().interrupt(); }
        SystemClock.sleep(time);
    }

    public void makePost() {
        OkHttpClient client = new OkHttpClient();
        System.out.println("Making POST request...");

        // This stuff was here to be able to set certain parameters in the app UI
//        EditText ampInput = (EditText)findViewById(R.id.freqEntry);
//        amplitude = Integer.parseInt(ampInput.getText().toString());
//
//        EditText bufferInput = (EditText)findViewById(R.id.bufferEntry);
//        buffDuration = Integer.parseInt(bufferInput.getText().toString());
//
//        EditText timingInput = (EditText)findViewById(R.id.timingEntry);
//        timingDuration = Integer.parseInt(timingInput.getText().toString());

        String url = "http://10.178.33.115/phone_api";    //Must set correct Vibe Server url
        long currTime = System.currentTimeMillis();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("collectionTimeStamp", String.valueOf(currTime))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();

            }
            /* This is where the data from the server will go to  */
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String respString = response.body().string();
                    System.out.println("Raw Server Response1: " + respString);
                }
                else{
                    System.out.println("Sorry");
                }
            }
        });
    }

    public String makeRequest() {
//        OkHttpClient client = new OkHttpClient();

        String url = "http://10.178.33.115/phone_api"; //Must set correct Vibe Server url

        while(loginAttempt == false) {
            OkHttpClient client = new OkHttpClient();
            System.out.println("run GET: ");
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                e.printStackTrace();
                    System.out.println("Cannot connect to server");
//                    loginAttempt = false;
                }

                /* This is where the data from the server will go to  */
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String respString = response.body().string();
                        JSONObject json;
                        String data;
                        try {
                            json = new JSONObject(respString);
                            data = json.getString("data");
                            if(!data.equals("null")){
                                System.out.println("Valid response received");
                                serverCode = data;
                                loginAttempt = true;
                            }
                        } catch (JSONException e) {
                            json = null;
                            data = "";
                            loginAttempt = false;
                        }
//                    code = data;
                        System.out.println("Raw Server GET Response: " + respString);
                        System.out.println(data);


                    }
                }
            });
            System.out.println("about to sleep...");
            SystemClock.sleep(1000);
            System.out.println("slept");
        }
        /* Okay now we officially have a string */
        makePost();
        return serverCode;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void readyApp(View view) {
        loginAttempt = false;
        String vibeCode;

        Toast t = Toast.makeText(getApplicationContext(), "Ready for code", Toast.LENGTH_SHORT);
        t.show();
        SystemClock.sleep(5000);

        vibeCode = makeRequest();

        noHandsVibration(view, vibeCode);
    }


    void noHandsVibration(View view, String vibeCode) {

        Vibrator vibrator = (Vibrator) getSystemService(view.getContext().VIBRATOR_SERVICE);

        int codeLength = vibeCode.length();
        List<Integer> code_timings = new ArrayList<>();

        System.out.print(vibeCode);
        for (int i = 0; i < codeLength; i++) {
            code_timings.add(Integer.parseInt((String) codeConversion.get(String.valueOf(vibeCode.charAt(i)))));
        }
        System.out.print(code_timings);

        int fullLength = 2*codeLength;
        long[] timings = new long [fullLength];
        int[] amplitudes = new int [fullLength];

        // Set the power of each vibration action (buffers should be 0, vibrations should be 255)
        for(int i = 0; i < fullLength; i++){
            if(i % 2 != 0){
                amplitudes[i] = 0;
            }
            else{
                amplitudes[i] = amplitude;
            }
        }

        // Set the timings of each vibration action (both vibrations and buffer periods)
        for(int i = 0; i < fullLength; i++){
            if(i % 2 != 0){
                timings[i] = buffDuration;
            }
            else{
                timings[i] = code_timings.get(i/2);
            }
        }

        System.out.println("amplitudes\n");
        System.out.println(Arrays.toString(amplitudes));
        System.out.println("timings\n");
        System.out.println(Arrays.toString(timings));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            System.out.println("I am vibing");
            Toast t = Toast.makeText(getApplicationContext(), "Vibrating...", Toast.LENGTH_SHORT);
            t.show();
            vibrator.vibrate(VibrationEffect.createWaveform(timings,amplitudes,-1));
        }
        else {
            Toast t = Toast.makeText(getApplicationContext(), "API version too low.", Toast.LENGTH_SHORT);
            t.show();
            System.out.println("Can't vibrate");
        }
    }

}
