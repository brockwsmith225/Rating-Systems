import ratingSystem.RatingSystem;
import rrs.RRS;

public class Main {
    public static void main(String[] args) {
        RatingSystem rrs = new RRS();
        rrs.setup();
        rrs.printEntityRankings();
    }
}
