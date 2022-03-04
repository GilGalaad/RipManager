package ripmanager.engine.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class VideoDemuxOptions extends DemuxOptions {

    private boolean convertToHuff;

    public VideoDemuxOptions(boolean selected, boolean convertToHuff) {
        super(selected);
        this.convertToHuff = convertToHuff;
    }

}
