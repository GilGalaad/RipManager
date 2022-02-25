package ripmanager.engine.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class VideoTrack extends TypedTrack<Void, VideoDemuxOptions> {

    public VideoTrack(int index, String label) {
        super(index, TrackType.VIDEO, label);
    }

}
