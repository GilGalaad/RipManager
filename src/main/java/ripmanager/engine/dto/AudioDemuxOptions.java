package ripmanager.engine.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class AudioDemuxOptions extends DemuxOptions {

    public AudioDemuxOptions(boolean selected) {
        super(selected);
    }

}
