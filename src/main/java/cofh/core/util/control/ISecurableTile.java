package cofh.core.util.control;

import cofh.lib.tileentity.ITileCallback;
import cofh.lib.util.control.ISecurable;
import com.mojang.authlib.GameProfile;

public interface ISecurableTile extends ISecurable, ITileCallback {

    SecurityControlModule securityControl();

    // region ISecurable
    @Override
    default void setAccess(AccessMode access) {

        securityControl().setAccess(access);
    }

    @Override
    default boolean setOwner(GameProfile profile) {

        return securityControl().setOwner(profile);
    }

    @Override
    default AccessMode getAccess() {

        return securityControl().getAccess();
    }

    @Override
    default GameProfile getOwner() {

        return securityControl().getOwner();
    }

    @Override
    default boolean isSecurable() {

        return securityControl().isSecurable();
    }
    // endregion
}
