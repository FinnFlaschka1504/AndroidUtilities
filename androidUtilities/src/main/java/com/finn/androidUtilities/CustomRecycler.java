package com.finn.androidUtilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import kotlin.jvm.JvmStatic;

import static java.util.stream.Collectors.toList;

public class CustomRecycler<T> {

    // ToDo: ItemTouchHelper https://www.youtube.com/watch?v=dvDTmJtGE_I
    //  holder.layoutId.setTag

    public enum ORIENTATION {
        VERTICAL, HORIZONTAL
    }

    public enum SELECTION_MODE {
        SINGLE_SELECTION, MULTI_SELECTION
    }

    private int rowOrColumnCount = 1;
    private long multipleClickDelay = 300;
    private long lastClickTime;
    private boolean isMultiClickEnabled = false;
    private boolean showDivider = false;
    private boolean hideLastDivider;
    private boolean useCustomRipple = true;
    private AppCompatActivity context;
    private RecyclerView recycler;
    private int layoutId = -1;
    private SetItemContent<T> setItemContent;
    private List<T> objectList = new ArrayList<>();
    private int orientation = RecyclerView.VERTICAL;
    private OnLongClickListener<T> onLongClickListener;
    private View.OnLongClickListener longClickListener = view -> {
        if ((lastClickTime > System.currentTimeMillis() - multipleClickDelay) && !isMultiClickEnabled)
            return false;
        lastClickTime = System.currentTimeMillis();
        int index = recycler.getChildAdapterPosition(view);
        onLongClickListener.runOnLongClickListener(this, view, objectList.get(index), index);
        return true;
    };
    private OnClickListener<T> onClickListener;
    private View.OnClickListener clickListener = view -> {
        if ((lastClickTime > System.currentTimeMillis() - multipleClickDelay) && !isMultiClickEnabled)
            return;
        lastClickTime = System.currentTimeMillis();
        int index = recycler.getChildAdapterPosition(view);
        onClickListener.runOnClickListener(this, view, objectList.get(index), index);
    };
    private Map<Integer, Pair<OnClickListener<T>, Boolean>> idSubOnClickListenerMap = new HashMap<>();
    private Map<Integer, Pair<OnClickListener<T>, Boolean>> idSubOnLongClickListenerMap = new HashMap<Integer, Pair<OnClickListener<T>, Boolean>>();
    private SELECTION_MODE selectionMode = SELECTION_MODE.SINGLE_SELECTION;
    private boolean useActiveObjectList;
    private GetActiveObjectList<T> getActiveObjectList;
    private boolean hideOverscroll;
    private MyAdapter mAdapter;
    private OnDragAndDrop<T> onDragAndDrop;
    private int dividerMargin;
    private OnSwiped<T> onSwiped;
    private Pair<Boolean, Boolean> leftRightSwipe_pair;
    private ExpandableHelper expandableHelper;


    public CustomRecycler(AppCompatActivity context) {
        this.context = context;
    }

    public CustomRecycler(AppCompatActivity context, RecyclerView recycler) {
        this.context = context;
        this.recycler = recycler;
    }


    public CustomRecycler setRecycler(RecyclerView recycler) {
        this.recycler = recycler;
        return this;
    }

    public RecyclerView getRecycler() {
        return recycler;
    }

