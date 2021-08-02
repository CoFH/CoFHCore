package cofh.core.client.gui.element.panel;

/**
 * Keeps track of which panels should be open by default when a player opens a GUI.
 *
 * @author King Lemming
 */
public class PanelTracker {

    private static Class<? extends PanelBase> openedLeft;
    private static Class<? extends PanelBase> openedRight;

    private PanelTracker() {

    }

    public static Class<? extends PanelBase> getOpenedLeft() {

        return openedLeft;
    }

    public static Class<? extends PanelBase> getOpenedRight() {

        return openedRight;
    }

    public static void setOpenedLeft(Class<? extends PanelBase> panelClass) {

        openedLeft = panelClass;
    }

    public static void setOpenedRight(Class<? extends PanelBase> panelClass) {

        openedRight = panelClass;
    }

}
