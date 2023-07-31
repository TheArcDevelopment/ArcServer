package ArcServer;

import java.util.ArrayList;
import java.util.List;

public class OnlineUsers {
    private List<String> userList;

    public OnlineUsers() {
        userList = new ArrayList<>();
    }

    public synchronized void addUser(String user) {
        userList.add(user);
    }

    public synchronized void removeUser(String user) {
        userList.remove(user);
    }

    public synchronized List<String> getUserList() {
        return new ArrayList<>(userList);
    }
}
