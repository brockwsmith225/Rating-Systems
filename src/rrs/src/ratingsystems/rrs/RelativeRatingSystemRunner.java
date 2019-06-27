package ratingsystems.rrs;

import ratingsystems.common.runner.Runner;
import ratingsystems.common.runner.datatypes.Command;

public class RelativeRatingSystemRunner extends Runner {

    public RelativeRatingSystemRunner(String[] command) {
        super(command);

        commands.put("rank", (Command comm) -> {

            return true;
        });
    }

    public static void main(String[] args) {

    }
}
