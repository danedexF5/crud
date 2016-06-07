import java.sql.*;
import java.util.ArrayList;

/**
 * Created by danedexheimer on 5/23/16.
 */
public class CrudService {

    private final Connection connection;

    public CrudService(Connection connection) {
        this.connection = connection;
    }

    //Add a initDatabase method to create the tables required for this project if they don't already exist.

    public void initDatabase() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS groceryItems (id IDENTITY, usersID INT, name VARCHAR, quantity INT)");
        statement.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
    }

    //Create insertUser
    //Create an insertUser method, which creates a new record in the users table.
    public void insertUser(Connection connection, User user) throws SQLException {
        // insert the new user
        PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        statement.setString(1, user.getName());
        statement.setString(2, user.getPassword());
        statement.executeUpdate();

        // get the generated id
        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next(); // read the first line of results

        // set the generated id into my person
        user.setId(resultSet.getInt(1));
    }


    //create selectUser
    //Create a selectUser method, which returns a User object for the given username.
    public User selectUser(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.next();

        User user = new User();

        user.setId(resultSet.getInt(1));
        user.setName(resultSet.getString(2));
        user.setPassword(resultSet.getString(3));

        return user;
    }
    public User selectUser(String userName){
        User user = new User();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE name = ?");

            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();

            user.setId(resultSet.getInt(1));
            user.setName(resultSet.getString(2));
            user.setPassword(resultSet.getString(3));
        }
        catch (Exception e){

        }
        return user;
    }

    //todo: create select users method. Make it accept string instead of int

    //Create an insertEntry method, which creates a new record for the thing you're tracking.
    public void insertEntry(Connection connection, GroceryItem groceryItem) throws SQLException {
        // insert the new Entry
        PreparedStatement statement = connection.prepareStatement("INSERT INTO groceryItems VALUES (NULL, ?, ?, ?)");
        statement.setInt(1, groceryItem.getUserID());
        statement.setString(2, groceryItem.getName());
        statement.setInt(3, groceryItem.getQuantity());
        statement.executeUpdate();

        // get the generated id
        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next(); // read the first line of results

        // set the generated id into my gorcery item
        groceryItem.setId(resultSet.getInt(1));
    }

    //Create a selectEntry method, which returns a new object for the given id.
    public GroceryItem selectEntry(Connection connection, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM groceryItems WHERE id = ?");
        preparedStatement.setInt(1, id);


        GroceryItem groceryItem = new GroceryItem();

        try {
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();

            groceryItem.setId(resultSet.getInt(1));
            groceryItem.setUserID(resultSet.getInt(2));
            groceryItem.setName(resultSet.getString(3));
            groceryItem.setQuantity(resultSet.getInt(4));
        }
        catch (SQLException se){

        }

        return groceryItem;
    }
    //Create a selectEntries method, which returns an ArrayList of all objects you are tracking.
    //This query should use an INNER JOIN between your users and entries table.
    public ArrayList<GroceryItem> selectEntries(Connection connection, int usersId) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM GROCERYITEMS INNER JOIN USERS ON " +
        "GROCERYITEMS.USERSID = USERS.ID WHERE Users.id = ?");
        preparedStatement.setInt(1, usersId);

        ArrayList<GroceryItem>groceryItems = new ArrayList<>();

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){

            GroceryItem groceryItem = new GroceryItem();

            groceryItem.setId(resultSet.getInt(1));
            groceryItem.setUserID(resultSet.getInt(2));
            groceryItem.setName(resultSet.getString(3));
            groceryItem.setQuantity(resultSet.getInt(4));

            groceryItems.add(groceryItem);
        }

        return groceryItems;
    }
    //Create an updateEntry method, which updates all the values for a given id
    public void updateEntry(Connection connection, GroceryItem groceryItem) throws SQLException {
        // update the new Entry
        PreparedStatement statement = connection.prepareStatement("UPDATE GROCERYITEMS SET NAME = ?, QUANTITY = ? WHERE ID = ? ");

        statement.setString(1, groceryItem.getName());
        statement.setInt(2, groceryItem.getQuantity());
        statement.setInt(3, groceryItem.getId());
        statement.executeUpdate();
    }

    //Create a deleteEntry method, which deletes an entry with the given id.
    public void deleteEntry(Connection connection, int Id) throws SQLException {
        // update the new Entry
        PreparedStatement statement = connection.prepareStatement("DELETE FROM GROCERYITEMS WHERE ID = ? ");

        statement.setInt(1, Id);
        statement.executeUpdate();
    }
}
