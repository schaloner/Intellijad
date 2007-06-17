package net.stevechaloner.intellijad.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import net.stevechaloner.idea.util.fs.FileSelectionAction;
import net.stevechaloner.idea.util.fs.FileSelectionDescriptor;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 */
public class ConfigForm
{

    private JTabbedPane tabbedPane1;
    private JTextField outputDirectoryTextField;
    private JButton button1;
    private JCheckBox markDecompiledFilesAsCheckBox;
    private JTextField outputFileExtensionTextField;
    private JTextField classesWithNumericalNamesTextField;
    private JTextField fieldsWithNumericalNamesTextField;
    private JTextField localsWithNumericalNamesTextField;
    private JTextField methodsWithNumericalNamesTextField;
    private JTextField parametersWithNumericalNamesTextField;
    private JTextField allPackagesTextField;
    private JTextField unusedExceptionNamesTextField;
    private JSpinner packFieldsWithTheSpinner;
    private JSpinner splitStringsIntoPiecesSpinner;
    private JSpinner spacesForIndentationSpinner;
    private JSpinner displayLongsUsingRadixSpinner;
    private JSpinner displayIntegersUsingRadixSpinner;
    private JCheckBox printDefaultInitializersForCheckBox;
    private JCheckBox generateRedundantBracesCheckBox;
    private JCheckBox generateFullyQualifiedNamesCheckBox;
    private JCheckBox suppressEmptyConstructorsCheckBox;
    private JCheckBox clearAllPrefixesIncludingCheckBox;
    private JCheckBox donTGenerateAuxiliaryCheckBox;
    private JCheckBox donTDisambiguateFieldsCheckBox;
    private JCheckBox originalLineNumbersAsCheckBox;
    private JCheckBox useTabsInsteadOfCheckBox;
    private JCheckBox sortLinesAccordingToCheckBox;
    private JCheckBox spaceBetweenKeywordAndCheckBox;
    private JCheckBox insertANewlineBeforeCheckBox;
    private JCheckBox outputFieldsBeforeMethodsCheckBox;
    private JCheckBox splitStringsOnNewlineCheckBox;
    private JPanel root;
    private JTextField jadTextField;
    private JButton actionRemoveExclusionButton;
    private JTextField pathTextField;
    private JButton addButton;
    private JScrollPane excludesScrollPane;
    private JTable exclusionTable;
    private JComboBox navTriggeredDecomp;
    private JButton browseButton1;
    private JCheckBox decompileToMemoryCheckBox;
    private JCheckBox createIfDirectoryDoesnCheckBox;
    private JCheckBox alwaysExcludePackagesRecursivelyCheckBox;

    private ExclusionTableModel exclusionTableModel;

