package br.unicamp.cst.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.util.CodeletsProfiler.CodeletTrack;
import br.unicamp.cst.util.CodeletsProfiler.FileFormat;

public class CodeletsProfilerTest {
      
     private String filePath = "D:\\\\Projeto Ericsson-Unicamp\\\\codelet_profiler_test\\\\";
     private String fileNameCSV_1 = "file_in_csv_1.txt";
     private String fileNameJSON_1 = "file_in_json_1.txt";
     private String fileNameJSON_2 = "file_in_json_2.txt";
     private final static String comma = ",";
     private final static String csvSeparator = ";";
     

	@BeforeClass
	public static void beforeAllTestMethods() {
	}

	@AfterClass
	public static void afterAllTestMethods() {

	}

	private List<List<String>> readCSVFile(String fileName) {
		BufferedReader br = null;
		List<List<String>> records = new ArrayList<>();
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();

			while ((line = br.readLine()) != null) {
		        String[] values = line.split(comma);
		        records.add(Arrays.asList(values));
		    }
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return records;
	}
	
	private JsonArray readJSONFile(String fileName) {
		JsonArray data = null;
		BufferedReader br = null;
        try
        {
        	br = new BufferedReader(new FileReader(fileName));
    		JsonParser parser = new JsonParser();
    		data = parser.parse(br).getAsJsonArray();
 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return data;
	}
	
	private void deleteFile(String fileName) {
		try  
		{         
			File f= new File(fileName);      
			f.delete(); 
		}  
		catch(Exception e)  {  
			e.printStackTrace();  
		}    
	}
	
	private void assertsCodeletNameInCSVFile(List<List<String>> textCSV, String codeletName) {
		  for (List<String> item : textCSV) {
	        	 for (String line : item) {
	        		 String[] values = line.split(csvSeparator);
	        		 assertEquals(values[2], codeletName);
	        	 }
	        	 
	         }
	}
	
	private void assertsCodeletNameInJSONFile(JsonArray codeletsTrack, String codeletName) {
        for (int i = 0; i < (codeletsTrack.size() - 1); i++) {
       	 assertEquals(codeletsTrack.get(i).getAsJsonObject().get("codeletName").getAsString(), codeletName);
        }
	}

	@Test
    public void testCodeletsProfiler() throws InterruptedException {
		
    	 Mind m = new Mind();
         MemoryObject m1 = m.createMemoryObject("M1", 0.12);
         MemoryObject m2 = m.createMemoryObject("M2", 0.32);
         MemoryObject m3 = m.createMemoryObject("M3", 0.44);
         MemoryObject m4 = m.createMemoryObject("M4", 0.52);
         MemoryObject m5 = m.createMemoryObject("M5", 0.12);
         MemoryContainer m6 = m.createMemoryContainer("C1");
         MemoryContainer m7 = m.createMemoryContainer("C2");
         TestComplexMemoryObjectInfo mComplex = new TestComplexMemoryObjectInfo();
         mComplex.complextest = new TestComplexMemoryObjectInfo();
         for (int i=0;i<3;i++)
        	 mComplex.complextestarray[i] = new TestComplexMemoryObjectInfo();
         MemoryObject mo = new MemoryObject();
         mo.setType("TestObject");
         mo.setI(mComplex);
         m7.setI(0.55, 0.23);
         m6.setI(0.33, 0.22);
         m6.setI(0.12, 0.13);
         m6.setI(m7);
         Codelet c = new TestCodelet("Codelet 1");
         c.addInput(m1);
         c.addInput(m2);
         c.addOutput(m3);
         c.addOutput(m4);
         c.addBroadcast(m5);
         c.addBroadcast(mo);
         c.setCodeletProfiler(filePath, fileNameCSV_1, "Mind 1", 10, null, FileFormat.CSV);
         m.insertCodelet(c);
         Codelet c2 = new TestCodelet("Codelet 2");
         c2.addInput(m4);
         c2.addInput(m5);
         c2.addOutput(m6);
         c2.addOutput(m3);
         c2.addBroadcast(m5);
         c2.setCodeletProfiler(filePath, fileNameJSON_1, "Mind 1", 10, null, FileFormat.JSON);
         m.insertCodelet(c2);
         m.start();
         Thread.sleep(2000);
         m.shutDown();
         
         List<List<String>> textCSV = readCSVFile(filePath+fileNameCSV_1);         
         assertsCodeletNameInCSVFile(textCSV, "Codelet 1");
         
         deleteFile(filePath+fileNameCSV_1);
         
 		 JsonArray codeletsTrack = readJSONFile(filePath+fileNameJSON_1);
         assertsCodeletNameInJSONFile(codeletsTrack, "Codelet 2");

         deleteFile(filePath+fileNameJSON_1);
    	
    }

	@Test
	public void testCodeletsProfiler2() throws InterruptedException {
		Mind m = new Mind();
		MemoryObject m1 = m.createMemoryObject("M1", 0.12);
		MemoryObject m2 = m.createMemoryObject("M2", 0.32);
		MemoryObject m3 = m.createMemoryObject("M3", 0.44);
		MemoryObject m4 = m.createMemoryObject("M4", 0.52);
		MemoryObject m5 = m.createMemoryObject("M5", 0.12);
		MemoryContainer m6 = m.createMemoryContainer("C1");
		MemoryContainer m7 = m.createMemoryContainer("C2");
		TestComplexMemoryObjectInfo mComplex = new TestComplexMemoryObjectInfo();
		mComplex.complextest = new TestComplexMemoryObjectInfo();
		for (int i = 0; i < 3; i++)
			mComplex.complextestarray[i] = new TestComplexMemoryObjectInfo();
		MemoryObject mo = new MemoryObject();
		mo.setType("TestObject");
		mo.setI(mComplex);
		m7.setI(0.55, 0.23);
		m6.setI(0.33, 0.22);
		m6.setI(0.12, 0.13);
		m6.setI(m7);
		Codelet c = new TestCodelet("Codelet 1");
		c.addInput(m1);
		c.addInput(m2);
		c.addOutput(m3);
		c.addOutput(m4);
		c.addBroadcast(m5);
		c.addBroadcast(mo);
		c.setCodeletProfiler(filePath, fileNameJSON_1, "Mind 1", null, (long) 1000, FileFormat.JSON);
		m.insertCodelet(c);
		Codelet c2 = new TestCodelet("Codelet 2");
		c2.addInput(m4);
		c2.addInput(m5);
		c2.addOutput(m6);
		c2.addOutput(m3);
		c2.addBroadcast(m5);
		c2.setCodeletProfiler(filePath, fileNameJSON_2, "Mind 1", null, (long) 1000, FileFormat.JSON);
		m.insertCodelet(c2);
		m.start();
		Thread.sleep(2000);
		m.shutDown();
		
		JsonArray codeletsTrack1 = readJSONFile(filePath+fileNameJSON_1);
        assertsCodeletNameInJSONFile(codeletsTrack1, "Codelet 1");

        deleteFile(filePath+fileNameJSON_1);
        
		JsonArray codeletsTrack2 = readJSONFile(filePath+fileNameJSON_2);
        assertsCodeletNameInJSONFile(codeletsTrack2, "Codelet 2");

        deleteFile(filePath+fileNameJSON_2);

	}

	@Test
	public void testCodeletsProfiler3() throws InterruptedException {
		Mind m = new Mind();
		MemoryObject m1 = m.createMemoryObject("M1", 0.12);
		MemoryObject m2 = m.createMemoryObject("M2", 0.32);
		MemoryObject m3 = m.createMemoryObject("M3", 0.44);
		MemoryObject m4 = m.createMemoryObject("M4", 0.52);
		MemoryObject m5 = m.createMemoryObject("M5", 0.12);
		MemoryContainer m6 = m.createMemoryContainer("C1");
		MemoryContainer m7 = m.createMemoryContainer("C2");
		TestComplexMemoryObjectInfo mComplex = new TestComplexMemoryObjectInfo();
		mComplex.complextest = new TestComplexMemoryObjectInfo();
		for (int i = 0; i < 3; i++)
			mComplex.complextestarray[i] = new TestComplexMemoryObjectInfo();
		MemoryObject mo = new MemoryObject();
		mo.setType("TestObject");
		mo.setI(mComplex);
		m7.setI(0.55, 0.23);
		m6.setI(0.33, 0.22);
		m6.setI(0.12, 0.13);
		m6.setI(m7);
		Codelet c = new TestCodelet("Codelet 1");
		c.addInput(m1);
		c.addInput(m2);
		c.addOutput(m3);
		c.addOutput(m4);
		c.addBroadcast(m5);
		c.addBroadcast(mo);
		m.insertCodelet(c);
		Codelet c2 = new TestCodelet("Codelet 2");
		c2.addInput(m4);
		c2.addInput(m5);
		c2.addOutput(m6);
		c2.addOutput(m3);
		c2.addBroadcast(m5);
		c2.setCodeletProfiler(filePath, fileNameCSV_1, "Mind 1", null, (long) 1000, FileFormat.CSV);
		m.insertCodelet(c2);
		m.start();
		Thread.sleep(2000);
		m.shutDown();
		
		List<List<String>> textCSV = readCSVFile(filePath+fileNameCSV_1);         
        assertsCodeletNameInCSVFile(textCSV, "Codelet 2");
         
        deleteFile(filePath+fileNameCSV_1);

	}

	@Test
	public void testCodeletsProfiler4() throws InterruptedException {
		Mind m = new Mind();
		MemoryObject m1 = m.createMemoryObject("M1", 0.12);
		MemoryObject m2 = m.createMemoryObject("M2", 0.32);
		MemoryObject m3 = m.createMemoryObject("M3", 0.44);
		MemoryObject m4 = m.createMemoryObject("M4", 0.52);
		MemoryObject m5 = m.createMemoryObject("M5", 0.12);
		MemoryContainer m6 = m.createMemoryContainer("C1");
		MemoryContainer m7 = m.createMemoryContainer("C2");
		TestComplexMemoryObjectInfo mComplex = new TestComplexMemoryObjectInfo();
		mComplex.complextest = new TestComplexMemoryObjectInfo();
		for (int i = 0; i < 3; i++)
			mComplex.complextestarray[i] = new TestComplexMemoryObjectInfo();
		MemoryObject mo = new MemoryObject();
		mo.setType("TestObject");
		mo.setI(mComplex);
		m7.setI(0.55, 0.23);
		m6.setI(0.33, 0.22);
		m6.setI(0.12, 0.13);
		m6.setI(m7);
		Codelet c = new TestCodelet("Codelet 1");
		c.addInput(m1);
		c.addInput(m2);
		c.addOutput(m3);
		c.addOutput(m4);
		c.addBroadcast(m5);
		c.addBroadcast(mo);
		c.setCodeletProfiler(filePath, fileNameJSON_1, "Mind 1", 5, null, FileFormat.JSON);
		m.insertCodelet(c);
		Codelet c2 = new TestCodelet("Codelet 2");
		c2.addInput(m4);
		c2.addInput(m5);
		c2.addOutput(m6);
		c2.addOutput(m3);
		c2.addBroadcast(m5);
		c2.setCodeletProfiler(filePath, fileNameCSV_1, "Mind 1", 5, (long) 1000, FileFormat.CSV);
		m.insertCodelet(c2);
		m.start();
		Thread.sleep(2000);
		m.shutDown();
		
		JsonArray codeletsTrack = readJSONFile(filePath+fileNameJSON_1);
	    assertsCodeletNameInJSONFile(codeletsTrack, "Codelet 1");

	    deleteFile(filePath+fileNameJSON_1);
		
		List<List<String>> textCSV = readCSVFile(filePath+fileNameCSV_1);         
        assertsCodeletNameInCSVFile(textCSV, "Codelet 2");
        
        deleteFile(filePath+fileNameCSV_1);
        
	}

	@Test
	public void testCodeletsProfiler5() throws InterruptedException {
		Mind m = new Mind();
		MemoryObject m1 = m.createMemoryObject("M1", 0.12);
		MemoryObject m2 = m.createMemoryObject("M2", 0.32);
		MemoryObject m3 = m.createMemoryObject("M3", 0.44);
		MemoryObject m4 = m.createMemoryObject("M4", 0.52);
		MemoryObject m5 = m.createMemoryObject("M5", 0.12);
		MemoryContainer m6 = m.createMemoryContainer("C1");
		MemoryContainer m7 = m.createMemoryContainer("C2");
		TestComplexMemoryObjectInfo mComplex = new TestComplexMemoryObjectInfo();
		mComplex.complextest = new TestComplexMemoryObjectInfo();
		for (int i = 0; i < 3; i++)
			mComplex.complextestarray[i] = new TestComplexMemoryObjectInfo();
		MemoryObject mo = new MemoryObject();
		mo.setType("TestObject");
		mo.setI(mComplex);
		m7.setI(0.55, 0.23);
		m6.setI(0.33, 0.22);
		m6.setI(0.12, 0.13);
		m6.setI(m7);
		Codelet c = new TestCodelet("Codelet 1");
		c.addInput(m1);
		c.addInput(m2);
		c.addOutput(m3);
		c.addOutput(m4);
		c.addBroadcast(m5);
		c.addBroadcast(mo);
		c.setCodeletProfiler(filePath, fileNameJSON_1, "Mind 1", null, null, FileFormat.JSON);
		m.insertCodelet(c);
		Codelet c2 = new TestCodelet("Codelet 2");
		c2.addInput(m4);
		c2.addInput(m5);
		c2.addOutput(m6);
		c2.addOutput(m3);
		c2.addBroadcast(m5);
		c2.setCodeletProfiler(filePath, fileNameCSV_1, "Mind 1", null, null, FileFormat.CSV);
		m.insertCodelet(c2);
		m.start();
		Thread.sleep(2000);
		m.shutDown();
		
		JsonArray codeletsTrack = readJSONFile(filePath+fileNameJSON_1);
	    assertsCodeletNameInJSONFile(codeletsTrack, "Codelet 1");

	    deleteFile(filePath+fileNameJSON_1);
		
		List<List<String>> textCSV = readCSVFile(filePath+fileNameCSV_1);         
        assertsCodeletNameInCSVFile(textCSV, "Codelet 2");
        
        deleteFile(filePath+fileNameCSV_1);

	}

}
