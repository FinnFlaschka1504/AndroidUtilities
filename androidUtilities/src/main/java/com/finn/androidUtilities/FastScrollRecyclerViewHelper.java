/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.finn.androidUtilities;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewOverlay;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finn.androidUtilities.CustomList;
import com.finn.androidUtilities.CustomUtility;
import com.finn.androidUtilities.CustomRecycler;
import com.google.android.material.appbar.AppBarLayout;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.zhanghai.android.fastscroll.FastScroller;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;
import me.zhanghai.android.fastscroll.PopupTextProvider;
import me.zhanghai.android.fastscroll.Predicate;

public class FastScrollRecyclerViewHelper implements FastScroller.ViewHelper {
    private static final String TAG = "RecyclerViewHelper";

    @NonNull
    private final RecyclerView mView;
    @Nullable
    FastScroller[] mFastScroller;
    @Nullable
    private final PopupTextProvider mPopupTextProvider;

    @NonNull
    private final Rect mTempRect = new Rect();

    Integer[] scrollRange;
    CustomList<Integer>[] heightList;
    CustomRecycler customRecycler;
    int thumbOffset = 0;
    int wouldBeThumbOffset = 0;
    boolean smoothScroll;
    private Runnable onPreDraw;
    private View thumbView;
    private Pair<Integer,Integer> paddingTopAndBottom;

    // ToDo: wenn nach ganz untern gescrollt dann letztes element anzeigen
    //  & warum so abgehackt in medien

    /**  <------------------------- Constructor -------------------------  */
    public FastScrollRecyclerViewHelper(@NonNull RecyclerView view, @Nullable PopupTextProvider popupTextProvider) {
        mView = view;
        mPopupTextProvider = popupTextProvider;
    }

    public FastScrollRecyclerViewHelper(@NonNull RecyclerView view, @Nullable FastScroller[] fastScroller, @Nullable PopupTextProvider popupTextProvider) {
        mView = view;
        mFastScroller = fastScroller;
        mPopupTextProvider = popupTextProvider;
    }

    public FastScrollRecyclerViewHelper(CustomRecycler customRecycler, @Nullable FastScroller[] fastScroller, Integer[] scrollRange, CustomList<Integer>[] heightList, boolean smoothScroll, @Nullable Pair<Integer,Integer> paddingTopAndBottom, @Nullable PopupTextProvider popupTextProvider) {
        mView = customRecycler.getRecycler();
        mFastScroller = fastScroller;
        mPopupTextProvider = popupTextProvider;
        this.customRecycler = customRecycler;
        this.scrollRange = scrollRange;
        this.heightList = heightList;
        this.smoothScroll = smoothScroll;
        this.paddingTopAndBottom = paddingTopAndBottom;
    }

    public FastScrollRecyclerViewHelper(CustomRecycler customRecycler, @Nullable FastScroller[] fastScroller, boolean smoothScroll, @Nullable Pair<Integer,Integer> paddingTopAndBottom, @Nullable PopupTextProvider popupTextProvider) {
        mView = customRecycler.getRecycler();
        mFastScroller = fastScroller;
        mPopupTextProvider = popupTextProvider;
        this.customRecycler = customRecycler;
        this.smoothScroll = smoothScroll;
        this.paddingTopAndBottom = paddingTopAndBottom;
    }
    /**  ------------------------- Constructor ------------------------->  */

