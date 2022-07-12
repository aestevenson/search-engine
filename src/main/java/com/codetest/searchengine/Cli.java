package com.codetest.searchengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class Cli {

    private static SearchEngine se = new SearchEngine();
    public static void main(String[] argv) {
        //TODO: close on exception
        Scanner scan = new Scanner(System.in);
        while (true) {            
            String line = new String(scan.nextLine());
            if(line.length() == 0) {
                break;
            }
            String result = handle(line);
            System.out.println(result);

        }
        scan.close();        
    }

    private static String handle(String line) {        
        StringTokenizer st = new StringTokenizer(line);
        String command = null;
        Integer docId = null;
        List<String> tokens = new ArrayList<String>();
        String query = null;
        int i = 0;
        while (st.hasMoreTokens()) {
            if (i == 0) {
                command = st.nextToken();
            } else if (i == 1) {
                if (command.equals("index")) {
                    docId = Integer.valueOf(st.nextToken());                
                } else {
                    // We know its a query and the command is already set.  Short ciruit out with the query.
                    query = line.replace("query ", "");
                    break;   
                }
            } else {
                tokens.add(st.nextToken());
            }

            i += 1;
        }
                        
        if (command.equals("index")) {
             try { 
                String r = se.index(docId, tokens);
                return String.format("index ok %s", r);
             } catch (Exception e) {
                return String.format("index error %s", e.getMessage());
             }
                            
        } else if (command.equals("query")) {
            try { 
                Set<Integer> r = se.query(query);
                return String.format("query results %s", r);
             } catch (Exception e) {
                return String.format("query error %s", e.getMessage());
             }
        } else {
            return "Invalid command";
        }

    }
}