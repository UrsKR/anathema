package net.sf.anathema.character.main.persistence;

import com.google.common.base.Preconditions;
import net.sf.anathema.character.main.framework.item.Item;
import net.sf.anathema.lib.exception.PersistenceException;
import net.sf.anathema.lib.xml.ElementUtilities;
import org.dom4j.Element;

public class ItemMetaDataPersister {

  private static final String ATTRIB_REPOSITORY_ID = "repositoryId";
  private static final String ATTRIB_REPOSITORY_PRINT_NAME = "repositoryPrintName";

  public void save(Element element, Item item) {
    String repositoryId = item.getId();
    Preconditions.checkNotNull(repositoryId, "Repository item must have an id for saving.");
    element.addAttribute(ATTRIB_REPOSITORY_ID, repositoryId);
    element.addAttribute(ATTRIB_REPOSITORY_PRINT_NAME, item.getDisplayName());
  }

  public void load(Element element, Item item) throws PersistenceException {
    item.getRepositoryLocation().setId(ElementUtilities.getRequiredAttrib(element, ATTRIB_REPOSITORY_ID));
  }
}