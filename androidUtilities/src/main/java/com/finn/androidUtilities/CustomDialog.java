package com.finn.androidUtilities;

import android.app.Dialog;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apmem.tools.layouts.FlowLayout;

import java.util.Optional;

public class CustomDialog {

    public enum BUTTON_CONFIGURATION {
        YES_NO, SAVE_CANCEL, BACK, OK, OK_CANCEL, CUSTOM
    }

    public enum BUTTON_TYPE {
        YES_BUTTON("Ja"), NO_BUTTON("Nein"), SAVE_BUTTON("Speichern")
        , CANCEL_BUTTON("Abbrechen"), BACK_BUTTON("Zurück"), OK_BUTTON("Ok"), DELETE_BUTTON("Löschen")
        , GO_TO_BUTTON("Gehe zu"), EDIT_BUTTON("Bearbeiten");

        String label;

        BUTTON_TYPE(String label) {
            this.label = label;
        }
    }

    private Dialog dialog;
    private Context context;
    private CharSequence title;
    private CharSequence text;
    private View view;
    private BUTTON_CONFIGURATION buttonConfiguration = BUTTON_CONFIGURATION.CUSTOM;
    private Pair<Boolean, Boolean> dimensions = new Pair<>(true, false);
    private boolean dividerVisibility = true;
    private Object objectExtra;
    private boolean scroll = true;
    private EditBuilder editBuilder;
    private boolean showEdit;
    private boolean buttonLabelAllCaps = true;
    private boolean stackButtons;
    private boolean expandButtons;
    private boolean firstTime = true;

    private SetViewContent setViewContent;

    private CustomList<ButtonHelper> buttonHelperList = new CustomList<>();



