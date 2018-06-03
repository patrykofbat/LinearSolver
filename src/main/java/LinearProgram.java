import org.ejml.simple.SimpleMatrix;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class LinearProgram {
    private SimpleMatrix inequalities;
    private double[] golaFunctionCoefficients;


    public LinearProgram(SimpleMatrix inequalities, double[] golaFunctionCoefficients){
        this.inequalities = inequalities;
        this.golaFunctionCoefficients = golaFunctionCoefficients;
    }

    private double[] linearFunctionSolve(double[] f1, double[] f2){
        double x;
        double y;

        double[] solution = new double[2];
        if((f2[0] * f1[1]) - (f1[0] * f2 [1]) != 0) {
            x = ((f2[2] * f1[1]) - (f1[2] * f2[1])) / ((f2[0] * f1[1]) - (f1[0] * f2[1]));
            y = (-1/f1[1])*(f1[0] * x - f1[2]);
            solution[0] = x;
            solution[1] = y;
            return solution;
        }
        else
            return null;
    }

    private double[] crossWithAxis(double[] f1, String axis){
        double x;
        double y;
        if(axis.equals("x")){
            x = 0;
            y = f1[2]/f1[1];
        }
        else {
            y = 0;
            x = f1[2]/f1[0];
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
                if(f1[0] * solution[0] + f1[1] * solution[1] <= f1[2])
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

        int i = 1;
        int last = this.inequalities.numRows()-1;
        double firstFactor = this.inequalities.get(last, 0);
        double secondFactor = this.inequalities.get(last, 1);
        for(double[] sol : solutions) {
            if (sol != null) {
                System.out.println(i+ ". " +"x = " + sol[0] + " y = " + sol[1]);
                i++;
                System.out.println(sol[0] * firstFactor + sol[1] * secondFactor);
            }
        }



    }
}
