package interactive;

import java.util.Arrays;

import models.IMoveController.IMove;
import models.Kilominx;
import solvers.KilominxSolver;

public class KilominxTester {

    KilominxSolver solver;

    public static void main(String[] args) {
        KilominxTester tester = new KilominxTester();

        int scrambleLength = Integer.valueOf(args[0]);
        int testRuns = Integer.valueOf(args[1]);

        for (int i = 0; i < testRuns; i++) {
            tester.testRun(scrambleLength);
        }
    }

    public KilominxTester() {
        solver = new KilominxSolver(null);
    }


    private void testRun(int scrambleLength) {
        System.out.println("\n -- Starting test run of length " + scrambleLength + " -- ");
        Kilominx kilominx = new Kilominx();
        solver.setPuzzleInstance(kilominx);
        
        IMove[] scramble = kilominx.getMoveController().scramble(scrambleLength);
        System.out.println("Scramble: " + Arrays.toString(scramble));

        IMove[] solution = solver.solve();
        System.out.println("Solution: " + Arrays.toString(solution));
        System.out.println("Solution length: " + solution.length);
        System.out.println(" -- Test run complete -- \n");
    }
}
