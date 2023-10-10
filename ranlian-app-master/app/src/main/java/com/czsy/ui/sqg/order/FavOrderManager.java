package com.czsy.ui.sqg.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import mylib.utils.FileUtils;

// favorite order
public class FavOrderManager {
    private final static String FILE_MY_FAV_ORDER = "FILE_MY_FAV_ORDER";

    public static Set<String> getFavOrderIds() {
        Set<String> set = (Set<String>) FileUtils.getObject(FILE_MY_FAV_ORDER, Set.class);
        if (set == null) {
            set = new HashSet<>();
            FileUtils.saveObject(FILE_MY_FAV_ORDER, set);
        }
        return set;
    }

    public static void saveFavOrder(String id) {
        Set<String> set = getFavOrderIds();
        set.add(id);
        FileUtils.saveObject(FILE_MY_FAV_ORDER, set);
    }

    public static void removeFavOrder(String id) {
        Set<String> set = getFavOrderIds();
        if (set.remove(id)) {
            FileUtils.saveObject(FILE_MY_FAV_ORDER, set);
        }
    }

    public static void mergeIds(Collection<String> ids) {
        Set<String> set = getFavOrderIds();
        Set<String> newSet = new HashSet<>();
        for (String id : ids) {
            if (set.contains(id)) {
                newSet.add(id);
            }
        }
        FileUtils.saveObject(FILE_MY_FAV_ORDER, newSet);
    }
}
