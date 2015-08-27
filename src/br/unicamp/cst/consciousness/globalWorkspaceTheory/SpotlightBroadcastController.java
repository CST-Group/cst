/**
 * 
 */
package br.unicamp.cst.consciousness.globalWorkspaceTheory;

import java.util.ArrayList;
import java.util.List;

import br.unicamp.cst.core.entities.CodeRack;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.exceptions.CodeletActivationBoundsException;


/**
 * @author André Paraense
 *
 *	A codelet-based implementation of the Global Workspace Theory, originally formulated 
 *  in [1988 Baars] Bernard J. Baars. A Cognitive Theory of Consciousness. Cambridge University Press, 1988.
 *
 */
public class SpotlightBroadcastController extends Codelet 
{
	private Codelet consciousCodelet; 
	
	/** access to all codelets, so the broadcast can be made*/
	private CodeRack codeRack;
	
	private double thresholdActivation = 0.9d;
	
	public SpotlightBroadcastController(CodeRack codeRack)
	{
		this.setName("SpotlightBroadcastController");
		this.codeRack = codeRack;		
		consciousCodelet = null;
	}

	/* (non-Javadoc)
	 * @see br.unicamp.cogsys.core.entities.Codelet#accessMemoryObjects()
	 */
	@Override
	public void accessMemoryObjects() 
	{
		// nothing	
	}

	/* (non-Javadoc)
	 * @see br.unicamp.cogsys.core.entities.Codelet#calculateActivation()
	 */
	@Override
	public void calculateActivation() 
	{
		try 
		{
			setActivation(0.0d);
		} catch (CodeletActivationBoundsException e) 
		{			
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see br.unicamp.cogsys.core.entities.Codelet#proc()
	 */
	@Override
	public void proc() 
	{	
		
		if(consciousCodelet!=null)
		{
			if(consciousCodelet.getActivation() < thresholdActivation)
			{			
				consciousCodelet = null;
			}
		}	
		
		if(codeRack!=null)
		{
			//first, select the coalition with greater activation to gain consciousness
			List<Codelet> allCodeletsList = codeRack.getAllCodelets();    
			
			if(allCodeletsList!=null)
			{
				for (Codelet codelet: allCodeletsList)
				{ 
					if(consciousCodelet == null)
					{
						if(codelet.getActivation() > thresholdActivation)
						{					
							consciousCodelet = codelet;
						}
					}else
					{
						if(codelet.getActivation() > consciousCodelet.getActivation())
						{
							consciousCodelet = codelet;
						}
					}			
				}
						
				//then, broadcast its information to all codelets
				
				if(consciousCodelet!=null)
				{											
					List<MemoryObject> memoryObjectsToBeBroadcasted  = consciousCodelet.getOutputs();
					if(memoryObjectsToBeBroadcasted!=null)
					{
						for (Codelet codelet: allCodeletsList)
						{ 
							if(!codelet.getName().equalsIgnoreCase(consciousCodelet.getName()))
								codelet.setBroadcast(memoryObjectsToBeBroadcasted);
							else
								codelet.setBroadcast(new ArrayList<MemoryObject>());
						}
					}else
					{
						for (Codelet codelet: allCodeletsList)
						{ 					
							codelet.setBroadcast(new ArrayList<MemoryObject>());
						}
					}
				}else
				{
					for (Codelet codelet: allCodeletsList)
					{ 					
						codelet.setBroadcast(new ArrayList<MemoryObject>());
					}						
				}
			}
		}	
	}
}
