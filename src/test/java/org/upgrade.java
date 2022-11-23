package org;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;
import javax.swing.plaf.synth.SynthSeparatorUI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class upgrade {

	public static void main(String[] args) throws IOException, InterruptedException 
	{
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd - HH.mm.ss").format(new java.util.Date());
        System.out.println("\n** Execution Started : "+timeStamp+" **");
        
		//String ConfigPropFile = args[0];
        String ConfigPropFile = "C:\\Users\\MaralayS\\Documents\\Documents\\shivam\\Platform Testing Suite\\EdgeUpgrade\\config.properties";                        
        
		
		/**
		 * ********    READING PROPERTY FILE *************
		 */
					Properties prop = new Properties();
					InputStream input = null;
					File f = new File(ConfigPropFile);
			        input = new FileInputStream(f);	
					prop.load(input);
					
			        /**
			         * Handling JMeter location path
			         */
			        String JMETER_BATCHFILE_LOCATION = prop.getProperty("JMETER_LOCATION");
					// Check if file Exist or not 
					File file1 = new File(JMETER_BATCHFILE_LOCATION);
					if(!file1.exists())
					{
						System.out.println("\nJmeter bat file location is invalid = "+
								"\n kindly check that path is correct or you are putting single slash '\\' "
								+ "Since this is windows machine you have to use Doubleslash '\\\\' please check and try again");
						System.exit(1);
					}
					
					/**
					 * JMX_FOLDER_LOCATION -  - Checking '\' at last of the location
					 */
					String JMX_FOLDER_LOCATION  = prop.getProperty("JMX_FOLDER_LOCATION");
					int length = JMX_FOLDER_LOCATION.length()-1;
					if(JMX_FOLDER_LOCATION.charAt(length)=='\\')  // It means \ is present in path \\
					{
						JMX_FOLDER_LOCATION = prop.getProperty("JMX_FOLDER_LOCATION");
					}
					else
						JMX_FOLDER_LOCATION = prop.getProperty("JMX_FOLDER_LOCATION")+"\\";
					// Check if file exist or not //
					file1 = new File(JMX_FOLDER_LOCATION);
					if(!file1.exists())
					{
						System.out.println("JMX folder location is invalid = "+
								"\n kindly check that path is correct or you are putting single slash '\\' "
								+ "Since this is windows machine you have to use Doubleslash '\\\\' please check and try again");
						System.exit(1);
					}
				
		/**
		 * ******** STEP - 2  CHECKING the JMX folder containing any .JMX file or not  *************
		 */
					// Deleting the old / new file so that everytime new file will get created //
					DELETE_FILE("_NEW",JMX_FOLDER_LOCATION);
		    		DELETE_FILE("_OLD",JMX_FOLDER_LOCATION);
		    		
					System.out.println("Checking for JMX file in the JMX folder");
					// Getting all file within folder //
				    List<String> AllJMX = new ArrayList<String>();
				    File[] file = new File(JMX_FOLDER_LOCATION).listFiles();
				    for(File f1 : file)
				    {
				    	if(f1.isFile() && f1.getName().contains(".jmx"))
				    	{
				    		AllJMX.add(f1.getName());
				    		System.out.println("File present = "+f1.getName());
				    	}
				    }
				    System.out.println("Total File found = "+AllJMX.size());
				    if(AllJMX.size()==0)
				    {
				    	System.out.println("No valid JMX File inside this folder Kinldy check and try again");
				    	System.exit(1);
				    }
		
		
	    /**
		 * ******** STEP - 3  CHECKING every JMX - adding IP,PORT so that it can be totally ready for execution *************
		 */
				    int TotalFiles = AllJMX.size();
				    String COMPARE = prop.getProperty("COMPARE");
				    String OLD_JMX = "";
				    String NEW_JMX = "";
				    String Comparison_Results = "";
				    
				    for(int i =0;i<TotalFiles;i++)
				    {
				    	
				    	String JMX = AllJMX.get(i);
					    System.out.println("********** Running for "+(i+1)+" **********");
				    	if(COMPARE.equalsIgnoreCase("NO"))
				    	{
				    		CheckJMX(JMX,JMX_FOLDER_LOCATION,COMPARE,JMETER_BATCHFILE_LOCATION);
				    	}
				    	
				    	if(COMPARE.equalsIgnoreCase("YES")) // MEANS Old file is already create now we have to create NEW and COMPARE results.
				        {
				    		CheckJMX(JMX,JMX_FOLDER_LOCATION,COMPARE,JMETER_BATCHFILE_LOCATION);
				    		//System.out.println("NEW JMX Path "+NEW_JMX);
				    		OLD_JMX = JMX_FOLDER_LOCATION + "OLD\\"+JMX.split(".jmx")[0]+"_OLD.csv";
				    		NEW_JMX = JMX_FOLDER_LOCATION + "NEW\\"+JMX.split(".jmx")[0]+"_NEW.csv";
				    		
				    		String ResponseCodeResult = "";
				    		String ResponseMessage = "";
				    		String LatencyResults = "";
				    		
				    		// ** Comparing ResponseCode **//
				    		String Result1_RESPONSECODE = genericfun.ReadCSV(OLD_JMX,"responseCode").trim();
				    		String Result2_RESPONSECODE = genericfun.ReadCSV(NEW_JMX,"responseCode").trim();
				    		if(Result1_RESPONSECODE.equalsIgnoreCase(Result2_RESPONSECODE))
				    			ResponseCodeResult = "Response Code is Matched before and After Upgrade - PASS";
				    		else
				    			ResponseCodeResult = "Response Code is Different before and After Upgrade - FAIL";
				    		
				    		
				    		//** Comparing Response Message **//
				    		String Result1_RESPONSETEXT = genericfun.ReadCSV(OLD_JMX,"responseMessage").trim();
				    		String Result2_RESPONSETEXT = genericfun.ReadCSV(NEW_JMX,"responseMessage").trim();
				    		if(Result1_RESPONSECODE.equalsIgnoreCase(Result2_RESPONSECODE))
				    			ResponseMessage = "Response Text is matched before and after Upgrade - PASS";
				    		else
				    			ResponseMessage = "Response Text is different before and after Upgrade - FAIL";
				    		
				    		String Result1_Latency = genericfun.ReadCSV(OLD_JMX,"Latency").trim();
				    		String Result2_Latency = genericfun.ReadCSV(NEW_JMX,"Latency").trim();
				    		String[] latency1 = Result1_Latency.split(" ");
				    		String[] latency2 = Result2_Latency.split(" ");
				    		for(int counter=0;counter<latency1.length;counter++)
				    		{
				    			int oldlatencytime = Integer.parseInt(latency1[counter])/1000;  // Convert into seconds
				    			int newlatencytime = Integer.parseInt(latency2[counter])/1000;
				    			
				    			if(newlatencytime-oldlatencytime>=10)
				    			{
				    				LatencyResults = "Latency time after upgrade is more than previous by 10 second - FAIL";
				    				break;
				    			}				    				
				    			else
				    				LatencyResults = "Latency time after upgrade is less than previous - PASS";
				    		}
				    		
				    		Comparison_Results = Comparison_Results + JMX + " :- \n"+ResponseCodeResult +"\n"+ResponseMessage+"\n"+LatencyResults+"\n\n";
				    		// Writing this Result to a text file //
						    WriteFile(Comparison_Results, JMX_FOLDER_LOCATION+"FinalResults.txt");
				    	}				    	
				    }
				    
	    timeStamp = new SimpleDateFormat("yyyy.MM.dd - HH.mm.ss").format(new java.util.Date());
	    System.out.println("** Execution Completed : "+timeStamp+" **");
	    
			        
	}
	
	/**
	 * Check IP , Port , Result File name -FOLDER+NAME.csv
	 * function will create OLD.jmx , execute and save the results file in old.csv format 
	 * @param JMX_NAME - Name of JMX 
	 * @param Result_Type - OLD or NEW
	 * @return - The result file which is get created
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void CheckJMX(String JMX_NAME,String FOLDERPATH,String Result_Type,String JMeterlocation) throws IOException, InterruptedException
	{
		String JMX_LOCATION ="";
		JMX_LOCATION = FOLDERPATH+JMX_NAME;
        
			
		String Content = ReadFile(JMX_LOCATION);
		String FILENAME = "";
		
		
		// Check Result File //
		if(Result_Type.equalsIgnoreCase("NO"))
		{
			// Create JMX and Run with old.csv and save in Folder Path + "OLD"//
			FILENAME = JMX_LOCATION.split(".jmx")[0]+"_OLD.jmx";
			WriteFile(Content,FILENAME);
			System.out.println("File_OLD created successfully - "+FILENAME);
			
			// Run the JMX and save the results//
			String Result_location = FOLDERPATH+"OLD\\"+JMX_NAME.split(".jmx")[0]+"_OLD.csv";
			File temp = new File(Result_location);
			if(temp.exists()) 
			{
				temp.delete();
			}
			
			RunJmeter(JMeterlocation, FILENAME, Result_location);
		}
		else if(Result_Type.equalsIgnoreCase("YES"))
		{
			// Create JMX and Run with new.csv and save in Folder Path + "NEW"//
			FILENAME = JMX_LOCATION.split(".jmx")[0]+"_NEW.jmx";
			WriteFile(Content,FILENAME);
			System.out.println("File_NEW created successfully - "+FILENAME);
			
			// Run the JMX and save the results//
			String Result_location = FOLDERPATH+"NEW\\"+JMX_NAME.split(".jmx")[0]+"_NEW.csv";
			File temp = new File(Result_location);
			try {
					if(temp.exists()) 
					{
						temp.delete();
					}
			}catch(Exception e)
			{
				System.out.println("Unable to delete the file "+e);
			}
			RunJmeter(JMeterlocation, FILENAME, Result_location);
		}
		System.out.println("JMX file created and executed successfully for - "+FILENAME);		
	}
	
	/**
	 * Reading the content of the file
	 * @param FILENAME
	 * @return
	 */
	public static String ReadFile(String FILENAME)
    {
		String FileContent = "";
        try {
             LineIterator it = FileUtils.lineIterator(new File(FILENAME), "UTF-8");
            while (it.hasNext()) {
                String line = it.nextLine();
                FileContent = FileContent + line;
            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //LineIterator.closeQuietly(it);
        }
        return FileContent;
    }
	
	/**
	 * Writing the File Content to the Name
	 * @param FILECONTENT
	 * @param Name
	 * @return
	 */
	public static String WriteFile(String FILECONTENT,String Name)
	{
		try {
                BufferedWriter out = new BufferedWriter(new FileWriter(Name));
                out.write(FILECONTENT);
                out.newLine();
                out.close();
            }
            catch(IOException e)
            {
                System.out.println("Exception e"+e);
            }
		return Name;
	}
	
	/**
	 * Running Jmeter in Batch Mode 
	 * @param JmeterLocation
	 * @param File
	 * @param RESULTFILE_LOCATION
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void RunJmeter(String JmeterLocation,String File,String RESULTFILE_LOCATION) throws IOException, InterruptedException
	{
		String[] command =
	    {
	        "cmd",
	    };
	    Process p = Runtime.getRuntime().exec(command);
	    new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
	    new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
	    PrintWriter stdin = new PrintWriter(p.getOutputStream());
		//stdin.println("\"C:\\Users\\MaralayS\\Documents\\Documents\\Software\\apache-jmeter-3.2\\bin\\jmeter.bat\" -n -t \"\" -l \"C:\\Users\\MaralayS\\Documents\\Documents\\shivam\\Jmeter Framework\\Version1_Base\\JMX\\TestResult.csv\"");
	    String Combine = "\""+JmeterLocation+"\"" +" -n -t "+"\""+File+"\""+ " -l "+"\""+RESULTFILE_LOCATION+"\"";
	    System.out.println("Combine = "+Combine);
	    stdin.println(Combine);
	    stdin.close();
	    int returnCode = p.waitFor();
	    System.out.println("Completed for "+File);     
	}
	
		
	public static void DELETE_FILE(String extension,String LOCATION)
	{
		System.out.println("Deleting the file with extension _"+extension+".jmx on this Location - "+LOCATION);
		
		// Getting all file within folder //
	    File[] file = new File(LOCATION).listFiles();
	    for(File f1 : file)
	    {
	    	if(f1.isFile() && f1.getName().contains(extension+".jmx"))
	    	{
	    		f1.delete();
	    		System.out.println("FILE deleted"+f1.getName());
	    	}
	    }
	 }
}