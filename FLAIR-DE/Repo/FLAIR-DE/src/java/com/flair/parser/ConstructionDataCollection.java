/*
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/.
 
 */
package com.flair.parser;

import com.flair.grammar.GrammaticalConstruction;
import java.util.EnumMap;

/**
 * A table of construction data mapped to constructions
 * @author shadeMe
 */
class ConstructionDataCollection
{
    private final EnumMap<GrammaticalConstruction, AbstractConstructionData>    dataStore;
    
    public ConstructionDataCollection(AbstractConstructionDataFactory prototypeFactory)
    {
	dataStore = new EnumMap<>(GrammaticalConstruction.class);
	
	for (GrammaticalConstruction itr : GrammaticalConstruction.values())
	    dataStore.put(itr, prototypeFactory.create(itr));
    }
    
    public AbstractConstructionData getData(GrammaticalConstruction type)
    {
	return dataStore.get(type);
    }
}
