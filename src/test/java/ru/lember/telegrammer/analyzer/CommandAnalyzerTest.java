package ru.lember.telegrammer.analyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CommandAnalyzerTestConfiguration.class})
class CommandAnalyzerTest {

    @Autowired
    private CommandAnalyzer analyzer;

    @Test
    void analyzeTest() {

        AnalyzedResult result = analyzer.analyze("cmd1");

        result = analyzer.analyze("unknown");
    }
}
