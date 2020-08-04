import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class App {

    public static void main(final String[] args) {

        // root is 'src/main/resources', so put files in 'src/main/resources/public'
        staticFiles.location("/public"); // Static files
        get("/greet", (req, res) -> "Hello!");
        get("/greet/:username", (req, res) -> "Hello World");
        get("/greet/:username/language/:language", (req, res) -> "Hello World");

        post("/greet", (request, response) -> {
            // Create something
            return "Hello: " + request.params("username");
        });

        final Map<String, String> map = new HashMap<String,String>();
        map.put("name", "Sam");

        get("/hello", (request, response) -> {
            // Show something
            final Map<String, Object> map1 = new HashMap<>();
            return new HandlebarsTemplateEngine().render(new ModelAndView(map1, "hello.handlebars"));
        });

        post("/hello", (request, response) -> {
            // Create something
            final Map<String, Object> map2 = new HashMap<>();

            // create the greeting message
            final String greeting = "Hello, " + request.queryParams("username");

            // put it in the map which is passed to the template - the value will be merged into the template
            map2.put("greeting", greeting);

            return new HandlebarsTemplateEngine()
                    .render(new ModelAndView(map2, "hello.handlebars"));
        });

    }

}
