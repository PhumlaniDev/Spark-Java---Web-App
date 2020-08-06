import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class App {

    public static void main(final String[] args) {
        port(getHerokuAssignedPort());

        Map<String, Integer> users = new HashMap<>();

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
            Map<String, Object> map2 = new HashMap<>();

            // create the greeting message
            String lang = request.queryParams("language");

            String username = request.queryParams("username");

            String greeting = "";
            switch (lang){
                case "IsiXhosa":
                    greeting =  "Mholo, " + username;
                    break;

                case "English":
                    greeting =  "Hello, " + username;
                    break;

                case "TshiVenda":
                    greeting=  "Ndaa, " + username;

                default:
                    break;
            }


//            final String username = lang + username1;


            if (users.containsKey(username)){
                users.put(username, users.get(username) + 1);
            }

            else{
                users.put(username,1);
            }

            // put it in the map which is passed to the template - the value will be merged into the template
            map2.put("greeting", greeting);
            map2.put("users", users);

            return new HandlebarsTemplateEngine()
                    .render(new ModelAndView(map2, "hello.handlebars"));
        });

    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

}
