package com.itheima.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class LuceneFirst {
    @Test
    public void testLucene() throws IOException {
        // 1. 创建Directory对象,指定索引库的保存位置
        Directory directory = FSDirectory.open (new File("E:\\code\\lucene\\index").toPath ());
        // 2. 基于Directory对象创建IndexWriter对象
        IndexWriter indexWriter = new IndexWriter (directory,new IndexWriterConfig ());
        // 3. 读取磁盘上的文件，每个文件对应创建一个Document文档对象
        File dir = new File ("E:\\code\\lucene\\searchsource");
        for (File file : dir.listFiles ()) {
            // 文件名
            String fileName = file.getName ();
            // 文件路径
            String filePath = file.getPath ();
            // 文件内容
            String fileContent = FileUtils.readFileToString (file, "utf-8");
            // 文件大小
            long fileSize = FileUtils.sizeOf (file);

            // 创建Field
            // 参数1： 域的名称  参数2： 域的内容  参数3：是否存储
            TextField fieldName = new TextField ("name", fileName, Field.Store.YES);
            TextField fieldPath = new TextField ("path", filePath, Field.Store.YES);
            TextField fieldContent = new TextField ("content", fileContent, Field.Store.YES);
            TextField fieldSize = new TextField ("size", fileSize + "", Field.Store.YES);

            // 4. 向文档对象中添加域
            Document document = new Document ();
            document.add (fieldName);
            document.add (fieldPath);
            document.add (fieldContent);
            document.add (fieldSize);

            // 5. 把文档对象写入索引库
            indexWriter.addDocument (document);
        }

        // 6. 关闭IndexWriter
        indexWriter.close ();
        directory.close ();
    }

    @Test
    public void serachIndex () throws Exception {
        // 1. 创建一个Directory对象，指定索引库的位置
        Directory directory = FSDirectory.open (new File ("E:\\code\\lucene\\index").toPath ());
        // 2. 创建一个IndexReader对象
        IndexReader indexReader = DirectoryReader.open (directory);
        // 3. 创建一个IndexSearch对象，构造方法中的参数是indexReader对象
        IndexSearcher indexSearcher = new IndexSearcher (indexReader);
        // 4. 创建一个Query对象，TermQuery
       // new Term(关键词所在域，关键词)
        Query query = new TermQuery (new Term ("content", "spring"));
        // 5. 执行查询，得到一个TopDocs对象
        // 参数1：查询对象  参数2：查询结果返回的最大记录数
        TopDocs topDocs = indexSearcher.search (query, 10);
        // 6. 取查询结果的总记录数
        System.out.println ("查询总记录数：" + topDocs.totalHits);
        // 7. 取文档列表
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        // 8. 打印文档内容
        for (ScoreDoc doc : scoreDocs) {
            // 取文档id
            int docId = doc.doc;
            // 根据id取文档对象
            Document document = indexSearcher.doc (docId);
            System.out.println (document.get ("name"));
            System.out.println (document.get ("path"));
            System.out.println (document.get ("size"));
//            System.out.println (document.get ("content"));
            System.out.println ("---------分割线--------");
        }
        // 9. 关闭IndexReader对象
        indexReader.close ();
        directory.close ();
    }

}
