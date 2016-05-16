import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.halt;


public class Main {
    // I'm providing this as a pseudo-database of users.
    // See addUsers below for the set of users who can login
    static HashMap<String, User> users = new HashMap<>();

    // todo: create a HashMap that maps usernames to ArrayLists containing GroceryItem objects. (HashMap<String, ArrayList<GroceryItem>>)
    static HashMap<String, ArrayList<GroceryItem>> groceryMap =
            new HashMap<String, ArrayList<GroceryItem>>();
    // todo: create a "sequence" variable. This is an integer that represents the next id for a grocery item.
    static int nextId = 0;
    // todo: Each time you read from it be sure to increment it. Set its initial value to 0.

    public static void main(String[] args){
        // this adds test users to the application.
        addTestUsers();

        // this sets the path to the static images and css files
        Spark.staticFileLocation("static");

        // Spark has more than just .get and .post methods. It also has .before. The .before
        // method runs before all requests that match the endpoint pattern specified. The one
        // below matches all paths and therefore runs before all requests. This is a good place
        // to put global login/security checks.
        Spark.before(
                "/*",
                (request, response) -> {

                    // allow anything with /images/ in the root of the uri
                    if(request.uri().startsWith("/images/")) return;

                    // allow anything with /css/ in the root of the uri
                    if(request.uri().startsWith("/css/")) return;

                    // allow the login page no matter what
                    if(request.uri().startsWith("/login")) return;

                    // allow anything if the user is logged in
                    if(request.session().attributes().contains("user")) return;

                    // todo: redirect to the login page
                    response.redirect("/login");
                    // todo: halt the request
                    halt();
                }
        );

        Spark.get(
                "/",
                (request, response) -> {
                    // todo: create a HashMap to hold our model
                    HashMap m = new HashMap();

                    // todo: Get the user from the session
                    User user = request.session().attribute("user");
                    // todo: user the getGroceryItems() method you created below to get a list of the user's own grocery items
                    ArrayList userGroceryItems = getGroceryItems(user);

                    // todo: Put the user's grocery list into the model
                    m.put("groceryList", userGroceryItems);

                    // todo: return a ModelAndView for the groceryList.mustache template
                    return new ModelAndView(m, "groceryList.mustache");

                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/add-grocery-item",
                (request, response) -> {
                    // todo: create a new GroceryItem
                    GroceryItem item = new GroceryItem();

                    // todo: Determine the next value in the sequence sequence. If it's currently 1, return 2. Then,
                    // todo: set the item's id using the next value in the sequence
                    // todo: update the sequence to *be* the next value. That way when you come here again you'll get 2 instead of 1.
                    item.id = incrementId();
                    // todo: Set the item's name
                    item.name = request.queryParams("name");
                    // todo: Set the item's quantity
                    item.quantity = Integer.parseInt(request.queryParams("quantity"));
                    // todo: Get the user from the session
                    User user = request.session().attribute("user");
                    // todo: Get the user's grocery list using the getGroceryItems() method you created below
                    ArrayList userGroceryItems = getGroceryItems(user);
                    // todo: add the item to the grocery list (the static property defined above)
                    userGroceryItems.add(item);

                    // todo: redirect to the webroot
                    response.redirect("/");
                    // todo: halt this request
                    halt();
                    // todo: return null
                    return null;
                }
        );

        // note that this is a get method. There is also a past method with the same endpoint
        Spark.get(
                "/edit-grocery-item",
                (request, response) -> {
                    // todo: create a map to hold your model values
                    HashMap m = new HashMap();
                    // todo: get the id of the item being deleted from the query params and convert it to an integer
                    int item = Integer.parseInt(request.queryParams("id"));
                    // todo: Get the user from the session
                    User user = request.session().attribute("user");
                    // todo: Get the user's grocery list using the method below
                    ArrayList userGroceryItems =  getGroceryItems(user);

                    // todo: use the getItem() method you create below to get the correct item from the grocery list
                    GroceryItem newGroceryItem = getItem(userGroceryItems, item);

                    // todo: add the item into your m hashmap. Be sure to name the key "item".
                    m.put("item", newGroceryItem);

                    // todo: return a new model and view object for the grocery item edit form, groceryItemForm.mustache
                    return new ModelAndView(m, "groceryItemForm.mustache");
                },
                new MustacheTemplateEngine()
        );

        // note that this is a post method. There is also a get method with the same endpoint
        Spark.post(
                "/edit-grocery-item",
                (request, response) -> {
                    // todo: get the id of the item being deleted from the query params and convert it to an integer
                    int item = Integer.parseInt(request.queryParams("id"));
                    // todo: Get the user from the session
                    User user = request.session().attribute("user");
                    // todo: Get the user's grocery list
                    ArrayList userGroceryItems = getGroceryItems(user);
                    // todo: use getItem() to get the item being edited from the user's grocery list
                    GroceryItem newGroceryItem = getItem(userGroceryItems, item);
                    // todo: update the item's name
                    newGroceryItem.name = request.queryParams("name");
                    // todo: update the item's quantity
                    newGroceryItem.quantity = Integer.parseInt(request.queryParams("quantity"));
                    // note: (no code to write here) we don't need to add this item into the grocery list because it's already there.

                    // todo: redirect to the webroot
                    response.redirect("/");
                    // todo: halt this request
                    halt();
                    // todo: return null
                    return null;
                }
        );

        Spark.get(
                "/delete-grocery-item",
                (request, response) -> {
                    // todo: get the id of the item being deleted from the query params and convert it to an integer
                    int item = Integer.parseInt(request.queryParams("id"));
                    // todo: Get the user from the session
                    User user = request.session().attribute("user");
                    // todo: Get the user's grocery list
                    ArrayList<GroceryItem> userGroceryItems = getGroceryItems(user);
                    // todo: use getItem() to get the item being edited from the user's grocery list
                    GroceryItem groceryItemToRemove = getItem(userGroceryItems, item);
                    // todo: delete this item from the array list
                    userGroceryItems.remove(groceryItemToRemove);

                    // todo: redirect to the webroot
                    response.redirect("/");
                    // todo: halt this request
                    halt();

                    // todo: return null
                    return null;
                }
        );

        Spark.get(
                "/login",
                (request, response) -> {
                    // todo: return a ModelAndView for the loginForm.mustache
                    return new ModelAndView(new HashMap(), "loginForm.mustache");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    // todo: get the username that was posted
                    String userName = request.queryParams("username");
                    // todo: get the password that was posted
                    String password = request.queryParams("password");
                    // todo: get the matching user (if any) from the ArrayList of users (defined at the top of this file)
                    User user = users.get(userName);

                    // todo: if the user is not null and the user's password equals the submitted password
                    if(user == null){
                        //throw new Exception("Login name not found.");
                        response.redirect("/?loginFailed=true");
                    }

                    if(!user.getPassword().equals(password)){
                        //throw new Exception("password not provided.");
                        response.redirect("/?loginFailed=true");
                    }
                    // todo: add the user to the session
                    request.session().attribute("user", user);

                    // todo: redirect to the webroot
                    response.redirect("/");
                    // todo: halt this request
                    halt();

                    // todo: return null
                    return null;
                }
        );

        Spark.get(
                "/logout",
                (request, response) -> {
                    // todo: invalidate the user's session
                    request.session().invalidate();
                    // todo: redirect to the webroot
                    response.redirect("/");
                    // todo: halt this request
                    halt();
                    // todo: return null
                    return "";
                }
        );
    }

    public static int incrementId(){
        return ++nextId;
    }

    private static ArrayList<GroceryItem> getGroceryItems(User user) {
        // todo: check if we have an array list in the groceryLists "global" variable already.
        ArrayList temp = groceryMap.get(user.name);
            // todo: if not, create an empty array list and assign it into the hashmap. Use the user's name as the key.
            if (temp == null){
                temp = new ArrayList();
                groceryMap.put(user.name, temp);
            }
        // return the arraylist of groceries for this user. Use their name as the key in the hashmap
        return groceryMap.get(user.name);
    }

    static GroceryItem getItem(ArrayList<GroceryItem> groceryList, int id){
        // todo: loop over the list of grocery items
        for(GroceryItem g : groceryList){
            if(g.id == id){
                return g;
            }
        }

        // todo: check if this item's id match the id of the item being deleted.

        // todo: if so, return this item


        // it's possible that the list of grocery items is empty or the id provided isn't actually in the list.
        // If that happens we won't reach the return statement in the loop above. Because of this we must add
        // a default return statement that returns null.
        return null;
    }

    // this method adds a set of test users to log in with.
    static void addTestUsers() {
        users.put("Alice", new User("Alice", "cats"));
        users.put("Bob", new User("Bob", "bob"));
        users.put("Charlie", new User("Charlie", "password"));
        users.put("Doug", new User("Doug", "password"));
    }
}
