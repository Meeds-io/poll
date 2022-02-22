package org.exoplatform.poll.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Poll implements Cloneable {
    private long id;
    private String question;
    private List<Long> options;
    private String remainingTime;

    public Poll clone() { // NOSONAR
        return new Poll(id,
                        question,
                        options,
                        remainingTime);
    }
}
