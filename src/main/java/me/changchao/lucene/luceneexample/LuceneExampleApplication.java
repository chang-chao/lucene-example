package me.changchao.lucene.luceneexample;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LegacyIntField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.LegacyNumericRangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.apachecommons.CommonsLog;

@SpringBootApplication
@CommonsLog
public class LuceneExampleApplication implements CommandLineRunner {
	public static final int MAX_RESULTS = Integer.MAX_VALUE;

	public static void main(String[] args) {
		SpringApplication.run(LuceneExampleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String destination = "index";
		String intFieldName = "intField";

		int docCount = 1000;

		File dstFile = new File(destination);
		if (dstFile.exists()) {
			FileUtils.forceDelete(dstFile);
		}

		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		FSDirectory directory = FSDirectory.open(Paths.get(destination));
		try (final IndexWriter writer = new IndexWriter(directory, config);) {

			for (int i = 0; i < docCount; i++) {
				Document doc = new Document();
				doc.add(new LegacyIntField(intFieldName, i, Store.YES));
				writer.addDocument(doc);
			}
		}

		IndexReader reader = DirectoryReader.open(directory);
		final IndexSearcher searcher = new IndexSearcher(reader);

		// QueryParser parser = new QueryParser("id", analyzer);
		// String searchString = "1";
		// Query query = parser.parse(searchString);

		// Query query = IntPoint.newSetQuery(intFieldName, 1, 2, 3, 4, 5);
		LegacyNumericRangeQuery<Integer> query = LegacyNumericRangeQuery.newIntRange(intFieldName, 200, 500, true, false);
		TopDocs result = searcher.search(query, MAX_RESULTS);
		ScoreDoc[] docs = result.scoreDocs;
		for (ScoreDoc scoreDoc : docs) {
			log.debug("scoreDoc:" + scoreDoc);
		}

	}
}
