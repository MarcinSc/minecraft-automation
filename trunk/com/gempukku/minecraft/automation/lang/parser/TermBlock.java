package com.gempukku.minecraft.automation.lang.parser;

import java.util.ArrayList;
import java.util.List;

public class TermBlock {
    private Term _term;
    private List<TermBlock> _termBlocks;

    public TermBlock() {
        _termBlocks = new ArrayList<TermBlock>();
    }

    private TermBlock(Term term) {
        _term = term;
    }

    public void addTermBlock(TermBlock termBlock) {
        _termBlocks.add(termBlock);
    }

    public void addTermBlock(Term term) {
        _termBlocks.add(new TermBlock(term));
    }

    public boolean isTerm() {
        return _term != null;
    }

    public Term getTerm() {
        return _term;
    }

    public List<TermBlock> getTermBlocks() {
        return _termBlocks;
    }
}
