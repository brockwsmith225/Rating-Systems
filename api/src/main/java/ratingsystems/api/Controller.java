package ratingsystems.api;

import org.springframework.web.bind.annotation.*;
import ratingsystems.common.Runner;
import ratingsystems.common.commands.CommandMode;
import ratingsystems.common.parameters.Parameters;
import ratingsystems.hps.HistoricalPredictionSystemRunner;
import ratingsystems.rrs.RelativeRatingSystemRunner;
import ratingsystems.sdr.SimpleDifferenceRatingRunner;
import ratingsystems.ser.SimpleEfficiencyRatingRunner;
import ratingsystems.eps.EfficiencyPredictionSystemRunner;
import ratingsystems.dps.DifferencePredictionSystemRunner;

import java.util.*;

@RestController
public class Controller {
    private HashMap<String, Runner> ratingSystems;
    private HashSet<String> allowedCommands;

    public Controller() {
        ratingSystems = new HashMap<>();
        ratingSystems.put("rrs", new RelativeRatingSystemRunner());
        ratingSystems.put("ser", new SimpleEfficiencyRatingRunner());
        ratingSystems.put("sdr", new SimpleDifferenceRatingRunner());
        ratingSystems.put("eps", new EfficiencyPredictionSystemRunner());
        ratingSystems.put("dps", new DifferencePredictionSystemRunner());
        ratingSystems.put("hps", new HistoricalPredictionSystemRunner());

        Parameters.leagues = new HashSet<>();
        for (Runner ratingSystem : ratingSystems.values()) {
            Parameters.leagues.addAll(ratingSystem.getLeagues());
        }

        allowedCommands = new HashSet<>();
        allowedCommands.add("rank");
        allowedCommands.add("predict");
    }

    @RequestMapping("/api/{system}/{command}")
    public @ResponseBody Response request(@PathVariable String system, @PathVariable String command, @RequestParam(required=false) List<String> args, @RequestParam(required=false) Map<String, String> params) {
        system = system.toLowerCase();
        command = command.toLowerCase();
        if (!ratingSystems.containsKey(system)) {
            return new Error("Rating system " + system + " does not exist");
        }
        if (!allowedCommands.contains(command)) {
            return new Error("Command " + command + " does not exist");
        }

        if (args == null) {
            args = new ArrayList<>();
        }

        HashMap<String, Boolean> options = new HashMap<>();
        Parameters parameters = new Parameters();
        if (params != null) {
            for (String param : params.keySet()) {
                if (params.get(param).toLowerCase().equals("true")) {
                    options.put(param.toUpperCase(), true);
                } else if (params.get(param).toLowerCase().equals("false")) {
                    options.put(param.toUpperCase(), false);
                } else {
                    parameters.setParameterValue(param.toUpperCase(), params.get(param));
                }
            }
        }
        options.put("CLEAN", false);
        options.put("PRETTY_PRINT", false);

        return new Response(ratingSystems.get(system).run(command, args, options, parameters, CommandMode.API));
    }

}
