package sss.resources;

import com.db4o.ObjectContainer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import sss.texttools.normalizer.Normalizer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class CorpusReader {
    public abstract void read(IndexWriter writer, ObjectContainer db, File[] files, List<Normalizer> normalizers) throws IOException;

    protected void addDoc(IndexWriter w, String question, String answer) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("question", question, Field.Store.YES));
        doc.add(new TextField("answer", answer, Field.Store.YES));
        w.addDocument(doc);
    }
}
