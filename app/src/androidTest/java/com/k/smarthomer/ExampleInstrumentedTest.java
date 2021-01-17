package com.k.smarthomer;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.k.smarthomer.Database.FirebaseDatabaseInstance;
import com.k.smarthomer.Models.Devices.Boiler;
import com.k.smarthomer.Models.Room;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "instrument test";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.k.smarthomer", appContext.getPackageName());
    }

    @Test
    public void testDevice() {
        final int[] x = {-1};
        Boiler boiler = new Boiler("454d45", "Kitchen Boiler", 0, false, false, false);
        FirebaseDatabaseInstance.getDevicesDatabase().child(boiler.getDeviceId()).setValue(boiler).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    x[0] = 1;
                }
                assertEquals(5, x[0]);
            }
        });

    }
}