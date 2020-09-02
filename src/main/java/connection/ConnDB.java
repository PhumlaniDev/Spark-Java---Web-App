package connection;

import greeter.Greetings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ConnDB implements Greetings {

    static final String KOANS_DATABASE_URL = "jdbc:postgresql:greetdb";
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String userName = "Phumlani";
    static final String password = "Christian9432";

    public Connection getConnection() throws Exception {

        Class.forName(JDBC_DRIVER);
        return DriverManager.getConnection(KOANS_DATABASE_URL, userName, password);

    }

    @Override
    public String greeter(String name) throws Exception {
        Connection connection = getConnection();

        try {
            //final String CREATE_TABLE_IF_NOT_CREATED = "create table if not exists greet ( id integer identity, name varchar(50), counter int )";
            final String INSERT_NAME_LANG_SQL = "insert into greet (name,count) values (?,?)";

            PreparedStatement addNameLangPreparedStatement = connection.prepareStatement(INSERT_NAME_LANG_SQL);
            addNameLangPreparedStatement.setString(1,name);
            addNameLangPreparedStatement.setInt(2,1);
            addNameLangPreparedStatement.execute();

            return name;

        }

        catch (Exception e){
            return e.getMessage();
        }
    }
}
