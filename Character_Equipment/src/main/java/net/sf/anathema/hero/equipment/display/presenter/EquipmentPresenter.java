package net.sf.anathema.hero.equipment.display.presenter;

import net.sf.anathema.character.equipment.character.EquipmentHeroEvaluator;
import net.sf.anathema.character.equipment.character.EquipmentOptionsProvider;
import net.sf.anathema.character.equipment.character.IEquipmentStringBuilder;
import net.sf.anathema.character.equipment.character.model.IEquipmentItem;
import net.sf.anathema.character.equipment.creation.presenter.stats.properties.EquipmentUI;
import net.sf.anathema.character.equipment.item.EquipmentTemplateNameComparator;
import net.sf.anathema.equipment.core.MagicalMaterial;
import net.sf.anathema.equipment.core.MaterialComposition;
import net.sf.anathema.framework.environment.Resources;
import net.sf.anathema.framework.presenter.resources.BasicUi;
import net.sf.anathema.hero.equipment.EquipmentModel;
import net.sf.anathema.hero.equipment.model.EquipmentPersonalizationModel;
import net.sf.anathema.interaction.Tool;
import net.sf.anathema.lib.control.CollectionListener;
import net.sf.anathema.lib.gui.AgnosticUIConfiguration;
import net.sf.anathema.lib.gui.selection.ObjectSelectionView;
import net.sf.anathema.lib.gui.selection.VetoableObjectSelectionView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.sf.anathema.equipment.core.MaterialComposition.Fixed;
import static net.sf.anathema.equipment.core.MaterialComposition.None;
import static net.sf.anathema.equipment.core.MaterialComposition.Variable;
import static net.sf.anathema.lib.lang.StringUtilities.isNullOrTrimmedEmpty;

public class EquipmentPresenter {

  private final Resources resources;
  private final EquipmentModel model;
  private final EquipmentView view;
  private final IEquipmentStringBuilder resourceBuilder;
  private ObjectSelectionView<IEquipmentItem> ownedEquipmentOverview;

  public EquipmentPresenter(Resources resources, EquipmentModel model, EquipmentView view) {
    this.resources = resources;
    this.model = model;
    this.view = view;
    this.resourceBuilder = new EquipmentStringBuilder(resources);
  }

  public void initPresentation() {
    VetoableObjectSelectionView<String> equipmentTemplatePickList = view.getEquipmentTemplatePickList();
    this.ownedEquipmentOverview = view.addOwnedEquipmentList(new SimpleEquipmentItemRenderer(resources));
    model.addEquipmentObjectListener(new UpdateOwnedItems());
    equipmentTemplatePickList.setCellRenderer(new EquipmentItemUIConfiguration(model, resources));
    setObjects(equipmentTemplatePickList);
    MagicalMaterialView magicalMaterialView = initMaterialView(equipmentTemplatePickList);
    addAddButton(equipmentTemplatePickList, magicalMaterialView);
    addRemoveButton();
    addRefreshTool(equipmentTemplatePickList);
    EquipmentObjectView editView = view.addItemEditView();
    ownedEquipmentOverview.addObjectSelectionChangedListener(item -> initItemPresentation(item, editView));
    refreshOwnedItemOverview();
  }

  private void addRemoveButton() {
    Tool remove = view.addToolButton();
    remove.setIcon(new BasicUi().getLeftArrowIconPath());
    remove.setTooltip(resources.getString("AdditionalTemplateView.RemoveTemplate.Action.Name"));
    remove.setCommand(() -> model.removeItem(ownedEquipmentOverview.getSelectedObject()));
    ownedEquipmentOverview.addObjectSelectionChangedListener(newValue -> {
      if (newValue == null || !(model.canBeRemoved(newValue))) {
        remove.disable();
      } else {
        remove.enable();
      }
    });
    remove.disable();
  }

  private void addAddButton(VetoableObjectSelectionView<String> equipmentTemplatePickList,
                            MagicalMaterialView magicalMaterialView) {
    Tool addTool = view.addToolButton();
    addTool.setIcon(new BasicUi().getRightArrowIconPath());
    addTool.setTooltip(resources.getString("AdditionalTemplateView.AddTemplate.Action.Tooltip"));
    addTool.setCommand(() -> model.addEquipmentObjectFor(equipmentTemplatePickList.getSelectedObject(),
            magicalMaterialView.getSelectedMaterial()));
    equipmentTemplatePickList.addObjectSelectionChangedListener(newValue -> setEnabled(newValue, addTool));
    setEnabled(equipmentTemplatePickList.getSelectedObject(), addTool);
  }

