package testserver;

/**
 * Created by Abd on 8/29/2016.
 */
public class CommandsMessages {
    double zoomFactor = 1;
    int commnadType = 0;


    public CommandsMessages() {

    }

    public CommandsMessages(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }


    public int getCommnadType() {
        return commnadType;
    }

    public void setCommnadType(int commnadType) {
        this.commnadType = commnadType;
    }
}
