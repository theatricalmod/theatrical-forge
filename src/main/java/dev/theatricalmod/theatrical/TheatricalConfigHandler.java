package dev.theatricalmod.theatrical;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class TheatricalConfigHandler {

  public static class Common {
    public final ForgeConfigSpec.BooleanValue emitLight;
    public final ForgeConfigSpec.BooleanValue consumePower;

    public Common(ForgeConfigSpec.Builder builder) {
      builder.push("fixtures");
      emitLight = builder.comment("Set this to false to prevent lights emitting actual light")
          .define("emitLight", true);
      consumePower = builder.comment("Set this to false to prevent moving lights consuming power")
              .define("consumePower", true);
      builder.pop();
    }
  }

  public static final Common COMMON;
  public static final ForgeConfigSpec COMMON_SPEC;
  static {
    final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
    COMMON_SPEC = specPair.getRight();
    COMMON = specPair.getLeft();
  }

}
