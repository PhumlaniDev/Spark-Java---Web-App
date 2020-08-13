import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {

    public static void main(final String[] args) {
        port(getHerokuAssignedPort());

        String dbDiskURL = "jdbc:h2:file:./greetdb";
        // String dbMemoryURL = "jdbc:h2:mem:greetdb";

        Jdbi jdbi = Jdbi.create(dbDiskURL, "sa", "");

        // get a handle to the database
        Handle handle = jdbi.open();

        // create the table if needed
        handle.execute("create table if not exists greet ( id integer identity, name varchar(50), counter int )");

        // root is 'src/main/resources', so put files in 'src/main/resources/public'
        staticFiles.location("/public"); // Static files

        // get all the usernames from the database
        List<String> users = handle.createQuery("select name from greet")
                .mapTo(String.class)
                .list();


        get("/", (req, res) -> {
            res.redirect("/hello");
            return null;
        });

        get("/greet", (req, res) -> "Hello!");
        get("/greet/:username", (req, res) -> "Hello World");
        get("/greet/:username/language/:language", (req, res) -> "Hello World");

        post("/greet", (request, response) -> {
            // Create something
            return "Hello: " + request.params("username");
        });

        get("/hello", (request, response) -> {
            // Show something
            final Map<String, Object> map1 = new HashMap<>();
            map1.put("users", users);
            map1.put("count", users.size());
            return new HandlebarsTemplateEngine().render(new ModelAndView(map1, "hello.handlebars"));
        });

        post("/hello", (request, response) -> {

            // Create something
            Map<String, Object> map2 = new HashMap<>();

            // create the greeting message
            String lang = request.queryParams("language");

            String username = request.queryParams("username");
            String greeting = "";

            if (!lang.isEmpty()){
                switch (lang) {
                    case "IsiXhosa":
                        greeting = "Mholo, " + username;
                        break;

                    case "English":
                        greeting = "Hello, " + username;
                        break;

                    case "TshiVenda":
                        greeting = "Ndaa, " + username;
                        break;

                    default:
                        break;
                }
            }

            if (!users.contains(username)){
                users.add(0,username);
            }
            else {
                users.get(1);
            }

            // put it in the map which is passed to the template - the value will be merged into the template
            map2.put("greeting", greeting);
            map2.put("users", users);
            map2.put("counter", users.size());

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
