/*
 * Copyright (c) 2022 Miquel Sas.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.msfx.lib.fx;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.msfx.lib.task.ExecPool;
import com.msfx.lib.task.Monitor;
import com.msfx.lib.task.TaskProgress;
import com.msfx.lib.util.Numbers;
import com.msfx.lib.util.Strings;
import com.msfx.lib.util.res.StringRes;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.Border;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

/**
 * A layout component that displays the progress of a multi-level task.
 *
 * @author Miquel Sas
 */
public class PaneProgress {

	/** Pack of components of a level. */
	private static class Level {
		private Map<String, Label> labels = new HashMap<>();
		private ProgressBar progress = new ProgressBar();
	}
	/** Timer task. */
	private class TimerReport extends TimerTask {
		public void run() { Platform.runLater(() -> timerReport()); }
	}

	/** Title label. */
	private final Label labelTitle = new Label();
	/** State label. */
	private final Label labelState = new Label();
	/** List of levels. */
	private final List<Level> levels = new ArrayList<>();
	/** Root grid pane. */
	private final GridPane root = new GridPane();

	/** Button start. */
	private Button buttonStart = null;
	/** Button cancel. */
	private Button buttonCancel = null;
	/** Button error. */
	private Button buttonError = null;
	/** Button remove. */
	private Button buttonRemove = null;

	/** The progress task that this pane monitors. */
	private TaskProgress task;

	/** Timeout to notify in millis, to not collapse the event queue. */
	private final int timeout;
	/** Timer to monitor progress. */
	private Timer timer;
	/** Timer task. */
	private TimerReport timerTask;

	/** Parent padding. */
	private final double pad;

	/** External execution pool. */
	private ExecPool pool;

	/**
	 * Constructor that creates a progress pane to display the performance of the progress task.
	 * @param task The progress task.
	 */
	public PaneProgress(TaskProgress task) { this(task, FX.PADDING); }
	/**
	 * Constructor that creates a progress pane to display the performance of the progress task.
	 * @param task    The progress task.
	 * @param padding Padding.
	 */
	public PaneProgress(TaskProgress task, double padding) { this(task, 50, padding); }
	/**
	 * Constructor that creates a progress pane to display the performance of the progress task.
	 * @param task    The progress task.
	 * @param timeout The timeout to refresh the monitor.
	 * @param pad     Padding.
	 */
	public PaneProgress(TaskProgress task, int timeout, double pad) {
		this.task = task;
		this.timeout = timeout;
		this.pad = pad;
		layoutComponents();
	}
	/**
	 * Return the root grid pane.
	 * @return The root grid pane.
	 */
	public GridPane getRoot() { return root; }

	/**
	 * Set the external execution pool.
	 * @param pool The pool.
	 */
	public void setPool(ExecPool pool) { this.pool = pool; }

	/**
	 * Set the remove action.
	 * @param action The remove action.
	 */
	public void setRemoveAction(EventHandler<ActionEvent> action) { buttonRemove.setOnAction(action); }

