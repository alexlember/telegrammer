package ru.lember.telegrammer.outbound;

import lombok.*;
import org.jetbrains.annotations.Nullable;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private long requestId;
    private String cmd;
    private @Nullable String replyMessage;

}
