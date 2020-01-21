package com.finn.androidUtilitiesExample;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.finn.androidUtilities.CustomDialog;
import com.finn.androidUtilities.CustomInternetHelper;
import com.finn.androidUtilities.CustomList;
import com.finn.androidUtilities.CustomRecycler;
import com.finn.androidUtilities.CustomUtility;
import com.finn.androidUtilities.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomInternetHelper.InternetStateReceiverListener {

    private CustomRecycler<CustomRecycler.Expandable<String>> testRecycler;
    private int amount = 40;
    List<Player> playerList = new ArrayList<>(Arrays.asList(new Player("Player1"), new Player("Player2"), new Player("Player3"), new Player("Player4"), new Player("Player5")));
    CustomRecycler<CustomRecycler.Expandable<Player>> recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Test.calcStart(9);
//
//        Test.test(this);

        List<String> testList = Arrays.asList("1", "2", "3");
        new CustomRecycler.Expandable<String>().toExpandableList(testList, s -> new CustomRecycler.Expandable<>(s, s));

//        List<CustomRecycler.Expandable<String>> expandableList = new ArrayList<>();
//        for (int i = 0; i < amount; i++) {
//            List<String> numberList = new ArrayList<>();
//            if (true /*i % 2 == 0*/) {
//                for (int i1 = 0; i1 < i + 1; i1++) {
//                    numberList.add(String.valueOf(i1 + 1));
//                }
//                expandableList.add(new CustomRecycler.Expandable<>(String.valueOf(i + 1), String.join("\n", numberList)));
//            } else
//                expandableList.add(new CustomRecycler.Expandable<>(String.valueOf(i + 1)));
//        }

//        List<CustomRecycler.Expandable<List<String>>> expandableList = new ArrayList<>();
//        for (int i = 0; i < amount; i++) {
//            List<String> numberList = new ArrayList<>();
//            if (i % 2 == 0) {
//                for (int i1 = 0; i1 < i + 1; i1++) {
//                    numberList.add(String.valueOf(i1 + 1));
//                }
//            }
//            expandableList.add(new CustomRecycler.Expandable<>(String.valueOf(i + 1), numberList));
//        }

        List<Pair<String, String>> pairList = Arrays.asList(new Pair<>("A", "1"), new Pair<>("C", "2"), new Pair<>("A", "3"), new Pair<>("A", "4"), new Pair<>("B", "5"), new Pair<>("C", "6"), new Pair<>("D", "7"), new Pair<>("B", "8"), new Pair<>("A", "9"), new Pair<>("A", "10"), new Pair<>("D", "11"));

        List<CustomRecycler.Expandable<String>> expandableList = new CustomRecycler.Expandable.ToGroupExpandableList<String, Pair<String, String>, String>()
                .setSort((o1, o2) -> o1.getName().compareTo(o2.getName()) * -1)
                .setSubSort((o1, o2) -> Integer.valueOf(o1).compareTo(Integer.valueOf(o2)) * -1)
                .runToGroupExpandableList(pairList, stringStringPair -> stringStringPair.first, (s, m) -> s, stringStringPair -> stringStringPair.second);


        recycler = new CustomRecycler<CustomRecycler.Expandable<Player>>(this, findViewById(R.id.recycler))
                .setGetActiveObjectList(customRecycler -> new CustomRecycler.Expandable.ToExpandableList<Player, Player>()
                        .keepExpandedState(customRecycler)
                        .runToExpandableList(playerList, null))
//                .setSetItemContent((customRecycler, itemView, player) -> ((TextView) itemView.findViewById(R.id.listItem_player_name)).setText(player.getName()))
                .setExpandableHelper(customRecycler -> customRecycler.new ExpandableHelper<Player>(R.layout.list_item_player, (customRecycler1, itemView, player, expanded) -> {
                    if (!expanded)
                        ((TextView) itemView.findViewById(R.id.listItem_player_name)).setText(player.getName());
                    else
                        ((TextView) itemView.findViewById(R.id.listItem_player_name)).setText("Mip\n" + player.getName() + "\n\nExpand");

                }))
                .addSubOnClickListener(R.id.listItem_player_delete, (customRecycler, itemView, playerExpandable, index) -> {
                    Toast.makeText(this, playerExpandable.getName(), Toast.LENGTH_SHORT).show();
                    playerList.remove(playerExpandable.getObject());
                    customRecycler.reload();
                })
                .enableDragAndDrop(R.id.listItem_player_drag, (customRecycler, objectList) -> {}, false)
                .generate();



        if (true)
            return;
        testRecycler = new CustomRecycler<CustomRecycler.Expandable<String>>(this, findViewById(R.id.recycler))
//                .setItemLayout(R.layout.list_item_expandable)
//                .setSetItemContent((itemView, s) -> ((TextView) itemView.findViewById(R.id.listItem_expandable_name)).setText(s))
//                .setExpandableHelper(customRecycler -> customRecycler.new ExpandableHelper<String>(R.layout.expandable_content_test, (itemView, s) -> {
//                            itemView.setOnClickListener(v -> CustomDialog.Builder(this)
//                                    .setTitle("SquareLayout")
//                                    .setView(R.layout.square_test)
//                                    .setButtonConfiguration(CustomDialog.BUTTON_CONFIGURATION.BACK)
//                                    .setDimensions(false, false)
//                                    .show());
//                            ((TextView) itemView.findViewById(R.id.test)).setText(s);
//                        })
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
                                    subRecycler
                                            .setOnClickListener((customRecycler2, itemView, s, index) -> {
                                                Toast.makeText(this, String.valueOf(CustomUtility.PingTask.hasPending()), Toast.LENGTH_SHORT).show();
//                                                if (CustomUtility.PingTask.isPending(s))
//                                                    return;
                                                CustomUtility.isOnline(this, () -> {
                                                    CustomRecycler.Expandable expandable = customRecycler.getExpandable(customRecycler2);
                                                    Toast.makeText(this, expandable.getName() + ":" + s, Toast.LENGTH_SHORT).show();
                                                });
                                            })
//                                            .enableDivider()
//                                            .removeLastDivider()
//                                            .disableCustomRipple()
                                            .setItemLayout(R.layout.expandable_content_test)
                                            .setSetItemContent((customRecycler1, itemView, s) -> ((TextView) itemView.findViewById(R.id.test)).setText(s))
                                            .enableSwiping((objectList, direction, s) -> {

                                                Toast.makeText(this, s + (direction == 16 ? " links" : " rechts"), Toast.LENGTH_SHORT).show();
                                                CustomList.replace(objectList, s, s1 -> String.valueOf(Integer.parseInt(s) + 1));
//                                                objectList.replaceAll(s1 -> s.equals(s1) ? String.valueOf(Integer.parseInt(s) + 1) : s1);
//                                                subRecycler.reload();
                                            }, true, true)
                                            .setSwipeBackgroundHelper(new CustomRecycler.SwipeBackgroundHelper<String>(R.drawable.ic_delete_black_24dp, getColor(R.color.colorGreen))
                                                    .setDynamicResources((swipeBackgroundHelper, s) -> {
                                                        swipeBackgroundHelper
                                                                .setFarEnoughColor_icon(getColor(R.color.colorAccent)).setNotFarEnoughColor_icon(Color.RED).setThreshold(0.4f)
                                                                .setFarEnoughIconResId(R.drawable.ic_arrow_up)
                                                                .enableBouncyThreshold(2, true, false)
                                                                .setFarEnoughColor_circle_left(Color.BLUE)
                                                                .setIconResId_left(R.drawable.ic_arrow_down);

                                                        if (s.contains("1"))
                                                            swipeBackgroundHelper.setFarEnoughColor_circle_left(Color.YELLOW);
                                                    })
                                            );
                                })
