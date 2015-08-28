/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/

package br.unicamp.cst.behavior.glas;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Merges sequences' branches of a given glas solution
 * @author klausr
 *
 */
public class Merger {

	private int nNodes=-1;

	public double[] merge(double[] unmerged_solution) {
		// TODO Implement merging algorithm here
		nNodes=Math.round(unmerged_solution.length/3);

		double[] temp_merged_solution= new double[unmerged_solution.length];

		for(int i=0; i<temp_merged_solution.length;i++){
			temp_merged_solution[i]=unmerged_solution[i];
		}

		//START WHILE LOOP HERE


		int it_count=0;

		boolean merge_done=false;



		while(!merge_done){

			merge_done=true;


			HashMap<Integer, Integer> nodes_contents = this.getNodesContents(temp_merged_solution);



			for(int parent_node=1; parent_node<temp_merged_solution.length;parent_node++){

				ArrayList<Integer> children_nodes = this.getChildrenNodes(temp_merged_solution,parent_node); 


				for(int child=0; child<children_nodes.size();child++){
					int child_content=nodes_contents.get(children_nodes.get(child));
					for(int sibling=child+1; sibling<children_nodes.size();sibling++){
						int sibling_content = nodes_contents.get(children_nodes.get(sibling));
						if(child_content==sibling_content){//Move sibling's children to child
							ArrayList<Integer> siblings_children = this.getChildrenNodes(temp_merged_solution, children_nodes.get(sibling));
							for(Integer sibling_child: siblings_children){
								temp_merged_solution[sibling_child-1]=children_nodes.get(child);
							}

							//Prune sibling away

							double[] temp2_merged_solution = new double[temp_merged_solution.length-3];
							int count_p2=0;
							for(int p2=0;p2<nNodes;p2++){
								if(p2!=children_nodes.get(sibling)-1){
									temp2_merged_solution[count_p2]=temp_merged_solution[p2];
									temp2_merged_solution[count_p2+(nNodes-1)]=temp_merged_solution[p2+nNodes];
									temp2_merged_solution[count_p2+2*(nNodes-1)]=temp_merged_solution[p2+2*nNodes];

									if(temp2_merged_solution[count_p2]>children_nodes.get(sibling)){
										temp2_merged_solution[count_p2]--;
									}

									count_p2++;
								}

							}
							temp_merged_solution=temp2_merged_solution;

							merge_done=false;
							break;
						}//inside if
					}
					if(!merge_done){
						break;
					}
				}
				if(!merge_done){
					break;
				}
			}//Parent_node loop
			System.out.println("Iteration: "+it_count);
			it_count++;
		}


		return temp_merged_solution;
	}

	private HashMap<Integer,Integer> getNodesContents(
			double[] temp_merged_solution) {
		HashMap<Integer,Integer> nodes_contents = new HashMap<Integer,Integer>();

		for(int i = nNodes;i<2*nNodes;i++){
			nodes_contents.put(i-nNodes+1, (int)temp_merged_solution[i]);
		}


		return nodes_contents;
	}

	private ArrayList<Integer> getChildrenNodes(double[] temp_merged_solution,
			int parent_node) {

		ArrayList<Integer> children_nodes = new ArrayList<Integer>();

		for(int i = 0;i<nNodes; i++){
			if((int)temp_merged_solution[i]==parent_node){
				children_nodes.add(i+1);
			}
		}


		return children_nodes;
	}

}
