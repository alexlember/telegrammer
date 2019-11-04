package ru.lember.telegrammer.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import ru.lember.telegrammer.configs.CmdProperties;
import ru.lember.telegrammer.configs.ReplyProperties;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
public class CommandAnalyzerImpl implements CommandAnalyzer {


    private CmdProperties properties;

    @Autowired
    public CommandAnalyzerImpl(CmdProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    private void postConstruct() {
      log.info("CommandAnalyzerImpl initialized");
    }

    @Override
    public AnalyzedResult analyze(String cmd) {
        try {

            log.info("CommandAnalyzerImpl analyze cmd: {}", cmd);

            if (properties.getSyncReplyMapping() != null) {
                String syncReplyMessage = properties.getSyncReplyMapping().get(cmd);
                if (!StringUtils.isEmpty(syncReplyMessage)) {
                    return AnalyzedResult.sync(syncReplyMessage);
                }
            }

            if (properties.getAsyncCmdProperties() != null) {
                ReplyProperties replyProperties = properties.getAsyncCmdProperties().getReplyMapping().get(cmd);
                if (replyProperties != null) {
                    return AnalyzedResult.async(
                            cmd,
                            replyProperties.getReplyMessage(),
                            Optional.ofNullable(replyProperties.getTimeoutMs())
                                    .orElse(properties.getAsyncCmdProperties().getGlobalTimeoutMs()),
                            !StringUtils.isEmpty(replyProperties.getTimeoutErrorMessage())
                                    ? replyProperties.getTimeoutErrorMessage()
                                    : properties.getAsyncCmdProperties().getGlobalTimeoutErrorMessage(),
                            replyProperties.getBeforeActionReplyMessage());
                }

            }

            if (properties.isIgnoreUnknownCmd()) {
                return AnalyzedResult.ignored();
            }

            return AnalyzedResult.unknown(properties.getUnknownCmdMessageTemplate());

        } catch (Exception e) {
            log.error("CommandAnalyzerImpl::analyze error: ", e);
            return AnalyzedResult.failed(e.toString());
        }
    }
}
