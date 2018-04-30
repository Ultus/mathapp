package com.example.panag.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Stack;
import java.lang.Math;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    Stack<String> globalStack=new Stack<>();
    int bracketCount=0;
    String[] errorList={"Error: Bad Syntax", "Error: Bad Factorial","Error: Division by 0"};
    int errorCode=-1;
    public void setGlobalStack(Stack<String> stack) { //Note argument should be in reverse
        while (!stack.isEmpty()) {
            globalStack.push(stack.pop());
        }
    }
    public static double round(double value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places,BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
    public boolean isOperand(String x) {
        try {
            Double.parseDouble(x);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isOperand(char x) {
        try {
            Double.parseDouble(String.valueOf(x));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String editExpression(String expression) {
        for (int i=0;i<expression.length();i++) {
            String tempExpression=expression;
            if (expression.charAt(i)=='π' || expression.charAt(i)=='e') {
                tempExpression=String.valueOf(expression.subSequence(0,i));
                if (i>0) {
                    if (isOperand(expression.charAt(i-1))) {
                        tempExpression+="*";
                    }
                }
                if (expression.charAt(i)=='e') {
                    tempExpression+=String.valueOf(Math.E);
                }
                else {
                    tempExpression+=String.valueOf(Math.PI);
                }
                if (i<expression.length()-1) {
                    if (isOperand(expression.charAt(i+1))) {
                        tempExpression+="*";
                    }
                    tempExpression+=String.valueOf(expression.subSequence(i+1,expression.length()));
                }
            }
            tempExpression = tempExpression.replace("sin(", "s(");
            tempExpression = tempExpression.replace("cos(", "c(");
            tempExpression = tempExpression.replace("tan(", "t(");
            tempExpression = tempExpression.replace("sinh(", "h(");
            tempExpression = tempExpression.replace("cosh(", "i(");
            tempExpression = tempExpression.replace("tanh(", "j(");
            int counter = 1, brackCounter = 2;
            while (counter < tempExpression.length() - 1) {
                if (tempExpression.charAt(counter) == 's' || tempExpression.charAt(counter) == 'c' || tempExpression.charAt(counter) == 't') {
                    if (isOperand(tempExpression.charAt(counter - 1))) {
                        tempExpression = tempExpression.substring(0, counter) + "*" + tempExpression.substring(counter, tempExpression.length());
                    }
                    brackCounter = 1;
                    counter++;
                } else if (tempExpression.charAt(counter) == ')') {
                    brackCounter--;
                    if (isOperand(tempExpression.charAt(counter + 1))) {
                        tempExpression = tempExpression.substring(0, counter + 1) + "*" + tempExpression.substring(counter + 1, tempExpression.length());
                    }
                } else if (tempExpression.charAt(counter) == '(') {
                    brackCounter++;
                    if (isOperand(tempExpression.charAt(counter - 1))) {
                        tempExpression = tempExpression.substring(0, counter) + "*" + tempExpression.substring(counter, tempExpression.length());
                    }
                }
                if (brackCounter == 0) {
                    if (isOperand(tempExpression.charAt(counter + 1))) {
                        tempExpression = tempExpression.substring(0, counter + 1) + "*" + tempExpression.substring(counter + 1, tempExpression.length());
                    }
                    brackCounter = 2;
                }
                counter++;
            }
            expression=tempExpression;
        }
        expression="("+expression+")"; //program assumes loops of brackets
        return expression;
    }

    public String factorial(String x) {
        double val=Double.parseDouble(x);
        if (val<0) {
            errorCode=1;
        }
        if (val<=1)
            return "1";
        else
            return String.valueOf(val*Double.parseDouble(factorial(String.valueOf(val-1))));
    }
    public Stack<String> invertStack(Stack<String> stack) {
        Stack<String> newStack=new Stack<>();
        while (!stack.isEmpty()) {
            newStack.push(stack.pop());
        }
        return newStack;
    }
    public Stack<String> StringToStack(String expression) {
        int end=-1,i=expression.length()-1;
        Stack<String> stream=new Stack<>();
        while (i>=0) {
            if (isOperand(expression.charAt(i)) || expression.charAt(i)=='.') {
                if (end<0) {
                    if (i>0) {
                        end=i;
                    }
                    else {
                        stream.push(String.valueOf(expression.charAt(i)));
                    }
                }
                else {
                    if (i==0) {
                        stream.push(expression.substring(i,end+1));
                    }
                }

            }
            else {
                if (end>=0) {
                    stream.push(expression.substring(i+1,end+1));
                    end = -1;
                }
                stream.push(String.valueOf(expression.charAt(i)));
            }
            i--;
        }
        return stream;
    }

    public boolean isValid(String expression) {
        boolean isExpression=true;
        int count=0;
        for (int i=0;i<expression.length();i++) { //check bracket count
            if (expression.charAt(i)=='(')
                {
                    count++;
                    bracketCount++;
            }
            else if (expression.charAt(i)==')') {
                if (count==0)
                    isExpression=false;
                else
                    count--;
            }
        }
        if (count!=0)
            isExpression=false;
        isExpression=isExpression&&expression.matches("^(([(]*([+\\-]?([ctshij][(]+)*\\d+((.\\d+)|!)?([)][!]?)*))(([+^\\-/*)(]|((([+^\\-/*)(])?)([ctshij][(]+)+))[(]*\\d+((.\\d+)|!)?([)][!]?)*)*)$");
        return isExpression;
    }

    public void computeBrackets() {
        Stack<String> tempStack=new Stack<>();
        String tempValue;
        int currentCount=0;
        while (!globalStack.isEmpty()) {
            tempValue=globalStack.pop();
            if (tempValue.equals("(")) {
                currentCount++;
                if (currentCount==bracketCount) {
                    tempValue=String.valueOf(computeExpression());
                    bracketCount--;
                }
            }
            tempStack.push(tempValue);
        }
        setGlobalStack(tempStack);
        if (bracketCount>0) {
            computeBrackets();
        }
    }

    public String computeExpression() {
        computeTrig();
        computeFactorial();
        computePowers();
        computeMultDiv();
        computeAddSub();
        return globalStack.pop();
    }

    public void computeTrig() {
        String tempValue;
        Stack<String> tempStack=new Stack<>();
        while (!globalStack.peek().equals(")")) {
            tempValue=globalStack.pop();
            if (tempValue.equals("s")) {
                tempStack.push(String.valueOf(round(Math.sin(Double.parseDouble(globalStack.pop())),10)));
            }
            else if (tempValue.equals("c")) {
                tempStack.push(String.valueOf(round(Math.cos(Double.parseDouble(globalStack.pop())),10)));
            }
            else if (tempValue.equals("t")) {
                tempStack.push(String.valueOf(round(Math.tan(Double.parseDouble(globalStack.pop())),10)));
            }
            else if (tempValue.equals("h")) {
                tempStack.push(String.valueOf(round(Math.sinh(Double.parseDouble(globalStack.pop())),10)));
            }
            else if (tempValue.equals("i")) {
                tempStack.push(String.valueOf(round(Math.cosh(Double.parseDouble(globalStack.pop())),10)));
            }
            else if (tempValue.equals("j")) {
                tempStack.push(String.valueOf(round(Math.tanh(Double.parseDouble(globalStack.pop())),10)));
            }
            else {
                tempStack.push(tempValue);
            }
        }
        setGlobalStack(tempStack);
    }
    public void computeFactorial() {
        String tempValue;
        Stack<String> tempStack=new Stack<>();
        while (!globalStack.peek().equals(")")) {
            tempValue=globalStack.pop();
            if (tempValue.equals("!")) {
                tempStack.push(factorial(tempStack.pop()));
            }
            else {
                tempStack.push(tempValue);
            }
        }
        setGlobalStack(tempStack);
    }

    public void computeAddSub() {
        String tempValue;
        double result=0;
        while (!globalStack.peek().equals(")")) {
            tempValue=globalStack.pop();
            if (tempValue.equals("+")) {
                result+=Double.parseDouble(globalStack.pop());
            }
            else if (tempValue.equals("-")) {
                result-=Double.parseDouble(globalStack.pop());
            }
            else {
                result+=Double.parseDouble(tempValue); //Operand without preceding sign
            }
            result=round(result,10);
        }
        globalStack.pop(); //No more need for ")"
        globalStack.push(String.valueOf(result));
    }

    public void computeMultDiv() {
        Stack<String> tempStack=new Stack<>();
        String tempValue;
        while (!globalStack.peek().equals(")")) {
            tempValue=globalStack.pop();
            if (tempValue.equals("*")) {
                tempStack.push(String.valueOf(Double.parseDouble(tempStack.pop())*Double.parseDouble(globalStack.pop())));
            }
            else if (tempValue.equals("/")) {
                double divCheck=Double.parseDouble(globalStack.pop());
                if (divCheck==0) {
                    errorCode=2;
                    tempStack.push("!");
                    tempStack.pop();
                }
                else {
                    tempStack.push(String.valueOf(Double.parseDouble(tempStack.pop())/divCheck));
                }
            }
            else {
                tempStack.push(tempValue);
            }
        }
        setGlobalStack(tempStack);
    }
    public void computePowers() {
        Stack<String> tempStack=new Stack<>();
        String tempValue;
        while (!globalStack.peek().equals(")")){
            tempValue=globalStack.pop();
            if (tempValue.equals("^")) {
                tempStack.push(String.valueOf(Math.pow(Double.parseDouble(tempStack.pop()),Double.parseDouble(globalStack.pop()))));
            }
            else {
                tempStack.push(tempValue);
            }
        }
        setGlobalStack(tempStack);
    }

    public void parse(TextView label) {
        String expression=label.getText().toString();
        expression=editExpression(expression);
        if (isValid(expression)) {
            setGlobalStack(invertStack(StringToStack(expression)));
            computeBrackets();
            if (errorCode<0) {
                label.setText(globalStack.pop());
            }
            else {
                label.setText(errorList[errorCode]);
                errorCode=-1;
            }
        }
        else {
            errorCode=0;
            label.setText(errorList[errorCode]);
            errorCode=-1;
            bracketCount=0; //editExpression gives bracket not computed in invalid expression
        }
    }

    //Calculator Buttons
    public void onButtonTap0(View v) {
        TextView label=findViewById(R.id.label);
        label.append("0");
    }
    public void onButtonTap1(View v) {
        TextView label=findViewById(R.id.label);
        label.append("1");
    }
    public void onButtonTap2(View v) {
        TextView label=findViewById(R.id.label);
        label.append("2");
    }
    public void onButtonTap3(View v) {
        TextView label=findViewById(R.id.label);
        label.append("3");
    }
    public void onButtonTap4(View v) {
        TextView label=findViewById(R.id.label);
        label.append("4");
    }
    public void onButtonTap5(View v) {
        TextView label=findViewById(R.id.label);
        label.append("5");
    }
    public void onButtonTap6(View v) {
        TextView label=findViewById(R.id.label);
        label.append("6");
    }
    public void onButtonTap7(View v) {
        TextView label=findViewById(R.id.label);
        label.append("7");
    }
    public void onButtonTap8(View v) {
        TextView label=findViewById(R.id.label);
        label.append("8");
    }
    public void onButtonTap9(View v) {
        TextView label=findViewById(R.id.label);
        label.append("9");
    }
    public void onButtonTapAddition(View v) {
        TextView label=findViewById(R.id.label);
        label.append("+");
    }
    public void onButtonTapSubtraction(View v) {
        TextView label=findViewById(R.id.label);
        label.append("-");
    }
    public void onButtonTapMult(View v) {
        TextView label=findViewById(R.id.label);
        label.append("*");
    }
    public void onButtonTapDivision(View v) {
        TextView label=findViewById(R.id.label);
        label.append("/");
    }
    public void onButtonTapOpenBracket(View v) {
        TextView label=findViewById(R.id.label);
        label.append("(");
    }
    public void onButtonTapCloseBracket(View v) {
        TextView label=findViewById(R.id.label);
        label.append(")");
    }
    public void onButtonTapDel(View v) {
        TextView label=findViewById(R.id.label);
        if (label.length()>0) {
            label.setText((label.getText().subSequence(0,label.length()-1)));
        }
    }
    public void onButtonTapFloat(View v) {
        TextView label=findViewById(R.id.label);
        label.append(".");
    }
    public void onButtonTapPow(View v) {
        TextView label=findViewById(R.id.label);
        label.append("^");
    }
    public void onButtonTapCLR(View v) {
        TextView label=findViewById(R.id.label);
        label.setText("");
    }
    public void onButtonTapPI(View v) {
        TextView label=findViewById(R.id.label);
        label.append("π");
    }
    public void onButtonTapE(View v) {
        TextView label=findViewById(R.id.label);
        label.append("e");
    }
    public void onButtonTapSin(View v) {
        TextView label=findViewById(R.id.label);
        label.append("sin(");
    }
    public void onButtonTapCos(View v) {
        TextView label=findViewById(R.id.label);
        label.append("cos(");
    }
    public void onButtonTapTan(View v) {
        TextView label=findViewById(R.id.label);
        label.append("tan(");
    }
    public void onButtonTapSinh(View v) {
        TextView label=findViewById(R.id.label);
        label.append("sinh(");
    }
    public void onButtonTapCosh(View v) {
        TextView label=findViewById(R.id.label);
        label.append("cosh(");
    }
    public void onButtonTapTanh(View v) {
        TextView label=findViewById(R.id.label);
        label.append("tanh(");
    }
    public void onButtonTapEqual(View v) {
        TextView label=findViewById(R.id.label);
        parse(label);
    }
    public void onButtonTapFactorial(View v) {
        TextView label=findViewById(R.id.label);
        label.append("!");
    }
}
