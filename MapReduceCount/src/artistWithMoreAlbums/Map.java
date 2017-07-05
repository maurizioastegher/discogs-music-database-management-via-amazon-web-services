package artistWithMoreAlbums;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Map extends Mapper<LongWritable, Text, Text, Text>{
	
    private Text idText = new Text();

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	String line = value.toString();
    	String id = line.split("\t")[0];
    	idText.set(id);
        context.write(idText, value);
    }
}