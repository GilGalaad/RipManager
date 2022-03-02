package ripmanager.engine.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum AudioCodec {
    DOLBY_ATMOS(Arrays.asList("TrueHD (Atmos)", "TrueHD/AC3 (Atmos)"), true, "thd", "ac3"),
    DOLBY_THD(Arrays.asList("TrueHD", "TrueHD/AC3"), true, "thd", "ac3"),
    DOLBY_DIGITAL_PLUS(Arrays.asList("EAC3", "E-AC3"), false, "eac3", null),
    DOLBY_DIGITAL(Arrays.asList("AC3", "AC3 EX", "AC3 Surround"), false, "ac3", null),
    DTS_MA(Arrays.asList("DTS Master Audio"), true, "dtsma", "dts"),
    DTS_HIRES(Arrays.asList("DTS Hi-Res"), true, "dtshd", "dts"),
    DTS(Arrays.asList("DTS", "DTS-ES", "DTS Express"), false, "dts", null),
    PCM(Arrays.asList("RAW/PCM"), true, "wav", null);

    private final List<String> names;
    private final boolean lossless;
    private final String originalExtension;
    private final String coreExtension;

    AudioCodec(List<String> names, boolean lossless, String originalExtension, String coreExtension) {
        this.names = names;
        this.lossless = lossless;
        this.originalExtension = originalExtension;
        this.coreExtension = coreExtension;
    }

    public static AudioCodec findByName(String name) {
        for (AudioCodec codec : values()) {
            if (name != null && codec.names.stream().map(String::toLowerCase).collect(Collectors.toSet()).contains(name.toLowerCase())) {
                return codec;
            }
        }
        return null;
    }

}