    public CustomDialog(Context context) {
        this.context = context;
        dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.dialog_custom);
    }

    public static CustomDialog Builder(Context context) {
        CustomDialog customDialog = new CustomDialog(context);
        return customDialog;
    }


    //  ----- Getters & Setters ----->
    public CustomDialog setContext(Context context) {
        this.context = context;
        return this;
    }

    public CustomDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public CustomDialog setText(CharSequence text) {
        this.text = text;
        return this;
    }

    public CustomDialog setView(int layoutId) {
        LayoutInflater li = LayoutInflater.from(context);
        this.view = li.inflate(layoutId, null);
        return this;
    }

    public CustomDialog setView(View view) {
        this.view = view;
        return this;
    }

    public View getView() {
        return view;
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
        this.dimensions = new Pair<>(width, height);
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

    public Dialog getDialog() {
        return dialog;
    }

    public Object getObjectExtra() {
        return objectExtra;
    }

    public CustomDialog setObjectExtra(Object objectExtra) {
        this.objectExtra = objectExtra;
        return this;
    }

    public CustomDialog setOnDialogDismiss(OnDialogCallback onDialogDismiss) {
        dialog.setOnDismissListener(dialog1 -> onDialogDismiss.runOnDialogCallback(this));
        return this;
    }

    public CustomDialog setOnDialogShown(OnDialogCallback onDialogShown) {
        dialog.setOnShowListener(dialog1 -> onDialogShown.runOnDialogCallback(this));
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

    //  <----- Getters & Setters -----


    //  ----- Interfaces ----->
    public interface OnClick {
        void runOnClick(CustomDialog customDialog);
    }

    public interface SetViewContent{
        void runSetViewContent(CustomDialog customDialog, View view);
    }

    public interface OnDialogCallback {
        void runOnDialogCallback(CustomDialog customDialog);
    }

    public interface GoToFilter<T>{
        boolean runGoToFilter(String search, T t);
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

        private Helpers.TextInputHelper.INPUT_TYPE inputType = Helpers.TextInputHelper.INPUT_TYPE.CAP_SENTENCES;
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
    }

    public static class TextBuilder{
    }
    //  <----- Builder -----


    //  ----- Convenience ----->
    public CustomDialog dismiss() {
    dialog.dismiss();
    return this;
}

    public <T extends View> T findViewById(int id) {
        return dialog.findViewById(id);
    }

    public String getEditText() {
        TextInputEditText editText = dialog.findViewById(R.id.dialog_custom_edit);
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
                .filter(buttonHelper -> (buttonHelper.buttonType == BUTTON_TYPE.OK_BUTTON || buttonHelper.buttonType == BUTTON_TYPE.SAVE_BUTTON || buttonHelper.buttonType == BUTTON_TYPE.YES_BUTTON))
                .findFirst();
        return optional.orElse(null);
    }

    public ButtonHelper getButton(int id) {
        Optional<ButtonHelper> optional = buttonHelperList.stream()
                .filter(buttonHelper -> buttonHelper.id == id)
                .findFirst();
        return optional.orElse(null);
    }
    //  <----- Convenience -----


    //  ----- Buttons ----->
    public class ButtonHelper {
        private Integer id;
        private String label;
        private BUTTON_TYPE buttonType;
        private OnClick onClick;
        private boolean dismiss;
        private Button button;
        private boolean alignLeft;
        private boolean disabled;
        private boolean hidden;

        public ButtonHelper(BUTTON_TYPE buttonType) {
            this.buttonType = buttonType;
            label = buttonType.label;
            dismiss = true;
            button = new Button(context);
            button.setBackground(dialog.findViewById(R.id.dialog_custom_Button1).getBackground().getConstantState().newDrawable());
            button.setTextColor(((Button)dialog.findViewById(R.id.dialog_custom_Button1)).getTextColors());
        }

        public ButtonHelper(String label, BUTTON_TYPE buttonType, OnClick onClick, Integer id, boolean dismiss) {
            this.id = id;
            this.label = label;
            this.buttonType = buttonType;
            this.onClick = onClick;
            this.dismiss = dismiss;
//            gravityLeft = true;
        }

        public Button generateButton() {
            Button button = new Button(context);
            button.setBackground(dialog.findViewById(R.id.dialog_custom_Button1).getBackground().getConstantState().newDrawable());
            button.setTextColor(((Button)dialog.findViewById(R.id.dialog_custom_Button1)).getTextColors());
            if (stackButtons || expandButtons) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                button.setLayoutParams(params);
            }



            if (label != null)
                button.setText(label);
            else if (buttonType != null)
                button.setText(buttonType.label);

            if (id != null)
                button.setId(id);
            else {
                id = View.generateViewId();
                button.setId(id);
            }

            if (!buttonLabelAllCaps)
                button.setAllCaps(false);

            if (disabled)
                button.setEnabled(false);

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
            this.button = button;

            button.setOnClickListener(v -> {
                if (dismiss)
                    dialog.dismiss();

                if (onClick != null)
                    onClick.runOnClick(CustomDialog.this);
            });
            return button;
        }

        public ButtonHelper setEnabled(boolean enabled) {
            button.setEnabled(enabled);
            return this;
        }

        public ButtonHelper setVisibility(int visibility) {
            button.setVisibility(visibility);
            return this;
        }

        public Button getButton() {
            return button;
        }
    }

    public CustomDialog addButton(String buttonName) {
        return addButton_complete(buttonName, null, null, null, true);
    }
    public CustomDialog addButton(String buttonName, OnClick onClick) {
        return addButton_complete(buttonName, null, onClick, null, true);
    }
    public CustomDialog addButton(String buttonName, OnClick onClick, int buttonId) {
        return addButton_complete(buttonName, null, onClick, buttonId, true);
    }
    public CustomDialog addButton(String buttonName, OnClick onClick, boolean dismissDialog){
        return addButton_complete(buttonName, null, onClick, null, dismissDialog);
    }
    public CustomDialog addButton(String buttonName, OnClick onClick, int buttonId, boolean dismissDialog){
        return addButton_complete(buttonName, null, onClick, buttonId, dismissDialog);
    }

    public CustomDialog addButton(BUTTON_TYPE button_type) {
        return addButton_complete(null , button_type, null, null, true);
    }
    public CustomDialog addButton(BUTTON_TYPE button_type, OnClick onClick) {
        return addButton_complete(null , button_type, onClick, null, true);
    }
    public CustomDialog addButton(BUTTON_TYPE button_type, OnClick onClick, int buttonId) {
        return addButton_complete(null , button_type, onClick, buttonId, true);
    }
    public CustomDialog addButton(BUTTON_TYPE button_type, OnClick onClick, boolean dismissDialog){
        return addButton_complete(null , button_type, onClick, null, dismissDialog);
    }
    public CustomDialog addButton(BUTTON_TYPE button_type, OnClick onClick, int buttonId, boolean dismissDialog){
        return addButton_complete(null , button_type, onClick, buttonId, dismissDialog);
    }


    private CustomDialog addButton_complete(String buttonName, BUTTON_TYPE button_type, OnClick onClick, Integer buttonId, boolean dismissDialog) {
        ButtonHelper buttonHelper = new ButtonHelper(buttonName, button_type, onClick, buttonId, dismissDialog);
        buttonHelperList.add(buttonHelper);
        return this;
    }

    public CustomDialog hideLastAddedButton(){
        buttonHelperList.getLast().hidden = true;
        return this;
    }
    public CustomDialog disableLastAddedButton(){
        buttonHelperList.getLast().disabled = true;
        return this;
    }
    public CustomDialog alignPreviousButtonsLeft() {
        buttonHelperList.forEach(buttonHelper -> buttonHelper.alignLeft = true);
        return this;
    }

    public CustomDialog addGoToButton(CustomRecycler.GoToFilter goToFilter, CustomRecycler customRecycler) {
        return addButton_complete(null , BUTTON_TYPE.GO_TO_BUTTON, customDialog -> customRecycler.goTo(goToFilter, null), null, false);
    }
    //  <----- Buttons -----


    //  ----- Actions ----->
    public Dialog show_dialog() {
    show();
    return dialog;
}

    public CustomDialog show() {
        if (!firstTime){
            dialog.show();
            return this;
        }
        // ToDo: TextBuilder

        if (title != null) {
            ((TextView) dialog.findViewById(R.id.dialog_custom_title)).setText(this.title);
            dialog.findViewById(R.id.dialog_custom_title_layout).setVisibility(View.VISIBLE);
        }

        if (text != null) {
            ((TextView) dialog.findViewById(R.id.dialog_custom_text)).setText(this.text);
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
        FlowLayout dialog_custom_buttonLayout_left = dialog.findViewById(R.id.dialog_custom_buttonLayout_left);
        FlowLayout dialog_custom_buttonLayout_right = dialog.findViewById(R.id.dialog_custom_buttonLayout_right);
        if (dialog_custom_buttonLayout_left != null && dialog_custom_buttonLayout_right != null) {
            LinearLayout.LayoutParams layoutParams_left =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, dialog_custom_buttonLayout_right.getChildCount());
            LinearLayout.LayoutParams layoutParams_right =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, dialog_custom_buttonLayout_left.getChildCount());
            dialog_custom_buttonLayout_left.setLayoutParams(layoutParams_left);
            dialog_custom_buttonLayout_right.setLayoutParams(layoutParams_right);
        }

        if (showEdit) {
            applyEdit();
        }

        if (setViewContent != null)
            setViewContent.runSetViewContent(this, view);

        setDialogLayoutParameters(dialog, dimensions.first, dimensions.second);
        dialog.show();

        firstTime = false;
        return this;
    }

    public CustomDialog reloadView() {
        setViewContent.runSetViewContent(this, view);
        return this;
    }
    //  <----- Actions -----

    private void applyEdit() {
        TextInputLayout textInputLayout = dialog.findViewById(R.id.dialog_custom_edit_editLayout);
        TextInputEditText textInputEditText = dialog.findViewById(R.id.dialog_custom_edit);

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
                textInputEditText.requestFocus();
                CustomUtility.changeWindowKeyboard(dialog.getWindow(), true);
            }

            if (!editBuilder.text.isEmpty())
                textInputEditText.setText(editBuilder.text);

            textInputLayout.setHint(editBuilder.hint);

            if (editBuilder.selectAll)
                textInputEditText.selectAll();
            else
                textInputEditText.setSelection(editBuilder.text.length());

            if (!editBuilder.regEx.isEmpty())
                textInputHelper.setValidation(textInputLayout, editBuilder.regEx);
            else if (editBuilder.textValidation != null)
                textInputHelper.setValidation(textInputLayout, editBuilder.textValidation);

            if (editBuilder.inputType != null)
                textInputHelper.setInputType(textInputLayout, editBuilder.inputType);

            if (editBuilder.allowEmpty)
                textInputHelper.allowEmpty(textInputLayout);
        } else {
            textInputEditText.requestFocus();
            CustomUtility.changeWindowKeyboard(dialog.getWindow(), true);
        }

        if (editBuilder.onActionActionPair == null) {
            textInputHelper.addActionListener(textInputLayout, (textInputHelper1, textInputLayout1, actionId, text) -> {
                if (textInputHelper1.isValid() && finalButton != null)
                    finalButton.callOnClick();
            });
        }
        else
            textInputHelper.addActionListener(textInputLayout, editBuilder.onActionActionPair.first, editBuilder.onActionActionPair.second);

        if (!editBuilder.text.isEmpty())
            textInputHelper.validate();

        if (editBuilder != null && editBuilder.disableButtonByDefault)
            button.setEnabled(false);

        if (editBuilder.fireActionDirectly)
            editBuilder.onActionActionPair.first.runOnAction(textInputHelper, textInputLayout, -1, textInputHelper.getText(textInputLayout));
    }

    static void setDialogLayoutParameters(Dialog dialog, boolean width, boolean height) {
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
