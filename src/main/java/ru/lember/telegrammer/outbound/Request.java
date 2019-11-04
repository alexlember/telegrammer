package ru.lember.telegrammer.outbound;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    private long id;
    private String cmd;

}
