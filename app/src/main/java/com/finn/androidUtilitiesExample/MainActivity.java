package com.finn.androidUtilitiesExample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.finn.androidUtilities.CustomDialog;
import com.finn.androidUtilities.CustomInternetHelper;
import com.finn.androidUtilities.CustomList;
import com.finn.androidUtilities.CustomRecycler;
import com.finn.androidUtilities.Helpers;
import com.finn.androidUtilities.Test;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomInternetHelper.InternetStateReceiverListener {

    private CustomRecycler<CustomRecycler.Expandable<List<String>>> customRecycler;
    private int amount = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Test.test(this);


//        List<CustomRecycler.Expandable<String>> expandableList = new ArrayList<>();
//        for (int i = 0; i < amount; i++) {
//            List<String> numberList = new ArrayList<>();
//            for (int i1 = 0; i1 < i + 1; i1++) {
//                numberList.add(String.valueOf(i1 + 1));
//            }
//            expandableList.add(new CustomRecycler.Expandable<>(String.valueOf(i + 1), String.join("\n", numberList)));
//        }

        List<CustomRecycler.Expandable<List<String>>> expandableList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            List<String> numberList = new ArrayList<>();
            for (int i1 = 0; i1 < i + 1; i1++) {
                numberList.add(String.valueOf(i1 + 1));
            }
            expandableList.add(new CustomRecycler.Expandable<>(String.valueOf(i + 1), numberList));
        }



        customRecycler = new CustomRecycler<CustomRecycler.Expandable<List<String>>>(this, findViewById(R.id.recycler))
                .setItemLayout(R.layout.list_item_expandable)
//                .setSetItemContent((itemView, s) -> ((TextView) itemView.findViewById(R.id.listItem_expandable_name)).setText(s))
                .setExpandableHelper(new CustomRecycler.ExpandableHelper<>(R.layout.expandable_content_test, (itemView, stringExpandable) -> {}))
//                        ((TextView) itemView.findViewById(R.id.test)).setText(stringExpandable.getObject())))
                .setObjectList(expandableList)
//                .setOnClickListener(expandableOnClickListener)
//                .setOnClickListener((customRecycler1, itemView, listExpandable, index) ->
//                        Toast.makeText(this, listExpandable.getName() + ":" + listExpandable.getList().get(index), Toast.LENGTH_SHORT).show()) // ToDo: onClick
                .generate();

        CustomInternetHelper.initialize(this);
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
//                    customRecycler.reload(generateObjectList());
                })
                .show();
    }

    public void goTo(View view) {
        customRecycler.goTo((search, o) -> o.equals(search), null);
    }

    @Override
    protected void onDestroy() {
        CustomInternetHelper.destroyInstance(this);
        super.onDestroy();
    }

    @Override
    public void networkAvailable() {
        Toast.makeText(this, "Verbunden", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void networkUnavailable() {
        Toast.makeText(this, "Offline", Toast.LENGTH_SHORT).show();
        CustomInternetHelper.showActivateInternetDialog(this);
    }
}
