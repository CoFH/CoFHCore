package cofh.core.client.particle;

import cofh.core.client.particle.options.CylindricalParticleOptions;
import cofh.core.util.helpers.vfx.VFXHelper;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

public abstract class CylindricalParticle extends ColorParticle {

    public float height;
    public Quaternion rotation = Quaternion.ONE;

    public CylindricalParticle(CylindricalParticleOptions data, ClientLevel level, double x, double y, double z) {

        super(data, level, x, y, z, 0, 0, 0);
        setHeight(data.height);
        hasPhysics = false;
    }

    public CylindricalParticle(CylindricalParticleOptions data, ClientLevel level, double x, double y, double z, double xDir, double yDir, double zDir) {

        super(data, level, x, y, z, 0, 0, 0);
        setHeight(data.height);
        setDirection(xDir, yDir, zDir);
        hasPhysics = false;
    }

    protected void setHeight(float height) {

        this.height = height; //TODO bounding box
    }

    protected void setDirection(double xDir, double yDir, double zDir) {

        rotation = VFXHelper.alignVertical(new Vector3f((float) xDir, (float) yDir, (float) zDir));
    }

    protected void setDirection(Vec3 direction) {

        rotation = VFXHelper.alignVertical(new Vector3f(direction));
    }

}
