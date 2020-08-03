package com.finn.androidUtilitiesExample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.finn.androidUtilities.CustomDialog;
import com.finn.androidUtilities.CustomInternetHelper;
import com.finn.androidUtilities.CustomList;
import com.finn.androidUtilities.CustomPopupWindow;
import com.finn.androidUtilities.CustomRecycler;
import com.finn.androidUtilities.CustomUtility;
import com.finn.androidUtilities.Helpers;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity implements CustomInternetHelper.InternetStateReceiverListener {

    private CustomRecycler<CustomRecycler.Expandable<String>> testRecycler;
    private int amount = 40;
    List<Player> playerList = Stream.iterate(1, count -> count + 1).limit(0).map(count -> new Player("Spieler" + count)).collect(Collectors.toList());
    CustomRecycler<CustomRecycler.Expandable<Player>> recycler;
    List<Pair<String, String>> pairList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("TEST", "onCreate: test");

        setContentView(R.layout.activity_test);

        int aButtonId = View.generateViewId();
        int bButtonId = View.generateViewId();
        int cButtonId = View.generateViewId();
        int dButtonId = View.generateViewId();
        int eButtonId = View.generateViewId();
        int fButtonId = View.generateViewId();
        int gButtonId = View.generateViewId();
        int hButtonId = View.generateViewId();
        int iButtonId = View.generateViewId();
        int jButtonId = View.generateViewId();

        CustomDialog.Builder(this)
                .setView(R.layout.dialog_filter_by_rating)
//                .setDimensionsFullscreen()
                .setTitle("Double Click Test")
                .enableDoubleClickOutsideToDismiss(customDialog -> false, "eins", "zwei", "drei")
                .enableTitleBackButton(customDialog -> {
                    Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
                    customDialog.dismiss();
                })
//                .addButton(R.drawable.ic_drag, customDialog -> {
//                    CustomUtility.ifNotNull(customDialog.getActionButton(), buttonHelper -> Toast.makeText(this, "ja", Toast.LENGTH_SHORT).show(),() -> Toast.makeText(this, "nein", Toast.LENGTH_SHORT).show() );
//                })
//                .colorLastAddedButton()
////                .disableLastAddedButton()
//                .addButton("test")
//                .colorLastAddedButton()
////                .disableLastAddedButton()
//                .addButton(R.drawable.ic_drag)
////                .disableLastAddedButton()
//                .addButton("test")
////                .disableLastAddedButton()
//                .alignPreviousButtonsLeft()
//                .setButtonConfiguration(CustomDialog.BUTTON_CONFIGURATION.OK_CANCEL)
//                .addButton(CustomDialog.BUTTON_TYPE.OK_BUTTON)
//                .disableLastAddedButton()
                .addButton("Eins", customDialog -> customDialog.getButton(aButtonId).setVisibility(View.GONE), aButtonId, false)
                .addButton("Zwei", customDialog -> customDialog.getButton(bButtonId).setVisibility(View.GONE), bButtonId, false)
                .addButton("Drei", customDialog -> customDialog.getButton(cButtonId).setVisibility(View.GONE), cButtonId, false)
                .alignPreviousButtonsLeft()
                .addButton(R.drawable.ic_add, customDialog -> customDialog.getButton(dButtonId).setVisibility(View.GONE), dButtonId, false)
//                .addButton("Vier", customDialog -> customDialog.getButton(dButtonId).setVisibility(View.GONE), dButtonId, false)
//                .hideLastAddedButton()
                .addButton(R.drawable.ic_arrow_down, customDialog -> customDialog.getButton(eButtonId).setVisibility(View.GONE), eButtonId, false)
//                .addButton("Fünf", customDialog -> customDialog.getButton(eButtonId).setVisibility(View.GONE), eButtonId, false)
//                .hideLastAddedButton()
                .addButton(R.drawable.ic_broken_image, customDialog -> customDialog.getButton(fButtonId).setVisibility(View.GONE), fButtonId, false)
//                .addButton("Sechs", customDialog -> customDialog.getButton(fButtonId).setVisibility(View.GONE), fButtonId, false)
//                .hideLastAddedButton()
                .addButton("Sieben", customDialog -> {
                    customDialog.getButton(hButtonId).setVisibility(View.VISIBLE);
                    customDialog.getButton(gButtonId).setVisibility(View.GONE);
                }, gButtonId, false)
//                .hideLastAddedButton()
                .addButton("Acht", customDialog -> {
                    customDialog.getButton(gButtonId).setVisibility(View.VISIBLE);
                    customDialog.getButton(hButtonId).setVisibility(View.GONE);
                }, hButtonId, false)
                .hideLastAddedButton()
                .addButton("Neun", customDialog -> customDialog.getButton(iButtonId).setVisibility(View.GONE), iButtonId, false)
                .hideLastAddedButton()
                .addButton("Zehn", customDialog -> customDialog.getButton(jButtonId).setVisibility(View.GONE), jButtonId, false)
                .hideLastAddedButton()
//                .setSetViewContent((customDialog, view, reload) -> customDialog.getActionButton().setEnabled(true))
                .show();

//        NestedScrollView scrollView = findViewById(R.id.scrollView);
        RecyclerView recycler = findViewById(R.id.recycler);
//        TextView textView = findViewById(R.id.textView);
//        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);

//        recycler.getViewTreeObserver().addOnScrollChangedListener(() -> {
//
//        });

//        recycler.setOnTouchListener((v, event) -> {
//            if (scrollView.getScrollY() - 1 < textView.getHeight()) {
//                scrollView.onTouchEvent(event);
//                return true;
//            }
//            return false;
//        });


//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Notice Board");
//        setSupportActionBar(toolbar);

//        if (true)
//            return;

//        setContentView(R.layout.activity_main);


//        List<String> testList = Arrays.asList("1", "2", "3");
//        new CustomRecycler.Expandable<String>().toExpandableList(testList, s -> new CustomRecycler.Expandable<>(s, s));

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

//        pairList = new ArrayList<>((Arrays.asList(new Pair<>("A", "1"), new Pair<>("C", "2"), new Pair<>("A", "3"), new Pair<>("A", "4"), new Pair<>("B", "5"), new Pair<>("C", "6"), new Pair<>("D", "7"), new Pair<>("B", "8"), new Pair<>("A", "9"), new Pair<>("D", "10"))));
//
//
//        List<CustomRecycler.Expandable<String>> expandableList = new CustomRecycler.Expandable.ToGroupExpandableList<String, Pair<String, String>, String>()
//                .setSort((o1, o2) -> o1.getName().compareTo(o2.getName()) * -1)
//                .setSubSort((o1, o2) -> Integer.valueOf(o1).compareTo(Integer.valueOf(o2)) * -1)
//                .runToGroupExpandableList(pairList, stringStringPair -> stringStringPair.first, (s, m) -> s, stringStringPair -> stringStringPair.second);
//
//
//        List<String> stringList = Stream.iterate(1, count -> count + 1).limit(100).map(String::valueOf).collect(Collectors.toList());
//
//        new CustomRecycler<String>(this, findViewById(R.id.recycler))
//                .setObjectList(stringList)
//                .setOnClickListener((customRecycler, itemView, s, index) -> {
//                    stringList.set(index, String.valueOf(Integer.parseInt(s) + 1));
//                    customRecycler.update(index);
//                })
//                .setMultipleClickDelay(0)
//                .removeLastDivider()
//                .disableCustomRipple()
//                .enableDivider()
//                .generate();

        this.recycler = new CustomRecycler<CustomRecycler.Expandable<Player>>(this, recycler)
                .setGetActiveObjectList(customRecycler -> new CustomRecycler.Expandable.ToExpandableList<Player, Player>()
//                        .keepExpandedState(customRecycler)
//                        .enableExpandNewByDefault(customRecycler)
                        .enableUseExpandMatching(customRecycler)
                        .runToExpandableList(playerList, null))
//                .setSetItemContent((customRecycler, itemView, player) -> ((TextView) itemView.findViewById(R.id.listItem_player_name)).setText(player.getName()))
                .setExpandableHelper(customRecycler -> customRecycler.new ExpandableHelper<Player>(R.layout.list_item_player, (customRecycler1, itemView, player, expanded) -> {
                    if (!expanded)
                        ((TextView) itemView.findViewById(R.id.listItem_player_name)).setText(player.getName());
                    else {
                        ((TextView) itemView.findViewById(R.id.listItem_player_name)).setText("Mip\n" + player.getName() + "\n\nExpand");
                    }
                    itemView.findViewById(R.id.listItem_player_delete).setEnabled(expanded);

                })
                        .setExpandMatching(expandable -> Integer.valueOf(expandable.getObject().getName().substring(7)) % 2 == 0))
                .addSubOnClickListener(R.id.listItem_player_delete, (customRecycler, itemView, playerExpandable, index) -> {
                    Toast.makeText(this, playerExpandable.getName(), Toast.LENGTH_SHORT).show();
                    playerList.remove(playerExpandable.getObject());
                    customRecycler.reload();
                })
                .enableDragAndDrop(R.id.listItem_player_drag, (customRecycler, objectList) -> {
                }, true)
//                .setOrientation(CustomRecycler.ORIENTATION.HORIZONTAL)
//                .addSubOnClickListener(R.id.listItem_player_drag, (customRecycler, itemView, playerExpandable, index) -> {
//                    Toast.makeText(this, playerExpandable.getName(), Toast.LENGTH_SHORT).show();
//                    View inflate = LayoutInflater.from(this).inflate(R.layout.popup_set_amount, null);
//                    new CustomPopupWindow(this, itemView, inflate).show();
//                }, true)
                .generate();




//        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
//        final boolean[] first = {true};
//        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
//            if (first[0]) {
//                ((AppBarLayout.Behavior) ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior()).setTopAndBottomOffset(-(findViewById(R.id.elementCount).getHeight()));
//                first[0] = false;
//            }
//        });

        if (true)
            return;
        testRecycler = new CustomRecycler<CustomRecycler.Expandable<String>>(this, recycler)
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
                                            .setSwipeBackgroundHelper(new CustomRecycler.SwipeBackgroundHelper<String>(R.drawable.ic_delete, getColor(R.color.colorGreen))
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
                            .enableUseExpandMatching(customRecycler)
//                            .enableExpandNewByDefault(customRecycler)
                            .setSort((o1, o2) -> o1.getName().compareTo(o2.getName()) * -1)
                            .setSubSort((o1, o2) -> Integer.valueOf(o1).compareTo(Integer.valueOf(o2)) * -1)
                            .runToGroupExpandableList(pairList, stringStringPair -> stringStringPair.first, (s, m) -> s, stringStringPair -> stringStringPair.second);
                })
