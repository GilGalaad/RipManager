package ripmanager.engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ripmanager.engine.enums.AudioCodec;
import ripmanager.engine.enums.Language;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioProperties {

    private Language language;
    private AudioCodec codec;
    private int channels;
    private boolean hasCore;

}
