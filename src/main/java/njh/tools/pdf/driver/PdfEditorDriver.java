package njh.tools.pdf.driver;

import lombok.extern.slf4j.Slf4j;
import njh.tools.pdf.driver.cmdLine.PdfEditorCommandLineArgs;
import njh.tools.pdf.editor.PdfEditor;
import njh.tools.pdf.editor.exception.PdfEditorException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.System.exit;

@SpringBootApplication
@Slf4j
public class PdfEditorDriver implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(PdfEditorDriver.class, args);
    }


    @Override
    public void run(ApplicationArguments args) {

        try {
            PdfEditorCommandLineArgs driverArgs = new PdfEditorCommandLineArgs(args);
            PdfEditor pdfEditor = new PdfEditor(driverArgs);
            if(driverArgs.isValid()) {
                driverArgs.logArguments();

                pdfEditor.addMetadataTag();
                exit(0);
            }
        } catch (PdfEditorException e) {
            System.out.println("I was unable to update your PDF :(");
            System.out.println(e.humanReadable());
            exit(-1);
        }

    }


}
