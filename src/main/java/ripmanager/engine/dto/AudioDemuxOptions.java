package ripmanager.engine.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class AudioDemuxOptions extends DemuxOptions {

    private LosslessDemuxStrategy demuxStrategy;
    private Boolean normalize;
    private Boolean extractCore;

    public AudioDemuxOptions(boolean selected, LosslessDemuxStrategy demuxStrategy, Boolean normalize, Boolean extractCore) {
        super(selected);
        this.demuxStrategy = demuxStrategy;
        this.normalize = normalize;
        this.extractCore = extractCore;
    }

}
