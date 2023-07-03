package mekanism.quilt.capability;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface Capability extends Component {
    interface Provider<T extends Capability, C> {
        String getNbtKey();
        T create(C ctx);
    };
}
