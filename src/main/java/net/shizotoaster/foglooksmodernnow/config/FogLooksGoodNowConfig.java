package net.shizotoaster.foglooksmodernnow.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.shizotoaster.foglooksmodernnow.client.FogManager;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FogLooksGoodNowConfig {
    public static final ModConfigSpec config;
    public static final ClientConfig CLIENT_CONFIG;

    static {
        final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        config = specPair.getRight();
        CLIENT_CONFIG = specPair.getLeft();
    }

    public static class ClientConfig {
        public final ModConfigSpec.ConfigValue<List<? extends String>> biomeFogs;
        public final ModConfigSpec.DoubleValue defaultFogStart;
        public final ModConfigSpec.DoubleValue defaultFogDensity;

        public final ModConfigSpec.BooleanValue useCaveFog;
        public final ModConfigSpec.DoubleValue caveFogDensity;
        public final ModConfigSpec.IntValue caveFogColor;

        private ClientConfig(ModConfigSpec.Builder builder) {
            builder.push("client");

            this.defaultFogStart = builder.comment("Defines the global default fog start value").defineInRange("globalFogStart", 0.0, 0.0, 1.0);
            this.defaultFogDensity = builder.comment("Defines the global default fog end value, as a percentage of render distance. At 1.0, the fog end is at render distance. At 0, there is no fog").defineInRange("fogEnd", 1.0, 0.0, 1.0);

            this.useCaveFog = builder.comment("Defines if fog will darken and get more dense when underground.").define("useCaveFog", true);
            this.caveFogDensity = builder.comment("Defines the density of fog in caves. If cave fog is active, this will be multiplied with the current fog end.").defineInRange("caveFogDensity", 0.8, 0.0, 1.0);
            this.caveFogColor = builder.comment("Defines the color of cave fog, in the decimal color format. If cave fog is active, this will be multiplied with the current fog color.").defineInRange("caveFogColor",  3355443, 0, 16777215);

            this.biomeFogs = builder.comment("Defines a specific fog start, fog end, and cave fog end per biome. Entries are comma separated, structured like \"<biomeid>,<fog start>,<fog end>,<cave fog color>\"",
                            "Example: [\"minecraft:plains,0.1,1.2,5066351\", \"minecraft:nether_wastes,0,0.5,5056548\"]")
                    .defineListAllowEmpty(Arrays.stream(new String[]{"biomeFogMap"}).toList(), () -> new ArrayList<>(), o -> o instanceof String);

            builder.pop();
        }
    }

    public static List<Pair<String, FogManager.BiomeFogDensity>> getDensityConfigs() {
        List<Pair<String, FogManager.BiomeFogDensity>> list = new ArrayList<>();
        List<? extends String> densityConfigs = CLIENT_CONFIG.biomeFogs.get();

        for (String densityConfig : densityConfigs) {
            String[] options = densityConfig.split(",");
            try {
                list.add(Pair.of(options[0], new FogManager.BiomeFogDensity(Float.parseFloat(options[1]), Float.parseFloat(options[2]), Integer.parseInt(options[3]))));
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }

        return list;
    }
}
