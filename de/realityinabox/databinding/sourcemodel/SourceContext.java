package de.realityinabox.databinding.sourcemodel;

public class SourceContext {

    private String charsetName;
    private int indentDepth = 0;

    public SourceContext(String charsetName) {
        this.charsetName = charsetName;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void increaseIndent() {
        indentDepth++;
    }

    public void decreaseIndent() {
        indentDepth--;
    }

    public void println(java.io.OutputStream stream, String line) throws java.io.IOException {
        for (int i=0; i<indentDepth; i++) stream.write("    ".getBytes(charsetName));
        stream.write(line.getBytes(charsetName));
        stream.write("\n".getBytes(charsetName));
    }

    public SourceContext duplicate() {
        SourceContext result = new SourceContext(charsetName);
        result.indentDepth = this.indentDepth;
        return result;
    }
}
