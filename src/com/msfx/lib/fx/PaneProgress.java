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
/**
 * 
 */
package com.msfx.lib.fx;

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
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
	private final double padding;

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
	 * @param padding Padding.
	 */
	public PaneProgress(TaskProgress task, int timeout, double padding) {
		this.task = task;
		this.timeout = timeout;
		this.padding = padding;
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

		Insets paddingLabel = new Insets(0, padding, 0, padding);
		Border borderLabel = Border.stroke(Color.rgb(200, 200, 200));
		int row = 0;

		/* First row shows the title and the state. */

		Font font = new Label().fontProperty().get();
		font = Font.font(font.getFamily(), FontWeight.BOLD, 14);
		Background fill = Background.fill(Color.rgb(235, 235, 235));

		labelTitle.fontProperty().set(font);
		labelTitle.textProperty().set(task.getTitle());
		labelTitle.backgroundProperty().set(fill);
		labelTitle.maxWidthProperty().set(Double.MAX_VALUE);
		labelTitle.paddingProperty().set(paddingLabel);
		labelTitle.borderProperty().set(borderLabel);

		labelState.fontProperty().set(font);
		labelState.textProperty().set(task.getState().name());
		labelState.backgroundProperty().set(fill);
		labelState.maxWidthProperty().set(Double.MAX_VALUE);
		labelState.paddingProperty().set(paddingLabel);
		labelState.borderProperty().set(borderLabel);

		ColumnConstraints ccTitle = new ColumnConstraints();
		ccTitle.percentWidthProperty().set(80);
		ColumnConstraints ccState = new ColumnConstraints();
		ccState.percentWidthProperty().set(20);

		Insets insTitle = new Insets(0, 2, padding, padding);
		Insets insState = new Insets(0, padding, padding, 0);

		GridPane.setConstraints(labelTitle, 0, row, 1, 1,
			HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER, insTitle);
		GridPane.setConstraints(labelState, 1, row, 1, 1,
			HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, insState);
		root.getChildren().add(labelTitle);
		root.getChildren().add(labelState);
		root.getColumnConstraints().addAll(ccTitle, ccState);

		/* For each level. */

		Insets insets = new Insets(2, padding, 2, padding);

		for (int i = 0; i < pm.size(); i++) {
			Level level = new Level();
			levels.add(level);

			/* Message label. */
			row++;
			Label labelMessage = new Label();
			labelMessage.maxWidthProperty().set(Double.MAX_VALUE);
			labelMessage.paddingProperty().set(paddingLabel);
			labelMessage.borderProperty().set(borderLabel);
			level.labels.put("message", labelMessage);
			GridPane.setConstraints(labelMessage, 0, row, 2, 1,
				HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, insets);
			root.getChildren().add(labelMessage);

			/* Time progress. */
			row++;
			Label labelTime = new Label();
			labelTime.maxWidthProperty().set(Double.MAX_VALUE);
			labelTime.paddingProperty().set(paddingLabel);
			labelTime.borderProperty().set(borderLabel);
			level.labels.put("time", labelTime);
			GridPane.setConstraints(labelTime, 0, row, 2, 1,
				HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, insets);
			root.getChildren().add(labelTime);

			/* Work progress. */
			row++;
			Label labelWork = new Label();
			labelWork.maxWidthProperty().set(Double.MAX_VALUE);
			labelWork.paddingProperty().set(paddingLabel);
			labelWork.borderProperty().set(borderLabel);
			level.labels.put("work", labelWork);
			GridPane.setConstraints(labelWork, 0, row, 2, 1,
				HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, insets);
			root.getChildren().add(labelWork);

			/* Progress bar. */
			row++;
			level.progress.progressProperty().set(0);
			level.progress.maxWidthProperty().set(Double.MAX_VALUE);
			level.progress.maxHeightProperty().set(15);
			GridPane.setConstraints(level.progress, 0, row, 2, 1,
				HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, insets);
			root.getChildren().add(level.progress);
		}

		/* Start, cancel and remove buttons. */
		row++;

		buttonStart = Buttons.start(false, false, false);
		buttonCancel = Buttons.cancel(false, false, false);
		buttonRemove = Buttons.remove(false, false, false);

		ButtonBar buttonBar = new ButtonBar();
		buttonBar.getButtons().addAll(buttonStart, buttonCancel, buttonRemove);
		GridPane.setConstraints(buttonBar, 0, row, 2, 1,
			HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, insets);
		root.getChildren().add(buttonBar);

		/* Separator. */
		row++;
		Separator separator = new Separator();
		GridPane.setConstraints(separator, 0, row, 2, 1,
			HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, insets);
		root.getChildren().add(separator);

		buttonStart.setOnAction(e -> taskStart());
		buttonCancel.setOnAction(e -> taskCancel());

		buttonStart.disableProperty().set(false);
		buttonCancel.disableProperty().set(true);
		buttonStart.requestFocus();
	}

	/**
	 * Start the task.
	 */
	private void taskStart() {

		timer = new Timer();
		timerTask = new TimerReport();
		timer.schedule(timerTask, timeout, timeout);

		task.reinitialize();
		if (pool != null) {
			pool.submit(task);
		} else {
			new Thread(task).start();
		}

		buttonStart.disableProperty().set(true);
		buttonRemove.disableProperty().set(true);
		buttonCancel.disableProperty().set(false);
		buttonCancel.requestFocus();
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
			}

			if (pg.message != null) {
				lv.labels.get("message").textProperty().set(pg.message);
			}

			StringBuilder timeMessage = new StringBuilder();
			if (timeStart != null) {
				timeMessage.append(StringRes.get("task-start-time", "Start time"));
				timeMessage.append(": ");
				timeMessage.append(timeStart);
			}
			if (timeCurrent != null) {
				timeMessage.append(" - ");
				timeMessage.append(StringRes.get("task-current-time", "Current time"));
				timeMessage.append(": ");
				timeMessage.append(timeCurrent);
			}
			if (elapsedDuration != null) {
				timeMessage.append(" - ");
				timeMessage.append(StringRes.get("task-elapsed-duration", "Elapsed duration"));
				timeMessage.append(": ");
				timeMessage.append(elapsedDuration);
			}
			if (expectedDuration != null) {
				timeMessage.append(" - ");
				timeMessage.append(StringRes.get("task-expected-duration", "Expected duration"));
				timeMessage.append(": ");
				timeMessage.append(expectedDuration);
			}
			if (timeEnd != null) {
				timeMessage.append(" - ");
				timeMessage.append(StringRes.get("task-end-time", "End time"));
				timeMessage.append(": ");
				timeMessage.append(timeEnd);
			}
			lv.labels.get("time").textProperty().set(timeMessage.toString());

			if (progress >= 0) {
				lv.progress.progressProperty().set(progress);
			}
		}

		if (task.hasTerminated()) {
			timerTask.cancel();
			timer.cancel();
			buttonStart.disableProperty().set(false);
			buttonRemove.disableProperty().set(false);
			buttonCancel.disableProperty().set(true);
			buttonStart.requestFocus();
		}
	}
}
