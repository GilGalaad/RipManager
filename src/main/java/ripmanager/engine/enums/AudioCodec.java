package ripmanager.engine.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AudioCodec {
    DOLBY_ATMOS(Arrays.asList("TrueHD (Atmos)", "TrueHD/AC3 (Atmos)"), true, "thd", "ac3"),
    DOLBY_THD(Arrays.asList("TrueHD", "TrueHD/AC3"), true, "thd", "ac3"),
    DOLBY_DIGITAL_PLUS(Arrays.asList("EAC3", "E-AC3"), false, null, "eac3"),
    DOLBY_DIGITAL(Arrays.asList("AC3", "AC3 EX", "AC3 Surround"), false, null, "ac3"),
    DTS_MA(Arrays.asList("DTS Master Audio"), true, "dtsma", "dts"),
    DTS_HIRES(Arrays.asList("DTS Hi-Res"), true, "dtshd", "dts"),
    DTS(Arrays.asList("DTS", "DTS-ES", "DTS Express"), false, null, "dts"),
    PCM(Arrays.asList("RAW/PCM"), true, "wav", null);

    private final List<String> names;
    private final boolean lossless;
    private final String losslessExtension;
    private final String lossyExtension;

    AudioCodec(List<String> names, boolean lossless, String losslessExtension, String lossyExtension) {
        this.names = names;
        this.lossless = lossless;
        this.losslessExtension = losslessExtension;
        this.lossyExtension = lossyExtension;
    }

    public static AudioCodec findByName(String name) {
        for (AudioCodec codec : values()) {
            if (name != null && codec.names.stream().map(i -> i.toLowerCase()).collect(Collectors.toSet()).contains(name.toLowerCase())) {
                return codec;
            }
        }
        return null;
    }

}
