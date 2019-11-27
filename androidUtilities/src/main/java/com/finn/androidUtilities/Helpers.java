package com.finn.androidUtilities;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Helpers {
    //  ----- TextInput ----->
    public static class TextInputHelper {
        public enum INPUT_TYPE {
            TEXT(0x00000001), NUMBER(0x00000002), NUMBER_DECIMAL(0x00002002), CAP_SENTENCES(0x00004001),
            CAPS_LOCK(0x00001001), CAPS_WORD(0x00002001), MULTI_LINE(0x00040001), E_MAIL(0x00000021), PASSWORD(0x00000081), NUMBER_PASSWORD(0x00000012), DATE_TIME(0x00000004), DATE(0x00000014), TIME(0x00000024);

            int code;

            INPUT_TYPE(int code) {
                this.code = code;
            }
        }

        public enum IME_ACTION {
            GO(0x00000002), SEARCH(0x00000003), SEND(0x00000004), NEXT(0x00000005), DONE(0x00000006), PREVIOUS(0x00000007);

            int code;

            IME_ACTION(int code) {
                this.code = code;
            }

            public int getCode() {
                return code;
            }
        }

        private CustomList<TextInputLayout> layoutList;
        private Map<TextInputLayout, Validator> inputValidationMap = new HashMap<>();
        private OnValidationResult onValidationResult;
        private boolean valid;
        private INPUT_TYPE defaultInputType = INPUT_TYPE.CAP_SENTENCES;
        private Validator.STATUS status;


        public TextInputHelper(Button buttonToBind, TextInputLayout... inputLayouts) {
            this.onValidationResult = buttonToBind::setEnabled;
            this.layoutList = new CustomList<>(inputLayouts);
            inputValidationMap = this.layoutList.stream().collect(Collectors.toMap(o -> o, Validator::new));
            applyValidationListeners();
            layoutList.forEach(textInputLayout -> textInputLayout.getEditText().setInputType(defaultInputType.code));
        }

        public TextInputHelper(OnValidationResult onValidationResult, TextInputLayout... inputLayouts) {
            this.onValidationResult = onValidationResult;
            this.layoutList = new CustomList<>(inputLayouts);
            inputValidationMap = this.layoutList.stream().collect(Collectors.toMap(o -> o, Validator::new));
            applyValidationListeners();
            layoutList.forEach(textInputLayout -> textInputLayout.getEditText().setInputType(defaultInputType.code));
        }

        public TextInputHelper() {}

        //  ----- Validation ----->
        public Validator.STATUS validate(TextInputLayout... layoutLists) {
            List<TextInputLayout> inputLayoutList = new CustomList<>(layoutLists);
            status = Validator.STATUS.VALID;
            for (Map.Entry<TextInputLayout, Validator> entry : inputValidationMap.entrySet()) {
                Validator.STATUS validate = entry.getValue().validate(entry.getKey().getEditText().getText().toString().trim(),
                        inputLayoutList.contains(entry.getKey()) || layoutLists.length == 0);
                if (validate.getLevel() > status.getLevel()) {
                    status = validate;
                }
            }
            valid = status.isValid();
            if (onValidationResult != null)
                onValidationResult.runOnValidationResult(status.isValid());
            return status;
        }

        public TextInputHelper setValidation(TextInputLayout textInputLayout, TextValidation textValidation) {
            inputValidationMap.get(textInputLayout).setTextValidation(textValidation);
            return this;
        }

        public void setValidation(TextInputLayout textInputLayout, String regEx) {
            inputValidationMap.get(textInputLayout).setRegEx(regEx);
        }

        public TextInputHelper addValidator(@NonNull TextInputLayout... textInputLayouts) {
            for (TextInputLayout textInputLayout : textInputLayouts) {
                inputValidationMap.put(textInputLayout, new Validator(textInputLayout));
                applyValidationListerner(textInputLayout);
                textInputLayout.getEditText().setInputType(defaultInputType.code);
            }
            return this;
        }

        public void applyValidationListeners(TextInputLayout... inputLayouts) {
            if (inputLayouts.length > 0) {
                for (TextInputLayout inputLayout : inputLayouts)
                    applyValidationListerner(inputLayout);
            } else {
                for (TextInputLayout inputLayout : inputValidationMap.keySet())
                    applyValidationListerner(inputLayout);
            }
        }

        private void applyValidationListerner(TextInputLayout textInputLayout) {
            textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    validate(textInputLayout);
                }
            });
        }

        public boolean isValid() {
            return valid;
        }

        public TextInputHelper removeStandardValidation(TextInputLayout... textInputLayouts) {
            for (TextInputLayout textInputLayout : textInputLayouts) {
                inputValidationMap.get(textInputLayout).alwaysUseDefaultValidation = false;
            }
            return this;
        }

        public TextInputHelper allowEmpty(@NonNull TextInputLayout... textInputLayouts) {
            for (TextInputLayout textInputLayout : textInputLayouts) {
                inputValidationMap.get(textInputLayout).allowEmpty = true;
            }
            return this;
        }

        public TextInputHelper warnIfEmpty(@NonNull TextInputLayout... textInputLayouts) {
            for (TextInputLayout textInputLayout : textInputLayouts) {
                inputValidationMap.get(textInputLayout).warnIfEmpty = true;
            }
            return this;
        }

        public interface TextValidation {
            void runTextValidation(Validator validator, String text);
        }

        public static class Validator {
            enum STATUS {
                NONE(false, false, 3), VALID(true, true, 0), INVALID(false, false, 2), WARNING(true, false, 1);

                private boolean valid;
                private boolean allwaysValid;
                private int level;

                STATUS(boolean valid, boolean allwaysValid, int level) {
                    this.valid = valid;
                    this.allwaysValid = allwaysValid;
                    this.level = level;
                }

                public boolean isValid() {
                    return valid;
                }

                public boolean isAlwaysValid() {
                    return allwaysValid;
                }

                public int getLevel() {
                    return level;
                }
            }

            enum MODE {
                WHITE_LIST, BLACK_LIST
            }

            private boolean alwaysUseDefaultValidation = true;
            private STATUS status = STATUS.NONE;
            private MODE mode;
            private MODE defaultMode = MODE.BLACK_LIST;
            private String message = "<Fehler>";
            private TextValidation textValidation;
            private TextInputLayout textInputLayout;
            private TextWatcher textWatcher;
            private boolean useDefaultValidation = true;
            private boolean allowEmpty;
            private boolean warnIfEmpty;
            private String regEx = "";
            private int warningColor = 0xffFFB300;
            private int errorColor = 0xFFFF7043;

            public Validator(TextInputLayout textInputLayout) {
                this.textInputLayout = textInputLayout;
            }

            private void reset() {
                message = null;
                status = STATUS.NONE;
                useDefaultValidation = alwaysUseDefaultValidation;
                message = "<Fehler>";
                mode = defaultMode;
            }

            private void defaultValidation(String text, boolean changeErrorMessage) {
                if (text.isEmpty() && !allowEmpty && !warnIfEmpty) {
                    if (changeErrorMessage)
                        message = "Das Feld darf nicht leer sein!";
                    status = STATUS.INVALID;
                } else if (text.isEmpty() && !allowEmpty && warnIfEmpty){
                    if (changeErrorMessage)
                        message = "Das Feld ist leer!";
                    status = STATUS.WARNING;
                } else {
                    switch (mode) {
                        case BLACK_LIST:
                            if (changeErrorMessage)
                                message = null;
                            status = STATUS.VALID;
                            break;
                        case WHITE_LIST:
                            if (changeErrorMessage)
                                message = "Ungültige Eingabe";
                            status = STATUS.INVALID;
                            break;
                    }
                }
            }

            public STATUS validate(String text, boolean changeErrorMessage) {
                reset();

                if (textValidation != null && regEx.isEmpty())
                    textValidation.runTextValidation(this, text);

                if (!regEx.isEmpty()) {
                    if (text.matches(regEx)) {
                        status = STATUS.VALID;
                        message = null;
                    } else {
                        status = STATUS.INVALID;
                        message = "Ungültige Eingabe";
                    }
                }

                if (status == STATUS.NONE && useDefaultValidation)
                    defaultValidation(text, changeErrorMessage);

                if (changeErrorMessage)
                    textInputLayout.setError(message);



                switch (status) {
                    case WARNING:
                        setMessageColor(warningColor);
                        return STATUS.WARNING;
                    case VALID:
                        return STATUS.VALID;

                    default:
                    case INVALID:
                        setMessageColor(errorColor);
                        return STATUS.INVALID;
                }
            }

            private void setMessageColor(int color) {
                int[][] states = {{android.R.attr.state_enabled}};
                int[] colors = {color};
                textInputLayout.setErrorTextColor(new ColorStateList(states, colors));
                if (textInputLayout.hasFocus()) {
                    textInputLayout.setVisibility(View.GONE);
                    textInputLayout.setVisibility(View.VISIBLE);
                    textInputLayout.requestFocus();
                } else {
                    textInputLayout.setEnabled(false);
                    textInputLayout.setEnabled(true);
                }

            }

            public Validator setTextValidation(TextValidation textValidation) {
                this.textValidation = textValidation;
                return this;
            }

            public TextWatcher getTextWatcher() {
                return textWatcher;
            }

            public Validator setTextWatcher(TextWatcher textWatcher) {
                this.textWatcher = textWatcher;
                return this;
            }

            public void setValid() {
                message = null;
                status = STATUS.VALID;
            }

            public void setInvalid(String errorMessage) {
                this.message = errorMessage;
                status = STATUS.INVALID;
            }

            public void setWarning(String warningMessage) {
                this.message = warningMessage;
                status = STATUS.WARNING;
            }

            public void asWhiteList() {
                mode = MODE.WHITE_LIST;
            }

            public void asBlackList() {
                mode = MODE.BLACK_LIST;
            }

            public Validator setDefaultMode(MODE defaultMode) {
                this.defaultMode = defaultMode;
                return this;
            }

            public Validator allowEmpty() {
                this.allowEmpty = true;
                return this;
            }

            public Validator setRegEx(String regEx) {
                this.regEx = regEx;
                return this;
            }

            public Validator setErrorColor(int errorColor) {
                this.errorColor = errorColor;
                return this;
            }

            public Validator setWarningColor(int warningColor) {
                this.warningColor = warningColor;
                return this;
            }

            public String getMessage() {
                return message;
            }
        }

        public interface OnValidationResult {
            void runOnValidationResult(boolean result);
        }
        //  <----- Validation -----


        //  ----- Action ----->
        public TextInputHelper addActionListener(TextInputLayout textInputLayout, OnAction onAction, IME_ACTION... actions) {
            textInputLayout.getEditText().setOnEditorActionListener((v, actionId, event) -> {
                boolean handled = false;
                if (actions.length == 0) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        onAction.runOnAction(this, textInputLayout, actionId, textInputLayout.getEditText().getText().toString());
                        handled = true;
                    }
                } else {
                    if (new CustomList<IME_ACTION>(actions).map(IME_ACTION::getCode).contains(actionId)) {
                        onAction.runOnAction(this, textInputLayout, actionId, textInputLayout.getEditText().getText().toString());
                        handled = true;
                    }
                }
                return handled;
            });
            if (actions.length == 0) {
                textInputLayout.getEditText().setImeOptions(IME_ACTION.DONE.code);
            } else {
                final int[] actionFlag = {actions[0].code};
                new CustomList<IME_ACTION>(actions).forEachCount((imeAction, count) -> {
                    if (count == 0) return;

                    actionFlag[0] = actionFlag[0] | imeAction.code;
                });
                textInputLayout.getEditText().setImeOptions(actionFlag[0]);
            }
            return this;
        }

        public interface OnAction {
            void runOnAction(TextInputHelper textInputHelper, TextInputLayout textInputLayout, int actionId, String text);
        }
        //  <----- Action -----


        //  ----- Convenience ----->
        public TextInputHelper disableSelectAll(TextInputLayout textInputLayout) {
            textInputLayout.getEditText().setSelectAllOnFocus(false);
            return this;
        }

        public TextInputHelper setInputType(TextInputLayout textInputLayout, INPUT_TYPE inputType) {
            textInputLayout.getEditText().setInputType(inputType.code);
            return this;
        }

        public TextInputHelper setDefaultInputType(INPUT_TYPE defaultInputType) {
            this.defaultInputType = defaultInputType;
            return this;
        }

        public TextInputHelper setOnValidationResult(OnValidationResult onValidationResult) {
            this.onValidationResult = onValidationResult;
            return this;
        }

        public TextInputHelper bindButton(Button buttonToBind) {
            onValidationResult = buttonToBind::setEnabled;
            buttonToBind.callOnClick();
            View.OnClickListener onClickListener = v -> {};
            return this;
        }

        public TextInputHelper bindButton(CustomDialog.ButtonHelper buttonToBind) {
            onValidationResult = buttonToBind::setEnabled;
            return this;
        }

        public TextInputHelper defaultDialogValidation(CustomDialog customDialog) {
            bindButton(customDialog.getActionButton());
            return this;
        }

        public TextInputHelper setText(TextInputLayout textInputLayout, String text) {
            if (textInputLayout.getEditText() != null) {
                textInputLayout.getEditText().setText(text);
            }
            return this;
        }

        public String getText(TextInputLayout textInputLayout) {
            if (textInputLayout.getEditText() != null) {
                return textInputLayout.getEditText().getText().toString();
            }
            return null;
        }

        public TextInputHelper interceptDialogActionForValidation(CustomDialog customDialog, Runnable... onIntercept_onAllow) {
            return interceptForValidation(customDialog.getActionButton().getButton(), onIntercept_onAllow);
        }
        public TextInputHelper interceptForValidation(View view, Runnable... onIntercept_onAllow) {
            final long[] time = {0};
            CustomUtility.interceptOnClick(view, view1 -> {
                long currentTime = System.currentTimeMillis();
                boolean alwaysValid = validate().isAlwaysValid();
                if (currentTime - time[0] > 500 && !alwaysValid) {
                    time[0] = currentTime;
                    if (onIntercept_onAllow.length > 0)
                        onIntercept_onAllow[0].run();
                    return true;
                }
                if (onIntercept_onAllow.length > 1)
                    onIntercept_onAllow[1].run();
                return false;
            });
            return this;
        }

        public List<String> getMessage(TextInputLayout... textInputLayouts) {
            List<String> messageList = new ArrayList<>();
            if (textInputLayouts.length == 0)
                textInputLayouts = inputValidationMap.keySet().toArray(new TextInputLayout[0]);
            for (TextInputLayout textInputLayout : textInputLayouts) {
                String message = inputValidationMap.get(textInputLayout).getMessage();
                if (message != null)
                    messageList.add(message);
            }
            return messageList;
        }
        //  <----- Convenience -----
    }
    //  <----- TextInput -----


    //  --------------- SpannableString --------------->
    public static class SpannableStringHelper {
        public enum SPAN_TYPE {
            BOLD(new StyleSpan(Typeface.BOLD)), ITALIC(new StyleSpan(Typeface.ITALIC)), BOLD_ITALIC(new StyleSpan(Typeface.BOLD_ITALIC)), STRIKE_THROUGH(new StrikethroughSpan()),
            UNDERLINED(new UnderlineSpan());

            Object what;

            SPAN_TYPE(Object what) {
                this.what = what;
            }

            public Object getWhat() {
                return what;
            }
        }
        private SpannableStringBuilder builder = new SpannableStringBuilder();

        public SpannableStringHelper append(String text) {
            builder.append(text);
            return this;
        }

        public SpannableStringHelper append(String text, SPAN_TYPE span_type) {
            builder.append(text, span_type.getWhat(), Spannable.SPAN_COMPOSING);
            return this;
        }

        public SpannableStringHelper appendColor(String text, int color) {
            builder.append(text, new ForegroundColorSpan(color), Spannable.SPAN_COMPOSING);
            return this;
        }

        public SpannableStringBuilder get() {
            return builder;
        }
    }
    //  <--------------- SpannableString ---------------
}
