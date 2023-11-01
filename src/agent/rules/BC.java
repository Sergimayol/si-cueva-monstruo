package agent.rules;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

public class BC<T> {
    private LinkedList<Rule<T>> contentRules;

    public BC() {
        this.contentRules = new LinkedList<>();
    }

    public BC(List<Rule<T>> contentRules) {
        this.contentRules = new LinkedList<>(contentRules);
    }

    public void addProdRule(Rule<T> rule) {
        contentRules.add(rule);
    }

    public T check() {
        Iterator<Rule<T>> it = contentRules.iterator();

        while (it.hasNext()) {
            Rule<T> rule = it.next();
            if (rule.eval()) {
                return rule.getAction();
            }

        }

        return null;
    }

    public List<Rule<T>> getContentRules() {
        return contentRules;
    }

    public String toStringEvaluated() {
        Iterator<Rule<T>> it = contentRules.iterator();
        String str = "";
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            Rule<T> rule = it.next();
            sb.append(rule.toString() + ": " + rule.eval() + "\n");
        }
        return str;
    }

    @Override
    public String toString() {
        Iterator<Rule<T>> it = contentRules.iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            sb.append(it.next().toString() + "\n");
        }
        return sb.toString();
    }

}
