package ripmanager.engine.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class VideoTrack extends TypedTrack<Void, VideoDemuxOptions> {

    public VideoTrack(int index) {
        super(index, TrackType.VIDEO);
    }

}
