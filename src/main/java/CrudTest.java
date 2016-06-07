import org.h2.tools.Server;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by danedexheimer on 5/24/16.
 */
public class CrudTest {

    Connection connection;
    CrudService crudService;

    @Before
    public void before() throws SQLException {


        Server server = Server.createTcpServer("-baseDir", "./data").start();

        // created our connection
        String jdbcUrl = "jdbc:h2:" + server.getURL() + "/main";

        connection = DriverManager.getConnection(jdbcUrl, "", null);

        // configure service
        crudService = new CrudService(connection);

        crudService.initDatabase();

    }

    //Create a test for insertUser and selectUser
    @Test
    public void whenInsertingPersonIntoTable() throws SQLException {

        //Set up your test directory and write a test called testUser which tests both of the aforementioned methods.
        /**Given a User object
         * When inserting a User into table
         * Then User object gets an id
         */

        //arrange
        User userTest = new User();

        userTest.setName("name");
        userTest.setPassword("password");


        //act
        //call insertUser method
        crudService.insertUser(connection, userTest);

        connection.commit();

        //assert
        assertThat(userTest.getId(), not(0));

        //arrange

        //act
        //call insertUser method
        User user = crudService.selectUser((userTest.getId()));

        //assert
        assertThat(user.getId(), is(userTest.getId()));

    }

    //Create a test for insertEntry and selectEntry
    @Test
    public void whenInsertingEntryIntoTable() throws SQLException {
        /**Given a groceryItem
         * When inserting a groceryItem into table
         * Then groceryItem gets an id
         */

        //arrange
        GroceryItem groceryItemTest = new GroceryItem();

        groceryItemTest.setName("name");
        groceryItemTest.setQuantity(1);


        //act
        //call insertEntry method
        crudService.insertEntry(connection, groceryItemTest);

        connection.commit();

        //assert
        assertThat(groceryItemTest.getId(), not(0));

        //arrange

        //act
        //call selectEntry method
        GroceryItem groceryItemTest2 = crudService.selectEntry(connection, (groceryItemTest.getId()));

        //assert
        assertThat(groceryItemTest2.getId(), is(groceryItemTest.getId()));

    }

    //Create a test for selectEntries
    @Test
    public void whenSelectingEntriesFromTable() throws SQLException {
        /**Given a userID
         * When selecting entries from table
         * Then groceryItems has at least one element
         */

        //arrange

        GroceryItem groceryItemTest = new GroceryItem();
        groceryItemTest.setUserID(1);
        groceryItemTest.setName("name");
        groceryItemTest.setQuantity(1);


        //act
        //call insertEntry method
        crudService.insertEntry(connection, groceryItemTest);

        connection.commit();

        ArrayList<GroceryItem> testGroceryItems = crudService.selectEntries(connection, 1);

        //assert
        assertThat(testGroceryItems.size(), not(0));

    }

    //Create a test for updateEntry and deleteEntry.
    @Test
    public void whenUpdatingEntriesFromTable() throws SQLException {
        /**Given a groceryItem
         * When updating entries from table
         * Then groceryItem fields are updated
         */

        //arrange
        GroceryItem groceryItemTest = new GroceryItem();
        groceryItemTest.setUserID(1);
        groceryItemTest.setName("Apple");
        groceryItemTest.setQuantity(1);

        crudService.insertEntry(connection, groceryItemTest);

        //act
        groceryItemTest.setUserID(1);
        groceryItemTest.setName("Orange");
        groceryItemTest.setQuantity(2);

        crudService.updateEntry(connection, groceryItemTest);

        GroceryItem groceryItemTest2 = crudService.selectEntry(connection, groceryItemTest.getId());

        //assert
        assertThat(groceryItemTest2.getName(), is(groceryItemTest.getName()));
        assertThat(groceryItemTest2.getQuantity(), is(groceryItemTest.getQuantity()));

    }

    //Create a test for updateEntry and deleteEntry.
    @Test
    public void whenDeletingEntriesFromTable() throws SQLException {
        /**Given a groceryItem id
         * When deleting entries from table
         * Then groceryItem record is deleted
         */

        //arrange
        GroceryItem groceryItemTest = new GroceryItem();
        groceryItemTest.setUserID(1);
        groceryItemTest.setName("Apple");
        groceryItemTest.setQuantity(1);

        crudService.insertEntry(connection, groceryItemTest);

        //act
        crudService.deleteEntry(connection, groceryItemTest.getId());

        GroceryItem groceryItemTest2 = crudService.selectEntry(connection, groceryItemTest.getId());

        //assert
        assertThat(groceryItemTest2.getId(), is(0));

    }
}
