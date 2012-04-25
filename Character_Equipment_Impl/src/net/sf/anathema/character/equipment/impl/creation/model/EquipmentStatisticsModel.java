package net.sf.anathema.character.equipment.impl.creation.model;

import net.sf.anathema.character.equipment.MagicalMaterial;
import net.sf.anathema.character.equipment.creation.model.stats.IEquipmentStatisticsModel;
import net.sf.anathema.lib.workflow.textualdescription.ITextualDescription;
import net.sf.anathema.lib.workflow.textualdescription.model.SimpleTextualDescription;

public class EquipmentStatisticsModel implements IEquipmentStatisticsModel {

  private final ITextualDescription name = new SimpleTextualDescription();
  private MagicalMaterial materialType = null;

  @Override
  public final ITextualDescription getName() {
    return name;
  }
  
  @Override
  public MagicalMaterial getMaterialType()
  {
	  return materialType;
  }
  
  @Override
  public void setMaterialType(MagicalMaterial material)
  {
	  materialType = material;
  }
}