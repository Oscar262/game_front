package org.game.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.sql.Time;

public class Config {

    public static String ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvcy5zYW5hdi4yNkBnbWFpbC5jb20iLCJpYXQiOjE3NTkyMjE2OTIsImV4cCI6MTc1OTMwODA5Mn0.fT1Uhk7DVR9Fw-dScMwKl-i1vJgMTJD0kDNxGSQhFgwzDitklVtnkPl62Vre8gYgy23d0i44moMpZhwF6OEo6Q";

    private static final int NUM_POINTS = 5;
    private static final double POINT_RADIUS = 7;
    private static final double OVAL_SCALE_X = 2.0;
    private static final double OVAL_SCALE_Y = 0.5;
    private static final double CIRCLE_SCALE_X = 2.5;
    private static final double CIRCLE_SCALE_Y = 2.5;

    public static Image convertByteArrayToImage(byte[] imageData) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
        return new Image(byteArrayInputStream);
    }

    public static Circle[] createProgressBar(StackPane root, HBox pointBox) {
        pointBox.setAlignment(Pos.CENTER);
        Circle[] points = new Circle[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            points[i] = new Circle(POINT_RADIUS, Color.GRAY);
            points[i].setScaleX(OVAL_SCALE_X);
            points[i].setScaleY(OVAL_SCALE_Y);
            points[i].setStrokeWidth(0);
            pointBox.getChildren().add(points[i]);
        }
        root.getChildren().add(pointBox);
        return points;
    }

    public static Timeline createTimeline(Circle[] points) {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        for (int i = 0; i < NUM_POINTS; i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.seconds(i * 0.5),
                    e -> animatePoint(points, index)
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        KeyFrame finalKeyFrame = new KeyFrame(
                Duration.seconds(NUM_POINTS * 0.5),
                e -> animatePoint(points, NUM_POINTS - 1)
        );
        timeline.getKeyFrames().add(finalKeyFrame);

        return timeline;
    }

    private static void animatePoint(Circle[] points, int currentIndex) {
        for (int i = 0; i < NUM_POINTS; i++) {
            points[i].setFill(Color.GRAY);
            points[i].setScaleX(OVAL_SCALE_X);
            points[i].setScaleY(OVAL_SCALE_Y);
        }

        RadialGradient blueGradient = new RadialGradient(
                0,
                0,
                0.5,
                0.5,
                0.5,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.DARKBLUE),
                new Stop(1, Color.DODGERBLUE)
        );

        Circle circle = new Circle(50);
        circle.setFill(blueGradient);

        circle.setStroke(Color.DODGERBLUE);
        circle.setStrokeWidth(5);

        points[currentIndex].setFill(blueGradient);
        points[currentIndex].setScaleX(CIRCLE_SCALE_X);
        points[currentIndex].setScaleY(CIRCLE_SCALE_Y);

        if (currentIndex > 0) {
            points[currentIndex - 1].setFill(Color.GRAY);
            points[currentIndex - 1].setScaleX(OVAL_SCALE_X);
            points[currentIndex - 1].setScaleY(OVAL_SCALE_Y);
        }
    }

    //TODO:crear metodos para cerrar y minimizar


}