    @Override
    public void addOnPreDrawListener(@NonNull Runnable onPreDraw) {
        this.onPreDraw = onPreDraw;
        mView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                LinearLayoutManager layoutManager = (LinearLayoutManager) mView.getLayoutManager();
//                int count = getLastFullyVisibleTopElement();
//                CustomUtility.logD(TAG, "onScrolled:  %d | %d | %s | %f || %d | %d | %f"
////                        , layoutManager.findFirstCompletelyVisibleItemPosition(), getFirstVisiblePosition() //%d | %d | %d | %d ||
////                        , layoutManager.findLastCompletelyVisibleItemPosition(), layoutManager.findLastVisibleItemPosition()
//                        , mView.getHeight(), count, istLastFullyVisibleTopElement(), getLastItemVisiblePercentage()
//                        , getLastItemHeight(), thumbOffset, getThumbOffsetLastItemPercentage()
//                );
                onPreDraw.run();
            }
        });
    }

    @Override
    public void addOnScrollChangedListener(@NonNull Runnable onScrollChanged) {
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                onScrollChanged.run();
            }
        });
    }

    @Override
    public void addOnTouchEventListener(@NonNull Predicate<MotionEvent> onTouchEvent) {
        CustomUtility.GenericReturnOnlyInterface<View> tryGetThumbView = () -> {
            if (mFastScroller != null && mFastScroller[0] != null) {
                try {
                    Field mThumbView = FastScroller.class.getDeclaredField("mThumbView");
                    mThumbView.setAccessible(true);
                    thumbView = (View) mThumbView.get(mFastScroller[0]);
                    return thumbView;
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    Log.e(TAG, "addOnTouchEventListener: ", e);
                }
            }
            return null;
        };

        final boolean[] blocked = {true};

        final MotionEvent[] isFake = {null};

        Runnable startFakeScroll = () -> {
            long startTime = System.currentTimeMillis();
            CustomUtility.GenericInterface<Integer> dispatch = posY -> {
                MotionEvent motionEvent = MotionEvent.obtain(
                        isFake[0].getDownTime(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_MOVE,
                        isFake[0].getX(),
                        posY,
                        0
                );
                onTouchEvent.test(motionEvent);
            };

            int start = 85;
            int end = 2640;
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        dispatch.run(end);
                        long endTime = System.currentTimeMillis();
                        CustomUtility.logD(TAG, "applyTransformation: %d", endTime - startTime);
//                        thumbOffset = 0;
                    } else {
                        dispatch.run((int) ((end - start) * interpolatedTime));
//                        thumbOffset = startOffset - (int) (startOffset * interpolatedTime);
//                        onPreDraw.run();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return false;
                }
            };

            a.setDuration(300);
            thumbView.startAnimation(a);

        };

