package ru.lember.telegrammer.outbound;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatUpdate {

    private String cmd;
    private Long chatId;
    private Integer messageId;
    private Integer userId;
}
