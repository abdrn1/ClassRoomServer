package testserver;

import com.esotericsoftware.kryonet.Listener;

/**
 * Created by abdrn on 9/5/2016.
 */
public class ClientKey {
    String userID;
    Listener L1;
    boolean stat1;
    Object tag;


    public ClientKey(String userID) {
        this.userID = userID;
    }

    public ClientKey() {

    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ClientKey)) {
            return false;
        }
        ClientKey curr = (ClientKey) o;
        return userID.equals(curr.userID);

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Listener getL1() {
        return L1;
    }

    public void setL1(Listener l1) {
        L1 = l1;
    }

    public boolean isStat1() {
        return stat1;
    }

    public void setStat1(boolean stat1) {
        this.stat1 = stat1;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
