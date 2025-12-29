package com.xrea.jeisi.berettacommittool2.stagemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.StageEx;
import com.xrea.jeisi.berettacommittool2.configinfo.WindowRectangle;
import com.xrea.jeisi.berettacommittool2.stylemanager.StyleManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.awt.*;
import java.util.function.Consumer;

public abstract class StageManager {
    protected double x0, x1, y0, y1;

    public Scene buildScene(Parent parent, WindowRectangle windowRectangle) {
        double width = windowRectangle.getWidth();
        double height = windowRectangle.getHeight();
        ((Region)parent).setPrefSize(width, height);
        return new Scene(parent);
    }

    protected void setUp() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        //double x0 = 0, x1 = 0, y0 = 0, y1 = 0;
        for (GraphicsDevice g : gs) {
            //DisplayMode dm = g.getDisplayMode();
            Rectangle r = g.getConfigurations()[0].getBounds();
            if (r.x < x0) {
                x0 = r.x;
            }
            if (x1 < r.x + r.width) {
                x1 = r.x + r.width;
            }
            if (r.y < y0) {
                y0 = r.y;
            }
            if (y1 < r.y + r.height) {
                y1 = r.y + r.height;
            }
        }
    }

    protected WindowRectangle getWindowRectangle(double x, double y, double width, double height) {
        if (x < x0) {
            x = x0;
        }
        if (y < y0) {
            y = y0;
        }
        if (x1 < x + width) {
            x = x1 - width;
        }
        if (y1 < y + height) {
            y = y1 - height;
        }
        return new WindowRectangle(x, y, width, height);
    }

    protected void doShowStage(final StageEx stage, WindowRectangle windowRectangle, ConfigInfo configInfo, Consumer<StageEx> processor, ChangeListener<Boolean> listener) {
        // ウィンドウが閉じたときに呼び出される listener をセット。
        stage.showingProperty().addListener(listener);

        StyleManager styleManager = configInfo.getMainApp().getStyleManager();
        styleManager.setStage(stage);

        if(windowRectangle.isValidXY()) {
            stage.setX(windowRectangle.getX());
            stage.setY(windowRectangle.getY());
        }
        stage.setWidth(windowRectangle.getWidth());
        stage.setHeight(windowRectangle.getHeight());

        // stage.show() or stage.showAndWait() を実行
        processor.accept(stage);

        // 枠を含めたサイズに再補正
        Platform.runLater(stage::sizeToScene);
    }

    public abstract void open(StageEx stage, WindowRectangle windowRectangle, ConfigInfo configInfo);
    public abstract WindowRectangle getWindowRectangle(ConfigInfo configInfo, String identifier, double defaultWidth, double defaultHeight);
    //protected abstract ChangeListener<Boolean> getShowingChangeListener(StageEx stage, WindowRectangle windowRectangle, ConfigInfo configInfo);

}
