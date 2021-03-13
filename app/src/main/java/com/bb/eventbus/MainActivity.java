package com.bb.eventbus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bb.eventbus.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        EventBus.getInstance().register(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }

    @EventBus.Subscribe
    public void testSetText(String msg) {
        Log.d(TAG, "testSetText: " + msg);
        TextView textView = findViewById(R.id.tv);
        textView.setText(msg);
    }

    @EventBus.Subscribe
    public void unregister(Integer i) {
        Log.d(TAG, "unregister: ");
        EventBus.getInstance().unRegister(this);
    }
}
