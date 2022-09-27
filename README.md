# calculator
   A math sentence calculator made 100% by me using Java.
---
## How it works
&nbsp;&nbsp;This calculator only works with numbers, multiplication, division, exponentiation, subtraction, addition and parentheses. <br>
Its heart is `calc(String sentence)`, inside this function the sentence will pass in `isValidSentence(String sentence)` which will check if the passed sentence is valid or not. It's important to know how 'isValidSentence' works before using the program, so have a look at the code!
<br> <br>
In general, it will only accept these characters:
1. Numbers in general
2. "." for floating numbers
3. "(" and ")"
4. "+" and "-"
5. "\*" for multiplication
6. "\\" for division
7. "^" for exponentiation

Your system works with several 'for's reading the sentence several times, separating the terms and operating them in the order of operation.
