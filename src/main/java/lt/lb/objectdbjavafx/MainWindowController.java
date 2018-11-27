package lt.lb.objectdbjavafx;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.containers.BasicProperty;
import lt.lb.commons.javafx.CosmeticsFX;
import lt.lb.commons.javafx.CosmeticsFX.MenuTree;
import lt.lb.commons.javafx.scenemanagement.Frame;
import lt.lb.commons.javafx.scenemanagement.InjectableController;
import lt.lb.objectdbjavafx.model.EAValue;
import lt.lb.objectdbjavafx.model.FileEntity;
import lt.lb.objectdbjavafx.model.FileEntityFolder;

/**
 * FXML Controller class
 *
 * @author laim0nas100
 */
public class MainWindowController implements InjectableController {

    public FileEntityFolder folder;
    public ObservableList<FileEntity> files = FXCollections.observableArrayList();

    public Supplier<Collection<FileEntity>> filePopulatingFunction = () -> {
        return folder.getFiles();
    };

    @FXML
    public Label currentFolderName;

    @FXML
    public Button buttonUp;

    @FXML
    public TextField textField;

    @FXML
    public TableView<FileEntity> table;

    @Override
    public void inject(Frame frame, URL url, ResourceBundle rb) {
    }

    BooleanBinding selectionNotEmpty;
    SimpleBooleanProperty isFolder = new SimpleBooleanProperty(false);
    SimpleBooleanProperty alwaysOK = new SimpleBooleanProperty(true);
    BooleanExpression selectionIsTextFile;

    @Override
    public void initialize() {
        selectionNotEmpty = Bindings.isNotEmpty(table.getSelectionModel().getSelectedItems());
        selectionIsTextFile = selectionNotEmpty.and(Bindings.createBooleanBinding(() -> {
            FileEntity selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return false;
            }
            return selectedItem.meta.hasMetaName(MetaEnums.textContent);
        }, selectionNotEmpty));
        TableColumn<FileEntity, String> col_1 = new TableColumn<>("Name");
        col_1.setCellValueFactory((CellDataFeatures<FileEntity, String> cellData) -> new BasicProperty(cellData.getValue().meta.get("fileName").get()));
        TableColumn<FileEntity, String> col_2 = new TableColumn<>("Type");
        col_2.setCellValueFactory((CellDataFeatures<FileEntity, String> cellData) -> {
            return new BasicProperty(cellData.getValue().getClass().getSimpleName());
        });
        TableColumn<FileEntity, String> col_3 = new TableColumn<>("ID");
        col_3.setCellValueFactory((CellDataFeatures<FileEntity, String> cellData) -> {
            return new BasicProperty<>(cellData.getValue().id);
        });

        table.getColumns().addAll(Arrays.asList(col_1, col_2, col_3));

        MenuTree menuTree = new MenuTree(null);
        MenuItem edit = CosmeticsFX.simpleMenuItem("Edit", (event) -> {
            FileEntity selectedItem = table.getSelectionModel().getSelectedItem();
            Main.makeTextEditWindow(selectedItem);
        }, selectionIsTextFile);
        menuTree.addMenuItem(edit, "Edit");
        MenuItem rename = CosmeticsFX.simpleMenuItem("Rename", (event) -> {
            EAValue get = table.getSelectionModel().getSelectedItem().meta.get(MetaEnums.fileName);
            Main.makeMetaEditWindow(get);
        }, selectionNotEmpty);

        menuTree.addMenuItem(rename, "Rename");
        MenuItem delete = CosmeticsFX.simpleMenuItem("Delete", (event) -> {
            FAPI.deleteFile(table.getSelectionModel().getSelectedItem());
            Main.updateAllWindows();
        }, selectionNotEmpty);
        menuTree.addMenuItem(delete, "Delete");

        Menu addMenu = new Menu("Add...");
        menuTree.addMenuItem(addMenu, addMenu.getText());

        MenuItem addNewTextFile = CosmeticsFX.simpleMenuItem("Text file", (event) -> {
            Q.submit(() -> {
                FileEntity textFile = FAPI.createTextFile("New file.txt", "");
                FAPI.addAssignFolder(folder, textFile);
            });

            Main.updateAllWindows();
        }, isFolder);

        menuTree.addMenuItem(addNewTextFile, addMenu.getText(), addNewTextFile.getText());
        MenuItem addNewFolder = CosmeticsFX.simpleMenuItem("Folder", (event) -> {
            Optional<Throwable> submit = Q.submit(() -> {
                FileEntityFolder newFolder = FAPI.createFolder("New folder");
                FAPI.addAssignFolder(folder, newFolder);
            });
            FAPI.report(submit);
            Main.updateAllWindows();
        }, isFolder);
        menuTree.addMenuItem(addNewFolder, addMenu.getText(), addNewFolder.getText());

        ContextMenu context = menuTree.constructContextMenu();
        CosmeticsFX.simpleMenuBindingWrap(context);
        table.setContextMenu(context);
        table.setOnMouseClicked(event -> {
            FileEntity selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem instanceof FileEntityFolder) {
                if (event.getClickCount() >= 2) {
                    this.folder = F.cast(selectedItem);
                }
                this.update();
            }
        });
        table.setItems(files);
    }

    @Override
    public void update() {
        files.setAll(filePopulatingFunction.get());
        TableColumn<FileEntity, ?> get = table.getColumns().get(0);
        
        isFolder.set(folder != null);
        if (isFolder.get()) {
            
            this.currentFolderName.setText(folder.meta.getString(MetaEnums.fileName));
        } else {
            buttonUp.setDisable(true);
            this.currentFolderName.setText("");
        }
        get.setVisible(false);
        get.setVisible(true);

    }

    public void fullTextSearch() {
        String ss = textField.getText();
        
        Main.makeNewWindow(()->FAPI.fullTextSearch(ss),"Seach text :\""+ss+"\"");
    }

    public void up() {
        Optional<FileEntityFolder> parentOptional = FAPI.getParent(folder);
        if (parentOptional.isPresent()) {
            folder = parentOptional.get();
            update();
        }

    }

}