//                .setObjectList(expandableList)
//                .enableSwiping((objectList, direction, stringExpandable) -> {
//                    String BREAKPOINT = null;
//                }, true, true)
//                .setSwipeBackgroundHelper(new CustomRecycler.SwipeBackgroundHelper(R.drawable.ic_delete, getColor(R.color.colorGreen))
//                        .setFarEnoughColor_icon(Color.YELLOW).setNotFarEnoughColor_icon(Color.RED).enableBouncyThreshold().setThreshold(0.3f))
                .setOnGenerate(customRecycler -> customRecycler.getRecycler().getHeight())
                .setOnReload(customRecycler -> customRecycler.getRecycler().getHeight())
                .generate();





        if (true)
            return;

        int validateButtonId = View.generateViewId();
        CustomDialog.Builder(MainActivity.this)
                .setTitle("DatenBank-Code Eingeben")
                .setView(R.layout.dialog_database_login)
                .setSetViewContent((customDialog, view, reload) -> {
                    TextInputLayout dialog_databaseLogin_name_layout = customDialog.findViewById(R.id.dialog_databaseLogin_name_layout);
                    TextInputLayout dialog_databaseLogin_oldPassword_layout = customDialog.findViewById(R.id.dialog_databaseLogin_oldPassword_layout);
                    TextInputLayout dialog_databaseLogin_passwordFirst_layout = customDialog.findViewById(R.id.dialog_databaseLogin_passwordFirst_layout);
                    TextInputLayout dialog_databaseLogin_passwordSecond_layout = customDialog.findViewById(R.id.dialog_databaseLogin_passwordSecond_layout);

                    Helpers.TextInputHelper helper = new Helpers.TextInputHelper();
                    helper.addValidator(dialog_databaseLogin_name_layout, dialog_databaseLogin_passwordFirst_layout, dialog_databaseLogin_passwordSecond_layout)
                            .defaultDialogValidation(customDialog)
//                            .setInputType(dialog_databaseLogin_passwordFirst_layout, Helpers.TextInputHelper.INPUT_TYPE.PASSWORD)
//                            .setInputType(dialog_databaseLogin_passwordSecond_layout, Helpers.TextInputHelper.INPUT_TYPE.PASSWORD)
                            .setValidation(dialog_databaseLogin_passwordSecond_layout, (validator, text) -> {
                                if (helper.getText(dialog_databaseLogin_passwordFirst_layout).trim().isEmpty())
                                    validator.setWarning("Das Passwort ist noch leer");
                                else if (CustomUtility.stringExists(text) && !text.equals(helper.getText(dialog_databaseLogin_passwordFirst_layout).trim()))
                                    validator.setInvalid("Die Passwörter müssen gleich sein");
                            });

                    helper.interceptForValidation(customDialog.getButton(validateButtonId).getButton(),
                            () -> Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show(),
                            () -> Toast.makeText(this, "Valide", Toast.LENGTH_SHORT).show());
                })
                .addOptionalModifications(customDialog -> {
                    if (Math.round(Math.random()) != 0)
                        customDialog.addButton("Test");
                })
                .addButton("Validate", customDialog -> {
                    customDialog.setTitle("Valid");
                }, validateButtonId, false)
