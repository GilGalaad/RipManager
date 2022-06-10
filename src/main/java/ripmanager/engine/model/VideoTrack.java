package ripmanager.engine.model;

import lombok.Data;
import lombok.ToString;
import ripmanager.engine.enums.TrackType;

@Data
@ToString(callSuper = true)
public class VideoTrack extends TypedTrack<Void, VideoDemuxOptions> {

    public VideoTrack(int index, String label) {
        super(index, TrackType.VIDEO, label);
    }

}