//                                .enableExpandByDefault()
                                .setExpandMatching(expandable -> expandable.getList().stream().anyMatch(s -> s.contains("1")))
                )
                .setGetActiveObjectList(customRecycler -> {
                    return new CustomRecycler.Expandable.ToGroupExpandableList<String, Pair<String, String>, String>()
                            .keepExpandedState(customRecycler)
                            .setSort((o1, o2) -> o1.getName().compareTo(o2.getName()) * -1)
                            .setSubSort((o1, o2) -> Integer.valueOf(o1).compareTo(Integer.valueOf(o2)) * -1)
                            .runToGroupExpandableList(pairList, stringStringPair -> stringStringPair.first, (s, m) -> s, stringStringPair -> stringStringPair.second);
                })
//                .setObjectList(expandableList)
//                .enableSwiping((objectList, direction, stringExpandable) -> {
//                    String BREAKPOINT = null;
//                }, true, true)
//                .setSwipeBackgroundHelper(new CustomRecycler.SwipeBackgroundHelper(R.drawable.ic_delete_black_24dp, getColor(R.color.colorGreen))
//                        .setFarEnoughColor_icon(Color.YELLOW).setNotFarEnoughColor_icon(Color.RED).enableBouncyThreshold().setThreshold(0.3f))
                .setOnGenerate(customRecycler -> customRecycler.getRecycler().getHeight())
                .setOnReload(customRecycler -> customRecycler.getRecycler().getHeight())
                .generate();

