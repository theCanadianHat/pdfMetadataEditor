package njh.tools.pdf.editor;

import lombok.extern.slf4j.Slf4j;
import njh.tools.pdf.driver.cmdLine.PdfEditorCommandLineArgs;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Editor of PDF files.
 * Creates a new PDF from supplied input with added new custom metadata tag and value.
 */
@Slf4j
public class PdfEditor {
    private PdfEditorCommandLineArgs editorArgs;

    public PdfEditor(final @NotNull PdfEditorCommandLineArgs editorArgs) {
        this.editorArgs = Objects.requireNonNull(editorArgs);
    }

    public void addMetadataTag() {
        if(editorArgs.isValid()) {
            try {
                PDDocument document = PDDocument.load(new File(editorArgs.getInputFileName().replaceAll("\\\\","/")));
                if(editorArgs.isDebugEnabled()) log.debug("PDF file loaded with path: " + editorArgs.getInputFileName());
                PDDocumentInformation information = document.getDocumentInformation();
                information.setCustomMetadataValue(editorArgs.getMetadataTagName(), editorArgs.getMetadataTagValue());
                if(editorArgs.isDebugEnabled()) log.debug("New metadata tag and value added: "
                        + editorArgs.getMetadataTagName() + "=" + editorArgs.getMetadataTagValue());
                document.save(new File(editorArgs.getOutputFileName()));
                if (editorArgs.isDebugEnabled()) log.debug("New PDF file saved with path: " + editorArgs.getOutputFileName());
                listCustomMetadata(document.getDocumentInformation());
                document.close();
            } catch (IOException e) {
                // TODO: 2/5/2022 need to create new exception to tell caller PDF can't load
                if(editorArgs.isDebugEnabled()) e.printStackTrace();
                System.out.println("Unable to load PDF");
                return;
            }
        } else {
            //todo add exception to throw
            System.out.println("Arguments are not valid, nothing to do.");
        }
    }

    public void listCustomMetadata(@NotNull final PDDocumentInformation information) {
        for (String key :information.getMetadataKeys()) {
            System.out.println("Custom metadata found: " + key + "=" + information.getCustomMetadataValue(key));
        }
    }
}
