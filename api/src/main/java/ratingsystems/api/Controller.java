package ratingsystems.api;

import org.springframework.web.bind.annotation.*;
import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.commands.CommandMode;
import ratingsystems.hps.HistoricalPredictionSystemRunner;
import ratingsystems.rrs.RelativeRatingSystemRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {
    private HashMap<String, Runner> ratingSystems;

    public Controller() {
        ratingSystems = new HashMap<>();
        ratingSystems.put("rrs", new RelativeRatingSystemRunner());
        ratingSystems.put("hps", new HistoricalPredictionSystemRunner());
    }

    @RequestMapping("/api/{system}/{command}")
    public @ResponseBody Response request(@PathVariable String system, @PathVariable String command, @RequestParam(required=false) List<String> args, @RequestParam(required=false) Map<String, String> parameters) {
        system = system.toLowerCase();
        if (!ratingSystems.containsKey(system)) {
            return new Error("Rating system " + system + " does not exist");
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

        CommandInput commandInput;
        if (args == null && options == null) {
            commandInput = new CommandInput(command);
        } else if (args == null) {
            commandInput = new CommandInput(command, options);
        } else if (options == null) {
            commandInput = new CommandInput(command, args);
        } else {
            commandInput = new CommandInput(command, args, options);
        }

        return new Response(ratingSystems.get(system).run(commandInput, CommandMode.API));
    }

}
