import org.ejml.simple.SimpleMatrix;

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
            y = -1/f1[1]*(f1[0] * x - f1[2]);
            solution[0] = x;
            solution[1] = y;
            return solution;
        }
        else
            return null;
    }

    public void run(){
        System.out.println(this.inequalities);
        double[] f1 ={this.inequalities.get(0,0), this.inequalities.get(0,1), this.golaFunctionCoefficients[0]};
        double[] f2 ={this.inequalities.get(1,0), this.inequalities.get(1,1), this.golaFunctionCoefficients[1]};
        double[] solution = this.linearFunctionSolve(f1, f2);
        for(double x: solution){
            System.out.println(x);
        }

    }
}
