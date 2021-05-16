package ParquetFile.testFile;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.format.converter.ParquetMetadataConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.io.ColumnIOFactory;
import org.apache.parquet.io.MessageColumnIO;
import org.apache.parquet.io.RecordReader;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;
 
public class Main {
 
  private static Path path = new Path("hdfs://hadoop-master.openshift.sdntest.netcracker.com:9870/datahub/netcracker/staging/r_pb_phone_number_owner/2020-10-19-11-39-16/part-00000-4885df51-8aa6-48b9-beae-2eb0935a1a77-c000.snappy.parquet");
 
  private static void printGroup(Group g) {
    int fieldCount = g.getType().getFieldCount();
    for (int field = 0; field< fieldCount; field++) {
      int valueCount = g.getFieldRepetitionCount(field);
 
      Type fieldType = g.getType().getType(field);
      String fieldName = fieldType.getName();
     
 
      for (int index = 0; index < valueCount; index++) {
        if (fieldType.isPrimitive()) {
          System.out.println(fieldName);
          
        }
      }
    }
    System.out.println("");
  }
 
  public static void main(String[] args) throws IllegalArgumentException {
 
    Configuration conf = new Configuration();
 
    try {
      ParquetMetadata readFooter = ParquetFileReader.readFooter(conf, path, ParquetMetadataConverter.NO_FILTER);
      MessageType schema = readFooter.getFileMetaData().getSchema();
      ParquetFileReader r = new ParquetFileReader(conf, path, readFooter);
 
      PageReadStore pages = null;
      try {
        while (null != (pages = r.readNextRowGroup())) {
          final long rows = pages.getRowCount();
          System.out.println("Number of rows: " + rows);
 
          final MessageColumnIO columnIO = new ColumnIOFactory().getColumnIO(schema);
          final RecordReader recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema));
          for (int i = 0; i < rows; i++) {
            final Group g = (Group) recordReader.read();
            printGroup(g);
 
            // TODO Compare to System.out.println(g);
          }
        }
      } finally {
        r.close();
      }
    } catch (IOException e) {
      System.out.println("Error reading parquet file.");
      e.printStackTrace();
    }
  }
}
