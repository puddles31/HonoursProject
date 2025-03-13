import models.Kilominx;
import patterndatabases.kilominx.SparseKubiesPatternDatabase;

public class sparsetest {
    public static void main(String[] args) {
        int setNo = 1;

        SparseKubiesPatternDatabase sparsePDB = new SparseKubiesPatternDatabase(setNo);
        boolean readSuccess = sparsePDB.readDatabaseFromFile("kilominx/sparse_kubies_" + setNo + ".pdb");

        if (readSuccess) {
            System.out.println("Pattern databases loaded successfully.");
        }

        Kilominx kilominx = new Kilominx();

        byte estimate = sparsePDB.getNumberOfMoves(kilominx);
        System.out.println("Estimated moves: " + estimate);
    }
}
