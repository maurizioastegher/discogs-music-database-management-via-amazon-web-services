package artistsReleasesJoin;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Reduce extends Reducer<Text, Text, Text, Text> {
	
	

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		ArrayList<String> releases = new ArrayList<String>();
		String artist = null;
		Text output = new Text();
		String joinedArtistRelease;
		StringBuilder b = new StringBuilder();
		
		for (Text t : values) {
			joinedArtistRelease=t.toString();
			if (joinedArtistRelease.contains("Name: realName"))
				artist = joinedArtistRelease;
			else
				releases.add(joinedArtistRelease);
		}

		for (String s : releases) {
			joinedArtistRelease = s.split("Attributes: \\[")[1];

			if (artist != null) {
				b.setLength(0);
				b.append(artist);
				b.replace(artist.lastIndexOf("]}"), artist.lastIndexOf("]}") + 2, ", ");

				joinedArtistRelease = b.toString() + joinedArtistRelease;
				
				output.set(joinedArtistRelease);
				context.write(key, output);
			}
		}
		
	}
}