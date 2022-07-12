package com.codetest.searchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchEngine {

    private Map<Integer, List<String>> documents = new HashMap<>();


    public String index(Integer docId, List<String> tokens) {
        documents.put(docId, tokens);
        return docId.toString();
    }

    class TokenCombinator {
        Set<Integer> firstTokenSet;
        Set<Integer> secondTokenSet;
        Character operator;

        public TokenCombinator() {

        }

        public void setOperator(Character operator) {
            this.operator = operator;
        }

        public void addTokenSet(Set<Integer> tokenSet) {
            if (firstTokenSet == null) {
                firstTokenSet = tokenSet;
            } else if (secondTokenSet == null) {
                secondTokenSet = tokenSet;
            } else {
                throw new RuntimeException("Cannot add 3rd token set.");
            }
        }

        public Set<Integer> resolve() {
            if (firstTokenSet != null && secondTokenSet == null) {
                return firstTokenSet;
            } else {
                if (this.operator == '&') {
                    return resolveResultsAnd(firstTokenSet, secondTokenSet);
                } else if (this.operator == '|') {
                    return resolveResultsOr(firstTokenSet, secondTokenSet);
                } else {
                    throw new RuntimeException("Operand expected.");
                }            
            }
        }

        private Set<Integer> resolveResultsOr(Set<Integer> listOne, Set<Integer> listTwo) {
            Set<Integer> vals = new HashSet<Integer>();
            vals.addAll(listOne);
            vals.addAll(listTwo);
            return vals;
        }
    
        private Set<Integer> resolveResultsAnd(Set<Integer> listOne, Set<Integer> listTwo) {
            Set<Integer> vals = new HashSet<Integer>(listOne);
            vals.retainAll(listTwo);
            return vals;
        }
    }

    public Set<Integer> query(String queryExpression) {
        Set<Integer> results = solve(queryExpression);
        return results;
    }

    /**
     * I want to find the outermost couplet of A and B as well as the operand seperating them.  A and B can be either
     * another couplet in parentheses or a token.  Use reccursion on the elements that are withing brances.  Currently assuming that
     * you can't have nested braces.
     * 
     * @param qe the string query: must have spaces, must adhere to test parameters
     * @return the Set of Integer docIds that the query matches
     */
    private Set<Integer> solve(String qe) {         
        
        TokenCombinator tokenCombinator = new TokenCombinator();
        
        char[] qeChars = qe.toCharArray();
        List<Character> tokenBuilder = new ArrayList<Character>();
        List<Character> subQueryBuilder = new ArrayList<Character>();
        boolean inSubQuery = false;

        for (int i = 0; i < qeChars.length; i++) {
            if (qeChars[i] == '(') {
                inSubQuery = true;
            } else if (!inSubQuery && (qeChars[i] == '&' || qeChars[i] == '|')) {                
                tokenCombinator.setOperator(qeChars[i]);
                //skipping the space after the operand so it doesn't count as a token
                //TODO: this is cloogy
                i += 1; 
            } else if (!inSubQuery) {
                tokenBuilder.add(qeChars[i]);
                if (qeChars.length - 1 == i || qeChars[i] == ' ') {                    
                    // finish the token
                    String current = tokenBuilder.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining());

                    // clear the builder
                    tokenBuilder = new ArrayList<Character>();

                    //get the sets for the token
                    tokenCombinator.addTokenSet(getSets(current.trim()));
                    // tokenSets.add(getSets(current.trim()));
                }                
            } else if (inSubQuery) {
                if (qeChars[i] == ')') {
                    inSubQuery = false;
                    String subQuery = subQueryBuilder.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining());
                    // tokenSets.add(solve(subQuery));
                    tokenCombinator.addTokenSet(solve(subQuery));
                    i += 1; //Again, having to skip the space after the closed parentheses so it doesn't treat it as a token.
                } else {
                    subQueryBuilder.add(qeChars[i]);
                }
            } 
        }

        return tokenCombinator.resolve();
    }

    //TODO: opportunity for optimazation: build a cache, parallelize
    private Set<Integer> getSets(String value) {
        Set<Integer> results = new HashSet<Integer>();
        for (Integer key: documents.keySet()) {
            List<String> values = documents.get(key);
            for (String val: values) {
                if (val.equals(value)) {
                    results.add(key);
                    break;
                }
            }
        }
        return results;
    }
    
}