	/**
	 * Layout components.
	 */
	private void layoutComponents() {

		Monitor pm = task.getMonitor();

		Insets paddingLabel = new Insets(0, pad, 0, pad);
		Border borderLabel = Border.stroke(Color.rgb(200, 200, 200));
		int row = 0;

		/* Two columns, left 75% and right 25%. */
		
		ColumnConstraints ccLeft = new ColumnConstraints();
		ccLeft.percentWidthProperty().set(75);
		ColumnConstraints ccright = new ColumnConstraints();
		ccright.percentWidthProperty().set(25);
		root.getColumnConstraints().addAll(ccLeft, ccright);

		/* First row shows the title and the state. */
		
		String styleTitle = "";
		styleTitle += " -fx-font-size: 14px;";
		styleTitle += " -fx-font-weight: bold;";
		styleTitle += " -fx-background-color: rgb(235, 235, 235);";

		labelTitle.textProperty().set(task.getTitle());
		labelTitle.styleProperty().set(styleTitle);
		labelTitle.maxWidthProperty().set(Double.MAX_VALUE);
		labelTitle.paddingProperty().set(paddingLabel);
		labelTitle.borderProperty().set(borderLabel);

		labelState.textProperty().set(task.getState().name());
		labelState.styleProperty().set(styleTitle);
		labelState.maxWidthProperty().set(Double.MAX_VALUE);
		labelState.paddingProperty().set(paddingLabel);
		labelState.borderProperty().set(borderLabel);

		GridPane.setConstraints(labelTitle, 0, row, 1, 1,
			HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER, new Insets(0, 2, pad, pad));
		GridPane.setConstraints(labelState, 1, row, 1, 1,
			HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(0, pad, pad, 0));
		root.getChildren().add(labelTitle);
		root.getChildren().add(labelState);

		/* For each level. */

		for (int i = 0; i < pm.size(); i++) {
			Level level = new Level();
			levels.add(level);
			
			row++;

			/* Message . */
			Label labelMessage = new Label();
			labelMessage.maxWidthProperty().set(Double.MAX_VALUE);
			labelMessage.paddingProperty().set(paddingLabel);
			labelMessage.borderProperty().set(borderLabel);
			level.labels.put("message", labelMessage);
			GridPane.setConstraints(labelMessage, 0, row, 2, 1,
				HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(8, pad, 2, pad));
			root.getChildren().add(labelMessage);
			
			row++;

			/* Time progress. */
			Label labelTime = new Label();
			labelTime.maxWidthProperty().set(Double.MAX_VALUE);
			labelTime.paddingProperty().set(paddingLabel);
			labelTime.borderProperty().set(borderLabel);
			level.labels.put("progress", labelTime);
			GridPane.setConstraints(labelTime, 0, row, 1, 1,
				HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(2, pad, 2, pad));
			root.getChildren().add(labelTime);

			/* Progress bar. */
			level.progress.progressProperty().set(0);
			level.progress.maxWidthProperty().set(Double.MAX_VALUE);
			GridPane.setConstraints(level.progress, 1, row, 1, 1,
				HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(2, pad, 2, pad));
			root.getChildren().add(level.progress);
		}

		/* Start, cancel and remove buttons. */
		row++;

		buttonStart = Buttons.start(false, false, false);
		buttonCancel = Buttons.cancel(false, false, false);
		buttonError = Buttons.error(false, false, false);
		buttonRemove = Buttons.remove(false, false, false);
		
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.getButtons().addAll(buttonStart, buttonCancel, buttonError, buttonRemove);
		GridPane.setConstraints(buttonBar, 0, row, 2, 1,
			HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(8, pad, 2, pad));
		root.getChildren().add(buttonBar);

		/* Separator. */
		row++;
		Separator separator = new Separator();
		GridPane.setConstraints(separator, 0, row, 2, 1,
			HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(2, pad, 2, pad));
		root.getChildren().add(separator);

		buttonStart.setOnAction(e -> taskStart());
		buttonCancel.setOnAction(e -> taskCancel());
		buttonError.setOnAction(e -> showError());

		buttonStart.disableProperty().set(false);
		buttonCancel.disableProperty().set(true);
		buttonError.disableProperty().set(true);
		buttonStart.requestFocus();
	}

	/**
	 * Start the task.
	 */
	private void taskStart() {

		timer = new Timer();
		timerTask = new TimerReport();
		timer.schedule(timerTask, timeout, timeout);

		if (pool != null) {
			pool.submit(task);
		} else {
			new Thread(task).start();
		}

		buttonStart.disableProperty().set(true);
		buttonRemove.disableProperty().set(true);
		buttonCancel.disableProperty().set(false);
		buttonError.disableProperty().set(true);
		buttonError.styleProperty().set("-fx-text-fill: gray;");
	}

	/**
	 * Request to cancel the task.
	 */
	private void taskCancel() { task.requestCancel(); }

