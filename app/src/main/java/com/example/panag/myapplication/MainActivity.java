package com.example.panag.myapplication;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.math.BigDecimal;
import java.util.Stack;
import java.lang.Math;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.cosh;
import static java.lang.Math.log;
import static java.lang.Math.log10;
import static java.lang.Math.sin;
import static java.lang.Math.sinh;
import static java.lang.Math.tan;
import static java.lang.Math.tanh;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    Stack<String> globalStack=new Stack<>();
    int bracketCount=0;
    String[] errorList={"Error: Bad Syntax", "Error: Bad Factorial","Error: Division by Zero","Error: Bad Angle","Error: Bad Logarithm"};
    int errorCode=-1;
    boolean shiftState=false;
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

    public double asinh(double x)
    {
        return Math.log(x + Math.sqrt(x*x + 1.0));
    }

    public double acosh(double x)
    {
        return Math.log(x + Math.sqrt(x*x - 1.0));
    }

    public double atanh(double x)
    {
        return 0.5*Math.log( (x + 1.0) / (1.0 - x ) );
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

    public boolean isMultiplicativeBack(char x) { //e.g s(5)x must recognize ) as multiplication
        if (isOperand(x) || x==')') {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isMultiplicativeFront(char x) { //e.g x(5) must recognize ( as multiplication
        if (isOperand(x) || x=='(') {
            return true;
        }
        else {
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
                    tempExpression+=String.valueOf(PI);
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
            tempExpression = tempExpression.replace("arcs(", "S(");
            tempExpression = tempExpression.replace("arcc(", "C(");
            tempExpression = tempExpression.replace("arct(", "T(");
            tempExpression = tempExpression.replace("arch(", "H(");
            tempExpression = tempExpression.replace("arci(", "I(");
            tempExpression = tempExpression.replace("arcj(", "J(");
            tempExpression = tempExpression.replace("abs(", "A(");
            tempExpression = tempExpression.replace("log(", "L(");
            tempExpression = tempExpression.replace("ln(", "N(");
            tempExpression = tempExpression.replace("sec(", "α(");
            tempExpression = tempExpression.replace("csc(", "β(");
            tempExpression = tempExpression.replace("cot(", "γ(");
            tempExpression = tempExpression.replace("sech(", "δ(");
            tempExpression = tempExpression.replace("csch(", "ε(");
            tempExpression = tempExpression.replace("coth(", "ζ(");
            int counter = 1;
            while (counter < tempExpression.length() - 1) {
                if (tempExpression.charAt(counter) == 's' || tempExpression.charAt(counter) == 'c' || tempExpression.charAt(counter) == 't' || tempExpression.charAt(counter) == 'h' || tempExpression.charAt(counter) == 'i' || tempExpression.charAt(counter) == 'j' || tempExpression.charAt(counter) == 'S' || tempExpression.charAt(counter) == 'C' || tempExpression.charAt(counter) == 'T' || tempExpression.charAt(counter) == 'H' || tempExpression.charAt(counter) == 'I' || tempExpression.charAt(counter) == 'J') {
                    if (isMultiplicativeBack(tempExpression.charAt(counter - 1))) {
                        tempExpression = tempExpression.substring(0, counter) + "*" + tempExpression.substring(counter, tempExpression.length());
                    }
                    counter++;
                } else if (tempExpression.charAt(counter) == ')') {
                    if (isMultiplicativeFront(tempExpression.charAt(counter + 1))) {
                        tempExpression = tempExpression.substring(0, counter + 1) + "*" + tempExpression.substring(counter + 1, tempExpression.length());
                    }
                } else if (tempExpression.charAt(counter) == '(') {
                    if (isMultiplicativeBack(tempExpression.charAt(counter - 1))) {
                        tempExpression = tempExpression.substring(0, counter) + "*" + tempExpression.substring(counter, tempExpression.length());
                    }
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
        isExpression=isExpression&&expression.matches("^(([(]*([+\\-]?[(]*([ctshijCTSHIJALNαβγδεζ][(]+[-]?)*\\d+((.\\d+)|!)?([)][!]?)*))(([+^\\-/*)(]|((([+^\\-/*)(])?)([ctshijCTSHIJALNαβγδεζ][(]+[-]?)+))[(]*\\d+((.\\d+)|!)?([)][!]?)*)*)$");
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
                tempStack.push(String.valueOf(round(sin(Double.parseDouble(globalStack.pop())),10)));
            }
            else if (tempValue.equals("c")) {
                tempStack.push(String.valueOf(round(cos(Double.parseDouble(globalStack.pop())),10)));
            }
            else if (tempValue.equals("t")) {
                double x=Double.parseDouble(globalStack.pop());
                if (x!=0 && ((x-round(PI/2,10))/round(PI,10)==Math.floor((x-round(PI/2,10))/round(PI,10)) || (x+round(PI/2,10))/round(PI,10)==Math.floor((x+round(PI/2,10))/round(PI,10)))) {
                    tempStack.push("1");
                    errorCode=3;
                }
                else {
                    tempStack.push(String.valueOf(round(tan(x),10)));
                }
            }
            else if (tempValue.equals("h")) {
                tempStack.push(String.valueOf(round(Math.sinh(Double.parseDouble(globalStack.pop())), 10)));
            }
            else if (tempValue.equals("i")) {
                tempStack.push(String.valueOf(round(Math.cosh(Double.parseDouble(globalStack.pop())),10)));
            }
            else if (tempValue.equals("j")) {
                tempStack.push(String.valueOf(round(Math.tanh(Double.parseDouble(globalStack.pop())),10)));
            }
            else if (tempValue.equals("S")) {
                double x=Double.parseDouble(globalStack.pop());
                if (x>1 || x<-1) {
                    tempStack.push("1");
                    errorCode=3;
                }
                else {
                    tempStack.push(String.valueOf(round(Math.asin(x),10)));
                }
            }
            else if (tempValue.equals("C")) {
                double x=Double.parseDouble(globalStack.pop());
                if (x>1 || x<-1) {
                    tempStack.push("1");
                    errorCode=3;
                }
                else {
                    tempStack.push(String.valueOf(round(Math.acos(x),10)));
                }
            }
            else if (tempValue.equals("T")) {
                tempStack.push(String.valueOf(round(Math.atan(Double.parseDouble(globalStack.pop())),10)));
            }
            else if (tempValue.equals("H")) {
                tempStack.push(String.valueOf(round(asinh(Double.parseDouble(globalStack.pop())),10)));
            }
            else if (tempValue.equals("I")) {
                double x=Double.parseDouble(globalStack.pop());
                if (x<1) {
                    tempStack.push("1");
                    errorCode=3;
                }
                else {
                    tempStack.push(String.valueOf(round(acosh(x),10)));
                }
            }
            else if (tempValue.equals("J")) {
                double x=Double.parseDouble(globalStack.pop());
                if (x<-1 || x>1) {
                    tempStack.push("1");
                    errorCode=3;
                }
                else {
                    tempStack.push(String.valueOf(round(atanh(x),10)));
                }
            }
            else if (tempValue.equals("A")) {
                double x=Double.parseDouble(globalStack.pop());
                tempStack.push(String.valueOf(abs(x)));
                }
            else if (tempValue.equals("L")) {
                double x=Double.parseDouble(globalStack.pop());
                if (x>0) {
                    tempStack.push(String.valueOf(log10(x)));
                }
                else {
                    errorCode=4;
                    tempStack.push("1");
                }
            }
            else if (tempValue.equals("N")) {
                double x=Double.parseDouble(globalStack.pop());
                if (x>0) {
                    tempStack.push(String.valueOf(log(x)));
                }
                else {
                    errorCode=4;
                    tempStack.push("1");
                }
            }
            else if (tempValue.equals("α")) {
                double x=cos(Double.parseDouble(globalStack.pop()));
                if (x!=0) {
                    tempStack.push(String.valueOf(1/x));
                }
                else {
                    errorCode=3;
                    tempStack.push("1");
                }
            }
            else if (tempValue.equals("β")) {
                double x=sin(Double.parseDouble(globalStack.pop()));
                if (x!=0) {
                    tempStack.push(String.valueOf(1/x));
                }
                else {
                    errorCode=3;
                    tempStack.push("1");
                }
            }
            else if (tempValue.equals("γ")) {
                double x=Double.parseDouble(globalStack.pop());
                if (x!=0 && ((x-round(PI/2,10))/round(PI,10)==Math.floor((x-round(PI/2,10))/round(PI,10)) || (x+round(PI/2,10))/round(PI,10)==Math.floor((x+round(PI/2,10))/round(PI,10)))) {
                    tempStack.push("1");
                    errorCode=3;
                }
                else {
                    x=tan(x);
                    if (x!=0) {
                        tempStack.push(String.valueOf(1/tan(x)));
                    }
                    else {
                        errorCode=3;
                        tempStack.push("1");
                    }
                }
            }
            else if (tempValue.equals("δ")) {
                double x=sinh(Double.parseDouble(globalStack.pop()));
                if (x!=0) {
                    tempStack.push(String.valueOf(1/x));
                }
                else {
                    errorCode=3;
                    tempStack.push("1");
                }
            }
            else if (tempValue.equals("ε")) {
                double x=cosh(Double.parseDouble(globalStack.pop()));
                if (x!=0) {
                    tempStack.push(String.valueOf(1/x));
                }
                else {
                    errorCode=3;
                    tempStack.push("1");
                }
            }
            else if (tempValue.equals("ζ")) {
                double x=tanh(Double.parseDouble(globalStack.pop()));
                if (x!=0) {
                    tempStack.push(String.valueOf(1/x));
                }
                else {
                    errorCode=3;
                    tempStack.push("1");
                }
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
        Button self=findViewById(R.id.buttonsin);
        label.append(self.getText()+"(");
    }
    public void onButtonTapCos(View v) {
        TextView label=findViewById(R.id.label);
        Button self=findViewById(R.id.buttoncos);
        label.append(self.getText()+"(");
    }
    public void onButtonTapTan(View v) {
        TextView label=findViewById(R.id.label);
        Button self=findViewById(R.id.buttontan);
        label.append(self.getText()+"(");
    }
    public void onButtonTapSinh(View v) {
        TextView label=findViewById(R.id.label);
        Button self=findViewById(R.id.buttonsinh);
        label.append(self.getText()+"(");
    }
    public void onButtonTapCosh(View v) {
        TextView label=findViewById(R.id.label);
        Button self=findViewById(R.id.buttoncosh);
        label.append(self.getText()+"(");
    }
    public void onButtonTapTanh(View v) {
        TextView label=findViewById(R.id.label);
        Button self=findViewById(R.id.buttontanh);
        label.append(self.getText()+"(");
    }
    public void onButtonTapSec(View v) {
        TextView label=findViewById(R.id.label);
        Button self=findViewById(R.id.buttonsec);
        label.append(self.getText()+"(");
    }
    public void onButtonTapCosec(View v) {
        TextView label=findViewById(R.id.label);
        Button self=findViewById(R.id.buttoncsc);
        label.append(self.getText()+"(");
    }
    public void onButtonTapCot(View v) {
        TextView label=findViewById(R.id.label);
        Button self=findViewById(R.id.buttoncot);
        label.append(self.getText()+"(");
    }
    public void onButtonTapSech(View v) {
        TextView label=findViewById(R.id.label);
        Button self=findViewById(R.id.buttonsech);
        label.append(self.getText()+"(");
    }
    public void onButtonTapCosech(View v) {
        TextView label=findViewById(R.id.label);
        Button self=findViewById(R.id.buttoncsch);
        label.append(self.getText()+"(");
    }
    public void onButtonTapCoth(View v) {
        TextView label=findViewById(R.id.label);
        Button self=findViewById(R.id.buttoncoth);
        label.append(self.getText()+"(");
    }
    public void onButtonTapAbs(View v) {
        TextView label=findViewById(R.id.label);
        label.append("abs(");
    }
    public void onButtonTapLog(View v) {
        TextView label=findViewById(R.id.label);
        label.append("log(");
    }
    public void onButtonTapLn(View v) {
        TextView label=findViewById(R.id.label);
        label.append("ln(");
    }
    public void onButtonTapShift(View v) {
        shiftState=!shiftState;
        Button shift=findViewById(R.id.buttonsin);
        if (shiftState) {
            shift.setText("arcsin");
        }
        else {
            shift.setText("sin");
        }
        shift=findViewById(R.id.buttoncos);
        if (shiftState) {
            shift.setText("arccos");
        }
        else {
            shift.setText("cos");
        }
        shift=findViewById(R.id.buttontan);
        if (shiftState) {
            shift.setText("arctan");
        }
        else {
            shift.setText("tan");
        }
        shift=findViewById(R.id.buttonsinh);
        if (shiftState) {
            shift.setText("arcsinh");
        }
        else {
            shift.setText("sinh");
        }
        shift=findViewById(R.id.buttoncosh);
        if (shiftState) {
            shift.setText("arccosh");
        }
        else {
            shift.setText("cosh");
        }
        shift=findViewById(R.id.buttontanh);
        if (shiftState) {
            shift.setText("arctanh");
        }
        else {
            shift.setText("tanh");
        }
        shift=findViewById(R.id.buttonsec);
        if (shiftState) {
            shift.setText("arcsec");
        }
        else {
            shift.setText("sec");
        }
        shift=findViewById(R.id.buttoncsc);
        if (shiftState) {
            shift.setText("arccsc");
        }
        else {
            shift.setText("csc");
        }
        shift=findViewById(R.id.buttoncot);
        if (shiftState) {
            shift.setText("arccot");
        }
        else {
            shift.setText("cot");
        }
        shift=findViewById(R.id.buttonsech);
        if (shiftState) {
            shift.setText("arcsech");
        }
        else {
            shift.setText("sech");
        }
        shift=findViewById(R.id.buttoncsch);
        if (shiftState) {
            shift.setText("arccsch");
        }
        else {
            shift.setText("csch");
        }
        shift=findViewById(R.id.buttoncoth);
        if (shiftState) {
            shift.setText("arccoth");
        }
        else {
            shift.setText("coth");
        }
    }
    public void onButtonTapEqual(View v) {
        TextView label=findViewById(R.id.label);
        parse(label);
    }
    public void onButtonTapFactorial(View v) {
        TextView label=findViewById(R.id.label);
        label.append("!");
    }
    public void showlayout(View v) {
        ConstraintLayout lay1=findViewById(R.id.maintab);
        if (lay1.getVisibility()==View.GONE) {
            lay1.setVisibility(View.VISIBLE);
        }
        else {
            lay1.setVisibility(View.GONE);
        }
    }
}