//        CustomInternetHelper.initialize(this);


//        CustomUtility.PingTask.simulate(true, 3000);

//        CustomUtility.isOnline(() -> {
//            CustomDialog.Builder(this)
//                    .setTitle(Boolean.toString(true))
//                    .setDimensions(false, false)
//                    .show();
//
//        }, () -> {
//            CustomDialog.Builder(this)
//                    .setTitle(Boolean.toString(false))
//                    .setDimensions(false, false)
//                    .show();
//
//        }, this);
    }

    private CustomList<String> generateObjectList() {
        return new CustomList<String>().generate(amount, String::valueOf);
    }

    public void showAmountDialog(View view) {
        CustomDialog.Builder(this)
                .setTitle("Spieler Hinzufügen")
                .setText("Den Namen des Spielers eingeben")
                .setEdit(new CustomDialog.EditBuilder().setHint("Spieler Name"))
                .setButtonConfiguration(CustomDialog.BUTTON_CONFIGURATION.OK_CANCEL)
                .addButton(CustomDialog.BUTTON_TYPE.OK_BUTTON, customDialog -> {
                    playerList.add(new Player(customDialog.getEditText()));
                    recycler.reload();
                })
                .disableLastAddedButton() // ToDo: wenn kein button vorhanden dann nichts machen
                .show();


        if (true)
            return;
        CustomUtility.isOnline(this, () -> {
            CustomDialog.Builder(this)
                    .setTitle(new CustomDialog.TextBuilder("Anzahl Festlegen").setColor(Color.BLUE))
                    .setText(new CustomDialog.TextBuilder("Wie viele Elemente sollen angezeigt werden?").setColor(Color.MAGENTA))
                    .setEdit(new CustomDialog.EditBuilder().setHint("Anzahl").setText(String.valueOf(amount)).setInputType(Helpers.TextInputHelper.INPUT_TYPE.NUMBER))
                    .addButton(CustomDialog.BUTTON_TYPE.CANCEL_BUTTON)
                    .addButton(CustomDialog.BUTTON_TYPE.OK_BUTTON, customDialog -> {
                        amount = Integer.parseInt(customDialog.getEditText());
//                    testRecycler.reload(generateObjectList());
                    })
//                    .removeBackground()
//                    .colorLastAddedButton()
                    .enableColoredActionButtons()
                    .show();
        }).suspendOnClick(view);

    }

    public void goTo(View view) {
        testRecycler.goTo((search, expandable) -> expandable.getName().equals(search), CustomRecycler.Expandable::getName, null);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (CustomUtility.PingTask.isPending(item))
            return false;
        int id = item.getItemId();
        switch (id) {
            case R.id.toolbar_main_internetTest:
                recycler.reload();
                Toast.makeText(this, "reload", Toast.LENGTH_SHORT).show();
//                CustomUtility.isOnline(this, status -> {
//                    Toast.makeText(this, String.valueOf(status), Toast.LENGTH_SHORT).show();
//                }).markAsPending(item); // .suspendMenuItem(item);
                break;
        }
        return true;
    }

}
