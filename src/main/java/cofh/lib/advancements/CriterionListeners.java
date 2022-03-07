package cofh.lib.advancements;

import com.google.common.collect.Sets;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.PlayerAdvancements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class CriterionListeners<T extends CriterionTriggerInstance> {

    private final PlayerAdvancements playerAdvancements;
    private final Set<CriterionTrigger.Listener<T>> listeners = Sets.newHashSet();

    public CriterionListeners(PlayerAdvancements playerAdvancements) {

        this.playerAdvancements = playerAdvancements;
    }

    public boolean isEmpty() {

        return this.listeners.isEmpty();
    }

    public void add(CriterionTrigger.Listener<T> listener) {

        this.listeners.add(listener);
    }

    public void remove(CriterionTrigger.Listener<T> listener) {

        this.listeners.remove(listener);
    }

    public void trigger(Predicate<T> test) {

        final List<CriterionTrigger.Listener<T>> toGrant = new ArrayList<>();
        for (CriterionTrigger.Listener<T> listener : this.listeners) {
            if (test.test(listener.getTriggerInstance())) {
                toGrant.add(listener);
            }
        }
        toGrant.forEach(listener -> listener.run(this.playerAdvancements));
    }

}
