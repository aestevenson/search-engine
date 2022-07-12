package com.codetest.searchengine;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Set;

import org.junit.Test;




public class SearchEngineTest {

    @Test
    public void testIndex() {
        SearchEngine se = new SearchEngine();
        se.index(1, Arrays.asList("Buenos Aires", "Córdoba", "La Plata"));
    }
    
    @Test
    public void testQuery() {
        SearchEngine se = new SearchEngine();
        se.index(1, Arrays.asList("foo", "bar", "baz"));
        se.index(2, Arrays.asList("foo"));
        se.index(3, Arrays.asList("baz"));

        Set<Integer> results = se.query("foo");
        assertEquals("[1, 2]", results.toString());
    }

    @Test
    public void testConjunctionQuery() {
        SearchEngine se = new SearchEngine();
        se.index(1, Arrays.asList("foo", "bar", "baz"));
        se.index(2, Arrays.asList("foo"));
        se.index(3, Arrays.asList("baz"));

        Set<Integer> results = se.query("foo | baz");
        assertEquals("[1, 2, 3]", results.toString());
    }

    @Test
    public void testQueryWithSetJoins() {
        SearchEngine se = new SearchEngine();
        se.index(1, Arrays.asList("foo", "bar", "baz"));
        se.index(2, Arrays.asList("foo"));
        se.index(3, Arrays.asList("baz"));

        Set<Integer> results = se.query("(foo | bar) & baz");
        assertEquals("[1]", results.toString());
    }

    @Test
    public void testQueryWithSetJoins_reverseOrder() {
        SearchEngine se = new SearchEngine();
        se.index(1, Arrays.asList("foo", "bar", "baz"));
        se.index(2, Arrays.asList("foo"));
        se.index(3, Arrays.asList("baz"));

        Set<Integer> results = se.query("baz & (foo | bar)");
        assertEquals("[1]", results.toString());
    }

    @Test
    public void testGiven1() {
        SearchEngine se = new SearchEngine();
        se.index(1, Arrays.asList("soup", "tomato", "cream", "salt"));
        se.index(2, Arrays.asList("sugar", "eggs", "flour", "sugar", "cocoa", "cream", "butter"));
        se.index(1, Arrays.asList("bread", "butter", "salt"));
        se.index(3, Arrays.asList("soup", "fish", "potato", "salt", "pepper"));

        Set<Integer> results = se.query("butter");
        assertEquals("[1, 2]", results.toString());

    }

    @Test
    public void testGiven2() {
        SearchEngine se = new SearchEngine();
        se.index(1, Arrays.asList("soup", "tomato", "cream", "salt"));
        se.index(2, Arrays.asList("sugar", "eggs", "flour", "sugar", "cocoa", "cream", "butter"));
        se.index(1, Arrays.asList("bread", "butter", "salt"));
        se.index(3, Arrays.asList("soup", "fish", "potato", "salt", "pepper"));

        Set<Integer> results = se.query("sugar");
        assertEquals("[2]", results.toString());        
    }

    @Test
    public void testGiven3() {
        SearchEngine se = new SearchEngine();
        se.index(1, Arrays.asList("soup", "tomato", "cream", "salt"));
        se.index(2, Arrays.asList("sugar", "eggs", "flour", "sugar", "cocoa", "cream", "butter"));
        se.index(1, Arrays.asList("bread", "butter", "salt"));
        se.index(3, Arrays.asList("soup", "fish", "potato", "salt", "pepper"));

        Set<Integer> results = se.query("soup");
        assertEquals("[3]", results.toString());        
    }

    @Test
    public void testGiven4() {
        SearchEngine se = new SearchEngine();
        se.index(1, Arrays.asList("soup", "tomato", "cream", "salt"));
        se.index(2, Arrays.asList("sugar", "eggs", "flour", "sugar", "cocoa", "cream", "butter"));
        se.index(1, Arrays.asList("bread", "butter", "salt"));
        se.index(3, Arrays.asList("soup", "fish", "potato", "salt", "pepper"));

        Set<Integer> results = se.query("(butter | potato) & salt");
        assertEquals("[1, 3]", results.toString());        
    }

    @Test
    public void testPerformance() {
        SearchEngine se = new SearchEngine();
        for (int i = 1; i < 100001; i++) {
            se.index(i, Arrays.asList("sugar", "eggs", "flour", "sugar", "cocoa", "cream", "butter"));
        }
        Set<Integer> results = se.query("butter & (butter & butter)");
        assertEquals(100000, results.size());
    }
}