//                .doubleClickLastAddedButton("Doppelklick zum Validieren", customDialog -> ((EditText) customDialog.findViewById(R.id.dialog_databaseLogin_name)).getText().toString().isEmpty())
                .alignPreviousButtonsLeft()
                .addOptionalModifications(customDialog -> {
                    if (Math.round(Math.random()) != 0)
                        customDialog.addButton("Test2");
                })
                .setButtonConfiguration(CustomDialog.BUTTON_CONFIGURATION.OK_CANCEL)
                .addButton(CustomDialog.BUTTON_TYPE.OK_BUTTON, customDialog -> {
//                    onFinish.runOndatabaseCodeFinish(customDialog.getEditText());

                    TextInputLayout dialog_databaseLogin_name_layout = customDialog.findViewById(R.id.dialog_databaseLogin_name_layout);
                    TextInputLayout dialog_databaseLogin_passwordFirst_layout = customDialog.findViewById(R.id.dialog_databaseLogin_passwordFirst_layout);

                    String databaseCode = dialog_databaseLogin_name_layout.getEditText().getText().toString().trim();
                    String password = dialog_databaseLogin_passwordFirst_layout.getEditText().getText().toString().trim();



                }, false)
                .disableLastAddedButton()
                .enableDoubleClickOutsideToDismiss(customDialog -> !((EditText) customDialog.findViewById(R.id.dialog_databaseLogin_name)).getText().toString().isEmpty(), null, "Dismiss")
                .enableDynamicWrapHeight(findViewById(android.R.id.content))
                .show();

        new CustomUtility.KeyboardChangeListener(this).setOnChange(aBoolean -> Toast.makeText(this, aBoolean ? "show" : "hide", Toast.LENGTH_SHORT).show());

