/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.bindings.soar;

import java.io.File;

/**
 *
 * @author rgudwin
 */
class TestJSoarCodelet extends JSoarCodelet {
    
        public TestJSoarCodelet() {
            SilenceLoggers();
            initSoarPlugin("TestAgent",new File("soarRules.soar"),false);
        }
    
        public TestJSoarCodelet(String _agentName, File _productionPath, Boolean startSOARDebugger) {
            initSoarPlugin(_agentName, _productionPath, startSOARDebugger);
        }
        
        @Override
        public void proc() {
            
        }
        
        @Override
        public void calculateActivation() {
            
        }
        
        @Override
        public void accessMemoryObjects() {
            
        }
        
    }