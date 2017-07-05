package artistsReleasesJoin;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Map extends Mapper<LongWritable, Text, Text, Text> {

	private Text idText = new Text();
	private Text attributesText = new Text();

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();

		if (line.contains("Name: realName")) {
			String removeBeginning = line.replaceFirst("\\{Name: ", "");
			String id = removeBeginning.substring(0, removeBeginning.indexOf(","));

			attributesText.set(line);
			idText.set(id);
			context.write(idText, attributesText);
		} else {
			String removeBeginning = line.split(" \\{Name: artists,Value: \\[")[1];
			String artists = removeBeginning.substring(0, removeBeginning.indexOf("]"));

			artists = artists.replaceAll("\"", "");
			String[] artistsArray = artists.split(",");

			for (String id : artistsArray) {
				if (!id.equals("")) {
					attributesText.set(line);
					idText.set(id);
					context.write(idText, attributesText);
				}
			}
		}
	}
}