	/**
	 * Timer report.
	 */
	private void timerReport() {

		String title = task.getMonitor().getTitle();
		String state = task.getMonitor().getState().name();

		List<Monitor.Progress> pgs = new ArrayList<>();
		for (int i = 0; i < task.getMonitor().size(); i++) {
			pgs.add(task.getMonitor().getProgress(i));
		}

		labelTitle.textProperty().set(title);
		labelState.textProperty().set(state);

		for (int i = 0; i < pgs.size(); i++) {
			Level lv = levels.get(i);
			Monitor.Progress pg = pgs.get(i);
			if (pg.startTime == null) continue;

			String timeStart = null;
			String timeCurrent = null;
			String timeEnd = null;

			String elapsedDuration = null;
			String expectedDuration = null;

			String workProgress = null;

			double progress = -1;

			boolean estimated = (!pg.indeterminate && pg.totalWork > 0 && pg.estimatedDuration != null);
			boolean printDate = false;
			printDate |= !pg.startTime.toLocalDate().equals(pg.currentTime.toLocalDate());
			if (estimated) {
				printDate |= !pg.startTime.toLocalDate().equals(pg.currentTime.toLocalDate());
			}

			if (printDate) {
				timeStart = pg.startTime.truncatedTo(ChronoUnit.SECONDS).toString();
				timeCurrent = pg.currentTime.truncatedTo(ChronoUnit.SECONDS).toString();
			} else {
				timeStart = pg.startTime.toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString();
				timeCurrent = pg.currentTime.toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString();
			}
			elapsedDuration = Strings.toString(pg.elapsedDuration);

			if (estimated) {
				expectedDuration = Strings.toString(pg.estimatedDuration);
				if (printDate) {
					timeEnd = pg.endTime.truncatedTo(ChronoUnit.SECONDS).toString();
				} else {
					timeEnd = pg.endTime.toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString();
				}
				double workDone = pg.workDone;
				double totalWork = pg.totalWork;
				progress = workDone / totalWork;

				StringBuilder work = new StringBuilder();
				work.append(StringRes.get("task-work-progress", "Work"));
				work.append(" ");
				work.append(pg.workDone);
				work.append(" ");
				work.append(StringRes.get("task-work-of", "of"));
				work.append(" ");
				work.append(pg.totalWork);
				work.append(" (");
				work.append(Numbers.getBigDecimal(100.0 * progress, 2).toPlainString());
				work.append("%)");
				workProgress = work.toString();
			}

			if (pg.message != null) {
				lv.labels.get("message").textProperty().set(pg.message);
			}

			StringBuilder progressMessage = new StringBuilder();
			if (timeStart != null) {
				progressMessage.append(StringRes.get("task-start-time", "Start"));
				progressMessage.append(": ");
				progressMessage.append(timeStart);
			}
			if (timeCurrent != null) {
				progressMessage.append(" - ");
				progressMessage.append(StringRes.get("task-current-time", "Current"));
				progressMessage.append(": ");
				progressMessage.append(timeCurrent);
			}
			if (elapsedDuration != null) {
				progressMessage.append(" - ");
				progressMessage.append(StringRes.get("task-elapsed-duration", "Elapsed"));
				progressMessage.append(": ");
				progressMessage.append(elapsedDuration);
			}
			if (expectedDuration != null) {
				progressMessage.append(" - ");
				progressMessage.append(StringRes.get("task-expected-duration", "Expected"));
				progressMessage.append(": ");
				progressMessage.append(expectedDuration);
			}
			if (timeEnd != null) {
				progressMessage.append(" - ");
				progressMessage.append(StringRes.get("task-end-time", "End"));
				progressMessage.append(": ");
				progressMessage.append(timeEnd);
			}
			if (workProgress != null) {
				progressMessage.append(" - ");
				progressMessage.append(workProgress);
			}
			lv.labels.get("progress").textProperty().set(progressMessage.toString());

			if (progress >= 0) {
				lv.progress.progressProperty().set(progress);
			}
		}

		if (task.hasFailed() && task.getException() != null) {
			buttonError.disableProperty().set(false);
			buttonError.styleProperty().set("-fx-text-fill: red;");
		}

		if (task.hasTerminated()) {
			timerTask.cancel();
			timer.cancel();
			buttonStart.disableProperty().set(false);
			buttonRemove.disableProperty().set(false);
			buttonCancel.disableProperty().set(true);
			if (!task.hasFailed()) buttonStart.requestFocus();
			else buttonError.requestFocus();
		}
	}

	/**
	 * Show the exception if any.
	 */
	private void showError() {

		if (task.getException() == null) {
			Alert alert = new Alert(0.5, 0.2);
			alert.setup(Alert.Type.WARNING, Alert.Content.TEXT);
			alert.setTitle("No errors thrown");
			alert.addHeaderText("No error avsailable.", "-fx-font-family: serif; -fx-font-size: 20;");
			alert.show();
			return;
		}

		Alert alert = new Alert(0.5, 0.5);
		alert.setup(Alert.Type.ERROR, Alert.Content.TEXT);
		alert.setTitle("Task error thrown");
		alert.addHeaderText(task.getException().getMessage(), "-fx-font-family: serif; -fx-font-size: 20;");

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		task.getException().printStackTrace(pw);
		alert.addContentText(sw.toString());

		alert.show();
	}
}
