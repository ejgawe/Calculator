import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        System.out.println(calc(input));
    }

    public static String calc(String input) {
        boolean isArabicSystem = Character.isDigit(input.charAt(0));
        Expression exp = parse(input.trim(), isArabicSystem);
        int resInt = getResult(exp);
        return isArabicSystem ? String.valueOf(resInt) : Converter.toRoman(resInt);
    }

    private static int getResult(Expression exp) {
        Operation operation = exp.getOperation();
        Integer arg1 = exp.getArg1();
        Integer arg2 = exp.getArg2();
        return switch (operation) {
            case PLUS -> arg1 + arg2;
            case MINUS -> arg1 - arg2;
            case DIVISION -> arg1 / arg2;
            case MULTIPLICATION -> arg1 * arg2;
        };
    }

    private static Expression parse(String input, boolean isArabicSystem) {
        char[] inputArray = input.toCharArray();
        if (isArabicSystem)
            return getExpressionInArabicSystem(inputArray);
        else
            return getExpressionInRomanSystem(inputArray);
    }

    private static Expression getExpressionInArabicSystem(char[] inputArray) {
        Expression exp = new Expression();
        for (int i = 0; i < inputArray.length; i++) {
            char c = inputArray[i];
            if (c == ' ') continue;
            else if (c == '-' || c == '+' || c == '/' || c == '*') {
                exp.setOperation(c);
                continue;
            }
            int arg;
            if (!Character.isDigit(c))
                throw new CalculatorException("Использование одновременно двух систем счисления");
            if (c == '1' && i + 1 < inputArray.length && inputArray[i + 1] == '0') {
                arg = 10;
                i++;
            } else if (c == '0') {
                throw new CalculatorException("Только целые числа от 1 до 10 включительно");
            } else arg = Integer.parseInt(String.valueOf(c));
            exp.setArg(arg);
        }
        return exp;
    }

    private static Expression getExpressionInRomanSystem(char[] inputArray) {
        Expression exp = new Expression();
        for (int i = 0; i < inputArray.length; i++) {
            char c = inputArray[i];
            StringBuilder builder = new StringBuilder();
            Integer arg;
            if (c == ' ') continue;
            else if (c == '-' || c == '+' || c == '/' || c == '*') {
                exp.setOperation(c);
                continue;
            }
            int j = i;
            while (j < inputArray.length) {
                char romanChar = inputArray[j];
                if (Character.isDigit(romanChar))
                    throw new CalculatorException("Использование одновременно двух систем счисления");
                if (romanChar == '+' || romanChar == '-' || romanChar == '/' || romanChar == '*' || romanChar == ' ')
                    break;
                builder.append(romanChar);
                j++;
            }
            i = j - 1;
            arg = Converter.toArabic(builder.toString());
            exp.setArg(arg);
        }
        return exp;
    }
}

class Converter {

    private static final Map<String, Integer> map = new HashMap<>();

    static {
        map.put("I", 1);
        map.put("II", 2);
        map.put("III", 3);
        map.put("IV", 4);
        map.put("V", 5);
        map.put("VI", 6);
        map.put("VII", 7);
        map.put("VIII", 8);
        map.put("IX", 9);
        map.put("X", 10);
    }

    static String toRoman(int arabicNum) {
        if (arabicNum < 1) throw new CalculatorException("Невозможно перевести в римскую систему число: " + arabicNum);
        StringBuilder builder = new StringBuilder();
        int[] values = {100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romanNums = {"C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int count = 0;
        while (arabicNum > 0) {
            while (arabicNum >= values[count]) {
                arabicNum -= values[count];
                builder.append(romanNums[count]);
            }
            count++;
        }
        return builder.toString();
    }

    static Integer toArabic(String romanNum) {
        if (!map.containsKey(romanNum))
            throw new CalculatorException("Невозможно перевести в арабскую систему число: " + romanNum);
        return map.get(romanNum);
    }
}

class Expression {
    private Integer arg1;
    private Integer arg2;
    private Operation operation;

    public void setArg(Integer arg) {
        if (this.arg1 != null && this.arg2 != null) {
            throw new CalculatorException("Должно быть только два операнда - числа от 1 до 10");
        } else if (arg1 == null) {
            this.arg1 = arg;
        } else {
            this.arg2 = arg;
        }
    }

    public Integer getArg1() {
        return arg1;
    }

    public Integer getArg2() {
        return arg2;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Character operation) {
        if (this.operation != null) throw new CalculatorException("Неверный формат: должен быть только один оператор");
        Operation[] values = Operation.values();
        for (Operation value : values) {
            if (value.getCharOperation() == operation) {
                this.operation = value;
                break;
            }
        }
    }
}

enum Operation {
    PLUS('+'), MINUS('-'), DIVISION('/'), MULTIPLICATION('*');
    private final Character charOperation;

    Operation(Character charOperation) {
        this.charOperation = charOperation;
    }

    public Character getCharOperation() {
        return charOperation;
    }
}

class CalculatorException extends RuntimeException {
    public CalculatorException(String message) {
        super(message);
    }
}

