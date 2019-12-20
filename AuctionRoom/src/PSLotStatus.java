import net.jini.core.entry.*;

public class PSLotStatus implements Entry {
    // Variables
    public Integer nextLot;

    // No arg contructor
    public PSLotStatus() {
    }

    public PSLotStatus(int n) {
        // set count to n
        nextLot = n;
    }

    public void addJob() {
        nextLot++;
    }
}
