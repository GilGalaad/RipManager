package ripmanager.worker;

import lombok.Data;

@Data
public class ProcessOutcome {

    private final int exitCode;
    private final String stdout;

}
