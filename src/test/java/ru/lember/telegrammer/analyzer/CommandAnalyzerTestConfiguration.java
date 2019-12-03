package ru.lember.telegrammer.analyzer;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.lember.telegrammer.configs.AsyncCmdProperties;
import ru.lember.telegrammer.configs.CmdProperties;
import ru.lember.telegrammer.configs.ReplyProperties;
import ru.lember.telegrammer.configs.reply.ReplyDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CommandAnalyzerTestConfiguration {


    @Bean
    public CmdProperties properties() {

        CmdProperties properties = Mockito.mock(CmdProperties.class);
        Mockito.when(properties.isIgnoreUnknownCmd()).thenReturn(true, false);
        Mockito.when(properties.getUnknownCmdMessageTemplate()).thenReturn(ReplyDto.ofText("what the fuck: %s ?"));

        Map<String, String> syncMap = new HashMap<>();
        syncMap.put("cmd1", "reply1");
        syncMap.put("cmd2", "reply2");

        //Mockito.when(properties.getSyncReplyMapping()).thenReturn(syncMap);

        Map<String, ReplyProperties> asyncMap = new HashMap<>();

        ReplyProperties replyProp1 = new ReplyProperties();
        replyProp1.setTimeoutMs(1000L);
        replyProp1.setTimeoutError(ReplyDto.ofText("timeout error 1"));
        replyProp1.setReply(ReplyDto.ofText("async reply 1"));
        replyProp1.setBeforeActionReply(ReplyDto.ofText("before async reply 1"));

        ReplyProperties replyProp2 = new ReplyProperties();
        replyProp2.setReply(ReplyDto.ofText("async reply 2"));

        ReplyProperties replyProp3 = new ReplyProperties();
        replyProp3.setReply(ReplyDto.ofText("async reply 3"));
        replyProp3.setBeforeActionReply(ReplyDto.ofText("before async reply 3"));

        asyncMap.put("async1", replyProp1);
        asyncMap.put("async2", replyProp2);
        asyncMap.put("async3", replyProp3);

        AsyncCmdProperties asyncCmdProperties = new AsyncCmdProperties();

        asyncCmdProperties.setGlobalTimeoutMs(5000L);
        asyncCmdProperties.setGlobalTimeoutError(ReplyDto.ofText("Global timeout!"));
        asyncCmdProperties.setReplyMapping(asyncMap);

        Mockito.when(properties.getAsyncCmdProperties()).thenReturn(asyncCmdProperties);

        return properties;
    }

    @Bean
    public CommandAnalyzer analyzer(final CmdProperties properties) {

        return new CommandAnalyzerImpl(properties);
    }

}
