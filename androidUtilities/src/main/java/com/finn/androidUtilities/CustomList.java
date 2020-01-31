package com.finn.androidUtilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomList<E> extends ArrayList<E> {

//  ----- Constructors ----->
    public CustomList() {
    }

    public CustomList(@NonNull Collection<? extends E> c) {
        super(c);
    }

    public CustomList(int initialCapacity) {
        super(initialCapacity);
    }

    public CustomList(E[] objects) {
        if (objects != null)
            addAll(Arrays.asList(objects));
    }
//  <----- Constructors -----


    //  ------------------------- Random ------------------------->
    public E getRandom() {
        return get((int) (Math.random() * size()));
    }

    public E removeRandom(){
        return remove((int) (Math.random() * size()));
    }
    //  <------------------------- Random -------------------------

    //  ------------------------- get... ------------------------->
    public E getLast() {
        if (isEmpty())
            return null;
        return get(- 1);
    }

    public E getFirst() {
        if (isEmpty())
            return null;
        return get(0);
    }

    @Override
    public E get(int index) {
        if (index < 0)
            return super.get(size() + index);
        else
            return super.get(index);
    }

    public E getSmallest() {
        if (isEmpty() || !(get(0) instanceof Comparable))
            return null;
        return stream().min((o1, o2) -> ((Comparable<E>) o1).compareTo(o2)).orElse(null);
    }

    public E getBiggest() {
        if (isEmpty() || !(get(0) instanceof Comparable))
            return null;
        return stream().max((o1, o2) -> ((Comparable<E>) o1).compareTo(o2)).orElse(null);
    }
    //  <------------------------- get... -------------------------

    public CustomList<E> add(E... e) {
        Collections.addAll(this, e);
        return this;
    }

    public CustomList<E> executeIf(Predicate<CustomList<E>> predicate, ListInterface<E> executeOnTrue) {
        if (predicate.test(this))
            executeOnTrue.runListInterface(this);
        return this;
    }

    public interface ListInterface<E> {
        void runListInterface(CustomList<E> customList);
    }

    //  ------------------------- Checks ------------------------->
    public boolean isFirst(E e) {
        if (e == null || isEmpty())
            return false;
        return e.equals(get(0));
    }

    public boolean isLast(E e) {
        if (e == null || isEmpty())
            return false;
        return e.equals(getLast());
    }

    public boolean isLast(int i) {
        return i == size() - 1;
    }
    //  <------------------------- Checks -------------------------

    //  --------------- Recycle --------------->
    public E next(E e) {
        if (isLast(e))
            return get(0);
        else
            return get(indexOf(e) + 1);
    }
    public E previous(E e) {
        if (isEmpty())
            return null;
//        if (get(0).equals(e))
//            return getLast();
//        else
        return get(indexOf(e) - 1);
    }
    //  <--------------- Recycle ---------------


//  ----- forEach ----->
    public void forEachCount(ForEachCount_breakable<E> forEachCount_breakable) {
        int count = 0;
        for (E e : this) {
            if (forEachCount_breakable.runForeEachCount(e, count)) {
                break;
            }
            count++;
        }
    }
    public interface ForEachCount_breakable<E> {
        boolean runForeEachCount(E e, int count);
    }

    public void forEachCount(ForEachCount<E> forEachCount) {
        int count = 0;
        for (E e : this) {
            forEachCount.runForeEachCount(e, count);
            count++;
        }
    }
    public interface ForEachCount<E> {
        void runForeEachCount(E e, int count);
    }
//  <----- forEach -----

    public Integer indexOf(Predicate<? super E> predicate) {
        final Integer[] foundAt = new Integer[1];
        forEachCount((e, count) -> {
            if (predicate.test(e)) {
                foundAt[0] = count;
                return true;
            }
            return false;
        });
        return foundAt[0];
    }

    //  --------------- Generate --------------->
    public CustomList<E> generate(int amount, Generator<E> generator) {
        for (int i = 0; i < amount; i++) {
            add(generator.runGenerator(i));
        }
        return this;
    }

    public interface Generator<T> {
        T runGenerator(int index);
    }
    //  <--------------- Generate ---------------

    //  ----- Stream ----->
    public static <E,R> CustomList<R> map(List<E> list, Function<? super E, ? extends R> mapper) {
        return list.stream().map(mapper).collect(Collectors.toCollection(CustomList::new));
    }

    public <R> CustomList<R> map(Function<? super E, ? extends R> mapper) {
        return stream().map(mapper).collect(Collectors.toCollection(CustomList::new));
    }

    public CustomList<E> filter(Predicate<? super E> mapper) {
        return stream().filter(mapper).collect(Collectors.toCollection(CustomList::new));
    }

    public CustomList<E> sorted(@Nullable Comparator<? super E> c) {
        super.sort(c);
        return this;
    }

    public CustomList<E> distinct() {
        Stream<E> distinct = stream().distinct();
        clear();
        addAll(distinct.collect(Collectors.toCollection(CustomList::new)));
        return this;
    }
//  <----- Stream -----


    //  --------------- To ... --------------->
    public ArrayList<E> toArrayList() {
        return new ArrayList<>(this);
    }
    //  <--------------- To ... ---------------


    //  -------------------- Replace -------------------->
    public static <T> void replace(List<T> list, T t, ReplaceWith<T> replaceWith){
        list.replaceAll(t2 -> t2 == t ? replaceWith.runReplaceWith(t2) : t2);
    }

    public static <T> void replace(List<T> list, int index, ReplaceWith<T> replaceWith){
        replace(list, list.get(index), replaceWith);
    }


    public interface ReplaceWith<T> {
        T runReplaceWith(T t);
    }
    //  <-------------------- Replace --------------------


    //  ------------------------- remove ------------------------->
    public E removeLast() {
        if (isEmpty())
            return null;
        return remove(size() - 1);
    }

    public E removeFirst() {
        if (isEmpty())
            return null;
        return remove(0);
    }
    //  <------------------------- remove -------------------------
}
