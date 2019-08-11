/**
 * 
 */
package br.unicamp.cst.consciousness;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;

/**
 * @author andre
 *
 */
public class LowActivationCodelet extends Codelet{
	
	public LowActivationCodelet(String name) {
		setName(name);		
	}

	@Override
	public void accessMemoryObjects() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calculateActivation() {
		try{
			setActivation(0.1d);
		} catch (CodeletActivationBoundsException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void proc() {
		// TODO Auto-generated method stub
		
	}

}
