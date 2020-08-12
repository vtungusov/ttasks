package com.siberteam.vtungusov.sorter;

public enum SortDirection {
    ASC(false),
    DESC(true);

    boolean reverse;

    SortDirection(boolean reverse) {
        this.reverse = reverse;
    }
}
