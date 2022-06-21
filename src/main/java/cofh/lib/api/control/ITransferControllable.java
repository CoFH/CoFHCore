package cofh.lib.api.control;

public interface ITransferControllable {

    boolean hasTransferIn();

    boolean hasTransferOut();

    boolean getTransferIn();

    boolean getTransferOut();

    void setControl(boolean input, boolean output);

}
