import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // Using Scanner for Getting Input from User
        Scanner in = new Scanner(System.in);
        System.out.print("Sentence: ");
        String sentence = in.nextLine(); // Sentence input

        // Removing spaces in sentence
        sentence = sentence.replaceAll("\\s+","").toLowerCase();

        System.out.println("Stating with: " + sentence);
        System.out.println("Final Result: " + Calculator.calc(sentence));
    }
}