//        final long[] startTime = {System.currentTimeMillis()};

        CustomUtility.GenericReturnInterface<MotionEvent, Boolean> processMotionEvent = event -> {
            if (isFake[0] != null) {
                return true;
            }


            if (thumbView == null && (thumbView = tryGetThumbView.run()) == null)
                return onTouchEvent.test(event);
            else {
                int[] ints = new int[]{0,0};
                thumbView.getLocationInWindow(ints);
                float rawX = event.getX();
                float rawY = event.getY();
                boolean isInX = rawX >= ints[0] && rawX <= ints[0] + thumbView.getWidth();
                boolean isInY = rawY >= ints[1] && rawY <= ints[1] + thumbView.getHeight();
//                CustomUtility.logD(TAG, "addOnTouchEventListener: x:%f | y:%f 85 2640", rawX, rawY);

                if (!blocked[0] && event.getAction() == MotionEvent.ACTION_UP) {
//                    long endTime = System.currentTimeMillis();
//                    CustomUtility.logD(TAG, "applyTransformation: %d", endTime - startTime[0]);
                    if (!smoothScroll) {
                        startResetThumbOffsetAnimation();
                    }
                    CustomUtility.reflectionCall(mView, "dispatchOnScrollStateChanged", Pair.create(int.class, RecyclerView.SCROLL_STATE_IDLE));
                }

                if ((event.getAction() == MotionEvent.ACTION_DOWN && isInX && isInY) || (event.getAction() != MotionEvent.ACTION_DOWN && !blocked[0])) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN)
//                        startTime[0] = System.currentTimeMillis();
//                    isFake[0] = event;
//                    startFakeScroll.run();

                    ViewParent parent;
                    View appBarLayout;
                    if (blocked[0] && (parent = mView.getParent()) instanceof CoordinatorLayout && ((CoordinatorLayout) parent).getChildCount() > 0 && (appBarLayout = ((CoordinatorLayout) parent).getChildAt(0)) instanceof AppBarLayout) {
                        if (!((AppBarLayout) appBarLayout).isLifted()) {
                            blocked[0] = true;
                            return false;
                        }
                    }
                    blocked[0] = false;
                    return onTouchEvent.test(event);
                } else
                    blocked[0] = true;
                return false;
            }
        };

        mView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {
                return processMotionEvent.run(event);
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent event) {
                processMotionEvent.run(event);
            }
        });
    }

    int lastRange;
    @Override
    public int getScrollRange() {
        if (scrollRange == null) {
            return /*lastRange = */getItemHeight() * getItemCount() + getPaddingSum();// - mView.getPaddingBottom() - mView.getPaddingTop();
        } else
            return /*lastRange = */scrollRange[0] + getPaddingSum();
    }

    private int getPaddingSum() {
        return paddingTopAndBottom != null ? paddingTopAndBottom.first + paddingTopAndBottom.second : 0;
    }

    private int getItemCount() {
        int count;
        if (heightList == null)
            count = mView.getAdapter().getItemCount();
        else
            count = heightList[0].size();
        LinearLayoutManager layoutManager = customRecycler.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
            count = (int) Math.ceil((double) count / ((GridLayoutManager) layoutManager).getSpanCount());
        return count;
    }

    int prev;
    @Override
    public int getScrollOffset() {
        int position = getFirstItemPosition();
        if (position == RecyclerView.NO_POSITION) {
            return 0;
        }
        int firstItemOffset = getFirstItemOffset();
        int sum;
        if (heightList == null)
            sum = getItemHeight() * position;
        else
            sum = heightList[0].subList(0, position).stream().mapToInt(Integer::intValue).sum();
        int i = mView.getPaddingTop() + sum - firstItemOffset  + thumbOffset;
//        CustomUtility.logD(TAG, "getScrollOffset: %d | %d | %d", i - prev, thumbOffset, sum);
        prev = i;
        return i;
    }

    @Override
    public void scrollTo(int offset) {
        mView.stopScroll();
        int i = 0;
        int size;
        boolean isLast;
        LinearLayoutManager layoutManager = customRecycler.getLayoutManager();

        if (heightList == null) {

//            CustomUtility.logD(TAG, "scrollTo: %d", offset);

            int columns = 1;
            if (layoutManager instanceof GridLayoutManager)
                columns = ((GridLayoutManager) layoutManager).getSpanCount();

            int itemHeight = getItemHeight();


            i = offset / itemHeight;
//            i -= i % 50;
            offset -= i * itemHeight;

            int lastFullyVisibleTopElement = getLastFullyVisibleTopElement();
            isLast = lastFullyVisibleTopElement <= getFirstVisiblePosition();

            int oldOffset = offset;
            if (isLast && !smoothScroll) {
                int lastItemClip =  (getItemCount() - lastFullyVisibleTopElement) * itemHeight - mView.getHeight() + getPaddingSum();
                double percentage = offset / (double) lastItemClip;
                offset = (int) CustomUtility.mapNumber(percentage, 0.5, 0.9, 0, offset);
            }

            if (smoothScroll)
                thumbOffset = 0;
            else if (isLast)
                thumbOffset = oldOffset - offset;
            else
                thumbOffset = offset;

            layoutManager.scrollToPositionWithOffset(i * columns, smoothScroll || isLast ? -offset : 0);


//            int rest = i % 3;
//            if (rest == 0 || isLast) {
//                layoutManager.scrollToPositionWithOffset(i * columns, smoothScroll || isLast ? -offset : 0);
//                CustomUtility.logD(TAG, "scroll: %d | %d | %d | %d", rest, thumbOffset, i, offset);
//            } else {
//                thumbOffset -= itemHeight * rest;
//                CustomUtility.logD(TAG, "thumb: %d | %d | %d | %d", rest, thumbOffset, i, offset);
////                layoutManager.scrollToPositionWithOffset((i - rest) * columns, smoothScroll ? -offset : 0);
//                if (mFastScroller != null && mFastScroller[0] != null)
//                    CustomUtility.reflectionCall(mFastScroller[0], "updateScrollbarState");
//            }
        } else {
            size = getItemCount();
            Integer itemHeight = 0;
            for (; i < size; i++) {
                itemHeight = heightList[0].get(i);
                if (offset - itemHeight < 0)
                    break;
                else
                    offset -= itemHeight;
            }
//            wouldBeThumbOffset = offset;
//            CustomUtility.logD(TAG, "scrollTo: %d | %d | %d | %f", offset, lastFullTopElement, lastItemClip, offset / (double) lastItemClip);
            int lastFullyVisibleTopElement = getLastFullyVisibleTopElement();
            isLast = lastFullyVisibleTopElement <= getFirstVisiblePosition();
//            isLast = /*layoutManager.findLastVisibleItemPosition() >= size - 1 isLastItemVisible && */istLastFullyVisibleTopElement() && (getThumbOffsetLastItemPercentage() >= 0.5 || getLastItemVisiblePercentage() <= 0.5);// && offset > itemHeight / 2;
//            CustomUtility.logD(TAG, "scrollTo: %s | %s | %f | %f | %d", isLast, istLastFullyVisibleTopElement(), getThumbOffsetLastItemPercentage(), getLastItemVisiblePercentage(), wouldBeThumbOffset);

            int oldOffset = offset;
            if (isLast) {
                int lastItemClip = heightList[0].subList(lastFullyVisibleTopElement, heightList[0].size()).stream().mapToInt(Integer::intValue).sum() - mView.getHeight() + getPaddingSum();
                double percentage = offset / (double) lastItemClip;
                offset = (int) CustomUtility.mapNumber(percentage, 0.5, 0.9, 0, offset);
//                double percentage = getThumbOffsetLastItemPercentage();
//                CustomUtility.logD(TAG, "scrollTo: %f | %d | %f || %d", percentage, offset, mapNumber, thumbOffset);
            }
            if (smoothScroll)
                thumbOffset = 0;
            else if (isLast)
                thumbOffset = oldOffset - offset;
            else
                thumbOffset = offset;
            layoutManager.scrollToPositionWithOffset(i, smoothScroll || isLast ? -offset : 0);


//            for (int j = layoutManager.findFirstVisibleItemPosition(); j <= layoutManager.findLastVisibleItemPosition(); j++) {
//
//            }

/*
            for (int j = 0; j < mView.getChildCount(); j++) {
                View child = mView.getChildAt(j);
                int index = layoutManager.getPosition(child);
                heightMap.put(index, child.getHeight());
            }
            String heights = heightMap.entrySet().stream().sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey())).map(entry -> MessageFormat.format("{0}: {1} | {2}", entry.getKey() + 1, entry.getValue(), heightList[0].get(entry.getKey()) - 36)).collect(Collectors.joining("\n"));
            int totalHeight = heightMap.values().stream().mapToInt(integer -> integer.intValue() + 36).sum();
            String range = "\n" + totalHeight + " | " + scrollRange[0];
            String result = "\n" + heights + range;
            CustomUtility.logD(TAG, "scrollTo: %s", result);
*/

//            Integer manuelHeight = heightList[0].get(layoutManager.findLastVisibleItemPosition());
//            int actualHeight = getLastItemHeight();

//            CustomUtility.logD(TAG, "scrollTo: %d || %d | %d", layoutManager.findLastVisibleItemPosition(), manuelHeight, actualHeight);
        }
    }

    Map<Integer, Integer> heightMap = new HashMap<>();

    private void startResetThumbOffsetAnimation() {
        if (thumbView != null && thumbOffset > 0 && onPreDraw != null) {
            int startOffset = thumbOffset;
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        thumbOffset = 0;
                    } else {
                        thumbOffset = startOffset - (int) (startOffset * interpolatedTime);
                        onPreDraw.run();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return false;
                }
            };

            a.setDuration(125);
            thumbView.startAnimation(a);
        }
    }

    @Nullable
    @Override
    public String getPopupText() {
        PopupTextProvider popupTextProvider = mPopupTextProvider;
        if (popupTextProvider == null) {
            RecyclerView.Adapter<?> adapter = mView.getAdapter();
            if (adapter instanceof PopupTextProvider) {
                popupTextProvider = (PopupTextProvider) adapter;
            }
        }
        if (popupTextProvider == null) {
            return null;
        }
        int position = getFirstItemAdapterPosition();
        if (position == RecyclerView.NO_POSITION) {
            return null;
        }
        return popupTextProvider.getPopupText(position);
    }

    private int getItemHeight() {
        if (mView.getChildCount() == 0) {
            return 0;
        }
        View itemView = mView.getChildAt(0);
        mView.getDecoratedBoundsWithMargins(itemView, mTempRect);
        if (paddingTopAndBottom != null) {
            int position = mView.getLayoutManager().getPosition(itemView);
            return mTempRect.height() - (position == 0 ? paddingTopAndBottom.first : 0);
        } else
            return mTempRect.height();
    }

    private int getLastItemHeight() {
        int childCount = mView.getChildCount();
        if (childCount == 0) {
            return RecyclerView.NO_POSITION;
        }
        View itemView = mView.getChildAt(childCount - 1);
        return itemView.getHeight();
    }

    public int getFirstItemPosition() {
        int position = getFirstItemAdapterPosition();
        LinearLayoutManager linearLayoutManager = getVerticalLinearLayoutManager();
        if (linearLayoutManager == null) {
            return RecyclerView.NO_POSITION;
        }
        if (linearLayoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) linearLayoutManager;
            position /= gridLayoutManager.getSpanCount();
        }
        return position;
    }

    private int getFirstItemAdapterPosition() {
        if (mView.getChildCount() == 0) {
            return RecyclerView.NO_POSITION;
        }
        View itemView = mView.getChildAt(0);
        LinearLayoutManager linearLayoutManager = getVerticalLinearLayoutManager();
        if (linearLayoutManager == null) {
            return RecyclerView.NO_POSITION;
        }
        return linearLayoutManager.getPosition(itemView);
    }

    public int getFirstItemOffset() {
        if (mView.getChildCount() == 0) {
            return RecyclerView.NO_POSITION;
        }
        View itemView = mView.getChildAt(0);
        mView.getDecoratedBoundsWithMargins(itemView, mTempRect);
        return mTempRect.top;
    }

    /**  <------------------------- Last Element -------------------------  */
    private double getThumbOffsetLastItemPercentage() {
        return wouldBeThumbOffset / (double) getLastItemHeight();
    }

    public int getLastItemOffset() {
        int childCount = mView.getChildCount();
        if (childCount == 0) {
            return RecyclerView.NO_POSITION;
        }
        View itemView = mView.getChildAt(childCount - 1);
        mView.getDecoratedBoundsWithMargins(itemView, mTempRect);
        return mTempRect.bottom - mView.getHeight();
    }

    public double getLastItemVisiblePercentage() {
        int childCount = mView.getChildCount();
        if (childCount == 0) {
            return RecyclerView.NO_POSITION;
        }
        View itemView = mView.getChildAt(childCount - 1);
        mView.getDecoratedBoundsWithMargins(itemView, mTempRect);
        return (mTempRect.bottom - mView.getHeight()) / (double) itemView.getHeight();
    }

    private boolean istLastFullyVisibleTopElement() {
        return getLastFullyVisibleTopElement() <= getFirstVisiblePosition();
    }

    private int getLastFullyVisibleTopElement() {
        int count = 0;
        int height = mView.getHeight();
        height -= getPaddingSum();
        if (heightList != null) {
            for (int i = heightList[0].size() - 1; i >= 0 && height > 0; i--) {
                height -= heightList[0].get(i);
                count++;
            }
        } else {
            int itemHeight = getItemHeight();
            count = (int) Math.ceil(height / (double) itemHeight);
//            while (height > 0) {
//                height -= itemHeight;
//                count++;
//            }
        }

        return getItemCount() - count;
    }

    private int getFirstVisiblePosition() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mView.getLayoutManager();
        return layoutManager.findFirstVisibleItemPosition() / (layoutManager instanceof GridLayoutManager ? ((GridLayoutManager) layoutManager).getSpanCount() : 1);
    }
    /**  ------------------------- Last Element ------------------------->  */

    private void scrollToPositionWithOffset(int position, int offset) {
        LinearLayoutManager linearLayoutManager = getVerticalLinearLayoutManager();
        if (linearLayoutManager == null) {
            return;
        }
        if (linearLayoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) linearLayoutManager;
            position *= gridLayoutManager.getSpanCount();
        }
        // LinearLayoutManager actually takes offset from paddingTop instead of top of RecyclerView.
        offset -= mView.getPaddingTop();
        linearLayoutManager.scrollToPositionWithOffset(position, offset);
    }

    @Nullable
    private LinearLayoutManager getVerticalLinearLayoutManager() {
        RecyclerView.LayoutManager layoutManager = mView.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return null;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        if (linearLayoutManager.getOrientation() != RecyclerView.VERTICAL) {
            return null;
        }
        return linearLayoutManager;
    }
}
