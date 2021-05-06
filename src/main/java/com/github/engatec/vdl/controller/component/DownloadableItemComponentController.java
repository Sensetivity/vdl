package com.github.engatec.vdl.controller.component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.engatec.vdl.model.Format;
import com.github.engatec.vdl.model.Resolution;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.Icon;
import com.github.engatec.vdl.ui.Tooltips;
import com.github.engatec.vdl.ui.data.ComboBoxValueHolder;
import com.github.engatec.vdl.util.YoutubeDlUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class DownloadableItemComponentController extends HBox {

    private final VideoInfo videoInfo;

    @FXML private Label titleLabel;
    @FXML private Label durationLabel;
    @FXML private ComboBox<ComboBoxValueHolder<String>> formatsComboBox;
    @FXML private Button allFormatsButton;
    @FXML private Button audioButton;
    @FXML private CheckBox itemSelectedCheckBox;

    public DownloadableItemComponentController(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    @FXML
    public void initialize() {
        initControlButtons();
        initLabels();
        initFormats();
    }

    private void initControlButtons() {
        audioButton.setGraphic(new ImageView(Icon.AUDIOTRACK_SMALL.getImage()));
        audioButton.setTooltip(Tooltips.createNew("download.audio"));

        allFormatsButton.setGraphic(new ImageView(Icon.FILTER_LIST_SMALL.getImage()));
        allFormatsButton.setTooltip(Tooltips.createNew("format.all"));

        formatsComboBox.prefHeightProperty().bind(allFormatsButton.heightProperty());
    }

    private void initLabels() {
        titleLabel.setText(videoInfo.getTitle());

        int durationSeconds = Objects.requireNonNullElse(videoInfo.getDuration(), 0);
        String formattedDuration = durationSeconds <= 0 ? StringUtils.EMPTY : DurationFormatUtils.formatDuration(durationSeconds * 1000L, "HH:mm:ss");
        durationLabel.setText(formattedDuration);
    }

    private void initFormats() {
        List<Integer> commonAvailableFormats = ListUtils.emptyIfNull(videoInfo.getFormats()).stream()
                .map(Format::getHeight)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        ObservableList<ComboBoxValueHolder<String>> comboBoxItems = formatsComboBox.getItems();
        for (Integer height : commonAvailableFormats) {
            ComboBoxValueHolder<String> item = new ComboBoxValueHolder<>(height + "p " + Resolution.getDescriptionByHeight(height), YoutubeDlUtils.createFormat(height));
            comboBoxItems.add(item);
        }

        formatsComboBox.getSelectionModel().selectFirst();

        adjustWidth();
    }

    /**
     * A small hack to make comboboxes the same width no matter when they get rendered
     */
    private void adjustWidth() {
        formatsComboBox.getItems().add(new ComboBoxValueHolder<>("99999p 8K Ultra HD", StringUtils.EMPTY));
        formatsComboBox.setOnShowing(e -> {
            ObservableList<ComboBoxValueHolder<String>> items = formatsComboBox.getItems();
            items.remove(items.size() - 1);
            formatsComboBox.setOnShowing(null);
        });
    }

    public void setSelectable(boolean selectable) {
        itemSelectedCheckBox.setVisible(selectable);
        itemSelectedCheckBox.setManaged(selectable);
    }

    public boolean isSelected() {
        return itemSelectedCheckBox.isSelected();
    }

    public void setSelected(boolean selected) {
        itemSelectedCheckBox.setSelected(selected);
    }

    public CheckBox getItemSelectedCheckBox() {
        return itemSelectedCheckBox;
    }
}
