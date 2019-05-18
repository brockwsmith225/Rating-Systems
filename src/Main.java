import cfb.CFBInterpreter;
import ratingSystem.RatingSystem;
import rrs.RRS;

public class Main {
    public static void main(String[] args) {
        try {
            RatingSystem rrs = new RRS(new CFBInterpreter(), 2018);
            rrs.setup();
            rrs.printEntityRankings();
        } catch (Exception e) {}
    }
}
