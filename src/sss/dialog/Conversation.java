package sss.dialog;

import java.util.concurrent.CopyOnWriteArrayList;

public class Conversation {
    private CopyOnWriteArrayList<BasicQA> conversation;
    private static final int MAXIMUM_SIZE = 200;

    public Conversation() {
        this.conversation = new CopyOnWriteArrayList<>();
    }

    public void addQA(BasicQA qa) {
        assert conversation.size() <= MAXIMUM_SIZE;
        if (conversation.size() >= MAXIMUM_SIZE) {
            conversation.remove(0);
            conversation.add(qa);
        } else {
            conversation.add(qa);
        }
    }

    public BasicQA getLastQA() {
        return conversation.get(conversation.size()-1);
    }

    public BasicQA getNFromLastQA(int n) {
        return conversation.get(conversation.size() - 1 - n);
    }

    public boolean isEmpty() {
        return conversation.isEmpty();
    }
}
