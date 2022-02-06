package njh.tools.pdf.driver.cmdLine;

import lombok.extern.slf4j.Slf4j;
import njh.tools.pdf.driver.PdfEditorDriver;
import njh.tools.pdf.editor.exception.PdfEditorException;
import org.apache.logging.log4j.core.util.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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
    private final Map<String, Boolean> requiredArgs;
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
            verbose = arguments.getNonOptionArgs().contains(VERBOSE_FLAG);
            debug = arguments.getNonOptionArgs().contains(DEBUG_FLAG);
        }
        requiredArgs = new HashMap<>();
        requiredArgs.put(INPUT_FILE_OPTION, true);
        requiredArgs.put(OUTPUT_FILE_OPTION, true);
        requiredArgs.put(METADATA_TAG_NAME_OPTION, true);
        requiredArgs.put(METADATA_TAG_VALUE_OPTION, true);

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
            throw new PdfEditorException("Unable to print Usage.");
        }
    }

    private void validateArguments() {
        if(debug) log.debug("Validating arguments.");
        int requiredArgsCount = getRequiredArgsCount();

        if(arguments.getOptionNames().isEmpty()
                || arguments.getOptionNames().size() < requiredArgsCount
                || arguments.getOptionNames().size() > requiredArgsCount ) {
            if(debug) log.debug("Provided arguments don't match expected count.");
            printUsage();
            throw new PdfEditorException("You didn't provide enough arguments.");
        } else {
            if(debug) log.debug("Provided arguments match expected count.");
            for (Map.Entry<String, Boolean> entry : requiredArgs.entrySet()) {
                if(entry.getValue() && !arguments.getOptionNames().contains(entry.getKey())) {
                    log.error("unable to find required argument");
                    log.error("The arg \"" + entry.getKey() + "\" is missing");
                    throw new PdfEditorException("Missing arguments I require to do the work you asked me to.");
                }
            }
            if(debug) log.debug("Provided arguments contain all required options.");
            Set<String> optionNames = arguments.getOptionNames();
            for(String optionName : optionNames) {
                List<String> optionValues = arguments.getOptionValues(optionName);
                int optionValueSize = optionValues.size();
                if(optionValueSize != 1) {
                    log.error("Please only provide a single value per option");
                    log.error("Option: \"" + optionName + "\" contains " + optionValueSize + " options.");
                    if(optionValueSize > 0) {
                        log.error("Option Values provided: " + optionValues);
                    }
                    throw new PdfEditorException("Missing Or too many option value(s) for option " + optionName);
                } else if (!StringUtils.hasLength(StringUtils.trimAllWhitespace(optionValues.get(0)))) {
                    log.error("Please provide a non-empty value per option.");
                    log.error("Option: \"" + optionName + "\" does not contain an option value.");
                    throw new PdfEditorException("Please provide non-empty option values.") ;
                }
            }
            if(debug){
                log.debug("Provided arguments contain a single value.");
                log.debug("Arguments are valid.");
            }
            valid = true;
        }
    }

    private int getRequiredArgsCount() {
        int count = 0;
        for(Map.Entry<String, Boolean> entry : requiredArgs.entrySet()) {
            if(entry.getValue()) {
                count++;
            }
        }
        return count;
    }
}
