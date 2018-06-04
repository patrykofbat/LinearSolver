import org.ejml.simple.SimpleMatrix;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class LinearProgram {
    private SimpleMatrix inequalities;
    private double[] golaFunctionCoefficients;


    public LinearProgram(SimpleMatrix inequalities, double[] golaFunctionCoefficients){
        this.inequalities = inequalities;
        this.golaFunctionCoefficients = golaFunctionCoefficients;
    }

    private double round(double number){
        BigDecimal bg = new BigDecimal(number);
//        bg = bg.setScale(6, BigDecimal.ROUND_HALF_UP);
        bg = bg.round(new MathContext(6, RoundingMode.HALF_UP));
        return bg.doubleValue();

    }

    private double[] linearFunctionSolve(double[] f1, double[] f2){
        double x;
        double y;

        double[] solution = new double[2];
        if((f2[0] * f1[1]) - (f1[0] * f2 [1]) != 0 && f1[1] != 0) {
            x = ((f2[2] * f1[1]) - (f1[2] * f2[1])) / ((f2[0] * f1[1]) - (f1[0] * f2[1]));
            y = (-1/f1[1])*(f1[0] * x - f1[2]);
            solution[0] = round(x);
            solution[1] = round(y);
            return solution;
        }
        else if(f1[1] == 0){
            x = f1[2]/f1[0];
            y = (-1/f2[1])*(f2[0] * x - f2[2]);
            solution[0] = round(x);
            solution[1] = round(y);
            return solution;
        }
        else
            return null;
    }

    private double[] crossWithAxis(double[] f1, String axis){
        double x;
        double y;
        if(axis.equals("x")){
            if(f1[1] != 0){
                x = 0;
                y = round(f1[2]/f1[1]);
            }
            else
                return null;

        }
        else {
            if(f1[0] !=0){
                y = 0;
                x = round(f1[2]/f1[0]);
            }
            else
                return null;

        }
        double[] solution = {x, y};
        return solution;
    }


    private boolean isAllowed(double[] solution){
        boolean isAllowed = false;
        if(solution == null)
            return false;
        else if(solution[0] < 0)
            return false;
        else{
            for(int i = 0; i< this.inequalities.numRows()-1; i++){
                double[] f1 ={this.inequalities.get(i,0), this.inequalities.get(i,1), this.golaFunctionCoefficients[i]};
                if(round(f1[0] * solution[0] + f1[1] * solution[1]) <= f1[2])
                    isAllowed = true;
                else
                    return false;
            }
            return isAllowed;
        }
    }

    public void run(){
        System.out.println(this.inequalities);
        LinkedList<double[]> solutions = new LinkedList<>();

        for(int i = 0; i<this.inequalities.numRows()-2; i++){
            double[] f1 ={this.inequalities.get(i,0), this.inequalities.get(i,1), this.golaFunctionCoefficients[i]};
            double[] crossX = this.crossWithAxis(f1, "x");
            double[] crossY = this.crossWithAxis(f1, "y");

            if(isAllowed(crossX))
                solutions.add(crossX);
            if(isAllowed(crossY))
                solutions.add(crossY);

            for(int j = i + 1 ; j<this.inequalities.numRows()-1; j++) {
                double[] f2 ={this.inequalities.get(j,0), this.inequalities.get(j,1), this.golaFunctionCoefficients[j]};
                double[] solution = this.linearFunctionSolve(f1, f2);

                if(isAllowed(solution))
                    solutions.add(solution);
            }
        }

        //add last
        int height = this.inequalities.numRows()-2;
        double[] f1 ={this.inequalities.get(height,0), this.inequalities.get(height,1), this.golaFunctionCoefficients[height]};
        double[] crossX = this.crossWithAxis(f1, "x");
        double[] crossY = this.crossWithAxis(f1, "y");

        if(isAllowed(crossX))
            solutions.add(crossX);
        if(isAllowed(crossY))
            solutions.add(crossY);


        int last = this.inequalities.numRows()-1;
        double firstFactor = this.inequalities.get(last, 0);
        double secondFactor = this.inequalities.get(last, 1);
        double[] optiumDual = new double[2];
        double max = 0;
        System.out.println("Lista punktów ograniczających zbiór rozwiązań dopuszczalnych dla PD:");
        for(double[] sol : solutions) {
            System.out.println(sol[0] + " " + sol[1]);
            double currentValue = sol[0] * firstFactor + sol[1] * secondFactor;
            if(currentValue > max){
                max = currentValue;
                optiumDual[0] = sol[0];
                optiumDual[1] = sol[1];
            }

        }

        System.out.println("Optium dual x1 = " + optiumDual[0] + " x2 = " + optiumDual[1] );
        int[] zeroVariables = new int[this.inequalities.numRows()-1];
        //check inequalites
        for(int i = 0; i<this.inequalities.numRows()-1;i++){
            double[] fun ={this.inequalities.get(i,0), this.inequalities.get(i,1), this.golaFunctionCoefficients[i]};
            System.out.println(round((fun[0] * optiumDual[0]) + (fun[1] * optiumDual[1])));
            System.out.println("----------");
            System.out.println(fun[2]);

            if(round((fun[0] * optiumDual[0]) + (fun[1] * optiumDual[1])) < fun[2])
                zeroVariables[i] = 0;
            else
                zeroVariables[i] = 1;

        }

        int currCol = 0;

        System.out.println("zero variables");
        for(int x : zeroVariables){
            System.out.print(x + " ");
        }

        SimpleMatrix linearMatrixA = new SimpleMatrix(2,2);
        SimpleMatrix linearMatrixB = this.inequalities.extractMatrix(this.inequalities.numRows()-1,this.inequalities.numRows(),0,2);
        for (int i = 0; i < zeroVariables.length ; i++) {
            if(zeroVariables[i] == 1){
                linearMatrixA.setColumn(currCol, 0, this.inequalities.get(i, 0), this.inequalities.get(i,1) );
                currCol++;
            }
        }

        System.out.println(linearMatrixA);
        System.out.println(linearMatrixB.transpose());

        SimpleMatrix solutionX = linearMatrixA.solve(linearMatrixB.transpose());
        System.out.println(solutionX);
        System.out.println("Punkt W = (x1, x2, ... , xn) realizujący optimum PP: ");
        int licz = 0;
        double valueOfGolaFun = 0;
        for(int i =0; i<zeroVariables.length; i++){
            if(zeroVariables[i] == 1) {
                System.out.print(solutionX.get(licz) + " ");
                valueOfGolaFun += this.golaFunctionCoefficients[i] * solutionX.get(licz);
                licz++;
            }
            else {
                System.out.print(0.0 + " ");
            }
        }

        System.out.print("\nWartość minimalną: G(W): " + valueOfGolaFun);


    }
}
