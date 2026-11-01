
package com.example.jarvisai;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQ_SPEECH = 10;
    private static final int REQ_AUDIO = 11;
    private TextView status, conversation;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private boolean malayalam = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(3,10,28));
        getWindow().setNavigationBarColor(Color.rgb(3,10,28));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(1);
        root.setPadding(24,28,24,24);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.rgb(3,10,28));

        TextView title = new TextView(this);
        title.setText("J.A.R.V.I.S");
        title.setTextColor(Color.rgb(0,220,255));
        title.setTextSize(34);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title,new LinearLayout.LayoutParams(-1,72));

        status = new TextView(this);
        status.setText("SYSTEM READY");
        status.setTextColor(Color.GREEN);
        status.setGravity(Gravity.CENTER);
        root.addView(status);

        Button listen = new Button(this);
        listen.setText("TAP TO TALK");
        root.addView(listen,new LinearLayout.LayoutParams(-1,58));

        Button language = new Button(this);
        language.setText("Language: English / Malayalam");
        root.addView(language);

        ScrollView scroll = new ScrollView(this);
        conversation = new TextView(this);
        conversation.setTextColor(Color.WHITE);
        conversation.setTextSize(16);
        conversation.setText(
            "JARVIS: Hello! I am ready.\n" +
            "ജാർവിസ്: നമസ്കാരം! ഞാൻ തയ്യാറാണ്."
        );
        scroll.addView(conversation);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        setContentView(root);

        tts = new TextToSpeech(this, result -> {
            if (result == TextToSpeech.SUCCESS) {
                ttsReady = true;
                setVoiceLanguage();
            }
        });

        listen.setOnClickListener(v -> startListening());

        language.setOnClickListener(v -> {
            malayalam = !malayalam;
            setVoiceLanguage();
            language.setText(malayalam ?
                "Language: Malayalam" : "Language: English");
        });
    }

    private void setVoiceLanguage() {
        if (!ttsReady) return;
        tts.setLanguage(malayalam ?
            new Locale("ml","IN") : new Locale("en","IN"));
    }

    private void startListening() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                new String[]{Manifest.permission.RECORD_AUDIO},REQ_AUDIO);
            return;
        }

        Intent intent = new Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            malayalam ? "ml-IN" : "en-IN");

        try {
            status.setText("LISTENING...");
            startActivityForResult(intent,REQ_SPEECH);
        } catch (Exception e) {
            status.setText("Speech service unavailable");
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
        int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == REQ_SPEECH &&
            resultCode == RESULT_OK && data != null) {
            ArrayList<String> results =
                data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                handleCommand(results.get(0));
            }
        }
        status.setText("SYSTEM READY");
    }

    private void handleCommand(String raw) {
        String q = raw.toLowerCase(Locale.ROOT);
        conversation.append("\n\nYOU: " + raw);

        if (q.contains("time") || q.contains("സമയം")) {
            reply(new SimpleDateFormat("hh:mm a",
                Locale.getDefault()).format(new Date()));
        } else if (q.contains("date") || q.contains("തീയതി")) {
            reply(new SimpleDateFormat("dd MMMM yyyy",
                Locale.getDefault()).format(new Date()));
        } else if (q.contains("youtube")) {
            openApp("com.google.android.youtube");
        } else if (q.contains("settings") ||
                   q.contains("ക്രമീകരണം")) {
            startActivity(new Intent(
                android.provider.Settings.ACTION_SETTINGS));
            reply("Opening Settings");
        } else if (q.contains("hello") ||
                   q.contains("നമസ്കാരം")) {
            reply(malayalam ?
                "നമസ്കാരം! ഞാൻ തയ്യാറാണ്." :
                "Hello! I am ready.");
        } else {
            reply(malayalam ?
                "ഈ command ഇപ്പോൾ ലഭ്യമല്ല." :
                "This command is not available yet.");
        }
    }

    private void openApp(String packageName) {
        Intent launch =
            getPackageManager().getLaunchIntentForPackage(packageName);
        if (launch != null) {
            startActivity(launch);
            reply("Opening YouTube");
        } else {
            reply("YouTube is not installed");
        }
    }

    private void reply(String message) {
        conversation.append("\n\nJARVIS: " + message);
        if (ttsReady) {
            tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,"reply");
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
