package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.model.Environment;

public class WordsCollector {
    private final Environment env;

    public WordsCollector(Environment environment) {
        this.env = environment;
    }

    public void collectWords() {
        while (true) {
            env.getMqBroker().waitData();
            env.getQueue().drainTo(env.getVocabulary());
            env.getMqBroker().sayNotFull();
            env.getMqBroker().sayEmpty();
        }
    }
}
