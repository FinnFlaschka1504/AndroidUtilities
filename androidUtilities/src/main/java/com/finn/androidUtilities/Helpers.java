package com.finn.androidUtilities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Helpers {
    //  ----- TextInput ----->
    public static class TextInputHelper {
        public enum INPUT_TYPE {
            TEXT(0x00000001), NUMBER(0x00000002), NUMBER_DECIMAL(0x00002002), CAP_SENTENCES(0x00004001),
            CAPS_LOCK(0x00001001), CAPS_WORD(0x00002001), MULTI_LINE(0x00020001), E_MAIL(0x00000021), PASSWORD(0x00000081), NUMBER_PASSWORD(0x00000012), DATE_TIME(0x00000004), DATE(0x00000014), TIME(0x00000024);

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


        public TextInputHelper(TextInputLayout... inputLayouts) {
            this.layoutList = new CustomList<>(inputLayouts);
            inputValidationMap = this.layoutList.stream().collect(Collectors.toMap(o -> o, Validator::new));
            applyValidationListeners();
            layoutList.forEach(textInputLayout -> {
                if (textInputLayout.getEditText().getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE))
                    textInputLayout.getEditText().setInputType(defaultInputType.code);
            });
        }

        public TextInputHelper(Button buttonToBind, TextInputLayout... inputLayouts) {
            this.onValidationResult = buttonToBind::setEnabled;
            this.layoutList = new CustomList<>(inputLayouts);
            inputValidationMap = this.layoutList.stream().collect(Collectors.toMap(o -> o, Validator::new));
            applyValidationListeners();
            layoutList.forEach(textInputLayout -> {
                if (textInputLayout.getEditText().getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE))
                    textInputLayout.getEditText().setInputType(defaultInputType.code);
            });
        }

        public TextInputHelper(OnValidationResult onValidationResult, TextInputLayout... inputLayouts) {
            this.onValidationResult = onValidationResult;
            this.layoutList = new CustomList<>(inputLayouts);
            inputValidationMap = this.layoutList.stream().collect(Collectors.toMap(o -> o, Validator::new));
            applyValidationListeners();
            layoutList.forEach(textInputLayout -> {
                if (textInputLayout.getEditText().getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE))
                    textInputLayout.getEditText().setInputType(defaultInputType.code);
            });
        }

        public TextInputHelper() {}

        //  ----- Validation ----->
        public Validator.STATUS validate(@Nullable TextInputLayout... layoutLists) {
            List<TextInputLayout> inputLayoutList = new CustomList<>(layoutLists);
            status = Validator.STATUS.VALID;
            for (Map.Entry<TextInputLayout, Validator> entry : inputValidationMap.entrySet()) {
                Validator.STATUS validate = entry.getValue().validate(entry.getKey().getEditText().getText().toString().trim(),
                        layoutLists != null && inputLayoutList.size() == 0 ? null : inputLayoutList.contains(entry.getKey()));
                if (validate.getLevel() > status.getLevel())
                    status = validate;
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

        public TextInputHelper setValidation(TextInputLayout textInputLayout, String regEx) {
            inputValidationMap.get(textInputLayout).setRegEx(regEx);
            return this;
        }

        public TextInputHelper addValidator(@NonNull TextInputLayout... textInputLayouts) {
            for (TextInputLayout textInputLayout : textInputLayouts) {
                inputValidationMap.put(textInputLayout, new Validator(textInputLayout));
                applyValidationListener(textInputLayout);
                if (textInputLayout.getEditText().getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE))
                    textInputLayout.getEditText().setInputType(defaultInputType.code);
            }
            return this;
        }

        public TextInputHelper applyValidationListeners(TextInputLayout... inputLayouts) {
            if (inputLayouts.length > 0) {
                for (TextInputLayout inputLayout : inputLayouts)
                    applyValidationListener(inputLayout);
            } else {
                for (TextInputLayout inputLayout : inputValidationMap.keySet())
                    applyValidationListener(inputLayout);
            }
            return this;
        }

        private TextInputHelper applyValidationListener(TextInputLayout textInputLayout) {
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
            return this;
        }

        public boolean isValid() {
            return valid;
        }

        public Validator.STATUS getStatus() {
            return status;
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
            public enum STATUS {
                NONE(false, false, 3), VALID(true, true, 0), INVALID(false, false, 2), WARNING(true, false, 1);

                private boolean valid;
                private boolean alwaysValid;
                private int level;

                STATUS(boolean valid, boolean alwaysValid, int level) {
                    this.valid = valid;
                    this.alwaysValid = alwaysValid;
                    this.level = level;
                }

                public boolean isValid() {
                    return valid;
                }

                public boolean isAlwaysValid() {
                    return alwaysValid;
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
            private STATUS prevStatus = STATUS.NONE;
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
            private boolean alreadyEdited;

            public Validator(TextInputLayout textInputLayout) {
                this.textInputLayout = textInputLayout;
            }

            private void reset() {
                message = null;
                prevStatus = status;
                status = STATUS.NONE;
                useDefaultValidation = alwaysUseDefaultValidation;
                message = "<Fehler>";
                mode = defaultMode;
            }

            private void defaultValidation(String text) {
                if (text.isEmpty() && !allowEmpty && !warnIfEmpty) {
                    message = "Das Feld darf nicht leer sein!";
                    status = STATUS.INVALID;
                } else if (text.isEmpty() && !allowEmpty && warnIfEmpty){
                    message = "Das Feld ist leer!";
                    status = STATUS.WARNING;
                } else if (text.isEmpty() && allowEmpty){
                    message = null;
                    status = STATUS.VALID;
                } else {
                    switch (mode) {
                        case BLACK_LIST:
                            message = null;
                            status = STATUS.VALID;
                            break;
                        case WHITE_LIST:
                            message = "Ungültige Eingabe";
                            status = STATUS.INVALID;
                            break;
                    }
                }
            }

            public STATUS validate(String text, Boolean changeErrorMessage) {
                boolean force = false;
                if (changeErrorMessage == null) {
                    changeErrorMessage = true;
                    force = true;
                }

                if (changeErrorMessage && !text.isEmpty())
                    alreadyEdited = true;
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
                    defaultValidation(text);

                if (force && status != STATUS.VALID)
                    alreadyEdited = true;

                if ((changeErrorMessage || prevStatus != status) && (alreadyEdited || force))
                    textInputLayout.setError(message);

                switch (status) {
                    case WARNING:
                        if (prevStatus != status)
                            setMessageColor(warningColor);
                        return STATUS.WARNING;
                    case VALID:
                        return STATUS.VALID;

                    default:
                    case INVALID:
                        if (prevStatus != status)
                            setMessageColor(errorColor);
                        return STATUS.INVALID;
                }
            }

            private void setMessageColor(int color) {
                int[][] states = {{android.R.attr.state_enabled}};
                int[] colors = {color};
                textInputLayout.setErrorTextColor(new ColorStateList(states, colors));
                if (textInputLayout.hasFocus()) {
                    Pair<Integer, Integer> selection = Pair.create(textInputLayout.getEditText().getSelectionStart(), textInputLayout.getEditText().getSelectionEnd());
                    textInputLayout.setVisibility(View.GONE);
                    textInputLayout.setVisibility(View.VISIBLE);
                    textInputLayout.requestFocus();
                    textInputLayout.getEditText().setSelection(selection.first, selection.second);
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
                    if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
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

        public TextInputHelper setInputType(TextInputLayout textInputLayout, int inputType) {
            textInputLayout.getEditText().setInputType(inputType);
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

        public TextInputHelper interceptDialogActionForValidation(CustomDialog customDialog, boolean allowLongPress, CustomUtility.GenericInterface<TextInputHelper>... onIntercept_onAllow) {
            return interceptForValidation(customDialog.getActionButton().getButton(), allowLongPress, onIntercept_onAllow);
        }

        public TextInputHelper interceptForValidation(View view, boolean allowLongPress, CustomUtility.GenericInterface<TextInputHelper>... onIntercept_onAllow) {
            final long[] time = {0};
            CustomUtility.interceptOnClick(view, allowLongPress, view1 -> {
                long currentTime = System.currentTimeMillis();
                boolean alwaysValid = validate().isAlwaysValid();
                if (currentTime - time[0] > 400 && !alwaysValid) {
                    time[0] = currentTime;
                    if (onIntercept_onAllow.length > 0)
                        onIntercept_onAllow[0].run(this);
                    return true;
                }
                if (onIntercept_onAllow.length > 1)
                    onIntercept_onAllow[1].run(this);
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

        public TextInputHelper addOptionalModifications(CustomUtility.GenericInterface<TextInputHelper> optionalModifications) {
            optionalModifications.run(this);
            return this;
        }
        //  <----- Convenience -----
    }
    //  <----- TextInput -----


    //  ------------------------- DoubleClickHelper ------------------------->
    public static class DoubleClickHelper{
        private long lastClicked;
        private int delay = 400;
        private Runnable onFailed;
        private Runnable onSuccess;

        //  ------------------------- Constructor ------------------------->
        public DoubleClickHelper() {
        }

        public static DoubleClickHelper create() {
            return new DoubleClickHelper();
        }
        //  <------------------------- Constructor -------------------------

        public boolean check() {
            if (System.currentTimeMillis() - lastClicked > delay) {
                if (onFailed != null)
                    onFailed.run();
                lastClicked = System.currentTimeMillis();
                return false;
            }
            if (onSuccess != null)
                onSuccess.run();
            return true;
        }

        public DoubleClickHelper reset() {
            lastClicked = 0;
            return this;
        }

        //  ------------------------- Getter & Setter ------------------------->
        public long getLastClicked() {
            return lastClicked;
        }

        public int getDelay() {
            return delay;
        }

        public DoubleClickHelper setDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public DoubleClickHelper setOnFailed(Runnable onFailed) {
            this.onFailed = onFailed;
            return this;
        }

        public DoubleClickHelper setOnSuccess(Runnable onSuccess) {
            this.onSuccess = onSuccess;
            return this;
        }
        //  <------------------------- Getter & Setter -------------------------
    }
    //  <------------------------- DoubleClickHelper -------------------------


    //  --------------- SpannableString --------------->
    public static class SpannableStringHelper {
        public enum SPAN_TYPE {
            BOLD(o -> new StyleSpan(Typeface.BOLD)), ITALIC(o -> new StyleSpan(Typeface.ITALIC)), BOLD_ITALIC(o -> new StyleSpan(Typeface.BOLD_ITALIC)), STRIKE_THROUGH(o -> new StrikethroughSpan()),
            UNDERLINED(o -> new UnderlineSpan()), COLOR(color -> new ForegroundColorSpan((Integer) color)), RELATIVE_SIZE(size -> new RelativeSizeSpan((Float) size)), NONE(o -> null);

            CustomUtility.GenericReturnInterface<Object, ParcelableSpan> what;

            SPAN_TYPE(CustomUtility.GenericReturnInterface<Object, ParcelableSpan> what) {
                this.what = what;
            }

            public ParcelableSpan getWhat() {
                return what.run(null);
            }

            public ParcelableSpan getWhat(Object o) {
                return what.run(o);
            }
        }

        public static class MultipleSpans {
            Set<ParcelableSpan> spanSet = new HashSet<>();

            public MultipleSpans bold() {
                spanSet.add(SPAN_TYPE.BOLD.getWhat());
                return this;
            }

            public MultipleSpans italic() {
                spanSet.add(SPAN_TYPE.ITALIC.getWhat());
                return this;
            }

            public MultipleSpans boldItalic() {
                spanSet.add(SPAN_TYPE.BOLD_ITALIC.getWhat());
                return this;
            }

            public MultipleSpans strikeThrough() {
                spanSet.add(SPAN_TYPE.STRIKE_THROUGH.getWhat());
                return this;
            }

            public MultipleSpans underlined() {
                spanSet.add(SPAN_TYPE.UNDERLINED.getWhat());
                return this;
            }

            public MultipleSpans color(@ColorInt int color) {
                spanSet.add(SPAN_TYPE.COLOR.getWhat(color));
                return this;
            }

            public MultipleSpans relativeSize(float size) {
                spanSet.add(SPAN_TYPE.RELATIVE_SIZE.getWhat(size));
                return this;
            }

            // ---------------

            private void apply(CharSequence text, SpannableStringBuilder builder) {
                int previousLength = builder.length();
                int textLength = text.length();
                builder.append(text);
                for (ParcelableSpan span : spanSet) {
                    builder.setSpan(span, previousLength, previousLength + textLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        private SpannableStringBuilder builder = new SpannableStringBuilder();


        //  ------------------------- Append ------------------------->
        public SpannableStringHelper append(CharSequence text) {
            builder.append(text);
            return this;
        }

        public SpannableStringHelper append(CharSequence text, SPAN_TYPE span) {
            builder.append(text, span.getWhat(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public SpannableStringHelper append(CharSequence text, ParcelableSpan span) {
            builder.append(text, span, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public SpannableStringHelper appendMultiple(CharSequence text, CustomUtility.GenericReturnInterface<MultipleSpans, MultipleSpans> multipleSpans) {
            multipleSpans.run(new MultipleSpans()).apply(text, builder);
            return this;
        }

        public SpannableStringHelper appendBold(CharSequence text) {
            builder.append(text, SPAN_TYPE.BOLD.getWhat(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public SpannableStringHelper appendItalic(CharSequence text) {
            builder.append(text, SPAN_TYPE.ITALIC.getWhat(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public SpannableStringHelper appendBoldItalic(CharSequence text) {
            builder.append(text, SPAN_TYPE.BOLD_ITALIC.getWhat(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public SpannableStringHelper appendStrikeThrough(CharSequence text) {
            builder.append(text, SPAN_TYPE.STRIKE_THROUGH.getWhat(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public SpannableStringHelper appendUnderlined(CharSequence text) {
            builder.append(text, SPAN_TYPE.UNDERLINED.getWhat(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public SpannableStringHelper appendColor(CharSequence text, @ColorInt int color) {
            builder.append(text, SPAN_TYPE.COLOR.getWhat(color), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public SpannableStringHelper appendRelativeSize(CharSequence text, float size) {
            builder.append(text, SPAN_TYPE.RELATIVE_SIZE.getWhat(size), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public SpannableStringHelper appendCustom(CustomUtility.GenericInterface<SpannableStringBuilder> applyCustomSpan) {
            applyCustomSpan.run(builder);
            return this;
        }
        //  <------------------------- Append -------------------------

        public SpannableStringBuilder get() {
            return builder;
        }


        //  --------------- Quick... --------------->
        private Object quickWhat;

        public SpannableStringHelper setQuickWhat(Object quickWhat) {
            this.quickWhat = quickWhat;
            return this;
        }

        public SpannableStringBuilder quick(CharSequence text) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            spannableStringBuilder.setSpan(quickWhat, 0, text.length(), 0);
            return spannableStringBuilder;
        }

        public static SpannableStringBuilder quick(CharSequence text, Object what) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            spannableStringBuilder.setSpan(what, 0, text.length(), 0);
            return spannableStringBuilder;
        }

        public static SpannableStringBuilder quickBold(CharSequence text) {
            SpannableStringBuilder spannableString = new SpannableStringBuilder(text);
            spannableString.setSpan(SPAN_TYPE.BOLD.getWhat(), 0, text.length(), 0);
            return spannableString;
        }

        public static SpannableStringBuilder quickItalic(CharSequence text) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            spannableStringBuilder.setSpan(SPAN_TYPE.ITALIC.getWhat(), 0, text.length(), 0);
            return spannableStringBuilder;
        }

        public static SpannableStringBuilder quickStrikeThrough(CharSequence text) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            spannableStringBuilder.setSpan(SPAN_TYPE.STRIKE_THROUGH.getWhat(), 0, text.length(), 0);
            return spannableStringBuilder;
        }
        //  <--------------- Quick... ---------------

        //  ------------------------- Builder ------------------------->
        public  interface SpannableStringHelperInterface {
            SpannableStringHelper get(SpannableStringHelper spanBuilder);
        }

        public static SpannableStringBuilder Builder(SpannableStringHelperInterface spanBuilderInterface) {
            return spanBuilderInterface.get(new SpannableStringHelper()).get();
        }
        //  <------------------------- Builder -------------------------

        public static CharSequence highlightText(String search, String originalText) {
            if (CustomUtility.stringExists(search) && CustomUtility.stringExists(originalText)) {
                search = search.toLowerCase();
                String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
                int start = normalizedText.indexOf(search);
                if (start < 0) {
                    return originalText;
                } else {
                    Spannable highlighted = new SpannableString(originalText);
                    while (start >= 0) {
                        int spanStart = Math.min(start, originalText.length());
                        int spanEnd = Math.min(start + search.length(), originalText.length());
                        highlighted.setSpan(new BackgroundColorSpan(0x40FFD03F), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        start = normalizedText.indexOf(search, spanEnd);
                    }
                    return highlighted;
                }
            }
            return originalText;
        }
    }
    //  <--------------- SpannableString ---------------

    //  ------------------------- SortHelper ------------------------->
    public static class SortHelper<T> {
        private List<T> list;
        private List<Sorter> sorterList = new ArrayList<>();
        private boolean allReversed;

        public SortHelper() {
        }

        public SortHelper(List<T> list) {
            this.list = list;
        }

        public SortHelper<T> setList(List<T> list) {
            this.list = list;
            return this;
        }

        public Sorter<T> addSorter(Object type, Comparator<T> comparator) {
            return addSorter(comparator).setType(type);
        }

        public Sorter<T> addSorter(Object type) {
            return addSorter().setType(type);
        }

        public Sorter<T> addSorter() {
            Sorter<T> sorter = new Sorter<T>();
            sorter.parent = this;
            sorterList.add(sorter);
            return sorter;
        }

        public Sorter<T> addSorter(Comparator<T> comparator) {
            return addSorter().addCondition(comparator);
        }


        //  ------------------------- Sort ------------------------->
        public List<T> sort(Object type) {
            Optional<Sorter> optionalSorter = sorterList.stream().filter(sorter -> sorter.type.equals(type)).findFirst();
            return optionalSorter.map(sorter -> sorter.sort_private(list)).orElseGet(() -> list);
        }

        public List<T> sort_new(Object type) {
            Optional<Sorter> optionalSorter = sorterList.stream().filter(sorter -> sorter.type.equals(type)).findFirst();
            return optionalSorter.map(sorter -> sorter.sort_private(new ArrayList<T>(list))).orElseGet(() -> new ArrayList<>(list));
        }

        public interface GetSortType {
            Object runGetSortType();
        }

        public List<T> sort(GetSortType getSortType) {
            return sort(getSortType.runGetSortType());
        }

        public List<T> sort_new(GetSortType getSortType) {
            return sort_new(getSortType.runGetSortType());
        }
        //  <------------------------- Sort -------------------------

        public SortHelper<T> setAllReversed(boolean reversed) {
            allReversed = reversed;
            return this;
        }

        public class Sorter<E> {
            private SortHelper<T> parent;
            private boolean reversed;
            private boolean reverseDefaultComparable;
            private boolean reverseParentClass;
            private boolean nullToBottom = true;
            private List<Comparator<E>> comparatorList = new ArrayList<>();
            private Object type;
            private ChangeType<T, E> changeType1 = t -> (E) t;
            private ChangeType<T, E> changeType2 = t -> (E) t;

            //  ------------------------- Sort ------------------------->
            public List<T> sort() {
                return sort_private(list);
            }

            public List<T> sort_new() {
                return sort_private(new ArrayList<>(list));
            }

            private List<T> sort_private(List<T> list) {
                if (allReversed)
                    sorterList.forEach(sorter -> sorter.reversed = true);

                list.sort((o1, o2) -> {
                    int result = 0;

                    E newO1 = changeType1.runChangeType(o1);
                    E newO2 = changeType2.runChangeType(o2);

                    for (Comparator<E> comparator : comparatorList) {
                        if (newO1 == null && newO2 == null && o1 instanceof ParentClass && o2 instanceof ParentClass)
                            result = ((ParentClass) o1).getName().compareTo(((ParentClass) o2).getName());
                        else if (newO1 == null)
                            result = reversed ^ !nullToBottom ? -1 : 1;
                        else if (newO2 == null)
                            result = reversed ^ !nullToBottom ? 1 : -1;
                        else
                            result = comparator.compare(newO1, newO2);

                        if (result != 0)
                            break;
                    }

                    if (result == 0) {
                        if (newO1 == null && newO2 == null && o1 instanceof ParentClass && o2 instanceof ParentClass)
                            result = ((ParentClass) o1).getName().compareTo(((ParentClass) o2).getName());
                        else if (newO1 == null)
                            result = reversed ^ !nullToBottom ? -1 : 1;
                        else if (newO2 == null)
                            result = reversed ^ !nullToBottom ? 1 : -1;
                    }

                    if (result == 0 && newO1 instanceof Comparable && newO2 instanceof Comparable) {
                        result = ((Comparable<E>) newO1).compareTo(newO2) * (reversed ^ reverseDefaultComparable ? -1 : 1);
                    }
                    if (result == 0 && newO1 instanceof ParentClass && newO2 instanceof ParentClass) {
                        result = ((ParentClass) newO1).getName().compareTo(((ParentClass) newO2).getName()) * (reversed ^ reverseParentClass ? -1 : 1);
                    }
                    if (result == 0 && o1 instanceof Comparable && o2 instanceof Comparable) {
                        result = ((Comparable<T>) o1).compareTo(o2) * (reversed ^ reverseDefaultComparable ? -1 : 1);
                    }
                    if (result == 0 && o1 instanceof ParentClass && o2 instanceof ParentClass) {
                        result = ((ParentClass) o1).getName().compareTo(((ParentClass) o2).getName()) * (reversed ^ reverseParentClass ? -1 : 1);
                    }

                    return result;
                });

                return list;
            }
            //  <------------------------- Sort -------------------------

            public Sorter<E> addCondition(Comparator<E> comparator) {
                comparatorList.add(comparator);
                return this;
            }

            public Sorter<E> enableReversed() {
                this.reversed = true;
                return this;
            }

            public Sorter<E> enableReverseDefaultComparable() {
                this.reverseDefaultComparable = true;
                return this;
            }

            public Sorter<E> enableReverseParentClass() {
                this.reverseParentClass = true;
                return this;
            }

            public Sorter<E> disableNullToBottom() {
                this.nullToBottom = false;
                return this;
            }

            public Sorter<E> setType(Object type) {
                this.type = type;
                return this;
            }

            public Sorter<T> addSorter(Object type) {
                return parent.addSorter(type);
            }

            public Sorter<T> addSorter(Comparator<T> comparator) {
                return parent.addSorter(comparator);
            }

            public Sorter<T> addSorter(Object type, Comparator<T> comparator) {
                return parent.addSorter(type, comparator);
            }

            public SortHelper<T> finish() {
                return parent;
            }

            public List<T> finish_and_sort(Object type) {
                return parent.sort(type);
            }

            public <N> Sorter<N> changeType(ChangeType<T, N> changeType1, ChangeType<T, N> changeType2) {
                Sorter<N> nSorter = clone();
                nSorter.changeType1 = changeType1;
                nSorter.changeType2 = changeType2;
                return nSorter;
            }

            public <N> Sorter<N> changeType(ChangeType<T, N> changeType1) {
                Sorter<N> nSorter = clone();
                nSorter.changeType1 = changeType1;
                nSorter.changeType2 = changeType1;
                return nSorter;
            }

            @NonNull
            @Override
            protected Sorter clone() {
                try {
                    return (Sorter) super.clone();
                } catch (CloneNotSupportedException e) {
                    return this;
                }
            }
        }

        public interface ChangeType<T, N> {
            N runChangeType(T t);
        }

    }
    //  <------------------------- SortHelper -------------------------


    //  ------------------------- WebViewHelper ------------------------->
    public static class WebViewHelper {
        public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36";

        private String userAgent = USER_AGENT;
        private boolean debug;
        private boolean loadInvisibleDialog;
        private boolean destroyOnSuccess = true;
        private WebView webView;
        private com.finn.androidUtilities.CustomDialog customDialog;
        private Context context;
        private boolean mobileVersion;
        private String[] urls;
        private int openJs;
        private boolean dialogCanceled;
        private List<Pair<String, CustomUtility.GenericInterface<String>>> requestList = new ArrayList<>();
        private ExecuteBeforeJavaScript executeBeforeJavaScript;
        private boolean alreadyLoaded;
        private boolean isRedirected;
        private CustomUtility.GenericInterface<WebSettings> setSettings;
        private boolean showToasts = true;
        private int urlsIndex;
        private Runnable onAllComplete;
        private boolean destroyed;
        private boolean loadImages;

        //  ------------------------- Constructor ------------------------->
        public WebViewHelper(Context context, String... urls) {
            this.context = context;
            this.urls = urls;
        }

        public WebViewHelper(Context context, WebView webView, String... urls) {
            this.webView = webView;
            this.context = context;
            this.urls = urls;
        }
        //  <------------------------- Constructor -------------------------


        private void buildWebView() {
            if (showToasts)
                Toast.makeText(context, "Einen Moment bitte..", Toast.LENGTH_SHORT).show();

            webView = new WebView(context);
            if (!debug)
                webView.setAlpha(0f);
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            if (!mobileVersion) {
                settings.setUserAgentString(userAgent);
                settings.setUseWideViewPort(true);
                settings.setLoadWithOverviewMode(true);
            }
            settings.setSupportZoom(true);
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            settings.setLoadWithOverviewMode(true);
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            settings.setDomStorageEnabled(true);

            if (!loadImages) {
//                settings.setBlockNetworkLoads(true);
                settings.setLoadsImagesAutomatically(false);
                settings.setBlockNetworkImage(true);
            }

            CustomUtility.runGenericInterface(setSettings, settings);

            webView.setWebViewClient(new WebViewClient() {
//                @Override
//                public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                    isRedirekted = false;
////                    super.onPageStarted(view, url, favicon);
//                }
//
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                    isRedirekted = true;
//                    view.loadUrl(request.getUrl().toString());
//                    return true;
////                    return super.shouldOverrideUrlLoading(view, request);
//                }

                @Override
                public void onPageFinished(WebView view, String url) { // ToDo: https://stackoverflow.com/questions/18282892/android-webview-onpagefinished-called-twice
                    if (!dialogCanceled /*&& !isRedirekted && !alreadyLoaded*/ && view.getProgress() == 100) { // && openJs != 0 && ) {
//                        alreadyLoaded = true;
                        if (executeBeforeJavaScript == null)
                            onPageLoaded();
                        else
                            executeBeforeJavaScript();
                    }
//                    super.onPageFinished(view, url);
                }
            });

            loadNextPage();

            if (debug || loadInvisibleDialog)
                buildDialog();
        }

        private void loadNextPage() {
            if (destroyed) return;
            openJs = requestList.size();
            alreadyLoaded = false;
            webView.loadUrl(urls[urlsIndex]);
            urlsIndex++;
        }

        public WebViewHelper addRequest(String javaScript, CustomUtility.GenericInterface<String> onParseResult) {
            requestList.add(Pair.create(javaScript, onParseResult));
            return this;
        }

        public WebViewHelper addCommand(String javaScript) {
            requestList.add(Pair.create(javaScript, null));
            return this;
        }

        private void onPageLoaded() {
            Iterator<Pair<String, CustomUtility.GenericInterface<String>>> iterator = requestList.iterator();
            new Runnable() {
                @Override
                public void run() {
                    if (destroyed) return;
                    if (iterator.hasNext()) {
                        Pair<String, CustomUtility.GenericInterface<String>> pair = iterator.next();
                        WebViewHelper.this.evaluateJavaScript(pair.first, pair.second, this, 0);
                    }
                }
            }.run();

//            for (Pair<String, CustomUtility.GenericInterface<String>> pair : requestList) {
//            }
        }

        private void evaluateJavaScript(String rawScript, CustomUtility.GenericInterface<String> onParseResult, Runnable onComplete, int tryCount) {
            if (destroyed) return;
            String script = rawScript;
            if (script.startsWith("{") && script.endsWith("}")) {
                script = "(function() " + script + ")();";

            } else {
                if (!script.endsWith(";"))
                    script += ";";
            }

            Runnable onSuccess = () -> {
                if (openJs <= 1) {
                    openJs--;
                    if (urlsIndex < urls.length) {
                        loadNextPage();
                    } else {
                        if (onAllComplete != null)
                            onAllComplete.run();
                        if (!debug && destroyOnSuccess) {
                            webView.destroy();
                            if (customDialog != null) {
                                customDialog.dismiss();
                                customDialog = null;
                            }
                        }
                    }
                } else {
                    openJs--;
                    onComplete.run();
                }
            };


            webView.evaluateJavascript(script, t -> {
                if (destroyed) return;
                if (onParseResult != null) {
                    if (t.startsWith("\"") && t.endsWith("\""))
                        t = CustomUtility.subString(t, 1, -1);

                    if (t.matches("null") && tryCount < 50) {
                        new Handler().postDelayed(() -> evaluateJavaScript(rawScript, onParseResult, onComplete, tryCount + 1), 100);
                    } else {
                        onParseResult.run(t + (tryCount < 50 ? "" : " (" + tryCount + ")"));
                        onSuccess.run();
                    }
                } else
                    onSuccess.run();
            });

        }

        private void buildDialog() {
            customDialog = com.finn.androidUtilities.CustomDialog.Builder(context)
                    .setView(webView)
                    .addOptionalModifications(customDialog1 -> {
                        if (debug) {
                            customDialog1
                                    .setDimensions(true, true);
                        } else {
                            customDialog1
                                    .setDimensions(true, true)
                                    .removeBackground_and_margin()
                                    .enableDoubleClickOutsideToDismiss(customDialog -> true, "Daten werden geladen");
                        }
                    })
                    .setOnDialogDismiss(customDialog -> {
                        dialogCanceled = true;
                        ((ViewGroup) customDialog.findViewById(R.id.dialog_custom_layout_view_interface)).removeAllViews();
                    })
                    .disableScroll()
                    .show();

            Toast toast = Toast.makeText(context, "Daten werden geladen", Toast.LENGTH_SHORT);
            com.finn.androidUtilities.Helpers.DoubleClickHelper doubleClickHelper = com.finn.androidUtilities.Helpers.DoubleClickHelper.create()
                    .setOnFailed(toast::show)
                    .setOnSuccess(() -> {
                        toast.cancel();
                        Toast.makeText(context, "Abgebrochen", Toast.LENGTH_SHORT).show();
                    });
            webView.setOnTouchListener((v, event) -> {
                if (!debug && event.getAction() == MotionEvent.ACTION_UP) {
                    if (doubleClickHelper.check()) {
                        customDialog.dismiss();
                        dialogCanceled = true;
                    }
                    return true;
                } else
                    return false;
            });
        }

        public WebViewHelper go() {
            dialogCanceled = false;
            if (webView == null)
                buildWebView();
            else {
                if (debug || loadInvisibleDialog)
                    buildDialog();

                if (executeBeforeJavaScript == null)
                    onPageLoaded();
                else
                    executeBeforeJavaScript();
            }
            return this;
        }

        public void destroy() {
            destroyed = true;
            if (!debug) {
                webView.destroy();
                if (customDialog != null) {
                    customDialog.dismiss();
                    customDialog = null;
                }
            }
        }


        //  ------------------------- ExecuteBeforeJavaScript ------------------------->
        public interface ExecuteBeforeJavaScript {
            void runExecuteBeforeJavaScript(com.finn.androidUtilities.CustomDialog internetDialog, WebView webView, Runnable resume);
        }

        private void executeBeforeJavaScript() {
            executeBeforeJavaScript.runExecuteBeforeJavaScript(customDialog, webView, this::onPageLoaded);
        }
        //  <------------------------- ExecuteBeforeJavaScript -------------------------


        //  ------------------------- Getter & Setter ------------------------->
        public String getUserAgent() {
            return userAgent;
        }

        public WebViewHelper setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public boolean isDebug() {
            return debug;
        }

        public WebViewHelper setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public boolean isLoadInvisibleDialog() {
            return loadInvisibleDialog;
        }

        public WebViewHelper setLoadInvisibleDialog(boolean loadInvisibleDialog) {
            this.loadInvisibleDialog = loadInvisibleDialog;
            return this;
        }

        public boolean isDestroyOnSuccess() {
            return destroyOnSuccess;
        }

        public WebViewHelper setDestroyOnSuccess(boolean destroyOnSuccess) {
            this.destroyOnSuccess = destroyOnSuccess;
            return this;
        }

        public WebView getWebView() {
            return webView;
        }

        public WebViewHelper setWebView(WebView webView) {
            this.webView = webView;
            return this;
        }

        public com.finn.androidUtilities.CustomDialog getCustomDialog() {
            return customDialog;
        }

        public WebViewHelper setCustomDialog(com.finn.androidUtilities.CustomDialog customDialog) {
            this.customDialog = customDialog;
            return this;
        }

        public Context getContext() {
            return context;
        }

        public WebViewHelper setContext(Context context) {
            this.context = context;
            return this;
        }

        public boolean isMobileVersion() {
            return mobileVersion;
        }

        public WebViewHelper setMobileVersion(boolean mobileVersion) {
            this.mobileVersion = mobileVersion;
            return this;
        }

        public String[] getUrls() {
            return urls;
        }

        public WebViewHelper setUrls(String... urls) {
            this.urls = urls;
            return this;
        }

        public WebViewHelper setExecuteBeforeJavaScript(ExecuteBeforeJavaScript executeBeforeJavaScript) {
            this.executeBeforeJavaScript = executeBeforeJavaScript;
            return this;
        }

        public WebViewHelper setSetSettings(CustomUtility.GenericInterface<WebSettings> setSettings) {
            this.setSettings = setSettings;
            return this;
        }

        public Runnable getOnAllComplete() {
            return onAllComplete;
        }

        public WebViewHelper setOnAllComplete(Runnable onAllComplete) {
            this.onAllComplete = onAllComplete;
            return this;
        }

        public WebViewHelper enableLoadImages() {
            this.loadImages = true;
            return this;
        }
        //  <------------------------- Getter & Setter -------------------------


        //  ------------------------- Convenience ------------------------->
        public WebViewHelper addOptional(CustomUtility.GenericInterface<WebViewHelper> addOptional) {
            addOptional.run(this);
            return this;
        }
        //  <------------------------- Convenience -------------------------
    }
    //  <------------------------- WebViewHelper -------------------------

}
