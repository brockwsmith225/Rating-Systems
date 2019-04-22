package rrs;

import ratingSystem.RatingSystem;

public class RRS extends RatingSystem {

    @Override
    public void setup() {
        double[][] partials = setupPartials(entities.size());
    }

    @Override
    public void rankEntities() {}

    @Override
    public void rankGroups() {}

    //========== RRS only methods ==========
    private double[][] setupPartials(int numberOfEntities) {
        double[][] partials = new double[numberOfEntities][numberOfEntities];
        for (int r = 0; r < numberOfEntities; r++) {
            for (int c = 0; c < numberOfEntities; c++) {
                partials[r][c] = 1.0 / numberOfEntities;
            }
        }
        return partials;
    }
}
