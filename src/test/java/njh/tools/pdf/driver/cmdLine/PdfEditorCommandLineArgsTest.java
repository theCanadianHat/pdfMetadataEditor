package njh.tools.pdf.driver.cmdLine;

import njh.tools.pdf.editor.exception.PdfEditorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;

class PdfEditorCommandLineArgsTest {
    private static final String INPUT_FILE = "inputFile";
    private static final String OUTPUT_FILE = "outputFile";
    private static final String TAG_NAME = "tagName";
    private static final String METADATA = "metadata";
    private static final String[] TEST_ARGS = {
            "-v",
            "-d",
            "--in=" + INPUT_FILE,
            "--out=" + OUTPUT_FILE,
            "--mdt=" + TAG_NAME,
            "--mdv=" + METADATA
    };

    @Test
    void allArgsProvided_valid() {
        final PdfEditorCommandLineArgs testSubject = new PdfEditorCommandLineArgs(createAppArgs());
        Assertions.assertTrue(testSubject.isVerboseEnabled(),"Verbose should be enabled.");
        Assertions.assertTrue(testSubject.isDebugEnabled(),"Debug should be enabled.");
        Assertions.assertTrue(testSubject.isValid(),"The arguments should be valid.");
        Assertions.assertEquals(testSubject.getInputFileName(), INPUT_FILE, "The input file names should match.");
        Assertions.assertEquals(testSubject.getOutputFileName(), OUTPUT_FILE, "The output file names should match.");
        Assertions.assertEquals(testSubject.getMetadataTagName(), TAG_NAME, "The tag names should match.");
        Assertions.assertEquals(testSubject.getMetadataTagValue(), METADATA, "The metadata should match.");
    }

    @Test
    void nullCommandLineArgs() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new PdfEditorCommandLineArgs(null));
    }

    @Test
    void tooFewArguments() {
        Assertions.assertThrows(PdfEditorException.class, () -> new PdfEditorCommandLineArgs(new DefaultApplicationArguments()));
    }

    @Test
    void tooManyArguments() {
        Assertions.assertThrows(
                PdfEditorException.class,
                () -> new PdfEditorCommandLineArgs(
                        new DefaultApplicationArguments(
                                "--1",
                                "--2",
                                "--3",
                                "--4",
                                "--5"
                        )
                )
        );
    }

    @Test
    void missingRequiredArguments_in() {
        Assertions.assertThrows(
                PdfEditorException.class,
                () -> new PdfEditorCommandLineArgs(
                        new DefaultApplicationArguments(
                                "--1",
                                "--mdt",
                                "--mdv",
                                "--out"
                        )
                )
        );
    }

    @Test
    void missingRequiredArguments_out() {
        Assertions.assertThrows(
                PdfEditorException.class,
                () -> new PdfEditorCommandLineArgs(
                        new DefaultApplicationArguments(
                                "--in",
                                "--mdt",
                                "--mdv",
                                "--1"
                        )
                )
        );
    }

    @Test
    void missingRequiredArguments_mdt() {
        Assertions.assertThrows(
                PdfEditorException.class,
                () -> new PdfEditorCommandLineArgs(
                        new DefaultApplicationArguments(
                                "--in",
                                "--1",
                                "--mdv",
                                "--out"
                        )
                )
        );
    }

    @Test
    void missingRequiredArguments_mdv() {
        Assertions.assertThrows(
                PdfEditorException.class,
                () -> new PdfEditorCommandLineArgs(
                        new DefaultApplicationArguments(
                                "--in",
                                "--mdt",
                                "--1",
                                "--out"
                        )
                )
        );
    }

    @Test
    void missingOptionValue() {
        Assertions.assertThrows(
                PdfEditorException.class,
                () -> new PdfEditorCommandLineArgs(
                        new DefaultApplicationArguments(
                                "--in",
                                "--mdt",
                                "--mdv",
                                "--out"
                        )
                )
        );
    }

    @Test
    void emptyOptionValues() {
        Assertions.assertThrows(
                PdfEditorException.class,
                () -> new PdfEditorCommandLineArgs(
                        new DefaultApplicationArguments(
                                "--in=",
                                "--mdt=",
                                "--mdv=",
                                "--out="
                        )
                )
        );
    }


    private ApplicationArguments createAppArgs() {
        return new DefaultApplicationArguments(TEST_ARGS);

    }

    private ApplicationArguments createAppArgs(final String nf, final String of, final String mdn, final String mdv) {
         final String[] args = {
            "-v",
                    "-d",
                    "--in=" + nf,
                    "--out=" + of,
                    "--mdt=" + mdn,
                    "--mdv=" + mdv
        };
        return new DefaultApplicationArguments(args);
    }
}