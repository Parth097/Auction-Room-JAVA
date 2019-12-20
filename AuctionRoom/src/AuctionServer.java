import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class AuctionServer {
    private static final long ONESECOND = 1000;  // one thousand milliseconds
    
    public static String currentLoggedInUser;

    public static void main(String[] args){

        JavaSpace space = SpaceUtils.getSpace();

        if (space == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }

        PSLotStatus template = new PSLotStatus();
        try {
            PSLotStatus returnedObject = (PSLotStatus)space.readIfExists(template,null, ONESECOND);
            if (returnedObject == null) {
                // there is no object in the space, so create one
                try {
                    PSLotStatus qs = new PSLotStatus(0);
                    space.write(qs, null, Lease.FOREVER);
                    System.out.println(template.getClass().getName() + " object added to space");
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // there is already an object available, so don't create one
                System.out.println(template.getClass().getName() + " object is already in the space");
                System.exit(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
