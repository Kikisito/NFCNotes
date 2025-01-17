/*
 * Copyright (C) 2020  Kikisito (Kyllian)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package es.kikisito.nfcnotes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class UpdateChecker {
    private final Main plugin;

    public UpdateChecker(Main plugin) {
        this.plugin = plugin;
    }

    public void getVersion(final Consumer<String> consumer) {
        String api = "https://api.github.com/repos/Kikisito/NFCNotes/releases";

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api))
                .header("Accept", "application/json")
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        String latestVersion = parseLatestVersion(body);
                        consumer.accept(latestVersion);
                    } catch(Exception e) {
                        plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
                    }
                }).exceptionally(e -> {
                    plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
                    return null;
                });
    }

    private String parseLatestVersion(String json) {
        int currentIndex = 0;
        while ((currentIndex = json.indexOf("\"prerelease\":false", currentIndex)) != -1) {
            int nameIndex = json.lastIndexOf("\"tag_name\":", currentIndex);
            if (nameIndex != -1) {
                int start = json.indexOf('"', nameIndex + 11) + 1;
                int end = json.indexOf('"', start);
                if (start != -1 && end != -1) {
                    return json.substring(start, end);
                }
            }
            currentIndex += 20; // Advance index to avoid infinite loops
        }
        return null;
    }
}
