package sss.evaluatedtas;

/**
 * Created by Daniel on 28/05/2014.
 */
public abstract class Answer {
    private String answer;

    protected Answer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Answer answer1 = (Answer) o;

        if (answer != null ? !answer.equalsIgnoreCase(answer1.getAnswer()) : answer1.getAnswer() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return answer != null ? answer.hashCode() : 0;
    }
}
