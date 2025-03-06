package com.github.felipeucelli.javatube;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicReference;

public class BotGuard {

    public static String generatePoToken(String visitorData)  {
        new JFXPanel();
        return callBotGuard(visitorData);
    }

    private static String callBotGuard(String params) {
        AtomicReference<Object> resultRef = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                WebView webView = new WebView();
                WebEngine webEngine = webView.getEngine();

                File file = new File("src/main/resources/poToken.html");
                String url = file.toURI().toURL().toString();
                webEngine.load(url);

                webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        String script = String.format("""
                            generatePoToken('%s')
                                .then(result => window.poTokenResult = result)
                                .catch(error => window.poTokenResult = 'Error: ' + error);
                        """, params);

                        webEngine.executeScript(script);

                        new Thread(() -> {
                            while (true) {

                                Platform.runLater(() -> {
                                    Object res = webEngine.executeScript("window.poTokenResult");
                                    resultRef.set(res);
                                });

                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                if (resultRef.get() != null && !"undefined".equals(resultRef.get().toString())) {
                                    Platform.exit();
                                    break;
                                }
                            }
                        }).start();
                    }
                });

            }catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
        while (resultRef.get() == null || "undefined".equals(resultRef.get().toString())) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return resultRef.get().toString();
    }
}
