package ripmanager.worker;

import lombok.Data;

@Data
public class ProcessOutcome {

    private final Integer exitCode;
    private final String output;

}
