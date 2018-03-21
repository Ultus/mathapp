package com.example.panag.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Global Variables
    Stack<String> globalStack=new Stack<>();
    int bracketCount=0;

    public void setGlobalStack(Stack<String> stack) { //Note argument should be in reverse
        while (!stack.isEmpty()) {
            globalStack.push(stack.pop());
        }
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
        for (int i=0;i<expression.length();i++) {
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
        isExpression=isExpression&&expression.matches("^(([(]*([+\\-]\\d+(.\\d+)?[)]*)|([(]*\\d+(.\\d+)?[)]*))([+^\\-/*)(][(]*\\d+(.\\d+)?[)]*)*)$");
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
                    tempValue=String.valueOf(computeExpression(globalStack);
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

    public String computeExpression(Stack<String> stack) {
        String result;
        computePowers();
        computeMultDiv();
        computeAddSub();
        return result;
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
                tempStack.push(String.valueOf(Double.parseDouble(tempStack.pop())/Double.parseDouble(globalStack.pop())));
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
    boolean handleClosingBracket=false;
    public String compute(Stack<String> stack) {
        Stack<Double> temporaryStack=new Stack<>();
        double currentValue,returnValue=0;
        String operator;
        boolean finishedLoop=false;
        while (!stack.isEmpty() && !stack.peek().equals(")") && !finishedLoop) {
            if (isOperand(stack.peek())) {
                if (handleClosingBracket) {
                    double bracketReturn=Double.parseDouble(stack.pop());
                    String next=null;
                    if (stack.size()>0) {
                        next=stack.peek();
                    }
                    if (stack.size()==0 || next.equals("+") || next.equals("-")) { //Last value must be handled
                        temporaryStack.push(bracketReturn);
                    }
                    else if (isOperand(next)) {
                        stack.push(String.valueOf(bracketReturn*Double.parseDouble(stack.pop())));
                    }
                    else if (next.equals("*")) {
                        stack.pop();
                        stack.push(String.valueOf(bracketReturn*Double.parseDouble(stack.pop())));
                    }
                    else if (next.equals("/")) {
                        stack.pop();
                        stack.push(String.valueOf(bracketReturn/Double.parseDouble(stack.pop())));
                    }
                    else if (next.equals("(")) {
                        stack.pop();
                        stack.push(String.valueOf(bracketReturn*Double.parseDouble(compute(stack))));
                    }
                    else if (next.equals(")")) {
                        stack.pop();
                        temporaryStack.push(bracketReturn);
                        finishedLoop=true;
                    }
                    handleClosingBracket=false;
                }
                else if (stack.size()==1) { //Last value must be handled
                    temporaryStack.push(Double.parseDouble(stack.pop()));
                }
                else {
                    currentValue=Double.parseDouble(stack.pop());
                    operator=stack.pop();
                    switch (operator) {
                        case "+":temporaryStack.push(currentValue);
                            break;
                        case "-":temporaryStack.push(currentValue);
                            String next=stack.pop();
                            if (isOperand(next)) {
                                stack.push(String.valueOf(-Double.parseDouble(next)));
                            }
                            else {
                                stack.push(String.valueOf(-Double.parseDouble(compute(stack))));
                            }
                            break;
                        case "*":if (stack.peek().equals("(")) {
                                stack.pop();
                                stack.push(String.valueOf(currentValue*Double.parseDouble(compute(stack))));
                            }
                            else {
                            stack.push(String.valueOf(currentValue*Double.parseDouble(stack.pop())));
                            }
                            break;
                        case "/":if (stack.peek().equals("(")) {
                            stack.pop();
                            stack.push(String.valueOf(currentValue/Double.parseDouble(compute(stack))));
                            }
                            else {
                            stack.push(String.valueOf(currentValue/Double.parseDouble(stack.pop())));
                            }
                            break;
                        case "(":stack.push(String.valueOf(currentValue*Double.parseDouble(compute(stack))));
                            break;
                        case ")":temporaryStack.push(currentValue);
                            handleClosingBracket=true;
                            finishedLoop=true;
                            break;
                    }
                }
            }
            else {
                operator=stack.pop();
                switch (operator) {
                    case "-":stack.push(String.valueOf(-Double.parseDouble(stack.pop())));
                        break;
                    case "+":
                        break;
                    case "(":stack.push(compute(stack));
                        break;
                }
            }
        }
        while (!temporaryStack.isEmpty()) {
            returnValue+=temporaryStack.pop();
        }
        return String.valueOf(returnValue);
    }

    public void parse(TextView label) {
        String expression=label.getText().toString();
        Stack<String> streamStack=StringToStack(expression);
        if (isValid(expression)) {
            label.setText(compute(computePowers(streamStack)));
        }
        else {
            label.setText("Error: bad syntax");
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
    public void onButtonTapEqual(View v) {
        TextView label=findViewById(R.id.label);
        parse(label);
    }
}