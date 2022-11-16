package io.github.olivoz.snowballing;

public final class SnowballingMod {
    public static final String MOD_ID = "snowballing";
    
    public static void init() {
        System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
