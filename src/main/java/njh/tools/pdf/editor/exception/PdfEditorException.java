package njh.tools.pdf.editor.exception;

public class PdfEditorException extends RuntimeException{
    public PdfEditorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PdfEditorException(final String message) {
        super(message);
    }

    public String humanReadable() {
        return "Here's you detailed message: " + this.getMessage();
    }
}
