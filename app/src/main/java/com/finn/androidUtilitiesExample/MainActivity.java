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
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomInternetHelper.InternetStateReceiverListener {

    private CustomRecycler<CustomRecycler.Expandable<List<String>>> testRecycler;
    private int amount = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Test.test(this);

        List<String> testList = Arrays.asList("1", "2", "3");
        new CustomRecycler.Expandable<String>().toExpandableList(testList, s -> new CustomRecycler.Expandable<>(s, s));


//        List<CustomRecycler.Expandable<String>> expandableList = new ArrayList<>();
//        for (int i = 0; i < amount; i++) {
//            List<String> numberList = new ArrayList<>();
//            if (i % 2 == 0) {
//                for (int i1 = 0; i1 < i + 1; i1++) {
//                    numberList.add(String.valueOf(i1 + 1));
//                }
//                expandableList.add(new CustomRecycler.Expandable<>(String.valueOf(i + 1), String.join("\n", numberList)));
//            }
//            else
//                expandableList.add(new CustomRecycler.Expandable<>(String.valueOf(i + 1)));
//        }

        List<CustomRecycler.Expandable<List<String>>> expandableList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            List<String> numberList = new ArrayList<>();
            if (i % 2 == 0) {
                for (int i1 = 0; i1 < i + 1; i1++) {
                    numberList.add(String.valueOf(i1 + 1));
                }
            }
            expandableList.add(new CustomRecycler.Expandable<>(String.valueOf(i + 1), numberList));
        }



//        testRecycler =
                new CustomRecycler<CustomRecycler.Expandable<List<String>>>(this, findViewById(R.id.recycler))
//                .setItemLayout(R.layout.list_item_expandable)
//                .setSetItemContent((itemView, s) -> ((TextView) itemView.findViewById(R.id.listItem_expandable_name)).setText(s))
//                .setExpandableHelper(customRecycler -> customRecycler.new ExpandableHelper<String>(R.layout.expandable_content_test, (itemView, s) -> ((TextView) itemView.findViewById(R.id.test)).setText(s))
                .setExpandableHelper(customRecycler -> customRecycler.new ExpandableHelper<String>() //R.layout.expandable_content_test, (itemView, s, expanded) -> {
//                    TextView test = itemView.findViewById(R.id.test);
//                    test.setText(s != null ? s : "<Nix>");
//                    if (expanded) {
//                        test.setMaxLines(Integer.MAX_VALUE);
//                    } else {
//                        test.setMaxLines(2);
//
//                    }
//                })
                        .customizeRecycler(subRecycler -> {
                            subRecycler.setOnClickListener((customRecycler2, itemView, s, index) -> {
                                CustomRecycler.Expandable expandable = customRecycler.getExpandable(customRecycler2);
                                Toast.makeText(this, expandable.getName() + ":" + s, Toast.LENGTH_SHORT).show();
                            });
                        })
                        .enableExpandByDefault()
                )
                .setObjectList(expandableList)
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
//                    testRecycler.reload(generateObjectList());
                })
                .show();
    }

    public void goTo(View view) {
        testRecycler.goTo((search, o) -> o.equals(search), null);
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
