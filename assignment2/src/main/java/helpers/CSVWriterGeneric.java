package helpers;

import de.siegmar.fastcsv.writer.CsvWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

public class CSVWriterGeneric {

    private String rootPath;
    private Set<String> intermediatePaths;
    private Path fullBasePath;
    private int iteration = 0;
    private String currentInterPath = "";
    private String uniqueD = "";


    private CsvWriter csvWriter;
    //    private Path csvDirPath;
    private String metadata;

    public CSVWriterGeneric(String rootPath, Set<String> intermediatePaths, String metadata) {
        this.rootPath = rootPath;
        this.intermediatePaths = intermediatePaths;
        this.metadata = metadata;
        ensurePaths();
    }

    public CSVWriterGeneric(String rootPath, Set<String> intermediatePaths) {
        this(rootPath, intermediatePaths, null);
    }

    private void ensurePaths() {

        this.uniqueD = Utils.uniqueDirName();
        this.fullBasePath = Path.of(this.rootPath, this.uniqueD);
//        File problemDir = new File(this.rootPath,ph.getProblemName()+File.separator+uniqueD );
        File rootDir = fullBasePath.toFile();
        boolean created = rootDir.mkdirs();
        if (!created)
            System.out.printf("The unique output directory for results already exists %s, exiting\n", rootDir);


        File metaFile = new File(this.rootPath, this.uniqueD + "_meta.txt");
        if (this.metadata != null) writeMeta(metaFile, this.uniqueD);

        for (String intPath : this.intermediatePaths) {
            Path csvDirPath = Path.of(rootDir.toString(), intPath);
            File intermediateDir = csvDirPath.toFile();
            created = intermediateDir.mkdirs();
            if (!created)
                System.out.printf("The unique output directory for results already exists %s, exiting\n", intermediateDir);
        }


    }

    public int incremenetIteration() {
        return this.iteration++;
    }

    public int getIteration() {
        return iteration;
    }

    public String getUniqueD() {
        return uniqueD;
    }

    private Path makeCsvPath(String intermediatePath) {
        String fileString = String.format("%02d.csv", this.iteration);
        return Path.of(this.fullBasePath.toString(), intermediatePath, fileString);
    }

    public void writeHeader(Iterable<String> values, String itermediatePath) {
        Path csvPath = makeCsvPath(itermediatePath);
        this.currentInterPath = itermediatePath;
        writeRow(values, csvPath);
    }

    public void writeRow(Iterable<String> values, String itermediatePath) {
        Path csvPath = makeCsvPath(itermediatePath);
        writeRow(values, csvPath);
    }

    public void writeRow(Iterable<String> values) {
        Path csvPath = makeCsvPath(this.currentInterPath);
        writeRow(values, csvPath);
    }


    private void writeRow(final Iterable<String> values, Path csvPath) {
        try (CsvWriter csv = CsvWriter.builder().build(csvPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            this.csvWriter = csv;
            this.csvWriter.writeRow(values);
        } catch (IOException e) {
            System.out.println("Cannot write to csv file at path:" + csvPath);
            e.printStackTrace();
        }

    }

    private void writeMeta(File outfile, String uniqueD) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        String header = String.format("       RunId: %s   Run time: %s \n ===================================\n", uniqueD, sdf1.format(ts));
        try {
            FileWriter myWriter = new FileWriter(outfile);

            myWriter.write(header);
            myWriter.write(this.metadata);
            myWriter.close();
            System.out.printf("Successfully wrote metat file the file %s\n", outfile);
        } catch (IOException e) {
            System.out.printf("An error occurred writing meta file %s\n", outfile);
            e.printStackTrace();
        }
    }
}
