package cofh.lib.util;

import net.minecraft.world.level.LevelReader;

import java.util.function.Supplier;

public class Sided<T> {

    protected T client;
    protected T server;

    public Sided() {

    }

    public Sided(Supplier<T> initial) {

        client = initial.get();
        server = initial.get();
    }

    public T client() {

        return this.client;
    }

    public T server() {

        return this.server;
    }

    public T get(boolean isClient) {

        return isClient ? client() : server();
    }

    public T get(LevelReader level) {

        return get(level.isClientSide());
    }

    public void set(boolean isClient, T value) {

        if (isClient) {
            this.client = value;
        } else {
            this.server = value;
        }
    }

    public void set(LevelReader level, T value) {

        set(level.isClientSide(), value);
    }

}
