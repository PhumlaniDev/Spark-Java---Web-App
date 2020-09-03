import connection.ConnDB;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {

    static Jdbi getDatabaseConnectionURL(String defualtJdbcUrl) throws URISyntaxException, SQLException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String database_url = processBuilder.environment().get("DATABASE_URL");
        if (database_url != null) {

            URI uri = new URI(database_url);
            String[] hostParts = uri.getUserInfo().split(":");
            String username = hostParts[0];
            String password = hostParts[1];
            String host = uri.getHost();

            int port = uri.getPort();

            String path = uri.getPath();
            String url = String.format("jdbc:postgresql://%s:%s%s", host, port, path);
            return Jdbi.create(url, username, password);
        }

        return Jdbi.create(defualtJdbcUrl);

    }

    static final String KOANS_DATABASE_URL = "jdbc:postgresql:greet?username=macgyver&password=mac123";

    public static void main(final String[] args) throws Exception {
        port(getHerokuAssignedPort());

//        ConnDB connDB = new ConnDB();

        Jdbi jdbi = getDatabaseConnectionURL(KOANS_DATABASE_URL);

        // get a handle to the database
        Handle handle = jdbi.open();

        // root is 'src/main/resources', so put files in 'src/main/resources/public'
        staticFiles.location("/public"); // Static files

        // get all the usernames from the database


        final Map<String, Object> map = new HashMap<>();


        get("/", (req, res) -> {
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

            List<String> users = handle.createQuery("select name from greet_user")
                    .mapTo(String.class)
                    .list();
            // Show something

            map.put("users", users);
            map.put("counter", users.size());
            return new HandlebarsTemplateEngine().render(new ModelAndView(map, "hello.handlebars"));
        });

        post("/hello", (request, response) -> {


            // create the greeting message
            String lang = request.queryParams("language");

            String username = request.queryParams("name");

            String greeting = "";


            /*//create a user initially with a counter of one
            handle.execute("insert into greet (name, counter) values (?, 1)", username);

            //if the username already exist update the counter using this query
            handle.execute("update greet set counter = counter + 1 where name = ?", username);*/

            handle.execute("insert into greet_user (name,count) values (?,?)", username, 0);

            List<String> users = handle.createQuery("select name from greet_user")
                    .mapTo(String.class)
                    .list();

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
