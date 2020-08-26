package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.model.Environment;

public class WordsCollector {
    private final Environment env;

    public WordsCollector(Environment environment) {
        this.env = environment;
    }

    public void collectWords() {
        do {
            env.getQueue().drainTo(env.getVocabulary());
        }
        while (env.getDoneSignal().getCount() > 0);
    }
}
