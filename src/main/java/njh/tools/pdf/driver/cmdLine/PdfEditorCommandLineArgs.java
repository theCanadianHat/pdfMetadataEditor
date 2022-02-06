package njh.tools.pdf.driver.cmdLine;

import lombok.extern.slf4j.Slf4j;
import njh.tools.pdf.driver.PdfEditorDriver;
import org.apache.logging.log4j.core.util.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.System.exit;

/**
 * Expects arguments to explain what this application should do.
 * Flags:
 *  - debug: -d
 *  - verbose: -v
 * Input File: --in=<file_path/>
 * Output file location: --out=<file_path/>
 * New Metadata tag name: --mdt=<new_metadata_tag_name/>
 * New metadata tag contents: --mdv=<value_for_new_metadata_tag/>
 */
@Slf4j
public class PdfEditorCommandLineArgs {
    private final ApplicationArguments arguments;
    private Boolean verbose = false;
    private Boolean debug = false;
    private Boolean valid = false;

    private static final String USAGE_FILE = "/usage.txt";

    private static final String VERBOSE_FLAG = "-v";
    private static final String DEBUG_FLAG = "-d";
    private static final String INPUT_FILE_OPTION = "in";
    private static final String OUTPUT_FILE_OPTION = "out";
    private static final String METADATA_TAG_NAME_OPTION = "mdt";
    private static final String METADATA_TAG_VALUE_OPTION = "mdv";

    public PdfEditorCommandLineArgs(final @NotNull ApplicationArguments arguments) {
        this.arguments = Objects.requireNonNull(arguments);
        if(!arguments.getNonOptionArgs().isEmpty()) {
            this.verbose = arguments.getNonOptionArgs().contains(VERBOSE_FLAG);
            this.debug = arguments.getNonOptionArgs().contains(DEBUG_FLAG);
        }
        validateArguments();
    }

    public boolean isVerboseEnabled() {
        return verbose;
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public boolean isValid() {
        return valid;
    }

    public String getInputFileName() {
        return arguments.getOptionValues(INPUT_FILE_OPTION).get(0);
    }

    public String getOutputFileName() {
        return arguments.getOptionValues(OUTPUT_FILE_OPTION).get(0);
    }

    public String getMetadataTagName() {
        return arguments.getOptionValues(METADATA_TAG_NAME_OPTION).get(0);
    }

    public String getMetadataTagValue() {
        return arguments.getOptionValues(METADATA_TAG_VALUE_OPTION).get(0);
    }

    public void logArguments() {
        if(verbose) {
            log.info("Arguments Provided:");
            log.info("\tInput Filename: " + getInputFileName());
            log.info("\tOutput Filename: " + getOutputFileName());
            log.info("\tNew Metadata tag name: " + getMetadataTagName());
            log.info("\tNew Metadata value: " + getMetadataTagValue());
        }
    }

    private void printUsage() {
        try {
            System.out.println(
                    IOUtils.toString(
                            new InputStreamReader(
                                    Objects.requireNonNull(PdfEditorDriver.class.getResourceAsStream(USAGE_FILE))
                            )
                    )
            );
        } catch (IOException | NullPointerException e) {
            if(debug) e.printStackTrace();
            System.out.println("Unable to print usage.");
            System.out.println("Enable debug to print stacktrace.");
        }
    }

    private void validateArguments() {
        if(arguments == null) {
            printUsage();
            exit(-1);
        }

        if(debug) log.debug("Validating arguments.");

        if(arguments.getOptionNames().isEmpty()
                || arguments.getOptionNames().size() < 4
                || arguments.getOptionNames().size() > 4) {
            if(debug) log.debug("Provided arguments don't match expected count.");
            printUsage();
            exit(-1);
        } else {
            if(debug) log.debug("Provided arguments match expected count.");
            Map<String, Boolean> requiredArgs = getRequiredArgs();
            for (Map.Entry<String, Boolean> entry : requiredArgs.entrySet()) {
                if(entry.getValue() && !arguments.getOptionNames().contains(entry.getKey())) {
                    log.error("unable to find required argument");
                    log.error("The arg \"" + entry.getKey() + "\" is missing");
                    exit(-1);
                }
            }
            if(debug) log.debug("Provided arguments contain all required options.");
            Set<String> optionNames = arguments.getOptionNames();
            for(String optionName : optionNames) {
                List<String> optionValues = arguments.getOptionValues(optionName);
                int optionValueSize = optionValues.size();
                if(optionValueSize > 1) {
                    log.error("Please only provide a single value per option");
                    log.error("Option: \"" + optionName + "\" contains " + optionValueSize + " options.");
                    log.error("Option Values provided: " + optionValues);
                    exit(-1);
                }
            }
            if(debug){
                log.debug("Provided arguments contain singleton option values.");
                log.debug("Arguments are valid.");
            }
            valid = true;
        }
    }

    private Map<String, Boolean> getRequiredArgs() {
        final Map<String, Boolean> requiredArgs = new HashMap<>();

        requiredArgs.put(INPUT_FILE_OPTION, true);
        requiredArgs.put(OUTPUT_FILE_OPTION, true);
        requiredArgs.put(METADATA_TAG_NAME_OPTION, true);
        requiredArgs.put(METADATA_TAG_VALUE_OPTION, true);

        return requiredArgs;
    }
}
