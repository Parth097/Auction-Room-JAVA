import net.jini.core.entry.*;

public class LotStatus implements Entry{
    // Variables
    public Integer nextLot;

    // No arg contructor
    public LotStatus(){
    }

    public LotStatus(int n){
        // set count to n
        nextLot = n;
    }

    public void addJob(){
        nextLot++;
    }
}