//        CustomDialog.Builder(this)
//                .setTitle("TextFieldBoxes Test")
//                .setView(R.layout.dialog_text_box_test)
//                .setSetViewContent((customDialog, view, reload) -> {
//                    TextFieldBoxes text_field_boxes1 = view.findViewById(R.id.text_field_boxes1);
//                })
//                .addButton("Test", customDialog -> Toast.makeText(this, "Kurz", Toast.LENGTH_SHORT).show(), false)
//                .addOnLongClickToLastAddedButton(customDialog -> Toast.makeText(this, "Lang", Toast.LENGTH_SHORT).show())
//                .show();

        CustomInternetHelper.initialize(this);


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

    private void showButtonTest() {
        int deleteButtonId = View.generateViewId();
        CustomDialog.Builder(this)
                .setTitle("Button Test")
                .addButton(R.drawable.ic_delete, customDialog -> customDialog.getButton(deleteButtonId).setEnabled(false), deleteButtonId, false)
                .colorLastAddedButton()
//                .disableLastAddedButton()
                .addButton(R.drawable.ic_arrow_down, customDialog -> Toast.makeText(this, "Test2", Toast.LENGTH_SHORT).show(), false)
                .alignPreviousButtonsLeft()
                .addButton(CustomDialog.BUTTON_TYPE.SAVE_BUTTON)
                .transformPreviousButtonToImageButton()
                .colorLastAddedButton()
                .addButton(CustomDialog.BUTTON_TYPE.EDIT_BUTTON)
                .transformPreviousButtonToImageButton()
                .colorLastAddedButton()
                .show();
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
        recycler.goTo((search, expandable) -> expandable.getName().contains(search), CustomRecycler.Expandable::getName, null);
//        testRecycler.goTo((search, expandable) -> expandable.getName().equals(search), CustomRecycler.Expandable::getName, null);
    }

    @Override
    protected void onDestroy() {
//        CustomInternetHelper.destroyInstance(this);
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
//                showButtonTest();

                pairList.addAll(Arrays.asList(new Pair<>("E", "11"), new Pair<>("F", "12"), new Pair<>("F", "13"), new Pair<>("E", "14"), new Pair<>("G", "24")));
                testRecycler.reload();

                if (true)
                    return true;

                playerList.addAll(Stream.iterate(playerList.size() + 1, count -> count + 1).limit(2).map(count -> new Player("Spieler" + count)).collect(Collectors.toList()));

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
