package sss.main;

import org.apache.lucene.queryparser.classic.ParseException;
import sss.lucene.LuceneManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {

        LuceneManager luceneManager = new LuceneManager();

        while (true) {
            System.out.println("Say something: ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                String query = br.readLine();
                String answer = luceneManager.getAnswer(query);
                System.out.println("Question: " + query);
                System.out.println("Answer: " + answer);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}



