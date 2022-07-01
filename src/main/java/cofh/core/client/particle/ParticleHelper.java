package cofh.core.client.particle;

import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.client.particle.types.PointToPointParticleType;
import cofh.core.client.particle.types.RadialParticleType;
import net.minecraft.world.level.Level;

public class ParticleHelper {

    /**
     * Spawns a radial particle, a particle that stays in one position and expands/contracts outward (e.g. shockwaves, cyclones, explosions). Client side method.
     * @param x         x position.
     * @param y         y position.
     * @param z         z position.
     * @param xDir      x direction. May not apply to all particles.
     * @param yDir      y direction. May not apply to all particles.
     * @param zDir      z direciton. May not apply to all particles.
     * @param duration  Scales the animation duration. //TODO
     * @param width     Scales the width/radius of the particle.
     * @param height    Scales the height of the particle. May not apply to all particles.
     * @param rgba      Color tint of the particle. May not apply to all particles.
     */
    //public static void radial(Level level, RadialParticleType type, double x, double y, double z, double xDir, double yDir, double zDir, float duration, float width, float height, int rgba) {
    //
    //    level.addParticle(new ColorParticleOptions(type), x, y, z, xDir, yDir, zDir);
    //}
    //
    //public static void radial(Level level, RadialParticleType type, double x, double y, double z, float duration, float width, float height, int rgba) {
    //
    //    radial(level, type, x, y, z, 0, 0, 0, duration, width, height, rgba);
    //}
    //
    //public static void radial(Level level, RadialParticleType type, double x, double y, double z, float duration, float width, float height) {
    //
    //    radial(level, type, x, y, z, 0, 0, 0, duration, width, height, 0xFFFFFFFF);
    //}

    /**
     * Spawns a point-to-point particle, a particle that goes from one point to another (e.g. lasers, bullets). Client side method.
     * @param sx        Starting x position.
     * @param sy        Starting y position.
     * @param sz        Starting z position.
     * @param ex        Ending x position.
     * @param ey        Ending y position.
     * @param ez        Ending z position.
     * @param duration  Scales the animation duration.
     * @param size      Scales the size/width of the particle.
     * @param rgba0     Tints the primary color of the particle.
     * @param rgba1     Tints the secondary color of the particle.
     */
    //public static void p2p(Level level, PointToPointParticleType type, double sx, double sy, double sz, double ex, double ey, double ez, float duration, float size, int rgba0, int rgba1) {
    //
    //    level.addParticle(new BiColorParticleOptions(type).duration(duration).size(size).colors(rgba0, rgba1), sx, sy, sz, ex, ey, ez);
    //}

}
