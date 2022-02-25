package ripmanager.engine.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class VideoDemuxOptions extends DemuxOptions {

    private boolean convertToHuff;

    public VideoDemuxOptions(boolean extract, boolean convertToHuff) {
        super(extract);
        this.convertToHuff = convertToHuff;
    }

}
