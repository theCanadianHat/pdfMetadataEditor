PDF Metadata Editor
=
Simple Spring boot app that allows you to add custom
metadata tags and values to a PDF. This application 
requires parameters to function properly.

## Usage
You may choose to run this application via your IDE or by using
maven to package this application into an executable jar.
Expected Parameters:
- `--in` &nbsp;&nbsp;: The file path to the input PDF
- `--out` : The file path to the output file (including new file name)
- `--mdt` : The new metadata tag name
- `--mdv` : Tne new metadata tag value

### Using the executable jar
<b>This requires Maven</b>  
Navigate to the project's `pom.xml` file and packge the project.
<pre>mvn package</pre>
This will create a `pdfMetadataEditor-<version>.jar` in the projects `target` folder. This is the executable jar.  
Run the jar with arguments
<pre>java -jar pdfMetadataEditor-&lt;version&gt;.jar --in=&lt;path_to_input_pdf&gt; --out=&lt;path_to_output_pdf&gt; --mdt=&lt;custom_metadata_tag_name&gt; --mdv=&lt;metadata_tag_value&gt;</pre>