  private void addRefreshTool(VetoableObjectSelectionView<String> equipmentTemplatePickList) {
    Tool refreshTool = view.addToolButton();
    refreshTool.setTooltip(resources.getString("AdditionalTemplateView.RefreshDatabase.Action.Tooltip"));
    refreshTool.setIcon(new EquipmentUI().getRefreshIconPath());
    refreshTool.setCommand(() -> {
      setObjects(equipmentTemplatePickList);
      model.refreshItems();
    });
  }

  private void updateMagicalMaterialSelector(MagicalMaterialView magicalMaterialView, String templateId) {
    MaterialComposition composition = templateId == null ? None : model.getMaterialComposition(templateId);
    MagicalMaterial magicMaterial = null;
    if (composition == Variable) {
      magicMaterial = model.getDefaultMaterial();
    } else if (composition == Fixed) {
      magicMaterial = model.getMagicalMaterial(templateId);
    }
    magicalMaterialView.setSelectedMaterial(magicMaterial, composition == Variable);
  }

  private MagicalMaterialView initMaterialView(VetoableObjectSelectionView<String> equipmentTemplatePickList) {
    String label = resources.getString("MagicMaterial.Label") + ":";
    AgnosticUIConfiguration<MagicalMaterial> renderer = new MagicMaterialUIConfiguration(resources);
    MagicalMaterialView magicMaterialView = view.addMagicMaterialView(label, renderer);
    magicMaterialView.setMaterials(MagicalMaterial.values());
    equipmentTemplatePickList.addObjectSelectionChangedListener(
            templateId -> updateMagicalMaterialSelector(magicMaterialView, templateId));
    return magicMaterialView;
  }

  private void setObjects(VetoableObjectSelectionView<String> equipmentTemplatePickList) {
    String[] templates = model.getAvailableTemplateIds();
    Arrays.sort(templates, new EquipmentTemplateNameComparator());
    equipmentTemplatePickList.setObjects(templates);
  }

  private void setEnabled(String newValue, Tool selectTool) {
    if (isNullOrTrimmedEmpty(newValue)) {
      selectTool.disable();
    } else {
      selectTool.enable();
    }
  }

  private void initItemPresentation(IEquipmentItem item, EquipmentObjectView objectView) {
    if (item == null) {
      objectView.clear();
      return;
    }
    EquipmentHeroEvaluator heroEvaluator = model.getHeroEvaluator();
    EquipmentOptionsProvider optionProvider = model.getOptionProvider();
    EquipmentObjectPresenter objectPresenter = new EquipmentObjectPresenter(item, objectView, resourceBuilder,
            heroEvaluator, optionProvider, resources);
    objectPresenter.initPresentation();
    enablePersonalization(item, objectPresenter);
  }

  private void refreshOwnedItemOverview() {
    List<IEquipmentItem> allItems = new ArrayList<>();
    Collections.addAll(allItems, model.getNaturalWeapons());
    Collections.addAll(allItems, model.getEquipmentItems());
    ownedEquipmentOverview.setObjects(allItems);
  }

  private void enablePersonalization(IEquipmentItem selectedObject, EquipmentObjectPresenter objectPresenter) {
    if (model.canBeRemoved(selectedObject)) {
      createPersonalizeTool(selectedObject, objectPresenter);
    }
  }

  private void createPersonalizeTool(IEquipmentItem selectedObject, EquipmentObjectPresenter objectPresenter) {
    Tool personalize = objectPresenter.addContextTool();
    personalize.setIcon(new BasicUi().getEditIconPath());
    personalize.setText(resources.getString("AdditionalTemplateView.Personalize.Action.Name"));
    personalize.setCommand(() -> personalizeItem(selectedObject));
  }

  private void personalizeItem(IEquipmentItem selectedObject) {
    PersonalizationEditView personalizationView = createView();
    EquipmentPersonalizationModel personalizationModel = new EquipmentPersonalizationModel(selectedObject);
    personalizationView.setTitle(personalizationModel.getTitle());
    personalizationView.setDescription(personalizationModel.getDescription());
    personalizationView.whenTitleChanges(personalizationModel::setTitle);
    personalizationView.whenDescriptionChanges(personalizationModel::setDescription);
    personalizationView.whenChangeIsConfirmed(() -> {
      personalizationModel.apply();
      refreshOwnedItemOverview();
      model.updateItem(selectedObject);
    });
    personalizationView.show();
  }

  private PersonalizationEditView createView() {
    EquipmentPersonalizationProperties properties = new EquipmentPersonalizationProperties(resources);
    return view.startEditingPersonalization(properties);
  }

  private class UpdateOwnedItems implements CollectionListener {
    @Override
    public void itemAdded() {
      refreshOwnedItemOverview();
    }

    @Override
    public void itemRemoved() {
      refreshOwnedItemOverview();
    }
  }
}