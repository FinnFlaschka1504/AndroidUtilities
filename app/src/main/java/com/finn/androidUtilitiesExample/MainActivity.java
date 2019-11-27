package com.finn.androidUtilitiesExample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.finn.androidUtilities.CustomDialog;
import com.finn.androidUtilities.CustomList;
import com.finn.androidUtilities.CustomRecycler;
import com.finn.androidUtilities.Helpers;
import com.finn.androidUtilities.Test;

public class MainActivity extends AppCompatActivity {

    private CustomRecycler<String> customRecycler;
    private int amount = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Test.test(this);

        customRecycler = new CustomRecycler<String>(this, findViewById(R.id.recycler))
                .setObjectList(generateObjectList())
                .showDivider()
                .setOnClickListener((customRecycler1, itemView, o, index) -> Toast.makeText(this, o, Toast.LENGTH_SHORT).show())
                .disableCustomRipple()
                .generate();
    }

    private CustomList<String> generateObjectList() {
        return new CustomList<String>().generate(amount, String::valueOf);
    }

    public void showAmountDialog(View view) {
        CustomDialog.Builder(this)
                .setTitle("Anzahl Festlegen")
                .setText("Wie viele Elemente sollen angezeigt werden?")
                .setEdit(new CustomDialog.EditBuilder().setHint("Anzahl").setText(String.valueOf(amount)).setInputType(Helpers.TextInputHelper.INPUT_TYPE.NUMBER))
                .addButton(CustomDialog.BUTTON_TYPE.OK_BUTTON, customDialog -> {
                    amount = Integer.parseInt(customDialog.getEditText());
                    customRecycler.reload(generateObjectList());
                })
                .show();
    }

    public void goTo(View view) {
        customRecycler.goTo((search, o) -> o.equals(search), null);
    }
}
