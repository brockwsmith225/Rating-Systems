package ratingsystems.api;

import org.springframework.web.bind.annotation.*;
import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.Runner;
import ratingsystems.common.commands.CommandMode;
import ratingsystems.hps.HistoricalPredictionSystemRunner;
import ratingsystems.rrs.RelativeRatingSystemRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {
    private HashMap<String, Runner> ratingSystems;
    private HashSet<String> allowedCommands;

    public Controller() {
        ratingSystems = new HashMap<>();
        ratingSystems.put("rrs", new RelativeRatingSystemRunner());
        ratingSystems.put("hps", new HistoricalPredictionSystemRunner());

        allowedCommands = new HashSet<>();
        allowedCommands.add("rank");
        allowedCommands.add("predict");
    }

    @RequestMapping("/api/{system}/{command}")
    public @ResponseBody Response request(@PathVariable String system, @PathVariable String command, @RequestParam(required=false) List<String> args, @RequestParam(required=false) Map<String, String> parameters) {
        system = system.toLowerCase();
        if (!ratingSystems.containsKey(system)) {
            return new Error("Rating system " + system + " does not exist");
        }
        if (!allowedCommands.contains(command)) {
            return new Error("Command " + command + " does not exist");
        }

        HashMap<String, Boolean> options = null;
        if (parameters != null) {
            options = new HashMap<>();
            for (String option : parameters.keySet()) {
                if (parameters.get(option).toLowerCase().equals("true")) {
                    options.put(option, true);
                } else if (parameters.get(option).toLowerCase().equals("false")) {
                    options.put(option, false);
                }
            }
        }

        return null;
        //return new Response(ratingSystems.get(system).run(args, parameters, options, CommandMode.API));
    }

}