    public CustomRecycler<T> setItemLayout(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public CustomRecycler<T> setObjectList(Collection<T> objectCollection) {
        if (objectCollection instanceof List)
            this.objectList = (List<T>) objectCollection;
        else
            this.objectList = new ArrayList<>(objectCollection);
        return this;
    }

    public List<T> getObjectList() {
        return objectList;
    }

    public interface GetActiveObjectList<T> {
        List<T> runGetActiveObjectList();
    }

    public CustomRecycler<T> setGetActiveObjectList(GetActiveObjectList<T> getActiveObjectList) {
        this.getActiveObjectList = getActiveObjectList;
        objectList.addAll(getActiveObjectList.runGetActiveObjectList());
        useActiveObjectList = true;
        return this;
    }

    public CustomRecycler<T> setOrientation(ORIENTATION orientation) {
        switch (orientation) {
            case VERTICAL:
                this.orientation = RecyclerView.VERTICAL;
                break;
            case HORIZONTAL:
                this.orientation = RecyclerView.HORIZONTAL;
                break;
        }
        return this;
    }

    public CustomRecycler<T> disableCustomRipple() {
        this.useCustomRipple = false;
        return this;
    }

    public CustomRecycler<T> enableDivider() {
        this.showDivider = true;
        return this;
    }

    public CustomRecycler<T> removeLastDivider() {
        this.hideLastDivider = true;
        return this;
    }

    public CustomRecycler<T> setSelectionMode(SELECTION_MODE selectionMode) {
        this.selectionMode = selectionMode;
        return this;
    }

    public CustomRecycler<T> setMultipleClickDelay(long multipleClickDelay) {
        this.multipleClickDelay = multipleClickDelay;
        return this;
    }

    public CustomRecycler<T> setMultiClickEnabled(boolean multiClickEnabled) {
        isMultiClickEnabled = multiClickEnabled;
        return this;
    }

    public CustomRecycler<T> addSubOnClickListener(int viewId, OnClickListener<T> onClickListener, boolean ripple) {
        idSubOnClickListenerMap.put(viewId, new Pair<>(onClickListener, ripple));
        return this;
    }

    public CustomRecycler<T> addSubOnClickListener(int viewId, OnClickListener<T> onClickListener) {
        idSubOnClickListenerMap.put(viewId, new Pair<>(onClickListener, false));
        return this;
    }

    public CustomRecycler<T> addSubOnLongClickListener(int viewId, OnClickListener<T> onClickListener, boolean ripple) {
        idSubOnLongClickListenerMap.put(viewId, new Pair<>(onClickListener, ripple));
        return this;
    }

    public CustomRecycler<T> addSubOnLongClickListener(int viewId, OnClickListener<T> onClickListener) {
        idSubOnLongClickListenerMap.put(viewId, new Pair<>(onClickListener, false));
        return this;
    }

    public CustomRecycler<T> setRowOrColumnCount(int rowOrColumnCount) {
        this.rowOrColumnCount = rowOrColumnCount;
        return this;
    }

    public interface SetItemContent<E> {
        void runSetCellContent(View itemView, E e);
    }

    public CustomRecycler<T> setSetItemContent(SetItemContent<T> setItemContent) {
        this.setItemContent = setItemContent;
        return this;
    }

    public interface OnClickListener<T> {
        void runOnClickListener(CustomRecycler<T> customRecycler, View itemView, T t, int index);
    }

    public CustomRecycler<T> setOnClickListener(OnClickListener<T> onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public interface OnLongClickListener<T> {
        void runOnLongClickListener(CustomRecycler<T> customRecycler, View view, T t, int index);
    }

    public CustomRecycler<T> setOnLongClickListener(OnLongClickListener<T> onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
        return this;
    }

    public CustomRecycler<T> hideOverscroll() {
        hideOverscroll = true;
        return this;
    }

    public CustomRecycler<T> enableDragAndDrop(OnDragAndDrop<T> onDragAndDrop) {
        this.onDragAndDrop = onDragAndDrop;
        return this;
    }

    public CustomRecycler<T> enableSwiping(OnSwiped<T> onSwiped, boolean start, boolean end) {
        this.onSwiped = onSwiped;
        this.leftRightSwipe_pair = new Pair<>(start, end);
        return this;
    }

    private void applyTouchActions() {
        int dragFlags = 0;
        int swipeFlags = 0;
        if (onDragAndDrop != null)
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        if (onSwiped != null) {
            if (leftRightSwipe_pair.first)
                swipeFlags += ItemTouchHelper.START;
            if (leftRightSwipe_pair.second)
                swipeFlags += ItemTouchHelper.END;
        }
        ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(dragFlags, swipeFlags) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {

                int posDragged = dragged.getAdapterPosition();
                int posTarget = target.getAdapterPosition();

                Collections.swap(objectList, posDragged, posTarget);
                mAdapter.notifyItemMoved(posDragged, posTarget);

                onDragAndDrop.runOnDragAndDrop(objectList);

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int index = viewHolder.getAdapterPosition();

                T t = objectList.remove(index);
                mAdapter.notifyDataSetChanged();
                onSwiped.runSwyped(objectList, direction, t);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View viewItem = viewHolder.itemView;
                    new SwipeBackgroundHelper().paintDrawCommandToStart(c, viewItem, R.drawable.ic_delete_black_24dp, dX);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            class SwipeBackgroundHelper {
                private static final String TAG = "SwipeBackgroundHelper";
                private static final float CIRCLE_ACCELERATION = 0.5f;
                private int OFFSET_PX = CustomUtility.dpToPx(16);
                private double THRESHOLD = 0.5;
                private Paint circlePaint;
                private float CIRCLE_OFFSET_PX = CustomUtility.dpToPx(16);

                {
                    circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    circlePaint.setColor(Color.RED);
                }

//                private void drawBackground(Canvas canvas, View viewItem, Float dX, int color) {
//                    Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//                    backgroundPaint.setColor(color);
//                    RectF backgroundRectangle = getBackGroundRectangle(viewItem, dX);
//                    canvas.drawRect(backgroundRectangle, backgroundPaint);
//                }

                private void drawBackground(Canvas canvas, View viewItem, Float dX, Float threshold, DrawCommand drawCommand) {
                    Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    backgroundPaint.setColor(drawCommand.backgroundColor);
                    RectF backgroundRectangle = getBackGroundRectangle(viewItem, dX);
                    float circleRadius = (Math.abs(dX / viewItem.getWidth()) - threshold) * viewItem.getWidth() * CIRCLE_ACCELERATION;

                    canvas.clipRect(backgroundRectangle);
                    canvas.drawColor(backgroundPaint.getColor());
                    if (circleRadius > 0f) {
                        float cx = getIntPosition(backgroundRectangle);//backgroundRectangle.left + drawCommand.icon.getIntrinsicWidth() / 2f + CIRCLE_OFFSET_PX;
                        float cy = backgroundRectangle.top + viewItem.getHeight() / 2f;
                        Log.d(TAG, "drawBackground: cx" + cx);

                        canvas.drawCircle(cx, cy, circleRadius, circlePaint);
                    }
                }

                private int getIntPosition(RectF backgroundRectangle) {
                    float v = (backgroundRectangle.right + backgroundRectangle.left) / 2;
                    if (v <  backgroundRectangle.left + OFFSET_PX)
                        return (int) (backgroundRectangle.left + OFFSET_PX);
                    else
                        return (int) v;
                }

                private RectF getBackGroundRectangle(View viewItem, Float dX) {
                    return new RectF(viewItem.getRight() + dX, viewItem.getTop(), viewItem.getRight(), viewItem.getBottom());
                }

                private int calculateTopMargin(Drawable icon, View viewItem) {
                    return (viewItem.getHeight() - icon.getIntrinsicHeight()) / 2;
                }

                private Rect getStartContainerRectangle(View viewItem, int iconWidth, int topMargin, int sideOffset, Float dx) {
                    int center = (viewItem.getRight() * 2 + dx.intValue()) / 2 ;
                    if (center < viewItem.getRight() + dx.intValue() + sideOffset)
                        center = viewItem.getRight() + dx.intValue() + sideOffset;
                    center -= iconWidth / 2;
                    Log.d(TAG, "getStartContainerRectangle: center" + center);
//                    int leftBound = viewItem.getRight() + (dx.intValue() + sideOffset) / 2;
                    int leftBound = center;
                    int rightBound = center + iconWidth;
                    int topBound = viewItem.getTop() + topMargin;
                    int bottomBound = viewItem.getBottom() - topMargin;

                    return new Rect(leftBound, topBound, rightBound, bottomBound);
                }

                private void drawIcon(Canvas canvas, View viewItem, Float dX, Drawable icon) {
                    int topMargin = calculateTopMargin(icon, viewItem);
                    icon.setBounds(getStartContainerRectangle(viewItem, icon.getIntrinsicWidth(), topMargin, OFFSET_PX, dX));
                    icon.draw(canvas);
                }

                private void paintDrawCommand(DrawCommand drawCommand, Canvas canvas, Float dX, View viewItem) {
                    drawBackground(canvas, viewItem, dX, 0f, drawCommand);
                    drawIcon(canvas, viewItem, dX, drawCommand.icon);
                }

                @JvmStatic
                void paintDrawCommandToStart(Canvas canvas, View viewItem, @DrawableRes int iconResId, Float dX) {
                    DrawCommand drawCommand = createDrawCommand(viewItem, dX, iconResId);
                    paintDrawCommand(drawCommand, canvas, dX, viewItem);
                }

                private DrawCommand createDrawCommand(View viewItem, Float dX, int iconResId) {
                    Context context = viewItem.getContext();
                    Drawable icon = ContextCompat.getDrawable(context, iconResId);
                    icon = DrawableCompat.wrap(icon).mutate();
                    icon.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorDrawable), PorterDuff.Mode.SRC_IN));
                    int backgroundColor = getBackgroundColor(R.color.colorTransparent, R.color.colorTransparent, dX, viewItem);
                    return new DrawCommand(icon, backgroundColor);
                }

                private int getBackgroundColor(@ColorRes int firstColor, @ColorRes int secondColor, float dX, View viewItem) {
                    if (willActionBeTriggered(dX, viewItem.getWidth()))
                        return ContextCompat.getColor(viewItem.getContext(), firstColor);
                    else
                        return ContextCompat.getColor(viewItem.getContext(), secondColor);
                }

                private boolean willActionBeTriggered(float dX, int viewWidth) {
                    return Math.abs(dX) >= viewWidth * THRESHOLD;
                }

                class DrawCommand {
                    Drawable icon;
                    int backgroundColor;

                    public DrawCommand(Drawable icon, int backgroundColor) {
                        this.icon = icon;
                        this.backgroundColor = backgroundColor;
                    }


                }
            }

        };
        ItemTouchHelper helper = new ItemTouchHelper(itemTouchHelperCallback);
        helper.attachToRecyclerView(recycler);
    }

    public interface OnDragAndDrop<T> {
        void runOnDragAndDrop(List<T> objectList);
    }

    public interface OnSwiped<T> {
        void runSwyped(List<T> objectList, int direction, T t);
    }

    public CustomRecycler<T> setDividerMargin_inDp(int dividerMargin_inDp) {
        this.dividerMargin = CustomUtility.dpToPx(dividerMargin_inDp);
        return this;
    }


    //  ----- Adapter ----->
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<T> dataSet;

        public MyAdapter(List<T> list) {
            dataSet = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (layoutId == -1)
                layoutId = R.layout.list_item_standard;

            View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            v.setId(View.generateViewId());

            if (!idSubOnClickListenerMap.isEmpty()) {
                for (Map.Entry<Integer, Pair<OnClickListener<T>, Boolean>> entry : idSubOnClickListenerMap.entrySet()) {
                    View view = v.findViewById(entry.getKey());
                    view.setOnClickListener(view2 -> {
                        int index = recycler.getChildAdapterPosition(v);
                        entry.getValue().first.runOnClickListener(CustomRecycler.this, v, dataSet.get(index), index);
                        view.setFocusable(true);
                        view.setClickable(true);
                        if (entry.getValue().second) {
                            TypedValue outValue = new TypedValue();
                            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                            view.setBackgroundResource(outValue.resourceId);
                        }
                    });
                }
            }

            if (!idSubOnLongClickListenerMap.isEmpty()) {
                for (Map.Entry<Integer, Pair<OnClickListener<T>, Boolean>> entry : idSubOnLongClickListenerMap.entrySet()) {
                    View view = v.findViewById(entry.getKey());
                    view.setOnLongClickListener(view2 -> {
                        int index = recycler.getChildAdapterPosition(v);
                        entry.getValue().first.runOnClickListener(CustomRecycler.this, v, dataSet.get(index), index);
                        view.setFocusable(true);
                        view.setClickable(true);
                        if (entry.getValue().second) {
                            TypedValue outValue = new TypedValue();
                            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                            view.setBackgroundResource(outValue.resourceId);
                        }
                        return true;
                    });
                }
            }

            if (selectionMode != SELECTION_MODE.MULTI_SELECTION) {
                if (onClickListener != null || onLongClickListener != null) {

                    if (onClickListener != null)
                        v.setOnClickListener(clickListener);

                    if (onLongClickListener != null)
                        v.setOnLongClickListener(longClickListener);

                    if (!useCustomRipple) {
                        v.setFocusable(true);
                        v.setClickable(true);
                        TypedValue outValue = new TypedValue();
                        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                        v.setBackgroundResource(outValue.resourceId);
                    }
                }
            }

            ViewHolder viewHolder = new ViewHolder(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            if (setItemContent == null) {
                setItemContent = (itemView, t) -> {
                    if (t instanceof CharSequence)
                        ((TextView) itemView.findViewById(R.id.listItem_standard_title)).setText((CharSequence) t);
                    else if (t instanceof Pair) {
                        Pair<String, String> pair = (Pair<String, String>) t;
                        ((TextView) itemView.findViewById(R.id.listItem_standard_title)).setText(pair.first);
                        TextView listItem_standard_subTitle = itemView.findViewById(R.id.listItem_standard_subTitle);
                        listItem_standard_subTitle.setText(pair.second);
                        listItem_standard_subTitle.setVisibility(View.VISIBLE);

                    }
                };
            }
            setItemContent.runSetCellContent(viewHolder.itemView, dataSet.get(position));
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        public void removeItemAt(int index) {
            if (dataSet.isEmpty())
                return;
            dataSet.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, dataSet.size());
        }

        public List<? extends T> getDataSet() {
            return dataSet;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View v) {
                super(v);
            }
        }

    }
    //  <----- Adapter -----


    //  --------------- Expandable --------------->
    private CustomRecycler.OnClickListener<CustomRecycler.Expandable<T>> expandableOnClickListener = (customRecycler1, itemView, expandable, index) -> {
        if (!expandable.canExpand())
            return;
        View expansion = itemView.findViewById(R.id.listItem_expandable_expansionLayout);
        boolean expanded = expandable.isExpended();

        ImageView imageView = itemView.findViewById(expandableHelper.getArrowId());
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AnimatedVectorDrawable) {
                AnimatedVectorDrawable compat = (AnimatedVectorDrawable) drawable;
                compat.start();
                compat.registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        imageView.setImageDrawable(context.getDrawable(expanded ? R.drawable.ic_arrow_down_to_up : R.drawable.ic_arrow_up_to_down));
                    }
                });
            }
        }

        if (expanded) {
            CustomUtility.collapse(expansion);
        } else {
            CustomUtility.expand(expansion);
        }

        expandable.setExpended(!expanded);
    };
    private CustomRecycler.OnClickListener<CustomRecycler.Expandable<T>> expandableOnClickListener_change = (customRecycler1, itemView, expandable, index) -> {
        if (!expandable.canExpand())
            return;
        boolean expanded = expandable.isExpended();
        CustomUtility.changeHeight(itemView, view -> expandableHelper.setExpandableItemContent.runSetExpandableItemContent(view, expandable.getObject(), !expandable.isExpended()));
        expandable.setExpended(!expanded);
    };

    private Map<CustomRecycler, Expandable> subRecyclerMap = new HashMap<>();

    public Expandable getExpandable(CustomRecycler customRecycler) {
        return subRecyclerMap.get(customRecycler);
    }

    public CustomRecycler<T> setExpandableHelper(GetExpandableHelper<T> getExpandableHelper) {
        this.expandableHelper = (ExpandableHelper) getExpandableHelper.runGetExpandableHelper(this);
        return this;
    }

    public interface GetExpandableHelper<T> {
        Object runGetExpandableHelper(CustomRecycler<T> customRecycler);
    }

    public interface SetExpandableItemContent<E> {
        void runSetExpandableItemContent(View itemView, E e, boolean expanded);
    }

    public class ExpandableHelper<E> {
        private boolean expandByDefault;
        private List<Expandable<E>> expandableList = new ArrayList<>();
        private boolean showArrow_default = true;
        private int arrowId = R.id.listItem_expandable_arrow;
        private int contentLayoutId;
        private SetItemContent<E> setItemContent;
        private SetExpandableItemContent<E> setExpandableItemContent;
        private CustomizeRecycler<E> customizeRecycler;

        public ExpandableHelper() {
            setItemContent = (itemView, e) -> {
            };
        }

        public ExpandableHelper(int contentLayoutId, SetItemContent<E> setItemContent) {
            this.contentLayoutId = contentLayoutId;
            this.setItemContent = setItemContent;
        }

        public ExpandableHelper(int contentLayoutId, SetExpandableItemContent<E> setExpandableItemContent) {
            this.contentLayoutId = contentLayoutId;
            this.setExpandableItemContent = setExpandableItemContent;
        }

        public int getContentLayoutId() {
            return contentLayoutId;
        }

        public List<Expandable<E>> getExpandableList() {
            return expandableList;
        }

        public ExpandableHelper<E> setExpandableList(List<Expandable<E>> expandableList) {
            this.expandableList = expandableList;
            return this;
        }

        public int getArrowId() {
            return arrowId;
        }

        public ExpandableHelper<E> setArrowId(int arrowId) {
            this.arrowId = arrowId;
            return this;
        }


        public CustomRecycler<E> generateRecycler(AppCompatActivity context, Expandable<List<E>> expandable) {
            CustomRecycler<E> customRecycler = new CustomRecycler<E>(context).setObjectList((Collection<E>) expandable.getList());
            if (customizeRecycler != null)
                customizeRecycler.runCustomizeRecycler(customRecycler);
            expandable.customRecycler = (CustomRecycler<List<E>>) customRecycler;
            return customRecycler.generate();
        }

        public ExpandableHelper<E> customizeRecycler(CustomizeRecycler<E> customRecycler) {
            this.customizeRecycler = customRecycler;
            return this;
        }

        public ExpandableHelper<E> disableArrows() {
            showArrow_default = false;
            return this;
        }

        public ExpandableHelper<E> enableExpandByDefault() {
            this.expandByDefault = true;
            return this;
        }
    }

    public interface CustomizeRecycler<T> {
        void runCustomizeRecycler(CustomRecycler<T> subRecycler);
    }

    // ToDo:
    //  animationslänge (evl. auch komplett deaktivieren)
    public static class Expandable<T> {
        public Expandable() {
        }

        public Expandable(String name) {
            this.name = name;
        }

        public Expandable(String name, T object) {
            this.name = name;

            if (object instanceof List)
                this.list = (List<T>) object;
            else
                this.object = object;
        }

        private String name;
        private boolean expended;
        private List<T> list = new ArrayList<>();
        private T object;
        private CustomRecycler<T> customRecycler;
        private boolean showArrow = true;
        private Object payload;

        public String getName() {
            return name;
        }

        public T getObject() {
            return object;
        }

        public List<T> getList() {
            return list;
        }

        public boolean isExpended() {
            return expended;
        }

        public Expandable setExpended(boolean expended) {
            this.expended = expended;
            return this;
        }

        public CustomRecycler<T> getCustomRecycler() {
            return customRecycler;
        }

        public Object getPayload() {
            return payload;
        }

        public Expandable<T> setPayload(Object payload) {
            this.payload = payload;
            return this;
        }

        //  --------------- Convenience --------------->
        public boolean canExpand() {
            return object != null || !list.isEmpty();
        }
        //  <--------------- Convenience ---------------

        //  --------------- toExpandable --------------->
        public List<Expandable<T>> toExpandableList(List<T> list) {
            return list.stream().map(t -> new CustomRecycler.Expandable<>(t.toString(), t)).collect(toList());
        }

        public List<Expandable<T>> toExpandableList(List<T> list, ToExpandable<T> toExpandable) {
            return list.stream().map(toExpandable::runToExpandable).collect(toList());
        }

        public interface ToExpandable<T> {
            Expandable<T> runToExpandable(T t);
        }

        public static class ToGroupExpandableList<Result, Item, Key> {
            Comparator<Expandable<List<Result>>> keyComparator;

            public List<Expandable<List<Result>>> runToGroupExpandableList(List<Item> list, Function<Item, Key> classifier
                    , KeyToString<Key, Item> keyToString, ItemToResult<Item, Result> itemToResult) {
                Map<Key, List<Item>> group = list.stream().collect(Collectors.groupingBy(classifier));

                List<Expandable<List<Result>>> expandableList = new ArrayList<>();
                for (Map.Entry<Key, List<Item>> entry : group.entrySet()) {
                    expandableList.add(new Expandable<>(keyToString.runKeyToString(entry.getKey(), entry.getValue()), entry.getValue().stream().map(itemToResult::runItemToResult).collect(Collectors.toList()))
                            .setPayload(entry.getKey()));
                }

                if (keyComparator != null)
                    expandableList.sort(keyComparator);

                return expandableList;
            }

            public ToGroupExpandableList<Result, Item, Key> setSort(Comparator<Expandable<List<Result>>> keyComparator) {
                this.keyComparator = keyComparator;
                return this;
            }
        }

        public interface KeyToString<T, M> {
            String runKeyToString(T t, List<M> m);
        }

        public interface ItemToResult<Item, Result> {
            Result runItemToResult(Item item);
        }
        //  <--------------- toExpandable ---------------
    }
    //  <--------------- Expandable ---------------


    //  ----- Generate ----->
    public Pair<CustomRecycler, RecyclerView> generatePair() {
        RecyclerView recyclerView = generateRecyclerView();
        return new Pair<>(this, recyclerView);
    }

    public CustomRecycler<T> generate() {
        this.recycler = generateRecyclerView();
        return this;
    }

    public RecyclerView generateRecyclerView() {
        if (this.recycler == null)
            recycler = new RecyclerView(context);

        RecyclerView.LayoutManager layoutManager;
        if (rowOrColumnCount > 1)
            layoutManager = new GridLayoutManager(context, rowOrColumnCount, orientation, false);
        else
            layoutManager = new LinearLayoutManager(context, orientation, false);
        recycler.setLayoutManager(layoutManager);


        if (expandableHelper != null) {
            expandableHelper.setExpandableList(objectList);
            for (Object o : expandableHelper.getExpandableList()) {
                Expandable expandable = (Expandable) o;
                if (!expandableHelper.showArrow_default)
                    expandable.showArrow = false;
                else
                    expandable.showArrow = expandable.canExpand();

                if (expandableHelper.expandByDefault && expandable.canExpand())
                    expandable.setExpended(true);
            }
            if (expandableHelper.setItemContent != null) {
                layoutId = R.layout.list_item_expandable;
                setSetItemContent((itemView, t) -> {
                    Expandable<T> expandable = (Expandable<T>) t;
                    ((TextView) itemView.findViewById(R.id.listItem_expandable_name)).setText(expandable.getName());
                    itemView.findViewById(R.id.listItem_expandable_arrow).setVisibility(expandable.showArrow ? View.VISIBLE : View.GONE);
                    if (expandable.canExpand()) {
                        View v;
                        if (expandable.getList().isEmpty()) {
                            v = context.getLayoutInflater().inflate(expandableHelper.getContentLayoutId(), null);
                        } else {
                            v = expandableHelper.generateRecycler(context, (Expandable<List>) t).getRecycler();
                            subRecyclerMap.put(((Expandable<List>) t).getCustomRecycler(), ((Expandable<List>) t));
                        }

                        FrameLayout listItem_expandable_content = itemView.findViewById(R.id.listItem_expandable_content);
                        listItem_expandable_content.removeAllViews();
                        listItem_expandable_content.addView(v);
                    }
                    if (expandableHelper.setItemContent != null && expandable.canExpand())
                        expandableHelper.setItemContent.runSetCellContent(itemView, expandable.getObject());

                    itemView.findViewById(R.id.listItem_expandable_expansionLayout).setVisibility(expandable.isExpended() ? View.VISIBLE : View.GONE);
                    ((ImageView) itemView.findViewById(expandableHelper.getArrowId()))
                            .setImageDrawable(context.getDrawable(expandable.isExpended() ? R.drawable.ic_arrow_up_to_down : R.drawable.ic_arrow_down_to_up));
                });
                addSubOnClickListener(R.id.listItem_expandable_header, (OnClickListener<T>) expandableOnClickListener);
//                onClickListener = (OnClickListener<T>) expandableOnClickListener;
            } else if (expandableHelper.setExpandableItemContent != null) {
                layoutId = expandableHelper.contentLayoutId;
                setSetItemContent((itemView, t) -> expandableHelper.setExpandableItemContent.runSetExpandableItemContent(itemView, ((Expandable) t).getObject(), ((Expandable) t).isExpended()));
                onClickListener = (OnClickListener<T>) expandableOnClickListener_change;
            }
        }


        mAdapter = new MyAdapter(objectList);
        recycler.setAdapter(mAdapter);

        if (showDivider) {
            Drawable mDivider = ContextCompat.getDrawable(context, R.drawable.divider);
            DividerItemDecoration dividerItemDecoration;
            if (hideLastDivider) {
                dividerItemDecoration = new DividerItemDecoration(context, orientation) {
                    @Override
                    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
                        int dividerLeft = parent.getPaddingLeft() + dividerMargin;
                        int dividerRight = parent.getWidth() - parent.getPaddingRight() - dividerMargin;

                        int childCount = parent.getChildCount();
                        for (int i = 0; i <= childCount - 2; i++) {
                            View child = parent.getChildAt(i);

                            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                            int dividerTop = child.getBottom() + params.bottomMargin;
                            int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();

                            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                            mDivider.draw(canvas);
                        }
                    }
                };
            } else {
                dividerItemDecoration = new DividerItemDecoration(recycler.getContext(),
                        ((LinearLayoutManager) layoutManager).getOrientation());
                dividerItemDecoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider));
            }
            recycler.addItemDecoration(dividerItemDecoration);
        }

        if (hideOverscroll)
            recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);

        if (onDragAndDrop != null || onSwiped != null)
            applyTouchActions();

        return recycler;
    }

    public CustomRecycler<T> reload() {
        if (useActiveObjectList) {
            objectList.clear();
            objectList.addAll(getActiveObjectList.runGetActiveObjectList());
        }
        mAdapter.notifyDataSetChanged();
        return this;
    }

    public CustomRecycler<T> reload(List<T> objectList) {
        this.objectList.clear();
        this.objectList.addAll(objectList);
        mAdapter.notifyDataSetChanged();
        return this;
    }

    public RecyclerView update(Integer... index) {
        if (useActiveObjectList) {
            objectList.clear();
            objectList.addAll(getActiveObjectList.runGetActiveObjectList());
        }
        Arrays.asList(index).forEach(mAdapter::notifyItemChanged);
        return recycler;
    }


    public RecyclerView reloadNew() {
        mAdapter = new MyAdapter(objectList);
        this.recycler.setAdapter(mAdapter);
        generateRecyclerView();
        return recycler;
    }
    //  <----- Generate -----


    //  --------------- GoTo --------------->
    public CustomRecycler<T> goTo(T t) {
        int index = objectList.indexOf(t);
        if (index == -1)
            return this;
        scrollTo(index, true);
        return this;
    }

    public CustomRecycler<T> goTo(GoToFilter<T> goToFilter, String search) {
        final T[] currentObject = (T[]) new Object[1];
        CustomList<T> filterdObjectList = new CustomList<>();
        List<T> allObjectList = getObjectList();

        if (search != null) {
            filterdObjectList.clear();
            filterdObjectList.addAll(allObjectList.stream().filter(t -> goToFilter.runGoToFilter(search, t)).collect(toList()));
            if (filterdObjectList.isEmpty())
                Toast.makeText(context, "Kein Eintrag für diese Suche", Toast.LENGTH_SHORT).show();
            else if (filterdObjectList.size() == 1) {
                scrollTo(allObjectList.indexOf(filterdObjectList.get(0)), true);
                return this;
            }
        }

        CustomDialog goToDialog = CustomDialog.Builder(context);
        goToDialog
                .setTitle("Gehe Zu")
                .addButton("Zurück", customDialog1 -> {
                    currentObject[0] = filterdObjectList.previous(currentObject[0]);
                    customDialog1.reloadView();
                }, false)
                .addButton("Weiter", customDialog1 -> {
                    currentObject[0] = filterdObjectList.next(currentObject[0]);
                    customDialog1.reloadView();
                }, false)
                .addButton(CustomDialog.BUTTON_TYPE.GO_TO_BUTTON, customDialog1 -> scrollTo(allObjectList.indexOf(currentObject[0]), true))
                .setView(getLayoutId())
                .setEdit(new CustomDialog.EditBuilder().setHint("Filter").setFireActionDirectly(search != null && !search.isEmpty()).setText(search != null ? search : "").allowEmpty()
                        .setOnAction((textInputHelper, textInputLayout, actionId, text1) -> {
                            filterdObjectList.clear();
                            filterdObjectList.addAll(allObjectList.stream().filter(t -> goToFilter.runGoToFilter(text1, t)).collect(toList()));
                            if (filterdObjectList.isEmpty())
                                Toast.makeText(context, "Kein Eintrag für diese Suche", Toast.LENGTH_SHORT).show();
                            else if (filterdObjectList.size() == 1) {
                                scrollTo(allObjectList.indexOf(filterdObjectList.get(0)), true);
                                goToDialog.dismiss();
                            } else {
                                currentObject[0] = filterdObjectList.get(0);
                                goToDialog.reloadView();
                            }
                        }, Helpers.TextInputHelper.IME_ACTION.SEARCH))
                .setSetViewContent((customDialog1, view1, reload) -> {
                    view1.setBackground(null);
                    View layoutView = customDialog1.findViewById(R.id.dialog_custom_layout_view);
                    if (currentObject[0] == null)
                        layoutView.setVisibility(View.GONE);
                    else {
                        setItemContent.runSetCellContent(layoutView, currentObject[0]);
                        layoutView.setVisibility(View.VISIBLE);
                    }
                })
                .show();
        return this;
    }

    public interface GoToFilter<T> {
        boolean runGoToFilter(String search, T t);
    }
    //  <--------------- GoTo ---------------


    //  --------------- Convenience --------------->
    public CustomRecycler<T> scrollTo(int index, boolean ripple) {
        if (index > objectList.size() - 1)
            return this;

        int firstVisiblePosition = ((LinearLayoutManager) recycler.getLayoutManager()).findFirstVisibleItemPosition();
        int lastVisiblePosition = ((LinearLayoutManager) recycler.getLayoutManager()).findLastVisibleItemPosition();
//        int middlePosition = (lastVisiblePosition + firstVisiblePosition) / 2;
//        int middleHeight = (lastVisiblePosition - firstVisiblePosition) / 2;

//        if (index > middlePosition) {
//            int diff = index - middlePosition;
//            if (index + diff >= objectList.size())
//                recycler.scrollToPosition(objectList.size() - 1);
//            else
//                recycler.scrollToPosition(index + diff);
//            recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                    recyclerView.getLayoutManager().findViewByPosition(index).setPressed(ripple);
//                    recyclerView.clearOnScrollListeners();
//                    super.onScrolled(recyclerView, dx, dy);
//                }
//            });
//        }
//        else
//            recycler.getLayoutManager().findViewByPosition(index).setPressed(ripple);


//        if (index + middleHeight >= objectList.size()) {
//            middleHeight = 0;
//            recycler.scrollToPosition(objectList.size() - 1);
//        } else
//            recycler.scrollToPosition(index + middleHeight);

        recycler.scrollToPosition(index);
        if (index >= firstVisiblePosition && index <= lastVisiblePosition)
            recycler.getLayoutManager().findViewByPosition(index).setPressed(ripple);
        else
            recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    recyclerView.getLayoutManager().findViewByPosition(index).setPressed(ripple);
                    recyclerView.clearOnScrollListeners();
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        return this;
    }

    public boolean isRecyclerScrollable() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recycler.getLayoutManager();
        RecyclerView.Adapter adapter = recycler.getAdapter();
        if (layoutManager == null || adapter == null) return false;

        return layoutManager.findLastCompletelyVisibleItemPosition() < adapter.getItemCount() - 1 && layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
    }
    //  <--------------- Convenience ---------------
}
