package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class GameScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    /** Initialize a new controller using dependency injection.
     *
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public GameScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }
}
