package ripmanager.engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class VideoDemuxOptions extends DemuxOptions {

    private boolean convertToHuff;

}
