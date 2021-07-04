import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class App extends Application {

	private void initUI(Stage stage) {
		
		GridPane root = new GridPane();
		
		Button browse = new Button("Browse");
		
		TextField filePath = new TextField(""); // initialize new text field not editable
		filePath.setEditable(false);
		
		TextField numSpinners = new TextField("");
		numSpinners.setEditable(true);

		Label numSpinnersLabel = new Label("# of Spinners:");
		
		Button perform = new Button("Create Files!");
		
		Label output = new Label("");
		
		root.add(browse, 0, 0);
		root.add(filePath, 1, 0);
		root.add(numSpinnersLabel, 0, 1);
		root.add(numSpinners, 1, 1);
		root.add(perform, 0, 2);
		root.add(output, 1, 2);

		browse.setOnAction(new BrowseEventHandler(filePath));
		perform.setOnAction(new PerformEventHandler(filePath, numSpinners, output));
		
		Scene scene = new Scene(root);
		
		stage.setTitle("FL Practice Diff Creator");
		stage.setScene(scene);
		stage.show();
	}
	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		initUI(arg0);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

class BrowseEventHandler implements EventHandler<ActionEvent> {
	
	FileChooser fileChooser;
	TextField filePath;
	
	public BrowseEventHandler(TextField filePath) {
		// TODO Auto-generated constructor stub
		this.fileChooser = new FileChooser();
		this.fileChooser.setTitle("Open osu File");
		this.filePath = filePath;
	}

	@Override
	public void handle(ActionEvent event) { // browse file system and set the text field to the selected file
		// TODO Auto-generated method stub
		Node source = (Node) event.getSource();
		File file = this.fileChooser.showOpenDialog(source.getScene().getWindow());
		if (file != null) {
			this.filePath.setText(file.getAbsolutePath());
		}
	}
}

class PerformEventHandler implements EventHandler<ActionEvent> {
	
	TextField filePath;
	TextField numSpinners;
	Label output;

	public PerformEventHandler(TextField filePath, TextField numSpinners, Label output) {
		// TODO Auto-generated constructor stub
		this.filePath = filePath;
		this.numSpinners = numSpinners;
		this.output = output;
	}

	@Override
	public void handle(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String path = this.filePath.getText();
		int spinCount = Integer.parseInt(this.numSpinners.getText());
		if (path.endsWith(".osu") == false) { // show error for not a .osu
			Alert fileExt = new Alert(AlertType.ERROR);
			fileExt.setTitle("Error");
			fileExt.setHeaderText("Incorrect file extension");
			fileExt.setContentText("File must be a .osu");
			fileExt.showAndWait();
			return;
		}
		
		File file = new File(path);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String currline;
			String prevline = "";
			
			StringBuilder new_file = new StringBuilder();
			
			
			while ((currline=br.readLine()) != null) {
				
				if (prevline.contains("[HitObjects]")) {
					String[] details = currline.split(",");
					int new_ms = Integer.parseInt(details[2]) - 2000;
					
					for (int i = 0; i < spinCount; i++) {
						new_file.append("256,192," + new_ms + ",12,0," + (new_ms + 1) + ",0:0:0:0:");
						new_file.append(System.lineSeparator());
					}
				}
				
				new_file.append(currline); // add current line
				new_file.append(System.lineSeparator()); // add \n
				
				prevline = currline;
			}
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			bw.write(new_file.toString());
			bw.close();
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			this.output.setText("Something went wrong: file not found.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.output.setText("Something went wrong: failed to access file.");
			e.printStackTrace();
		}
		
		this.output.setText("Complete!");
	}
}