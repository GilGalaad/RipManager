package ripmanager.engine;

import lombok.extern.log4j.Log4j2;
import ripmanager.engine.dto.*;
import ripmanager.engine.enums.AudioCodec;
import ripmanager.engine.enums.Language;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class Eac3toParser {

    // regex
    private static final Pattern LINE_SPLIT_PATTERN = Pattern.compile("\\R");
    private static final Pattern TRACK_PATTERN = Pattern.compile("^(?<index>\\d+): (?<content>.+)$");
    private static final Pattern VIDEO_PATTERN = Pattern.compile("^(?<codec>(h264/AVC|h265/HEVC|MPEG2|VC-1)), .*$");
    private static final Pattern AUDIO_PATTERN = Pattern.compile("^(?<codec>(.+?)),\\s(?<lang>.+?),\\s(?<channels>\\d+\\.\\d+)\\s+(\\(strange setup\\) )?channels,.+$");
    private static final Pattern SUBTITLE_PATTERN = Pattern.compile("^Subtitle\\s+\\((?<codec>PGS|SRT|VobSub)\\),\\s+(?<lang>.+?)(,.+)?$");
    private static final Pattern CHAPTERS_PATTERN = Pattern.compile("^Chapters,\\s+\\d+\\s+chapters$");

    // defaults
    private static final Set<Language> DEFAULT_LANGUAGES = Stream.of(Language.ENGLISH, Language.ITALIAN).collect(Collectors.toCollection(HashSet::new));

    public static List<Track> parse(String output) {
        // skipping and aggregating lines
        List<String> lines = new ArrayList<>();
        for (var line : LINE_SPLIT_PATTERN.splitAsStream(output).collect(Collectors.toList())) {
            // skip first line that contains general track information
            if (line.startsWith("M2TS, ") || line.startsWith("MKV, ")) {
                continue;
            }
            // skip warning lines typically at the end
            if (line.contains("The video framerate is correct, but rather unusual")
                || line.contains("Bitstream parsing")
                || line.contains("may still produce correct results")) {
                continue;
            }
            if (TRACK_PATTERN.matcher(line).matches()) {
                // if line starts with a number, add
                lines.add(line.trim());
            } else {
                // else append to the last added line
                lines.set(lines.size() - 1, lines.get(lines.size() - 1) + " " + line.trim());
            }
        }
        //lines.forEach(log::info);

        // extracting valid tracks
        List<Track> tracks = new ArrayList<>();
        for (var line : lines) {
            Matcher m = TRACK_PATTERN.matcher(line);
            m.find();
            int index = Integer.parseInt(m.group("index"));
            String content = m.group("content");

            // video
            if (VIDEO_PATTERN.matcher(content).matches()) {
                if (content.contains("Dolby Vision Enhancement Layer")) {
                    log.info("Skipping line: {}", line);
                    continue;
                }
                VideoTrack track = new VideoTrack(index);
                tracks.add(track);
                continue;
            }

            // chapters
            if (CHAPTERS_PATTERN.matcher(content).matches()) {
                ChaptersTrack track = new ChaptersTrack(index);
                tracks.add(track);
                continue;
            }

            // sub
            if (SUBTITLE_PATTERN.matcher(content).matches()) {
                Matcher subm = SUBTITLE_PATTERN.matcher(content);
                subm.find();
                Language lang = Language.findByName(subm.group("lang"));
                if (lang == null) {
                    log.info("Skipping line for unsupported lang: {}", line);
                    continue;
                }
                String codec = subm.group("codec");
                if (!codec.equals("PGS")) {
                    log.info("Skipping line for unsupported subtitle type: {}", line);
                    continue;
                }
                SubtitlesTrack track = new SubtitlesTrack(index);
                track.setProperties(new SubtitlesProperties(lang));
                tracks.add(track);
                continue;
            }

            // audio
            if (AUDIO_PATTERN.matcher(content).matches()) {
                Matcher audm = AUDIO_PATTERN.matcher(content);
                audm.find();
                Language lang = Language.findByName(audm.group("lang"));
                if (lang == null) {
                    log.info("Skipping line for unsupported lang: {}", line);
                    continue;
                }
                AudioCodec codec = AudioCodec.findByName(audm.group("codec"));
                if (codec == null) {
                    throw new RuntimeException("Unsupported audio codec for line: " + line);
                }
                int channels = Integer.parseInt(audm.group("channels").split("\\.")[0]) + Integer.parseInt(audm.group("channels").split("\\.")[1]);
                boolean hasCore = content.contains("(core:") || content.contains("(embedded:");
                AudioTrack track = new AudioTrack(index);
                AudioProperties properties = new AudioProperties(lang, codec, channels, hasCore);
                track.setProperties(properties);
                tracks.add(track);
                continue;
            }

            // unsupported track
            throw new RuntimeException("Unsupported track: " + line);
        }
        tracks.sort(Comparator.comparing(Track::getIndex));
        tracks.forEach(log::info);
        return tracks;
    }

}
