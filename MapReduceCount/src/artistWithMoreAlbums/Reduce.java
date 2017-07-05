package artistWithMoreAlbums;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Reduce extends Reducer<Text, Text, Text, Text> {

	Text countText = new Text();

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		int count = 0;
		String artistName = null;
		for (Text t : values) {
			String removeBeginning = t.toString().split("\\{Name: artistName,Value: ")[1];
			artistName = removeBeginning.substring(0, removeBeginning.indexOf(",}"));
			count++;
		}

		if (!artistName.equals("")) {
			key.set(artistName);
			countText.set(String.valueOf(count));
			context.write(key, countText);
		}
	}
}