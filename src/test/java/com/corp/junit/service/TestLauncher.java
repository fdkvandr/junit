package com.corp.junit.service;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.PrintWriter;

public class TestLauncher {

    public static void main(String[] args) {
        var launcher = LauncherFactory.create();

//        launcher.registerLauncherDiscoveryListeners();

        var summaryGeneratingListener = new SummaryGeneratingListener();
//        launcher.registerTestExecutionListeners(summaryGeneratingListener);
//        launcher.registerTestExecutionListeners();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
//                                                                          .selectors(DiscoverySelectors.selectClass(UserServiceTest.class))
                                                                          .selectors(DiscoverySelectors.selectPackage("com.corp.junit.service"))
//                                                                          .listeners()
                                                                          .build();

        launcher.execute(request, summaryGeneratingListener);

        try (var writer = new PrintWriter(System.out)) {
            summaryGeneratingListener.getSummary()
                                     .printTo(writer);
        }
    }

}
