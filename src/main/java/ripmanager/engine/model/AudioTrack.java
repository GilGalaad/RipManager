package ripmanager.engine.model;

import lombok.Data;
import lombok.ToString;
import ripmanager.engine.enums.TrackType;

@Data
@ToString(callSuper = true)
public class AudioTrack extends TypedTrack<AudioProperties, AudioDemuxOptions> {

    public AudioTrack(int index, String label) {
        super(index, TrackType.AUDIO, label);
    }

}
