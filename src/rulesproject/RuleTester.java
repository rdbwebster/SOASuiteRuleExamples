package rulesproject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import oracle.rules.sdk2.decisionpoint.*;
import oracle.rules.sdk2.dictionary.RuleDictionary;
import oracle.rules.sdk2.exception.SDKWarning;
import oracle.rules.rl.RuleSession;
import oracle.rules.rl.extensions.trace.RuleEngineState;
import oracle.rules.rl.extensions.trace.TraceAnalysis;
import oracle.rules.rl.trace.DecisionTrace;
import oracle.rules.rl.trace.FactTrace;
import oracle.rules.rl.trace.RuleTrace;
import oracle.rules.rl.trace.TraceEntry;

/**
 * A Java Class that demonstrates how to load an Oracle 11g Rules Dictionary
 * from a file system location and execute a decision Function.
 * The class is intended to be used only as Rule development / prototyping tool.
 * The rules and decision service to be tested are expected to located in a .rules
 * file on the file system created using the JDeveloper 11g Rules Editor.
 * The rules should be contained in a JDeveloper SOA Rules Composite Application,
 * for example a "Composite Business Rule" application.
 * Rules are tested in a slave JVM automatically started by JDeveloper.
 * A running WebLogic Application Server is not required.
 */
public class RuleTester {
    public RuleTester() {
        super();
    }
    
    /* Initialize loads the Rule dictionary from the specified file system location and creates a Rule Decision Point.
     * @param dictionaryLocation The full path to the .rules file.
     * @param decisionFunctionName The name of the Decision Function that will be called.
     *                             The function is defined using the JDevleloper rule editor 
     */
    private static DecisionPointInstance initialize(String dictionaryLocation, String decisionFunctionName) throws Exception {
      
      DecisionPointInstance pointInstance = null;
      if(dictionaryLocation == null  || decisionFunctionName == null)
          throw new Exception("RuleProcessor must have all input properties to successfully initialize.");
    
         // Load Decision Point using Dictionary on File System
         DecisionPoint decisionPoint = new DecisionPointBuilder()
                                   .with(decisionFunctionName)
                                   .with(loadRuleDictionary(dictionaryLocation))    
                                   .build();
         pointInstance = decisionPoint.getInstance(); 
         RuleSession session = pointInstance.ruleSession();
         session.callFunctionWithArgument("setDecisionTraceLevel", RuleSession.DECISION_TRACE_DEVELOPMENT);
    
         System.out.println("RuleTester Initialized");
         return pointInstance;
    }
    
    
  
      /**
       * Loads the rule dictionary from the specified dictionaryPath
       * @param dictionaryLocation The full path to the .rules file.
       * @return A rule dictionary object
       * 
       */
      private static RuleDictionary loadRuleDictionary(String dictionaryLocation) throws Exception{
              RuleDictionary dict = null;
              Reader reader = null;
              Writer writer = null;
                
              try {
                  reader = new FileReader(new File(dictionaryLocation));
                  dict = RuleDictionary.readDictionary(reader, new DecisionPointDictionaryFinder(null));
                  List<SDKWarning> warnings = new ArrayList<SDKWarning>();
       
                  dict.update(warnings);
                  if (warnings.size() > 0 ) { 
                      System.err.println("Validation warnings: " + warnings);
                  }
     
              } finally {
                  if (reader != null) { try { reader.close(); } 
                                        catch (IOException ioe) {ioe.printStackTrace();}}
                  if (writer != null) { try { writer.close(); } 
                                        catch (IOException ioe) {ioe.printStackTrace();}}
              }
            
              return dict;
          }
    
     /**
      * Produces a trace of the Rules Session to the Standard Output device
      * @param pointInstance The decision point instance for the test session.
 
      */
      private static void outputTrace(DecisionPointInstance pointInstance) throws Exception
     {
         DecisionTrace trace = pointInstance.decisionTrace();
         TraceAnalysis tAnalysis = new TraceAnalysis(trace);
              
         List<TraceEntry> traceList = trace.getTraceEntries();
         System.out.println("Trace list size is " + traceList.size() );
         
         if(traceList.size() > 0) {
         //  Iterator<TraceEntry> it = traceList.iterator();
           for(int t=0; t<traceList.size(); t++)
           {
             TraceEntry entry = traceList.get(t);
             
             RuleEngineState engineState =  tAnalysis.getRuleEngineState(entry);
            
             // Fact Trace List for Trace Entry
             List<FactTrace> factTraceList = engineState.getFacts();
             System.out.println("Trace entry " + (t+1) + " Engine has " + factTraceList.size() +  " Facts");
             for(int i=0; i< factTraceList.size(); i++)
             {            
               FactTrace fTrace = (FactTrace) factTraceList.get(i);
               System.out.println("Fact " + (i+1) + ": " + fTrace.getFactType());
             }
             
             // Output Fired Rules for Trace Entry
             List<RuleTrace> firedRulesList = engineState.getFiredRules();
             for(int i=0; i< firedRulesList.size(); i++)
             {            
               RuleTrace frTrace = (RuleTrace) firedRulesList.get(i);
               System.out.println("Fired rule " + frTrace.getRuleName());
             }
           }
         }
     }
      
     /**
      * Execute the Rule Decision Function and return the results.
      * @param dictionaryLocation The fully qualified path of the .rules file containing the rules.
      *                           Created by JDeveloper rule editor.
      * @param decisionFunctionName The name of the Decision Function in the Rules Dictionary that will be called.
      * @param inputs An ArrayList containing the parameters required by the decision service in order or appearance.
      * @param trace  A boolean flag indicating whether a trace for the session should be output to the standard out device.
      * @return A modified list of objects returned by the specified rule decision service
     */
     public static List runRules(String dicrtionaryLocation, String decisionFunctionName, ArrayList inputs, boolean trace) throws Exception {  
    
        List<Object> ruleResult = null;     

        DecisionPointInstance pInstance = initialize(dicrtionaryLocation, decisionFunctionName);
        
        if(pInstance == null)
          throw new Exception("RuleTester not intialized.");
        
        System.out.println("Running Rules");
        
        if (inputs != null)
        {          
            pInstance.setInputs(inputs);
        
            // invoke the decision point with our inputs
            ruleResult = pInstance.invoke();
            
            if (ruleResult == null || ruleResult.isEmpty()){
                System.out.println("RuleTester: No results returned by rules"); 
            }
            else System.out.println("RuleTester: " + ruleResult.size() + " result(s) returned.");
            
            if(trace)
              outputTrace(pInstance);
        }
        return ruleResult;
    }
  
}
