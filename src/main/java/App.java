import connection.ConnDB;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {

    static final String KOANS_DATABASE_URL = "jdbc:postgresql:greetdb";
    static final String userName = "Phumlani";
    static final String password = "Christian9432";

    public static void main(final String[] args) throws Exception {
        port(getHerokuAssignedPort());

        ConnDB connDB = new ConnDB();

        Jdbi jdbi = Jdbi.create(KOANS_DATABASE_URL, userName, password);

        // get a handle to the database
        Handle handle = jdbi.open();

        // root is 'src/main/resources', so put files in 'src/main/resources/public'
        staticFiles.location("/public"); // Static files

        // get all the usernames from the database
        List<String> users = handle.createQuery("select name from greet")
                .mapTo(String.class)
                .list();

        final Map<String, Object> map = new HashMap<>();


        get("/", (req, res) -> {
            connDB.getConnection();
            res.redirect("/hello");
            return null;
        });

        get("/greet", (req, res) -> "Hello!");
        get("/greet/:username", (req, res) -> "Hello World");
        get("/greet/:username/language/:language", (req, res) -> "Hello World");

        post("/greet", (request, response) -> {
            // Create something
            return "Hello: " + request.params("name");
        });

        get("/hello", (request, response) -> {

            connDB.getConnection();

            // Show something

            map.put("users", users);
            map.put("counter", users.size());
            return new HandlebarsTemplateEngine().render(new ModelAndView(map, "hello.handlebars"));
        });

        post("/hello", (request, response) -> {

            connDB.getConnection();

            // create the greeting message
            String lang = request.queryParams("language");

            String username = request.queryParams("name");

            String greeting = "";


            /*//create a user initially with a counter of one
            handle.execute("insert into greet (name, counter) values (?, 1)", username);

            //if the username already exist update the counter using this query
            handle.execute("update greet set counter = counter + 1 where name = ?", username);*/

            System.out.println(connDB.greeter(username) + "has been greeted.");

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
                users.get(0);
            }

            // put it in the map which is passed to the template - the value will be merged into the template
            map.put("greeting", greeting);

            /*return new HandlebarsTemplateEngine()
                    .render(new ModelAndView(map, "hello.handlebars"));*/

            response.redirect("/hello");
            return null;
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
