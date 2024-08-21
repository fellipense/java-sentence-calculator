import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Calculator {

    static char[] numericChars = { '0','1','2','3','4','5','6','7','8','9' }; // Numbers.

    static char[] firstOperators = { '*', '/', '^' }; // Operators that we need to calculate before addition and subtraction.

    static char[] lastOperators = { '+', '-' }; // Addition and subtraction operators.

    static char[] operatorsChars = { '+', '-', '*', '/', '^'}; // All operators.

    static DecimalFormat df = new DecimalFormat("#.00000");


    public static void main(String[] sentence) {
	
	try {
		System.out.println("Final result: " + String.valueOf(calc(sentence[0])).replace(".0", ""));
	} catch (Exception e) {
		e.printStackTrace();
	}

    }

    // Calculates a valid sentence (validated by 'isValidSentence()').
    public static double calc(String sentence) throws Exception {

        sentence = arrangeOperators(sentence).replace(".0", "");

        if (isValidSentence(sentence)){

            double result = 0; // It will save the final result.

            System.out.println("Calculating : " + sentence);

            // First: solve all brackets (consider Bracket == Parentheses) until there's none left.
            while (haveBrackets(sentence)){

                // Here I find the first sentence in parentheses that I need to calculate before continue.
                int counter = 0; // Counter to get the index of the sentence in the for loop.
                int openIndex = 0; // Where the "sub" sentence starts.
                int closeIndex = 0; // Where it finishes.

                for (char c : sentence.toCharArray()) {
                    if (c == '('){
                        openIndex = counter;
                    }
                    else if (c ==')'){
                        closeIndex = counter;
                        String beforeOpen = "";

                        // Basically: "x + (y)" == "x + y" and "x(y)" == "x * y".
                        try {
                            char c1 = sentence.toCharArray()[openIndex - 1];
                            if (!arrayContains(operatorsChars, c1)
                                    && (arrayContains(numericChars, c1) || c1 == ')')){ beforeOpen = "*"; }
                        }catch (Exception e){};

                        // Here I turn sentence into the current sentence but replacing the brackets with its result.
                        String start = sentence.substring(0, openIndex) + beforeOpen;
                        String middle = sentence.substring(openIndex, closeIndex + 1);
                        String end = sentence.substring(closeIndex + 1);

                        String middleWithNoBrackets = middle.replace("(", "").replace(")", "");

                        sentence = (start + calc(middleWithNoBrackets) + end).replace(".0", "");
                        System.out.println("New         : " + sentence);
                        break;
                    }
                    counter++;
                }
            }

            // Second: solve all exponentiation.
            while (haveExponent(sentence)){

                // In case "x^y":
                String xTerm = ""; // x
                String yTerm = ""; // y

                int openIndex = 0; // Where this exponentiation starts.

                // We need to do it from right to the left, so:
                int counter = sentence.length() - 1;
                char prevChar = ' '; // Previous char.
                boolean foundExpo = false; // When I reach the exponent, I will start searching the xTerm.

                // Starting the right to left reading.
                while (counter >= 0){

                    // Searching for terms works in this way:
                    // It will read char per char in the sentence, knowing the current char and the previous one;
                    // Each term are separated by an operator, so when it finds an operator it will know that the term ends/starts there;
                    // If the current char is a number and the previous is an operator (like "..3+34.."),
                    // it will realize that the last term that was being built is not in appropriate and will restart the search (Term = "").

                    char c = sentence.toCharArray()[counter]; // Current char.

                    if (!foundExpo){ // Searching yTerm.
                        if (arrayContains(operatorsChars, prevChar) && arrayContains(numericChars, c)){
                            yTerm = "";
                            yTerm = String.valueOf(c);
                        }
                        else if (c == '^'){
                            foundExpo = true;
                        }
                        else{
                            yTerm = c + yTerm;
                        }
                    }else{ // Searching xTerm
                        if (arrayContains(lastOperators, c)){
                            xTerm = c + xTerm;
                            openIndex = counter;
                            break;
                        }
                        else if (arrayContains(firstOperators, c)) {break;}
                        xTerm = c + xTerm;
                    }
                    prevChar = c;
                    counter--;
                }

                double res = Math.pow(Double.parseDouble(xTerm), Double.parseDouble(yTerm)); // Finally doing the exponentiation between "x" and "y".
                res = Double.parseDouble(df.format(res).replace(",", "."));

                // Here im replacing the exponentiation part for its result.
                String start = sentence.substring(0, openIndex) + "+";
                String middle = String.valueOf(res);
                String end = sentence.substring(openIndex + xTerm.length() + yTerm.length() + 1);

                sentence = arrangeOperators(start + middle + end).replace(".0", "");
                System.out.println("New         : " + sentence);
            }

            // Third: solve all multiplications and divisions
            while (haveFirstOperators(sentence)){

                // Here I'll do the same terms thing

                String xTerm = "";
                String yTerm = "";

                boolean foundOperator = false;
                char operator = ' ';
                int counter = 0;
                int openIndex = 0;

                for (char c : sentence.toCharArray()){
                    if (!foundOperator){
                        if (arrayContains(firstOperators, c)) { foundOperator = true; operator = c; }
                        else if (arrayContains(lastOperators, c)) { xTerm = String.valueOf(c); openIndex = counter; }
                        else { xTerm += c; }
                    }
                    else {
                        if (arrayContains(operatorsChars, c)){
                            break;
                        }
                        else {
                            yTerm += c;
                        }
                    }
                    counter++;
                }

                double x = Double.parseDouble(xTerm);
                double y = Double.parseDouble(yTerm);
                double res = 0.0;

                if (operator == '*'){
                    res = x * y;
                }
                else {
                    res = x / y;
                }

                res = Double.parseDouble(df.format(res).replace(",", "."));

                String start = sentence.substring(0, openIndex) + "+";
                String middle = String.valueOf(res);
                String end = sentence.substring(openIndex + xTerm.length() + yTerm.length() + 1);

                sentence = arrangeOperators(start + middle + end).replace(".0", "");
                System.out.println("New         : " + sentence);
            }

            // After solving all brackets, exponentiation, multiplications and divisions, let's do the addiction between all terms.
            List<String> termsList = new ArrayList<String>();

            sentence = arrangeOperators(sentence).replace(".0", "");

            String termBuilder = "";
            for (char c : sentence.toCharArray()){
                if (arrayContains(operatorsChars, c) && termBuilder != ""){
                    termsList.add(termBuilder);
                    termBuilder = "";
                }
                termBuilder += c;
            }
            termsList.add(termBuilder);

            // Converting the terms list to a terms array
            String[] termsArray = new String[termsList.size()];
            termsArray = termsList.toArray(termsArray);

            for (String term : termsArray){
                result += Double.parseDouble(term);
            }


            result = Double.parseDouble(df.format(result).replace(",", "."));
            System.out.println("Throwing    : " + String.valueOf(result).replace(".0", ""));
            return result;
        }else{
            throw new Exception("Sentence '" + sentence + "' is invalid!");
        }
    }


    // Replaces repeated operators like "x++y", "x+-y", "x--y" etc by a unique, following the logic that same operators ("++" or "--") returns "+" and different ("+-" or "-+") returns "-"
    public static String arrangeOperators(String sentence) throws Exception {

        char lastChar = ' ';
        String result = "";
        for (char c: sentence.toCharArray()){
            if (arrayContains(lastOperators, c)){
                if (arrayContains(lastOperators, lastChar)){
                    if (c == lastChar) { result = result.substring(0, result.length() - 1) + '+'; }
                    else { result = result.substring(0, result.length() - 1) + '-'; }
                }
                else {
                    result += c;
                }
            }
            else {
                result += c;
            }

            lastChar = c;
        }
        return result;

    }


    // Checks if sentence is valid or not
    public static boolean isValidSentence(String sentence){

        // Every time opening brackets: brackets +1
        // Every time closing brackets: brackets -1
        // So, if brackets < 0 sometime: the sentence will be invalid
        int brackets = 0;

        int numericCharsCounter = 0;
        char prevChar = ' ';

        char[] allowedChars = {
                '0','1','2','3','4','5','6','7','8','9',
                '(',')','+','-','*','^','/', '.'
        };

        for (char c : sentence.toCharArray()) {

            if (!arrayContains(allowedChars, c)) { return false; }

            // Avoiding sentence with no numbers
            if (arrayContains(numericChars, c)) { numericCharsCounter++; }

            // Avoiding first operators duplications ("x**y", "x//y", "x/*y" etc)
            if (arrayContains(firstOperators, prevChar) && arrayContains(firstOperators, c)) { return false; }

            // Avoiding sentence starting by first operator
            if (prevChar == ' ' && arrayContains(firstOperators, c)) { return false; }

            // Avoiding closing without opening or opening without closing ( "(()..." or "())..." )
            if (c == '(') {brackets++;}
            if (c == ')') {brackets--;}

            if (brackets < 0) { return false; }

            prevChar = c;
        }

        // We need have at least 1 number and if brackets aren't 0 our sentence won't be valid.
        return numericCharsCounter > 0 && brackets == 0;
    }


    // Checks if sentence have brackets
    public static boolean haveBrackets(String sentence){
        for (char c : sentence.toCharArray()){
            if (c == '(' || c == ')') { return true; }
        }
        return false;
    }


    // Checks if sentence have exponentiation
    public static boolean haveExponent(String sentence){
        for (char c : sentence.toCharArray()){
            if (c == '^') { return true; }
        }
        return false;
    }


    // Checks if sentence have first operators
    public static boolean haveFirstOperators(String sentence){
        for (char c : sentence.toCharArray()){
            if (arrayContains(firstOperators, c)){return true;}
        }
        return false;
    }


    // Checks if a char array contain informed char
    public static Boolean arrayContains(char[] array, char key){
        for (char item : array){
            if (item == key){return true;}
        }
        return false;
    }

}
