package moonlightflower.com.superioribus2.vocab;

public class VocabEntry {
    private String _source;
    private String _target;

    public String getSource() {
        return _source;
    }

    public String getTarget() {
        return _target;
    }

    public VocabEntry(String source, String target) {
        _source = source;
        _target = target;
    }
}