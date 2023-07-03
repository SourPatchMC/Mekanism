package mekanism.common.capabilities;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import mekanism.api.IAlloyInteraction;
import mekanism.api.IConfigCardAccess;
import mekanism.api.IConfigurable;
import mekanism.api.IEvaporationSolar;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.lasers.ILaserDissipation;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.api.radiation.capability.IRadiationEntity;
import mekanism.api.radiation.capability.IRadiationShielding;
import mekanism.api.security.IOwnerObject;
import mekanism.api.security.ISecurityObject;
import mekanism.common.Mekanism;
import mekanism.quilt.capability.CapabilityComponents;

public class Capabilities {

    private Capabilities() {
    }

    public static final ComponentKey<IGasHandler> GAS_HANDLER = ComponentRegistry.getOrCreate(Mekanism.rl("gas_handler"));
    public static final ComponentKey<IInfusionHandler> INFUSION_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ComponentKey<IPigmentHandler> PIGMENT_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ComponentKey<ISlurryHandler> SLURRY_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ComponentKey<IHeatHandler> HEAT_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ComponentKey<IStrictEnergyHandler> STRICT_ENERGY = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ComponentKey<IConfigurable> CONFIGURABLE = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ComponentKey<IAlloyInteraction> ALLOY_INTERACTION = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ComponentKey<IConfigCardAccess> CONFIG_CARD = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ComponentKey<IEvaporationSolar> EVAPORATION_SOLAR = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ComponentKey<ILaserReceptor> LASER_RECEPTOR = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ComponentKey<ILaserDissipation> LASER_DISSIPATION = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ComponentKey<IRadiationShielding> RADIATION_SHIELDING = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ComponentKey<IRadiationEntity> RADIATION_ENTITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ComponentKey<IOwnerObject> OWNER_OBJECT = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ComponentKey<ISecurityObject> SECURITY_OBJECT = CapabilityManager.get(new CapabilityToken<>() {});
}