/**
 * Created by doug on 5/8/16.
 */
public class GroceryItem {
    // create an id, default it to 1
    int id = 0;

    //create userID
    int userID;

    // create a property for name
    String name;

    // create a property for quantity
    int quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