    public ConfigForm(final Project project)
    {
        navTriggeredDecomp.addItem(NavigationTriggeredDecompile.ALWAYS);
        navTriggeredDecomp.addItem(NavigationTriggeredDecompile.ASK);
        navTriggeredDecomp.addItem(NavigationTriggeredDecompile.NEVER);

        packFieldsWithTheSpinner.setModel(createSpinnerModel());
        splitStringsIntoPiecesSpinner.setModel(createSpinnerModel());
        spacesForIndentationSpinner.setModel(createSpinnerModel());
        displayLongsUsingRadixSpinner.setModel(new SpinnerRadixModel());
        displayIntegersUsingRadixSpinner.setModel(new SpinnerRadixModel());

        button1.addActionListener(new FileSelectionAction(project,
                                                          outputDirectoryTextField,
                                                          FileSelectionDescriptor.DIRECTORIES_ONLY));
        browseButton1.addActionListener(new FileSelectionAction(project,
                                                                jadTextField,
                                                                FileSelectionDescriptor.FILES_ONLY));
        decompileToMemoryCheckBox.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                boolean decompileToMemory = decompileToMemoryCheckBox.isSelected();
                button1.setEnabled(!decompileToMemory);
                createIfDirectoryDoesnCheckBox.setEnabled(!decompileToMemory);
                outputDirectoryTextField.setEnabled(!decompileToMemory);
            }
        });

        addButton.setEnabled(false);
        pathTextField.addKeyListener(new KeyListener()
        {

            public void keyTyped(KeyEvent keyEvent)
            {
                // no-op
            }

            public void keyPressed(KeyEvent keyEvent)
            {
                // no-op
            }

            public void keyReleased(KeyEvent keyEvent)
            {
                switch (keyEvent.getKeyCode())
                {
                    case KeyEvent.VK_ENTER:
                        if (addButton.isEnabled())
                        {
                            addExclusion();
                        }
                        break;
                    default:
                        String s = pathTextField.getText();
                        addButton.setEnabled(s != null && s.length() != 0 && Character.isJavaIdentifierPart(s.charAt(s.length() - 1)));
                }
            }
        });
        addButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                addExclusion();
            }
        });
        actionRemoveExclusionButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (exclusionTableModel != null)
                {
                    int selectedRow = exclusionTable.getSelectedRow();
                    if (selectedRow != -1)
                    {
                        exclusionTableModel.removeRow(selectedRow);
                    }
                }
            }
        });
    }

    /**
     * Exclude a package from decompilation.
     */
    private void addExclusion()
    {
        if (exclusionTableModel != null)
        {
            String path = pathTextField.getText();
            if (!StringUtil.isEmptyOrSpaces(path))
            {
                exclusionTableModel.addRow(new Object[]{path,
                                                        alwaysExcludePackagesRecursivelyCheckBox.isSelected()});
            }
            pathTextField.setText("");
        }
    }

    /**
     * Gets the root component.
     *
     * @return the root component
     */
    public JComponent getRoot()
    {
        return root;
    }

    private static SpinnerModel createSpinnerModel()
    {
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(0);
        return model;
    }

    private void setUnboundData(Config data)
    {
        exclusionTableModel = data.getExclusionTableModel();
        exclusionTable.setModel(exclusionTableModel);
        packFieldsWithTheSpinner.setValue(data.getPackFields());
        splitStringsIntoPiecesSpinner.setValue(data.getMaxStringLength());
        spacesForIndentationSpinner.setValue(data.getIndentation());
        displayLongsUsingRadixSpinner.setValue(data.getLongRadix());
        displayIntegersUsingRadixSpinner.setValue(data.getIntRadix());
        navTriggeredDecomp.setSelectedItem(NavigationTriggeredDecompile.getByName(data.getConfirmNavigationTriggeredDecompile()));
    }

    public void setData(Config data)
    {
        setUnboundData(data);

        outputFileExtensionTextField.setText(data.getFileExtension());
        jadTextField.setText(data.getJadPath());
        markDecompiledFilesAsCheckBox.setSelected(data.isReadOnly());
        outputDirectoryTextField.setText(data.getOutputDirectory());
        createIfDirectoryDoesnCheckBox.setSelected(data.isCreateOutputDirectory());
        decompileToMemoryCheckBox.setSelected(data.isDecompileToMemory());
        printDefaultInitializersForCheckBox.setSelected(data.isDefaultInitializers());
        generateFullyQualifiedNamesCheckBox.setSelected(data.isFullyQualifiedNames());
        clearAllPrefixesIncludingCheckBox.setSelected(data.isClearPrefixes());
        generateRedundantBracesCheckBox.setSelected(data.isRedundantBraces());
        suppressEmptyConstructorsCheckBox.setSelected(data.isNoctor());
        donTGenerateAuxiliaryCheckBox.setSelected(data.isNocast());
        donTDisambiguateFieldsCheckBox.setSelected(data.isNofd());
        useTabsInsteadOfCheckBox.setSelected(data.isUseTabs());
        spaceBetweenKeywordAndCheckBox.setSelected(data.isSpaceAfterKeyword());
        sortLinesAccordingToCheckBox.setSelected(data.isSort());
        originalLineNumbersAsCheckBox.setSelected(data.isLineNumbersAsComments());
        outputFieldsBeforeMethodsCheckBox.setSelected(data.isFieldsFirst());
        insertANewlineBeforeCheckBox.setSelected(data.isNonlb());
        splitStringsOnNewlineCheckBox.setSelected(data.isSplitStringsAtNewline());
        classesWithNumericalNamesTextField.setText(data.getPrefixNumericalClasses());
        fieldsWithNumericalNamesTextField.setText(data.getPrefixNumericalFields());
        localsWithNumericalNamesTextField.setText(data.getPrefixNumericalLocals());
        methodsWithNumericalNamesTextField.setText(data.getPrefixNumericalMethods());
        parametersWithNumericalNamesTextField.setText(data.getPrefixNumericalParameters());
        allPackagesTextField.setText(data.getPrefixPackages());
        unusedExceptionNamesTextField.setText(data.getPrefixUnusedExceptions());
        alwaysExcludePackagesRecursivelyCheckBox.setSelected(data.isAlwaysExcludeRecursively());
    }

    private void getUnboundData(Config data)
    {
        data.setPackFields((Integer) packFieldsWithTheSpinner.getValue());
        data.setMaxStringLength((Integer) splitStringsIntoPiecesSpinner.getValue());
        data.setIndentation((Integer) spacesForIndentationSpinner.getValue());
        data.setLongRadix((Integer) displayLongsUsingRadixSpinner.getValue());
        data.setIntRadix((Integer) displayIntegersUsingRadixSpinner.getValue());
        data.setConfirmNavigationTriggeredDecompile(((NavigationTriggeredDecompile) navTriggeredDecomp.getSelectedItem()).getName());
    }

    public void getData(Config data)
    {
        getUnboundData(data);

        data.setFileExtension(outputFileExtensionTextField.getText());
        data.setJadPath(jadTextField.getText());
        data.setReadOnly(markDecompiledFilesAsCheckBox.isSelected());
        data.setOutputDirectory(outputDirectoryTextField.getText());
        data.setCreateOutputDirectory(createIfDirectoryDoesnCheckBox.isSelected());
        data.setDecompileToMemory(decompileToMemoryCheckBox.isSelected());
        data.setDefaultInitializers(printDefaultInitializersForCheckBox.isSelected());
        data.setFullyQualifiedNames(generateFullyQualifiedNamesCheckBox.isSelected());
        data.setClearPrefixes(clearAllPrefixesIncludingCheckBox.isSelected());
        data.setRedundantBraces(generateRedundantBracesCheckBox.isSelected());
        data.setNoctor(suppressEmptyConstructorsCheckBox.isSelected());
        data.setNocast(donTGenerateAuxiliaryCheckBox.isSelected());
        data.setNofd(donTDisambiguateFieldsCheckBox.isSelected());
        data.setUseTabs(useTabsInsteadOfCheckBox.isSelected());
        data.setSpaceAfterKeyword(spaceBetweenKeywordAndCheckBox.isSelected());
        data.setSort(sortLinesAccordingToCheckBox.isSelected());
        data.setLineNumbersAsComments(originalLineNumbersAsCheckBox.isSelected());
        data.setFieldsFirst(outputFieldsBeforeMethodsCheckBox.isSelected());
        data.setNonlb(insertANewlineBeforeCheckBox.isSelected());
        data.setSplitStringsAtNewline(splitStringsOnNewlineCheckBox.isSelected());
        data.setPrefixNumericalClasses(classesWithNumericalNamesTextField.getText());
        data.setPrefixNumericalFields(fieldsWithNumericalNamesTextField.getText());
        data.setPrefixNumericalLocals(localsWithNumericalNamesTextField.getText());
        data.setPrefixNumericalMethods(methodsWithNumericalNamesTextField.getText());
        data.setPrefixNumericalParameters(parametersWithNumericalNamesTextField.getText());
        data.setPrefixPackages(allPackagesTextField.getText());
        data.setPrefixUnusedExceptions(unusedExceptionNamesTextField.getText());
        data.setAlwaysExcludeRecursively(alwaysExcludePackagesRecursivelyCheckBox.isSelected());
    }

    private boolean isUnboundDataModified(Config data)
    {
        return false;
    }

    public boolean isModified(Config data)
    {
        if (isUnboundDataModified(data))
        {
            return true;
        }
        if (outputFileExtensionTextField.getText() != null ? !outputFileExtensionTextField.getText().equals(data.getFileExtension()) : data.getFileExtension() != null)
        {
            return true;
        }
        if (jadTextField.getText() != null ? !jadTextField.getText().equals(data.getJadPath()) : data.getJadPath() != null)
        {
            return true;
        }
        if (markDecompiledFilesAsCheckBox.isSelected() != data.isReadOnly())
        {
            return true;
        }
        if (outputDirectoryTextField.getText() != null ? !outputDirectoryTextField.getText().equals(data.getOutputDirectory()) : data.getOutputDirectory() != null)
        {
            return true;
        }
        if (createIfDirectoryDoesnCheckBox.isSelected() != data.isCreateOutputDirectory())
        {
            return true;
        }
        if (decompileToMemoryCheckBox.isSelected() != data.isDecompileToMemory())
        {
            return true;
        }
        if (printDefaultInitializersForCheckBox.isSelected() != data.isDefaultInitializers())
        {
            return true;
        }
        if (generateFullyQualifiedNamesCheckBox.isSelected() != data.isFullyQualifiedNames())
        {
            return true;
        }
        if (clearAllPrefixesIncludingCheckBox.isSelected() != data.isClearPrefixes())
        {
            return true;
        }
        if (generateRedundantBracesCheckBox.isSelected() != data.isRedundantBraces())
        {
            return true;
        }
        if (suppressEmptyConstructorsCheckBox.isSelected() != data.isNoctor())
        {
            return true;
        }
        if (donTGenerateAuxiliaryCheckBox.isSelected() != data.isNocast())
        {
            return true;
        }
        if (donTDisambiguateFieldsCheckBox.isSelected() != data.isNofd())
        {
            return true;
        }
        if (useTabsInsteadOfCheckBox.isSelected() != data.isUseTabs())
        {
            return true;
        }
        if (spaceBetweenKeywordAndCheckBox.isSelected() != data.isSpaceAfterKeyword())
        {
            return true;
        }
        if (sortLinesAccordingToCheckBox.isSelected() != data.isSort())
        {
            return true;
        }
        if (originalLineNumbersAsCheckBox.isSelected() != data.isLineNumbersAsComments())
        {
            return true;
        }
        if (outputFieldsBeforeMethodsCheckBox.isSelected() != data.isFieldsFirst())
        {
            return true;
        }
        if (insertANewlineBeforeCheckBox.isSelected() != data.isNonlb())
        {
            return true;
        }
        if (splitStringsOnNewlineCheckBox.isSelected() != data.isSplitStringsAtNewline())
        {
            return true;
        }
        if (classesWithNumericalNamesTextField.getText() != null ? !classesWithNumericalNamesTextField.getText().equals(data.getPrefixNumericalClasses()) : data.getPrefixNumericalClasses() != null)
        {
            return true;
        }
        if (fieldsWithNumericalNamesTextField.getText() != null ? !fieldsWithNumericalNamesTextField.getText().equals(data.getPrefixNumericalFields()) : data.getPrefixNumericalFields() != null)
        {
            return true;
        }
        if (localsWithNumericalNamesTextField.getText() != null ? !localsWithNumericalNamesTextField.getText().equals(data.getPrefixNumericalLocals()) : data.getPrefixNumericalLocals() != null)
        {
            return true;
        }
        if (methodsWithNumericalNamesTextField.getText() != null ? !methodsWithNumericalNamesTextField.getText().equals(data.getPrefixNumericalMethods()) : data.getPrefixNumericalMethods() != null)
        {
            return true;
        }
        if (parametersWithNumericalNamesTextField.getText() != null ? !parametersWithNumericalNamesTextField.getText().equals(data.getPrefixNumericalParameters()) : data.getPrefixNumericalParameters() != null)
        {
            return true;
        }
        if (allPackagesTextField.getText() != null ? !allPackagesTextField.getText().equals(data.getPrefixPackages()) : data.getPrefixPackages() != null)
        {
            return true;
        }
        if (unusedExceptionNamesTextField.getText() != null ? !unusedExceptionNamesTextField.getText().equals(data.getPrefixUnusedExceptions()) : data.getPrefixUnusedExceptions() != null)
        {
            return true;
        }
        if (alwaysExcludePackagesRecursivelyCheckBox.isSelected() != data.isAlwaysExcludeRecursively())
        {
            return true;
        }
        if (exclusionTableModel != null && !exclusionTableModel.equals(data.getExclusionTableModel()))
        {
            return true;
        }
        return false;
    }

    private class SpinnerRadixModel extends SpinnerNumberModel
    {
        SpinnerRadixModel()
        {
            this(10);
        }

        SpinnerRadixModel(int value)
        {
            super(value,
                  8,
                  16,
                  2);
        }


        public void setValue(Object object)
        {
            int value = ((Number) object).intValue();
            if (value != 8 && value != 10 && value != 16)
            {
                object = 10;
            }
            super.setValue(object);
        }

        public Object getNextValue()
        {
            Number number = getNumber();
            Integer next;
            switch (number.intValue())
            {
                case 8:
                    next = 10;
                    break;
                case 10:
                    next = 16;
                    break;
                case 16:
                    next = 8;
                    break;
                default:
                    next = 8;
            }
            return next;
        }

        public Object getPreviousValue()
        {
            Number number = getNumber();
            Integer previous;
            switch (number.intValue())
            {
                case 8:
                    previous = 16;
                    break;
                case 10:
                    previous = 8;
                    break;
                case 16:
                    previous = 10;
                    break;
                default:
                    previous = 8;
            }
            return previous;
        }
    }
}
