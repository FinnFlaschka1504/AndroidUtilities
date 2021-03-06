package com.finn.androidUtilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;


import org.apmem.tools.layouts.FlowLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomDialog {

    public enum BUTTON_CONFIGURATION {
        YES_NO, SAVE_CANCEL, BACK, OK, OK_CANCEL, CUSTOM
    }

    public enum BUTTON_TYPE {
        YES_BUTTON("Ja", R.drawable.ic_check), NO_BUTTON("Nein", R.drawable.ic_cancel), SAVE_BUTTON("Speichern", R.drawable.ic_save), CANCEL_BUTTON("Abbrechen", R.drawable.ic_cancel), BACK_BUTTON("Zurück", R.drawable.ic_arrow_back), OK_BUTTON("Ok", R.drawable.ic_check), DELETE_BUTTON("Löschen", R.drawable.ic_delete), GO_TO_BUTTON("Gehe zu", R.drawable.ic_search), EDIT_BUTTON("Bearbeiten", R.drawable.ic_edit), DETAIL_BUTTON("Details", R.drawable.ic_info), ADD_BUTTON("Hinzufügen", R.drawable.ic_add), CLOSE_BUTTON("Schließen", R.drawable.ic_cancel); // ToDo: Button oben Links & Rechts

        String label;
        int iconId;

        BUTTON_TYPE(String label, @DrawableRes int iconId) {
            this.label = label;
            this.iconId = iconId;
        }
    }

    private Dialog dialog;
    private Context context;
    private CharSequence title;
    private TextBuilder title_builder;
    private CharSequence text;
    private TextBuilder text_builder;
    private View view;
    private BUTTON_CONFIGURATION buttonConfiguration = BUTTON_CONFIGURATION.CUSTOM;
    private Pair<Boolean, Boolean> dimensions = new Pair<>(true, false);
    private boolean dividerVisibility = true;
    private Object payload;
    private boolean scroll = true;
    private EditBuilder editBuilder;
    private boolean showEdit;
    private boolean buttonLabelAllCaps = true;
    private boolean stackButtons;
    private boolean expandButtons;
    private boolean firstTime = true;
    private boolean removeLastDivider;
    private boolean titleBackButton;
    private OnDialogCallback titleBackButton_clickListener;
    private CustomList<Pair<Boolean, OnDialogCallback>> onDismissListenerList = new CustomList<>();
    private CustomList<Pair<Boolean, OnDialogCallback>> onShowListenerList = new CustomList<>();
    private boolean removeBackground;
    private boolean removeMargin;
    private Drawable backgroundDrawable;
    private boolean coloredActionButtons = true;
    private OnBackPressedListener onBackPressedListener;
    private OnDialogCallback onTouchOutside;
    private View activityRootView;
    private ViewTreeObserver.OnGlobalLayoutListener activityRootViewListener;

    public static final int VISIBLE = 0x00000000;
    public static final int INVISIBLE = 0x00000004;
    public static final int GONE = 0x00000008;

    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }


    private SetViewContent setViewContent;

    private CustomList<ButtonHelper> buttonHelperList = new CustomList<>();


    public CustomDialog(Context context) {
        this.context = context;
        dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.dialog_custom);
    }

    public static CustomDialog Builder(Context context) {
        return new CustomDialog(context);
    }


    //  ----- Getters & Setters ----->
    public CustomDialog setContext(Context context) {
        this.context = context;
        return this;
    }

    public CustomDialog setTitle(String title) {
        if (!firstTime)
            ((TextView) dialog.findViewById(R.id.dialog_custom_title)).setText(title);
        this.title = title;
        return this;
    }

    public CustomDialog setTitle(TextBuilder title_builder) {
        this.title_builder = title_builder;
        return this;
    }

    public CustomDialog setText(CharSequence text) {
        if (!firstTime)
            ((TextView) dialog.findViewById(R.id.dialog_custom_text)).setText(text);
        this.text = text;
        return this;
    }

    public CustomDialog setText(TextBuilder text_builder) {
        this.text_builder = text_builder;
        return this;
    }

    public CustomDialog setView(@LayoutRes int layoutId) {
        LayoutInflater li = LayoutInflater.from(context);
        this.view = li.inflate(layoutId, null);
        return this;
    }

    public CustomDialog setView(View view) {
        this.view = view;
        return this;
    }

    public CustomDialog setView(SetView setView) {
        this.view = setView.runSetView(this);
        return this;
    }

    public CustomDialog setButtonConfiguration(BUTTON_CONFIGURATION buttonConfiguration) {
        this.buttonConfiguration = buttonConfiguration;
        return this;
    }

    public CustomDialog setSetViewContent(CustomDialog.SetViewContent setViewContent) {
        this.setViewContent = setViewContent;
        return this;
    }

    public CustomDialog setDimensions(boolean width, boolean height) {
        setDialogLayoutParameters(dialog, width, height);
        this.dimensions = new Pair<>(width, height);
        return this;
    }

    public CustomDialog setDimensionsFullscreen() {
        this.dimensions = new Pair<>(true, true);
        return this;
    }

    public CustomDialog hideDividers() {
        this.dividerVisibility = false;
//        isDividerVisibilityCustom = true;
        return this;
    }

    public CustomDialog setEdit(EditBuilder editBuilder) {
        this.showEdit = true;
        this.editBuilder = editBuilder;
        return this;
    }

    public CustomDialog standardEdit() {
        this.editBuilder = new EditBuilder();
        showEdit = true;
        return this;
    }

    public CustomDialog setPayload(Object payload) {
        this.payload = payload;
        return this;
    }

    public CustomDialog disableScroll() {
        scroll = false;
        return this;
    }

    public CustomDialog disableButtonAllCaps() {
        buttonLabelAllCaps = false;
        return this;
    }

    public CustomDialog enableStackButtons() {
        this.stackButtons = true;
        return this;
    }

    public CustomDialog enableExpandButtons() {
        this.expandButtons = true;
        return this;
    }

    public CustomDialog enableRemoveLastDivider() {
        this.removeLastDivider = true;
        return this;
    }

    public CustomDialog enableTitleBackButton() {
        this.titleBackButton = true;
        return this;
    }

    public CustomDialog enableTitleBackButton(OnDialogCallback onButtonClick) {
        this.titleBackButton = true;
        this.titleBackButton_clickListener = onButtonClick;
        return this;
    }

    public CustomDialog enableColoredActionButtons() {
        this.coloredActionButtons = true;
        return this;
    }

    public CustomDialog disableColoredActionButtons() {
        this.coloredActionButtons = false;
        return this;
    }

    public CustomDialog setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
        return this;
    }

    public CustomDialog setDismissWhenClickedOutside(boolean dismiss) {
        dialog.setCanceledOnTouchOutside(dismiss);
        return this;
    }

    public CustomDialog enablePermanentDialog() {
        setDismissWhenClickedOutside(false);
        setOnBackPressedListener(customDialog -> true);
        return this;
    }

    public CustomDialog setOnTouchOutside(OnDialogCallback onTouchOutside) {
        this.onTouchOutside = onTouchOutside;
        return this;
    }

    public CustomDialog enableDoubleClickOutsideToDismiss(@Nullable CustomUtility.GenericReturnInterface<CustomDialog, Boolean> enableDoubleClick, String... onFirstClick_onSecondClick_onDisabled) {
        setDismissWhenClickedOutside(false);
        Helpers.DoubleClickHelper doubleClickHelper = Helpers.DoubleClickHelper.create();
        Toast toast = Toast.makeText(context, onFirstClick_onSecondClick_onDisabled.length > 0 && onFirstClick_onSecondClick_onDisabled[0] != null ? onFirstClick_onSecondClick_onDisabled[0] : "Doppelklick zum Schließen", Toast.LENGTH_SHORT);
        Runnable runCheck = () -> {
            boolean isDoubleClickEnabled = enableDoubleClick == null || enableDoubleClick.runGenericInterface(this);
            if (doubleClickHelper.check() || !isDoubleClickEnabled) {
                dismiss();
                toast.cancel();
                if (!isDoubleClickEnabled && onFirstClick_onSecondClick_onDisabled.length > 2)
                    Toast.makeText(context, onFirstClick_onSecondClick_onDisabled[2], Toast.LENGTH_SHORT).show();
                else if (onFirstClick_onSecondClick_onDisabled.length > 1)
                    Toast.makeText(context, onFirstClick_onSecondClick_onDisabled[1], Toast.LENGTH_SHORT).show();
            } else
                toast.show();
        };

        toast.getView().findViewById(android.R.id.message).setOnClickListener(v -> runCheck.run());

        setOnTouchOutside(customDialog -> runCheck.run());
        return this;
    }

    public CustomDialog enableDynamicWrapHeight(AppCompatActivity activity) {
        return enableDynamicWrapHeight(activity.findViewById(android.R.id.content).getRootView());
    }

    public CustomDialog enableDynamicWrapHeight(View rootView) {
        activityRootView = rootView;
        View dialogRootView = dialog.getWindow().getDecorView();
        activityRootViewListener = () -> {
            Rect r = new Rect();
            activityRootView.getWindowVisibleDisplayFrame(r);

            int availableSpace = r.bottom - r.top;
            dialogRootView.measure(View.MeasureSpec.makeMeasureSpec(dialogRootView.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(dialogRootView.getHeight(), View.MeasureSpec.UNSPECIFIED));
            int neededSpace = dialogRootView.getMeasuredHeight();

            boolean heightMatchParent = availableSpace < neededSpace;

            if (heightMatchParent != (dialog.getWindow().getAttributes().height == WindowManager.LayoutParams.MATCH_PARENT))
                setDialogLayoutParameters(dialog, true, heightMatchParent);
        };
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(activityRootViewListener);
        addOnDialogDismiss_system(customDialog -> activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(activityRootViewListener));
        return this;
    }

    public CustomDialog enableAutoUpdateDynamicWrapHeight() {
        if (activityRootView != null && activityRootViewListener != null) {
            View dialogRootView = dialog.getWindow().getDecorView();
            dialogRootView.getViewTreeObserver().addOnGlobalLayoutListener(activityRootViewListener);
        } else
            throw new RuntimeException("'enableDynamicWrapHeight' muss zuvor aufgerufen worden sein");
        return this;
    }

    public CustomDialog updateDynamicWrapHeight() {
        if (activityRootView != null && activityRootViewListener != null) {
            activityRootViewListener.onGlobalLayout();
        } else
            throw new RuntimeException("'enableDynamicWrapHeight' muss zuvor aufgerufen worden sein");
        return this;
    }

    public CustomDialog addOptionalModifications(OnDialogCallback optionalModifications) {
        optionalModifications.runOnDialogCallback(this);
        return this;
    }

    public CustomDialog disableFadingEdge() {
        dialog.findViewById(R.id.dialog_custom_layout_view_interface).setVerticalFadingEdgeEnabled(false);
        return this;
    }

    // ---------------

    public Dialog getDialog() {
        return dialog;
    }

    public Object getPayload() {
        return payload;
    }

    public View getView() {
        return view;
    }

    public TextView getTitleTextView() {
        return dialog.findViewById(R.id.dialog_custom_title);
    }

    public TextView getTextTextView() {
        return dialog.findViewById(R.id.dialog_custom_text);
    }

    public TextInputLayout getEditLayout() {
        return dialog.findViewById(R.id.dialog_custom_edit_editLayout);
    }

    public CustomList<ButtonHelper> getButtonHelperList() {
        return buttonHelperList;
    }
    //  <----- Getters & Setters -----


    //  ------------------------- Listener ------------------------->
    private CustomDialog addOnDialogDismiss_system(OnDialogCallback onDialogDismiss) {
        onDismissListenerList.add(Pair.create(true, onDialogDismiss));
        return this;
    }

    public CustomDialog addOnDialogDismiss(OnDialogCallback onDialogDismiss) {
        onDismissListenerList.add(Pair.create(false, onDialogDismiss));
        return this;
    }

    public CustomDialog setOnDialogDismiss(OnDialogCallback onDialogDismiss) {
        onDismissListenerList.filter(pair -> pair.first, true);
        onDismissListenerList.add(Pair.create(false, onDialogDismiss));
        return this;
    }

    public CustomDialog removeOnDialogDismiss(OnDialogCallback onDialogCallback) {
        onDismissListenerList.remove(Pair.create(false, onDialogCallback));
        return this;
    }

    public CustomDialog clearOnDialogDismiss() {
        onDismissListenerList.filter(pair -> pair.first, true);
        return this;
    }

    // ---------------

    private CustomDialog addOnDialogShown_system(OnDialogCallback onDialogShown) {
        onShowListenerList.add(Pair.create(true, onDialogShown));
        return this;
    }

    public CustomDialog addOnDialogShown(OnDialogCallback onDialogShown) {
        onShowListenerList.add(Pair.create(false, onDialogShown));
        return this;
    }

    public CustomDialog setOnDialogShown(OnDialogCallback onDialogShown) {
        onShowListenerList.filter(pair -> pair.first, true);
        onShowListenerList.add(Pair.create(false, onDialogShown));
        return this;
    }

    public CustomDialog removeOnDialogShown(OnDialogCallback onDialogCallback) {
        onShowListenerList.remove(Pair.create(false, onDialogCallback));
        return this;
    }

    public CustomDialog clearOnDialogShown() {
        onShowListenerList.filter(pair -> pair.first, true);
        return this;
    }
    //  <------------------------- Listener -------------------------


    //  ----- Interfaces ----->
    public interface OnClick {
        void runOnClick(CustomDialog customDialog);
    }

    public interface SetViewContent {
        void runSetViewContent(CustomDialog customDialog, View view, boolean reload);
    }

    public interface OnDialogCallback {
        void runOnDialogCallback(CustomDialog customDialog);
    }

    public interface GoToFilter<T> {
        boolean runGoToFilter(String search, T t);
    }

    public interface OnBackPressedListener {
        boolean runOnBackPressedListener(CustomDialog customDialog);
    }

    public interface SetView {
        View runSetView(CustomDialog customDialog);
    }
    //  <----- Interfaces -----


    //  ----- Builder ----->
    public static class EditBuilder {
        private String text = "";
        private String hint = "";
        private boolean showKeyboard = true;
        private boolean selectAll = true;
        private boolean disableButtonByDefault;
        private boolean allowEmpty;
        private String regEx = "";
        private boolean fireActionDirectly;
        private Pair<Helpers.TextInputHelper.OnAction, Helpers.TextInputHelper.IME_ACTION[]> onActionActionPair;
        private List<String> dropDownList = new ArrayList<>();
        private boolean showDropdownDirectly = true;
        private AdapterView.OnItemClickListener onItemClickListener;

        private Helpers.TextInputHelper.INPUT_TYPE inputType = Helpers.TextInputHelper.INPUT_TYPE.CAP_SENTENCES;
        private Integer inputTypeInt;
        Helpers.TextInputHelper.TextValidation textValidation;

        public EditBuilder setText(String text) {
            this.text = text;
            return this;
        }

        public EditBuilder setHint(String hint) {
            this.hint = hint;
            return this;
        }

        public EditBuilder setShowKeyboard(boolean showKeyboard) {
            this.showKeyboard = showKeyboard;
            return this;
        }

        public EditBuilder disableSelectAll() {
            this.selectAll = false;
            return this;
        }

        public EditBuilder setInputType(Integer inputType) {
            inputTypeInt = inputType;
            return this;
        }

        public EditBuilder setInputType(Helpers.TextInputHelper.INPUT_TYPE inputType) {
            this.inputType = inputType;
            return this;
        }

        public EditBuilder setValidation(Helpers.TextInputHelper.TextValidation textValidation) {
            this.textValidation = textValidation;
            return this;
        }

        public EditBuilder setValidation(String regEx) {
            this.regEx = regEx;
            return this;
        }

        public EditBuilder disableButtonByDefault() {
            this.disableButtonByDefault = true;
            return this;
        }

        public EditBuilder allowEmpty() {
            this.allowEmpty = true;
            return this;
        }

        public EditBuilder setRegEx(String regEx) {
            this.regEx = regEx;
            return this;
        }

        public EditBuilder enableFireActionDirectly() {
            this.fireActionDirectly = true;
            return this;
        }

        public EditBuilder setFireActionDirectly(boolean fireActionDirectly) {
            this.fireActionDirectly = fireActionDirectly;
            return this;
        }

        public EditBuilder setOnAction(Helpers.TextInputHelper.OnAction onAction, Helpers.TextInputHelper.IME_ACTION... actions) {
            onActionActionPair = new Pair<>(onAction, actions);
            return this;
        }

        public Pair<Helpers.TextInputHelper.OnAction, Helpers.TextInputHelper.IME_ACTION[]> getOnActionActionPair() {
            return onActionActionPair;
        }


        //       -------------------- DropDown -------------------->
        public EditBuilder disableShowDropdownDirectly() {
            this.showDropdownDirectly = false;
            return this;
        }

        public EditBuilder setDropDownList(GetDropdownList getDropdownList, AdapterView.OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            this.dropDownList = getDropdownList.runGetDropdownList();
            return this;
        }

        public EditBuilder setDropDownList(GetDropdownList dropDownList) {
            return setDropDownList(dropDownList, null);
        }

        public interface GetDropdownList {
            List<String> runGetDropdownList();
        }
        //       <-------------------- DropDown --------------------
    }

    public static class TextBuilder {
        private String text;
        private int color = -1;
        private int size = -1;
        private int style = -1;
        private int alignment = -1;

        public TextBuilder(String text) {
            this.text = text;
        }

        //  ------------------------- Getters & Setters ------------------------->
        public String getText() {
            return text;
        }

        public TextBuilder setText(String text) {
            this.text = text;
            return this;
        }

        public int getColor() {
            return color;
        }

        public TextBuilder setColor(int color) {
            this.color = color;
            return this;
        }

        public int getSize() {
            return size;
        }

        public TextBuilder setSize(int size) {
            this.size = size;
            return this;
        }

        public int getStyle() {
            return style;
        }
//
//        public TextBuilder setStyle(int style) {
//            this.style = style;
//            return this;
//        }
//
//        public int getAlignment() {
//            return alignment;
//        }
//
//        public TextBuilder setAlignment(int alignment) {
//            this.alignment = alignment;
//            return this;
//        }
        //  <------------------------- Getters & Setters -------------------------


        //  ------------------------- Convenience ------------------------->
        public TextBuilder useDarkTitle(Context context) {
            color = context.getColor(R.color.colorPrimaryDark);
            return this;
        }
        //  <------------------------- Convenience -------------------------
    }
    //  <----- Builder -----


    //  ----- Convenience ----->
    public CustomDialog dismiss() {
        dialog.dismiss();
        return this;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return dialog.findViewById(id);
    }

    public String getEditText() {
        AutoCompleteTextView editText = dialog.findViewById(R.id.dialog_custom_edit);
        if (editText == null)
            return null;
        else
            return editText.getText().toString().trim();
    }

    public static void changeText(CustomDialog customDialog, CharSequence text) {
        ((TextView) customDialog.findViewById(R.id.dialog_custom_text)).setText(text);
    }

    public ButtonHelper getActionButton() {
        Optional<ButtonHelper> optional = buttonHelperList.stream()
                .filter(ButtonHelper::isActionButton)
                .findFirst();
        return optional.orElse(null);
    }

    public ButtonHelper getButton(int id) {
        Optional<ButtonHelper> optional = buttonHelperList.stream()
                .filter(buttonHelper -> Objects.equals(buttonHelper.id, id))
                .findFirst();
        return optional.orElse(null);
    }

    public ButtonHelper getButtonByName(String name) {
        Optional<ButtonHelper> optional = buttonHelperList.stream()
                .filter(buttonHelper -> Objects.equals(buttonHelper.label, name))
                .findFirst();
        return optional.orElse(null);
    }

    public ButtonHelper getButtonByIcon(@DrawableRes int iconId) {
        Optional<ButtonHelper> optional = buttonHelperList.stream()
                .filter(buttonHelper -> Objects.equals(buttonHelper.iconId, iconId))
                .findFirst();
        return optional.orElse(null);
    }

    public ButtonHelper getButtonByType(BUTTON_TYPE buttonType) {
        Optional<ButtonHelper> optional = buttonHelperList.stream()
                .filter(buttonHelper -> Objects.equals(buttonHelper.buttonType, buttonType))
                .findFirst();
        return optional.orElse(null);
    }

    public CustomList<View> getDividers() {
        ViewGroup dialog_custom_root = dialog.findViewById(R.id.dialog_custom_root);
        return CustomUtility.getViewsByType(dialog_custom_root, View.class, true)
                .stream().filter(view1 -> ((LinearLayout) view1.getParent()).getVisibility() == View.VISIBLE).collect(Collectors.toCollection(CustomList::new));

    }

    public TextView getViewTitle() {
        if (dialog != null)
            return dialog.findViewById(R.id.dialog_custom_title);
        else
            return null;
    }

    public TextView getViewText() {
        if (dialog != null)
            return dialog.findViewById(R.id.dialog_custom_text);
        else
            return null;
    }

    public TextInputLayout getViewEditLayout() {
        if (dialog != null)
            return dialog.findViewById(R.id.dialog_custom_edit_editLayout);
        else
            return null;
    }

    public CustomDialog removeBackground() {
        removeBackground = true;
        return this;
    }

    public CustomDialog removeBackground_and_margin() {
        removeBackground = true;
        removeMargin = true;
        return this;
    }
    //  <----- Convenience -----


    //  ----- Buttons ----->
    public class ButtonHelper {
        private Integer id;
        private String label;
        private Integer iconId;
        private BUTTON_TYPE buttonType;
        private boolean actionButton;
        private OnClick onClick;
        private boolean dismiss;
        private OnClick onLongClick;
        private Boolean dismissOnLong;
        private View button;
        private boolean alignLeft;
        private boolean disabled;
        private boolean hidden;
        private boolean colored;
        private String doubleClickMessage;
        private CustomUtility.GenericReturnInterface<CustomDialog, Boolean> enableDoubleClick;


        public ButtonHelper(BUTTON_TYPE buttonType) {
            this.buttonType = buttonType;
            label = buttonType.label;
            dismiss = true;
            if (coloredActionButtons && isActionButton())
                button = new Button(context, null, 0, R.style.ActionButtonStyle);
            else
                button = new Button(context, null, 0, R.style.ColoredBorderlessButtonStyle);
//            button.setBackground(dialog.findViewById(R.id.dialog_custom_Button1).getBackground().getConstantState().newDrawable());
//            button.setTextColor(((Button)dialog.findViewById(R.id.dialog_custom_Button1)).getTextColors());
        }

        public ButtonHelper(String label, BUTTON_TYPE buttonType, Integer iconId, OnClick onClick, Integer id, boolean dismiss) {
            this.id = id;
            this.label = label;
            this.iconId = iconId;
            this.buttonType = buttonType;
            this.onClick = onClick;
            this.dismiss = dismiss;
        }

        public View generateButton() {
//            View button;
            if (iconId == null) {
                if ((coloredActionButtons && isActionButton()) || colored)
                    button = new Button(context, null, 0, R.style.ActionButtonStyle);
                else
                    button = new Button(context, null, 0, R.style.ColoredBorderlessButtonStyle);
            } else {
                button = new ImageView(context, null, 0, R.style.ImageButtonStyle_Wide);
                ImageView imageView = (ImageView) button;
                imageView.setImageResource(iconId);

                if (colored) {
                    imageView.setBackgroundResource(R.drawable.rounded_button_background);
//                    imageView.setColorFilter(context.getColor(R.color.colorButtonForeground));
                    CustomUtility.tintImageButton(imageView, true, context, iconId);
                } else
                    CustomUtility.tintImageButton(imageView, false, context, iconId);
            }

            if (stackButtons || expandButtons) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                button.setLayoutParams(params);
            }


            if (label != null)
                ((Button) button).setText(label);
            else if (buttonType != null)
                ((Button) button).setText(buttonType.label);

            if (id != null)
                button.setId(id);
            else {
                id = View.generateViewId();
                button.setId(id);
            }

            if (!buttonLabelAllCaps)
                ((Button) button).setAllCaps(false);

            if (disabled)
                setEnabled(false); //ohne Farbe, Text und disabled: dark-36ffffff; light-3a000000

            if (hidden)
                button.setVisibility(View.GONE);

            ViewGroup layout;
            if (expandButtons) {
                layout = dialog.findViewById(R.id.dialog_custom_buttonLayout);
                if (buttonHelperList.isFirst(this))
                    layout.removeAllViews();
            } else {
                if (alignLeft)
                    layout = dialog.findViewById(R.id.dialog_custom_buttonLayout_left);
                else
                    layout = dialog.findViewById(R.id.dialog_custom_buttonLayout_right);
            }

            layout.addView(button);


            if (isImageButton()) {
                ButtonHelper next = buttonHelperList.next(this, true);
                if (next != null && !buttonHelperList.isLast(this) && next.isImageButton())
                    CustomUtility.setMargins(button, -1, 7, 7, 7);
                else
                    CustomUtility.setMargins(button, -1, 7);
            }

//            this.button = button;

            View.OnClickListener onClickListener = v -> {
                if (onClick != null)
                    onClick.runOnClick(CustomDialog.this);

                if (dismiss)
                    dialog.dismiss();
            };
            if (doubleClickMessage == null)
                button.setOnClickListener(onClickListener);
            else {
                final long[] time = {0};
                Toast toast = Toast.makeText(context, doubleClickMessage, Toast.LENGTH_SHORT);
                View.OnClickListener doubleClickListener = v -> {
                    if (enableDoubleClick != null) {
                        if (!enableDoubleClick.runGenericInterface(CustomDialog.this)) {
                            toast.cancel();
                            onClickListener.onClick(v);
                            return;
                        }
                    }
                    if (System.currentTimeMillis() - time[0] > 400) {
                        toast.show();
                        time[0] = System.currentTimeMillis();
                        return;
                    }
                    toast.cancel();
                    onClickListener.onClick(v);
                };
                button.setOnClickListener(doubleClickListener);
            }

            if (onLongClick != null)
                button.setOnLongClickListener(v -> {
                    onLongClick.runOnClick(CustomDialog.this);

                    if ((dismissOnLong != null && dismissOnLong) || (dismissOnLong == null && dismiss))
                        dialog.dismiss();
                    return true;
                });

            return button;
        }

        public ButtonHelper setEnabled(boolean enabled) {
            button.setEnabled(enabled);
            if (button instanceof ImageView)
                CustomUtility.tintImageButton((ImageView) button, colored, context, iconId);
            return this;
        }

        public ButtonHelper setVisibility(/*@Visibility*/ int visibility) {
            button.setVisibility(visibility);
            alignButtons();
            return this;
        }

        public ButtonHelper enableAlignLeft() {
            alignLeft = true;
            return this;
        }

        public View getButton() {
            return button;
        }

        public boolean isActionButton() {
            return actionButton || CustomUtility.boolOr(buttonType, BUTTON_TYPE.OK_BUTTON, BUTTON_TYPE.SAVE_BUTTON, BUTTON_TYPE.YES_BUTTON, BUTTON_TYPE.CLOSE_BUTTON);
        }

        public boolean isImageButton() {
            return iconId != null;
        }

        public void click() {
            if (onClick != null) {
                button.callOnClick();
            }
        }

        public void longClick() {
            if (onLongClick != null) {
                button.performLongClick();
            }
        }
    }

    private void alignButtons() {
        FlowLayout dialog_custom_buttonLayout_left = dialog.findViewById(R.id.dialog_custom_buttonLayout_left);
        FlowLayout dialog_custom_buttonLayout_right = dialog.findViewById(R.id.dialog_custom_buttonLayout_right);
        if (dialog_custom_buttonLayout_left != null && dialog_custom_buttonLayout_right != null) {
            LinearLayout.LayoutParams layoutParams_left = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    buttonHelperList.stream().filter(buttonHelper -> !buttonHelper.alignLeft && buttonHelper.getButton().getVisibility() != View.GONE).mapToInt(value -> value.isImageButton() ? 1 : 2).sum());
            LinearLayout.LayoutParams layoutParams_right = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    buttonHelperList.stream().filter(buttonHelper -> buttonHelper.alignLeft && buttonHelper.getButton().getVisibility() != View.GONE).mapToInt(value -> value.isImageButton() ? 1 : 2).sum());
            dialog_custom_buttonLayout_left.setLayoutParams(layoutParams_left);
            dialog_custom_buttonLayout_right.setLayoutParams(layoutParams_right);
        }
    }

    public CustomDialog addButton(String buttonName) {
        return addButton_complete(buttonName, null, null, null, null, true);
    }

    public CustomDialog addButton(String buttonName, OnClick onClick) {
        return addButton_complete(buttonName, null, null, onClick, null, true);
    }

    public CustomDialog addButton(String buttonName, OnClick onClick, int buttonId) {
        return addButton_complete(buttonName, null, null, onClick, buttonId, true);
    }

    public CustomDialog addButton(String buttonName, OnClick onClick, boolean dismissDialog) {
        return addButton_complete(buttonName, null, null, onClick, null, dismissDialog);
    }

    public CustomDialog addButton(String buttonName, OnClick onClick, int buttonId, boolean dismissDialog) {
        return addButton_complete(buttonName, null, null, onClick, buttonId, dismissDialog);
    }

    public CustomDialog addButton(BUTTON_TYPE button_type) {
        return addButton_complete(null, button_type, null, null, null, true);
    }

    public CustomDialog addButton(BUTTON_TYPE button_type, OnClick onClick) {
        return addButton_complete(null, button_type, null, onClick, null, true);
    }

    public CustomDialog addButton(BUTTON_TYPE button_type, OnClick onClick, int buttonId) {
        return addButton_complete(null, button_type, null, onClick, buttonId, true);
    }

    public CustomDialog addButton(BUTTON_TYPE button_type, OnClick onClick, boolean dismissDialog) {
        return addButton_complete(null, button_type, null, onClick, null, dismissDialog);
    }

    public CustomDialog addButton(BUTTON_TYPE button_type, OnClick onClick, int buttonId, boolean dismissDialog) {
        return addButton_complete(null, button_type, null, onClick, buttonId, dismissDialog);
    }

    public CustomDialog addButton(@DrawableRes int drawableResId) {
        return addButton_complete(null, null, drawableResId, null, null, true);
    }

    public CustomDialog addButton(@DrawableRes int drawableResId, OnClick onClick) {
        return addButton_complete(null, null, drawableResId, onClick, null, true);
    }

    public CustomDialog addButton(@DrawableRes int drawableResId, OnClick onClick, Integer buttonId) {
        return addButton_complete(null, null, drawableResId, onClick, buttonId, true);
    }

    public CustomDialog addButton(@DrawableRes int drawableResId, OnClick onClick, boolean dismissDialog) {
        return addButton_complete(null, null, drawableResId, onClick, null, dismissDialog);
    }

    public CustomDialog addButton(@DrawableRes int drawableResId, OnClick onClick, int buttonId, boolean dismissDialog) {
        return addButton_complete(null, null, drawableResId, onClick, buttonId, dismissDialog);
    }

    public CustomDialog addOnLongClickToLastAddedButton(OnClick onLongClick) {
        return addOnLongClickToLastAddedButton(onLongClick, null);
    }

    public CustomDialog addOnLongClickToLastAddedButton(OnClick onLongClick, Boolean dismissDialog) {
        CustomUtility.ifNotNull(buttonHelperList.getLast(), buttonHelper -> {
            buttonHelper.onLongClick = onLongClick;
            buttonHelper.dismissOnLong = dismissDialog;
        }, () -> {
            throw new IllegalStateException("Es wurde noch kein Button hinzugefügt", new NoButtonAdded("Es wurde noch kein Button hinzugefügt"));
        });
        return this;
    }

    private CustomDialog addButton_complete(String buttonName, BUTTON_TYPE button_type, Integer iconId, OnClick onClick, Integer buttonId, boolean dismissDialog) {
        ButtonHelper buttonHelper = new ButtonHelper(buttonName, button_type, iconId, onClick, buttonId, dismissDialog);
        buttonHelperList.add(buttonHelper);
        return this;
    }

    public CustomDialog colorLastAddedButton() {
        CustomUtility.ifNotNull(buttonHelperList.getLast(), buttonHelper -> buttonHelper.colored = true, () -> {
            throw new IllegalStateException("Es wurde noch kein Button hinzugefügt", new NoButtonAdded("Es wurde noch kein Button hinzugefügt"));
        });
        return this;
    }

    public CustomDialog hideLastAddedButton() {
        CustomUtility.ifNotNull(buttonHelperList.getLast(), buttonHelper -> buttonHelper.hidden = true, () -> {
            throw new IllegalStateException("Es wurde noch kein Button hinzugefügt", new NoButtonAdded("Es wurde noch kein Button hinzugefügt"));
        });
        return this;
    }

    public CustomDialog disableLastAddedButton() {
        CustomUtility.ifNotNull(buttonHelperList.getLast(), buttonHelper -> buttonHelper.disabled = true, () -> {
            throw new IllegalStateException("Es wurde noch kein Button hinzugefügt", new NoButtonAdded("Es wurde noch kein Button hinzugefügt"));
        });
        return this;
    }

    public CustomDialog alignPreviousButtonsLeft() {
        buttonHelperList.forEach(buttonHelper -> buttonHelper.alignLeft = true);
        return this;
    }

    public CustomDialog transformPreviousButtonToImageButton() {
        CustomUtility.ifNotNull(buttonHelperList.getLast(), buttonHelper -> {
            buttonHelper.iconId = buttonHelper.buttonType.iconId;
            buttonHelper.buttonType = null;
        }, () -> {
            throw new IllegalStateException("Es wurde noch kein Button hinzugefügt", new NoButtonAdded("Es wurde noch kein Button hinzugefügt"));
        });
        return this;
    }

    public CustomDialog doubleClickLastAddedButton(@NonNull String message, @Nullable CustomUtility.GenericReturnInterface<CustomDialog, Boolean> enableDoubleClick) {
        CustomUtility.ifNotNull(buttonHelperList.getLast(), buttonHelper -> {
            buttonHelper.doubleClickMessage = message;
            buttonHelper.enableDoubleClick = enableDoubleClick;
        }, () -> {
            throw new IllegalStateException("Es wurde noch kein Button hinzugefügt", new NoButtonAdded("Es wurde noch kein Button hinzugefügt"));
        });
        return this;
    }

    public CustomDialog markLastAddedButtonAsActionButton() {
        CustomUtility.ifNotNull(buttonHelperList.getLast(), buttonHelper -> buttonHelper.actionButton = true, () -> {
            throw new IllegalStateException("Es wurde noch kein Button hinzugefügt", new NoButtonAdded("Es wurde noch kein Button hinzugefügt"));
        });
        return this;
    }

    public CustomDialog addGoToButton(CustomRecycler.GoToFilter goToFilter, CustomRecycler customRecycler) {
        return addButton_complete(null, BUTTON_TYPE.GO_TO_BUTTON, null, customDialog -> customRecycler.goTo(goToFilter, null), null, false);
    }

    public CustomDialog addConfirmationDialogToLastAddedButton(String title, String text) {
        return addConfirmationDialogToLastAddedButton(title, text, null);
    }

    public CustomDialog addConfirmationDialogToLastAddedButton(String title, String text, @Nullable CustomUtility.GenericInterface<CustomDialog> optionalModifications) {
        CustomUtility.ifNotNull(buttonHelperList.getLast(), buttonHelper -> {
            OnClick onClick = buttonHelper.onClick;
            boolean dismiss = buttonHelper.dismiss;
            buttonHelper.dismiss = false;
            buttonHelper.onClick = customDialog -> {
                CustomDialog.Builder(context)
                        .setPayload(customDialog)
                        .setTitle(title)
                        .setText(text)
                        .setButtonConfiguration(BUTTON_CONFIGURATION.YES_NO)
                        .addButton(BUTTON_TYPE.YES_BUTTON, customDialog1 -> {
                            if (onClick != null)
                                onClick.runOnClick(customDialog);
                            if (dismiss)
                                customDialog.dismiss();
                        })
                        .addOptionalModifications(customDialog1 -> {
                            if (optionalModifications != null)
                                optionalModifications.runGenericInterface(customDialog1);
                        })
                        .show();
            };
        }, () -> {
            throw new IllegalStateException("Es wurde noch kein Button hinzugefügt", new NoButtonAdded("Es wurde noch kein Button hinzugefügt"));
        });

        return this;
    }

    class NoButtonAdded extends Exception {
        public NoButtonAdded(String message) {
            super(message);
        }
    }
    //  <----- Buttons -----


    //  ----- Actions ----->
    public Dialog show_dialog() {
        show();
        return dialog;
    }

    public CustomDialog show() {
        if (!firstTime) {
            dialog.show();
            return this;
        }
        // ToDo: TextBuilder

        if (title != null) {
            ((TextView) dialog.findViewById(R.id.dialog_custom_title)).setText(this.title);
            dialog.findViewById(R.id.dialog_custom_title_layout).setVisibility(View.VISIBLE);
        } else if (title_builder != null) {
            applyText(dialog.findViewById(R.id.dialog_custom_title), title_builder);
            dialog.findViewById(R.id.dialog_custom_title_layout).setVisibility(View.VISIBLE);
        }

        if (text != null) {
            TextView textView = dialog.findViewById(R.id.dialog_custom_text);
            textView.setText(this.text);
            MaxDimensionsLayout dialog_custom_text_layout = dialog.findViewById(R.id.dialog_custom_text_layout);
            dialog_custom_text_layout.setVisibility(View.VISIBLE);

//            dialog_custom_text_layout.setEnabled(false/*view != null*/);

//            dialog_custom_text_layout.setMaxHeight(CustomUtility.dpToPx(view != null || editBuilder != null ? 175: 600));
            if (view == null && editBuilder == null) {
                dialog_custom_text_layout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                dialog_custom_text_layout.setEnabled(false);
            } else
                dialog_custom_text_layout.setMaxHeight(CustomUtility.dpToPx(175));

//            boolean isLast = buttonHelperList.isEmpty() && view == null;
//            if (!dividerVisibility || /*removeLastDivider &&*/ isLast)
//                CustomUtility.setPadding(textView, -1, -1, -1, 16);

        } else if (text_builder != null) {
            applyText(dialog.findViewById(R.id.dialog_custom_text), text_builder);
            dialog.findViewById(R.id.dialog_custom_text_layout).setVisibility(View.VISIBLE);
        }

        if (showEdit) {
            dialog.findViewById(R.id.dialog_custom_edit_layout).setVisibility(View.VISIBLE);
        }

        if (view != null) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            if (scroll) {
                ScrollView dialog_custom_layout_view_interface = dialog.findViewById(R.id.dialog_custom_layout_view_interface);
                dialog_custom_layout_view_interface.removeAllViews(); // <--------------------------------------------------------------------------------------
                dialog_custom_layout_view_interface.addView(view, layoutParams);
                dialog_custom_layout_view_interface.setVisibility(View.VISIBLE);
            } else {
                LinearLayout dialog_custom_layout_view_interface_restrained = dialog.findViewById(R.id.dialog_custom_layout_view_interface_restrained);
                dialog_custom_layout_view_interface_restrained.addView(view, layoutParams);
                dialog_custom_layout_view_interface_restrained.setVisibility(View.VISIBLE);
            }
            dialog.findViewById(R.id.dialog_custom_layout_view).setVisibility(View.VISIBLE);
        }

        if (!dividerVisibility) {
            dialog.findViewById(R.id.dialog_custom_dividerTitle).setVisibility(View.GONE);
            dialog.findViewById(R.id.dialog_custom_dividerText).setVisibility(View.GONE);
            dialog.findViewById(R.id.dialog_custom_dividerEdit).setVisibility(View.GONE);
            dialog.findViewById(R.id.dialog_custom_dividerView).setVisibility(View.GONE);
        }

        switch (buttonConfiguration) {
            case BACK:
                if (buttonHelperList.stream().noneMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.BACK_BUTTON))
                    buttonHelperList.add(new ButtonHelper(BUTTON_TYPE.BACK_BUTTON));
                break;
            case OK:
                if (buttonHelperList.stream().noneMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.OK_BUTTON))
                    buttonHelperList.add(new ButtonHelper(BUTTON_TYPE.OK_BUTTON));
                break;
            case OK_CANCEL:
                if (buttonHelperList.stream().anyMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.OK_BUTTON)
                        && buttonHelperList.stream().noneMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.CANCEL_BUTTON)) {
                    Integer index = buttonHelperList.indexOf(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.OK_BUTTON);
                    buttonHelperList.add(index, new ButtonHelper(BUTTON_TYPE.CANCEL_BUTTON));
                    break;
                }
                if (buttonHelperList.stream().noneMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.CANCEL_BUTTON))
                    buttonHelperList.add(new ButtonHelper(BUTTON_TYPE.CANCEL_BUTTON));
                if (buttonHelperList.stream().noneMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.OK_BUTTON))
                    buttonHelperList.add(new ButtonHelper(BUTTON_TYPE.OK_BUTTON));
                break;
            case YES_NO:
                if (buttonHelperList.stream().anyMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.YES_BUTTON)
                        && buttonHelperList.stream().noneMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.NO_BUTTON)) {
                    Integer index = buttonHelperList.indexOf(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.YES_BUTTON);
                    buttonHelperList.add(index, new ButtonHelper(BUTTON_TYPE.NO_BUTTON));
                    break;
                }
                if (buttonHelperList.stream().noneMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.NO_BUTTON))
                    buttonHelperList.add(new ButtonHelper(BUTTON_TYPE.NO_BUTTON));
                if (buttonHelperList.stream().noneMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.YES_BUTTON))
                    buttonHelperList.add(new ButtonHelper(BUTTON_TYPE.YES_BUTTON));
                break;
            case SAVE_CANCEL:
                if (buttonHelperList.stream().anyMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.SAVE_BUTTON)
                        && buttonHelperList.stream().noneMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.CANCEL_BUTTON)) {
                    Integer index = buttonHelperList.indexOf(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.SAVE_BUTTON);
                    buttonHelperList.add(index, new ButtonHelper(BUTTON_TYPE.CANCEL_BUTTON));
                    break;
                }
                if (buttonHelperList.stream().noneMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.CANCEL_BUTTON))
                    buttonHelperList.add(new ButtonHelper(BUTTON_TYPE.CANCEL_BUTTON));
                if (buttonHelperList.stream().noneMatch(buttonHelper -> buttonHelper.buttonType == BUTTON_TYPE.SAVE_BUTTON))
                    buttonHelperList.add(new ButtonHelper(BUTTON_TYPE.SAVE_BUTTON));
                break;
        }

        buttonHelperList.forEach(ButtonHelper::generateButton);
        alignButtons();

        if (showEdit) {
            applyEdit();
        }

        if (setViewContent != null)
            setViewContent.runSetViewContent(this, view, false);

        setDialogLayoutParameters(dialog, dimensions.first, dimensions.second);

        if (removeLastDivider || buttonHelperList.isEmpty())
            getDividers().getLast().setVisibility(View.GONE);

        if (titleBackButton) {
            ImageView dialog_custom_title_backButton = dialog.findViewById(R.id.dialog_custom_title_backButton);
            dialog_custom_title_backButton.setOnClickListener(v -> {
                if (titleBackButton_clickListener == null)
                    dismiss();
                else
                    titleBackButton_clickListener.runOnDialogCallback(this);
            });
            dialog_custom_title_backButton.setVisibility(View.VISIBLE);
            TextView dialog_custom_title = dialog.findViewById(R.id.dialog_custom_title);

            CustomUtility.setMargins(dialog_custom_title, 60, -1, 60, -1);
        }

        if (removeBackground) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                if (!removeMargin)
                    CustomUtility.setMargins(dialog.findViewById(R.id.dialog_custom_root), 16);
            }
        }

        if (backgroundDrawable != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(backgroundDrawable);
            }
        }

        if (onBackPressedListener != null) {
            dialog
                    .setOnKeyListener((dialog, keyCode, event) -> {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
                            return onBackPressedListener.runOnBackPressedListener(this);
                        }
                        return false;
                    });
        }

        if (onTouchOutside != null) {
            dialog.getWindow().getDecorView().setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP && (event.getX() < 0 || event.getY() < 0 || event.getX() > v.getWidth() || event.getY() > v.getHeight()))
                    onTouchOutside.runOnDialogCallback(this);
                return false;
            });

        }

        dialog.setOnDismissListener(dialog1 -> onDismissListenerList.forEach(pair -> pair.second.runOnDialogCallback(this)));
        dialog.setOnShowListener(dialog1 -> onShowListenerList.forEach(pair -> pair.second.runOnDialogCallback(this)));

        firstTime = false;
        dialog.show();
        return this;
    }

    public CustomDialog reloadView() {
        if (setViewContent != null)
            setViewContent.runSetViewContent(this, view, true);
        else if (payload instanceof CustomRecycler)
            ((CustomRecycler) payload).reload();
        return this;
    }
    //  <----- Actions -----


    //  ------------------------- Apply ------------------------->
    private void applyEdit() {
        TextInputLayout textInputLayout = dialog.findViewById(R.id.dialog_custom_edit_editLayout);
        AutoCompleteTextView autoCompleteTextView = dialog.findViewById(R.id.dialog_custom_edit);

        Button button = null;
        Optional<ButtonHelper> optional = buttonHelperList.stream()
                .filter(buttonHelper -> (buttonHelper.buttonType == BUTTON_TYPE.OK_BUTTON || buttonHelper.buttonType == BUTTON_TYPE.SAVE_BUTTON || buttonHelper.buttonType == BUTTON_TYPE.YES_BUTTON))
                .findFirst();
        if (optional.isPresent())
            button = dialog.findViewById(optional.get().id);

        Button finalButton = button;
        Helpers.TextInputHelper.OnValidationResult onValidationResult = result -> {
            if (finalButton != null)
                finalButton.setEnabled(result);
        };
        Helpers.TextInputHelper textInputHelper = new Helpers.TextInputHelper(onValidationResult, textInputLayout);


        if (editBuilder != null) {
            if (editBuilder.showKeyboard) {
                autoCompleteTextView.requestFocus();
                CustomUtility.changeWindowKeyboard(dialog.getWindow(), true);
            }

            if (!editBuilder.text.isEmpty())
                autoCompleteTextView.setText(editBuilder.text);

            textInputLayout.setHint(editBuilder.hint);

            if (editBuilder.selectAll)
                autoCompleteTextView.selectAll();
            else
                autoCompleteTextView.setSelection(editBuilder.text.length());

            if (!editBuilder.regEx.isEmpty())
                textInputHelper.setValidation(textInputLayout, editBuilder.regEx);
            else if (editBuilder.textValidation != null)
                textInputHelper.setValidation(textInputLayout, editBuilder.textValidation);

            if (editBuilder.inputType != null && editBuilder.inputTypeInt == null)
                textInputHelper.setInputType(textInputLayout, editBuilder.inputType);
            else if (editBuilder.inputTypeInt != null)
                textInputHelper.setInputType(textInputLayout, editBuilder.inputTypeInt);

            if (editBuilder.allowEmpty)
                textInputHelper.allowEmpty(textInputLayout);
        } else {
            autoCompleteTextView.requestFocus();
            CustomUtility.changeWindowKeyboard(dialog.getWindow(), true);
        }

        if (editBuilder.onActionActionPair == null) {
            textInputHelper.addActionListener(textInputLayout, (textInputHelper1, textInputLayout1, actionId, text) -> {
                if (textInputHelper1.isValid() && finalButton != null)
                    finalButton.callOnClick();
            });
        } else
            textInputHelper.addActionListener(textInputLayout, editBuilder.onActionActionPair.first, editBuilder.onActionActionPair.second);

        if (!editBuilder.text.isEmpty())
            textInputHelper.validate();

        if (editBuilder != null && editBuilder.disableButtonByDefault)
            button.setEnabled(false);

        if (editBuilder.fireActionDirectly)
            editBuilder.onActionActionPair.first.runOnAction(textInputHelper, textInputLayout, -1, textInputHelper.getText(textInputLayout));

        if (editBuilder.dropDownList != null && !editBuilder.dropDownList.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, editBuilder.dropDownList);
            autoCompleteTextView.setAdapter(adapter);
            if (editBuilder.onItemClickListener != null)
                autoCompleteTextView.setOnItemClickListener(editBuilder.onItemClickListener);
            if (editBuilder.showDropdownDirectly)
                addOnDialogShown_system(customDialog -> autoCompleteTextView.showDropDown());
        }
    }

    private void applyText(TextView textView, TextBuilder builder) {
        if ((builder.text != null))
            textView.setText(builder.text);
        if ((builder.color != -1))
            textView.setTextColor(builder.color);
        if ((builder.size != -1))
            textView.setTextSize(builder.size);
//        if ((builder.alignment != -1))
//            textView.setTextAlignment(builder.alignment);

    }
    //  <------------------------- Apply -------------------------

    public static void setDialogLayoutParameters(Dialog dialog, boolean width, boolean height) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        if (width)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        else
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        if (height)
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        else
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }
}
