package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private TextView mText;

    private HandlerThread mHandlerThread;
    private Handler mBackgroundHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = findViewById(R.id.text);

        mHandlerThread = new HandlerThread("background");
        mHandlerThread.start();

        mBackgroundHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBackgroundHandler.post(() -> parse("https://yeuchaybo.com/lich-cac-giai-chay-bo-o-viet-nam/"));
    }

    // https://www.w3schools.com/cssref/css_selectors.asp
    private void parse(String site) {
        Timber.w("parse %s", site);
        String text = "";
        mText.setText("");

        Document doc;
        try {
            doc = Jsoup.connect(site).get();
            Timber.d(doc.title());

            Elements monthsElement = doc.select(".css-events-list h2");
            Elements namesElement = doc.select(".css-events-list table tbody");

            for (int i = 0; i < monthsElement.size(); i++) {
                Timber.d(monthsElement.get(i).select("h2").text());
                if (!text.isEmpty()) {
                    text = text.concat("\n");
                }
                text = text.concat(monthsElement.get(i).select("h2").text());
                text = text.concat("\n");

                Elements races = namesElement.get(i).select("tr");
                for (int j = 0; j < races.size(); j++) {
                    Timber.d(races.get(j).text());
                    text = text.concat("\n" + races.get(j).text());

                    text = text.concat("\n");
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        final String s = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mText.setText(s);
            }
        });
    }
}