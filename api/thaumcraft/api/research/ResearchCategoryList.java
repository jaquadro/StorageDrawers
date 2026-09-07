package thaumcraft.api.research;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.Identifier;

public class ResearchCategoryList {
	
	/** Is the smallest column used on the GUI. */
    public int minDisplayColumn;

    /** Is the smallest row used on the GUI. */
    public int minDisplayRow;

    /** Is the biggest column used on the GUI. */
    public int maxDisplayColumn;

    /** Is the biggest row used on the GUI. */
    public int maxDisplayRow;
    
    /** display variables **/
    public Identifier icon;
    public Identifier background;
    public Identifier background2;
    
    public String researchKey;
	
	public ResearchCategoryList(String researchKey, Identifier icon, Identifier background) {
		this.researchKey = researchKey;
		this.icon = icon;
		this.background = background;
		this.background2 = null;
	}
	
	public ResearchCategoryList(String researchKey, Identifier icon, Identifier background, Identifier background2) {
		this.researchKey = researchKey;
		this.icon = icon;
		this.background = background;
		this.background2 = background2;
	}

	//Research
	public Map<String, ResearchItem> research = new HashMap<String,ResearchItem>();

		
		
	
	
}
