package com.example.demo.commons;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListUtils {
    public static <T> List<T> asList(T... a) {
    		if (a == null) {
    			return Collections.emptyList();
    		}
        return Arrays.asList(a);
    }
}
