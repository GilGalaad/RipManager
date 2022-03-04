package ripmanager.engine.model;

import lombok.Data;
import ripmanager.engine.enums.Encoder;

@Data
public class EncodingOptions {

    private Encoder encoder = Encoder.AUTODETECT;
    private int crf = 18;
    private String suffix;
    private boolean y4m = false;

}
